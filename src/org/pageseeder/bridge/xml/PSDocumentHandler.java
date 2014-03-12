/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.xml;

import java.util.ArrayList;
import java.util.List;

import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles for services returning documents or folders (as URIs) from services.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public class PSDocumentHandler extends DefaultHandler {

  private final static Logger LOGGER = LoggerFactory.getLogger(PSDocumentBrowseHandler.class);

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
   * Create a new handler for document belong to a specific group.
   *
   * @param group The group the documents belong to.
   */
  public PSDocumentHandler() {
  }

  /**
   * Create a new handler for document belong to a specific group.
   *
   * @param group The group the documents belong to.
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
      if (this.document != null) this.documents.add(this.document);
      if (this.folder != null) this.folders.add(this.folder);
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
