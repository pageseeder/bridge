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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A PageSeeder member.
 *
 * @author Christophe Lauret
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Member implements Serializable, XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 3L;

  /** The PageSeeder database ID. */
  private final long id;

  /** The username of the member. */
  private final Username username;

  /** The email address of the member. */
  private final Email email;

  /** The first name of the member. */
  private final String firstname;

  /** The last name of the member. */
  private final String surname;

  /** The member status. */
  private final MemberStatus status;

  /** Whether the member is locked. */
  private final boolean locked;

  /** Whether the member is on vacation */
  private final boolean onVacation;

  /** Whether the member prefers receiving attachments. */
  private final boolean attachments;
  
  /**
   * The last time the member logged into pageseeder.
   */
  private final OffsetDateTime lastLogin;

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
  public Member(long id, Username username, Email email, String firstname, String surname, MemberStatus status, 
      boolean locked, boolean onVacation, boolean attachments, OffsetDateTime lastLogin) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.firstname = firstname;
    this.surname = surname;
    this.status = status;
    this.locked = locked;
    this.onVacation = onVacation;
    this.attachments = attachments;
    this.lastLogin = lastLogin;
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
    this.id = id;
    this.username = username;
    this.email = email;
    this.firstname = firstname;
    this.surname = surname;
    this.status = status;
    this.locked = false;
    this.onVacation = false;
    this.attachments = false;
    this.lastLogin = OffsetDateTime.MIN;
  }

  /**
   * @return The PageSeeder database ID or -1 if unknown or new member
   */
  public long getId() {
    return this.id;
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
  public Username getUsername() {
    return this.username;
  }

  public String getFullname() {
    return this.firstname +" "+this.surname;
  }

  /**
   * @return the email
   */
  public @Nullable Email getEmail() {
    return this.email;
  }

  /**
   * @return the member status.
   */
  public @Nullable MemberStatus getStatus() {
    return this.status;
  }

  /**
   * @return true if the member status is `activated`
   */
  public boolean isActivated() {
    return this.status == MemberStatus.activated;
  }

  /**
   * @return true if the member is locked.
   */
  public boolean isLocked() {
    return this.locked;
  }

  /**
   * @return true if the member status is on vacation.
   */
  public boolean isOnVacation() {
    return this.onVacation;
  }

  /**
   * @return true if the member prefers receiving attachments
   */
  public boolean hasAttachments() {
    return this.attachments;
  }
  
  /**
   * @return return the last login date and time  of this member (it could have null value). 
   */
  public @Nullable OffsetDateTime getLastLogin() {
    return lastLogin;
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
    if (this.id > 0) {
      xml.attribute("id", Long.toString(this.id));
    }
    xml.attribute("username", this.username.toString());
    if (!this.email.equals(Email.NO_EMAIL)) {
      xml.attribute("email", this.email.toString());
    }
    xml.attribute("firstname", this.firstname);
    xml.attribute("surname", this.surname);
    if (this.status != MemberStatus.unknown) {
      xml.attribute("status", this.status.toString());
    }
    if (this.locked) {
      xml.attribute("locked", "true");
    }
    if (this.onVacation) {
      xml.attribute("onvacation", "true");
    }
    if (this.attachments) {
      xml.attribute("attachments", "true");
    }
    if (this.lastLogin != null && !this.lastLogin.equals(OffsetDateTime.MIN)) {
      xml.attribute("lastlogin", this.lastLogin.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
    
  }

  /**
   * A convenience method to return a copy of this member with the specified status.
   *
   * @return a new member with the specified status if the status is different from that of the current member
   */
  public Member status(MemberStatus status) {
    if (status == this.status) return this;
    return new Member(this.id, this.username, this.email, this.firstname, this.surname, status);
  }

  /**
   * A convenience method to return a copy of this member with the specified status.
   *
   * @return a new member with the specified status if the status is different from that of the current member
   */
  public Member lock() {
    if (this.locked) return this;
    return new Member(this.id, this.username, this.email, this.firstname, this.surname, this.status, true, this.onVacation, this.attachments, this.lastLogin);
  }

  /**
   * A convenience method to return a copy of this member with the specified status.
   *
   * @return a new member with the specified status if the status is different from that of the current member
   */
  public Member unlock() {
    if (!this.locked) return this;
    return new Member(this.id, this.username, this.email, this.firstname, this.surname, this.status, false, this.onVacation, this.attachments, this.lastLogin);
  }

  /**
   * A convenience method to return a copy of this member with the specified status.
   *
   * @return a new member with the specified status if the status is different from that of the current member
   */
  public Member isOnVacation(boolean yes) {
    if (yes == this.onVacation) return this;
    return new Member(this.id, this.username, this.email, this.firstname, this.surname, this.status, this.locked, yes, this.attachments, this.lastLogin);
  }

  /**
   * A convenience method to return a copy of this member with the specified last login.
   *
   * @return a new member with the specified last login if the last login is different from that of the current member
   */
  public Member lastLogin(OffsetDateTime lastLogin ) {
    if (lastLogin.equals(this.lastLogin)) return this;
    return new Member(this.id, this.username, this.email, this.firstname, this.surname, this.status, this.locked, this.onVacation, this.attachments, lastLogin);
  }
  
  /**
   * A convenience method to return a copy of this member with the specified status.
   *
   * @return a new member with the specified status if the status if different from that of the current member
   */
  public Member hasAttachments(boolean yes) {
    if (yes == this.attachments) return this;
    return new Member(this.id, this.username, this.email, this.firstname, this.surname, this.status, this.locked, this.onVacation, yes, this.lastLogin);
  }

  @Override
  public String toString() {
    return "Member("+this.id +":"+this.username +")";
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
    private OffsetDateTime lastLogin = OffsetDateTime.MIN;

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

    public Builder lastLogin(OffsetDateTime lastLogin) {
      this.lastLogin = lastLogin;
      return this;
    }
    
    public Member build() {
      return new Member(this.id, this.username, this.email, this.firstname, this.surname, this.status, this.locked, this.onVacation, this.attachments, this.lastLogin);
    }

  }

}
