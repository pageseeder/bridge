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
package org.pageseeder.bridge.berlioz.oauth;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.pageseeder.berlioz.GlobalSettings;
import org.pageseeder.bridge.PSToken;
import org.pageseeder.bridge.berlioz.auth.AuthorizationResult;
import org.pageseeder.bridge.berlioz.auth.Authorizer;
import org.pageseeder.bridge.berlioz.auth.LoggedInAuthorizer;
import org.pageseeder.bridge.berlioz.auth.ProtectedRequest;
import org.pageseeder.bridge.berlioz.auth.AuthSessions;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.net.UnsafeSSL;
import org.pageseeder.bridge.oauth.AuthorizationRequest;
import org.pageseeder.bridge.oauth.ClientCredentials;
import org.pageseeder.bridge.oauth.TokenRequest;
import org.pageseeder.bridge.oauth.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security filter using OAuth authorization code to secure access to pages.
 */
public final class AuthorizationCodeFilter implements Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationCodeFilter.class);

  private static final String OAUTH_STATE = "org.pageseeder.bridge.berlioz.auth.OAuthState";

  static {
    UnsafeSSL.enableIfSystemProperty();
  }

  @Override
  public void init(FilterConfig filterConfig) {
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    Properties p = GlobalSettings.getNode("oauth.authorization-code");
    if (p.containsKey("client")) {
      // Use HTTP specific requests.
      doHttpFilter((HttpServletRequest)req, (HttpServletResponse)res, chain);
    } else {
      LOGGER.error("This filter requires a valid `oauth.authorization-code.client`.");
      ((HttpServletResponse)res).sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
  }

  @Override
  public void destroy() {
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
    OAuthUser user = OAuthUtils.getOAuthUserInSession(req.getSession());

    // Get relevant URI.
    String context = req.getContextPath();
    String uri = req.getRequestURI();

    // The user is authenticated and has a valid token
    if (user != null) {

      // Invoke Authorizer method to see if user can access resource.
      Authorizer authorizer = LoggedInAuthorizer.getInstance();
      AuthorizationResult result = authorizer.isUserAuthorized(user, uri);
      if (result == AuthorizationResult.AUTHORIZED) {
        chain.doFilter(req, res);
      } else {
        res.sendError(HttpServletResponse.SC_FORBIDDEN);
      }

    // The user has not been authenticated yet
    } else {
      if (uri.startsWith(context+"/auth")) {
        token(req, res);
      } else {
        authorize(req, res);
      }
    }

  }

  /**
   * Token endpoint to exchange the authorization with a Token.
   *
   * Send the request MUST include the code and state parameters.
   *
   * <p>This method requires the <code>code</code> and <code>secret</code> properties
   * to be set for <code>oauth.authorization-code</code> global Berlioz setting..
   *
   * @param req The HTTP servlet request.
   * @param res The HTTP servlet response.
   *
   * @throws IOException If thrown by the {@link HttpServletResponse#sendRedirect(String)} method.
   */
  public void token(HttpServletRequest req, HttpServletResponse res) throws IOException {
    String code = req.getParameter("code");
    String state = req.getParameter("state");

    // Check the state
    HttpSession session = req.getSession();
    String expected = (String)session.getAttribute(OAUTH_STATE);
    if (expected == null) {
      LOGGER.error("State already used or no longer in memory");
      res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    if (!expected.equals(state)) {
      LOGGER.error("State does not match!");
      res.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    // Constructs OAuth2 redirect
    Properties p = GlobalSettings.getNode("oauth.authorization-code");
    String clientId = p.getProperty("client");
    String secret = p.getProperty("secret");
    ClientCredentials client = new ClientCredentials(clientId, secret);

    // Make OAuth token request
    TokenResponse oauth = TokenRequest.newAuthorizationCode(code, client).post();
    if (oauth.isSuccessful()) {
      PSToken token = oauth.getAccessToken();
      PSMember member = oauth.getMember();
      if (member == null) {
        member = OAuthUtils.retrieve(token);
      }

      if (member != null) {
        OAuthUser user = new OAuthUser(member, token);
        ProtectedRequest target = (ProtectedRequest)session.getAttribute(AuthSessions.REQUEST_ATTRIBUTE);
        session.invalidate();
        session = req.getSession(true);
        session.setAttribute(AuthSessions.USER_ATTRIBUTE, user);
        res.sendRedirect(target.url());
      } else {
        LOGGER.error("Unable to identify user!");
        res.sendError(HttpServletResponse.SC_FORBIDDEN);
      }
    } else {
      LOGGER.error("OAuth failed '{}': {}", oauth.getError(), oauth.getErrorDescription());
      res.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
  }

  /**
   * Send the authorization redirect URL for OAuth authorization code flow.
   *
   * <p>This method requires the <code>oauth.authorization-code.client</code> global Berlioz property.
   *
   * @param req The HTTP servlet request.
   * @param res The HTTP servlet response.
   *
   * @throws IOException If thrown by the {@link HttpServletResponse#sendRedirect(String)} method.
   */
  public void authorize(HttpServletRequest req, HttpServletResponse res) throws IOException {

    // Store in current session
    ProtectedRequest target = ProtectedRequest.create(req);
    HttpSession session = req.getSession(true);
    session.setAttribute(AuthSessions.REQUEST_ATTRIBUTE, target);

    // Constructs OAuth2 redirect
    Properties p = GlobalSettings.getNode("oauth.authorization-code");
    String clientId = p.getProperty("client");
    String scope = p.getProperty("scope");

    // Create the request
    AuthorizationRequest request = AuthorizationRequest.newAuthorization(clientId);
    if (scope != null) {
      request.scope(scope);
    }

    // Grab the state
    String state = request.state();
    session.setAttribute(OAUTH_STATE, state);

    // Redirect user to authorize URL
    String authorizeURL = request.toURLString();
    res.sendRedirect(authorizeURL);
  }

}
