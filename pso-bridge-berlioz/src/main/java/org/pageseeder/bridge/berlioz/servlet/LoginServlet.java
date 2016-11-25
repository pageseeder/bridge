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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.pageseeder.bridge.berlioz.auth.AuthException;
import org.pageseeder.bridge.berlioz.auth.AuthenticationResult;
import org.pageseeder.bridge.berlioz.auth.Authenticator;
import org.pageseeder.bridge.berlioz.auth.Sessions;
import org.pageseeder.bridge.berlioz.auth.User;
import org.pageseeder.bridge.berlioz.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A servlet to login.
 *
 * <p>This servlet actually performs the login using an authenticator.
 *
 * <h3>Initialisation parameters</h3>
 * <p>See {@link #init(ServletConfig)}.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.3
 * @since 0.1.0
 */
public final class LoginServlet extends HttpServlet {

  /**
   * As per requirement for the Serializable interface.
   */
  private static final long serialVersionUID = -5279152811865484362L;

  /**
   * The logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(LoginServlet.class);

  /**
   * The URI of the default target page.
   */
  protected static final String DEFAULT_TARGET = "/";

  /**
   * The URI of the login page.
   */
  private String loginPage = null;

  /**
   * The URI of the default target page.
   */
  private String defaultTarget = DEFAULT_TARGET;

  /**
   * This Servlet accepts two initialisation parameters.
   *
   * <p><code>login-page</code> is required and should point to the login page which includes the
   * form to login.
   * <p><code>default-target</code> is optional and should point to the default target after login,
   * defaults to "/".
   *
   * {@inheritDoc}
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    this.loginPage = config.getInitParameter("login-page");
    this.defaultTarget = config.getInitParameter("default-target");
    if (this.defaultTarget == null) {
      this.defaultTarget = DEFAULT_TARGET;
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    this.loginPage = null;
    this.defaultTarget = DEFAULT_TARGET;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    User user = Sessions.getUser(req);

    // We've already forwarded the user once, they may be a configuration problem
    if (req.getAttribute(getServletName()) != null) {
      LOGGER.error("Loop detected: check your configuration!");
      res.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
      return;
    }

    // Determine the target page based on authentication status
    String target = this.loginPage;
    if (user != null) {
      target = this.defaultTarget;
      res.setHeader("X-Deck-Auth", user.getName());
    }

    // The target page is null (e.g. if login servlet is not configured properly)
    if (target == null) {
      LOGGER.warn("No target to forward to!");
      res.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    // Set flag to prevent infinite loops
    req.setAttribute(getServletName(), "forward");

    // Adjust for context
    if (req.getContextPath() != null) {
      target = req.getContextPath()+target;
    }

    // Forward user to target page
    LOGGER.debug("Forwarding user to {}", target);
    req.getRequestDispatcher(target).forward(req, res);

  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    // Get the authenticator
    HttpSession session = req.getSession();
    String target = getTarget(req);

    // Perform login
    try {
      Authenticator<? extends User> authenticator = Configuration.getAuthenticator();
      AuthenticationResult result = authenticator.login(req);
      LOGGER.debug("Login User: {}", result);

      // Logged in successfully
      if (result == AuthenticationResult.LOGGED_IN || result == AuthenticationResult.ALREADY_LOGGED_IN) {

        // Forward the original request
        if (target != null) {
          LOGGER.debug("Redirecting to {}", target.toString());
          res.sendRedirect(target.toString());
          if (session != null) {
            session.removeAttribute(Sessions.REQUEST_ATTRIBUTE);
          }

        } else {
          LOGGER.debug("Redirecting to {}", this.defaultTarget);
          String context = req.getContextPath() == null ? "" : req.getContextPath();
          res.sendRedirect(context+this.defaultTarget);
        }

      // Login failed
      } else {
        if (target != null) {
          session = req.getSession(true);
          session.setAttribute(Sessions.REQUEST_ATTRIBUTE, target);
        }
        if (this.loginPage != null) {
          String ctxt = req.getContextPath() == null ? "" : req.getContextPath();
          LOGGER.debug("Redirecting to "+ctxt+this.loginPage+"?message=Login failed");
          res.sendRedirect(ctxt+this.loginPage+"?message=Login failed");
        } else {
          res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login failed");
        }
      }

    } catch (AuthException ex) {

      ex.printStackTrace();
      // Unable to connect to PageSeeder
      res.sendError(HttpServletResponse.SC_BAD_GATEWAY, ex.getMessage());

    }

  }

  /**
   * Filter the target for the login.
   *
   * @param req The HTTP servlet request
   *
   * @return the filtered target.
   */
  private static String getTarget(HttpServletRequest req) {
    HttpSession session = req.getSession();
    String target = null;

    // Check if target in session already
    if (session != null) {
      Object t = session.getAttribute(Sessions.REQUEST_ATTRIBUTE);
      if (t != null) {
        target = t.toString();
      }
    }

    // No target, let's look for it somewhere else
    if (target == null) {

      // Check if the target was specified in the request
      String t = req.getParameter("target");

      // We've got something, let's see if it's valid
      if (t != null) {
        try {
          // Base URL from servlet container
          URI base = new URI(req.getScheme(), null, req.getServerName(), req.getLocalPort(), "/", null, null);
          URI uri = base.resolve(t);

          // The specified target must match the scheme, host and port of server
          if (base.getScheme().equals(uri.getScheme())
           && base.getHost().equals(uri.getHost())
           && base.getPort() == uri.getPort()) {
            // Write target
            target = uri.getPath();
            if (uri.getQuery() != null) {
              target = target +"?"+uri.getQuery();
            }
            if (uri.getFragment() != null) {
              target = target +"#"+uri.getFragment();
            }
          }
        } catch (IllegalArgumentException ex) {
          LOGGER.warn("Illegal target URL {}", t, ex);
        } catch (URISyntaxException ex) {
          LOGGER.error("Illegal base URL", ex);
        }
      }
    }

    // Hopefully, we've got a target by now...
    return target;
  }

}
