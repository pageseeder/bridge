/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.control;

import java.util.List;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.InvalidEntityException;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.PSSession;
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
 * @version 0.2.32
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
        case 0x1001: return invalid_username;
        // 0x1002 If the email address is invalid.
        case 0x1002: return invalid_email;
        // 0x1003 If the specified group is a personal group.
        case 0x1003: return invalid_group;
        // 0x1004 If the username or email are already in use.
        case 0x1004: return already_in_use;
        // 0x1005 If the maximum number of members on the server has been reached
        case 0x1005: return too_many_members;
        // 0x1015 Password is too weak
        case 0x1015: return password_too_weak;
        // 0x1016 Password is equal to username
        case 0x1016: return invalid_password;
        // 0x1023 Invite to admin group not allowed
        case 0x1023: return invalid_group;
        // 0x1025 The member already belongs to the group
        case 0x1025: return already_a_member;
        // 0x6004 If the member details have not been configured properly
        case 0x6004: return member_details_misconfigured;
        // 0x6005 Group has no moderator with an email address (when Self Registration: Moderated)
        case 0x6005: return already_a_member;
        // Any other
        default: return MembershipResult.unknown;
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
   * @param session The session used to connect to PageSeeder.
   */
  public MembershipManager(PSSession session) {
    super(session);
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
    PSHTTPConnector connector = PSHTTPConnectors.createMembership(membership, null, true).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.createMembership(membership, password, true).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.createMembership(membership, password, options).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.inviteMembership(membership, options).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.inviteMembership(membership, new MemberOptions()).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.inviteMembership(membership, options).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.inviteMembership(membership, options).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.inviteSelf(membership, true).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.inviteSelf(membership, email).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.registerMembership(membership).using(this._session);
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
  public PSMembership get(String group, String member) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.getMembershipDetails(group, member).using(this._session);
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
  public PSMembership get(PSGroup group, PSMember member) throws APIException {
    if (!group.isValid()) throw new InvalidEntityException(PSGroup.class, group.checkValid());
    if (!member.isValid()) throw new InvalidEntityException(PSMember.class, member.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.getMembershipDetails(group.getName(), member.getUsername()).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.deleteMembership(group, member).using(this._session);
    PSMembershipHandler handler = new PSMembershipHandler();
    connector.post(handler);
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
    PSHTTPConnector connector = PSHTTPConnectors.editMembership(membership, forceEmail).using(this._session);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    PSHTTPResponseInfo info = connector.post(handler);
    Status status = info.getStatus();
    if (status != Status.SUCCESSFUL && status != Status.CLIENT_ERROR) throw new APIException(info.getMessage());
    return MembershipResult.forResponse(info);
  }

  /**
   * Updates the password of the member.
   */
  public void updatePassword(PSMembership membership, String password) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.updatePassword(membership, password).using(this._session);
    connector.post();
  }

  /**
   * @return the list of memberships for the specified member.
   */
  public List<PSMembership> listForMember(PSMember member) throws APIException {
    if (!member.isValid()) throw new InvalidEntityException(PSMember.class, member.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.listMembershipsForMember(member.getIdentifier()).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.listMembershipsForMember(username).using(this._session);
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
    if (!group.isValid()) throw new InvalidEntityException(PSGroup.class, group.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.listMembershipsForGroup(group.getName()).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.findMembershipsForGroup(membership, isManager).using(this._session);
    PSMembershipHandler handler = new PSMembershipHandler(membership.getGroup());
    connector.get(handler);
    return handler.list();
  }

  /**
   * @return the internal cache for memberships.
   */
  public static PSEntityCache<PSMembership> getCache() {
    return cache;
  }

}
