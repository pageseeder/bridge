/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.spi;

import java.util.List;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.InvalidEntityException;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.core.PSGroup;
import org.pageseeder.bridge.core.PSMember;
import org.pageseeder.bridge.core.PSMembership;
import org.pageseeder.bridge.net.PSConnector;
import org.pageseeder.bridge.net.PSConnectors;
import org.pageseeder.bridge.xml.PSMembershipHandler;

/**
 * A manager for memberships (based on PageSeeder MemberForGroups and Details)
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public final class MembershipManager extends PSManager {

  /**
   * Internal cache for memberships
   */
  private static volatile PSEntityCache<PSMembership> cache = EHEntityCache.newInstance("psmemberships");

  public MembershipManager(PSSession user) {
    super(user);
  }

  /**
   * Creates the specified membership in PageSeeder.
   *
   * @param membership The Membership to create.
   */
  public void create(PSMembership membership) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSConnector connector = PSConnectors.createMembership(membership, null, true);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    connector.setUser(this.user);
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
    PSConnector connector = PSConnectors.createMembership(membership, password, true);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    connector.setUser(this.user);
    connector.post(handler);
  }

  /**
   * Create a membership by inviting the member to the group.
   *
   * @param membership The membership to create.
   */
  public void invite(PSMembership membership) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSConnector connector = PSConnectors.inviteMembership(membership, true);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    connector.setUser(this.user);
    connector.post(handler);
  }

  /**
   * Create a membership by letting the member inviting herself to the group.
   *
   * @param membership The membership to create.
   */
  public void inviteSelf(PSMembership membership) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSConnector connector = PSConnectors.inviteSelf(membership, true);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    connector.setUser(this.user);
    connector.post(handler);
  }

  /**
   * Creates the specified membership in PageSeeder.
   *
   * @param membership The Membership to create.
   */
  public void register(PSMembership membership) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSConnector connector = PSConnectors.registerMembership(membership);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    connector.setUser(this.user);
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
    PSConnector connector = PSConnectors.getMembershipDetails(group, member);
    PSMembershipHandler handler = new PSMembershipHandler();
    connector.setUser(this.user);
    connector.get(handler);
    return handler.getMembership();
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
    PSConnector connector = PSConnectors.getMembershipDetails(group.getName(), member.getUsername());
    PSMembershipHandler handler = new PSMembershipHandler(group);
    connector.setUser(this.user);
    connector.get(handler);
    return handler.getMembership();
  }

  /**
   * Remove the specified member from the group
   *
   * @param group  The group the member is to be removed from
   * @param member The username of the member
   */
  public void remove(String group, String member) throws APIException {
    PSConnector connector = PSConnectors.deleteMembership(group, member);
    PSMembershipHandler handler = new PSMembershipHandler();
    connector.setUser(this.user);
    connector.post(handler);
  }

  /**
   * Saves the specified membership in PageSeeder.
   *
   * @param membership The Membership to create.
   * @param password   The password for the user (must be strong enough)
   */
  public void save(PSMembership membership) throws APIException {
    save(membership, false);
  }

  /**
   * Saves the specified membership in PageSeeder.
   *
   * @param membership The Membership to create.
   * @param password   The password for the user (must be strong enough)
   */
  public void save(PSMembership membership, boolean forceEmail) throws APIException {
    if (!membership.isValid()) throw new InvalidEntityException(PSMembership.class, membership.checkValid());
    PSConnector connector = PSConnectors.editMembership(membership, forceEmail);
    PSMembershipHandler handler = new PSMembershipHandler(membership);
    connector.setUser(this.user);
    connector.post(handler);
  }


  /**
   * Updates the password of the member.
   */
  public void updatePassword(PSMembership membership, String password) throws APIException {
    PSConnector connector = PSConnectors.updatePassword(membership, password);
    connector.setUser(this.user);
    connector.post();
  }

  /**
   * Returns the list of memberships for the specified member.
   *
   * @param member
   * @return
   */
  public List<PSMembership> listForMember(PSMember member) throws APIException {
    if (!member.isValid()) throw new InvalidEntityException(PSMember.class, member.checkValid());
    PSConnector connector = PSConnectors.listMembershipsForMember(member.getUsername());
    PSMembershipHandler handler = new PSMembershipHandler(member);
    connector.setUser(this.user);
    connector.get(handler);
    return handler.listMemberships();
  }

  /**
   * Returns the list of membership for specific user.
   *
   * @param username the member username
   * @return
   * @throws APIException
   */
  public List<PSMembership> listForMember(String username) throws APIException {
    PSConnector connector = PSConnectors.listMembershipsForMember(username);
    PSMembershipHandler handler = new PSMembershipHandler();
    connector.setUser(this.user);
    connector.get(handler);
    return handler.listMemberships();
  }

  /**
   * Returns the list of memberships for specific group.
   *
   * @param group the name of the group.
   * @return
   * @throws APIException
   */
  public List<PSMembership> listForGroup(PSGroup group) throws APIException {
    if (!group.isValid()) throw new InvalidEntityException(PSGroup.class, group.checkValid());
    PSConnector connector = PSConnectors.listMembershipsForGroup(group.getName());
    PSMembershipHandler handler = new PSMembershipHandler(group);
    connector.setUser(this.user);
    connector.get(handler);
    return handler.listMemberships();
  }

  /**
   *
   * @param membership
   *
   * @return
   * @throws APIException
   */
  public List<PSMembership> find(PSMembership membership, boolean isManager) throws APIException {
    PSConnector connector = PSConnectors.findMembershipsForGroup(membership, isManager);
    PSMembershipHandler handler = new PSMembershipHandler(membership.getGroup());
    connector.setUser(this.user);
    connector.get(handler);
    return handler.listMemberships();
  }


  /**
   * @return the internal cache for memberships.
   */
  public static PSEntityCache<PSMembership> getCache() {
    return cache;
  }
}
