/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

import java.util.Date;

import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSEntity;
import org.pageseeder.bridge.Requires;

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

  /**
   * The PageSeeder id of the member.
   */
  private Long id;

  /**
   * The PageSeeder group the member belongs to
   */
  private PSGroup group;

  /**
   * The member instance
   */
  private PSMember member;

  /**
   * Indicates whether the email address is visible to others in the group
   */
  private boolean listed = true;

  /**
   * Notification setting of the member for the group.
   */
  private PSNotification notification;

  /**
   * Role of the member in the group.
   */
  private PSRole role;

  /**
   * Membership created date (since 5.7)
   */
  @Requires(minVersion = 57000)
  private Date created = null;

  /**
   * Membership detail fields if any
   */
  private PSDetails details = null;

  /**
   * Generated key
   */
  private transient String key = null;

  /**
   * Create a new membership without setting the group of member.
   */
  public PSMembership() {
  }

  /**
   * Create a new membership.
   *
   * @param group  the PageSeeder group the member belongs to
   * @param member the member instance
   */
  public PSMembership(PSGroup group, PSMember member) {
    this.group = group;
    this.member = member;
  }

  /**
   * @return the PageSeeder id of the member.
   */
  @Override
  public Long getId() {
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
  public PSGroup getGroup() {
    return this.group;
  }

  /**
   * @return the member
   */
  public PSMember getMember() {
    return this.member;
  }

  /**
   * @return the listed
   */
  public boolean isListed() {
    return this.listed;
  }

  /**
   * @return the notification
   */
  public PSNotification getNotification() {
    return this.notification;
  }

  /**
   * @return the role
   */
  public PSRole getRole() {
    return this.role;
  }

  /**
   * @return the date the membership was created
   */
  @Requires(minVersion = 57000)
  public Date getCreated() {
    return this.created;
  }

  /**
   * @return Membership detail fields if any
   */
  public PSDetails getDetails() {
    return this.details;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @param group the group to set
   */
  public void setGroup(PSGroup group) {
    this.group = group;
    this.key = null;
  }

  /**
   * @param member the member to set
   */
  public void setMember(PSMember member) {
    this.member = member;
    this.key = null;
  }

  /**
   * @param listed the listed to set
   */
  public void setListed(boolean listed) {
    this.listed = listed;
  }

  /**
   * @param notification the notification to set
   */
  public void setNotification(PSNotification notification) {
    this.notification = notification;
  }

  /**
   * @param role the role to set
   */
  public void setRole(PSRole role) {
    this.role = role;
  }

  /**
   * @param created the date the membership was created.
   */
  public void setCreated(Date created) {
    this.created = created;
  }

  /**
   * @param details Membership detail fields if any
   */
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
    return "X("+this.id+":"+getMember()+","+getGroup()+")";
  }
}
