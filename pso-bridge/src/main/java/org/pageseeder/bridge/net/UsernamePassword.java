/*
 * Copyright 2020 Allette Systems (Australia)
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
package org.pageseeder.bridge.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.pageseeder.bridge.PSCredentials;

/**
 * A username and password pair to authenticate a user on PageSeeder.
 *
 * @author Christophe Lauret
 *
 * @version 0.11.22
 * @since 0.3.32
 */
public final class UsernamePassword implements PSCredentials {

  /**
   * The user ID.
   */
  private final String username;

  /**
   * The password.
   */
  private final String password;

  /**
   * Creates a new username and password set of credentials.
   *
   * @param username The username
   * @param password The password
   *
   * @throws NullPointerException if either argument is <code>null</code>
   * @throws IllegalArgumentException if either argument is considered invalid.
   */
  public UsernamePassword(String username, String password) {
    if (username == null || password == null) throw new NullPointerException();
    this.username = username;
    this.password = password;
  }

  /**
   * @return The username (cannot be <code>null</code>).
   */
  public String username() {
    return this.username;
  }

  /**
   * @return The password (cannot be <code>null</code>).
   */
  public String password() {
    return this.password;
  }

  /**
   * @return The basic authorization string
   */
  public String toBasicAuthorization() {
    byte[] bc = (this.username +":"+this.password).getBytes(StandardCharsets.UTF_8);
    return "Basic "+ Base64.getEncoder().encodeToString(bc);
  }

}
