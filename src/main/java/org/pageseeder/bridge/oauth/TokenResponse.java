/*
 * Copyright 2016 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.bridge.oauth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.PSToken;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.net.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The response from a token request.
 *
 * <p>Typical usage:
 * <pre>
 *   // Execute the request to get a new response
 *   TokenResponse response = request.execute();
 *   if (response.isSuccessful()) {
 *     PSToken token = response.getToken();
 *
 *     // If grant type and client supports refresh tokens
 *     String refreshToken = response.getRefreshToken();
 *
 *     // If using 'openid profile' scope
 *     PSMember member = response.getMember();
 *
 *   } else {
 *
 *   }
 * </pre>
 *
 * @version 0.9.0
 * @since 0.9.0
 */
public final class TokenResponse {

  /** Logger for this class */
  private static final Logger LOGGER = LoggerFactory.getLogger(TokenResponse.class);

  /**
   * The HTTP response code.
   */
  private final int _responseCode;

  /**
   * The access token if the response was successful.
   */
  private @Nullable PSToken token;

  /**
   * The member if the request was using open ID.
   */
  private @Nullable PSMember member;

  /**
   * The actual response as a string.
   */
  private @Nullable final String _response;

  /**
   * The parsed JSON values.
   */
  private final Map<@NonNull String, @NonNull String> _json;

  /**
   * Creates a new token response.
   *
   * @param code     The HTTP code
   * @param response The actual response.
   * @param json     A map of JSON elements.
   */
  TokenResponse(int code, @Nullable String response, Map<@NonNull String, @NonNull String> json) {
    this._responseCode = code;
    this._response = response;
    this._json = json;
  }

  /**
   * Returns the access token issued by the authorization server if the response is successful.
   *
   * @return the access token if the response is successful or <code>null</code>
   */
  public @Nullable PSToken getAccessToken() {
    return this.token;
  }

  /**
   * Returns the PageSeeder member if the response is successful and the scope included 'openid profile'.
   *
   * @return the PageSeeder member or <code>null</code>
   */
  public @Nullable PSMember getMember() {
    return this.member;
  }

  /**
   * Indicates whether the response is available
   *
   * @return <code>true</code> if the response code is a valid HTTP response code.
   */
  public boolean isAvailable() {
    return this._responseCode > 0;
  }

  /**
   * Indicates whether the response was successful.
   *
   * @return <code>true</code> if the response code is 200.
   *
   * @see <a href="http://tools.ietf.org/html/rfc6749#section-5.1"> OAuth 2.0 - 5.1. Successful Response</a>
   */
  public boolean isSuccessful() {
    return this._responseCode == 200;
  }

  /**
   * @return the HTTP response code.
   */
  public int getResponseCode() {
    return this._responseCode;
  }

  /**
   * @return the raw JSON response.
   */
  public @Nullable String getJSONResponse() {
    return this._response;
  }

  /**
   * Returns the refresh token, which can be used to obtain new
   * access tokens using the same authorization grant.
   *
   * <p>Corresponds to the <code>refresh_token</code> parameter.
   *
   * @return The refresh token
   */
  public @Nullable String getRefreshToken() {
    return this._json.get("refresh_token");
  }

  /**
   * Returns the scope of the access token.
   *
   * <p>It may not be returned if identical to the scope requested by the
   * client.
   *
   * @return The scope
   */
  public @Nullable String getScope() {
    return this._json.get("scope");
  }

  /**
   * Returns a ASCII error code if the response is not successful.
   *
   * <p>It should be one of:
   * <ul>
   *   <li>invalid_request</li>
   *   <li>invalid_client</li>
   *   <li>invalid_grant</li>
   *   <li>unauthorized_client</li>
   *   <li>unsupported_grant_type</li>
   *   <li>invalid_scope</li>
   * </ul>
   *
   * @return The error
   *
   * @see <a href="http://tools.ietf.org/html/rfc6749#section-5.2">OAuth 2.0 - 5.2 Error Response</a>
   */
  public @Nullable String getError() {
    return this._json.get("error");
  }

  /**
   * Returns a human-readable ASCII text providing additional information,
   * used to assist the client developer in understanding the error that
   * occurred.
   *
   * @return The error description
   */
  public @Nullable String getErrorDescription() {
    return this._json.get("error_description");
  }

  /**
   * A URI identifying a human-readable web page with information about the
   * error, used to provide the client developer with additional information
   * about the error.
   *
   * @return The error URI
   */
  public @Nullable String getErrorURI() {
    return this._json.get("error_uri");
  }

  /**
   * @param name The name of the JSON parameter
   *
   * @return A OAuth parameter from the JSON response.
   */
  public @Nullable String getParameter(String name) {
    return this._json.get(name);
  }

  /**
   * Consumes the URL Connection
   *
   * @param connection  the HTTP URL Connection.
   * @param credentials the client credentials (required for parsing Open ID tokens)
   *
   * @return a new Token response.
   */
  public static TokenResponse consume(HttpURLConnection connection, ClientCredentials credentials) {
    // Record time BEFORE connecting
    long time = System.currentTimeMillis();
    try {
      int responseCode = connection.getResponseCode();
      try (InputStream in = HTTP.stream(connection)) {
        String raw = toString(in, connection.getContentLength());
        LOGGER.debug("JSON response: {}", raw);
        Map<String, String> json = JSONParameter.parse(raw);
        TokenResponse response = new TokenResponse(responseCode, raw, json);
        if (response.isSuccessful()) {
          response.token = extractToken(json, time);
          response.member = extractMember(json, credentials);
        }
        return response;
      }
    } catch (IOException ex) {
      return TokenResponse.error("io_error", ex.getMessage());
    }
  }

  /**
   * Create a new error token response
   *
   * @param error       the name or ID of the error
   * @param description a description for the error
   *
   * @return a new Token response.
   */
  static TokenResponse error(String error, @Nullable String description) {
    Map<String, String> json = new HashMap<>(2);
    json.put("error", error);
    if (description != null) {
      json.put("error_description", description);
    }
    return new TokenResponse(-1, null, json);
  }

  /**
   *
   * @param json The JSON properties.
   * @param time Time the request was made.
   *
   * @return The PageSeeder token.
   *
   * @throws NullPointerException If the token could not be constructed.
   * @throws IllegalArgumentException If the token could not be constructed.
   */
  private static PSToken extractToken(Map<String, String> json, long time) {
    // Compute the token
    String accessToken = json.get("access_token");
    String expiresInSeconds = json.get("expires_in");
    if (accessToken != null && expiresInSeconds != null) {
      try {
        long expiresInMillis = time + Long.parseLong(expiresInSeconds)*1000;
        LOGGER.debug("Access token: {}", accessToken);
        return new PSToken(accessToken, expiresInMillis);
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Unable to parse 'expires_in' value");
      }
    } else throw new IllegalArgumentException("JSON missing 'access_token' or 'expires_in'");
  }

  /**
   *
   * @param json The JSON properties.
   *
   * @return The PageSeeder member or <code>null</code>.
   */
  private static @Nullable PSMember extractMember(Map<String, String> json, ClientCredentials credentials) {
    PSMember member = null;
    String idToken = json.get("id_token");
    if (idToken != null) {
      member = OpenID.parseIDToken(idToken, credentials.secret().getBytes());
    }
    return member;
  }

  /**
   * Parse the content as a string.
   */
  private static String toString(InputStream in, int expectedLength) throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    int bufferSize = expectedLength > 1024 || expectedLength < 0? 1024 : expectedLength;
    byte[] buffer = new byte[bufferSize];
    int length;
    while ((length = in.read(buffer)) != -1) {
      result.write(buffer, 0, length);
    }
    return result.toString("UTF-8");
  }
}
