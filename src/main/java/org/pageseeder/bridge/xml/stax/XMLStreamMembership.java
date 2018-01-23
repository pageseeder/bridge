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
package org.pageseeder.bridge.xml.stax;

import org.pageseeder.bridge.core.*;
import org.pageseeder.bridge.xml.MissingElementException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.time.OffsetDateTime;

/**
 * This class returns <code>Membership</code> instances from the {@code <membership>} elements.
 *
 * <p>This handler can also be used in the list of memberships where there is a common member or group/project to
 * be added to the individual memberships</p>
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public class XMLStreamMembership extends ElementXMLStreamHandler<Membership> implements XMLStreamHandler<Membership> {

  private Member commonMember;

  private BasicGroup commonGroup;

  public XMLStreamMembership() {
    this(null, null);
  }

  public XMLStreamMembership(Member member) {
    this(member, null);
  }

  public XMLStreamMembership(BasicGroup group) {
    this(null, group);
  }

  private XMLStreamMembership(Member member, BasicGroup group) {
    super("membership");
    this.commonMember = member;
    this.commonGroup = group;
  }

  /**
   * Set the member common to all the memberships.
   *
   * @param member The member common to all the memberships
   */
  public void setCommonMember(Member member) {
    this.commonMember = member;
  }

  /**
   * Set the group common to all the memberships.
   *
   * @param group The member common to all the memberships
   */
  public void setCommonGroup(BasicGroup group) {
    this.commonGroup = group;
  }

  /**
   * Skip to the get "membership" element
   *
   * @param xml The XML Stream to process
   *
   * @return <code>true</code> if the current event is the START_ELEMENT with local name "membership"
   *
   * @throws XMLStreamException Should any error occur while processing the stream
   */
  @Override
  public boolean find(XMLStreamReader xml) throws XMLStreamException {
    while (xml.hasNext() && !isOnElement(xml)) {
      xml.next();
      // If we encounter a membership we automatically extract the member or group common to the membership
      if (xml.isStartElement() && xml.getLocalName().equals("memberships")) {
        extractCommonMemberOrGroup(xml);
      }
    }
    return isOnElement(xml);
  }

  /**
   * Returns a <code>Membership</code> from the current event.
   *
   * <p>Precondition: the current event is START_ELEMENT with name "membership"</p>
   * <p>Postcondition: the current event is END_ELEMENT with name "membership".</p>
   *
   * @param xml The XML stream to process
   *
   * @throws XMLStreamException If thrown by XML stream or if the precondition failed
   */
  @Override
  public Membership get(XMLStreamReader xml) throws XMLStreamException {
    checkOnElement(xml);
    // NB. Note all memberships have an ID (e.g. from subgroups)
    long id = attribute(xml, "id", -1);
    boolean common = "true".equals(attribute(xml, "email-listed", "true"));
    boolean deleted = "true".equals(attribute(xml, "deleted", "false"));
    Role role = Role.forParameter(attribute(xml, "role", "unknown"));
    MembershipStatus status = MembershipStatus.forName(attribute(xml, "status", "unknown"));
    Notification notification = Notification.forName(attribute(xml, "notification"));
    OffsetDateTime created = OffsetDateTime.MIN;
    optionalAttribute(xml, "created");

    Member member = this.commonMember;
    BasicGroup group = this.commonGroup;
    Details details = Details.NO_DETAILS;

    do {
      xml.next();
      if (xml.isStartElement()) {
        String localName = xml.getLocalName();
        if ("member".equals(localName)) {
          member = new XMLStreamMember().get(xml);
        } else if ("group".equals(localName)) {
          group = new XMLStreamGroup().get(xml);
        } else if ("project".equals(localName)) {
          group = new XMLStreamProject().get(xml);
        } else if ("details".equals(localName)) {
          details = new XMLStreamDetails().get(xml);
        }
      }
    } while (!(xml.isEndElement() &&  "membership".equals(xml.getLocalName())));

//      override	list	no	Which attributes from subgroups are overridden (i.e not inherited).
//      subgroups	xs:string	no	Comma-separated list of subgroups

      if (member == null) throw new MissingElementException("Member is required for a membership");
      if (group == null) throw new MissingElementException("Group or project is required for a membership");

    // TODO

    return new Membership(id, member, group, common, notification, role, created, status, deleted, details);
  }

  /**
   * Extracts the member or group/project that is common to all the memberships.
   *
   * <p>Precondition: the current event is START_ELEMENT with name "memberships"</p>
   * <p>Postcondition: the current event is START_ELEMENT with name "membership"
   *  or END_ELEMENT with name "memberships".</p>
   *
   * @param xml The XML stream to process
   *
   * @throws XMLStreamException If thrown by XML stream or if the precondition failed
   */
  private void extractCommonMemberOrGroup(XMLStreamReader xml) throws XMLStreamException {
    xml.require(XMLStreamReader.START_ELEMENT, null, "memberships");
    do {
      xml.next();
      if (xml.isStartElement()) {
        String localName = xml.getLocalName();
        if ("member".equals(localName)) {
          this.commonMember = new XMLStreamMember().get(xml);
        } else if ("group".equals(localName)) {
          this.commonGroup = new XMLStreamGroup().get(xml);
        } else if ("project".equals(localName)) {
          this.commonGroup = new XMLStreamProject().get(xml);
        }
      }
    } while (!(xml.isEndElement() &&  "memberships".equals(xml.getLocalName()))
          && !(xml.isStartElement() &&  "membership".equals(xml.getLocalName())));
  }

}
