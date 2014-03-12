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

import org.pageseeder.bridge.model.PSMember;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public class PSMemberHandler extends DefaultHandler {

  /**
   * Member being processed.
   */
  private PSMember member = null;

  /**
   * The list of members found during last parse.
   */
  private List<PSMember> members = new ArrayList<PSMember>();

  public PSMemberHandler() {
  }

  /**
   * A new handler to fill up the values of an incomplete member.
   *
   * @param member the member to update
   */
  public PSMemberHandler(PSMember member) {
    this.member = member;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("member".equals(localName)) {
      this.member = PSEntityFactory.toMember(atts, this.member);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("member".equals(localName) && this.member != null) {
      this.members.add(this.member);
      this.member = null;
    }
  }

  /**
   * @return the memberships
   */
  public List<PSMember> listMembers() {
    return this.members;
  }

  /**
   * @return the memberships
   */
  public PSMember getMember() {
    int count = this.members.size();
    return count > 0 ? this.members.get(count-1) : null;
  }

}
