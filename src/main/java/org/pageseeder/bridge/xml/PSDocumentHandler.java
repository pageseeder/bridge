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
import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSFolder;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles for services returning documents or folders (as URIs) from services.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.1.0
 */
public final class PSDocumentHandler extends DefaultHandler {

  /**
   * The current document being processed.
   */
  private @Nullable PSDocument document = null;

  /**
   * The current folder being processed.
   */
  private @Nullable PSFolder folder = null;

  /**
   * The list of documents returned by the servlet.
   */
  List<PSDocument> documents = new ArrayList<>();

  /**
   * The list of folders returned by the servlet.
   */
  List<PSFolder> folders = new ArrayList<>();

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
   * Create a new handler for documents.
   */
  public PSDocumentHandler() {
  }

  /**
   * Create a new handler for document belong to a specific group.
   *
   * @param document A document to update.
   */
  public PSDocumentHandler(PSDocument document) {
    this.document = document;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) {
    if ("uri".equals(localName)) {
      this.inURI = true;
      String mediatype = atts.getValue("mediatype");
      if ("folder".equals(mediatype)) {
        this.folder = PSEntityFactory.toFolder(atts, this.folder);
      } else {
        this.document = PSEntityFactory.toDocument(atts, this.document);
      }
    // record element content
    } else if (this.inURI && "description".equals(localName)
        || "labels".equals(localName)) {
      this.buffer.setLength(0);
      this.record = true;
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) {
    PSDocument doc = this.document;
    if ("uri".equals(localName)) {
      if (doc != null) {
        this.documents.add(doc);
        this.document = null;
      }
      PSFolder fol = this.folder;
      if (fol != null) {
        this.folders.add(fol);
        this.folder = null;
      }
      this.inURI = false;
    } else if (this.inURI) {
      if ("description".equals(localName)) {
        if (doc != null) {
          doc.setDescription(this.buffer.toString());
        }
      } else if ("labels".equals(localName)) {
        if (doc != null) {
          doc.setLabels(this.buffer.toString());
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
   * @return the list of groups
   */
  public List<PSDocument> listDocuments() {
    return this.documents;
  }

  /**
   * @return the list of groups
   */
  public @Nullable PSDocument getDocument() {
    int size = this.documents.size();
    return size > 0? this.documents.get(size-1) : null;
  }

  /**
   * @return the list of groups
   */
  public List<PSFolder> listFolders() {
    return this.folders;
  }

  /**
   * @return the list of groups
   */
  public @Nullable PSFolder getFolder() {
    int size = this.folders.size();
    return size > 0? this.folders.get(size-1) : null;
  }
}
