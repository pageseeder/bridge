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

import org.jspecify.annotations.Nullable;
import org.pageseeder.bridge.Requires;
import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * A membership to a group or project
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Membership implements Serializable, XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 2L;

  /**
   * The PageSeeder id of the member.
   */
  private final long id;

  /**
   * The member instance
   */
  private final Member member;

  /**
   * The PageSeeder group the member belongs to
   */
  private final BasicGroup group;

  /**
   * Indicates whether the email address is visible to others in the group
   */
  private final boolean listed;

  /**
   * Notification setting of the member for the group.
   */
  private final Notification notification;

  /**
   * Role of the member in the group.
   */
  private final Role role;

  /**
   * When the member joined or was invited to the group
   */
  @Requires(minVersion = 57000)
  private final OffsetDateTime created;

  /**
   * The status of the membership
   */
  private final MembershipStatus status;

  /**
   * If the member was removed from the group
   */
  private final boolean deleted;

  /**
   * Membership detail fields if any
   */
  private Details details;

  /**
   * Generated key
   */
  private transient @Nullable String key = null;

  public Membership(long id, Member member, BasicGroup group, boolean listed, Notification notification, Role role, OffsetDateTime created, MembershipStatus status, boolean deleted, Details details) {
    this.id = id;
    this.member = member;
    this.group = group;
    this.listed = listed;
    this.notification = notification;
    this.role = role;
    this.created = created;
    this.status = status;
    this.deleted = deleted;
    this.details = details;
  }

  /**
   * @return the PageSeeder id of the member.
   */
  public long getId() {
    return this.id;
  }

  public @Nullable String getKey() {
    String k = this.key;
    if (k == null) {
      GroupName g = this.group.getName();
      MemberID m = this.member.getUsername();
      k = g+"/"+m;
      this.key = k;
    }
    return k;
  }

  /**
   * @return the group
   */
  public BasicGroup getGroup() {
    return this.group;
  }

  /**
   * @return the member
   */
  public Member getMember() {
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
  public Notification getNotification() {
    return this.notification;
  }

  /**
   * @return the role
   */
  public Role getRole() {
    return this.role;
  }

  /**
   * @return the date the membership was created
   */
  @Requires(minVersion = 57000)
  public OffsetDateTime getCreated() {
    return this.created;
  }


  public MembershipStatus getStatus() {
    return this.status;
  }

  public boolean isDeleted() {
    return this.deleted;
  }

  /**
   * @return Membership detail fields if any
   */
  public Details getDetails() {
    return this.details;
  }

  @Override
  public String toString() {
    return "Membership("+this.id +":"+getMember()+","+getGroup()+")";
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("membership");
    if (this.id > 0)
     {
      xml.attribute("id", Long.toString(this.id)); // id	xs:long	no	The ID of the membership in PageSeeder
    }
    xml.attribute("email-listed", Boolean.toString(this.listed)); // email-listed	xs:boolean	yes	Whether the member discloses its email address
    xml.attribute("status", this.status.toString()); // status	enum	yes	The status of the membership
    xml.attribute("notification", this.notification.toString()); // notification 	enum	yes	Notification preference for the member

    // TODO datetime formatting
    if (this.created != OffsetDateTime.MIN) {
      xml.attribute("created", this.created.toString());
    }
    if (this.role != Role.unknown) {
      xml.attribute("role", this.role.toString());
    }
    if (this.deleted) {
      xml.attribute("deleted", "true");
    }
//    xml.attribute("override", ); // override	list	no	Which attributes from subgroups are overridden (i.e not inherited).
//    xml.attribute("subgroups", );// subgroups	xs:string	no	Comma-separated list of subgroups
    this.member.toXML(xml);
    this.group.toXML(xml);
    if (!this.details.isEmpty()) {
      this.details.toXML(xml);
    }
    xml.closeElement();
  }

  public static class Builder {

    private long id = -1;
    private @Nullable Member member;
    private @Nullable BasicGroup group;
    private boolean listed;
    private Notification notification = Notification.none;
    private Role role;
    private OffsetDateTime created = OffsetDateTime.MIN;
    private MembershipStatus status = MembershipStatus.unknown;
    private boolean deleted;
    private Details details = Details.NO_DETAILS;

    //TODO  private String override;
    // subgroups xs:string no Comma-separated list of subgroups

    public Builder(Member member) {
      this.member = member;
      this.group = null;
    }

    public Builder(BasicGroup group) {
      this.member = null;
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
     * @param details Membership details if any
     */
    public Builder setDetails(Details details) {
      this.details = details;
      return this;
    }

    public Membership build() {
      Member m = this.member;
      BasicGroup g = this.group;
      if (m == null) throw new IllegalStateException("Member is required to build a membership");
      if (g == null) throw new IllegalStateException("Group or Project is required to build a membership");
      return new Membership(this.id, m, g, this.listed, this.notification, this.role, this.created, this.status, this.deleted, this.details);
    }

  }
}
