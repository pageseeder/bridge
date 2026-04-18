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

import java.util.Objects;
import java.util.Stack;

import org.jspecify.annotations.Nullable;
import org.pageseeder.bridge.model.PSThreadStatus;
import org.pageseeder.xmlwriter.XML.NamespaceAware;
import org.pageseeder.xmlwriter.XMLStringWriter;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for PageSeeder thread status.
 *
 * @author Jean-Baptiste Reure
 *
 * @version 0.10.2
 * @since 0.2.2
 */
public final class PSThreadHandler extends DefaultHandler {

  /**
   * Use old XML format
   */
  private boolean oldFormat = true;

  private @Nullable PSThreadStatus tempStatus = null;

  /**
   * To capture text data.
   */
  private @Nullable StringBuilder buffer = null;

  /**
   * To capture XML content.
   */
  private @Nullable XMLStringWriter xmlContent = null;

  /**
   * To keep track of elements
   */
  private final Stack<String> _elements = new Stack<>();

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) {
    String element = localName != null && !localName.isEmpty() ? localName : qName;
    XMLStringWriter xml = this.xmlContent;
    if (xml != null) {
      xml.openElement(uri, element, true);
      for (int i = 0; i < atts.getLength(); i++) {
        String name = atts.getLocalName(i);
        String value = atts.getValue(i);
        if (name != null && value != null) {
          xml.attribute(atts.getURI(i), name, value);
        }
      }
    }
    if ("thread".equals(element)) {
      String id = atts.getValue("id");
      if (id != null) {
        this.oldFormat = false;
        PSThreadStatus tmp = new PSThreadStatus(id);
        tmp.setGroupID(Long.valueOf(Objects.requireNonNull(atts.getValue("groupid"), "Missing required groupid")));
        tmp.setStatus(atts.getValue("status"));
        tmp.setUsername(atts.getValue("username"));
        tmp.setName(atts.getValue("name"));
        this.tempStatus = tmp;
      } else {
        this.oldFormat = true;
      }
    }
    String dad = this._elements.isEmpty() ? null : this._elements.peek();
    if ("thread".equals(dad) && ("id".equals(element) ||
                           "username".equals(element) ||
                               "name".equals(element) ||
                            "groupid".equals(element)) && this.oldFormat) {
      this.buffer = new StringBuilder();
    } else if ("threadstatus".equals(dad) && "status".equals(element) && this.oldFormat) {
      this.buffer = new StringBuilder();
    } else if ("message".equals(element) &&
        ((this.oldFormat && "threadstatus".equals(dad)) || (!this.oldFormat && "thread".equals(dad)))) {
      this.xmlContent = new XMLStringWriter(NamespaceAware.No);
    }
    this._elements.push(element);
  }

  @Override
  public void endElement(String uri, String localName, String qName) {
    String element = localName != null && !localName.isEmpty() ? localName : qName;
    StringBuilder buf = this.buffer;
    if ("id".equals(element) && buf != null && this.oldFormat) {
      this.tempStatus = new PSThreadStatus(buf.toString());
      this.buffer = null;
    } else {
      PSThreadStatus tmp = this.tempStatus;
      if (tmp != null) {
        if ("name".equals(element) && buf != null && this.oldFormat) {
          tmp.setName(buf.toString());
          this.buffer = null;
        } else if ("username".equals(element) && buf != null && this.oldFormat) {
          tmp.setUsername(buf.toString());
          this.buffer = null;
        } else if ("groupid".equals(element) && buf != null && this.oldFormat) {
          tmp.setGroupID(Long.parseLong(buf.toString()));
          this.buffer = null;
        } else if ("status".equals(element) && buf != null && this.oldFormat) {
          tmp.setStatus(buf.toString());
          this.buffer = null;
        } else if ("message".equals(element)) {
          XMLStringWriter xml = this.xmlContent;
          if (xml != null) {
            tmp.addMessage(xml.toString());
            this.xmlContent = null;
          }
        }
      }
    }

    this._elements.pop();
    XMLStringWriter xml = this.xmlContent;
    if (xml != null) {
      xml.closeElement();
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) {
    XMLStringWriter xml = this.xmlContent;
    StringBuilder buf = this.buffer;
    if (xml != null) {
      xml.writeText(ch, start, length);
    } else if (buf != null) {
      buf.append(ch, start, length);
    }
  }

  public @Nullable PSThreadStatus getThreadStatus() {
    return this.tempStatus;
  }
}
