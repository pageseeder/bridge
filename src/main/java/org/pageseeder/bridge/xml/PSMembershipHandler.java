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
package org.pageseeder.bridge.xml;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.model.PSDetails;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMembership;
import org.pageseeder.bridge.model.PSProject;
import org.xml.sax.Attributes;

/**
 * Handler for PageSeeder memberships.
 *
 * @author Christophe Lauret
 * @version 0.2.2
 * @since 0.1.0
 */
public final class PSMembershipHandler extends PSEntityHandler<PSMembership> {

  /**
   * The current member.
   */
  private @Nullable PSMember member = null;

  /**
   * The current group.
   */
  private @Nullable PSGroup group = null;

  /**
   * Text buffer.
   */
  private StringBuilder buffer = new StringBuilder();

  /**
   * Current details field position.
   */
  private int field = -1;

  /**
   * Create a new handler without a pre-existing member or group.
   *
   * This constructor should be used when only the member or group id is known.
   */
  public PSMembershipHandler() {
  }

  /**
   * Create a new handler from an existing membership.
   *
   * @param membership the membership to reuse.
   */
  public PSMembershipHandler(PSMembership membership) {
    super(membership);
    this.member = membership.getMember();
    this.group = membership.getGroup();
  }

  /**
   * Create a new handler from an existing group when listing the list of groups.
   *
   * @param member the membership to reuse.
   */
  public PSMembershipHandler(PSMember member) {
    this.member = member;
  }

  /**
   * Create a new handler from an existing group when listing the list of members.
   *
   * @param group the group to reuse.
   */
  public PSMembershipHandler(PSGroup group) {
    this.group = group;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) {
    PSMembership membership = this.current;
    if ("membership".equals(localName)) {
      this.current = PSEntityFactory.toMembership(atts, this.current);

    } else if ("member".equals(localName)) {
      PSMember member = PSEntityFactory.toMember(atts, this.member);
      if (membership != null) {
        membership.setMember(member);
      } else {
        this.member = member;
      }

    } else if ("group".equals(localName)) {
      PSGroup group = PSEntityFactory.toGroup(atts, this.group);
      if (membership != null) {
        membership.setGroup(group);
      } else {
        this.group = group;
      }

    } else if ("project".equals(localName)) {
      PSProject project = PSEntityFactory.toProject(atts, this.group);
      if (membership != null) {
        membership.setGroup(project);
      } else {
        this.group = project;
      }

    } else if ("details".equals(localName)) {
      if (membership != null) {
        PSDetails details = new PSDetails();
        membership.setDetails(details);
      }

    } else if ("field".equals(localName)) {
      this.field = PSHandlers.integer(atts.getValue("position"));
    }
  }

  @Override
  public void characters(char[] ch, int offset, int len) {
    if (this.field > 0) {
      this.buffer.append(ch, offset, len);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) {
    PSMembership membership = this.current;
    if ("membership".equals(localName)) {
      // Ensure that the group/member is added
      if (membership != null) {
        PSGroup g = this.group;
        PSMember m = this.member;
        if (membership.getGroup() == null && g != null) {
          membership.setGroup(g);
        }
        if (membership.getMember() == null && m != null) {
          membership.setMember(m);
        }
        this._items.add(membership);
        this.current = null;
      }
    } else if ("field".equals(localName)) {
      if (membership != null) {
        PSDetails details = membership.getDetails();
        if (details != null && this.field > 0) {
          details.setField(this.field, this.buffer.toString());
          this.buffer.setLength(0);
          this.field = -1;
        }
      }
    }
  }

  @Override
  public PSMembership make(Attributes atts, PSMembership entity) {
    return PSEntityFactory.toMembership(atts, entity);
  }

  /**
   * Set the member to use for the memberships.
   *
   * <p>If while parsing, the member is different, it may be replaced or updated.
   *
   * <p>There is generally no reason to invoke this method after parsing.
   *
   * @param member the member to set
   */
  public void setMember(PSMember member) {
    this.member = member;
  }

  /**
   * Set the group to use for the memberships.
   *
   * <p>If while parsing, the group is different, it may be replaced or updated.
   *
   * <p>There is generally no reason to invoke this method after parsing.
   *
   * @param group the group to set
   */
  public void setGroup(PSGroup group) {
    this.group = group;
  }

  /**
   * Returns the group that was before set (before parsing) or that was parsed (after parsing).
   *
   * @return the group
   */
  public @Nullable PSGroup getGroup() {
    return this.group;
  }

  /**
   * Returns the member that was before set (before parsing) or that was parsed (after parsing).
   *
   * @return the member
   */
  public @Nullable PSMember getMember() {
    return this.member;
  }
}
