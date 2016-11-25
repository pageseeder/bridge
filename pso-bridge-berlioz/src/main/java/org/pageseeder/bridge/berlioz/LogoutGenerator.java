/*
 * Copyright 2016 Allette Systems (Australia)
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
package org.pageseeder.bridge.berlioz;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.pageseeder.berlioz.BerliozException;
import org.pageseeder.berlioz.content.ContentGenerator;
import org.pageseeder.berlioz.content.ContentRequest;
import org.pageseeder.bridge.berlioz.auth.AuthException;
import org.pageseeder.bridge.berlioz.auth.Authenticator;
import org.pageseeder.bridge.berlioz.auth.Sessions;
import org.pageseeder.bridge.berlioz.auth.User;
import org.pageseeder.bridge.berlioz.config.Configuration;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * A generator to Logout.
 *
 * <p>This generator invalidates the session and logs the user out of PageSeeder.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public final class LogoutGenerator implements ContentGenerator {

  @Override
  public void process(ContentRequest req, XMLWriter xml) throws BerliozException, IOException {

    // Get the authenticator
    HttpSession session = req.getSession();

    // Already logged in?
    User user = Sessions.getUser(session);
    if (user != null) {
      try {
        Authenticator<?> authenticator = Configuration.getAuthenticator();
        authenticator.logoutUser(user);
      } catch (AuthException ex) {
        throw new BerliozException("Unable to log user out", ex);
      }
      session.invalidate();
    }

    // XML
    xml.openElement("logout");
    xml.attribute("status", "ok");
    xml.closeElement();
  }

}
