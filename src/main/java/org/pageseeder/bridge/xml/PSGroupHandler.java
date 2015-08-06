/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.xml;

import org.pageseeder.bridge.model.PSGroup;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Handler for PageSeeder groups and projects.
 *
 * @author Christophe Lauret
 * @version 0.2.2
 * @since 0.1.0
 */
public final class PSGroupHandler extends PSEntityHandler<PSGroup> {

  private PSGroup tempGroup = null;

  /**
   * A new handler to fill up the values of an incomplete group (or project).
   */
  public PSGroupHandler() {
  }

  /**
   * A new handler to fill up the values of an incomplete group (or project).
   *
   * @param group the group or project to update
   */
  public PSGroupHandler(PSGroup group) {
    super(group);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("group".equals(localName)) {
      this.tempGroup = make(atts, this.current);
    } else if ("project".equals(localName)) {
      this.tempGroup = PSEntityFactory.toProject(atts, this.current);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (("group".equals(localName) || "project".equals(localName)) && this.tempGroup != null) {
      this._items.add(this.tempGroup);
      this.tempGroup = null;
    }
  }

  @Override
  public PSGroup make(Attributes atts, PSGroup entity) {
    return PSEntityFactory.toGroup(atts, entity);
  }
}
