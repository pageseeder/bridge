/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.pageseeder.bridge.berlioz.auth.AuthorizationResult;
import org.pageseeder.bridge.berlioz.auth.Authorizer;
import org.pageseeder.bridge.berlioz.auth.LoggedInAuthorizer;
import org.pageseeder.bridge.berlioz.auth.ProtectedRequest;
import org.pageseeder.bridge.berlioz.auth.Sessions;
import org.pageseeder.bridge.berlioz.auth.User;

/**
 * Filters request and check that the user has access to the underlying resource.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public final class SecurityFilter implements Filter {

  /**
   * Do nothing.
   *
   * {@inheritDoc}
   */
  @Override
  public void init(FilterConfig config) throws ServletException {
  }

  /**
   * Do nothing.
   *
   * {@inheritDoc}
   */
  @Override
  public void destroy() {
  }

  /**
   * Does the filtering.
   *
   * {@inheritDoc}
   */
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
     throws IOException, ServletException {
    // Use HTTP specific requests.
    doHttpFilter((HttpServletRequest)req, (HttpServletResponse)res, chain);
  }

  /**
   * Does the filtering.
   *
   * @param req   the HTTP servlet request
   * @param res   the HTTP servlet response
   * @param chain The filter chain
   *
   * @throws IOException      If thrown by any of the underlying filters or servlets.
   * @throws ServletException If thrown by any of the underlying filters or servlets.
   */
  private void doHttpFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
     throws IOException, ServletException {

    // Retrieve the user from the session
    HttpSession session = req.getSession(true);
    Object o = session.getAttribute(Sessions.USER_ATTRIBUTE);

    // The user is authenticated
    if (o instanceof User) {

      // Get relevant URI.
      String uri = req.getRequestURI();

      // Invoke Authorizer method to see if user can access resource.
      Authorizer authorizer = LoggedInAuthorizer.getInstance();
      AuthorizationResult result = authorizer.isUserAuthorized((User)o, uri);
      if (result == AuthorizationResult.AUTHORIZED) {
        chain.doFilter(req, res);
      } else {
        res.sendError(HttpServletResponse.SC_FORBIDDEN);
      }

    // The user has not been authenticated yet
    } else {
      String url  = req.getRequestURI();
      String query = req.getQueryString();
      if (query != null) {
        url = url + '?' +query;
      }
      ProtectedRequest target = new ProtectedRequest(url);
      session.setAttribute(Sessions.REQUEST_ATTRIBUTE, target);
      res.setHeader("WWW-Authenticate", "FORM");
      res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

  }
}
