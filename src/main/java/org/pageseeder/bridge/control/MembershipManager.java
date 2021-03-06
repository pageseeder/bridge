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

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.FailedPrecondition;
import org.pageseeder.bridge.InvalidEntityException;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.model.MemberOptions;
import org.pageseeder.bridge.model.MemberOptions.Invitation;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMembership;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.net.PSHTTPResponseInfo;
import org.pageseeder.bridge.net.PSHTTPResponseInfo.Status;
import org.pageseeder.bridge.xml.PSMembershipHandler;

/**
 * A manager for memberships (based on PageSeeder MemberForGroups and Details).
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.2.0
 */
public final class MembershipManager extends Sessionful {

  /**
   * The results of creating a membership operation.
   */
  public enum MembershipResult {

    ok,

    invalid_username,

    invalid_email,

    invalid_group,

    already_in_use,

    already_a_member,

    password_too_weak,

    invalid_password,

    too_many_members,

    member_details_misconfigured,

    no_moderator,

    unknown;

    /**
     * Returns the membership results for the specified response info.
     *
     * @param info The response info from the connector
     *
     * @return The corresponding membership result.
     */
    public static MembershipResult forResponse(PSHTTPResponseInfo info) {
      if (info.isSuccessful()) return ok;
      final int _error;
      try {
        _error = Integer.parseInt(info.getErrorID(), 16);
      } catch (NumberFormatException ex) {
        // not a number, it is unknown then
        return MembershipResult.unknown;
      }
      switch (_error) {
      // 0x1001 If the username contains the character '@'.".
      case 0x1001:
        return invalid_username;
        // 0x1002 If the email address is invalid.
      case 0x1002:
        return invalid_email;
        // 0x1003 If the specified group is a personal group.
      case 0x1003:
        return invalid_group;
        // 0x1004 If the username or email are already in use.
      case 0x1004:
        return already_in_use;
        // 0x1005 If the maximum number of members on the server has been reached
      case 0x1005:
        return too_many_members;
        // 0x1015 Password is too weak
      case 0x1015:
        return password_too_weak;
        // 0x1016 Password is equal to username
      case 0x1016:
        return invalid_password;
        // 0x1023 Invite to admin group not allowed
      case 0x1023:
        return invalid_group;
        // 0x1025 The member already belongs to the group
      case 0x1025:
        return already_a_member;
        // 0x6004 If the member details have not been configured properly
      case 0x6004:
        return member_details_misconfigured;
        // 0x6005 Group has no moderator with an email address (when Self Registration: Moderated)
      case 0x6005:
        return already_a_member;
        // Any other
      default:
        return MembershipResult.unknown;
      }
    }
  }

  /**
   * Internal cache for memberships
   */
  private static volatile PSEntityCache<PSMembership> cache = EHEntityCache.newInstance("psmemberships");

  /**
   * Creates a new manager for membership using the specified session.
   *
   * @param credentials The session used to connect to PageSeeder.
   */
  public MembershipManager(PSCredentials credentials) {
    super(credentials);
  }

  /**
   * Creates the specified membership in PageSeeder.
   *
   * @param membership The Membership to create.
   *
   * @return The result of the membership creation as an enumeration value.
   *
   * @throws APIException if the operation is not successful or caused by client.
   */
  public MembershipResult create(PSMembership membership) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.createMembership(membership, null, true).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    PSHTTPResponseInfo info = connector.post(handler);
    Status status = info.getStatus();
    if (status != Status.SUCCESSFUL && status != Status.CLIENT_ERROR) throw new APIException(info.getMessage());
    return MembershipResult.forResponse(info);
  }

  /**
   * Creates the specified membership in PageSeeder.
   *
   * @param membership The Membership to create.
   * @param password   The password for the user (must be strong enough)
   *
   * @return The result of the membership creation as an enumeration value.
   *
   * @throws APIException if the operation is not successful or caused by client.
   */
  public MembershipResult create(PSMembership membership, String password) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.createMembership(membership, password, true).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    PSHTTPResponseInfo info = connector.post(handler);
    Status status = info.getStatus();
    if (status != Status.SUCCESSFUL && status != Status.CLIENT_ERROR) throw new APIException(info.getMessage());
    return MembershipResult.forResponse(info);
  }

  /**
   * Creates the specified membership in PageSeeder.
   *
   * @param membership The Membership to create.
   * @param password   The password for the user (must be strong enough)
   * @param options    The member options
   *
   * @return The result of the membership creation as an enumeration value.
   *
   * @throws APIException if the operation is not successful or caused by client.
   */
  public MembershipResult create(PSMembership membership, String password, MemberOptions options) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.createMembership(membership, password, options).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    PSHTTPResponseInfo info = connector.post(handler);
    Status status = info.getStatus();
    if (status != Status.SUCCESSFUL && status != Status.CLIENT_ERROR) throw new APIException(info.getMessage());
    return MembershipResult.forResponse(info);
  }

  /**
   * Add a member to a group directly (Admin only).
   *
   * @param membership The membership to create.
   * @param email      <code>true</code> to send a welcome email; <code>false</code> to silently add the member.
   *
   * @return The result of the membership creation as an enumeration value.
   *
   * @throws APIException if the operation is not successful or caused by client.
   */
  public MembershipResult add(PSMembership membership, boolean email) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    MemberOptions options = new MemberOptions();
    options.setInvitation(Invitation.NO);
    options.setWelcomeEmail(email);
    return add(membership, options);
  }

  /**
   * Add a member to a group directly (Admin only).
   *
   * @param membership The membership to create.
   * @param options    The {@link MemberOptions}
   *
   * @return The result of the membership creation as an enumeration value.
   *
   * @throws APIException if the operation is not successful or caused by client.
   */
  public MembershipResult add(PSMembership membership, MemberOptions options) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.inviteMembership(membership, options).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    PSHTTPResponseInfo info = connector.post(handler);
    Status status = info.getStatus();
    if (status != Status.SUCCESSFUL && status != Status.CLIENT_ERROR) throw new APIException(info.getMessage());
    return MembershipResult.forResponse(info);
  }

  /**
   * Invite a member to the group.
   *
   * <p>This method will use the default options by sending a welcome email and an invitation
   * based on the group properties.
   *
   * @param membership The membership to create.
   *
   * @return The result of the membership creation as an enumeration value.
   *
   * @throws APIException if the operation is not successful or caused by client.
   */
  public MembershipResult invite(PSMembership membership) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.inviteMembership(membership, new MemberOptions()).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    PSHTTPResponseInfo info = connector.post(handler);
    Status status = info.getStatus();
    if (status != Status.SUCCESSFUL && status != Status.CLIENT_ERROR) throw new APIException(info.getMessage());
    return MembershipResult.forResponse(info);
  }

  /**
   * Invite a member to the group.
   *
   * @param membership The membership to create.
   * @param email      <code>true</code> to send a welcome email;
   *                   <code>false</code> to silently add the member.
   *
   * @return The result of the membership creation as an enumeration value.
   *
   * @throws APIException if the operation is not successful or caused by client.
   */
  public MembershipResult invite(PSMembership membership, boolean email) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    MemberOptions options = new MemberOptions();
    options.setWelcomeEmail(email);
    PSHTTPConnector connector = PSHTTPConnectors.inviteMembership(membership, options).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    PSHTTPResponseInfo info = connector.post(handler);
    Status status = info.getStatus();
    if (status != Status.SUCCESSFUL && status != Status.CLIENT_ERROR) throw new APIException(info.getMessage());
    return MembershipResult.forResponse(info);
  }

  /**
   * Create a membership by inviting the member to the group.
   *
   * <p>Only the <code>invitation</code> and <code>sendWelcomeEmail</code> are considered.
   *
   * @param membership The membership to create.
   * @param options    The member options.
   *
   * @return The result of the membership creation as an enumeration value.
   *
   * @throws APIException if the operation is not successful or caused by client.
   */
  public MembershipResult invite(PSMembership membership, MemberOptions options) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.inviteMembership(membership, options).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    PSHTTPResponseInfo info = connector.post(handler);
    Status status = info.getStatus();
    if (status != Status.SUCCESSFUL && status != Status.CLIENT_ERROR) throw new APIException(info.getMessage());
    return MembershipResult.forResponse(info);
  }

  /**
   * Create a membership by letting the member inviting herself to the group.
   *
   * <p>A welcome email will be sent to the user.
   *
   * @param membership The membership to create.
   *
   * @return The result of the membership creation as an enumeration value.
   *
   * @throws APIException if the operation is not successful or caused by client.
   */
  public MembershipResult inviteSelf(PSMembership membership) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.inviteSelf(membership, true).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    PSHTTPResponseInfo info = connector.post(handler);
    Status status = info.getStatus();
    if (status != Status.SUCCESSFUL && status != Status.CLIENT_ERROR) throw new APIException(info.getMessage());
    return MembershipResult.forResponse(info);
  }

  /**
   * Create a membership by letting the member inviting herself to the group.
   *
   * To work the group owner must be the same as another group the member
   *
   * @param membership The membership to create.
   * @param email      <code>true</code> to send the welcome email;
   *                   <code>false</code> otherwise (the member is added silently)
   *
   * @return The result of the membership creation as an enumeration value.
   *
   * @throws APIException if the operation is not successful or caused by client.
   */
  public MembershipResult inviteSelf(PSMembership membership, boolean email) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.inviteSelf(membership, email).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    PSHTTPResponseInfo info = connector.post(handler);
    Status status = info.getStatus();
    if (status != Status.SUCCESSFUL && status != Status.CLIENT_ERROR) throw new APIException(info.getMessage());
    return MembershipResult.forResponse(info);
  }

  /**
   * Creates the specified membership in PageSeeder.
   *
   * @param membership The Membership to create.
   *
   * @return The result of the membership creation as an enumeration value.
   *
   * @throws APIException if the operation is not successful or caused by client.
   */
  public MembershipResult register(PSMembership membership) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.registerMembership(membership).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    PSHTTPResponseInfo info = connector.post(handler);
    Status status = info.getStatus();
    if (status != Status.SUCCESSFUL && status != Status.CLIENT_ERROR) throw new APIException(info.getMessage());
    return MembershipResult.forResponse(info);
  }

  /**
   * Returns the membership information for the given group and member.
   *
   * @param group  The name of the group.
   * @param member The username of the member.
   *
   * @return The corresponding membership if it exists
   *         or <code>null</code> if the member does not belong to the group
   */
  public @Nullable PSMembership get(String group, String member) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.getMembershipDetails(group, member).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler();
    connector.get(handler);
    return handler.get();
  }

  /**
   * Returns the membership information for the given group and email or username of member.
   *
   * @param group  The name of the group.
   * @param emailOrUsername The username or email of the member.
   * @param isManager <code>true</code> to indicate that the user is a manager.
   *
   * @return The corresponding membership if it exists
   *         or <code>null</code> if the member does not belong to the group
   */
  public @Nullable PSMembership getAuto(String group, String emailOrUsername, boolean isManager) throws APIException {
    PSMember member = new PSMember();
   // guess if the input value is email address
    if (emailOrUsername != null && emailOrUsername.contains("@")) {
      member.setEmail(emailOrUsername);
    } else {
      member.setUsername(emailOrUsername);
    }
    PSMembership membership = new PSMembership(new PSGroup(group), member);
    PSHTTPConnector connector = PSHTTPConnectors.findMembershipsForGroup(membership, isManager).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler();
    connector.get(handler);
    return handler.get();
  }

  /**
   * Returns the membership information for the given group and member.
   *
   * @param group  The group instance.
   * @param member The member instance.
   *
   * @return The corresponding membership if it exists
   *         or <code>null</code> if the member does not belong to the group
   */
  public @Nullable PSMembership get(PSGroup group, PSMember member) throws APIException {
    if (!group.isValid()) throw new InvalidEntityException(PSGroup.class, group.checkValid());
    if (!member.isValid()) throw new InvalidEntityException(PSMember.class, member.checkValid());
    String groupIdentifier = checkNotNull(group.getIdentifier(), "group id or name");
    String memberIdentifier = checkNotNull(member.getIdentifier(), "member id or username");
    PSHTTPConnector connector = PSHTTPConnectors.getMembershipDetails(groupIdentifier, memberIdentifier).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(group);
    connector.get(handler);
    return handler.get();
  }

  /**
   * Remove the specified member from the group.
   *
   * @param group  The group the member is to be removed from
   * @param member The username of the member
   */
  public void remove(String group, String member) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.deleteMembership(group, member).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler();
    connector.delete(handler);
  }

  /**
   * Saves the specified membership in PageSeeder.
   *
   * @param membership The Membership to create.
   */
  public MembershipResult save(PSMembership membership) throws APIException {
    return save(membership, false);
  }

  /**
   * Saves the specified membership in PageSeeder.
   *
   * @param membership The Membership to create.
   * @param forceEmail A boolean to force email change
   */
  public MembershipResult save(PSMembership membership, boolean forceEmail) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.patchMembership(membership, forceEmail).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    PSHTTPResponseInfo info = connector.patch(handler);
    Status status = info.getStatus();
    if (status != Status.SUCCESSFUL && status != Status.CLIENT_ERROR) throw new APIException(info.getMessage());
    return MembershipResult.forResponse(info);
  }

  /**
   * Updates the password of the member.
   */
  public void updatePassword(PSMembership membership, String password) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.updatePassword(membership, password).using(this._credentials);
    connector.patch();
  }

  /**
   * @return the list of memberships for the specified member.
   */
  public List<PSMembership> listForMember(PSMember member) throws APIException {
    if (!member.isValid()) throw new InvalidEntityException(PSMember.class, member.checkValid());
    String memberIdentifier = checkNotNull(member.getIdentifier(), "member id or username");
    PSHTTPConnector connector = PSHTTPConnectors.listMembershipsForMember(memberIdentifier).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(member);
    connector.get(handler);
    return handler.list();
  }

  /**
   * Returns the list of membership for specific user.
   *
   * @param username the member username
   */
  public List<PSMembership> listForMember(String username) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.listMembershipsForMember(username).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler();
    connector.get(handler);
    return handler.list();
  }

  /**
   * Returns the list of memberships for specific group.
   *
   * @param group the name of the group.
   *
   * @return the list of memberships.
   */
  public List<PSMembership> listForGroup(PSGroup group) throws APIException {
    return listForGroup(group, true);
  }

  /**
   * Returns the list of memberships for specific group.
   *
   * @param group            the name of the group.
   * @param includeSubgroups if members from subgroups should be included
   *
   * @return the list of memberships.
   */
  public List<PSMembership> listForGroup(PSGroup group, boolean includeSubgroups) throws APIException {
    if (!group.isValid()) throw new InvalidEntityException(PSGroup.class, group.checkValid());
    String groupIdentifier = checkNotNull(group.getIdentifier(), "group id or name");
    PSHTTPConnector connector = PSHTTPConnectors.listMembershipsForGroup(groupIdentifier, includeSubgroups).using(this._credentials);
    PSMembershipHandler handler = new PSMembershipHandler(group);
    connector.get(handler);
    return handler.list();
  }

  /**
   * Returns the list of memberships for specific group matching the values of the specified membership instance.
   *
   * @param membership search predicate.
   * @param isManager  <code>true</code> to indicate that the user is a manager.
   *
   * @return the list of memberships.
   */
  public List<PSMembership> find(PSMembership membership, boolean isManager) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.findMembershipsForGroup(membership, isManager).using(this._credentials);
    PSGroup group = checkNotNull(membership.getGroup(), "group of membership");
    PSMembershipHandler handler = new PSMembershipHandler(group);
    connector.get(handler);
    return handler.list();
  }

  /**
   * @return the internal cache for memberships.
   */
  public static PSEntityCache<PSMembership> getCache() {
    return cache;
  }

  /**
   * Precondition requiring the specified object to be non-null.
   *
   * @param o    The object to check for <code>null</code>
   * @param name The name of the object to generate the message.
   *
   * @return The object (not <code>null</code>)
   *
   * @throws FailedPrecondition If the pre-condition failed.
   */
  private static <T> @NonNull T checkNotNull(@Nullable T o, String name) throws FailedPrecondition {
    if (o == null) throw new FailedPrecondition(name + " must not be null");
    return o;
  }

}
