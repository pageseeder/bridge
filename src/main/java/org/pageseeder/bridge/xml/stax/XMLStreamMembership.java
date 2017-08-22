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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.time.OffsetDateTime;

public class XMLStreamMembership extends BasicXMLStreamHandler<Membership> implements XMLStreamItem<Membership> {

  private final Member _member;

  private final BasicGroup _group;

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
    this._member = member;
    this._group = group;
  }

  @Override
  public Membership toItem(XMLStreamReader xml) throws XMLStreamException {
    if (isOnElement(xml)) {
      // NB. Note all memberships have an ID (e.g. from subgroups)
      long id = attribute(xml, "id", -1);
      boolean common = "true".equals(attribute(xml, "email-listed", "true"));
      boolean deleted = "true".equals(attribute(xml, "deleted", "false"));
      Role role = Role.forParameter(attribute(xml, "role", "unknown"));
      MembershipStatus status = MembershipStatus.forName(attribute(xml, "status", "unknown"));
      Notification notification = Notification.forName(attribute(xml, "notification"));
      OffsetDateTime created = OffsetDateTime.MIN;
      optionalAttribute(xml, "created");

      Member member = this._member;
      BasicGroup group = this._group;
      Details details = Details.NO_DETAILS;

      do {
        xml.next();
        if (xml.isStartElement()) {
          String localName = xml.getLocalName();
          if ("member".equals(localName)) {
            member = new XMLStreamMember().toItem(xml);
          } else if ("group".equals(localName)) {
            group = new XMLStreamGroup().toItem(xml);
          } else if ("project".equals(localName)) {
            group = new XMLStreamProject().toItem(xml);
          } else if ("details".equals(localName)) {
            details = new XMLStreamDetails().toItem(xml);
          }
        }
      } while (!(xml.isEndElement() &&  "membership".equals(xml.getLocalName())));

//      override	list	no	Which attributes from subgroups are overridden (i.e not inherited).
//      subgroups	xs:string	no	Comma-separated list of subgroups



      // TODO

      return new Membership(id, member, group, common, notification, role, created, status, deleted, details);
    } else throw new IllegalStateException("not a member");
  }

}
