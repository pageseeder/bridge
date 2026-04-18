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

package org.pageseeder.bridge.xml.sax;

import org.junit.Test;
import org.pageseeder.bridge.core.Email;
import org.pageseeder.bridge.core.Member;
import org.pageseeder.bridge.core.MemberStatus;
import org.pageseeder.bridge.core.Username;
import org.pageseeder.bridge.xml.HandlerTests;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.junit.Assert.*;

public class MemberHandlerTest {


  @Test
  public void testPassBasic() throws IOException, SAXException {
    MemberHandler handler = new MemberHandler();
    HandlerTests.parse("member/member-pass-basic.xml", handler);
    Member member = handler.get();
    assertEquals(1L, member.getId());
    assertEquals(new Username("jsmith"), member.getUsername());
    assertEquals(new Email("jsmith@example.org"), member.getEmail());
    assertEquals("John", member.getFirstname());
    assertEquals("Smith", member.getSurname());
    assertSame(MemberStatus.activated, member.getStatus());
    assertTrue(member.isActivated());
  }

  @Test
  public void testPassNoEmail() throws IOException, SAXException {
    MemberHandler handler = new MemberHandler();
    HandlerTests.parse("member/member-pass-noemail.xml", handler);
    Member member = handler.get();
    assertEquals(2L, member.getId());
    assertEquals(new Username("jsmith"), member.getUsername());
    assertEquals(Email.NO_EMAIL, member.getEmail());
    assertEquals("John", member.getFirstname());
    assertEquals("Smith", member.getSurname());
    assertSame(MemberStatus.activated, member.getStatus());
    assertTrue(member.isActivated());
  }

  @Test
  public void testPassNoUsername() throws IOException, SAXException {
    MemberHandler handler = new MemberHandler();
    HandlerTests.parse("member/member-pass-nousername.xml", handler);
    Member member = handler.get();
    assertEquals(3L, member.getId());
    assertEquals(new Username("jsmith@example.org"), member.getUsername());
    assertEquals(new Email("jsmith@example.org"), member.getEmail());
    assertEquals("John", member.getFirstname());
    assertEquals("Smith", member.getSurname());
    assertSame(MemberStatus.activated, member.getStatus());
    assertTrue(member.isActivated());
  }

  @Test
  public void testPassUnactivated() throws IOException, SAXException {
    MemberHandler handler = new MemberHandler();
    HandlerTests.parse("member/member-pass-unactivated.xml", handler);
    Member member = handler.get();
    assertEquals(4L, member.getId());
    assertEquals(new Username("jsmith"), member.getUsername());
    assertEquals(new Email("jsmith@example.org"), member.getEmail());
    assertEquals("John", member.getFirstname());
    assertEquals("Smith", member.getSurname());
    assertSame(MemberStatus.unactivated, member.getStatus());
    assertFalse(member.isActivated());
  }

  @Test
  public void testPassSetPassword() throws IOException, SAXException {
    MemberHandler handler = new MemberHandler();
    HandlerTests.parse("member/member-pass-setpassword.xml", handler);
    Member member = handler.get();
    assertEquals(4L, member.getId());
    assertEquals(new Username("jsmith"), member.getUsername());
    assertEquals(new Email("jsmith@example.org"), member.getEmail());
    assertEquals("John", member.getFirstname());
    assertEquals("Smith", member.getSurname());
    assertSame(MemberStatus.set_password, member.getStatus());
    assertFalse(member.isActivated());
  }

  @Test(expected = SAXException.class)
  public void testFailEmptyId() throws IOException, SAXException {
    HandlerTests.parse("member/member-fail-emptyid.xml", new MemberHandler());
  }

  @Test(expected = SAXException.class)
  public void testFailIllegalId() throws IOException, SAXException {
    HandlerTests.parse("member/member-fail-illegalid.xml", new MemberHandler());
  }

  @Test(expected = SAXException.class)
  public void testFailNoId() throws IOException, SAXException {
    HandlerTests.parse("member/member-fail-noid.xml", new MemberHandler());
  }

  @Test(expected = SAXException.class)
  public void testFailNoFirstname() throws IOException, SAXException {
    HandlerTests.parse("member/member-fail-nofirstname.xml", new MemberHandler());
  }

  @Test(expected = SAXException.class)
  public void testFailNoSurname() throws IOException, SAXException {
    HandlerTests.parse("member/member-fail-nosurname.xml", new MemberHandler());
  }

  @Test(expected = SAXException.class)
  public void testFailNoUsername() throws IOException, SAXException {
    HandlerTests.parse("member/member-fail-nousername.xml", new MemberHandler());
  }

}