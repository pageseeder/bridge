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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.bridge.model.*;

/**
 * Test the membership handler
 */
public final class PSMembershipHandlerTest {

  @Test
  public void testParseOK() throws Exception {
    PSMembershipHandler handler = new PSMembershipHandler();
    HandlerTests.parse("membership/memberships1.xml", handler);
    // Check that member is retrieved
    PSMember member = handler.getMember();
    Assert.assertEquals("jsmith@example.org", member.getEmail());
    Assert.assertEquals("John", member.getFirstname());
    Assert.assertEquals("Smith", member.getSurname());
    Assert.assertEquals(123L, member.getId().longValue());
    Assert.assertEquals("jsmith", member.getUsername());
    // check groups and projects
    List<PSMembership> memberships = handler.list();
    Assert.assertTrue(memberships.size() > 0);
    PSMembership membership1 = memberships.get(0);
    Assert.assertEquals(PSNotification.immediate , membership1.getNotification());
    Assert.assertEquals(PSRole.manager , membership1.getRole());
    Assert.assertNotNull(membership1.getGroup());
    Assert.assertEquals("acme" , membership1.getGroup().getName());
    Assert.assertEquals(PSMembershipStatus.normal, membership1.getStatus());

    PSMembership membership2 = memberships.get(1);
    Assert.assertEquals(PSNotification.essential , membership2.getNotification());
    Assert.assertEquals(PSRole.contributor , membership2.getRole());
    Assert.assertNotNull(membership2.getGroup());
    Assert.assertEquals("acme-info" , membership2.getGroup().getName());
    Assert.assertEquals(PSMembershipStatus.self_invited, membership2.getStatus());

    PSMembership membership3 = memberships.get(2);
    Assert.assertEquals(null, membership3.getStatus());
  }

}
