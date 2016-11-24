/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.auth;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.control.MemberManager;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMembership;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.net.PSHTTPResponseInfo;
import org.pageseeder.bridge.xml.PSMembershipHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An authenticator that uses PageSeeder to authenticate users.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public final class ExtentedAuthenticator<T extends User> implements Authenticator<T> {

  /**
   * Logger for this class.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ExtentedAuthenticator.class);

  /**
   * If set to <code>true</code> logging out will also invalidate the session on PageSeeder.
   */
  private boolean hardLogout = true;

  /**
   *
   */
  private UserBuilder<T> builder = null;

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

    // Grab the username and password
    String username = req.getParameter("username") != null ? req.getParameter("username") : null;
    String password = req.getParameter("password") != null ? req.getParameter("password") : null;

    // Required details
    if (username == null || password == null) return AuthenticationResult.INSUFFICIENT_DETAILS;

    // Get the session
    HttpSession session = req.getSession();

    // Already logged in?
    if (session != null) {
      Object o = session.getAttribute(Sessions.USER_ATTRIBUTE);
      if (o instanceof User) {
        User current = (User)o;
        // Already logged in and it is the current user
        if (username.equals(current.getName())) return AuthenticationResult.ALREADY_LOGGED_IN;
        else {
          logoutUser(current);
          session.invalidate();
          session = req.getSession(true);
        }
      }
    }

    // Perform login
    User user = login(username, password);
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
  public T login(String username, String password) throws AuthException {
    T user = null;
    try {
      PSHTTPConnector connector = PSHTTPConnectors.listMembershipsForMember(username).using(username, password);
      PSMembershipHandler handler = new PSMembershipHandler();
      PSHTTPResponseInfo response = connector.get(handler);
      if (response.isSuccessful()) {
        // Get values from PageSeeder
        List<PSMembership> memberships = handler.list();
        PSSession session = connector.getSession();
        PSMember member = handler.getMember();

        if (this.builder != null) {
          // Pass values to callback
          this.builder.setMember(member);
          this.builder.setSession(session);
          for (PSMembership m : memberships) {
            this.builder.addMembership(m);
          }

          // Build the user
          user = this.builder.build();
        } else {
          LOGGER.warn("No builder specified - this method will always return null!");
        }

      } else {
        LOGGER.debug("Invalid credentials: {}", response);
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


  public interface UserBuilder<T extends User> {

    void setMember(PSMember member);

    void setSession(PSSession session);

    void addMembership(PSMembership member);

    T build();
  }

}
