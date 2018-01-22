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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Implementations of which can be supplied to a response in order to
 * retrieve an object or a list of objects directly from the PageSeeder XML
 * response.
 *
 * @param <T> the type of object that this handler returns.
 *
 * @author Christophe Lauret
 */
public interface XMLStreamHandler<T> {

  /**
   * @return The name of the element
   */
  String element();

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
  boolean isReady(XMLStreamReader xml) throws XMLStreamException;


}
