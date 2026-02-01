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

import org.jspecify.annotations.Nullable;
import org.pageseeder.bridge.model.PSExternalURI;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles for services returning external URIs from services.
 *
 * @author Christophe Lauret
 * @author Jean-Baptiste Reure
 *
 * @version 0.10.2
 * @since 0.3.0
 */
public final class PSExternalURIHandler extends DefaultHandler {

  /**
   * The list of external URIs returned by the servlet.
   */
  private final List<PSExternalURI> _externalURIs = new ArrayList<>();

  /**
   * The current external URI being processed.
   */
  private @Nullable PSExternalURI externalURI = null;

  /**
   * State variable, when <code>true</code> the handler should capture character data on the text buffer.
   */
  private boolean record = false;

  /**
   * State variable, when <code>true</code> the handler is within a "uri" element.
   */
  private boolean inURI = false;

  /**
   * State variable, Text buffer.
   */
  private StringBuilder buffer = new StringBuilder();

  /**
   * Create a new handler for external URIs.
   */
  public PSExternalURIHandler() {
  }

  /**
   * Create a new handler for external URI belong to a specific group.
   *
   * @param external An external URI to update.
   */
  public PSExternalURIHandler(PSExternalURI external) {
    this.externalURI = external;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) {
    if ("uri".equals(localName)) {
      this.inURI = true;
      this.externalURI = PSEntityFactory.toExternalURI(atts, this.externalURI);
    // record element content
    } else if (this.inURI && "description".equals(localName)
        || "labels".equals(localName)) {
      this.buffer.setLength(0);
      this.record = true;
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) {
    PSExternalURI extURI = this.externalURI;
    if ("uri".equals(localName)) {
      if (extURI != null) {
        this._externalURIs.add(extURI);
        this.externalURI = null;
      }
      this.inURI = false;
    } else if (this.inURI) {
      if (extURI != null) {
        if ("description".equals(localName)) {
          extURI.setDescription(this.buffer.toString());
        } else if ("labels".equals(localName)) {
          extURI.setLabels(this.buffer.toString());
        }
      }
    }
    // Stop recording when an element closes
    this.record = false;
  }

  @Override
  public void characters(char[] ch, int start, int length) {
    if (this.record) {
      this.buffer.append(ch, start, length);
    }
  }

  /**
   * @return the list of external URIs
   */
  public List<PSExternalURI> listExternalURIs() {
    return this._externalURIs;
  }

  /**
   * @return a single external URI
   */
  public @Nullable PSExternalURI getExternalURI() {
    int size = this._externalURIs.size();
    return size > 0 ? this._externalURIs.get(size-1) : null;
  }

}
