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
package org.pageseeder.bridge.xml.sax;

import org.pageseeder.bridge.core.Member;
import org.pageseeder.bridge.xml.BasicHandler;
import org.xml.sax.Attributes;

/**
 * Handler for PageSeeder members.
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class MemberHandler extends BasicHandler<Member> {

  @Override
  public void startElement(String element, Attributes attributes) {
    if ("member".equals(element)) {
      Long id = getLong(attributes, "id");
      String username = getString(attributes, "username");
      String firstname = getString(attributes, "firstname");
      String surname = getString(attributes, "surname");
      String email = getString(attributes, "email", "");
      String status = getOptionalString(attributes, "status");
      boolean locked = "true".equals(getOptionalString(attributes, "locked"));
      boolean onVacation = "true".equals(getOptionalString(attributes, "onvacation"));
      boolean attachments = "true".equals(getOptionalString(attributes, "attachments"));

      Member member = new Member.Builder()
          .id(id)
          .username(username)
          .firstname(firstname)
          .surname(surname)
          .email(email)
          .status(status)
          .build();

      // these flags aren't frequent so it's OK to create new instances when true
      if (locked) member = member.lock();
      if (onVacation) member = member.isOnVacation(true);
      if (attachments) member = member.hasAttachments(true);

      // TODO External ID

      add(member);
    }
  }

}
