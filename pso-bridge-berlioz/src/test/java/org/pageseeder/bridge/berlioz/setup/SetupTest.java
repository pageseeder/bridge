package org.pageseeder.bridge.berlioz.setup;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;
import org.pageseeder.berlioz.GlobalSettings;
import org.pageseeder.bridge.berlioz.setup.Setup;
import org.pageseeder.bridge.berlioz.setup.SetupException;
import org.pageseeder.xmlwriter.XMLWriter;
import org.pageseeder.xmlwriter.XMLWriterNSImpl;

public final class SetupTest {

  @Test
  public void testSetup() throws SetupException {
    File dir = new File("src/test/data");
    GlobalSettings.setup(dir);
    GlobalSettings.setMode("local");
    File f = new File("src/test/data/setup/setup.xml");
    Setup setup = Setup.parse(f);
    XMLWriter xml = new XMLWriterNSImpl(new PrintWriter(System.out), true);
    // TODO
//    setup.simulate(xml);
//    xml.flush();
  }
}
