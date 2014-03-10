/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.xml;

import java.util.ArrayList;
import java.util.List;

import org.pageseeder.bridge.core.PSDetails;
import org.pageseeder.bridge.core.PSGroup;
import org.pageseeder.bridge.core.PSMember;
import org.pageseeder.bridge.core.PSMembership;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public class PSMembershipHandler extends DefaultHandler {

  private PSMembership membership = null;

  private PSMember member = null;

  private PSGroup group = null;

  private List<PSMembership> memberships = new ArrayList<PSMembership>();

  private StringBuilder buffer = new StringBuilder();

  /**
   * Current details field position.
   */
  private int field = -1;

  public PSMembershipHandler() {
  }

  public PSMembershipHandler(PSMembership membership) {
    this.membership = membership;
    this.member = membership.getMember();
    this.group = membership.getGroup();
  }

  public PSMembershipHandler(PSMember member) {
    this.member = member;
  }

  public PSMembershipHandler(PSGroup group) {
    this.group = group;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("membership".equals(localName)) {
      this.membership = PSEntityFactory.toMembership(atts, this.membership);

    } else if ("member".equals(localName)) {
      PSMember member = PSEntityFactory.toMember(atts, this.member);
      this.membership.setMember(member);

    } else if ("group".equals(localName)) {
      PSGroup group = PSEntityFactory.toGroup(atts, this.group);
      this.membership.setGroup(group);

    } else if ("details".equals(localName)) {
      PSDetails details = new PSDetails();
      this.membership.setDetails(details);

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
      if (this.membership.getGroup() == null)
        this.membership.setGroup(this.group);
      if (this.membership.getMember() == null)
        this.membership.setMember(this.member);
      this.memberships.add(this.membership);
      this.membership = null;
    } else if ("field".equals(localName)) {
      PSDetails details = this.membership.getDetails();
      if (details != null && this.field > 0) {
        details.setField(this.field, this.buffer.toString());
        this.buffer.setLength(0);
        this.field = -1;
      }
    }
  }

  /**
   * @return the memberships
   */
  public List<PSMembership> listMemberships() {
    return this.memberships;
  }

  /**
   * @return the memberships
   */
  public PSMembership getMembership() {
    int count = this.memberships.size();
    return count > 0 ? this.memberships.get(count-1) : null;
  }

}
