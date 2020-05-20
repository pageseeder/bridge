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
import java.util.Objects;

/**
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class AssignedTo implements Serializable, XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  private final OffsetDateTime _date;

  private final Member _member;

  /**
   * @param member The member the task is assigned to.
   * @param date   The date the task was assigned to.
   */
  public AssignedTo(Member member, OffsetDateTime date) {
    this._member = Objects.requireNonNull(member, "Member is required");
    this._date = Objects.requireNonNull(date, "Date is required");
  }

  /**
   * @return The member the task is assigned to.
   */
  public Member getMember() {
    return this._member;
  }

  /**
   * @return The date the task was assigned to.
   */
  public OffsetDateTime getDate() {
    return this._date;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("assignedto");
    xml.attribute("date", this._date.toString()); // TODO date formatting
    this._member.toXMLAttributes(xml);
    xml.element("fullname", this._member.getFirstname()+" "+this._member.getSurname());
    xml.closeElement();
  }

}
