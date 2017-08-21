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

import org.pageseeder.bridge.core.Project;
import org.pageseeder.bridge.core.GroupAccess;
import org.pageseeder.bridge.core.GroupID;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamProject extends BasicXMLStreamHandler<Project> implements XMLStreamItem<Project> {

  public XMLStreamProject() {
    super("project");
  }

  @Override
  public Project toItem(XMLStreamReader xml) throws XMLStreamException {
    if (isOnElement(xml)) {
      long id = attribute(xml, "id", -1);
      GroupID name = new GroupID(attribute(xml, "name"));
      String description = attribute(xml, "description");
      String owner = attribute(xml, "owner");
      String title = attribute(xml, "title", "");
      GroupAccess access = GroupAccess.forName(attribute(xml, "access"));
      String relatedURL = attribute(xml, "relatedurl", "");
      boolean common = "true".equals(attribute(xml, "common"));
      skipToEndElement(xml, element());
      return new Project(id, name, title, description, owner, access, common, relatedURL);
    } else throw new IllegalStateException("not a member");
  }

}
