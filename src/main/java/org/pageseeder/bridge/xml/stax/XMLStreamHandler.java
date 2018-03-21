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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Implementations of which can be supplied to a response in order to retrieve objects
 * directly from the PageSeeder XML response.
 *
 * @param <T> the type of object that this handler returns.
 *
 * @author Christophe Lauret
 *
 * @since 0.12.0
 * @since 0.12.0
 */
public interface XMLStreamHandler<T> {

  /**
   * Find the event that will allow the {@link #get(XMLStreamReader)} method to return a value.
   *
   * <p>Precondition: any event</p>
   * <p>Postcondition: an event that may return and {@link #get(XMLStreamReader)} is invoked or the
   * end of the stream</p>
   *
   * @param xml The XML Stream reader
   *
   * @return <code>true</code> if the current event is at the position expected by the {@link #get(XMLStreamReader)} method;
   *         <code>false</code> otherwise.
   *
   * @throws XMLStreamException If thrown by the XMLStreamReader
   */
  boolean find(XMLStreamReader xml) throws XMLStreamException;

  /**
   * Return the object at the current position.
   *
   * <p>Precondition: an event from which an object can be constructed</p>
   * <p>Postcondition: an event after the initial event, usually the corresponding END_ELEMENT if the
   * precondition was a START_ELEMENT event.</p>
   *
   * @param xml The XML Stream reader
   *
   * @return the element at the current position if possible
   *
   * @throws XMLStreamException If thrown by the XMLStreamReader or if not at the correct position
   */
  @Nullable
  T get(XMLStreamReader xml) throws XMLStreamException;

  /**
   * Returns the next element that this handler can find.
   *
   * <p>The default implementation of this method is:</p>
   * <pre>{@code
   *  while (find(xml)) {
   *    T item = get(xml);
   *    if (item != null) return item;
   *  }
   *  return null;
   * }}</pre>
   *
   * <p>Precondition: any event</p>
   * <p>Postcondition: an event after {@link #get(XMLStreamReader)} is invoked or the
   * end of the stream</p>
   *
   * @param xml The XML Stream reader
   *
   * @return the get element if there is one or <code>null</code>
   *
   * @throws XMLStreamException If thrown by the XMLStreamReader
   */
  @Nullable
  default T next(XMLStreamReader xml) throws XMLStreamException {
    while (find(xml)) {
      T item = get(xml);
      if (item != null) return item;
    }
    // No more positions
    return null;
  }

}
