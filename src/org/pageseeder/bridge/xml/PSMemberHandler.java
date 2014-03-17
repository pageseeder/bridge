/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.xml;

import org.pageseeder.bridge.model.PSMember;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author Christophe Lauret
 * @version 0.2.2
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
    if ("member".equals(localName) && this.current != null) {
      this._items.add(this.current);
      this.current = null;
    }
  }

  @Override
  public PSMember make(Attributes atts, PSMember entity) {
    return PSEntityFactory.toMember(atts, entity);
  }

}
