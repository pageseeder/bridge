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
import org.pageseeder.bridge.core.Group;
import org.pageseeder.bridge.core.GroupID;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public final class XMLStreamGroupTest {

  @Test
  public void testGroupPassMinimal() throws IOException, XMLStreamException {
    Group group = XMLStreamTest.parseItem("group/group-pass-minimal.xml", new XMLStreamGroup());
    // Minimal
    Assert.assertEquals(1L, group.getId());
    Assert.assertEquals(new GroupID("australia-nsw-sydney"), group.getName());
    Assert.assertEquals(new GroupID("australia-nsw"), group.getParentName());
    Assert.assertEquals("sydney", group.getShortName());
    // Core
    Assert.assertEquals("", group.getDescription());
    Assert.assertEquals("", group.getOwner());
    Assert.assertEquals("", group.getTitle());
  }

  @Test
  public void testGroupPassCore() throws IOException, XMLStreamException {
    Group group = XMLStreamTest.parseItem("group/group-pass-core.xml", new XMLStreamGroup());
    // Minimal
    Assert.assertEquals(11L, group.getId());
    Assert.assertEquals(new GroupID("australia-nsw-sydney"), group.getName());
    Assert.assertEquals(new GroupID("australia-nsw"), group.getParentName());
    Assert.assertEquals("sydney", group.getShortName());
    // Core
    Assert.assertEquals("For unit testing", group.getDescription());
    Assert.assertEquals("Australia", group.getOwner());
    Assert.assertEquals("Sydney, NSW (Australia)", group.getTitle());
  }

  @Test
  public void testGroupPassExtended() throws IOException, XMLStreamException {
    Group group = XMLStreamTest.parseItem("group/group-pass-extended.xml", new XMLStreamGroup());
    // Minimal
    Assert.assertEquals(111L, group.getId());
    Assert.assertEquals(new GroupID("australia-nsw-sydney"), group.getName());
    Assert.assertEquals(new GroupID("australia-nsw"), group.getParentName());
    Assert.assertEquals("sydney", group.getShortName());
    // Core
    Assert.assertEquals("For unit testing", group.getDescription());
    Assert.assertEquals("Australia", group.getOwner());
    Assert.assertEquals("Sydney, NSW (Australia)", group.getTitle());
  }


}
