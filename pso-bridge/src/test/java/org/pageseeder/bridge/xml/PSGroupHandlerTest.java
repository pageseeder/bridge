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
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSNotification;
import org.pageseeder.bridge.model.PSProject;
import org.pageseeder.bridge.model.PSRole;
import org.xml.sax.SAXException;

/**
 * Test the group handler
 */
public final class PSGroupHandlerTest {

  @Test
  public void testGroupPassMinimal() throws IOException, SAXException {
    PSGroupHandler handler = new PSGroupHandler();
    HandlerTests.parse("group/group-pass-minimal.xml", handler);
    PSGroup group = handler.get();
    // Minimal
    Assert.assertEquals(1L, group.getId().longValue());
    Assert.assertEquals("australia-nsw-sydney", group.getName());
    Assert.assertEquals("sydney", group.getShortName());
    Assert.assertEquals("australia-nsw", group.getParentName());
    // Core
    Assert.assertNull(group.getDescription());
    Assert.assertNull(group.getOwner());
    Assert.assertNull(group.getTitle());
    // Extended
    Assert.assertNull(group.getOwner());
    Assert.assertNull(group.getTemplate());
    Assert.assertNull(group.getDetailsType());
    Assert.assertNull(group.getDefaultNotification());
    Assert.assertNull(group.getDefaultRole());
  }

  @Test
  public void testProjectPassMinimal() throws IOException, SAXException {
    PSGroupHandler handler = new PSGroupHandler();
    HandlerTests.parse("group/project-pass-minimal.xml", handler);
    PSGroup group = handler.get();
    Assert.assertTrue(group instanceof PSProject);
    PSProject project = (PSProject)group;
    // Minimal
    Assert.assertEquals(2L, project.getId().longValue());
    Assert.assertEquals("australia-nsw", project.getName());
    Assert.assertEquals("nsw", project.getShortName());
    Assert.assertEquals("australia", project.getParentName());
    // Core
    Assert.assertNull(project.getDescription());
    Assert.assertNull(project.getOwner());
    Assert.assertNull(project.getTitle());
    // Extended
    Assert.assertNull(project.getTemplate());
    Assert.assertNull(project.getDetailsType());
    Assert.assertNull(project.getDefaultNotification());
    Assert.assertNull(project.getDefaultRole());
  }

  @Test
  public void testGroupPassCore() throws IOException, SAXException {
    PSGroupHandler handler = new PSGroupHandler();
    HandlerTests.parse("group/group-pass-core.xml", handler);
    PSGroup group = handler.get();
    // Minimal
    Assert.assertEquals(11L, group.getId().longValue());
    Assert.assertEquals("australia-nsw-sydney", group.getName());
    Assert.assertEquals("sydney", group.getShortName());
    Assert.assertEquals("australia-nsw", group.getParentName());
    // Core
    Assert.assertEquals("For unit testing", group.getDescription());
    Assert.assertEquals("Australia", group.getOwner());
    Assert.assertEquals("Sydney, NSW (Australia)", group.getTitle());
    // Extended
    Assert.assertNull(group.getTemplate());
    Assert.assertNull(group.getDetailsType());
    Assert.assertNull(group.getDefaultNotification());
    Assert.assertNull(group.getDefaultRole());
  }

  @Test
  public void testProjectPassCore() throws IOException, SAXException {
    PSGroupHandler handler = new PSGroupHandler();
    HandlerTests.parse("group/project-pass-core.xml", handler);
    PSGroup group = handler.get();
    Assert.assertTrue(group instanceof PSProject);
    PSProject project = (PSProject)group;
    // Minimal
    Assert.assertEquals(22L, project.getId().longValue());
    Assert.assertEquals("australia-nsw", project.getName());
    Assert.assertEquals("nsw", project.getShortName());
    Assert.assertEquals("australia", project.getParentName());
    // Core
    Assert.assertEquals("For unit testing", project.getDescription());
    Assert.assertEquals("Australia", project.getOwner());
    Assert.assertEquals("New South Wales", project.getTitle());
    // Extended
    Assert.assertNull(project.getTemplate());
    Assert.assertNull(project.getDetailsType());
    Assert.assertNull(project.getDefaultNotification());
    Assert.assertNull(project.getDefaultRole());
  }


  @Test
  public void testGroupPassExtended() throws IOException, SAXException {
    PSGroupHandler handler = new PSGroupHandler();
    HandlerTests.parse("group/group-pass-extended.xml", handler);
    PSGroup group = handler.get();
    // Minimal
    Assert.assertEquals(111L, group.getId().longValue());
    Assert.assertEquals("australia-nsw-sydney", group.getName());
    Assert.assertEquals("sydney", group.getShortName());
    Assert.assertEquals("australia-nsw", group.getParentName());
    // Core
    Assert.assertEquals("For unit testing", group.getDescription());
    Assert.assertEquals("Australia", group.getOwner());
    Assert.assertEquals("Sydney, NSW (Australia)", group.getTitle());
    // Extended
    Assert.assertEquals("australia-nsw", group.getTemplate());
    Assert.assertEquals("city", group.getDetailsType());
    Assert.assertEquals(PSNotification.immediate, group.getDefaultNotification());
    Assert.assertEquals(PSRole.reviewer, group.getDefaultRole());
  }

  @Test
  public void testProjectPassExtended() throws IOException, SAXException {
    PSGroupHandler handler = new PSGroupHandler();
    HandlerTests.parse("group/project-pass-extended.xml", handler);
    PSGroup group = handler.get();
    Assert.assertTrue(group instanceof PSProject);
    PSProject project = (PSProject)group;
    // Minimal
    Assert.assertEquals(222L, project.getId().longValue());
    Assert.assertEquals("australia-nsw", project.getName());
    Assert.assertEquals("nsw", project.getShortName());
    Assert.assertEquals("australia", project.getParentName());
    // Core
    Assert.assertEquals("For unit testing", project.getDescription());
    Assert.assertEquals("Australia", project.getOwner());
    Assert.assertEquals("New South Wales", project.getTitle());
    // Extended
    Assert.assertEquals("australia-nsw", project.getTemplate());
    Assert.assertEquals("state", project.getDetailsType());
    Assert.assertEquals(PSNotification.immediate, project.getDefaultNotification());
    Assert.assertEquals(PSRole.reviewer, project.getDefaultRole());
  }

}
