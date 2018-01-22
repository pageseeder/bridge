/*
 * Copyright 2017 Allette Systems (Australia)
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

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.xml.InvalidAttributeException;
import org.pageseeder.bridge.xml.MissingAttributeException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public abstract class BasicXMLStreamHandler<T> implements XMLStreamHandler<T> {

  protected final String _element;

  public BasicXMLStreamHandler(String element) {
    this._element = element;
  }

  @Override
  public String element() {
    return this._element;
  }

  /**
   * Indicates whether the handler is ready to handle the XML based on the state of the <code>XMLStreamReader</code>.
   *
   * <p>This default implementation returns <code>true</code> if the current event type is <code>START_ELEMENT</code>
   * and the element name matches the name of the element returned by {@link #element()} method.</p>
   *
   * <p>Implementations are free to check for any state of the XML stream reader as long at they do not modify its
   * state.</p>
   *
   * @param xml The XML Stream reader
   *
   * @return <code>true</code> if the state of the XMLStreamReader is ready to take over processing;
   *         <code>false</code> otherwise.
   *
   * @throws XMLStreamException If thrown by the XMLStreamReader
   * @throws UnsupportedOperationException If a method affecting the state of the stream is used.
   */
  public boolean isReady(XMLStreamReader xml) throws XMLStreamException {
    return xml.getEventType() == XMLStreamReader.START_ELEMENT
        && xml.getLocalName().equals(element());
  }

  // A collection of utility methods to simplify methods

  public boolean isOnElement(XMLStreamReader xml) {
    return xml.isStartElement() && xml.getLocalName().equals(element());
  }

  public static String attribute(XMLStreamReader xml, String name) {
    String value = optionalAttribute(xml, name);
    if (value == null) throw new MissingAttributeException("Attribute "+name+" is not specified");
    return value;
  }

  public static String attribute(XMLStreamReader xml, String name, String fallback) {
    String value = optionalAttribute(xml, name);
    if (value == null) return fallback;
    return value;
  }

  public static long attribute(XMLStreamReader xml, String name, long fallback) {
    String value = optionalAttribute(xml, name);
    if (value == null) return fallback;
    try {
      return Long.parseLong(value);
    } catch (IllegalArgumentException ex) {
      throw new InvalidAttributeException(name);
    }
  }

  public static boolean attribute(XMLStreamReader xml, String name, boolean fallback) {
    String value = optionalAttribute(xml, name);
    if (value == null) return fallback;
    return "true".equals(value);
  }

  @Nullable
  public static String optionalAttribute(XMLStreamReader xml, String name) {
    for (int i=0; i< xml.getAttributeCount(); i++) {
      if (name.equals(xml.getAttributeLocalName(i))) return xml.getAttributeValue(i);
    }
    return null;
  }

  public static void skipToStartElement(XMLStreamReader xml) throws XMLStreamException {
    do {
      xml.next();
    } while (!xml.isStartElement());
  }

  public static void skipToEndElement(XMLStreamReader xml, String name) throws XMLStreamException {
    do {
      xml.next();
    } while (!(xml.isEndElement() && xml.getLocalName().equals(name)));
  }

}
