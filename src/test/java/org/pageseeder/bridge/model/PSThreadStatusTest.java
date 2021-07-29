package org.pageseeder.bridge.model;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.xmlwriter.XML;
import org.pageseeder.xmlwriter.XMLStringWriter;

import java.io.IOException;

/**
 * @author ccabral
 * @since 29 July 2021
 */
public class PSThreadStatusTest {

  @Test
  public void testToXML_validaValues(){
    XMLStringWriter writer = new XMLStringWriter(XML.NamespaceAware.No);
    PSThreadStatus status = new PSThreadStatus("q1w2e3");
    status.setStatus(PSThreadStatus.Status.COMPLETED);
    status.setGroupID(1L);
    status.setName("test-thread");
    status.setUsername("user");
    status.addMessage("test message");
    try {
      status.toXML(writer);
    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testToXML_nullValues(){
    XMLStringWriter writer = new XMLStringWriter(XML.NamespaceAware.No);
    PSThreadStatus status = new PSThreadStatus(null);
    try {
      status.toXML(writer);
    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
  }
}
