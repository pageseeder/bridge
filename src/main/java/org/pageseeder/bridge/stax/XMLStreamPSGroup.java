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

import org.pageseeder.bridge.model.PSGroup;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamPSGroup extends BasicXMLStreamHandler<PSGroup> implements XMLStreamItem<PSGroup> {

  public XMLStreamPSGroup() {
    super("group");
  }

  @Override
  public PSGroup toItem(XMLStreamReader xml) throws XMLStreamException {
    if (isOnElement(xml)) {
      long id = attribute(xml, "id", -1);
      PSGroup group = new PSGroup(id);
      String name    = attribute(xml, "name");
      String description = attribute(xml, "description");
      String owner   = attribute(xml, "owner");
//      String access  = attribute(xml, "access");
//      boolean common = attribute(xml, "common", false);

      group.setName(name);
      group.setDescription(description);
      group.setOwner(owner);
      skipToEndElement(xml, element());
      return group;
    } else throw new IllegalStateException("not a member");
  }

}
