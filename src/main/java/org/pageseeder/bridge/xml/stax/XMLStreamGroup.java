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

import org.pageseeder.bridge.core.Group;
import org.pageseeder.bridge.core.GroupAccess;
import org.pageseeder.bridge.core.GroupName;
import org.pageseeder.bridge.xml.MissingAttributeException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * This class returns <code>Group</code> instances from the {@code <group>} element.
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public class XMLStreamGroup extends ElementXMLStreamHandler<Group> implements XMLStreamHandler<Group> {

  public XMLStreamGroup() {
    super("group");
  }

  @Override
  public Group get(XMLStreamReader xml) throws XMLStreamException {
    checkOnElement(xml);
    long id = attribute(xml, "id", -1);
    if (id == -1L) throw new MissingAttributeException("Missing group ID");
    GroupName name = new GroupName(attribute(xml, "name"));
    String description = attribute(xml, "description", "");
    String owner = attribute(xml, "owner","");
    String title = attribute(xml, "title", "");
    GroupAccess access = GroupAccess.forName(attribute(xml, "access", "member"));
    String relatedURL = attribute(xml, "relatedurl", "");
    boolean common = "true".equals(attribute(xml, "common", "false"));
    skipToEndElement(xml, element());
    return new Group(id, name, title, description, owner, access, common, relatedURL);
  }

}
