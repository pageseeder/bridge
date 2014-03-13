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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A handler for generating documents from the "DocumentBrowse" servlet.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public final class PSDocumentBrowseHandler extends DefaultHandler {

  /**
   * The current document being processed.
   */
  private PSDocument doc = null;

  /**
   * The list of documents returned by the servlet.
   */
  List<PSDocument> docs = new ArrayList<PSDocument>();

  /**
   * The local name of the current element being processed.
   */
  private String currentElement = null;

  /**
   * A buffer for the characters in text nodes.
   */
  private StringBuilder buffer = new StringBuilder();


  /**
   * Create a new handler for document belong to a specific group.
   *
   * @param group The group the documents belong to.
   */
  public PSDocumentBrowseHandler() {
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("uri".equals(localName)) {
      this.doc = new PSDocument("/");
    } else {
      this.currentElement = filterURIElement(localName);
      this.buffer.setLength(0);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("uri".equals(localName)) {
      this.docs.add(this.doc);
    } else if (this.doc != null) {
      if ("behaviour".equals(localName)) {
        // TODO

      } else if ("datecreated".equals(localName)) {
        // TODO

      } else if ("description".equals(localName)) {
        this.doc.setDescription(this.buffer.toString());

      } else if ("docid".equals(localName)) {
        this.doc.setDocid(this.buffer.toString());

      } else if ( "external".equals(localName)) {
        // TODO

      } else if ("id".equals(localName)) {
        Long id = PSHandlers.id(this.buffer.toString());
        this.doc.setId(id);

      } else if ("lastmodified".equals(localName)) {
        // TODO

      } else if ("path".equals(localName)) {
        String path = this.buffer.toString();
        int s = path.lastIndexOf('/');
        String filename = s >= 0? path.substring(s+1) : path;
        this.doc.setFilename(filename);


      } else if ("port".equals(localName)) {
        // TODO

      } else if ( "scheme".equals(localName)) {
        // TODO

      } else if ("type".equals(localName)) {
        // TODO

      } else if ( "usertitle".equals(localName)) {
        this.doc.setTitle(this.buffer.toString());


      }
    }

    // Done with element reset
    this.currentElement = null;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (this.currentElement != null) {
      this.buffer.append(ch, start, length);
    }
  }

  /**
   * @return the list of groups
   */
  public List<PSDocument> listDocuments() {
    return this.docs;
  }

  private String filterURIElement(String name) {
    if ("behaviour".equals(name)
     || "datecreated".equals(name)
     || "description".equals(name)
     || "docid".equals(name)
     || "external".equals(name)
     || "id".equals(name)
     || "lastmodified".equals(name)
     || "path".equals(name)
     || "port".equals(name)
     || "type".equals(name)
     || "scheme".equals(name)
     || "usertitle".equals(name))
      return name;
    else return null;
  }
}
