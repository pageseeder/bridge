/*
 * Copyright 2016 Allette Systems (Australia)
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

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A handler which delegates parsing events to two handlers to be used at once.
 *
 * <p>For each SAX event, the method of the first handler is invoked first.
 *
 * <p>If either handler throws a {@link SAXException} during parsing, the
 * method for both handler will be invoked but a {@link SAXException} will be
 * thrown after the event is reported.
 *
 * @author Christophe Lauret
 *
 * @version 0.9.2
 * @since 0.9.2
 */
public final class DuplexHandler extends DefaultHandler {

  /**
   * First handler.
   */
  private final DefaultHandler _first;

  /**
   * Second handler.
   */
  private final DefaultHandler _second;

  /**
   * Creates
   *
   * @param first
   * @param second
   */
  public DuplexHandler(DefaultHandler first, DefaultHandler second) {
    this._first = Objects.requireNonNull(first);
    this._second = Objects.requireNonNull(second);
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    SAXException failedA = characters(this._first, ch, start, length);
    SAXException failedB = characters(this._second, ch, start, length);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void endDocument() throws SAXException {
    SAXException failedA = endDocument(this._first);
    SAXException failedB = endDocument(this._second);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    SAXException failedA = endElement(this._first, uri, localName, qName);
    SAXException failedB = endElement(this._second, uri, localName, qName);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
    SAXException failedA = endPrefixMapping(this._first, prefix);
    SAXException failedB = endPrefixMapping(this._second, prefix);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void error(SAXParseException error) throws SAXException {
    SAXException failedA = error(this._first, error);
    SAXException failedB = error(this._second, error);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void fatalError(SAXParseException error) throws SAXException {
    SAXException failedA = fatalError(this._first, error);
    SAXException failedB = fatalError(this._second, error);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    SAXException failedA = ignorableWhitespace(this._first, ch, start, length);
    SAXException failedB = ignorableWhitespace(this._second, ch, start, length);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void notationDecl(String name, String publicId, String systemId) throws SAXException {
    SAXException failedA = notationDecl(this._first, name, publicId, systemId);
    SAXException failedB = notationDecl(this._second, name, publicId, systemId);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void processingInstruction(String target, String data) throws SAXException {
    SAXException failedA = processingInstruction(this._first, target, data);
    SAXException failedB = processingInstruction(this._second, target, data);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void setDocumentLocator(Locator locator) {
    this._first.setDocumentLocator(locator);
    this._second.setDocumentLocator(locator);
  }

  @Override
  public void skippedEntity(String name) throws SAXException {
    SAXException failedA = skippedEntity(this._first, name);
    SAXException failedB = skippedEntity(this._second, name);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void startDocument() throws SAXException {
    SAXException failedA = startDocument(this._first);
    SAXException failedB = startDocument(this._second);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    SAXException failedA = startElement(this._first, uri, localName, qName, attributes);
    SAXException failedB = startElement(this._second, uri, localName, qName, attributes);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    SAXException failedA = startPrefixMapping(this._first, prefix, uri);
    SAXException failedB = startPrefixMapping(this._second, prefix, uri);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName)
      throws SAXException {
    SAXException failedA = unparsedEntityDecl(this._first, name, publicId, systemId, notationName);
    SAXException failedB = unparsedEntityDecl(this._second, name, publicId, systemId, notationName);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  @Override
  public void warning(SAXParseException exception) throws SAXException {
    SAXException failedA = warning(this._first, exception);
    SAXException failedB = warning(this._second, exception);
    if (failedA != null) throw failedA;
    if (failedB != null) throw failedB;
  }

  // Delegate methods
  // --------------------------------------------------------------------------


  private static @Nullable SAXException characters(DefaultHandler handler, char[] ch, int start, int length) {
    try {
      handler.characters(ch, start, length);
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException endDocument(DefaultHandler handler) {
    try {
      handler.endDocument();
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException endElement(DefaultHandler handler, String uri, String localName, String qName) {
    try {
      handler.endElement(uri, localName, qName);
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException endPrefixMapping(DefaultHandler handler, String prefix) {
    try {
      handler.endPrefixMapping(prefix);
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException error(DefaultHandler handler, SAXParseException exception) {
    try {
      handler.error(exception);
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException fatalError(DefaultHandler handler, SAXParseException error) {
    try {
      handler.fatalError(error);
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException ignorableWhitespace(DefaultHandler handler, char[] ch, int start, int length) {
    try {
      handler.ignorableWhitespace(ch, start, length);
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException notationDecl(DefaultHandler handler, String name, String publicId, String systemId) {
    try {
      handler.notationDecl(name, publicId, systemId);
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException processingInstruction(DefaultHandler handler, String target, String data) {
    try {
      handler.processingInstruction(target, data);
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException skippedEntity(DefaultHandler handler, String name) {
    try {
      handler.skippedEntity(name);
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException startDocument(DefaultHandler handler) {
    try {
      handler.startDocument();
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException startElement(DefaultHandler handler, String uri, String localName, String qName, Attributes attributes) {
    try {
      handler.startElement(uri, localName, qName, attributes);
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException startPrefixMapping(DefaultHandler handler, String prefix, String uri) {
    try {
      handler.startPrefixMapping(prefix, uri);
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException unparsedEntityDecl(DefaultHandler handler, String name, String publicId, String systemId, String notationName) {
    try {
      handler.unparsedEntityDecl(name, publicId, systemId, notationName);
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }

  private static @Nullable SAXException warning(DefaultHandler handler, SAXParseException exception) {
    try {
      handler.warning(exception);
      return null;
    } catch (SAXException ex) {
      return ex;
    }
  }


}

