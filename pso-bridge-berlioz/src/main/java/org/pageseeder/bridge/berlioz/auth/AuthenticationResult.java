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
package org.pageseeder.bridge.berlioz.auth;

/**
 * An enumeration for the results of authentication methods such as login and logout.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public enum AuthenticationResult {

  /**
   * The login succeeded and resulted in the user being logged in.
   */
  LOGGED_IN,

  /**
   * The user was already logged in.
   */
  ALREADY_LOGGED_IN,

  /**
   * The login could not proceed because the user provided insufficient details.
   */
  INSUFFICIENT_DETAILS,

  /**
   * The login failed because the user provided incorrect details.
   */
  INCORRECT_DETAILS,

  /**
   * The user has been logged out.
   */
  LOGGED_OUT,

  /**
   * The user was already logged out.
   */
  ALREADY_LOGGED_OUT,

  /**
   * The user account is locked.
   */
  ACCOUNT_LOCKED;

}
