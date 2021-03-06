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
 * Defines whether is allowed to access specific resources.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Authorizer {

  /**
   * Indicates whether a user is allowed to access a given resource.
   *
   * @param user A user.
   * @param uri  The URI the user is trying to access.
   *
   * @return <code>true</code> is the user can access the resource;
   *         <code>false</code> otherwise.
   */
  AuthorizationResult isUserAuthorized(User user, String uri);

}
