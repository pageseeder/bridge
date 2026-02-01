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

import org.jspecify.annotations.Nullable;
import org.pageseeder.bridge.model.PSGroup;
import org.xml.sax.Attributes;

/**
 * Handler for PageSeeder groups and projects.
 *
 * @author Christophe Lauret
 * @version 0.2.2
 * @since 0.1.0
 */
public final class PSGroupHandler extends PSEntityHandler<PSGroup> {

  private @Nullable PSGroup tempGroup = null;

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
  public void startElement(String uri, String localName, String qName, Attributes atts) {
    if ("group".equals(localName)) {
      // save parent project (for tree)
      PSGroup tmp = this.tempGroup;
      if (tmp != null) {
        this._items.add(tmp);
      }
      this.tempGroup = make(atts, this.current);
    } else if ("project".equals(localName)) {
      // save parent project (for tree)
      PSGroup tmp = this.tempGroup;
      if (tmp != null) {
        this._items.add(tmp);
      }
      this.tempGroup = PSEntityFactory.toProject(atts, this.current);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) {
    if ("group".equals(localName) || "project".equals(localName)) {
      PSGroup tmp = this.tempGroup;
      if (tmp != null) {
        this._items.add(tmp);
        this.tempGroup = null;
      }
    }
  }

  @Override
  public PSGroup make(Attributes atts, @Nullable PSGroup entity) {
    return PSEntityFactory.toGroup(atts, entity);
  }
}
