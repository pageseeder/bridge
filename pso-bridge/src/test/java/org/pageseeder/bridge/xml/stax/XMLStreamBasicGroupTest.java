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
import org.pageseeder.bridge.core.BasicGroupTest;
import org.pageseeder.bridge.core.Group;
import org.pageseeder.bridge.core.GroupAccess;
import org.pageseeder.bridge.core.GroupName;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public final class XMLStreamBasicGroupTest {

  @Test
  public void testGroupPassMinimal() throws IOException, XMLStreamException {
    Group expected = new Group(1L, new GroupName("australia-nsw-sydney"), "", "", "");
    assertParseOK(expected, "group/group-pass-minimal.xml");
  }

  @Test
  public void testGroupPassCore() throws IOException, XMLStreamException {
    Group expected = new Group(11L, new GroupName("australia-nsw-sydney"), "Sydney, NSW (Australia)", "For unit testing", "Australia");
    assertParseOK(expected, "group/group-pass-core.xml");
  }

  @Test
  public void testGroupPassFull() throws IOException, XMLStreamException {
    Group expected = new Group(111L, new GroupName("australia-nsw-sydney"), "Sydney, NSW (Australia)", "For unit testing", "Australia", GroupAccess.PUBLIC, true, "https://example.org/hello.html");
    assertParseOK(expected, "group/group-pass-extended.xml");
  }

  private void assertParseOK(Group expected, String path) throws IOException, XMLStreamException {
    Group group = XMLStreamTest.parseItem(path, new XMLStreamGroup());
    BasicGroupTest.assertEquals(expected, group);
    group = XMLStreamTest.parseItem(group, new XMLStreamGroup());
    BasicGroupTest.assertEquals(expected, group);
  }

}
