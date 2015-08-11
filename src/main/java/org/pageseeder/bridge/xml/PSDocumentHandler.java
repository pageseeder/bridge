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

import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSFolder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles for services returning documents or folders (as URIs) from services.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public final class PSDocumentHandler extends DefaultHandler {

  /**
   * The current document being processed.
   */
  private PSDocument document = null;

  /**
   * The current folder being processed.
   */
  private PSFolder folder = null;

  /**
   * The list of documents returned by the servlet.
   */
  List<PSDocument> documents = new ArrayList<PSDocument>();

  /**
   * The list of folders returned by the servlet.
   */
  List<PSFolder> folders = new ArrayList<PSFolder>();

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
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("uri".equals(localName)) {
      String mediatype = atts.getValue("mediatype");
      if ("folder".equals(mediatype)) {
        PSFolder folder = PSEntityFactory.toFolder(atts, this.folder);
        this.folder = folder;
      } else {
        PSDocument document = PSEntityFactory.toDocument(atts, this.document);
        this.document = document;
      }

    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("uri".equals(localName)) {
      if (this.document != null) {
        this.documents.add(this.document);
      }
      if (this.folder != null) {
        this.folders.add(this.folder);
      }
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
  public PSDocument getDocument() {
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
  public PSFolder getFolder() {
    int size = this.folders.size();
    return size > 0? this.folders.get(size-1) : null;
  }
}
