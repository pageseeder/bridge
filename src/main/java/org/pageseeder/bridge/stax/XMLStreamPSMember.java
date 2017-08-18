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
package org.pageseeder.bridge.stax;

import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMemberStatus;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamPSMember extends BasicXMLStreamHandler<PSMember> implements XMLStreamItem<PSMember> {

  public XMLStreamPSMember() {
    super("member");
  }

  @Override
  public PSMember toItem(XMLStreamReader xml) throws XMLStreamException {
    if (isOnElement(xml)) {
      long id = attribute(xml, "id", -1);
      PSMember member = new PSMember(id);
      String email     = attribute(xml, "email");
      String firstname = attribute(xml, "firstname");
      String surname   = attribute(xml, "surname");
      String username  = attribute(xml, "username");
      String status    = attribute(xml, "status", "unknown");
      member.setEmail(email);
      member.setUsername(username);
      member.setStatus(PSMemberStatus.fromAttribute(status));
      member.setFirstname(firstname);
      member.setSurname(surname);
      skipToEndElement(xml, element());
      return member;
    } else throw new IllegalStateException("not a member");
  }

}
