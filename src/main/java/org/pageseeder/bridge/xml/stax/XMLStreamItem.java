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
 * @deprecated This interface was part of an experimental API and will be removed.
 *
 * @author Christophe Lauret
 *
 * @param <T> The type of object to return
 */
@Deprecated
public interface XMLStreamItem<T> extends XMLStreamHandler<T> {

  /**
   * Generates an item from the XML Stream.
   *
   * @param xml The XML Stream reader.
   * @return the item that was processed and added to the list.
   *
   * @throws XMLStreamException If an error occurs while processing the XML stream.
   */
  default T toItem(XMLStreamReader xml) throws XMLStreamException {
    return get(xml);
  }

}
