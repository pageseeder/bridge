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
package org.pageseeder.bridge.stax;

import org.pageseeder.bridge.model.PSProject;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamPSProject extends BasicXMLStreamHandler<PSProject> implements XMLStreamItem<PSProject> {

  public XMLStreamPSProject() {
    super("project");
  }

  @Override
  public PSProject toItem(XMLStreamReader xml) throws XMLStreamException {
    if (isOnElement(xml)) {
      long id = attribute(xml, "id", -1);
      PSProject project = new PSProject(id);
      String name    = attribute(xml, "name");
      String description = attribute(xml, "description");
      String owner   = attribute(xml, "owner");
//      String access  = attribute(xml, "access");
//      boolean common = attribute(xml, "common", false);

      project.setName(name);
      project.setDescription(description);
      project.setOwner(owner);
      skipToEndElement(xml, element());
      return project;
    } else throw new IllegalStateException("not a member");
  }

}
