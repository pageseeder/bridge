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

import org.jspecify.annotations.Nullable;
import org.pageseeder.bridge.xml.InvalidAttributeException;
import org.pageseeder.bridge.xml.MissingAttributeException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * A base class providing common utility methods for the {@link XMLStreamHandler} implementations
 *
 * @param <T> the type of object returned by the stream handler.
 */
public abstract class BasicXMLStreamHandler<T> implements XMLStreamHandler<T> {

  /**
   * Iterate over the events until the next START_ELEMENT.
   *
   * @param xml The XML Stream
   *
   * @throws XMLStreamException if thrown by the underlying XML stream
   */
  public static void skipToAnyStartElement(XMLStreamReader xml) throws XMLStreamException {
    do {
      xml.next();
    } while (!xml.isStartElement());
  }

  /**
   * Iterate over the events until the next END_ELEMENT matching the specified name.
   *
   * @param xml  The XML Stream
   * @param name The name of the element
   *
   * @throws XMLStreamException if thrown by the underlying XML stream
   */
  public static void skipToEndElement(XMLStreamReader xml, String name) throws XMLStreamException {
    do {
      xml.next();
    } while (!(xml.isEndElement() && xml.getLocalName().equals(name)));
  }

  /**
   * Return the value of a required attribute.
   *
   * @param xml The XML Stream
   * @param name The name of the attribute
   *
   * @return the attribute value
   *
   * @throws MissingAttributeException If the attribute was missing.
   */
  public static String attribute(XMLStreamReader xml, String name) {
    String value = optionalAttribute(xml, name);
    if (value == null) throw new MissingAttributeException("Attribute "+name+" is not specified");
    return value;
  }

  /**
   * Return the value of a required attribute and defaults to the specific fallback value.
   *
   * @param xml The XML Stream
   * @param name The name of the attribute
   * @param fallback The fallback value for the attribute
   *
   * @return the attribute value or the fallback value if not specified
   *
   * @throws MissingAttributeException If the attribute was missing.
   */
  public static String attribute(XMLStreamReader xml, String name, String fallback) {
    String value = optionalAttribute(xml, name);
    if (value == null) return fallback;
    return value;
  }


  /**
   * Return the long value of a required attribute and defaults to the specific fallback value.
   *
   * @param xml The XML Stream
   * @param name The name of the attribute
   * @param fallback The fallback value for the attribute
   *
   * @return the attribute value or the fallback value if not specified
   *
   * @throws InvalidAttributeException If the attribute could not be parsed as a long
   */
  public static long attribute(XMLStreamReader xml, String name, long fallback) {
    String value = optionalAttribute(xml, name);
    if (value == null) return fallback;
    try {
      return Long.parseLong(value);
    } catch (IllegalArgumentException ex) {
      throw new InvalidAttributeException(name);
    }
  }

  /**
   * Return the boolean value of a required attribute and defaults to the specific fallback value.
   *
   * @param xml The XML Stream
   * @param name The name of the attribute
   * @param fallback The fallback value for the attribute
   *
   * @return <code>true</code> if the attribute value is equal to "true" or the fallback value is <code>true</code>.
   */
  public static boolean attribute(XMLStreamReader xml, String name, boolean fallback) {
    String value = optionalAttribute(xml, name);
    if (value == null) return fallback;
    return "true".equals(value);
  }

  /**
   * Return the value of an optional attribute.
   *
   * @param xml The XML Stream
   * @param name The name of the attribute
   *
   * @return the attribute value or <code>null</code>
   *
   * @throws MissingAttributeException If the attribute was missing.
   */
  @Nullable
  public static String optionalAttribute(XMLStreamReader xml, String name) {
    for (int i=0; i< xml.getAttributeCount(); i++) {
      if (name.equals(xml.getAttributeLocalName(i))) return xml.getAttributeValue(i);
    }
    return null;
  }

}
