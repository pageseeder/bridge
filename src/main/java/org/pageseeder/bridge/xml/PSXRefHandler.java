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

import java.util.ArrayList;
import java.util.List;

import org.pageseeder.bridge.model.PSXRef;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles for services returning XRefs from services.
 *
 * @author Philip Rutherford
 * @version 0.8.1
 */
public final class PSXRefHandler extends DefaultHandler {

  /**
   * The current XRef being processed.
   */
  private PSXRef xref = null;

  /**
   * The list of XRefs returned by the service.
   */
  List<PSXRef> xrefs = new ArrayList<PSXRef>();

  /**
   * State variable, when <code>true</code> the handler should capture character data on the text buffer.
   */
  private boolean record = false;

  /**
   * State variable, Text buffer.
   */
  private StringBuilder buffer = new StringBuilder();
  
  /**
   * Create a new handler for external URIs.
   */
  public PSXRefHandler() {
  }

  /**
   * Create a new handler for external URI belong to a specific group.
   *
   * @param external An external URI to update.
   */
  public PSXRefHandler(PSXRef external) {
    this.xref = external;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("xref".equals(localName) || "blockxref".equals(localName)) {
      this.xref = PSEntityFactory.toXRef(atts, new PSXRef(PSXRef.ELEMENT.fromString(localName)));
      // record element content
      this.buffer.setLength(0);
      this.record = true;
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("xref".equals(localName) || "blockxref".equals(localName)) {
      this.xref.setContent(this.buffer.toString());
      this.xrefs.add(this.xref);
    }
    // Stop recording when an element closes
    this.record = false;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (this.record) {
      this.buffer.append(ch, start, length);
    }
  }
  /**
   * @return the list of external URIs
   */
  public List<PSXRef> listXRefs() {
    return this.xrefs;
  }

  /**
   * @return a single external URI
   */
  public PSXRef getXRef() {
    int size = this.xrefs.size();
    return size > 0 ? this.xrefs.get(size-1) : null;
  }

}
