/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.net;

import java.nio.charset.StandardCharsets;

import javax.xml.bind.DatatypeConverter;

import org.pageseeder.bridge.PSCredentials;

/**
 * A username and password pair to authenticate a user on PageSeeder.
 *
 * @author Christophe Lauret
 *
 * @version 0.3.32
 * @since 0.3.32
 */
public final class UsernamePassword implements PSCredentials {

  /**
   * The user ID.
   */
  private final String _userid;

  /**
   * The password.
   */
  private final String _password;

  /**
   * Creates a new user id and password set of credentials.
   *
   * @param userid   The user id
   * @param password The password
   */
  public UsernamePassword(String userid, String password) {
    if (userid == null || password == null) throw new NullPointerException();
    this._userid = userid;
    this._password = password;
  }

  /**
   * @return The username (cannot be <code>null</code>).
   */
  public String username() {
    return this._userid;
  }

  /**
   * @return The password (cannot be <code>null</code>).
   */
  protected String password() {
    return this._password;
  }

  /**
   * @return The basic authorization string
   */
  public String toBasicAuthorization() {
    byte[] bc = (this._userid+":"+new String(this._password)).getBytes(StandardCharsets.UTF_8);
    return "Basic "+DatatypeConverter.printBase64Binary(bc);
  }
}
