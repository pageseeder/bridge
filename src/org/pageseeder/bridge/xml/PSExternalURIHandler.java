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

import org.pageseeder.bridge.model.PSExternalURI;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles for services returning external URIs from services.
 *
 * @author Christophe Lauret
 * @author Jean-Baptiste Reure
 * @version 0.3.0
 */
public final class PSExternalURIHandler extends DefaultHandler {

  /**
   * The current external URI being processed.
   */
  private PSExternalURI externaluri = null;

  /**
   * The list of external URIs returned by the servlet.
   */
  List<PSExternalURI> externaluris = new ArrayList<PSExternalURI>();

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
    this.externaluri = external;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("uri".equals(localName)) {
      this.externaluri = PSEntityFactory.toExternalURI(atts, this.externaluri);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("uri".equals(localName)) {
      if (this.externaluri != null) this.externaluris.add(this.externaluri);
    }
  }

  /**
   * @return the list of external URIs
   */
  public List<PSExternalURI> listExternalURIs() {
    return this.externaluris;
  }

  /**
   * @return a single external URI
   */
  public PSExternalURI getExternalURI() {
    int size = this.externaluris.size();
    return size > 0 ? this.externaluris.get(size-1) : null;
  }

}
