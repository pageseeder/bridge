/*
 * Copyright 2016 Allette Systems (Australia)
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
package org.pageseeder.bridge.berlioz.oauth;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.PSToken;
import org.pageseeder.bridge.berlioz.auth.User;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * A user connected to PageSeeder via OAuth
 *
 * @author Christophe Lauret
 *
 * @version 0.9.9
 * @since 0.9.9
 */
public final class OAuthUser implements User {

  /** As per requirement for Serializable */
  private static final long serialVersionUID = 20160812L;

  /**
   * The PagerSeeder Member ID.
   */
  private final Long _id;

  /**
   * The Member's email.
   */
  private final String _email;

  /**
   * The Member's first name.
   */
  private final String _firstname;

  /**
   * The Member's surname.
   */
  private final String _surname;

  /**
   * The Member's username.
   */
  private final String _username;

  private PSToken token;

  public OAuthUser(PSMember member, PSToken token) {
    this._id = member.getId();
    this._firstname = member.getFirstname();
    this._surname = member.getSurname();
    this._email = member.getEmail();
    this._username = member.getUsername();
    this.token = token;
  }

  /**
   * @return The PageSeeder Member ID of this user.
   */
  public final Long id() {
    return this._id;
  }

  /**
   * @return the PageSeeder email for this user.
   */
  public String getEmail() {
    return this._email;
  }

  /**
   * @return the PageSeeder first name for this user.
   */
  public String getFirstname() {
    return this._firstname;
  }

  /**
   * @return the PageSeeder surname for this user.
   */
  public String getSurname() {
    return this._surname;
  }

  /**
   * @return same as username.
   */
  @Override
  public String getName() {
    return this._username;
  }

  /**
   * @return the PageSeeder username for this user.
   */
  public String getUsername() {
    return this._username;
  }

  @Override
  public boolean hasRole(@Nullable String role) {
    return true;
  }

  public void setToken(PSToken token) {
    this.token = token;
  }

  public PSToken getToken() {
    return this.token;
  }

  /**
   * Returns the member instance of this user.
   *
   * @return this user as a new member instance.
   */
  public final PSMember toMember() {
    PSMember m = new PSMember();
    m.setId(this._id);
    m.setFirstname(this._firstname);
    m.setSurname(this._surname);
    m.setUsername(this._username);
    m.setEmail(this._email);
    return m;
  }

  @Override
  public String toString() {
    return getName();
  }

  /**
   * A PageSeeder User as XML.
   *
   * <p>Note: The password is never included.
   *
   * <pre>{@code
   *  <user type="oauth">
   *    <id>[member_id]</id>
   *    <username>[member_username]</username>
   *    <firstname>[member_firstname]</firstname>
   *    <surname>[member_surname]</surname>
   *    <email>[member_email]</email>
   *  </user>
   * }</pre>
   *
   * {@inheritDoc}
   */
  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("user");
    xml.attribute("type", "oauth");
    xml.attribute("id", this._id.toString());
    if (this._username != null) {
      xml.element("username", this._username);
    }
    if (this._firstname != null) {
      xml.element("firstname", this._firstname);
    }
    if (this._surname != null) {
      xml.element("surname", this._surname);
    }
    if (this._email != null) {
      xml.element("email", this._email);
    }
    xml.closeElement();
  }

}