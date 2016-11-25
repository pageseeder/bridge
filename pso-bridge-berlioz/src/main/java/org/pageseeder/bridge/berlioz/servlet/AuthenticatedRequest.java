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
package org.pageseeder.bridge.berlioz.servlet;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * A servlet request wrapper implementing security methods mapped to the
 * authenticated PageSeeder user.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public final class AuthenticatedRequest extends HttpServletRequestWrapper {

  public AuthenticatedRequest(HttpServletRequest req) {
    // TODO
    super(req);
  }

  @Override
  public Principal getUserPrincipal() {
    // TODO Auto-generated method stub
    return super.getUserPrincipal();
  }

  @Override
  public String getAuthType() {
    // TODO Auto-generated method stub
    return super.getAuthType();
  }

  @Override
  public boolean isUserInRole(String role) {
    // TODO Auto-generated method stub
    return super.isUserInRole(role);
  }

}
