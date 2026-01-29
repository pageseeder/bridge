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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.pageseeder.berlioz.GlobalSettings;
import org.pageseeder.berlioz.content.ContentRequest;
import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.model.PSMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class to deal with sessions
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public final class AuthSessions {

  /**
   * The name of the attribute that contains the User currently logged in.
   */
  public static final String USER_ATTRIBUTE = "org.pageseeder.bridge.berlioz.auth.User";

  /**
   * The name of the attribute that contains the request to a protected resource.
   */
  public static final String REQUEST_ATTRIBUTE = "org.pageseeder.bridge.berlioz.auth.HttpRequest";

  /**
   * The property of the PageSeeder administrator user.
   */
  protected static final String ADMIN_USER_PROPERTY = "bridge.admin";

  /**
   * The property of the PageSeeder setup user.
   */
  protected static final String SETUP_USER_PROPERTY = "bridge.setup";

  /**
   * Logger for this class.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthSessions.class);

  /**
   * One minute in millis seconds.
   */
  private static final long ONE_MINUTE_IN_MS = 60000;

  /**
   * Caches the PageSeeder user instances.
   */
  private static final Map<String, PSUser> CACHE = new ConcurrentHashMap<>();

  /** Utility class */
  private AuthSessions() {}

  /**
   * Returns the user stored in the session.
   *
   * @param req the content request.
   * @return the user if any or <code>null</code>.
   */
  public static PSSession getPSSession(HttpServletRequest req) {
    PSUser user = getInstanceOfUser(req);
    return user.getSession();
  }

  /**
   * Returns the user stored in the session.
   *
   * @param req the content request.
   * @return the user if any or <code>null</code>.
   */
  public static PSSession getPSSession(ContentRequest req) {
    PSUser user = getInstanceOfUser(req);
    return user.getSession();
  }

  /**
   * Returns the user stored in the session.
   *
   * @param req the content request.
   * @return the user if any or <code>null</code>.
   */
  public static User getUser(HttpServletRequest req) {
    return getUser(req.getSession());
  }

  /**
   * Returns the user stored in the session.
   *
   * @param req the content request.
   * @return the user if any or <code>null</code>.
   */
  public static User getUser(ContentRequest req) {
    return getUser(req.getSession());
  }

  /**
   * Returns the user stored in the session.
   *
   * @param <T> The the type of user.
   *
   * @param req the content request.
   * @return the user if any or <code>null</code>.
   */
  public static <T extends User> T getInstanceOfUser(ContentRequest req) {
    return (T)getUser(req.getSession());
  }

  /**
   *Returns the user stored in the session.
   *
   * @param <T> The the type of user.
   *
   * @param req the content request.
   * @return the user if any or <code>null</code>.
   */
  public static <T extends User> T getInstanceOfUser(HttpServletRequest req) {
    return (T)getUser(req.getSession());
  }

  /**
   * Returns the user instance from the session if any.
   *
   * @param session the session
   * @return the user if any or <code>null</code>.
   */
  public static User getUser(HttpSession session) {
    if (session == null) return null;
    Object o = session.getAttribute(AuthSessions.USER_ATTRIBUTE);
    if (o instanceof User) return (User)o;
    // No match or not a user
    return null;
  }

  /**
   * Returns the session of the PageSeeder administrator user.
   *
   * <p>The administrator user can be configured as:
   * <pre>{@code
   * <bridge>
   *   <admin username="[username]" password="[password]"/>
   * </bridge>
   * }</pre>
   *
   *
   * @return The PageSeeder Admin user or <code>null</code> if it is not configured properly or could not login.
   * @throws APIException If an error occurs while trying to login the admin user.
   */
  public static PSSession getAdmin() throws APIException {
    return getConfiguredSession(ADMIN_USER_PROPERTY);
  }

  /**
   * Returns the PageSeeder administrator member.
   *
   * <p>The administrator user can be configured as:
   * <pre>{@code
   * <bridge>
   *   <admin username="[username]" password="[password]"/>
   * </bridge>
   * }</pre>
   *
   * @return The PageSeeder setup member or <code>null</code> if it is not configured properly or could not login.
   * @throws APIException If an error occurs while trying to login the setup user.
   */
  public static PSMember getAdminMember() throws APIException {
    PSUser user = AuthSessions.getConfiguredUser(ADMIN_USER_PROPERTY);
    return user != null? user.toMember() : null;
  }

  /**
   * Returns the session of the setup user.
   *
   * <p>The setup user can be configured as:
   * <pre>{@code
   * <bridge>
   *   <setup username="[username]" password="[password]"/>
   * </bridge>
   * }</pre>
   *
   * @return The PageSeeder setup user or <code>null</code> if it is not configured properly or could not login.
   * @throws APIException If an error occurs while trying to login the setup user.
   */
  public static PSSession getSetup() throws APIException {
    return AuthSessions.getConfiguredSession(SETUP_USER_PROPERTY);
  }

  /**
   * Returns the setup member.
   *
   * <p>The admin user can be configured as:
   * <pre>{@code
   * <bridge>
   *   <setup username="[username]" password="[password]"/>
   * </bridge>
   * }</pre>
   *
   * @return The PageSeeder setup member or <code>null</code> if it is not configured properly or could not login.
   * @throws APIException If an error occurs while trying to login the setup user.
   */
  public static PSMember getSetupMember() throws APIException {
    PSUser user = AuthSessions.getConfiguredUser(SETUP_USER_PROPERTY);
    return user != null? user.toMember() : null;
  }

  /**
   * Returns the session of a user that has been predefined in the configuration.
   *
   * <p>A predefined user can be configured as:
   * <pre>{@code
   *   <bridge>
   *     <[user] username="[username]" password="[password]"/>
   *   </bridge>
   * }</pre>
   *
   * @return The PageSeeder setup member or <code>null</code> if it is not configured properly or could not login.
   * @throws APIException If an error occurs while trying to login the setup user.
   */
  public static PSSession getConfiguredSession(String property) throws APIException {
    return toSession(AuthSessions.getConfiguredUser(property));
  }

  /**
   * Indicates whether the session is still valid for the specified session.
   *
   * @param user The PageSeeder session to check.
   *
   * @return <code>true</code> if the session is still valid;
   *         <code>false</code> otherwise.
   */
  public static boolean hasValidSession(PSUser user) {
    if (user == null) return false;
    return isValid(user.getSession());
  }

  /**
   * Indicates whether the session is still valid for the specified session.
   *
   * @param session The PageSeeder session to check.
   *
   * @return <code>true</code> if the session is still valid;
   *         <code>false</code> otherwise.
   */
  public static boolean isValid(PSSession session) {
    if (session == null) return false;
    int minutes = GlobalSettings.get("pageseeder.session.timeout", 60);
    long maxSessionAge = minutes * ONE_MINUTE_IN_MS;
    return session.age() < maxSessionAge;
  }

  /**
   * Returns the session for the specified user.
   *
   * @param user The user to test.
   *
   * @return the session of this user or <code>null</code> if the user is <code>null</code>.
   */
  public static PSSession toSession(PSUser user) {
    return user != null? user.getSession() : null;
  }

  /**
   * Returns this user as a member.
   *
   * @param user The user to test.
   *
   * @return the user as a member or <code>null</code> if the user is <code>null</code>.
   */
  public static PSMember toMember(PSUser user) {
    return user != null? user.toMember() : null;
  }


  /**
   * Returns the user from the property stored in the global settings.
   *
   * <p>This class will log the user to PageSeeder to retrieve his info.
   *
   * <p>If the password is using some
   *
   * @param property The property of the PageSeeder user.
   *
   * @return The user or <code>null</code> if it is not configured properly or could not login.
   *
   * @throws APIException Should an error occur while attempting login
   */
  public static PSUser getConfiguredUser(String property) throws APIException {
    String username = GlobalSettings.get(property+".username");
    String password = GlobalSettings.get(property+".password");

    // We must have both a username and a password in order to login
    if (username == null) {
      LOGGER.warn("Unable to return user - config property '{}.username' is null.", property);
      return null;
    }

    // Try the cache
    PSUser user = CACHE.get(property);

    // Ensure that the PageSeeder session is still valid and can be used
    if (user == null || !hasValidSession(user) || !user.getUsername().equals(username)) {
      // TODO
//      if (password.startsWith("OB1:")) {
//        password = Obfuscator.clear(password.substring(4));
//      } else {
//        LOGGER.warn("Config property '{}.password' left in clear - consider obfuscating.", property);
//      }
      try {
        Authenticator<PSUser> authenticator = new PSAuthenticator();
        user = authenticator.login(username, password);
        if (user != null) {
          CACHE.put(property, user);
        } else {
          LOGGER.warn("User config property '{}' not setup property - results in null user", property);
        }
      } catch (Exception ex) {
        throw new APIException(ex);
      }
    }
    return user;
  }
}
