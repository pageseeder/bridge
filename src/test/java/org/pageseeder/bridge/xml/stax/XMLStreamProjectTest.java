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
import org.pageseeder.bridge.core.GroupAccess;
import org.pageseeder.bridge.core.GroupName;
import org.pageseeder.bridge.core.Project;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public final class XMLStreamProjectTest {


  @Test
  public void testGroupPassMinimal() throws IOException, XMLStreamException {
    Project expected = new Project(2L, new GroupName("australia-nsw"), "", "", "", GroupAccess.MEMBER, false, "");
    assertParseOK(expected,"group/project-pass-minimal.xml");
  }

  @Test
  public void testGroupPassCore() throws IOException, XMLStreamException {
    Project expected = new Project(22L, new GroupName("australia-nsw"), "New South Wales", "For unit testing", "Australia", GroupAccess.MEMBER, false, "");
    assertParseOK(expected,"group/project-pass-core.xml");
  }

  @Test
  public void testGroupPassExtended() throws IOException, XMLStreamException {
    Project expected = new Project(222L, new GroupName("australia-nsw"), "New South Wales", "For unit testing", "Australia", GroupAccess.MEMBER, false, "");
    assertParseOK(expected,"group/project-pass-extended.xml");
  }

  private void assertParseOK(Project expected, String path) throws IOException, XMLStreamException {
    Project project = XMLStreamTest.parseItem(path, new XMLStreamProject());
    BasicGroupTest.assertEquals(expected, project);
    project = XMLStreamTest.parseItem(project, new XMLStreamProject());
    BasicGroupTest.assertEquals(expected, project);
  }

}
