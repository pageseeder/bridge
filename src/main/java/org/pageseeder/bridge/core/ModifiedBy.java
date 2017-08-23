/*
 * Copyright 2017 Allette Systems (Australia)
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

package org.pageseeder.bridge.core;

import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.io.Serializable;
import java.time.OffsetDateTime;

public final class ModifiedBy implements Serializable, XMLWritable {

  public final OffsetDateTime _date;

  public final Member _member;

  public ModifiedBy(Member member, OffsetDateTime date) {
    this._date = date;
    this._member = member;
  }

  public Member getMember() {
    return this._member;
  }

  public OffsetDateTime getDate() {
    return this._date;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("modifiedby");
    xml.attribute("date", this._date.toString()); // TODO date formatting
    this._member.toXMLAttributes(xml);
    xml.closeElement();
  }

}
