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

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.util.ISO8601;

/**
 * Options for resetting the password.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.3.0
 */
public final class PasswordResetOptions {

  /**
   * The key
   */
  private @Nullable String key;

  /**
   * The new password.
   */
  private @Nullable String password;

  /**
   * The significant date.
   */
  @Deprecated
  private @Nullable Date significantDate;

  /**
   * @return the key
   */
  public @Nullable String getKey() {
    return this.key;
  }

  /**
   * @return the password
   */
  public @Nullable String getPassword() {
    return this.password;
  }

  /**
   * @param key the key to set
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return the significantDate
   *
   * @deprecated The significant date is no longer supported in PageSeeder.
   */
  @Deprecated
  public @Nullable Date getSignificantDate() {
    return this.significantDate;
  }

  /**
   * @return the significant date as a string
   *
   * @deprecated The significant date is no longer supported in PageSeeder.
   */
  @Deprecated
  public @Nullable String getSignificantDateAsString() {
    Date d = this.significantDate;
    return d != null? ISO8601.CALENDAR_DATE.format(d.getTime()) : null;
  }

  /**
   * @param significantDate the significant date to set
   *
   * @deprecated The significant date is no longer supported in PageSeeder
   */
  @Deprecated
  public void setSignificantDate(Date date) {
    this.significantDate = date;
  }

}
