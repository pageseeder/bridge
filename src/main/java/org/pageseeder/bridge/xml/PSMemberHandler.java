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

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.model.PSMember;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Handler for PageSeeder members.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.1.0
 */
public final class PSMemberHandler extends PSEntityHandler<PSMember> {

  /**
   * A new handler to extract new members only.
   */
  public PSMemberHandler() {
  }

  /**
   * A new handler to fill up the values of an incomplete member.
   *
   * @param member the member to update
   */
  public PSMemberHandler(PSMember member) {
    super(member);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("member".equals(localName)) {
      this.current = make(atts, this.current);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("member".equals(localName)) {
      PSMember m = this.current;
      if (m != null) {
        this._items.add(m);
        this.current = null;
      }
    }
  }

  @Override
  public PSMember make(Attributes atts, @Nullable PSMember entity) {
    return PSEntityFactory.toMember(atts, entity);
  }

}
