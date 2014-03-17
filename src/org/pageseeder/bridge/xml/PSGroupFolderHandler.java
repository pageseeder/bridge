/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.xml;

import org.pageseeder.bridge.model.PSGroupFolder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Handler for group folders.
 *
 * @author Christophe Lauret
 * @version 0.2.2
 * @since 0.1.0
 */
public class PSGroupFolderHandler extends PSEntityHandler<PSGroupFolder> {

  /**
   * Create a new handler.
   */
  public PSGroupFolderHandler() {
  }

  /**
   * Create a new handler for group folders.
   *
   * @param folder The group folder instance to update
   */
  public PSGroupFolderHandler(PSGroupFolder folder) {
    super(folder);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("groupfolder".equals(localName)) {
      this.current = make(atts, this.current);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("groupfolder".equals(localName) && this.current != null) {
      this._items.add(current());
    }
  }

  @Override
  public PSGroupFolder make(Attributes atts, PSGroupFolder entity) {
    return PSEntityFactory.toGroupFolder(atts, entity);
  }
}
