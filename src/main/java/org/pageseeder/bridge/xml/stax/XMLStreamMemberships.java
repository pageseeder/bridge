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

import org.pageseeder.bridge.core.BasicGroup;
import org.pageseeder.bridge.core.Member;
import org.pageseeder.bridge.core.Membership;
import org.pageseeder.bridge.xml.InvalidElementException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;

public class XMLStreamMemberships extends BasicXMLStreamHandler<Membership> implements XMLStreamList<Membership> {

  public XMLStreamMemberships() {
    super("memberships");
  }

  @Override
  public List<Membership> toList(XMLStreamReader xml) throws XMLStreamException {
    if (isOnElement(xml)) {
      BasicGroup group = null;
      Member member = null;
      ArrayList<Membership> memberships = new ArrayList<>();
      XMLStreamMembership factory = null;
      do {
        xml.next();
        if (xml.isStartElement()) {
          String localName = xml.getLocalName();
          if ("membership".equals(localName)) {
            if (factory == null) {
              // Once we get the first membership element, we know which member/group we build for
              if (member != null) {
                factory = new XMLStreamMembership(member);
              } else if (group != null) {
                factory = new XMLStreamMembership(group);
              } else {
                factory = new XMLStreamMembership();
              }
            }
            Membership membership = factory.toItem(xml);
            memberships.add(membership);
          } else if ("member".equals(localName)) {
            member = new XMLStreamMember().toItem(xml);
          } else if ("group".equals(localName)) {
            group = new XMLStreamGroup().toItem(xml);
          } else if ("project".equals(localName)) {
            group = new XMLStreamProject().toItem(xml);
          }
        }
      } while (!(xml.isEndElement() &&  "memberships".equals(xml.getLocalName())));
      return memberships;
    } else throw new InvalidElementException("not a membership list");
  }

}
