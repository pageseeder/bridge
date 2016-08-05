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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.http.Method;
import org.pageseeder.bridge.http.Request;
import org.pageseeder.bridge.http.Response;
import org.pageseeder.bridge.http.ServiceError;
import org.pageseeder.bridge.http.ServicePath;
import org.pageseeder.bridge.net.UsernamePassword;
import org.pageseeder.bridge.xml.BasicHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

/**
 * Registration of OAuth clients on PageSeeder.
 *
 * <p>This class provides a simple API to register an OAuth client PageSeeder.
 *
 * @version 0.9.5
 * @since 0.9.5
 */
public final class ClientRegistration {

  /** Logger for this class */
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientRegistration.class);

  /**
   * Default scope with OpenID <code>"openid email profile"</code>.
   */
  public static String DEFAULT_OPENID_SCOPE = "openid email profile";

  /**
   * Valid grant types that can be assigned to a client.
   */
  private static List<String> VALID_GRANT_TYPES = Arrays.asList(
    "authorization_code",
    "implicit",
    "password",
    "client_credentials"
  );

  /**
   * The name of the client
   */
  private String _clientName;

  /**
   * The grant type.
   */
  private String grantType;

  /**
   * The redirect URI (for authorization code grant type)
   */
  private String redirectURI;

  /**
   * URI of the client.
   */
  private String clientURI;

  /**
   * Max age for the access token.
   */
  private long accessTokenMaxAge;

  /**
   * Max age for the refresh token (if applicable)
   */
  private long refreshTokenMaxAge;

  /**
   * The scope
   */
  private String scope = "openid email profile";

  /**
   * A description for the client.
   */
  private String description;

  /**
   * Create a client registration for a client with the specified name;
   *
   * @param clientName the name of the client to register.
   */
  public ClientRegistration(String clientName) {
    this._clientName = Objects.requireNonNull(clientName, "Client name must not be null");
    if ("".equals(clientName)) throw new IllegalArgumentException("Client name must not be empty");
  }

  /**
   * @return The name of the client to register.
   */
  public String getClientName() {
    return this._clientName;
  }

  /**
   * @return The grant type.
   */
  public String getGrantType() {
    return this.grantType;
  }

  /**
   * Set the grant type.
   *
   * <p>It must be one of:
   * <ul>
   *   <li><code>authorization_code</code></li>
   *   <li><code>implicit</code></li>
   *   <li><code>password</code></li>
   *   <li><code>client_credentials</code></li>
   * </ul>
   *
   * @param grantType The grant type for the client to register.
   */
  public void setGrantType(String grantType) {
    if (!VALID_GRANT_TYPES.contains(grantType))
      throw new IllegalArgumentException("Invalid grant type");
    this.grantType = grantType;
  }

  /**
   * @return the redirect URI
   */
  public String getRedirectURI() {
    return this.redirectURI;
  }

  /**
   * Sets the redirect URI for the following grant types:
   * <ul>
   *   <li><code>authorization_code</code></li>
   *   <li><code>implicit</code></li>
   * </ul>
   *
   * @param redirectURI the redirect URI
   */
  public void setRedirectURI(String redirectURI) {
    this.redirectURI = redirectURI;
  }


  public String getClientURI() {
    return this.clientURI;
  }

  public void setClientURI(String clientURI) {
    this.clientURI = clientURI;
  }

  public long getAccessTokenMaxAge() {
    return this.accessTokenMaxAge;
  }

  public void setAccessTokenMaxAge(long age, TimeUnit unit) {
    this.accessTokenMaxAge = TimeUnit.SECONDS.convert(age, unit);
  }

  public long getRefreshTokenMaxAge() {
    return this.refreshTokenMaxAge;
  }

  public void setRefreshTokenMaxAge(long age, TimeUnit unit) {
    this.refreshTokenMaxAge = TimeUnit.SECONDS.convert(age, unit);
  }

  public String getScope() {
    return this.scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Register the new OAuth client.
   *
   * @param credentials The username and password of the user
   * @param config The PageSeeder configuration to use.
   *
   * @return The credentials for client.
   */
  public ClientCredentials register(UsernamePassword credentials, PSConfig config) {
    String service = ServicePath.newPath("/service/oauth/members/{member}/clients", credentials.username());
    Request request = new Request(Method.POST, service).using(credentials).config(config);
    request.parameter("name", this._clientName);
    if (this.description != null) {
      request.parameter("description", this.description);
    }
    if (this.grantType != null) {
      request.parameter("grant-type", this.grantType);
    }

    boolean requiresRedirectURI = "authorization_code".equals(this.grantType) || "implicit".equals(this.grantType);
    if (requiresRedirectURI && this.redirectURI != null) {
      request.parameter("redirect-uri", this.redirectURI);
    } else if (requiresRedirectURI && this.redirectURI == null) {
      LOGGER.warn("Missing redirect URI for grant type {}", this.grantType);
    } else if (!requiresRedirectURI && this.redirectURI != null) {
      LOGGER.warn("Ignoring redirect URI for grant type {}", this.grantType);
    }

    if (this.clientURI != null) {
      request.parameter("client-uri", this.clientURI);
    }
    if (this.accessTokenMaxAge > 0) {
      request.parameter("access-token-max-age", Long.toString(this.accessTokenMaxAge));
    }
    if (this.refreshTokenMaxAge > 0) {
      if ("client_credentials".equals(this.grantType)) {
        LOGGER.warn("Ignoring refresh token for client credentials grant type");
      } else {
        request.parameter("refresh-token-max-age", Long.toString(this.refreshTokenMaxAge));
      }
    }
    if (this.scope != null) {
      request.parameter("scope", this.scope);
    }
    Response response = request.response();
    if (response.isSuccessful()) return response.consumeItem(new ClientCredentialsHandler());
    else {
      ServiceError error = response.consumeServiceError();
      LOGGER.error("Unable to register client - error {}: {}", error.code(), error.message());
    }
    return null;
  }

  /**
   * Extracts the client registration details from the response.
   */
  public static class ClientCredentialsHandler extends BasicHandler<ClientCredentials> {

    /**
     * The client identifier.
     */
    private String identifier;

    /**
     * The client secret.
     */
    private String secret;

    @Override
    public void startElement(String element, Attributes atts) {
      if (isElement("client-registration")) {
        this.secret = atts.getValue("secret");
      } else if (isElement("client")) {
        this.identifier = atts.getValue("identifier");
      }
    }

    @Override
    public void endElement(String element) {
      if (isElement("client-registration")) {
        ClientCredentials credentials = new ClientCredentials(this.identifier, this.secret);
        add(credentials);
      }
    }
  }

}
