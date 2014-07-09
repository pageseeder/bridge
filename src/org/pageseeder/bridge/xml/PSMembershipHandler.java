/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.xml;

import org.pageseeder.bridge.model.PSDetails;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMembership;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

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
  private PSMember member = null;

  /**
   * The current group.
   */
  private PSGroup group = null;

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
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("membership".equals(localName)) {
      this.current = PSEntityFactory.toMembership(atts, this.current);

    } else if ("member".equals(localName)) {
      PSMember member = PSEntityFactory.toMember(atts, this.member);
      if (this.current != null)
        this.current.setMember(member);
      else
        this.member = member;

    } else if ("group".equals(localName)) {
      PSGroup group = PSEntityFactory.toGroup(atts, this.group);
      if (this.current != null)
        this.current.setGroup(group);
      else
        this.group = group;

    } else if ("details".equals(localName)) {
      PSDetails details = new PSDetails();
      this.current.setDetails(details);

    } else if ("field".equals(localName)) {
      this.field = PSHandlers.integer(atts.getValue("position"));
    }
  }

  @Override
  public void characters(char[] ch, int offset, int len) throws SAXException {
    if (this.field > 0) {
      this.buffer.append(ch, offset, len);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("membership".equals(localName)) {
      // Ensure that the group/member is added
      if (this.current.getGroup() == null)
        this.current.setGroup(this.group);
      if (this.current.getMember() == null)
        this.current.setMember(this.member);
      this._items.add(this.current);
      this.current = null;
    } else if ("field".equals(localName)) {
      PSDetails details = this.current.getDetails();
      if (details != null && this.field > 0) {
        details.setField(this.field, this.buffer.toString());
        this.buffer.setLength(0);
        this.field = -1;
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
  public PSGroup getGroup() {
    return this.group;
  }

  /**
   * Returns the member that was before set (before parsing) or that was parsed (after parsing).
   *
   * @return the member
   */
  public PSMember getMember() {
    return this.member;
  }
}
