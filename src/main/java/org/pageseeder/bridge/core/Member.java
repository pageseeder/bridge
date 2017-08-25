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

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;

/**
 * A PageSeeder member.
 *
 * @author Christophe Lauret
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Member implements XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 2L;

  /** The PageSeeder database ID. */
  private final long _id;

  /** The username of the member. */
  private final Username _username;

  /** The email address of the member. */
  private final Email _email;

  /** The first name of the member. */
  private final String _firstname;

  /** The last name of the member. */
  private final String _surname;

  /** The member status. */
  private final MemberStatus _status;

  /** Whether the member is locked. */
  private final boolean _locked;

  /** Whether the member is on vacation */
  private final boolean _onVacation;

  /** Whether the member prefers receiving attachments. */
  private final boolean _attachments;

  /**
   * Create a new member.
   *
   * @param id        The ID of the member.
   * @param username  The username of the member.
   * @param email     The email of the member
   * @param firstname The first name of the member
   * @param surname   The surname of the member
   * @param status    The status of the member
   */
  public Member(long id, Username username, Email email, String firstname, String surname, MemberStatus status, boolean locked, boolean onVacation, boolean attachments) {
    this._id = id;
    this._username = username;
    this._email = email;
    this._firstname = firstname;
    this._surname = surname;
    this._status = status;
    this._locked = locked;
    this._onVacation = onVacation;
    this._attachments = attachments;
  }

  /**
   * Create a new member.
   *
   * @param id        The ID of the member.
   * @param username  The username of the member.
   * @param email     The email of the member
   * @param firstname The first name of the member
   * @param surname   The surname of the member
   * @param status    The status of the member
   */
  public Member(long id, Username username, Email email, String firstname, String surname, MemberStatus status) {
    this._id = id;
    this._username = username;
    this._email = email;
    this._firstname = firstname;
    this._surname = surname;
    this._status = status;
    this._locked = false;
    this._onVacation = false;
    this._attachments = false;
  }

  /**
   * @return The PageSeeder database ID or -1 if unknown or new member
   */
  public long getId() {
    return this._id;
  }

  /**
   * @return the firstname
   */
  public String getFirstname() {
    return this._firstname;
  }

  /**
   * @return the surname
   */
  public String getSurname() {
    return this._surname;
  }

  /**
   * @return the username
   */
  public Username getUsername() {
    return this._username;
  }

  public String getFullname() {
    return this._firstname+" "+this._surname;
  }

  /**
   * @return the email
   */
  public @Nullable Email getEmail() {
    return this._email;
  }

  /**
   * @return the member status.
   */
  public @Nullable MemberStatus getStatus() {
    return this._status;
  }

  /**
   * @return true if the member status is `activated`
   */
  public boolean isActivated() {
    return this._status == MemberStatus.activated;
  }

  /**
   * @return true if the member is locked.
   */
  public boolean isLocked() {
    return this._locked;
  }

  /**
   * @return true if the member status is on vacation.
   */
  public boolean isOnVacation() {
    return this._onVacation;
  }

  /**
   * @return true if the member prefers receiving attachments
   */
  public boolean hasAttachments() {
    return this._attachments;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("member");
    toXMLAttributes(xml);
    xml.element("fullname", getFullname());
    xml.closeElement();
  }

  /**
   * Writes the attribute for the member
   *
   * @param xml XML Writer
   *
   * @throws IOException if reported by the writer
   */
  public void toXMLAttributes(XMLWriter xml) throws IOException {
    if (this._id > 0) {
      xml.attribute("id", Long.toString(this._id));
    }
    xml.attribute("username", this._username.toString());
    if (!this._email.equals(Email.NO_EMAIL)) {
      xml.attribute("email", this._email.toString());
    }
    xml.attribute("firstname", this._firstname);
    xml.attribute("surname", this._surname);
    if (this._status != MemberStatus.unknown) {
      xml.attribute("status", this._status.toString());
    }
    if (this._locked) {
      xml.attribute("locked", "true");
    }
    if (this._onVacation) {
      xml.attribute("onvacation", "true");
    }
    if (this._attachments) {
      xml.attribute("attachments", "true");
    }
  }

  /**
   * A convenience method to return a copy of this member with the specified status.
   *
   * @return a new member with the specified status if the status if different from that of the current member
   */
  public Member status(MemberStatus status) {
    if (status == this._status) return this;
    return new Member(this._id, this._username, this._email, this._firstname, this._surname, status);
  }

  /**
   * A convenience method to return a copy of this member with the specified status.
   *
   * @return a new member with the specified status if the status if different from that of the current member
   */
  public Member lock() {
    if (this._locked) return this;
    return new Member(this._id, this._username, this._email, this._firstname, this._surname, this._status, true, this._onVacation, this._attachments);
  }

  /**
   * A convenience method to return a copy of this member with the specified status.
   *
   * @return a new member with the specified status if the status if different from that of the current member
   */
  public Member unlock() {
    if (!this._locked) return this;
    return new Member(this._id, this._username, this._email, this._firstname, this._surname, this._status, false, this._onVacation, this._attachments);
  }

  /**
   * A convenience method to return a copy of this member with the specified status.
   *
   * @return a new member with the specified status if the status if different from that of the current member
   */
  public Member isOnVacation(boolean yes) {
    if (yes == this._onVacation) return this;
    return new Member(this._id, this._username, this._email, this._firstname, this._surname, this._status, this._locked, yes, this._attachments);
  }

  /**
   * A convenience method to return a copy of this member with the specified status.
   *
   * @return a new member with the specified status if the status if different from that of the current member
   */
  public Member hasAttachments(boolean yes) {
    if (yes == this._attachments) return this;
    return new Member(this._id, this._username, this._email, this._firstname, this._surname, this._status, this._locked, this._onVacation, yes);
  }

  @Override
  public String toString() {
    return "Member("+this._id+":"+this._username+")";
  }

  public static class Builder {

    private long id;
    private Username username;
    private Email email = Email.NO_EMAIL;
    private String firstname = "";
    private String surname = "";
    private MemberStatus status = MemberStatus.unknown;
    private boolean locked;
    private boolean onVacation;
    private boolean attachments;

    /**
     * @param id the id to set
     *
     * @return this builder
     */
    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    /**
     * @param username the username to set
     *
     * @return this builder
     */
    public Builder username(String username) {
      this.username = new Username(username);
      return this;
    }

    /**
     * @param username the username to set
     *
     * @return this builder
     */
    public Builder username(Username username) {
      this.username = username;
      return this;
    }

    /**
     * @param firstname the firstname to set
     *
     * @return this builder
     */
    public Builder firstname(String firstname) {
      this.firstname = firstname;
      return this;
    }

    /**
     * @param surname the surname to set
     */
    public Builder surname(String surname) {
      this.surname = surname;
      return this;
    }

    /**
     * @param email the email to set
     */
    public Builder email(String email) {
      this.email = new Email(email);
      return this;
    }

    /**
     * @param email the email to set
     */
    public Builder email(Email email) {
      this.email = email;
      return this;
    }

    /**
     * @param status The status of the user
     */
    public Builder status(MemberStatus status) {
      this.status = status;
      return this;
    }

    /**
     * @param status The status of the user
     */
    public Builder status(String status) {
      this.status = MemberStatus.forAttribute(status);
      return this;
    }

    /**
     * @param yes the flag
     */
    public Builder locked(boolean yes) {
      this.locked = yes;
      return this;
    }

    /**
     * @param yes the flag
     */
    public Builder onVacation(boolean yes) {
      this.onVacation = yes;
      return this;
    }

    /**
     * @param yes the flag
     */
    public Builder attachments(boolean yes) {
      this.attachments = yes;
      return this;
    }

    public Member build() {
      return new Member(this.id, this.username, this.email, this.firstname, this.surname, this.status, this.locked, this.onVacation, this.attachments);
    }

  }

}
