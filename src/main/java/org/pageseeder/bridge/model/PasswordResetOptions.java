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
