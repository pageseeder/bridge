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
import java.util.Stack;

import org.pageseeder.bridge.model.PSThreadStatus;
import org.pageseeder.xmlwriter.XMLStringWriter;
import org.pageseeder.xmlwriter.XMLWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for PageSeeder thread status.
 *
 * @author Jean-Baptiste Reure
 * @version 0.3.27
 * @since 0.2.2
 */
public final class PSThreadHandler extends DefaultHandler {

  /**
   * Use old XML format
   */
  private boolean oldFormat = true;

  private PSThreadStatus tempStatus = null;

  /**
   * To capture text data.
   */
  private StringBuilder buffer = null;

  /**
   * To capture XML content.
   */
  private XMLWriter xmlContent = null;

  private Stack<String> elements = new Stack<String>();
  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if (this.xmlContent != null) {
      try {
        this.xmlContent.openElement(uri, localName, true);
        for (int i = 0; i < atts.getLength(); i++) {
          this.xmlContent.attribute(atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
        }
      } catch (IOException ex) {
        // shouldn't happen as internal writer
      }
    }
    if ("thread".equals(localName)) {
      String id = atts.getValue("id");
      this.oldFormat = id == null;
      if (!this.oldFormat) {
        this.tempStatus = new PSThreadStatus(id);
        this.tempStatus.setGroupID(Long.valueOf(atts.getValue("groupid")));
        this.tempStatus.setStatus(atts.getValue("status"));
        this.tempStatus.setUsername(atts.getValue("username"));
        this.tempStatus.setName(atts.getValue("name"));
      }
    }
    String dad = this.elements.isEmpty() ? null : this.elements.peek();
    if ("thread".equals(dad) && ("id".equals(localName) ||
                           "username".equals(localName) ||
                               "name".equals(localName) ||
                            "groupid".equals(localName)) && this.oldFormat) {
      this.buffer = new StringBuilder();
    } else if ("threadstatus".equals(dad) && "status".equals(localName) && this.oldFormat) {
      this.buffer = new StringBuilder();
    } else if ("message".equals(localName) &&
        ((this.oldFormat && "threadstatus".equals(dad)) || (!this.oldFormat && "thread".equals(dad)))) {
      this.xmlContent = new XMLStringWriter(false);
    }
    this.elements.push(localName);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("id".equals(localName) && this.buffer != null && this.oldFormat) {
      this.tempStatus = new PSThreadStatus(this.buffer.toString());
      this.buffer = null;
    } else if ("name".equals(localName) && this.buffer != null && this.tempStatus != null && this.oldFormat) {
      this.tempStatus.setName(this.buffer.toString());
      this.buffer = null;
    } else if ("username".equals(localName) && this.buffer != null && this.tempStatus != null && this.oldFormat) {
      this.tempStatus.setUsername(this.buffer.toString());
      this.buffer = null;
    } else if ("groupid".equals(localName) && this.buffer != null && this.tempStatus != null && this.oldFormat) {
      this.tempStatus.setGroupID(Long.parseLong(this.buffer.toString()));
      this.buffer = null;
    } else if ("status".equals(localName) && this.buffer != null && this.tempStatus != null && this.oldFormat) {
      this.tempStatus.setStatus(this.buffer.toString());
      this.buffer = null;
    } else if ("message".equals(localName) && this.xmlContent != null && this.tempStatus != null) {
      this.tempStatus.addMessage(this.xmlContent.toString());
      this.xmlContent = null;
    }
    this.elements.pop();
    if (this.xmlContent != null) {
      try {
        this.xmlContent.closeElement();
      } catch (IOException ex) {
        // shouldn't happen as internal writer
      }
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (this.xmlContent != null) {
      try {
        this.xmlContent.writeText(ch, start, length);
      } catch (IOException ex) {
        // shouldn't happen as internal writer
      }
    } else if (this.buffer != null) {
      this.buffer.append(ch, start, length);
    }
  }

  public PSThreadStatus getThreadStatus() {
    return this.tempStatus;
  }
}
