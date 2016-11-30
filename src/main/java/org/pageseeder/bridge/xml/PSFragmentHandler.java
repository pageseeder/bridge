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
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSURI;
import org.pageseeder.bridge.model.PSXRef;
import org.pageseeder.bridge.psml.Fragment;
import org.pageseeder.bridge.psml.PSMLFragment;
import org.pageseeder.bridge.psml.PropertiesFragment;
import org.pageseeder.bridge.psml.Property;
import org.pageseeder.bridge.psml.XRefFragment;
import org.pageseeder.xmlwriter.XML.NamespaceAware;
import org.pageseeder.xmlwriter.XMLStringWriter;
import org.pageseeder.xmlwriter.XMLWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for PSML fragments.
 *
 * @author Christophe Lauret
 * @author Philip Rutherford
 * @version 0.8.1
 */
public final class PSFragmentHandler extends DefaultHandler {

  /**
   * The containing uri
   */
  private @Nullable PSURI fraguri = null;

  /**
   * The current document being processed.
   */
  private @Nullable PSMLFragment fragment = null;

  /**
   * The list of documents returned by the servlet.
   */
  private List<PSMLFragment> fragments = new ArrayList<>();

  /**
   * A buffer for the characters in text nodes.
   */
  private StringBuilder buffer = new StringBuilder();

  /**
   * Whether to copy the content
   */
  private boolean copyAll = false;

  /**
   * A writer to store the fragment content.
   */
  private XMLWriter fragXMLContent = new XMLStringWriter(NamespaceAware.No);

  /**
   * A handler to do the elements copy.
   */
  private XMLCopy copy = new XMLCopy(this.fragXMLContent);

  /**
   * Create a new fragment handler for document.
   *
   * @param document  the document containing the fragment
   */
  public PSFragmentHandler(PSDocument document) {
    this.fraguri = document;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

    if (this.copyAll) {
      // copy the start element and attribute
      this.copy.startElement(uri, localName, qName, atts);
    }

    if ("fragment".equals(localName)) {
      String id = Objects.requireNonNull(atts.getValue("id"), "expected id attribute on fragment");
      String type = Objects.requireNonNull(atts.getValue("type"), "expected type attribute on fragment");
      this.fragment = new Fragment(id, type);
      this.copyAll = true;

    } else if ("properties-fragment".equals(localName)) {
      String id = Objects.requireNonNull(atts.getValue("id"), "expected id attribute on properties-fragment");
      String type = Objects.requireNonNull(atts.getValue("type"), "expected type attribute on properties-fragment");
      this.fragment = new PropertiesFragment(id, type);

    } else if ("xref-fragment".equals(localName)) {
      String id = Objects.requireNonNull(atts.getValue("id"), "expected id attribute on xref-fragment");
      String type = Objects.requireNonNull(atts.getValue("type"), "expected type attribute on xref-fragment");
      this.fragment = new XRefFragment(id, type);

    } else if ("media-fragment".equals(localName)) {
      // TODO

    } else if ("property".equals(localName) && this.fragment instanceof PropertiesFragment) {
      PropertiesFragment f = (PropertiesFragment) this.fragment;
      String name = Objects.requireNonNull(atts.getValue("name"), "expected property name");
      String title = atts.getValue("title");
      String value = atts.getValue("value");
      String datatype = atts.getValue("datatype");
      Property p = new Property(name);
      p.setType(datatype);
      p.setValue(value);
      p.setTitle(title);
      if (f != null) {
        f.add(p);
      }
    } else if ("blockxref".equals(localName) && this.fragment instanceof XRefFragment) {
      XRefFragment f = (XRefFragment) this.fragment;
      PSXRef xref = PSEntityFactory.toXRef(atts, this.fraguri, null);
      if (f != null) {
        f.add(xref);
      }
    } else {
      this.buffer.setLength(0);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("fragment".equals(localName)) {
      // store the content
      if (this.fragment instanceof Fragment) {
        Fragment frag = (Fragment) this.fragment;
        if (frag != null) {
          frag.setContent(this.fragXMLContent.toString());
        }
        this.copyAll = false;
      }

    } else if ("properties-fragment".equals(localName)) {

    } else if ("xref-fragment".equals(localName)) {

    } else if ("media-fragment".equals(localName)) {

    } else if ("property".equals(localName)) {

    }

    // copy the end element
    if (this.copyAll) {
      this.copy.endElement(uri, localName, qName);
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    this.buffer.append(ch, start, length);

    // copy the value
    if (this.copyAll) {
      this.copy.characters(ch, start, length);
    }
  }

  /**
   * @return the list of groups
   */
  public @Nullable PSMLFragment getFragment() {
    return this.fragment;
  }

  /**
   * @return the list of groups
   */
  public List<PSMLFragment> listFragments() {
    return this.fragments;
  }

}
