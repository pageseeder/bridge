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
package org.pageseeder.bridge.berlioz.auth;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * An immutable representation of a PageSeeder User associating, member details, roles and a session.
 *
 * <p>It is constructed from the group memberships of the member at login.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 *
 * @deprecated Use `OAuthUser` instead.
 */
@Deprecated
public final class PSUser implements User {

  /** As per requirement for the {@link Serializable} interface. */
  private static final long serialVersionUID = 1L;

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

  /**
   * The list of roles for this user - never expose the array publicly.
   */
  private final String[] roles;

  /**
   * The Member's PageSeeder session.
   */
  private @Nullable PSSession session = null;

  /**
   * Creates a new PageSeeder User.
   *
   * @param member  The PageSeeder member
   * @param session The session
   * @param roles   The list of roles for this user.
   */
  public PSUser(PSMember member, PSSession session, List<String> roles) {
    this.id = member.getId();
    this.email = member.getEmail();
    this.firstname = member.getFirstname();
    this.surname = member.getSurname();
    this.username = member.getUsername();
    this.session = session;
    this.roles = roles.toArray(new String[]{});
  }

  /**
   * @return The PageSeeder Member ID of this user.
   */
  public final Long id() {
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
   * Indicates whether the user is a member of the specified group.
   *
   * @param group The group to check membership of.
   * @return the group the user is a member of.
   */
  @Override
  public boolean hasRole(String group) {
    if (this.roles == null) return false;
    for (String g : this.roles) {
      if (g.equals(group)) return true;
    }
    return false;
  }

  /**
   * @return the PageSeeder username for this user.
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * @return the ID of this user session in PageSeeder (changes after each login)
   */
  public String getJSessionId() {
    return this.session.getJSessionId();
  }

  /**
   * Return the PageSeeder session for this user.
   *
   * @return the last connected time stamp.
   */
  public @Nullable PSSession getSession() {
    return this.session;
  }

  /**
   * @return the groups the user is a member of.
   */
  public List<String> listRoles() {
    return Arrays.asList(this.roles);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.email == null) ? 0 : this.email.hashCode());
    result = prime * result + ((this.firstname == null) ? 0 : this.firstname.hashCode());
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    result = prime * result + Arrays.hashCode(this.roles);
    result = prime * result + ((this.surname == null) ? 0 : this.surname.hashCode());
    result = prime * result + ((this.username == null) ? 0 : this.username.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PSUser other = (PSUser)obj;
    if (this.email == null) {
      if (other.email != null) return false;
    } else if (!this.email.equals(other.email)) return false;
    if (this.firstname == null) {
      if (other.firstname != null) return false;
    } else if (!this.firstname.equals(other.firstname)) return false;
    if (this.id == null) {
      if (other.id != null) return false;
    } else if (!this.id.equals(other.id)) return false;
    if (!Arrays.equals(this.roles, other.roles)) return false;
    if (this.surname == null) {
      if (other.surname != null) return false;
    } else if (!this.surname.equals(other.surname)) return false;
    if (this.username == null) {
      if (other.username != null) return false;
    } else if (!this.username.equals(other.username)) return false;
    return true;
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

  // Others
  // ----------------------------------------------------------------------------------------------

  /**
   * A PageSeeder User as XML.
   *
   * <p>Note: The password is never included.
   *
   * <pre>{@code
   *  <user type="pageseeder">
   *    <id>[memberid]</id>
   *    <username>[memberusername]</username>
   *    <firstname>[memberfirstname]</firstname>
   *    <surname>[membersurname]</surname>
   *    <email>[memberemail]</email>
   *    <member-of groups="[group0],[group1]"/>
   *  </user>
   * }</pre>
   *
   * {@inheritDoc}
   */
  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("user");
    xml.attribute("type", "pageseeder");
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
    if (this.roles != null) {

      // old format
      String[] roles = this.roles;
      xml.openElement("member-of");
      StringBuilder csv = new StringBuilder();
      for (String role : roles) {
        if (csv.length() > 0) {
          csv.append(',');
        }
        csv.append(role);
      }
      xml.attribute("groups", csv.toString());
      xml.closeElement();

      // New format
      xml.openElement("roles");
      for (String role : roles) {
        xml.openElement("role");
        xml.attribute("name", role);
        xml.closeElement();
      }
      xml.closeElement();
    }
    xml.closeElement();
  }

  /**
   * Builder for users.
   *
   * @author Christophe Lauret
   */
  public static class Builder {

    /**
     * The PagerSeeder Member instance from login.
     */
    private @Nullable PSMember member;

    /**
     * The PagerSeeder session
     */
    private @Nullable PSSession session;

    /**
     * The roles of this user.
     */
    private List<String> roles = new ArrayList<>();

    /**
     * Sets the member.
     *
     * @param member The member
     *
     * @return this builder
     */
    public Builder member(PSMember member) {
      this.member = member;
      return this;
    }

    /**
     * Sets the session.
     *
     * @param session The PageSeeder session.
     *
     * @return this builder
     */
    public Builder session(PSSession session) {
      this.session = session;
      return this;
    }

    /**
     * Adds a role to this user.
     *
     * @param role The role to add
     *
     * @return this builder
     */
    public Builder addRole(String role) {
      this.roles.add(role);
      return this;
    }

    /**
     * Builds the user.
     *
     * @return an immutable instance
     */
    public PSUser build() {
      return new PSUser(this.member, this.session, this.roles);
    }

  }

}
