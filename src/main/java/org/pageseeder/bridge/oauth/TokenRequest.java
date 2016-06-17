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

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.net.HTTP;
import org.pageseeder.bridge.net.UsernamePassword;

/**
 * A request to the PageSeeder token endpoint.
 *
 * <p>This class provides a factory for all server-side token requests:
 * <ul>
 *   <li><code>authorization_code</code>
 *   <li><code>password</code>
 *   <li><code>client_credentials</code>
 *   <li><code>refresh_token</code>
 * </ul>
 *
 * <p>Example for authorization code flow:
 * <pre>
 *   // Instantiate a new request using code returned from authorization end point
 *   TokenRequest request = TokenRequest.newAuthorizationCode(code, clientCredentials);
 * </pre>
 *
 * <p>Example for resource owner password flow:
 * <pre>
 *   // Specify client credentials
 *   UsernamePassword userCredentials = new UsernamePassword("ali_baba", "open sesame!");
 *
 *   // Instantiate a new request
 *   TokenRequest request = TokenRequest.newPassword(userCredentials, clientCredentials);
 * </pre>
 *
 * <p>Example for client credentials flow:
 * <pre>
 *   // Instantiate a new request
 *   TokenRequest request = TokenRequest.newClientCredentials(clientCredentials);
 * </pre>
 *
 * <p>Example for token refresh flow:
 * <pre>
 *   // Instantiate a new request using refresh token
 *   TokenRequest request = TokenRequest.newRefreshToken(refreshToken, clientCredentials);
 * </pre>
 *
 * @version 0.9.0
 * @since 0.9.0
 */
public final class TokenRequest {

  /**
   * The PageSeeder token endpoint.
   */
  private static final String TOKEN_ENDPOINT = "/oauth/token";

  /**
   * The url based on the PageSeeder config.
   */
  private final String _url;

  /**
   * The parameters to send.
   */
  private final Map<String, String> _parameters;

  /**
   * The client credentials to make the request.
   */
  private final ClientCredentials _client;

  /**
   * Creates a new instance.
   *
   * @param url        The URL of the endpoint.
   * @param parameters The parameters to include on POST
   * @param client     The client credentials
   */
  private TokenRequest(String url, Map<String, String> parameters, ClientCredentials client) {
    this._url = url;
    this._parameters = parameters;
    this._client = client;
  }

  // Factory methods
  // --------------------------------------------------------------------------

  /**
   * Construct a token request for the "authorization_code" credential grant.
   *
   * @param user   The username and password of the user.
   * @param client The client credentials
   *
   * @return the corresponding token request.
   */
  public static TokenRequest newAuthorizationCode(String code, ClientCredentials client) {
    Objects.requireNonNull(code, "The code parameter is required");
    Objects.requireNonNull(client, "The client credentials are required");
    String url = toBaseURL(PSConfig.getDefault());
    Map<String, String> parameters = new LinkedHashMap<>(3);
    parameters.put("grant_type", "authorization_code");
    parameters.put("code", code);
    return new TokenRequest(url, parameters, client);
  }

  /**
   * Construct a token request for the resource owner "password" credential grant.
   *
   * @param user   The username and password of the user.
   * @param client The client credentials
   *
   * @return the corresponding token request.
   */
  public static TokenRequest newPassword(UsernamePassword user, ClientCredentials client) {
    Objects.requireNonNull(client, "The client credentials are required");
    String url = toBaseURL(PSConfig.getDefault());
    Map<String, String> parameters = new LinkedHashMap<>(3);
    parameters.put("grant_type", "password");
    parameters.put("username", user.username());
    parameters.put("password", user.password());
    return new TokenRequest(url, parameters, client);
  }

  /**
   * Construct a token request for the "client_credentials" credential grant.
   *
   * @param client The client credentials
   *
   * @return the corresponding token request.
   */
  public static TokenRequest newClientCredentials(ClientCredentials client) {
    Objects.requireNonNull(client, "The client credentials are required");
    String url = toBaseURL(PSConfig.getDefault());
    Map<String, String> parameters = new LinkedHashMap<>(3);
    parameters.put("grant_type", "client_credentials");
    return new TokenRequest(url, parameters, client);
  }

  /**
   * Construct a token request for the "refresh_token" credential grant.
   *
   * @param refreshToken The refresh token.
   * @param client       The client credentials
   *
   * @return the corresponding token request.
   */
  public static TokenRequest newRefreshToken(String refreshToken, ClientCredentials client) {
    Objects.requireNonNull(client, "The refresh_token parameter is required");
    Objects.requireNonNull(client, "The client credentials are required");
    String url = toBaseURL(PSConfig.getDefault());
    Map<String, String> parameters = new LinkedHashMap<>(3);
    parameters.put("grant_type", "refresh_token");
    parameters.put("refresh_token", refreshToken);
    return new TokenRequest(url, parameters, client);
  }

  // Getters (return String)
  // --------------------------------------------------------------------------

  /**
   * @return the grant type
   */
  public String grantType() {
    return this._parameters.get("grant_type");
  }

  /**
   * @return the scope used for this request.
   */
  public String scope() {
    return this._parameters.get("scope");
  }

  /**
   * @return The redirect URI used in this request.
   */
  public String redirectURI() {
    return this._parameters.get("client_id");
  }

  /**
   * @param name  The name of the parameter
   *
   * @return the corresponding value or <code>null</code>.
   */
  public String parameter(String name) {
    Objects.requireNonNull(name, "The parameter name cannot be null");
    return this._parameters.get(name);
  }

  // Setters (return TokenRequest)
  // --------------------------------------------------------------------------

  /**
   * Create a new token request with the specified redirect URI.
   *
   * @param url The URL for the redirect URI
   *
   * @return a new token request.
   */
  public TokenRequest redirectURI(String url) {
    Objects.requireNonNull(url, "The redirect_uri must not be null");
    return parameter("redirect_uri", url);
  }

  /**
   * Create a new token request with the specified scope.
   *
   * @param scope The scope
   *
   * @return a new token request.
   */
  public TokenRequest scope(String scope) {
    Objects.requireNonNull(scope, "The scope must not be null");
    return parameter("scope", scope);
  }

  /**
   * Create a new token request with the custom parameter.
   *
   * @param name  The name of the parameter
   * @param value The value of the parameter
   *
   * @return a new token request.
   */
  public TokenRequest parameter(String name, String value) {
    Objects.requireNonNull(name, "The parameter name cannot be null");
    Objects.requireNonNull(value, "The parameter value cannot be null");
    Map<String, String> p = new LinkedHashMap<>(this._parameters);
    p.put(name, value);
    return new TokenRequest(this._url, p, this._client);
  }

  /**
   * Make the request using POST method and returns the corresponding response.
   *
   * @return The corresponding response.
   */
  public TokenResponse post() throws IOException {
    // Create connection to URL using client credentials
    URL url = new URL(this._url);
    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
    String authorization = this._client.toBasicAuthorization();
    connection.addRequestProperty("Authorization", authorization);
    connection.setRequestMethod("POST");

    // Write the parameters
    byte[] parameters = HTTP.encodeParameters(this._parameters).getBytes(StandardCharsets.UTF_8);
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    connection.setRequestProperty("Content-Length", Integer.toString(parameters.length));
    connection.setDoInput(true);
    connection.setDoOutput(true);
    try (OutputStream post = connection.getOutputStream()) {
      post.write(parameters);
      post.flush();
    }
    return TokenResponse.consume(connection, this._client);
  }

  /**
   * Utility method returning the URL of the token request for the specified PageSeeder configuration.
   *
   * @param config The PageSeeder configuration
   *
   * @return The corresponding token endpoint URL.
   */
  public static String toBaseURL(PSConfig config) {
    return config.buildHostURL().append(config.getSitePrefix()).append(TOKEN_ENDPOINT).toString();
  }

  @Override
  public String toString() {
    return "POST "+this._url+"?"+HTTP.encodeParameters(this._parameters).replaceAll("password=([^&]+)", "password=******");
  }

}
