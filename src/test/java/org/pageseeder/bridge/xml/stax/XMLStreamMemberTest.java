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

import org.junit.Test;
import org.pageseeder.bridge.core.*;
import org.pageseeder.bridge.xml.InvalidAttributeException;
import org.pageseeder.bridge.xml.MissingAttributeException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.time.OffsetDateTime;

public final class XMLStreamMemberTest {

  @Test
  public void testPassBasic() throws IOException, XMLStreamException {
    Member expected = new Member(1L, new Username("jsmith"), new Email("jsmith@example.org"), "John", "Smith", MemberStatus.activated);
    assertParseOK(expected, "member/member-pass-basic.xml");
  }

  @Test
  public void testPassNoEmail() throws IOException, XMLStreamException {
    Member expected = new Member(2L, new Username("jsmith"), Email.NO_EMAIL, "John", "Smith", MemberStatus.activated);
    assertParseOK(expected, "member/member-pass-noemail.xml");
  }

  @Test
  public void testPassNoUsername() throws IOException, XMLStreamException {
    Member expected = new Member(3L, new Username("jsmith@example.org"), new Email("jsmith@example.org"), "John", "Smith", MemberStatus.activated);
    assertParseOK(expected, "member/member-pass-nousername.xml");
  }

  @Test
  public void testPassUnactivated() throws IOException, XMLStreamException {
    Member expected = new Member(4L, new Username("jsmith"), new Email("jsmith@example.org"), "John", "Smith", MemberStatus.unactivated);
    assertParseOK(expected, "member/member-pass-unactivated.xml");
  }

  @Test
  public void testPassSetPassword() throws IOException, XMLStreamException {
    Member expected = new Member(4L, new Username("jsmith"), new Email("jsmith@example.org"), "John", "Smith", MemberStatus.set_password);
    assertParseOK(expected, "member/member-pass-setpassword.xml");
  }

  @Test
  public void testPassLastLogin() throws IOException, XMLStreamException {
    Member expected = new Member(1L, new Username("jsmith"), new Email("jsmith@example.org"), "John", "Smith", MemberStatus.activated);
    expected = expected.lastLogin(OffsetDateTime.parse("2018-12-10T12:24:18+11:00"));
    assertParseOK(expected, "member/member-pass-lastlogin.xml");
  }
  
  @Test(expected = InvalidAttributeException.class)
  public void testFailEmptyId() throws IOException, XMLStreamException {
    XMLStreamTest.parseItem("member/member-fail-emptyid.xml", new XMLStreamMember());
  }

  @Test(expected = InvalidAttributeException.class)
  public void testFailIllegalId() throws IOException, XMLStreamException {
    XMLStreamTest.parseItem("member/member-fail-illegalid.xml", new XMLStreamMember());
  }

  @Test(expected = MissingAttributeException.class)
  public void testFailNoId() throws IOException, XMLStreamException {
    XMLStreamTest.parseItem("member/member-fail-noid.xml", new XMLStreamMember());
  }

  @Test(expected = MissingAttributeException.class)
  public void testFailNoFirstname() throws IOException, XMLStreamException {
    XMLStreamTest.parseItem("member/member-fail-nofirstname.xml", new XMLStreamMember());
  }

  @Test(expected = MissingAttributeException.class)
  public void testFailNoSurname() throws IOException, XMLStreamException {
    XMLStreamTest.parseItem("member/member-fail-nosurname.xml", new XMLStreamMember());
  }

  @Test(expected = MissingAttributeException.class)
  public void testFailNoUsername() throws IOException, XMLStreamException {
    XMLStreamTest.parseItem("member/member-fail-nousername.xml", new XMLStreamMember());
  }

  private void assertParseOK(Member expected, String path) throws IOException, XMLStreamException {
    Member member = XMLStreamTest.parseItem(path, new XMLStreamMember());
    MemberTest.assertEquals(expected, member);
    member = XMLStreamTest.parseItem(member, new XMLStreamMember());
    MemberTest.assertEquals(expected, member);
  }

}
