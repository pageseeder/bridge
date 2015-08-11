/*
 * Copyright 2015 Allette Systems (Australia)
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
package org.pageseeder.bridge.psml;

import java.io.IOException;

import org.pageseeder.xmlwriter.XMLWriter;

/**
 * A simple PSML fragment.
 *
 * <p>IMPORTANT: DO NOT USE THIS CLASS IS INCOMPLETE
 *
 * @author Christophe Lauret
 */
public class Fragment extends FragmentBase implements PSMLFragment {

  /**
   * Creates a new fragment with the specified ID.
   *
   * @param id The fragment ID.
   */
  public Fragment(String id) {
    super(id);
  }

  /**
   * Creates a new fragment with the specified ID.
   *
   * @param id   The fragment ID.
   * @param type The fragment type.
   */
  public Fragment(String id, String type) {
    super(id, type);
  }

  @Override
  public void toXML(XMLWriter psml) throws IOException {
    psml.openElement("fragment", true);
    psml.attribute("id", id());
    if (type() != null) {
      psml.attribute("type", type());
    }
    // FIXME
    psml.closeElement();
  }

}
