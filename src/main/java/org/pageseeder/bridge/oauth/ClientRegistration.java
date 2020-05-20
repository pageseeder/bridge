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

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.http.*;
import org.pageseeder.bridge.net.UsernamePassword;
import org.pageseeder.bridge.xml.BasicHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Registration of OAuth clients on PageSeeder.
 *
 * <p>This class provides a simple API to register an OAuth client PageSeeder.
 *
 * @version 0.12.0
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
  private static final List<String> VALID_GRANT_TYPES = Arrays.asList(
    "authorization_code",
    "implicit",
    "password",
    "client_credentials"
  );

  /** The name of the client (required and unique) */
  private final String _clientName;

  /** The grant type. */
  private @Nullable String grantType;

  /** The redirect URI (for authorization code grant type) */
  private @Nullable String redirectURI;

  /** URI of the client. */
  private @Nullable String clientURI;

  /** Name of the application for this client. */
  private @Nullable String appName;

  /** The webhook client secret. */
  private @Nullable String webhookSecret;

  /** Max age for the access token in seconds. */
  private long accessTokenMaxAge;

  /** Max age for the refresh token in seconds (if applicable) */
  private long refreshTokenMaxAge;

  /** The scope. */
  private String scope = "openid email profile";

  /** A description for the client. */
  private @Nullable String description;

  /**
   * Create a client registration for a client with the specified name.
   *
   * <p>The client name is required and must be unique.
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
  public @Nullable String getGrantType() {
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
   *
   * @throws IllegalArgumentException If the grant type is invalid.
   */
  public void setGrantType(String grantType) {
    if (!VALID_GRANT_TYPES.contains(grantType))
      throw new IllegalArgumentException("Invalid grant type");
    this.grantType = grantType;
  }

  /**
   * @return the redirect URI
   */
  public @Nullable String getRedirectURI() {
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

  /**
   * @return The URI of the client.
   */
  public @Nullable String getClientURI() {
    return this.clientURI;
  }

  /**
   * Set the URI, URL or Website for the client.
   *
   * @param clientURI The URI of the client.
   */
  public void setClientURI(String clientURI) {
    this.clientURI = clientURI;
  }

  /**
   * @return Max age for the access token in seconds.
   */
  public long getAccessTokenMaxAge() {
    return this.accessTokenMaxAge;
  }

  /**
   * Set the maximum age of access token issued for this client.
   *
   * @param age  Max age for the access token.
   * @param unit THe time unit to use for the specified max age value
   */
  public void setAccessTokenMaxAge(long age, TimeUnit unit) {
    this.accessTokenMaxAge = TimeUnit.SECONDS.convert(age, unit);
  }

  /**
   * @return Max age for the refresh token in seconds (if applicable)
   */
  public long getRefreshTokenMaxAge() {
    return this.refreshTokenMaxAge;
  }

  /**
   * Set the maximum age of refresh token issued for this client.
   *
   * @param age Max age for the refresh token (if applicable)
   * @param unit THe time unit to use for the specified max age value
   */
  public void setRefreshTokenMaxAge(long age, TimeUnit unit) {
    this.refreshTokenMaxAge = TimeUnit.SECONDS.convert(age, unit);
  }

  /**
   * @return The OAuth scope that this client can have.
   */
  public String getScope() {
    return this.scope;
  }

  /**
   * The scope for this client.
   *
   * <p>To use OpenID for login, the scope must include "openid email profile"
   *
   * @param scope The OAuth scope that this client can have.
   */
  public void setScope(String scope) {
    this.scope = scope;
  }

  /**
   * @return An optional description for this client.
   */
  public @Nullable String getDescription() {
    return this.description;
  }

  /**
   * @param description An optional description for this client.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Set the application name for the client.
   *
   * The app name is different from the client name, it is not required to be unique
   * and is usually specified in the case when multiple clients from the same application
   * are created.
   *
   * @param appName An optional name for the application.
   */
  public void setAppName(String appName) {
    this.appName = appName;
  }

  /**
   * @return An optional name for the application.
   */
  public @Nullable String getAppName() {
    return this.appName;
  }

  public void setWebhookSecret(String webhookSecret) {
    this.webhookSecret = webhookSecret;
  }

  public @Nullable String getWebhookSecret() {
    return this.webhookSecret;
  }

  /**
   * Register the new OAuth client.
   *
   * @param credentials The username and password of the user
   * @param config The PageSeeder configuration to use.
   *
   * @return The credentials for client.
   */
  public @Nullable ClientCredentials register(UsernamePassword credentials, PSConfig config) {
    String service = ServicePath.newPath("/oauth/members/{member}/clients", credentials.username());
    Request request = new Request(Method.POST, service).using(credentials).config(config);
    request.parameter("name", this._clientName);
    String d = this.description;
    if (d != null) {
      request.parameter("description", d);
    }

    String g = this.grantType;
    if (g != null) {
      request.parameter("grant-type", g);
    }

    String r = this.redirectURI;
    boolean requiresRedirectURI = "authorization_code".equals(this.grantType) || "implicit".equals(this.grantType);
    if (requiresRedirectURI && r != null) {
      request.parameter("redirect-uri", r);
    } else if (requiresRedirectURI && this.redirectURI == null) {
      LOGGER.warn("Missing redirect URI for grant type {}", this.grantType);
    } else if (!requiresRedirectURI && this.redirectURI != null) {
      LOGGER.warn("Ignoring redirect URI for grant type {}", this.grantType);
    }

    String c = this.clientURI;
    if (c != null) {
      request.parameter("client-uri", c);
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
    String app = this.appName;
    if (app != null) {
      request.parameter("app", app);
    }
    String webhook = this.webhookSecret;
    if (webhook != null) {
      request.parameter("webhook-secret", webhook);
    }
    Response response = request.response();
    if (response.isSuccessful()) return response.consumeItem(new ClientCredentialsHandler());
    else {
      ServiceError error = response.consumeServiceError();
      if (error != null) {
        LOGGER.error("Unable to register client - service error {}: {}", error.code(), error.message());
      } else {
        LOGGER.error("Unable to register client - error: {}", response.message());
      }
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
    private @Nullable String identifier;

    /**
     * The client secret.
     */
    private @Nullable String secret;

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
        String clientId = this.identifier;
        String clientSecret = this.secret;
        if (clientId == null) {
          LOGGER.warn("Unable to retrieve client ID from client-registration");
        } else if (clientSecret == null) {
          LOGGER.warn("Unable to retrieve client secret from client-registration");
        } else {
          ClientCredentials credentials = new ClientCredentials(clientId, clientSecret);
          add(credentials);
        }
      }
    }
  }

}
