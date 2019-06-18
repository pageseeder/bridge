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

import org.pageseeder.bridge.xml.InvalidElementException;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * A stream handler that returns object for a single element name.
 *
 * @param <T> The type of object this handler returns
 */
public abstract class ElementXMLStreamHandler<T> extends BasicXMLStreamHandler<T> {

  /**
   * The name of the element.
   */
  protected final String _element;

  /**
   * Creates a new handler for the specified element name.
   *
   * @param element The name of the element.
   */
  public ElementXMLStreamHandler(String element) {
    this._element = element;
  }

  /**
   * @return The name of the element
   */
  public String element() {
    return this._element;
  }

  /**
   * Finds the next START_ELEMENT element which local name match the element name.
   *
   * <p>This method will modify the position in the XML stream in order to move to the next START_ELEMENT event
   * that matches the element name, unless the current event is already at the correct position.</p>
   *
   * @param xml The XML Stream reader
   *
   * @return <code>true</code> if the current event is the start element;
   *         <code>false</code> otherwise.
   *
   * @throws XMLStreamException if thrown while iteraring over the events in the stream.
   */
  public boolean find(XMLStreamReader xml) throws XMLStreamException {
    while (xml.hasNext() && !isOnElement(xml)) {
      xml.next();
    }
    return isOnElement(xml);
  }

  /**
   * Indicates whether the current event type is <code>START_ELEMENT</code> and the element name matches the name
   * of the element returned by {@link #element()} method.
   *
   * <p>This method only checks the current state and does move the cursor.</p>
   *
   * @param xml The XML Stream reader
   *
   * @return <code>true</code> if the state of the XMLStreamReader is ready to take over processing;
   *         <code>false</code> otherwise.
   */
  public boolean isOnElement(XMLStreamReader xml) {
    return xml.isStartElement() && xml.getLocalName().equals(element());
  }

  /**
   * Checks that the cursor is the start element specified for this element handler and throws an exception otherwise.
   *
   * @param xml The XML Stream reader
   *
   * @throws InvalidElementException If the cursor is not at the correct position.
   */
  public void checkOnElement(XMLStreamReader xml) {
    try {
      // We use the require method to get better diagnostics from the XML stream
      xml.require(XMLStreamConstants.START_ELEMENT, null, element());
    } catch (XMLStreamException ex) {
      throw new InvalidElementException("Expecting to be on start of '"+element()+"'", ex);
    }
  }

}
