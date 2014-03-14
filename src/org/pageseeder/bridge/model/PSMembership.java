/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSEntity;

/**
 *
 *
 * @author Christophe Lauret
 * @version 0.2.1
 * @since 0.2.0
 */
public final class PSMembership implements PSEntity  {

  /** As per recommendation */
  private static final long serialVersionUID = 2L;

  private Long id;

  private PSGroup group;

  private PSMember member;

  private boolean listed = true;

  private PSNotification notification;

  private PSRole role;

  private PSDetails details = null;

  private transient String key = null;

  /**
   *
   */
  public PSMembership() {
  }

  /**
   *
   */
  public PSMembership(PSGroup group, PSMember member) {
    this.group = group;
    this.member = member;
  }

  /**
   * @return the id
   */
  @Override
  public final Long getId() {
    return this.id;
  }

  @Override
  public String getKey() {
    if (this.key == null && this.group != null && this.member != null) {
      this.key = this.group.getKey()+'/'+this.member.getKey();
    }
    return this.key;
  }

  /**
   * Indicates whether the membership is identifiable.
   *
   * <p>To be identifiable, the membership must have either PageSeeder database ID
   * or it must have both a group and member defined and they both must be identifiable.
   *
   * @return <code>true</code> if ID is specified or both group and member are identifiable;
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean isIdentifiable() {
    if (this.id != null) return true;
    if (this.group == null || this.member == null) return false;
    return this.group.isIdentifiable() && this.member.isIdentifiable();
  }

  /**
   * Returns the private ID only.
   *
   * @return The membership ID only if not <code>null</code>.
   */
  @Override
  public String getIdentifier() {
    return this.id != null? this.id.toString() : null;
  }

  /**
   * @return the group
   */
  public final PSGroup getGroup() {
    return this.group;
  }

  /**
   * @return the member
   */
  public final PSMember getMember() {
    return this.member;
  }

  /**
   * @return the listed
   */
  public final boolean isListed() {
    return this.listed;
  }

  /**
   * @return the notification
   */
  public final PSNotification getNotification() {
    return this.notification;
  }

  /**
   * @return the role
   */
  public final PSRole getRole() {
    return this.role;
  }

  /**
   * @param id the id to set
   */
  public final void setId(Long id) {
    this.id = id;
  }

  /**
   * @param group the group to set
   */
  public final void setGroup(PSGroup group) {
    this.group = group;
    this.key = null;
  }

  /**
   * @param member the member to set
   */
  public final void setMember(PSMember member) {
    this.member = member;
    this.key = null;
  }

  /**
   * @param listed the listed to set
   */
  public final void setListed(boolean listed) {
    this.listed = listed;
  }

  /**
   * @param notification the notification to set
   */
  public final void setNotification(PSNotification notification) {
    this.notification = notification;
  }

  /**
   * @param role the role to set
   */
  public final void setRole(PSRole role) {
    this.role = role;
  }

  public PSDetails getDetails() {
    return this.details;
  }

  public void setDetails(PSDetails details) {
    this.details = details;
  }

  /**
   * Shorthand method to get the detail field on that membership.
   *
   * @param i the 1-based index of the field.
   * @return the corresponding value if the membership has details and this field has a value.
   *
   * @throws IndexOutOfBoundsException If the index is less than 1 or greater than 15.
   */
  public String getField(int i) {
    return this.details != null? this.details.getField(i) : null;
  }

  /**
   * Shorthand method to set the detail field on that membership.
   *
   * <p>If this membership has no details, this method will add a new empty PSDetails object.
   *
   * @param i     the 1-based index of the field.
   * @param value the value to set.
   *
   * @throws IndexOutOfBoundsException If the index is less than 1 or greater than 15.
   */
  public void setField(int i, String value) {
    if (this.details == null) {
      this.details = new PSDetails();
    }
    this.details.setField(i, value);
  }

  @Override
  public EntityValidity checkValid() {
    if (this.details != null) return this.details.checkValid();
    return EntityValidity.OK;
  }

  @Override
  public boolean isValid() {
    return checkValid() == EntityValidity.OK;
  }

  @Override
  public String toString() {
    return "X("+this.id+":"+this.getMember()+","+this.getGroup()+")";
  }
}
