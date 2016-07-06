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

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.pageseeder.bridge.util.Base64;

/**
 * Class used to store the client credentials.
 */
public final class ClientCredentials {

  /**
   * PageSeeder client IDs are normally 16-digit long identifiers
   *
   * <p>We don't need to be too strict and we allow Base64 strings for extensibility.
   *
   * <p>No assumption is made on the length, but we discard any token that is too short or too long.
   */
  protected static final Pattern VALID_CLIENT_ID = Pattern.compile("[a-zA-Z0-9=.+/_-]{16,512}");

  /**
   * The client identifier.
   */
  private final String _client;

  /**
   * The client secret.
   */
  private final String _secret;

  /**
   * The basic authorization header value.
   *
   * We keep a copy as it will have to be computed every time this client is use.
   */
  private final String _basic;

  /**
   * Creates a new set of PageSeeder OAuth client credentials.
   *
   * @param client The client identifier
   * @param secret The secret.
   *
   * @throws NullPointerException if either argument is <code>null</code>
   * @throws IllegalArgumentException if either argument is considered invalid.
   */
  public ClientCredentials(String client, String secret) {
    if (client == null || secret == null) throw new NullPointerException();
    if (!VALID_CLIENT_ID.matcher(client).matches())
      throw new IllegalArgumentException("Client ID is invalid");
    if (secret.length() == 0)
      throw new IllegalArgumentException("Client secret is empty");
    this._client = client;
    this._secret = secret;
    this._basic = "Basic "+Base64.encode(this._client+":"+this._secret, StandardCharsets.UTF_8);
  }

  /**
   * @return The client (cannot be <code>null</code>).
   */
  public String client() {
    return this._client;
  }

  /**
   * @return The secret (cannot be <code>null</code>).
   */
  public String secret() {
    return this._secret;
  }

  /**
   * @return The basic authorization string
   */
  public String toBasicAuthorization() {
    return this._basic;
  }

}
