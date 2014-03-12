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

import org.pageseeder.bridge.model.PSResult;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public class PSResultHandler extends DefaultHandler {

  private List<PSResult> results = new ArrayList<PSResult>();

  private StringBuilder buffer = new StringBuilder();

  private PSResult result = null;

  /**
   * Current details field position.
   */
  private String field = null;

  public PSResultHandler() {
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("document".equals(localName)) {
      this.result = new PSResult();
    } else if ("field".equals(localName)) {
      this.field = atts.getValue("name");
    }
  }

  @Override
  public void characters(char[] ch, int offset, int len) throws SAXException {
    if (this.field != null) {
      this.buffer.append(ch, offset, len);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (this.result != null) {
      if ("document".equals(localName)) {
        this.results.add(this.result);
        this.result = null;
      } else if ("field".equals(localName)) {
        this.result.add(new PSResult.Field(this.field, this.buffer.toString()));
        this.buffer.setLength(0);
        this.field = null;
      }
    }
  }

  /**
   * @return the memberships
   */
  public List<PSResult> listResults() {
    return this.results;
  }

}
