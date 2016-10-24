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

import java.io.File;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMembership;

/**
 * @deprecated SubscriptionForm no longer supported from PageSeeder 5.9
 */
@Deprecated
public final class SubscriptionFormHandlerTest {

  @Test
  public void testParse1() throws Exception {
    File f = new File("src/test/resource/data/org.pageseeder.bridge.xml/subscription-form1.xml");
    SubscriptionFormHandler handler = new SubscriptionFormHandler();
    parse(f, handler);

    // Check member
    PSMember actualMember = handler.getMember();
    Assert.assertNotNull(actualMember);
    PSMember expectedMember = new PSMember(1L);
    expectedMember.setEmail("jsmith@example.org");
    expectedMember.setFirstname("John");
    expectedMember.setSurname("Smith");
    expectedMember.setUsername("jsmith");
    assertEquals(expectedMember, actualMember);

    List<PSMembership> memberships = handler.getMemberships();
    Assert.assertNotNull(memberships);
    Assert.assertTrue(memberships.size() > 0);

    PSGroup actualGroup = memberships.get(0).getGroup();
    Assert.assertNotNull(actualGroup);
    PSGroup expectedGroup = new PSGroup(6L);
    expectedGroup.setName("role-administrator");
    assertEquals(expectedGroup, actualGroup);
  }

  @Test
  public void testParse2() throws Exception {
    File f = new File("src/test/resource/data/org.pageseeder.bridge.xml/subscription-form2.xml");
    SubscriptionFormHandler handler = new SubscriptionFormHandler();
    parse(f, handler);

    // Check member
    PSMember actualMember = handler.getMember();
    Assert.assertNotNull(actualMember);
    PSMember expectedMember = new PSMember(28L);
    expectedMember.setEmail("boris.test@example.org");
    expectedMember.setFirstname("Boris");
    expectedMember.setSurname("Test");
    expectedMember.setUsername("UC441127E7E40523A02275E2CC1FC99CC");
    assertEquals(expectedMember, actualMember);

    List<PSMembership> memberships = handler.getMemberships();
    Assert.assertNotNull(memberships);
    Assert.assertTrue(memberships.size() > 0);

    PSGroup actualGroup = memberships.get(0).getGroup();
    Assert.assertNotNull(actualGroup);
    PSGroup expectedGroup = new PSGroup(7L);
    expectedGroup.setName("role-individual");
    assertEquals(expectedGroup, actualGroup);
  }

  // private helpers
  // ----------------------------------------------------------------------------------------------

  public static void assertEquals(PSMember exp, PSMember act) {
    Assert.assertEquals(exp.getId(), act.getId());
    Assert.assertEquals(exp.getFirstname(), act.getFirstname());
    Assert.assertEquals(exp.getSurname(), act.getSurname());
    Assert.assertEquals(exp.getUsername(), act.getUsername());
    Assert.assertEquals(exp.getEmail(), act.getEmail());
  }

  public static void assertEquals(PSGroup exp, PSGroup act) {
    Assert.assertEquals(exp.getId(),   act.getId());
    Assert.assertEquals(exp.getName(), act.getName());
  }

  private static final void parse(File f, SubscriptionFormHandler handler) throws Exception {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);
    SAXParser parser = factory.newSAXParser();
    parser.parse(f, handler);
  }

}
