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

import java.io.File;
import java.io.IOException;

import org.pageseeder.berlioz.BerliozException;
import org.pageseeder.berlioz.GlobalSettings;
import org.pageseeder.berlioz.content.ContentGenerator;
import org.pageseeder.berlioz.content.ContentRequest;
import org.pageseeder.bridge.berlioz.setup.Setup;
import org.pageseeder.bridge.berlioz.setup.SetupException;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * Executes the PageSeeder setup script located in <code>WEB-INF/setup/setup.xml</code>.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.4
 * @since 0.1.4
 */
public final class SetupPageSeeder implements ContentGenerator {

  @Override
  public void process(ContentRequest req, XMLWriter xml) throws BerliozException, IOException {
    boolean simulate = "true".equals(req.getParameter("simulate"));
    try {
      File r = GlobalSettings.getRepository();
      File config = new File(r, "setup/setup.xml");
      Setup setup = Setup.parse(config);
      if (simulate) {
        setup.simulate(xml);
      } else {
        setup.execute(xml);
      }
    } catch (SetupException ex) {
      throw new BerliozException("Unable to setup application", ex);
    }
  }

}
