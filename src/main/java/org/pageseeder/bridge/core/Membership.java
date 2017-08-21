/*
 * Copyright 2017 Allette Systems (Australia)
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
package org.pageseeder.bridge.core;

import java.time.OffsetDateTime;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.Requires;

/**
 * A membership to a group or project
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Membership  {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * The PageSeeder id of the member.
   */
  private final long _id;

  /**
   * The member instance
   */
  private final Member _member;

  /**
   * The PageSeeder group the member belongs to
   */
  private final BasicGroup _group;

  /**
   * Indicates whether the email address is visible to others in the group
   */
  private final boolean _listed;

  /**
   * Notification setting of the member for the group.
   */
  private final Notification _notification;

  /**
   * Role of the member in the group.
   */
  private final Role _role;

  /**
   * When the member joined or was invited to the group
   */
  @Requires(minVersion = 57000)
  private final OffsetDateTime _created;

  /**
   * The status of the membership
   */
  private final MembershipStatus _status;

  /**
   * If the member was removed from the group
   */
  private final boolean _deleted;

  /**
   * Which attributes from subgroups are overridden (i.e not inherited).
   */
  //TODO  private String _override;

  /**
   * List of subgroups
   */
  // subgroups	xs:string	no	Comma-separated list of subgroups


  /**
   * Membership detail fields if any
   */
 //TODO private @Nullable PSDetails details = null;

  /**
   * Generated key
   */
  private transient @Nullable String key = null;

  public Membership(long id, Member member, BasicGroup group, boolean listed, Notification notification, Role role, OffsetDateTime created, MembershipStatus status, boolean deleted) {
    this._id = id;
    this._member = member;
    this._group = group;
    this._listed = listed;
    this._notification = notification;
    this._role = role;
    this._created = created;
    this._status = status;
    this._deleted = deleted;
  }

  /**
   * @return the PageSeeder id of the member.
   */
  public long getId() {
    return this._id;
  }

  public @Nullable String getKey() {
    String k = this.key;
    if (k == null) {
      GroupID g = this._group.getName();
      MemberID m = this._member.getUsername();
      k = g+"/"+m;
      this.key = k;
    }
    return k;
  }

  /**
   * @return the group
   */
  public BasicGroup getGroup() {
    return this._group;
  }

  /**
   * @return the member
   */
  public Member getMember() {
    return this._member;
  }

  /**
   * @return the listed
   */
  public boolean isListed() {
    return this._listed;
  }

  /**
   * @return the notification
   */
  public Notification getNotification() {
    return this._notification;
  }

  /**
   * @return the role
   */
  public Role getRole() {
    return this._role;
  }

  /**
   * @return the date the membership was created
   */
  @Requires(minVersion = 57000)
  public OffsetDateTime getCreated() {
    return this._created;
  }


  public MembershipStatus getStatus() {
    return this._status;
  }

  public boolean isDeleted() {
    return this._deleted;
  }

  /**
   * @return Membership detail fields if any
   */
//TODO  public @Nullable PSDetails getDetails() {
//    return this.details;
//  }


  @Override
  public String toString() {
    return "Membership("+this._id+":"+getMember()+","+getGroup()+")";
  }

  public static class Builder {

    private long id = -1;
    private Member member;
    private BasicGroup group;
    private boolean listed;
    private Notification notification = Notification.none;
    private Role role;
    private OffsetDateTime created = OffsetDateTime.MIN;
    private MembershipStatus status = MembershipStatus.unknown;
    private boolean deleted;
    //TODO  private String override;
    // subgroups	xs:string	no	Comma-separated list of subgroups
    //TODO private @Nullable PSDetails details = null;

    public Builder(Member member) {
      this.member = member;
    }

    public Builder(BasicGroup group) {
      this.group = group;
    }

    public Builder(BasicGroup group, boolean listed, Notification defaultNotification, Role defaultRole) {
      this.group = group;
      this.listed = listed;
      this.notification = defaultNotification;
      this.role = defaultRole;
    }

    /**
     * @param id the id to set
     */
    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    /**
     * @param group the group to set
     */
    public Builder group(BasicGroup group) {
      this.group = group;
      return this;
    }

    /**
     * @param member the member to set
     */
    public Builder member(Member member) {
      this.member = member;
      return this;
    }

    /**
     * @param listed the listed to set
     */
    public Builder isListed(boolean listed) {
      this.listed = listed;
      return this;
    }

    /**
     * @param notification the notification to set
     */
    public Builder notification(Notification notification) {
      this.notification = notification;
      return this;
    }

    /**
     * @param role the role to set
     */
    public Builder role(Role role) {
      this.role = role;
      return this;
    }

    /**
     * @param created the date the membership was created.
     */
    public Builder created(OffsetDateTime created) {
      this.created = created;
      return this;
    }

    /**
     * @param details Membership detail fields if any
     */
   // public Builder setDetails(PSDetails details) {
//      this.details = details;
//    }

    /**
     * Shorthand method to set the detail field on that membership.
     *
     * <p>If this membership has no details, this method will add a new empty PSDetails object.
     *
     * @param i     the 1-based index of the field.
     * @param value the value to set.
     *
     * @throws IndexOutOfBoundsException If the index is less than 1 or greater than 15.

    public void setField(int i, @Nullable String value) {
      Details d = this.details;
      if (d == null) {
        d = new PSDetails();
        this.details = d;
      }
      d.setField(i, value);
    }
     */

    public Membership build() {
      return new Membership(this.id, this.member, this.group, this.listed, this.notification, this.role, this.created, this.status, this.deleted);
    }


  }
}
