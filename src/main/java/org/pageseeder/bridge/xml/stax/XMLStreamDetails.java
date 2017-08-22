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

import org.pageseeder.bridge.core.Details;
import org.pageseeder.bridge.core.Field;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;

public class XMLStreamDetails extends BasicXMLStreamHandler<Details> implements XMLStreamItem<Details> {

  public XMLStreamDetails() {
    super("details");
  }

  @Override
  public Details toItem(XMLStreamReader xml) throws XMLStreamException {
    if (isOnElement(xml)) {
      List<Field> fields = new ArrayList<>();
      do {
        xml.next();
        if (xml.isStartElement() && "field".equals(xml.getLocalName())) {
          long position = attribute(xml, "position", -1);
          boolean editable = "true".equals(attribute(xml, "editable", "false"));
          String name = attribute(xml, "name");
          String title = attribute(xml, "title", name);
          String type = attribute(xml, "type", "text");
          String value = xml.getElementText();
          Field field = new Field((int)position , name, value, editable, title, type);
          fields.add(field);
        }
      } while (!(xml.isEndElement() && "details".equals(xml.getLocalName())));

      if (fields.isEmpty()) return Details.NO_DETAILS;
      else return new Details(fields);
    } else throw new IllegalStateException("not a details");
  }

}
