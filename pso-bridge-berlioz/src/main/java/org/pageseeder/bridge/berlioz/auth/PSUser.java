/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.auth;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 */
public final class PSUser implements User {

  /** As per requirement for the {@link Serializable} interface. */
  private static final long serialVersionUID = 1L;

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

  /**
   * The list of roles for this user - never expose the array publicly.
   */
  private final String[] _roles;

  /**
   * The Member's PageSeeder session.
   */
  private PSSession _session = null;

  /**
   * Creates a new PageSeeder User.
   * @param id the ID of the user in PageSeeder.
   */
  public PSUser(PSMember member, PSSession session, List<String> roles) {
    this._id = member.getId();
    this._email = member.getEmail();
    this._firstname = member.getFirstname();
    this._surname = member.getSurname();
    this._username = member.getUsername();
    this._session = session;
    this._roles = roles.toArray(new String[]{});;
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
   * Indicates whether the user is a member of the specified group.
   *
   * @param group The group to check membership of.
   * @return the group the user is a member of.
   */
  @Override
  public boolean hasRole(String group) {
    if (this._roles == null) return false;
    for (String g : this._roles) {
      if (g.equals(group)) return true;
    }
    return false;
  }

  /**
   * @return the PageSeeder username for this user.
   */
  public String getUsername() {
    return this._username;
  }

  /**
   * @return the ID of this user session in PageSeeder (changes after each login)
   */
  public String getJSessionId() {
    return this._session.getJSessionId();
  }

  /**
   * Return the PageSeeder session for this user.
   *
   * @return the last connected time stamp.
   */
  public PSSession getSession() {
    return this._session;
  }

  /**
   * @return the groups the user is a member of.
   */
  public List<String> listRoles() {
    return Arrays.asList(this._roles);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this._email == null) ? 0 : this._email.hashCode());
    result = prime * result + ((this._firstname == null) ? 0 : this._firstname.hashCode());
    result = prime * result + ((this._id == null) ? 0 : this._id.hashCode());
    result = prime * result + Arrays.hashCode(this._roles);
    result = prime * result + ((this._surname == null) ? 0 : this._surname.hashCode());
    result = prime * result + ((this._username == null) ? 0 : this._username.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PSUser other = (PSUser)obj;
    if (this._email == null) {
      if (other._email != null) return false;
    } else if (!this._email.equals(other._email)) return false;
    if (this._firstname == null) {
      if (other._firstname != null) return false;
    } else if (!this._firstname.equals(other._firstname)) return false;
    if (this._id == null) {
      if (other._id != null) return false;
    } else if (!this._id.equals(other._id)) return false;
    if (!Arrays.equals(this._roles, other._roles)) return false;
    if (this._surname == null) {
      if (other._surname != null) return false;
    } else if (!this._surname.equals(other._surname)) return false;
    if (this._username == null) {
      if (other._username != null) return false;
    } else if (!this._username.equals(other._username)) return false;
    return true;
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

  // Others
  // ----------------------------------------------------------------------------------------------

  /**
   * A PageSeeder User as XML.
   *
   * <p>Note: The password is never included.
   *
   * <pre>{@code
   *  <user type="pageseeder">
   *    <id>[member_id]</id>
   *    <username>[member_username]</username>
   *    <firstname>[member_firstname]</firstname>
   *    <surname>[member_surname]</surname>
   *    <email>[member_email]</email>
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
    if (this._roles != null) {

      // old format
      String[] roles = this._roles;
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
    private PSMember member;

    /**
     * The PagerSeeder session
     */
    private PSSession session;

    /**
     * The roles of this user.
     */
    private List<String> roles = new ArrayList<String>();

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
