/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSEntity;

/**
 * A PageSeeder member.
 *
 * <p>The public identifier for a member is the username.
 *
 * @author Christophe Lauret
 * @version 0.2.1
 * @since 0.2.0
 */
public final class PSMember implements PSEntity {

  /** As per recommendation */
  private static final long serialVersionUID = 2L;

  /** The PageSeeder database ID. */
  private Long id;

  /** The username of the member. */
  private String username;

  /** The first name of the member. */
  private String firstname;

  /** The last name of the member. */
  private String surname;

  /** The email address of the member. */
  private String email;

  /** Whether the member has been activated. */
  private boolean activated;

  @Override
  public Long getId() {
    return this.id;
  }

  @Override
  public String getKey() {
    return this.username;
  }

  @Override
  public boolean isIdentifiable() {
    return this.id != null || this.username != null;
  }

  @Override
  public String getIdentifier() {
    return this.id != null ? this.id.toString() : this.username;
  }

  /**
   * @return the firstname
   */
  public String getFirstname() {
    return this.firstname;
  }

  /**
   * @return the surname
   */
  public String getSurname() {
    return this.surname;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return this.email;
  }

  /**
   * @return the activated
   */
  public boolean isActivated() {
    return this.activated;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @param firstname the firstname to set
   */
  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  /**
   * @param surname the surname to set
   */
  public void setSurname(String surname) {
    this.surname = surname;
  }

  /**
   * @param activated the activated to set
   */
  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Known constraints on Member are based on SQL definition:
   *
   * <pre>
   *   FirstName VARCHAR(20) NULL
   *   Surname VARCHAR(20) NULL
   *   Email VARCHAR(100) NULL
   *   Username VARCHAR(100) NULL
   * </pre>
   */
  @Override
  public EntityValidity checkValid() {
    if (this.firstname != null && this.firstname.length() > 20) return EntityValidity.MEMBER_FIRSTNAME_IS_TOO_LONG;
    if (this.surname   != null && this.surname.length()   > 20) return EntityValidity.MEMBER_SURNAME_IS_TOO_LONG;
    if (this.username  != null && this.username.length()  > 100) return EntityValidity.MEMBER_USERNAME_IS_TOO_LONG;
    if (this.email     != null && this.email.length()     > 100) return EntityValidity.MEMBER_EMAIL_IS_TOO_LONG;
    return EntityValidity.OK;
  }

  @Override
  public boolean isValid() {
    return checkValid() == EntityValidity.OK;
  }

  @Override
  public String toString() {
    return "M("+this.id+":"+this.username+")";
  }
}
