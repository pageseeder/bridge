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

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.bridge.core.Email;
import org.pageseeder.bridge.core.Member;
import org.pageseeder.bridge.core.MemberStatus;
import org.pageseeder.bridge.core.Username;
import org.pageseeder.bridge.xml.InvalidAttributeException;
import org.pageseeder.bridge.xml.MissingAttributeException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public final class XMLStreamMemberTest {

  @Test
  public void testPassBasic() throws IOException, XMLStreamException {
    Member member = XMLStreamTest.parseItem("member/member-pass-basic.xml", new XMLStreamMember());
    Assert.assertEquals(1L, member.getId());
    Assert.assertEquals(new Username("jsmith"), member.getUsername());
    Assert.assertEquals(new Email("jsmith@example.org"), member.getEmail());
    Assert.assertEquals("John", member.getFirstname());
    Assert.assertEquals("Smith", member.getSurname());
    Assert.assertSame(MemberStatus.activated, member.getStatus());
    Assert.assertTrue(member.isActivated());
  }

  @Test
  public void testPassNoEmail() throws IOException, XMLStreamException {
    Member member = XMLStreamTest.parseItem("member/member-pass-noemail.xml", new XMLStreamMember());
    Assert.assertEquals(2L, member.getId());
    Assert.assertEquals(new Username("jsmith"), member.getUsername());
    Assert.assertEquals(Email.NO_EMAIL, member.getEmail());
    Assert.assertEquals("John", member.getFirstname());
    Assert.assertEquals("Smith", member.getSurname());
    Assert.assertSame(MemberStatus.activated, member.getStatus());
    Assert.assertTrue(member.isActivated());
  }

  @Test
  public void testPassNoUsername() throws IOException, XMLStreamException {
    Member member = XMLStreamTest.parseItem("member/member-pass-nousername.xml", new XMLStreamMember());
    Assert.assertEquals(3L, member.getId());
    Assert.assertEquals(new Username("jsmith@example.org"), member.getUsername());
    Assert.assertEquals(new Email("jsmith@example.org"), member.getEmail());
    Assert.assertEquals("John", member.getFirstname());
    Assert.assertEquals("Smith", member.getSurname());
    Assert.assertSame(MemberStatus.activated, member.getStatus());
    Assert.assertTrue(member.isActivated());
  }

  @Test
  public void testPassUnactivated() throws IOException, XMLStreamException {
    Member member = XMLStreamTest.parseItem("member/member-pass-unactivated.xml", new XMLStreamMember());
    Assert.assertEquals(4L, member.getId());
    Assert.assertEquals(new Username("jsmith"), member.getUsername());
    Assert.assertEquals(new Email("jsmith@example.org"), member.getEmail());
    Assert.assertEquals("John", member.getFirstname());
    Assert.assertEquals("Smith", member.getSurname());
    Assert.assertSame(MemberStatus.unactivated, member.getStatus());
    Assert.assertFalse(member.isActivated());
  }

  @Test
  public void testPassSetPassword() throws IOException, XMLStreamException {
    Member member = XMLStreamTest.parseItem("member/member-pass-setpassword.xml", new XMLStreamMember());
    Assert.assertEquals(4L, member.getId());
    Assert.assertEquals(new Username("jsmith"), member.getUsername());
    Assert.assertEquals(new Email("jsmith@example.org"), member.getEmail());
    Assert.assertEquals("John", member.getFirstname());
    Assert.assertEquals("Smith", member.getSurname());
    Assert.assertSame(MemberStatus.set_password, member.getStatus());
    Assert.assertFalse(member.isActivated());
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

}
