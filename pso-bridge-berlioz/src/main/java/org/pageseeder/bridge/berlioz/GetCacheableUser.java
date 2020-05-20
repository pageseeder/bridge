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

import org.pageseeder.berlioz.BerliozException;
import org.pageseeder.berlioz.content.Cacheable;
import org.pageseeder.berlioz.content.ContentGenerator;
import org.pageseeder.berlioz.content.ContentRequest;
import org.pageseeder.bridge.berlioz.auth.AuthSessions;
import org.pageseeder.bridge.berlioz.auth.User;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * Returns the XML for the user currently logged in.
 *
 * <p>This generator is cacheable.
 *
 * @version 0.9.9
 * @since 0.1.0
 */
public final class GetCacheableUser implements ContentGenerator, Cacheable {

  @Override
  public String getETag(ContentRequest req) {
    User user = AuthSessions.getUser(req);
    if (user != null) return user.getName();
    else return "*";
  }

  @Override
  public final void process(ContentRequest req, XMLWriter xml) throws IOException {
    User user = AuthSessions.getUser(req);
    if (user != null) {
      user.toXML(xml);
    } else {
      xml.emptyElement("no-user");
    }
  }

}
