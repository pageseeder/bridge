/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.auth;

/**
 * An enumeration for the results of authentication methods such as login and logout.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public enum AuthenticationResult {

  /**
   * The login succeeded and resulted in the user being logged in.
   */
  LOGGED_IN,

  /**
   * The user was already logged in.
   */
  ALREADY_LOGGED_IN,

  /**
   * The login could not proceed because the user provided insufficient details.
   */
  INSUFFICIENT_DETAILS,

  /**
   * The login failed because the user provided incorrect details.
   */
  INCORRECT_DETAILS,

  /**
   * The user has been logged out.
   */
  LOGGED_OUT,

  /**
   * The user was already logged out.
   */
  ALREADY_LOGGED_OUT,

  /**
   * The user account is locked.
   */
  ACCOUNT_LOCKED;

}
