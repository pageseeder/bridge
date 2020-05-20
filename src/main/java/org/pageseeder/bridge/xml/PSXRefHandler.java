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

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.model.PSURI;
import org.pageseeder.bridge.model.PSXRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles for services returning XRefs from services.
 *
 * @author Philip Rutherford
 *
 * @version 0.10.2
 * @version 0.8.1
 */
public final class PSXRefHandler extends DefaultHandler {

  private final Logger LOGGER = LoggerFactory.getLogger(PSXRefHandler.class);

  /**
   * The context URI
   */
  private @Nullable PSURI uri = null;

  /**
   * The current XRef being processed.
   */
  private @Nullable PSXRef xref = null;

  /**
   * The list of XRefs returned by the service.
   */
  List<PSXRef> xrefs = new ArrayList<>();

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
  public void startElement(String uri, String localName, String qName, Attributes atts) {
    if ("uri".equals(localName)) {
      if ("true".equals(atts.getValue("external"))) {
        this.uri = PSEntityFactory.toExternalURI(atts, null);
      } else if ("folder".equals(atts.getValue("mediatype"))) {
        this.uri = PSEntityFactory.toFolder(atts, null);
      } else {
        this.uri = PSEntityFactory.toDocument(atts, null);
      }
    } else if ("xref".equals(localName) || "blockxref".equals(localName)) {
      PSURI u = this.uri;
      if (u != null) {
        this.xref = PSEntityFactory.toXRef(atts, u, null);
      } else {
        this.LOGGER.warn("Unable to construct cross-reference {}: No URI", atts.getValue("id"));
      }
    } else if ("reversexref".equals(localName)) {
      PSURI u = this.uri;
      if (u != null) {
        this.xref = PSEntityFactory.toReverseXRef(atts, u, null);
      } else {
        this.LOGGER.warn("Unable to construct reverse cross-reference {}: No URI", atts.getValue("id"));
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) {
    PSXRef x = this.xref;
    if (x != null) {
      if ("xref".equals(localName) || "blockxref".equals(localName)) {
        this.xrefs.add(x);
        this.xref = null;
      } else if ("reversexref".equals(localName)) {
        this.xrefs.add(x);
        this.xref = null;
      }
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
  public @Nullable PSXRef getXRef() {
    int size = this.xrefs.size();
    return size > 0 ? this.xrefs.get(size-1) : null;
  }

}
