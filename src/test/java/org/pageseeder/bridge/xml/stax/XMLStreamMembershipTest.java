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
import org.pageseeder.bridge.core.*;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.Assert.*;

public final class XMLStreamMembershipTest {

  @Test
  public void testMembershipGroup() throws IOException, XMLStreamException {
    Membership membership = XMLStreamTest.parseItem("membership/membership-group.xml", new XMLStreamMembership());
    assertNotNull(membership);
    // <membership id="101" email-listed="true" notification="immediate" status="normal" role="manager">
    assertEquals(102L, membership.getId());
    assertTrue(membership.isListed());
    assertEquals(Role.manager, membership.getRole());
    assertEquals(MembershipStatus.normal, membership.getStatus());
    assertEquals(Notification.immediate, membership.getNotification());
    assertEquals(OffsetDateTime.parse("2018-01-01T01:01:01+10:00"), membership.getCreated());
    assertEquals(Details.NO_DETAILS, membership.getDetails());
    assertTrue(membership.getGroup() instanceof Group);
    assertNotNull(membership.getMember());
    assertEquals(203L, membership.getGroup().getId());
    assertEquals(123L, membership.getMember().getId());
  }

  @Test
  public void testMembershipGroupWithDetails() throws IOException, XMLStreamException {
    Membership membership = XMLStreamTest.parseItem("membership/membership-group-details.xml", new XMLStreamMembership());
    assertNotNull(membership);

    assertEquals(102L, membership.getId());
    assertTrue(membership.isListed());
    assertEquals(Role.manager, membership.getRole());
    assertEquals(MembershipStatus.normal, membership.getStatus());
    assertEquals(Notification.immediate, membership.getNotification());
    assertEquals(OffsetDateTime.MIN, membership.getCreated());
    assertFalse(membership.getDetails().isEmpty());
    assertTrue(membership.getGroup() instanceof Group);
    assertNotNull(membership.getMember());
    assertEquals(203L, membership.getGroup().getId());
    assertEquals(123L, membership.getMember().getId());
  }

  @Test
  public void testMembershipProject() throws IOException, XMLStreamException {
    Membership membership = XMLStreamTest.parseItem("membership/membership-project.xml", new XMLStreamMembership());
    assertNotNull(membership);

    assertEquals(101L, membership.getId());
    assertTrue(membership.isListed());
    assertEquals(Role.manager, membership.getRole());
    assertEquals(MembershipStatus.normal, membership.getStatus());
    assertEquals(Notification.immediate, membership.getNotification());
    assertEquals(Details.NO_DETAILS, membership.getDetails());
    assertEquals(123L, membership.getMember().getId());
    assertNotNull(membership.getMember());
    assertTrue(membership.getGroup() instanceof Project);
    assertEquals(201L, membership.getGroup().getId());

  }

  @Test
  public void testMemberships() throws IOException, XMLStreamException {
    List<Membership> memberships = XMLStreamTest.parseList("membership/memberships-formember.xml", new XMLStreamMembership());
//    Assert.assertEquals(1L, membership.getId());
    Assert.assertEquals(3, memberships.size());
  }

}
