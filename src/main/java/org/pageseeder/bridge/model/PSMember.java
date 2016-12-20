/*
 * Copyright 2015 Allette Systems (Australia)
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
package org.pageseeder.bridge.model;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
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
  private @Nullable Long id;

  /** The username of the member. */
  private @Nullable String username;

  /** The first name of the member. */
  private @Nullable String firstname;

  /** The last name of the member. */
  private @Nullable String surname;

  /** The email address of the member. */
  private @Nullable String email;

  /** The member status. */
  private @Nullable PSMemberStatus status;

  /**
   * Construct a new member without an ID or username.
   */
  public PSMember() {
  }

  /**
   * Construct a new member with the specified ID.
   *
   * @param id The ID of the member.
   */
  public PSMember(Long id) {
    this.id = id;
  }

  /**
   * Construct a new member with the specified username.
   *
   * @param username The username of the member.
   */
  public PSMember(String username) {
    this.username = username;
  }

  @Override
  public @Nullable Long getId() {
    return this.id;
  }

  @Override
  public @Nullable String getKey() {
    return this.username;
  }

  @Override
  public boolean isIdentifiable() {
    return this.id != null || this.username != null;
  }

  @Override
  public @Nullable String getIdentifier() {
    return Objects.toString(this.id, this.username);
  }

  /**
   * @return the firstname
   */
  public @Nullable String getFirstname() {
    return this.firstname;
  }

  /**
   * @return the surname
   */
  public @Nullable String getSurname() {
    return this.surname;
  }

  /**
   * @return the username
   */
  public @Nullable String getUsername() {
    return this.username;
  }

  /**
   * @return the email
   */
  public @Nullable String getEmail() {
    return this.email;
  }

  /**
   * @return the member status.
   */
  public @Nullable PSMemberStatus getStatus() {
    return this.status;
  }

  /**
   * @return the activated
   */
  public boolean isActivated() {
    return this.status == PSMemberStatus.activated;
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
  public void setUsername(@Nullable String username) {
    this.username = username;
  }

  /**
   * @param firstname the firstname to set
   */
  public void setFirstname(@Nullable String firstname) {
    this.firstname = firstname;
  }

  /**
   * @param surname the surname to set
   */
  public void setSurname(@Nullable String surname) {
    this.surname = surname;
  }

  /**
   * @param activated the activated to set
   */
  @Deprecated
  public void setActivated(boolean activated) {
    this.status = activated? PSMemberStatus.activated : null;
  }

  /**
   * @param email the email to set
   */
  public void setEmail(@Nullable String email) {
    this.email = email;
  }

  /**
   * @param status The status of the user
   */
  public void setStatus(@Nullable PSMemberStatus status) {
    this.status = status;
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
   *
   * {@inheritDoc}
   */
  @Override
  public EntityValidity checkValid() {
    if (checkMaxLength(this.firstname, 50)) return EntityValidity.MEMBER_FIRSTNAME_IS_TOO_LONG;
    if (checkMaxLength(this.surname, 50)) return EntityValidity.MEMBER_SURNAME_IS_TOO_LONG;
    if (checkMaxLength(this.username, 100)) return EntityValidity.MEMBER_USERNAME_IS_TOO_LONG;
    if (checkMaxLength(this.email, 100)) return EntityValidity.MEMBER_EMAIL_IS_TOO_LONG;
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

  private static boolean checkMaxLength(@Nullable String s, int length) {
    return s != null && s.length() > length;
  }
}
