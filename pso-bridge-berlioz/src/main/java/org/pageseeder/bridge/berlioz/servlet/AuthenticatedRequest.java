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
