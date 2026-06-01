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

import org.jspecify.annotations.Nullable;
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
  private final Long id;

  /**
   * The Member's email.
   */
  private final @Nullable String email;

  /**
   * The Member's first name.
   */
  private final @Nullable String firstname;

  /**
   * The Member's surname.
   */
  private final @Nullable String surname;

  /**
   * The Member's username.
   */
  private final String username;

  private PSToken token;

  public OAuthUser(PSMember member, PSToken token) {
    this.id = member.getId();
    this.firstname = member.getFirstname();
    this.surname = member.getSurname();
    this.email = member.getEmail();
    this.username = member.getUsername();
    this.token = token;
  }

  /**
   * @return The PageSeeder Member ID of this user.
   */
  public Long id() {
    return this.id;
  }

  /**
   * @return the PageSeeder email for this user.
   */
  public @Nullable String getEmail() {
    return this.email;
  }

  /**
   * @return the PageSeeder first name for this user.
   */
  public @Nullable String getFirstname() {
    return this.firstname;
  }

  /**
   * @return the PageSeeder surname for this user.
   */
  public @Nullable String getSurname() {
    return this.surname;
  }

  /**
   * @return same as username.
   */
  @Override
  public String getName() {
    return this.username;
  }

  /**
   * @return the PageSeeder username for this user.
   */
  public String getUsername() {
    return this.username;
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
    m.setId(this.id);
    m.setFirstname(this.firstname);
    m.setSurname(this.surname);
    m.setUsername(this.username);
    m.setEmail(this.email);
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
   *    <id>[memberid]</id>
   *    <username>[memberusername]</username>
   *    <firstname>[memberfirstname]</firstname>
   *    <surname>[membersurname]</surname>
   *    <email>[memberemail]</email>
   *  </user>
   * }</pre>
   *
   * {@inheritDoc}
   */
  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("user");
    xml.attribute("type", "oauth");
    xml.attribute("id", this.id.toString());
    if (this.username != null) {
      xml.element("username", this.username);
    }
    if (this.firstname != null) {
      xml.element("firstname", this.firstname);
    }
    if (this.surname != null) {
      xml.element("surname", this.surname);
    }
    if (this.email != null) {
      xml.element("email", this.email);
    }
    xml.closeElement();
  }

}