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
