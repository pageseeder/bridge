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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class XMLStreamTest {

  private final static XMLInputFactory FACTORY = newFactory();

  private final static String FOLDER = "org/pageseeder/bridge/xml/";

  public static <T> T parseItem(String filename, XMLStreamItem<T> handler) throws IOException, XMLStreamException {
    try (InputStream in = XMLStreamTest.class.getClassLoader().getResourceAsStream(FOLDER+filename)) {
      XMLStreamReader xml = FACTORY.createXMLStreamReader(in);
      while (xml.hasNext()) {
        xml.next();
        if (xml.getEventType() == XMLStreamReader.START_ELEMENT) {
          if (handler.element().equals(xml.getLocalName())) {
            return handler.toItem(xml);
          }
        }
      }
    }
    return null;
  }

  public static <T> List<T> parseList(String filename, XMLStreamList<T> handler) throws IOException, XMLStreamException {
    try (InputStream in = XMLStreamTest.class.getClassLoader().getResourceAsStream(FOLDER+filename)) {
      XMLStreamReader xml = FACTORY.createXMLStreamReader(in);
      while (xml.hasNext()) {
        xml.next();
        if (xml.getEventType() == XMLStreamReader.START_ELEMENT) {
          if (handler.element().equals(xml.getLocalName())) {
            return handler.toList(xml);
          }
        }
      }
    }
    return Collections.emptyList();
  }

  private static XMLInputFactory newFactory() {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    factory.setProperty(XMLInputFactory.IS_COALESCING, true);
    factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, true);
    factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
    return factory;
  }
}
