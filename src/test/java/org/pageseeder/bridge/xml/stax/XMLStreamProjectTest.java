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
import org.pageseeder.bridge.core.GroupID;
import org.pageseeder.bridge.core.Project;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public final class XMLStreamProjectTest {


  @Test
  public void testGroupPassMinimal() throws IOException, XMLStreamException {
    Project project = XMLStreamTest.parseItem("group/project-pass-minimal.xml", new XMLStreamProject());
    assertNotNull(project);
    // Minimal
    assertEquals(2L, project.getId());
    assertEquals(new GroupID("australia-nsw"), project.getName());
    assertEquals(new GroupID("australia"), project.getParentName());
    assertEquals("nsw", project.getShortName());
    // Core
    assertEquals("", project.getDescription());
    assertEquals("", project.getOwner());
    assertEquals("", project.getTitle());
  }

  @Test
  public void testGroupPassCore() throws IOException, XMLStreamException {
    Project project = XMLStreamTest.parseItem("group/project-pass-core.xml", new XMLStreamProject());
    assertNotNull(project);
    // Minimal
    assertEquals(22L, project.getId());
    assertEquals(new GroupID("australia-nsw"), project.getName());
    assertEquals(new GroupID("australia"), project.getParentName());
    assertEquals("nsw", project.getShortName());
    // Core
    assertEquals("For unit testing", project.getDescription());
    assertEquals("Australia", project.getOwner());
    assertEquals("New South Wales", project.getTitle());
  }

  @Test
  public void testGroupPassExtended() throws IOException, XMLStreamException {
    Project project = XMLStreamTest.parseItem("group/project-pass-extended.xml", new XMLStreamProject());
    // Minimal
    assertNotNull(project);
    assertEquals(222L, project.getId());
    assertEquals(new GroupID("australia-nsw"), project.getName());
    assertEquals(new GroupID("australia"), project.getParentName());
    assertEquals("nsw", project.getShortName());
    // Core
    assertEquals("For unit testing", project.getDescription());
    assertEquals("Australia", project.getOwner());
    assertEquals("New South Wales", project.getTitle());
  }

}
