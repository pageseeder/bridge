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
package org.pageseeder.berlioz.bridge;

import java.io.IOException;

import org.pageseeder.berlioz.BerliozException;
import org.pageseeder.berlioz.content.ContentGenerator;
import org.pageseeder.berlioz.content.ContentRequest;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * A generator to Logout.
 *
 * <p>This generator invalidates the session and logs the user out of PageSeeder.
 *
 * @deprecated Use generator from `org.pageseeder.bridge.berlioz` package instead
 */
@Deprecated
public final class LogoutGenerator implements ContentGenerator {

  private final org.pageseeder.bridge.berlioz.LogoutGenerator _generator = new org.pageseeder.bridge.berlioz.LogoutGenerator();

  @Override
  public void process(ContentRequest req, XMLWriter xml) throws BerliozException, IOException {
    this._generator.process(req, xml);
  }

}
