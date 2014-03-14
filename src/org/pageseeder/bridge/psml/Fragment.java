/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.psml;

import java.io.IOException;

import com.topologi.diffx.xml.XMLWriter;

/**
 * A simple PSML fragment.
 *
 * DO NOT USE THIS CLASS IS INCOMPLETE
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
