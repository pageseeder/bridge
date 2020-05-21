package org.pageseeder.bridge.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HandlerTests {

  public static void parse(File f, DefaultHandler handler) throws SAXException, IOException {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(false);
      factory.setNamespaceAware(true);
      SAXParser parser = factory.newSAXParser();
      parser.parse(f, handler);
    } catch (ParserConfigurationException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static void parse(String filename, DefaultHandler handler) throws SAXException, IOException {
    File f = new File("src/test/resources/org/pageseeder/bridge/xml/"+filename);
    parse(f, handler);
  }

}
