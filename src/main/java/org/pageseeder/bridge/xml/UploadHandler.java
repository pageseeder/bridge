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

import org.pageseeder.bridge.model.PSDocument;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A handler to handle the response from the <code>UploadServlet</code>.
 *
 * <p>This is a low-level API, avoid using directly.
 *
 * @author Christophe Lauret
 * @version 0.3.0
 */
public final class UploadHandler extends DefaultHandler {

    private Long id = null;
    private String mediatype = null;
    private String scheme = null;
    private String host = null;
    private int port = -1;
    private String path = null;
    private String title = null;

    /**
     * The document we are trying to get.
     */
    private PSDocument document = null;

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

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      // record the character data of the elements below
      if ("uri".equals(localName)) {
        this.inURI = true;
        this.id = Long.valueOf(attributes.getValue("id") != null ? attributes.getValue("id") : null);
        this.scheme = attributes.getValue("scheme");
        this.host = attributes.getValue("host");
        this.port = Integer.parseInt(attributes.getValue("port") != null ? attributes.getValue("port") : null);
        this.path = attributes.getValue("path");
        this.title = attributes.getValue("title");
        this.mediatype = attributes.getValue("mediatype");
      // for backward compatibility
      } else if (this.inURI && "id".equals(localName)
       || "type".equals(localName)
       || "scheme".equals(localName)
       || "host".equals(localName)
       || "port".equals(localName)
       || "path".equals(localName)
       || "usertitle".equals(localName)) {
        this.buffer.setLength(0);
        this.record = true;
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      // for backward compatibility
      if (this.inURI) {
        if ("id".equals(localName)) {
          this.id = Long.valueOf(this.buffer.toString());

        } else if ("type".equals(localName)) {
          this.mediatype = this.buffer.toString();

        } else if ("scheme".equals(localName)) {
          this.scheme = this.buffer.toString();

        } else if ("host".equals(localName)) {
          this.host = this.buffer.toString();

        } else if ("port".equals(localName)) {
          this.port = Integer.parseInt(this.buffer.toString());

        } else if ("path".equals(localName)) {
          this.path = this.buffer.toString();

        } else if ("usertitle".equals(localName)) {
          this.title = this.buffer.toString();

        } else if ("uri".equals(localName)) {
          this.document = new PSDocument(this.scheme, this.host, this.port, this.path);
          this.document.setMediaType(this.mediatype);
          this.document.setTitle(this.title);
          this.document.setId(this.id);
          this.inURI = false;
        }
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
     * @return the document that was uploaded or <code>null</code>
     */
    public PSDocument getDocument() {
      return this.document;
    }
  }
