/*
 * Copyright (c) 1999-2012 weborganic systems pty. ltd.
 */
package org.pageseeder.bridge.model;

import java.util.Date;

import org.pageseeder.berlioz.util.ISO8601;

/**
 * Options for resetting the password.
 *
 * @author Christophe Lauret
 * @version 0.3.0
 * @since 0.3.0
 */
public final class PasswordResetOptions {

  /**
   * The key
   */
  private String key;

  /**
   * The significant date.
   */
  private Date significantDate;

  /**
   * The new password.
   */
  private String password;

  /**
   * @return the key
   */
  public String getKey() {
    return this.key;
  }

  /**
   * @return the significantDate
   */
  public Date getSignificantDate() {
    return this.significantDate;
  }

  /**
   * @return the significantDate
   */
  public String getSignificantDateAsString() {
    return this.significantDate != null? ISO8601.CALENDAR_DATE.format(this.significantDate.getTime()) : null;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return this.password;
  }

  /**
   * @param key the key to set
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * @param significantDate the significantDate to set
   */
  public void setSignificantDate(Date significantDate) {
    this.significantDate = significantDate;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

}
