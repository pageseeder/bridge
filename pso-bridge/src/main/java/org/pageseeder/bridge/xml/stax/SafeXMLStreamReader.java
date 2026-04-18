/*
 * Copyright 2018 Allette Systems (Australia)
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
package org.pageseeder.bridge.xml.stax;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * A XML Stream reader that throws an exception if any attempt to change the state of the stream is made.
 *
 * <p>By default, all method will defer processing to the wrapped <code>XMLStreamReader</code>; except for the
 * following methods which will always throw an <code>UnsupportedOperationException</code>:</p>
 * <ul>
 *   <li>{@link #close()}</li>
 *   <li>{@link #getElementText()}</li>
 *   <li>{@link #next()}</li>
 *   <li>{@link #nextTag()}</li>
 * </ul>
 *
 * <p>The purpose of this class is to provide a <code>XMLStreamReader</code> instance that can be passed
 * safely without having its state changed.</p>
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class SafeXMLStreamReader implements XMLStreamReader {

  private final static String CHANGE_STATE_MESSAGE = "You must not call this method while checking the state of the stream";

  /**
   * The wrapped <code>XMLStreamReader</code>.
   */
  private final XMLStreamReader _xml;

  public SafeXMLStreamReader(XMLStreamReader xml) {
    this._xml = xml;
  }

  @Override
  public Object getProperty(String name) throws IllegalArgumentException {
    return this._xml.getProperty(name);
  }

  /**
   * @throws UnsupportedOperationException Always throws
   */
  @Override
  public int next() {
    throw new UnsupportedOperationException(CHANGE_STATE_MESSAGE);
  }

  @Override
  public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
    this._xml.require(type, namespaceURI, localName);
  }

  /**
   * @throws UnsupportedOperationException Always throws
   */
  @Override
  public String getElementText() {
    throw new UnsupportedOperationException(CHANGE_STATE_MESSAGE);
  }

  /**
   * @throws UnsupportedOperationException Always throws
   */
  @Override
  public int nextTag() {
    throw new UnsupportedOperationException(CHANGE_STATE_MESSAGE);
  }

  @Override
  public boolean hasNext() throws XMLStreamException {
    return this._xml.hasNext();
  }

  /**
   * @throws UnsupportedOperationException Always throws
   */
  @Override
  public void close() {
    throw new UnsupportedOperationException(CHANGE_STATE_MESSAGE);
  }

  @Override
  public String getNamespaceURI(String prefix) {
    return this._xml.getNamespaceURI();
  }

  @Override
  public boolean isStartElement() {
    return this._xml.isStartElement();
  }

  @Override
  public boolean isEndElement() {
    return this._xml.isEndElement();
  }

  @Override
  public boolean isCharacters() {
    return this._xml.isCharacters();
  }

  @Override
  public boolean isWhiteSpace() {
    return this._xml.isWhiteSpace();
  }

  @Override
  public String getAttributeValue(String namespaceURI, String localName) {
    return this._xml.getAttributeValue(namespaceURI, localName);
  }

  @Override
  public int getAttributeCount() {
    return this._xml.getAttributeCount();
  }

  @Override
  public QName getAttributeName(int index) {
    return this._xml.getAttributeName(index);
  }

  @Override
  public String getAttributeNamespace(int index) {
    return this._xml.getAttributeNamespace(index);
  }

  @Override
  public String getAttributeLocalName(int index) {
    return this._xml.getAttributeLocalName(index);
  }

  @Override
  public String getAttributePrefix(int index) {
    return this._xml.getAttributePrefix(index);
  }

  @Override
  public String getAttributeType(int index) {
    return this._xml.getAttributeType(index);
  }

  @Override
  public String getAttributeValue(int index) {
    return this._xml.getAttributeValue(index);
  }

  @Override
  public boolean isAttributeSpecified(int index) {
    return this._xml.isAttributeSpecified(index);
  }

  @Override
  public int getNamespaceCount() {
    return this._xml.getNamespaceCount();
  }

  @Override
  public String getNamespacePrefix(int index) {
    return this._xml.getNamespacePrefix(index);
  }

  @Override
  public String getNamespaceURI(int index) {
    return this._xml.getNamespaceURI(index);
  }

  @Override
  public NamespaceContext getNamespaceContext() {
    return this._xml.getNamespaceContext();
  }

  @Override
  public int getEventType() {
    return this._xml.getEventType();
  }

  @Override
  public String getText() {
    return this._xml.getText();
  }

  @Override
  public char[] getTextCharacters() {
    return this._xml.getTextCharacters();
  }

  @Override
  public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
    return this._xml.getTextCharacters(sourceStart, target, targetStart, length);
  }

  @Override
  public int getTextStart() {
    return this._xml.getTextStart();
  }

  @Override
  public int getTextLength() {
    return this._xml.getTextLength();
  }

  @Override
  public String getEncoding() {
    return this._xml.getEncoding();
  }

  @Override
  public boolean hasText() {
    return this._xml.hasText();
  }

  @Override
  public Location getLocation() {
    return this._xml.getLocation();
  }

  @Override
  public QName getName() {
    return this._xml.getName();
  }

  @Override
  public String getLocalName() {
    return this._xml.getLocalName();
  }

  @Override
  public boolean hasName() {
    return this._xml.hasName();
  }

  @Override
  public String getNamespaceURI() {
    return this._xml.getNamespaceURI();
  }

  @Override
  public String getPrefix() {
    return this._xml.getPrefix();
  }

  @Override
  public String getVersion() {
    return this._xml.getVersion();
  }

  @Override
  public boolean isStandalone() {
    return this._xml.isStandalone();
  }

  @Override
  public boolean standaloneSet() {
    return this._xml.standaloneSet();
  }

  @Override
  public String getCharacterEncodingScheme() {
    return this._xml.getCharacterEncodingScheme();
  }

  @Override
  public String getPITarget() {
    return this._xml.getPITarget();
  }

  @Override
  public String getPIData() {
    return this._xml.getPIData();
  }

}
