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

import org.pageseeder.bridge.psml.Fragment;
import org.pageseeder.bridge.psml.PSMLFragment;
import org.pageseeder.bridge.psml.PropertiesFragment;
import org.pageseeder.bridge.psml.Property;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles for PSML fragments.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public final class PSFragmentHandler extends DefaultHandler {

  /**
   * The current document being processed.
   */
  private PSMLFragment fragment = null;

  /**
   * The list of documents returned by the servlet.
   */
  List<PSMLFragment> fragments = new ArrayList<PSMLFragment>();

  /**
   * A buffer for the characters in text nodes.
   */
  private StringBuilder buffer = new StringBuilder();

  /**
   * Create a new handler for document belong to a specific group.
   */
  public PSFragmentHandler() {
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("fragment".equals(localName)) {
      String id = atts.getValue("id");
      String type = atts.getValue("type");
      this.fragment = new Fragment(id, type);

    } else if ("properties-fragment".equals(localName)) {
      String id = atts.getValue("id");
      this.fragment = new PropertiesFragment(id);

    } else if ("xref-fragment".equals(localName)) {
      // TODO

    } else if ("media-fragment".equals(localName)) {
      // TODO

    } else if ("property".equals(localName) && this.fragment instanceof PropertiesFragment) {
      PropertiesFragment f = (PropertiesFragment)this.fragment;
      String name = atts.getValue("name");
      String title = atts.getValue("title");
      String value = atts.getValue("value");
      String datatype = atts.getValue("datatype");
      Property p = new Property(name);
      p.setType(datatype);
      p.setValue(value);
      p.setTitle(title);
      f.add(p);
    } else {
      this.buffer.setLength(0);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("fragment".equals(localName)) {

    } else if ("properties-fragment".equals(localName)) {

    } else if ("xref-fragment".equals(localName)) {

    } else if ("media-fragment".equals(localName)) {

    } else if ("property".equals(localName)) {

    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    this.buffer.append(ch, start, length);
  }

  /**
   * @return the list of groups
   */
  public PSMLFragment getFragment() {
    return this.fragment;
  }

  /**
   * @return the list of groups
   */
  public List<PSMLFragment> listFragments() {
    return this.fragments;
  }

}
