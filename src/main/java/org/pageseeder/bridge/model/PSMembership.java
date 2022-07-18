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
package org.pageseeder.bridge.model;

import java.util.Date;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSEntity;
import org.pageseeder.bridge.Requires;

/**
 *
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.2.0
 */
public final class PSMembership implements PSEntity  {

  /** As per recommendation */
  private static final long serialVersionUID = 2L;

  /**
   * The PageSeeder id of the member.
   */
  private @Nullable Long id;

  /**
   * The PageSeeder group the member belongs to
   */
  private @Nullable PSGroup group;

  /**
   * The member instance
   */
  private @Nullable PSMember member;

  /**
   * Indicates whether the email address is visible to others in the group
   */
  private boolean listed = true;

  /**
   * Notification setting of the member for the group.
   */
  private @Nullable PSNotification notification;

  /**
   * Role of the member in the group.
   */
  private @Nullable PSRole role;

  /**
   * Membership created date (since 5.7)
   */
  @Requires(minVersion = 57000)
  private @Nullable Date created = null;

  /**
   * Membership detail fields if any
   */
  private @Nullable PSDetails details = null;

  /**
   * Generated key
   */
  private transient @Nullable String key = null;


  /**
   * Membership status.
   */
  private @Nullable PSMembershipStatus status = null;

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
  public @Nullable Long getId() {
    return this.id;
  }

  @Override
  public @Nullable String getKey() {
    String k = this.key;
    if (k == null) {
      PSGroup g = this.group;
      PSMember m = this.member;
      if (g != null && m != null) {
        k = g.getKey()+'/'+m.getKey();
        this.key = k;
      }
    }
    return k;
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
    PSGroup g = this.group;
    PSMember m = this.member;
    return g != null && m != null && g.isIdentifiable() && m.isIdentifiable();
  }

  /**
   * Returns the private ID only.
   *
   * @return The membership ID only if not <code>null</code>.
   */
  @Override
  public @Nullable String getIdentifier() {
    Long id = this.id;
    return id != null? id.toString() : null;
  }

  /**
   * @return the group
   */
  public @Nullable PSGroup getGroup() {
    return this.group;
  }

  /**
   * @return the member
   */
  public @Nullable PSMember getMember() {
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
  public @Nullable PSNotification getNotification() {
    return this.notification;
  }

  /**
   * @return the role
   */
  public @Nullable PSRole getRole() {
    return this.role;
  }

  /**
   * @return the date the membership was created
   */
  @Requires(minVersion = 57000)
  public @Nullable Date getCreated() {
    return this.created;
  }

  /**
   * @return Membership detail fields if any
   */
  public @Nullable PSDetails getDetails() {
    return this.details;
  }

  /**
   * @return the notification
   */
  public @Nullable PSMembershipStatus getStatus() {
    return this.status;
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
  public void setNotification(@Nullable PSNotification notification) {
    this.notification = notification;
  }

  /**
   * @param role the role to set
   */
  public void setRole(@Nullable PSRole role) {
    this.role = role;
  }

  /**
   * @param created the date the membership was created.
   */
  public void setCreated(@Nullable Date created) {
    this.created = created;
  }

  /**
   * @param details Membership detail fields if any
   */
  public void setDetails(PSDetails details) {
    this.details = details;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(@Nullable PSMembershipStatus status) {
    this.status = status;
  }

  /**
   * Shorthand method to get the detail field on that membership.
   *
   * @param i the 1-based index of the field.
   * @return the corresponding value if the membership has details and this field has a value.
   *
   * @throws IndexOutOfBoundsException If the index is less than 1 or greater than 15.
   */
  public @Nullable String getField(int i) {
    PSDetails d = this.details;
    return d != null? d.getField(i) : null;
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
  public void setField(int i, @Nullable String value) {
    PSDetails d = this.details;
    if (d == null) {
      d = new PSDetails();
      this.details = d;
    }
    d.setField(i, value);
  }

  @Override
  public EntityValidity checkValid() {
    PSDetails d = this.details;
    if (d != null) return d.checkValid();
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
