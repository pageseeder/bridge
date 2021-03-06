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

import org.pageseeder.bridge.core.Email;
import org.pageseeder.bridge.core.Member;
import org.pageseeder.bridge.core.MemberStatus;
import org.pageseeder.bridge.core.Username;
import org.pageseeder.bridge.xml.MissingAttributeException;

import java.time.OffsetDateTime;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * This class returns <code>Member</code> instances from the {@code <member>} elements.
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public class XMLStreamMember extends ElementXMLStreamHandler<Member> implements XMLStreamHandler<Member> {

  public XMLStreamMember() {
    super("member");
  }

  @Override
  public Member get(XMLStreamReader xml) throws XMLStreamException {
    checkOnElement(xml);
    long id = attribute(xml, "id", -1L);
    if (id == -1L) throw new MissingAttributeException("Missing member ID");
    Username username = new Username(attribute(xml, "username"));
    String firstname = attribute(xml, "firstname");
    String surname   = attribute(xml, "surname");
    Email email = new Email(attribute(xml, "email", ""));
    MemberStatus status = MemberStatus.forAttribute(attribute(xml, "status", "unknown"));
    boolean locked = "true".equals(attribute(xml, "locked", "false"));
    boolean onVacation = "true".equals(attribute(xml, "onvacation", "false"));
    boolean attachments = "true".equals(attribute(xml, "attachments", "false"));
    String lastLogin = optionalAttribute(xml, "lastlogin");
    OffsetDateTime lastLoginDate =  lastLogin != null ? OffsetDateTime.parse(lastLogin) : OffsetDateTime.MIN;
    
    
    skipToEndElement(xml, element());

    Member member = new Member(id, username, email, firstname, surname, status);
    // these flags aren't frequent so it's OK to create new instances when true
    if (locked) member = member.lock();
    if (onVacation) member = member.isOnVacation(true);
    if (attachments) member = member.hasAttachments(true);
    
    //This attribute belongs to the extended member details therefore could be present or no.
    if (!lastLoginDate.equals(OffsetDateTime.MIN)) {
      member = member.lastLogin(lastLoginDate);
    }
    
    return member;
  }

}
