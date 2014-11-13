/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.xml;

import java.io.IOException;
import java.util.Stack;

import org.pageseeder.bridge.model.PSThreadStatus;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.topologi.diffx.xml.XMLStringWriter;
import com.topologi.diffx.xml.XMLWriter;

/**
 * Handler for PageSeeder groups and projects.
 *
 * @author Christophe Lauret
 * @version 0.2.2
 * @since 0.1.0
 */
public final class PSThreadHandler extends DefaultHandler {

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
    String dad = this.elements.isEmpty() ? null : this.elements.peek();
    if ("thread".equals(dad) && ("id".equals(localName) ||
                           "username".equals(localName) ||
                               "name".equals(localName) ||
                            "groupid".equals(localName))) {
      this.buffer = new StringBuilder();
    } else if ("threadstatus".equals(dad) && "status".equals(localName)) {
      this.buffer = new StringBuilder();
    } else if ("threadstatus".equals(dad) && "message".equals(localName)) {
      this.xmlContent = new XMLStringWriter(false);
    }
    this.elements.push(localName);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("id".equals(localName) && this.buffer != null) {
      this.tempStatus = new PSThreadStatus(this.buffer.toString());
      this.buffer = null;
    } else if ("name".equals(localName) && this.buffer != null && this.tempStatus != null) {
      this.tempStatus.setName(this.buffer.toString());
      this.buffer = null;
    } else if ("username".equals(localName) && this.buffer != null && this.tempStatus != null) {
      this.tempStatus.setUsername(this.buffer.toString());
      this.buffer = null;
    } else if ("groupid".equals(localName) && this.buffer != null && this.tempStatus != null) {
      this.tempStatus.setGroupID(Long.parseLong(this.buffer.toString()));
      this.buffer = null;
    } else if ("status".equals(localName) && this.buffer != null && this.tempStatus != null) {
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
