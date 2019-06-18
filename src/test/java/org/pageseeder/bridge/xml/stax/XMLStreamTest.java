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

import org.pageseeder.xmlwriter.XML;
import org.pageseeder.xmlwriter.XMLStringWriter;
import org.pageseeder.xmlwriter.XMLWritable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class XMLStreamTest {

  private final static XMLInputFactory FACTORY = newFactory();

  private final static String FOLDER = "org/pageseeder/bridge/xml/";


  public static <T> T parseItem(XMLWritable item, XMLStreamHandler<T> handler) throws IOException, XMLStreamException {
    try (Reader r = readXML(item)) {
      XMLStreamReader xml = FACTORY.createXMLStreamReader(r);
      return handler.next(xml);
    }
  }

  public static <T> T parseItem(String filename, XMLStreamHandler<T> handler) throws IOException, XMLStreamException {
    try (InputStream in = XMLStreamTest.class.getClassLoader().getResourceAsStream(FOLDER+filename)) {
      XMLStreamReader xml = FACTORY.createXMLStreamReader(in);
      return handler.next(xml);
    }
  }

  public static <T> List<T> parseList(String filename, XMLStreamHandler<T> handler) throws IOException, XMLStreamException {
    List<T> items = new ArrayList<>();
    try (InputStream in = XMLStreamTest.class.getClassLoader().getResourceAsStream(FOLDER+filename)) {
      XMLStreamReader xml = FACTORY.createXMLStreamReader(in);
      while (handler.find(xml)) {
        items.add(handler.get(xml));
      }
    }
    return items;
  }

  private static Reader readXML(XMLWritable item) throws IOException {
    XMLStringWriter xml = new XMLStringWriter(XML.NamespaceAware.No);
    item.toXML(xml);
    return new StringReader(xml.toString());
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
