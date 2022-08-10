/*
 * Copyright 2015 Allette Systems (Australia)
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
package org.pageseeder.bridge.control;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.*;
import org.pageseeder.bridge.model.MemberOptions;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PasswordResetOptions;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.net.PSHTTPResourceType;
import org.pageseeder.bridge.net.PSHTTPResponseInfo;
import org.pageseeder.bridge.net.PSHTTPResponseInfo.Status;
import org.pageseeder.bridge.net.Servlets;
import org.pageseeder.bridge.xml.PSMemberHandler;

/**
 * A manager for groups and projects (based on PageSeeder Groups).
 *
 * @author Christophe Lauret
 * @version 0.3.32
 * @since 0.2.0
 */
public final class MemberManager extends Sessionful {

  /**
   * Where the users are cached.
   */
  private static volatile PSEntityCache<PSMember> cache = EHEntityCache.newInstance("psmembers", "email");

  /**
   * Creates a new member manager using the specified session.
   *
   * @param credentials The session used to connect to PageSeeder.
   */
  public MemberManager(PSCredentials credentials) {
    super(credentials);
  }

  /**
   * Returns the member for the specified username.
   *
   * @param username The username of that member
   *
   * @return the corresponding member
   */
  public @Nullable PSMember getByUsername(String username) throws APIException {
    String identifier = Objects.requireNonNull(username);
    PSMember member = cache.get(username);
    if (member == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getMember(identifier).using(this._credentials);
      PSMemberHandler handler = new PSMemberHandler();
      connector.get(handler);
      member = handler.get();
      if (member != null) {
        cache.put(member);
      }
    }
    return member;
  }

  /**
   * Saves the details of the specified member.
   *
   * @param member  The username of that member
   * @param options The options to create the member.
   */
  public void create(PSMember member, MemberOptions options) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.createMember(member, options).using(this._credentials);
    PSMemberHandler handler = new PSMemberHandler(member);
    connector.post(handler);
    PSMember m = handler.get();
    if (m != null) {
      cache.put(m);
    }
  }

  /**
   * Saves the details of the specified member.
   *
   * @param member   The username of that member
   * @param options  The options to create the member.
   * @param password The member's password
   */
  public void create(PSMember member, MemberOptions options, String password) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.createMember(member, options, password).using(this._credentials);
    PSMemberHandler handler = new PSMemberHandler(member);
    connector.post(handler);
    PSMember m = handler.get();
    if (m != null) {
      cache.put(m);
    }
  }

  /**
   * Returns the specified member.
   *
   * @param member The member (id or username must be set).
   *
   * @return the corresponding member
   */
  public @Nullable PSMember get(PSMember member) throws APIException {
    PSMember m = cache.get(Objects.requireNonNull(member));
    if (m == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getMember(member).using(this._credentials);
      PSMemberHandler handler = new PSMemberHandler(member);
      connector.get(handler);
      m = handler.get();
      if (m != null) {
        cache.put(m);
      }
    }
    return m;
  }

  /**
   * Saves the details of the specified member.
   *
   * Cannot be used to change member.isActive(), use {@link #activate(String)} instead.
   *
   * @param member The member to save
   */
  public boolean save(PSMember member) throws APIException {
    // TODO Verify
    // XXX Force email change set to true (requires Admin)
    PSHTTPConnector connector = PSHTTPConnectors.patchMember(member, true).using(this._credentials);
    PSMemberHandler handler = new PSMemberHandler(member);
    PSHTTPResponseInfo resp = connector.patch(handler);
    PSMember m = handler.get();
    if (m != null) {
      cache.put(m);
    }
    return resp.isSuccessful();
  }

  /**
   * Resets the session for this user in PageSeeder ensuring that their session includes all
   * updates made to group membership.
   *
   * <p>This is required after a member has been joined to a group.
   *
   * @return <code>true</code> if the request was successful; <code>false</code> otherwise.
   *
   * @throws APIException If an error occurs while connecting.
   */
  public boolean resetSession() throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.resetSession().using(this._credentials);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Force the password of a user to be reset (administrators only).
   *
   * @param member The username or email of the member.
   *
   * @return <code>true</code> if the request was successful; <code>false</code> otherwise.
   *
   * @throws APIException If an error occurs while connecting.
   */
  public boolean forceResetPassword(String member) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.forceResetPassword(member).using(this._credentials);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Force the password of a user to be reset (administrators only)
   *
   * <p>This method is useful to use the email templates for the group; the member does not
   * have to be a member of that group if using the session of a member of the group.
   *
   * @param group  The group to use for the email templates.
   * @param member The username or email of the member.
   *
   * @return <code>true</code> if the request was successful; <code>false</code> otherwise.
   *
   * @throws APIException If an error occurs while connecting or the group is not identifiable
   */
  public boolean forceResetPassword(PSGroup group, String member) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.forceResetPassword(group, member).using(this._credentials);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Request that the password for the user be reset.
   *
   * @param member The username or email of the member.
   *
   * @return <code>true</code> if the request was successful; <code>false</code> otherwise.
   *
   * @throws APIException If an error occurs while connecting.
   */
  public boolean resetPassword(String member) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.resetPassword(member).using(this._credentials);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Request that the password for the user be reset.
   *
   * <p>This method is useful to use the email templates for the group; the member does not
   * have to be a member of that group if using the session of a member of the group.
   *
   * @param group  The group to use for the email templates.
   * @param member The username or email of the member.
   *
   * @return <code>true</code> if the request was successful; <code>false</code> otherwise.
   *
   * @throws APIException If an error occurs while connecting or the group is not identifiable
   */
  public boolean resetPassword(PSGroup group, String member) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.resetPassword(group, member).using(this._credentials);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Confirm a password reset for the specified user with a key.
   *
   * @param member  The username or email of the member.
   * @param options The password reset options, must include the key
   *
   * @return <code>true</code> if the request was successful; <code>false</code> otherwise.
   *
   * @throws APIException If an error occurs while connecting or the key is missing.
   */
  public boolean resetPassword(String member, PasswordResetOptions options) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.resetPassword(member, options).using(this._credentials);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Confirm a password reset for the specified user with a key.
   *
   * <p>This method is useful to use the email templates for the group; the member does not
   * have to be a member of that group if using the session of a member of the group.
   *
   * @param group  The group to use for the email templates
   * @param member The username or email of the member.
   * @param options The password reset options, must include the key
   *
   * @return <code>true</code> if the request was successful; <code>false</code> otherwise.
   *
   * @throws APIException If an error occurs while connecting or the group is not identifiable or key is missing
   */
  public boolean resetPassword(PSGroup group, String member, PasswordResetOptions options) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.resetPassword(group, member, options).using(this._credentials);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Logout the user from the current session in PageSeeder.
   *
   * <p>This will invalidate the session on PageSeeder. After calling this method, other methods
   * will fail if the call requires
   *
   * @return <code>true</code> if the user was successfully logout;
   *         <code>false</code> if it was not.
   *
   * @throws APIException If an error occurs while connecting to PageSeeder.
   */
  public boolean logout() throws APIException {
    return this._credentials instanceof PSSession && logout((PSSession) this._credentials);
  }

  /**
   * Activate an account for administrators only.
   *
   * <p>If the session does not belong to an administrator the call will fail.
   *
   * @param username The username of the member to activate
   *
   * @return If the member was successfully activated
   *
   * @throws APIException If an error occurs while connecting to PageSeeder.
   */
  public boolean activate(String username) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.getActivate(username).using(this._credentials);
    PSHTTPResponseInfo info = connector.post();
    return info.isSuccessful();
  }

  /**
   * Activate an account.
   *
   * <p>No session is required; since the user is not logged in
   *.
   * @param username The username of the member to activate
   * @param key      The key the required for activation.
   *
   * @return If the member was successfully activated
   *
   * @throws APIException If an error occurs while connecting to PageSeeder.
   */
  public static boolean activate(String username, String key) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.getActivateByKey(username, key);
    PSHTTPResponseInfo info = connector.get();
    return info.isSuccessful();
  }

  /**
   * Creates a session on PageSeeder from the specified username and password.
   *
   * @param username The username
   * @param password The password to login
   *
   * @return A new session if the username and password were valid; <code>null</code> otherwise.
   *
   * @throws APIException If an error occurs while connecting to PageSeeder.
   */
  public static @Nullable PSSession login(String username, String password) throws APIException {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/self");
    connector.addParameter("username", username);
    connector.addParameter("password", password);
    PSHTTPResponseInfo info = connector.get();
    return info.isSuccessful()? connector.getSession() : null;
  }

  /**
   * Allow members to confirm a change of email address by supplying a confirmation key.
   *
   * @param username the member's username
   * @param email    the new email address
   * @param key      the key (emailed to the new email address)
   *
   * @return <code>true</code> if successful.
   *
   * @throws APIException If an error occurs while connecting to PageSeeder.
   */
  public boolean confirmEmailChange(String username, String email, String key) throws APIException {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVLET, "com.pageseeder.ChangeDetailsForm");
    connector.using(this._credentials);
    connector.addParameter("login-username", username);
    connector.addParameter("username", username);
    connector.addParameter("email", email);
    connector.addParameter("key", key);
    connector.includeErrorContent(true);
    PSHTTPResponseInfo info = connector.get();
    return info.isSuccessful();
  }

  /**
   * Logout the user from the current session in PageSeeder.
   *
   * <p>This will invalidate the session on PageSeeder, the session should no longer
   * be used and be discarded.
   *
   * @param session The session to use to logout.
   *
   * @return <code>true</code> if the user was successfully logout;
   *         <code>false</code> if it was not.
   *
   * @throws APIException If an error occurs while connecting to PageSeeder.
   */
  public static boolean logout(PSSession session) throws APIException {
    return logout(session, PSConfig.getDefault());
  }

  /**
   * Logout the user from the current session in PageSeeder.
   *
   * <p>This will invalidate the session on PageSeeder, the session should no longer
   * be used and be discarded.
   *
   * @param session The session to use to logout.
   * @param config  Pageseeder configuration to logout.
   *
   * @return <code>true</code> if the user was successfully logout;
   *         <code>false</code> if it was not.
   *
   * @throws APIException If an error occurs while connecting to PageSeeder.
   */
  public static boolean logout(PSSession session, PSConfig config) throws APIException {
    Objects.requireNonNull(session, "Session is required to logout.");
    Objects.requireNonNull(config, "Pageseeder configuration is required to logout.");
    String servlet = Servlets.LOGIN_SERVLET;
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVLET, servlet);
    connector.setConfig(config);
    connector.using(session);
    connector.addParameter("action", "logout");
    return connector.get().isSuccessful();
  }

  /**
   * Returns the member cache.
   *
   * @return the member cache.
   */
  public static PSEntityCache<PSMember> getCache() {
    return cache;
  }
}
