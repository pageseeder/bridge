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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.net.HTTP;

/**
 * This class provides a simple mechanism to build the URL to the authorization endpoint
 * as part of the authorization code flow.
 *
 * <p>Typical usage:
 * <pre><code>
 * String clientId = "1234abcd1234abcd";
 *
 * // Create a new request with the specified client ID
 * AuthorizationRequest request = AuthorizationRequest.newAuthorization(clientId);
 *
 * // Save the state used in request to compare with response from authorizer
 * String state = request.state();
 *
 * // Use this URL to redirect the client
 * String url = request.toURLString();
 * </code></pre>
 *
 * @version 0.10.2
 * @since 0.9.0
 */
public final class AuthorizationRequest {

  /**
   * The PageSeeder authorization endpoint.
   */
  private static final String TOKEN_ENDPOINT = "/oauth/authorize";

  /**
   * The url based on the PageSeeder config.
   */
  private final String _url;

  /**
   * The parameters to send.
   */
  private final Map<String, String> _parameters;

  /**
   * Creates a new instance.
   *
   * @param url        The URL to the authorization endpoint.
   * @param parameters The parameters to add.
   */
  private AuthorizationRequest(String url, Map<String, String> parameters) {
    this._url = url;
    this._parameters = parameters;
  }

  /**
   * Creates a new client ID and automatically generate a random state ID.
   *
   * @param clientId the client identifier
   *
   * @return the authorization request.
   */
  public static AuthorizationRequest newAuthorization(String clientId) {
    Objects.requireNonNull(clientId, "The client_id parameter is required for an authorization");
    // Constructs OAuth2 redirect
    String state = UUID.randomUUID().toString().substring(0, 12);
    String url = toBaseURL(PSConfig.getDefault());
    Map<String, String> parameters = new LinkedHashMap<>(3);
    parameters.put("response_type", "code");
    parameters.put("client_id", clientId);
    parameters.put("state", state);
    return new AuthorizationRequest(url, parameters);
  }

  // GETTERS (return String)
  // --------------------------------------------------------------------------

  /**
   * @return the state parameter
   */
  public @Nullable String state() {
    return this._parameters.get("state");
  }

  /**
   * @return The client ID used in this request.
   */
  public @Nullable String clientId() {
    return this._parameters.get("client_id");
  }

  /**
   * @return the scope used for this request.
   */
  public @Nullable String scope() {
    return this._parameters.get("scope");
  }

  /**
   * @param name  The name of the parameter
   *
   * @return the corresponding value or <code>null</code>.
   */
  public @Nullable String parameter(String name) {
    Objects.requireNonNull(name, "The parameter name cannot be null");
    return this._parameters.get(name);
  }

  // SETTERS (return AuthorizationRequest)
  // --------------------------------------------------------------------------

  /**
   * Create a new token request with the specified state.
   *
   * @param state The state
   *
   * @return a new token request.
   */
  public AuthorizationRequest state(String state) {
    Objects.requireNonNull(state, "The state parameter cannot be null");
    return parameter("state", state);
  }

  /**
   * Create a new token request with the specified redirect URI.
   *
   * @param url The URL for the redirect URI
   *
   * @return a new token request.
   */
  public AuthorizationRequest redirectURI(String url) {
    Objects.requireNonNull(url, "The redirect_uri parameter cannot be null");
    return parameter("redirect_uri", url);
  }

  /**
   * Create a new token request with the specified scope.
   *
   * @param scope The scope
   *
   * @return a new token request.
   */
  public AuthorizationRequest scope(String scope) {
    Objects.requireNonNull(scope, "The scope parameter cannot be null");
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
  public AuthorizationRequest parameter(String name, String value) {
    Objects.requireNonNull(name, "The parameter name cannot be null");
    Objects.requireNonNull(value, "The parameter value cannot be null");
    Map<String, String> p = new LinkedHashMap<>(this._parameters);
    p.put(name, value);
    return new AuthorizationRequest(this._url, p);
  }

  /**
   * @return the URL for this authorization request.
   */
  public String toURLString() {
    return this._url+"?"+HTTP.encodeParameters(this._parameters);
  }

  /**
   * Utility method returning the URL of the authorization request for the specified PageSeeder configuration.
   *
   * @param config The PageSeeder configuration
   *
   * @return The corresponding authorization endpoint URL.
   */
  public static String toBaseURL(PSConfig config) {
    return config.buildWebsiteURL(TOKEN_ENDPOINT);
  }

  @Override
  public String toString() {
    return toURLString();
  }

}
