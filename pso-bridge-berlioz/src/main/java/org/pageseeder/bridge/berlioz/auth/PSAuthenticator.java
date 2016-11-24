/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.auth;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.berlioz.auth.PSUser.Builder;
import org.pageseeder.bridge.control.MemberManager;
import org.pageseeder.bridge.http.ContentException;
import org.pageseeder.bridge.http.Method;
import org.pageseeder.bridge.http.Request;
import org.pageseeder.bridge.http.Response;
import org.pageseeder.bridge.http.ServicePath;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMembership;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPResourceType;
import org.pageseeder.bridge.net.UsernamePassword;
import org.pageseeder.bridge.xml.HandlerFactory;
import org.pageseeder.bridge.xml.PSMembershipHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An authenticator that uses PageSeeder to authenticate users.
 *
 * @author Christophe Lauret
 *
 * @version 0.4.0
 * @since 0.1.0
 */
public final class PSAuthenticator implements Authenticator<PSUser> {

  /**
   * The name of the attribute to pass on to login for the username.
   */
  public static final String USERNAME_ATTRIBUTE = "org.pageseeder.bridge.berlioz.auth.Username";

  /**
   * The name of the attribute to pass on to login for the password.
   */
  public static final String PASSWORD_ATTRIBUTE = "org.pageseeder.bridge.berlioz.auth.Password";

  /**
   * Logger for this class.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(PSAuthenticator.class);

  /**
   * How to filter the groups.
   *
   * A comma separated list of groups.
   */
  private String groupFilter = "*";

  /**
   * If set to <code>true</code> logging out will also invalidate the session on PageSeeder.
   */
  private boolean hardLogout = true;

  /**
   * Indicates whether this authenticator should perform a hard logout
   *
   * @param hardLogout <code>true</code> to invalidate the session on PageSeeder;
   *                   <code>false</code> to simply invalidate the session on Berlioz.
   */
  public void setHardLogout(boolean hardLogout) {
    this.hardLogout = hardLogout;
  }

  /**
   * Set the filters to group
   *
   * @param filter
   */
  public void setGroupFilter(String filter) {
    this.groupFilter = filter;
  }

  /**
   * Indicates whether this authenticator will perform a hard logout on PageSeeder
   *
   * @return <code>true</code> to invalidate the session on PageSeeder;
   *                   <code>false</code> to simply invalidate the session on Berlioz.
   */
  public boolean isHardLogout() {
    return this.hardLogout;
  }

  /**
   * The PageSeeder login requires a username and password and checks them against the members on
   * a PageSeeder Server.
   *
   * {@inheritDoc}
   */
  @Override
  public AuthenticationResult login(HttpServletRequest req) throws AuthException {

    // Grab the username and password from parameters
    String username = req.getParameter("username") != null ? req.getParameter("username") : null;
    String password = req.getParameter("password") != null ? req.getParameter("password") : null;

    // Credentials can also be passed on as string attributes (none were specified in the request)
    if ((username == null || username.length() == 0)
     && (password == null || password.length() == 0)) {
      username = req.getAttribute(USERNAME_ATTRIBUTE) != null ? req.getAttribute(USERNAME_ATTRIBUTE).toString() : null;
      password = req.getAttribute(PASSWORD_ATTRIBUTE) != null ? req.getAttribute(PASSWORD_ATTRIBUTE).toString() : null;
    }

    // Required details
    if (username == null || password == null) return AuthenticationResult.INSUFFICIENT_DETAILS;

    // Get the session
    HttpSession session = req.getSession();

    // Already logged in?
    if (session != null) {
      Object o = session.getAttribute(Sessions.USER_ATTRIBUTE);
      if (o instanceof PSUser) {
        PSUser current = (PSUser)o;
        // Already logged in and it is the current user
        if (username.equals(current.getUsername())) return AuthenticationResult.ALREADY_LOGGED_IN;
        else if (username.equals(current.getEmail())) return AuthenticationResult.ALREADY_LOGGED_IN;
        else {
          logoutUser(current);
          session.invalidate();
          session = req.getSession(true);
        }
      }
    }

    // Perform login
    PSUser user = login(username, password);
    if (user != null) {
      if (session == null) {
        session = req.getSession(true);
      }
      session.setAttribute(Sessions.USER_ATTRIBUTE, user);
      return AuthenticationResult.LOGGED_IN;
    } else return AuthenticationResult.INCORRECT_DETAILS;
  }

  @Override
  public AuthenticationResult logout(HttpServletRequest req) throws AuthException {
    // Get the session
    HttpSession session = req.getSession();
    if (session != null) {
      User user = Sessions.getUser(session);
      if (user != null) {
        logoutUser(user);
      }
      // Invalidate the session and create a new one
      session.invalidate();
      return AuthenticationResult.LOGGED_OUT;
    }

    // User was already logged out
    return AuthenticationResult.ALREADY_LOGGED_OUT;
  }

  /**
   * Login the user using their username and password.
   *
   * @param username The username of the user to login
   * @param password The password of the user to login
   *
   * @return The corresponding user or <code>null</code>
   *
   * @throws AuthException Should any error occur while connecting to the server.
   */
  @Override
  public PSUser login(String username, String password) throws AuthException {
    PSUser user = null;
    try {
      if (this.groupFilter == null) {
        user = loginMemberOnly(username, password);
      } else {
        user = loginWithMemberships(username, password);
      }
    } catch (APIException ex) {
      LOGGER.warn("Unable to login", ex);
      throw new AuthException("Unable to login");
    }
    return user;
  }

  @Override
  public boolean logoutUser(User user) throws AuthException {
    if (!(user instanceof PSUser)) return false;
    boolean logout = !this.hardLogout;
    if (this.hardLogout) {
      PSUser u = (PSUser)user;
      PSSession session = u.getSession();
      try {
        if (session != null) {
          logout = MemberManager.logout(session);
        }
      } catch (APIException ex) {
        throw new AuthException("Unable to log out from PageSeeder", ex);
      }
    }
    return logout;
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * Filter the name of the group based on the name.
   *
   * @param name The name of the group
   *
   * @return <code>true</code> if the group is accepted.
   */
  private boolean filter(String name) {
    if ("*".equals(this.groupFilter)) return true;
    boolean accept = false;
    for (String filter : this.groupFilter.split(",")) {
      if (filter.endsWith("*")) {
        if (name.startsWith(filter.substring(0, filter.length()-1))) {
          accept = true;
        }
      } else {
        if (name.equals(filter)) {
          accept = true;
        }
      }
    }
    return accept;
  }

  /**
   * Login the user only without retrieving any membership details.
   *
   * Having an account on PageSeeder is sufficient to get access.
   *
   * @return the user if the login is successful; <code>null</code> otherwise.
   *
   * @throws ContentException If an error occurs such as a connection exception occurs.
   */
  private PSUser loginMemberOnly(String username, String password) throws ContentException {
    PSUser user = null;
    String service = ServicePath.newPath("/self");
    UsernamePassword credentials = new UsernamePassword(username, password);
    try (Response response = new Request(Method.GET, service).using(credentials).response()) {
      if (response.isSuccessful()) {
        PSSession session = response.session();
        PSMember member = response.consumeItem(HandlerFactory.newPSMemberHandler());
        Builder builder = new PSUser.Builder();
        builder.member(member).session(session);
        user = builder.build();
      } else {
        LOGGER.debug("Invalid credentials: {}", response);
      }
    }
    return user;
  }

  /**
   *
   */
  private PSUser loginWithMemberships(String username, String password) throws APIException {
    PSUser user = null;
    String service = ServicePath.newPath("/self/memberships");
    UsernamePassword credentials = new UsernamePassword(username, password);
    try (Response response = new Request(Method.GET, service).using(credentials).response()) {
      if (response.isSuccessful()) {
        PSMembershipHandler handler = new PSMembershipHandler();
        response.consumeXML(handler);

        // Get values from PageSeeder
        List<PSMembership> memberships = handler.list();
        PSSession session = response.session();
        PSMember member = handler.getMember();

        // Generate PSUser
        Builder builder = new PSUser.Builder();
        builder.member(member).session(session);

        // Create account and add roles
        for (PSMembership m : memberships) {
          PSGroup group = m.getGroup();
          if (group != null) {
            String name = group.getName();
            if (filter(name)) {
              builder.addRole(name);
            }
          }
        }
        user = builder.build();

      } else {
        LOGGER.debug("Invalid credentials: {}", response);
      }
    }
    return user;
  }

  /**
   * A connector to get the member currently logged in.
   *
   * @return The corresponding connector
   */
  public static PSHTTPConnector getSelf() {
    String service = "/self";
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

  /**
   * A connector to list the memberships for the member currently logged in.
   *
   * @return The corresponding connector
   */
  public static PSHTTPConnector listMembershipsForSelf() {
    String service = "/self/memberships";
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

}
