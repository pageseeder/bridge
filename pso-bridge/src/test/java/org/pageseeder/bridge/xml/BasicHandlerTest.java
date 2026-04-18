package org.pageseeder.bridge.xml;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class BasicHandlerTest {

  @Test
  public void testIsElement() throws IOException, SAXException {
    HandlerTests.parse("basic.xml", new BasicHandler<Object>() {
      @Override
      public void startElement(String element, Attributes atts) {
        Assert.assertTrue(isElement(element));
      }
      @Override
      public void endElement(String element) {
        Assert.assertTrue(isElement(element));
      }
    });
  }

  @Test
  public void testHasAncestor() throws IOException, SAXException {
    HandlerTests.parse("basic.xml", new BasicHandler<Object>() {
      @Override
      public void startElement(String element, Attributes atts) {
        checkAncestor();
      }

      @Override
      public void endElement(String element) {
        checkAncestor();
      }

      private void checkAncestor() {
        if (isElement("a")) {
          Assert.assertTrue(hasAncestor("basic"));
          Assert.assertFalse(hasAncestor("a"));
        } else if (isElement("b")) {
          Assert.assertTrue(hasAncestor("basic"));
          Assert.assertTrue(hasAncestor("a"));
          Assert.assertFalse(hasAncestor("b"));
        } else if (isElement("c")) {
          Assert.assertTrue(hasAncestor("basic"));
          Assert.assertTrue(hasAncestor("a"));
          Assert.assertTrue(hasAncestor("b"));
          Assert.assertFalse(hasAncestor("c"));
        } else if (isElement("d")) {
          Assert.assertTrue(hasAncestor("basic"));
          Assert.assertFalse(hasAncestor("a"));
        }
      }

    });
  }


}
