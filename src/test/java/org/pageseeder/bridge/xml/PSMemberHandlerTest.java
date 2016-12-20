/*
 * Copyright 2015 Allette Systems (Australia)
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
package org.pageseeder.bridge.xml;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMemberStatus;
import org.xml.sax.SAXException;

/**
 * Test the member handler
 */
public class PSMemberHandlerTest {

  @Test
  public void testPassBasic() throws IOException, SAXException {
    PSMemberHandler handler = new PSMemberHandler();
    HandlerTests.parse("member-pass-basic.xml", handler);
    PSMember member = handler.get();
    Assert.assertEquals(1L, member.getId().longValue());
    Assert.assertEquals("jsmith@example.org", member.getEmail());
    Assert.assertEquals("John", member.getFirstname());
    Assert.assertEquals("Smith", member.getSurname());
    Assert.assertEquals("jsmith", member.getUsername());
    Assert.assertSame(PSMemberStatus.activated, member.getStatus());
    Assert.assertTrue(member.isActivated());
  }

  @Test
  public void testPassNoEmail() throws IOException, SAXException {
    PSMemberHandler handler = new PSMemberHandler();
    HandlerTests.parse("member-pass-noemail.xml", handler);
    PSMember member = handler.get();
    Assert.assertEquals(2L, member.getId().longValue());
    Assert.assertEquals("jsmith", member.getUsername());
    Assert.assertEquals("John", member.getFirstname());
    Assert.assertEquals("Smith", member.getSurname());
    Assert.assertEquals("jsmith", member.getUsername());
    Assert.assertNull(member.getEmail());
    Assert.assertSame(PSMemberStatus.activated, member.getStatus());
    Assert.assertTrue(member.isActivated());
  }

  @Test
  public void testPassNoUsername() throws IOException, SAXException {
    PSMemberHandler handler = new PSMemberHandler();
    HandlerTests.parse("member-pass-nousername.xml", handler);
    PSMember member = handler.get();
    Assert.assertEquals(3L, member.getId().longValue());
    Assert.assertEquals("jsmith@example.org", member.getEmail());
    Assert.assertEquals("jsmith@example.org", member.getUsername());
    Assert.assertEquals("John", member.getFirstname());
    Assert.assertEquals("Smith", member.getSurname());
    Assert.assertSame(PSMemberStatus.activated, member.getStatus());
    Assert.assertTrue(member.isActivated());
  }

  @Test
  public void testPassUnactivated() throws IOException, SAXException {
    PSMemberHandler handler = new PSMemberHandler();
    HandlerTests.parse("member-pass-unactivated.xml", handler);
    PSMember member = handler.get();
    Assert.assertEquals(4L, member.getId().longValue());
    Assert.assertEquals("jsmith@example.org", member.getEmail());
    Assert.assertEquals("jsmith", member.getUsername());
    Assert.assertEquals("John", member.getFirstname());
    Assert.assertEquals("Smith", member.getSurname());
    Assert.assertSame(PSMemberStatus.unactivated, member.getStatus());
    Assert.assertFalse(member.isActivated());
  }

  @Test
  public void testPassSetPassword() throws IOException, SAXException {
    PSMemberHandler handler = new PSMemberHandler();
    HandlerTests.parse("member-pass-setpassword.xml", handler);
    PSMember member = handler.get();
    Assert.assertEquals(4L, member.getId().longValue());
    Assert.assertEquals("jsmith@example.org", member.getEmail());
    Assert.assertEquals("jsmith", member.getUsername());
    Assert.assertEquals("John", member.getFirstname());
    Assert.assertEquals("Smith", member.getSurname());
    Assert.assertSame(PSMemberStatus.set_password, member.getStatus());
    Assert.assertFalse(member.isActivated());
  }

  @Test(expected = InvalidAttributeException.class)
  public void testFailEmptyId() throws IOException, SAXException {
    PSMemberHandler handler = new PSMemberHandler();
    HandlerTests.parse("member-fail-emptyid.xml", handler);
  }

  @Test(expected = InvalidAttributeException.class)
  public void testFailIllegalId() throws IOException, SAXException {
    PSMemberHandler handler = new PSMemberHandler();
    HandlerTests.parse("member-fail-illegalid.xml", handler);
  }

  @Test(expected = MissingAttributeException.class)
  public void testFailNoId() throws IOException, SAXException {
    PSMemberHandler handler = new PSMemberHandler();
    HandlerTests.parse("member-fail-noid.xml", handler);
  }

  @Test(expected = MissingAttributeException.class)
  public void testFailNoFirstname() throws IOException, SAXException {
    PSMemberHandler handler = new PSMemberHandler();
    HandlerTests.parse("member-fail-nofirstname.xml", handler);
  }

  @Test(expected = MissingAttributeException.class)
  public void testFailNoSurname() throws IOException, SAXException {
    PSMemberHandler handler = new PSMemberHandler();
    HandlerTests.parse("member-fail-nosurname.xml", handler);
  }

  @Test(expected = MissingAttributeException.class)
  public void testFailNoUsername() throws IOException, SAXException {
    PSMemberHandler handler = new PSMemberHandler();
    HandlerTests.parse("member-fail-nousername.xml", handler);
  }

}
