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
import java.util.Objects;
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
import org.pageseeder.bridge.berlioz.auth.AuthSessions;
import org.pageseeder.bridge.berlioz.auth.AuthorizationResult;
import org.pageseeder.bridge.berlioz.auth.Authorizer;
import org.pageseeder.bridge.berlioz.auth.LoggedInAuthorizer;
import org.pageseeder.bridge.berlioz.auth.ProtectedRequest;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.net.UnsafeSSL;
import org.pageseeder.bridge.net.UsernamePassword;
import org.pageseeder.bridge.oauth.ClientCredentials;
import org.pageseeder.bridge.oauth.TokenRequest;
import org.pageseeder.bridge.oauth.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PasswordFilter implements Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(PasswordFilter.class);

  static {
    UnsafeSSL.enableIfSystemProperty();
  }

  private static final String DEFAULT_LOGIN = "/login.html";

  private static final String DEFAULT_TARGET = "/home.html";

  /**
   * Path to the login form (GET)
   */
  private String loginForm = DEFAULT_LOGIN;

  /**
   * Path to the login action (POST)
   */
  private String loginAction = DEFAULT_LOGIN;

  /**
   * Path to the default target URL.
   */
  private String defaultTarget = DEFAULT_TARGET;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    String contextPath = filterConfig.getServletContext().getContextPath();
    this.loginForm = contextPath+Objects.toString(filterConfig.getInitParameter("login-form"), DEFAULT_LOGIN);
    this.loginAction = contextPath+Objects.toString(filterConfig.getInitParameter("login-action"), DEFAULT_LOGIN);
    this.defaultTarget = contextPath+Objects.toString(filterConfig.getInitParameter("default-target"), DEFAULT_TARGET);
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    Properties p = GlobalSettings.getNode("oauth.password-credentials");
    if (p.containsKey("client")) {
      // Use HTTP specific requests.
      doHttpFilter((HttpServletRequest)req, (HttpServletResponse)res, chain);
    } else {
      LOGGER.error("This filter requires a valid `oauth.password-credentials.client`.");
      ((HttpServletResponse)res).sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
  }

  @Override
  public void destroy() {
    this.loginForm = DEFAULT_LOGIN;
    this.loginAction = DEFAULT_LOGIN;
    this.defaultTarget = DEFAULT_TARGET;
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

    // Get relevant URI.
    String uri = req.getRequestURI();
    OAuthUser user = OAuthUtils.getOAuthUserInSession(req.getSession());

    LOGGER.debug("{} {} {}", req.getMethod(), uri, user);

    // The user is authenticated, go through authorization process
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

      // Do login the user
      if (this.loginAction.equals(uri) && "POST".equals(req.getMethod())) {
        login(req, res);
      }

      // Always let the user through to the login form
      else if (this.loginForm.equals(uri) && "GET".equals(req.getMethod())) {
        chain.doFilter(req, res);
      }

      // Anywhere else, redirect to the login form
      else {
        loginForm(req, res);
      }
    }
  }

  /**
   * Login the user to PageSeeder using the username and password.
   *
   * <p>If successful, the user is automatically redirected to the initially protected
   * target URI or the default page after authentication.
   *
   * @param req The servlet request
   * @param res The servlet response
   *
   * @throws IOException
   * @throws ServletException
   */
  public void login(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {

    // Client credentials
    Properties p = GlobalSettings.getNode("oauth.password-credentials");
    String clientId = p.getProperty("client");
    String secret = p.getProperty("secret");
    ClientCredentials clientCredentials = new ClientCredentials(clientId, secret);

    // User credentials
    String username = req.getParameter("username");
    String password = req.getParameter("password");
    UsernamePassword userCredentials = new UsernamePassword(username, password);

    // Create request
    TokenResponse oauth = TokenRequest.newPassword(userCredentials, clientCredentials).post();
    if (oauth.isSuccessful()) {
      PSToken token = oauth.getAccessToken();

      // If open id was defined, we can get the member directly
      PSMember member = oauth.getMember();
      if (member == null) {
        member = OAuthUtils.retrieve(token);
      }

      if (member != null) {
        OAuthUser user = new OAuthUser(member, token);
        HttpSession session = req.getSession(false);
        String goToURL = this.defaultTarget;
        if (session != null) {
          ProtectedRequest target = (ProtectedRequest)session.getAttribute(AuthSessions.REQUEST_ATTRIBUTE);
          if (target != null) {
            goToURL = target.url();
            session.invalidate();
          }
        }
        session = req.getSession(true);
        session.setAttribute(AuthSessions.USER_ATTRIBUTE, user);
        res.sendRedirect(goToURL);
      } else {
        LOGGER.error("Unable to identify user!");
        res.sendError(HttpServletResponse.SC_FORBIDDEN);
      }

    } else {
      LOGGER.error("OAuth failed '{}': {}", oauth.getError(), oauth.getErrorDescription());
      if (oauth.isAvailable()) {
        res.sendError(HttpServletResponse.SC_FORBIDDEN);
      } else {
        res.sendError(HttpServletResponse.SC_BAD_GATEWAY);
      }
    }

  }


  private void loginForm(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
    // Save the protected request
    String url  = req.getRequestURI();
    String query = req.getQueryString();
    if (query != null) {
      url = url + '?' +query;
    }
    ProtectedRequest target = new ProtectedRequest(url);

    // Store in current session
    HttpSession session = req.getSession(true);
    session.setAttribute(AuthSessions.REQUEST_ATTRIBUTE, target);

    res.sendRedirect(this.loginForm);
  }


}
