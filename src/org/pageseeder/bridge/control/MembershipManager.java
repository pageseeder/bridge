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
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMembership;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.xml.PSMembershipHandler;

/**
 * A manager for memberships (based on PageSeeder MemberForGroups and Details)
 *
 * @author Christophe Lauret
 * @version 0.2.0
 * @since 0.2.0
 */
public final class MembershipManager extends Sessionful {

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
   */
  public void create(PSMembership membership) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.createMembership(membership, null, true).using(this._session);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    connector.post(handler);
  }

  /**
   * Creates the specified membership in PageSeeder.
   *
   * @param membership The Membership to create.
   * @param password   The password for the user (must be strong enough)
   */
  public void create(PSMembership membership, String password) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.createMembership(membership, password, true).using(this._session);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    connector.post(handler);
  }

  /**
   * Create a membership by inviting the member to the group.
   *
   * @param membership The membership to create.
   */
  public void invite(PSMembership membership) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.inviteMembership(membership, true).using(this._session);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    connector.post(handler);
  }

  /**
   * Create a membership by letting the member inviting herself to the group.
   *
   * @param membership The membership to create.
   */
  public void inviteSelf(PSMembership membership) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.inviteSelf(membership, true).using(this._session);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    connector.post(handler);
  }

  /**
   * Creates the specified membership in PageSeeder.
   *
   * @param membership The Membership to create.
   */
  public void register(PSMembership membership) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.registerMembership(membership).using(this._session);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    connector.post(handler);
  }

  /**
   * Returns the membership information for the given group and member.
   *
   * @param group  The name of the group.
   * @param member The username of the member.
   *
   * @return The corresponding membership if it exists
   *         or <code>null</code> if the member does not belong to the group
   *
   * @throws APIException
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
   * Remove the specified member from the group
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
  public void save(PSMembership membership) throws APIException {
    save(membership, false);
  }

  /**
   * Saves the specified membership in PageSeeder.
   *
   * @param membership The Membership to create.
   * @param forceEmail A boolean to force emeil change
   */
  public void save(PSMembership membership, boolean forceEmail) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.editMembership(membership, forceEmail).using(this._session);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    connector.post(handler);
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
    PSHTTPConnector connector = PSHTTPConnectors.listMembershipsForMember(member.getUsername()).using(this._session);
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
