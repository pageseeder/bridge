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

import org.jspecify.annotations.Nullable;
import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

/**
 * The author of a comment.
 *
 * <p>Either the name or the member will be non-null.
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Author implements Serializable, XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * The name of the author (required) yes string
   */
  private final @Nullable String _name;

  /**
   * When the author is a member
   */
  private final @Nullable Member _member;

  /**
   * The email of the author
   */
  private final @Nullable Email _email;

  /**
   * Set the author name and email for when the author is not a PageSeeder member.
   *
   * @param name  The name of the author (required)
   * @param email The email of the author
   */
  public Author(String name, @Nullable Email email) {
    this._name = Objects.requireNonNull(name,"The name is required");
    this._member = null;
    this._email = email;
  }

  /**
   * Set the author as a PageSeeder member.
   *
   * <p>The member must be identifiable and exist in PageSeeder.
   *
   * @param member the member.
   */
  public Author(Member member) {
    this._member = Objects.requireNonNull(member, "the member is required");
    this._name = null;
    this._email = member.getEmail();
  }

  /**
   * @return the member or <code>null</code> if the author is not specified or an external user.
   */
  public @Nullable Member getMember() {
    return this._member;
  }

  /**
   * Returns the name for when the author is an external user.
   *
   * @return the email of the external user
   */
  public @Nullable String getName() {
    return this._name;
  }

  /**
   * Returns the email for when the author is an external user.
   *
   * @return the email of the external user
   */
  public @Nullable Email getEmail() {
    return this._email;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("author");

    // TODO
//    attachments	boolean	no	If the user receives email attachments
//    email	email	no	The email address of the member
//    externalid	xs:string	no	An external identifier for the member
//    firstname	xs:string	if member	The first name of the member
//    id	xs:long	if member	The ID of the member in PageSeeder
//    locked	boolean	no	If the member account is locked
//    onvacation	boolean	no	If the member email option is set to 'on vacation'
//    status	enum	if member	The current status of the member
//    surname	xs:string	if member	The surname of the member
//    username	xs:string	if member	The username of the member
    xml.closeElement();
  }
}
