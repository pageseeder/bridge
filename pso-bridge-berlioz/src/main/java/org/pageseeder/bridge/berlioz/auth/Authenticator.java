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

import javax.servlet.http.HttpServletRequest;

/**
 * Must provide the login and logout mechanisms.
 *
 * @param <U> The type of user the authenticator implementation accepts
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Authenticator<U extends User> {

  /**
   * Logs the specified user in.
   *
   * <p>The servlet request must contain the details sufficient to login (eg. parameters, headers).
   *
   * <p>Implementations should specify which details are required to login.
   *
   * @param req the HTTP Servlet Request that contains the details sufficient to login.
   *
   * @return The result of this authentication process.
   *
   * @throws AuthException if any error occurs while trying to login.
   */
  AuthenticationResult login(HttpServletRequest req) throws AuthException;

  /**
   * Logs the specified user out.
   *
   * @param req The HTTP servlet request.
   *
   * @return The result of this authentication process..
   *
   * @throws AuthException Should an error occur while logging out.
   */
  AuthenticationResult logout(HttpServletRequest req) throws AuthException;

  /**
   * Logs the user in using its username and password.
   *
   * @param username The username
   * @param password The password
   *
   * @return The user instance.
   *
   * @throws AuthException Should an error occur while logging the user in.
   */
  U login(String username, String password) throws AuthException;

  /**
   * Logs the specified user out.
   *
   * @param user Logout the specified user.
   *
   * @return <code>true</code> if the logout request succeeded, <code>false</code> otherwise.
   *
   * @throws AuthException Should an error occur while logging the user out.
   */
  boolean logoutUser(User user) throws AuthException;

}
