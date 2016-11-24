/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz;

import java.io.IOException;

import org.pageseeder.berlioz.BerliozException;
import org.pageseeder.berlioz.content.ContentGenerator;
import org.pageseeder.berlioz.content.ContentRequest;
import org.pageseeder.bridge.berlioz.auth.Sessions;
import org.pageseeder.bridge.berlioz.auth.User;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * Returns the XML for the user currently logged in.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public final class GetUser implements ContentGenerator {

  @Override
  public final void process(ContentRequest req, XMLWriter xml) throws BerliozException, IOException {
    User user = Sessions.getUser(req);
    if (user != null) {
      user.toXML(xml);
    } else {
      xml.emptyElement("no-user");
    }
  }

}
