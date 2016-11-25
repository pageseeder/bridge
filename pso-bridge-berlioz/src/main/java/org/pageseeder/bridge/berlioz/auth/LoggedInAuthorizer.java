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
 * An authorizer which only requires the user to be logged in.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public final class LoggedInAuthorizer implements Authorizer {

  /**
   * The singleton instance.
   */
  private static final LoggedInAuthorizer SINGLETON = new LoggedInAuthorizer();

  /**
   * No need to allow creation of this class.
   */
  private LoggedInAuthorizer() {
  }

  /**
   * Always returns <code>AUTHORIZED</code> if the specified user is not <code>null</code>.
   *
   * @param user A user.
   * @param uri  The URI the user is trying to access.
   *
   * @return <code>AUTHORIZED</code> is the user is not <code>null</code>;
   *         <code>UNAUTHORIZED</code> otherwise.
   */
  @Override
  public AuthorizationResult isUserAuthorized(User user, String uri) {
    return user != null? AuthorizationResult.AUTHORIZED : AuthorizationResult.UNAUTHORIZED;
  }

  /**
   * @return a singleton instance.
   */
  public static LoggedInAuthorizer getInstance() {
    return SINGLETON;
  }

}
