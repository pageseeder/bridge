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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.xmlwriter.XMLWriter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Copy the parsed XML to the specified XML writer.
 *
 * <p>This class also implements the {@link LexicalHandler} interface, so that comments can be copied if the
 * {@link XMLReader} reader supports the <code>"http://xml.org/sax/properties/lexical-handler"</code> property.
 *
 * @author Christophe Lauret
 *
 * @version 0.9.7
 * @since 0.9.7
 */
public final class XMLCopy extends DefaultHandler implements ContentHandler, LexicalHandler {

  /**
   * Where the XML should be copied to.
   */
  private final XMLWriter to;

  /**
   * The prefix mapping to add to the next <i>startElement</i> event.
   */
  private final Map<String, String> mapping = new HashMap<>();

  /**
   * Creates a new XMLExtractor wrapping the specified XML writer.
   *
   * @param xml The XML writer to use.
   */
  public XMLCopy(XMLWriter xml) {
    this.to = xml;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    try {
      this.to.openElement(qName);
      for (int i = 0; i < atts.getLength(); i++) {
        String name = atts.getQName(i);
        String value = atts.getValue(i);
        // Since we iterate over the values, there is no reason to think we could get a null...
        if (name != null && value != null) {
          this.to.attribute(name, value);
        }
      }
      // Put the prefix mapping was reported BEFORE the startElement was reported...
      if (!this.mapping.isEmpty()) {
        for (Entry<String, String> e : this.mapping.entrySet()) {
          boolean hasPrefix = e.getKey() != null && e.getKey().length() > 0;
          this.to.attribute("xmlns"+(hasPrefix? ":"+ e.getKey() : e.getKey()), e.getValue());
        }
        this.mapping.clear();
      }
    } catch (IOException ex) {
      throw new SAXException(ex);
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    try {
      this.to.writeText(ch, start, length);
    } catch (IOException ex) {
      throw new SAXException(ex);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    try {
      this.to.closeElement();
    } catch (IOException ex) {
      throw new SAXException(ex);
    }
  }

  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    boolean hasPrefix = prefix != null && prefix.length() > 0;
    this.mapping.put((hasPrefix? prefix : ""), uri);
  }

  @Override
  public void processingInstruction(String target, @Nullable String data) throws SAXException {
    try {
      this.to.writePI(target, data);
    } catch (IOException ex) {
      throw new SAXException(ex);
    }
  }

  // Lexical Handler =============================================================================

  /**
   * Copy the comment to the output.
   *
   * {@inheritDoc}
   */
  @Override
  public void comment(char[] ch, int start, int length) throws SAXException {
    try {
      this.to.writeComment(String.copyValueOf(ch, start, length));
    } catch (IOException ex) {
      throw new SAXException(ex);
    }
  }

  /**
   * Does nothing.
   *
   * {@inheritDoc}
   */
  @Override
  public void startCDATA() throws SAXException {
  }

  /**
   * Does nothing.
   *
   * {@inheritDoc}
   */
  @Override
  public void endCDATA() throws SAXException {
  }

  /**
   * Does nothing.
   *
   * {@inheritDoc}
   */
  @Override
  public void startDTD(String name, @Nullable String publicId, @Nullable String systemId) throws SAXException {
  }

  /**
   * Does nothing.
   *
   * {@inheritDoc}
   */
  @Override
  public void endDTD() throws SAXException {
  }

  /**
   * Does nothing.
   *
   * {@inheritDoc}
   */
  @Override
  public void startEntity(String name) throws SAXException {
  }

  /**
   * Does nothing.
   *
   * {@inheritDoc}
   */
  @Override
  public void endEntity(String name) throws SAXException {
  }

}
