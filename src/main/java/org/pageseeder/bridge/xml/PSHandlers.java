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
package org.pageseeder.bridge.xml;

import java.text.ParseException;
import java.util.Date;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.model.PSNotification;
import org.pageseeder.bridge.model.PSRole;
import org.pageseeder.bridge.util.ISO8601;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of utility functions for PageSeeder handler.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public final class PSHandlers {

  /**
   * To report suspicious values.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(PSHandlers.class);

  /** Utility class. */
  private PSHandlers() {
  }

  /**
   * Parse the date as an ISO8601 date time and return the corresponding <code>Date</code>.
   *
   * @param date the date as 'YYYY-MM-DDThh:mm:ss' with optional timezone component.
   * @return the corresponding Date value or <code>null</code>
   */
  public static @Nullable Date datetime(@Nullable String date) {
    if (date == null) return null;
    try {
      return ISO8601.parseAuto(date);
    } catch (ParseException ex) {
      LOGGER.warn("Found suspicious date value: {}", date);
      return null;
    }
  }

  /**
   * Parse the id and return the corresponding <code>Long</code>.
   *
   * @param id the ID may be <code>null</code>.
   * @return the corresponding Long value or <code>null</code>
   */
  public static @Nullable Long id(@Nullable String id) {
    if (id == null) return null;
    try {
      return Long.valueOf(id);
    } catch (NumberFormatException ex) {
      LOGGER.warn("Found suspicious ID value: {}", id);
      return null;
    }
  }

  /**
   * Parse the id and return the corresponding <code>Long</code>.
   *
   * @param id the ID
   * @return the corresponding Long value
   */
  public static Long requiredId(String id) {
    try {
      return Long.valueOf(id);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("Invalid entity ID found", ex);
    }
  }

  /**
   * Parse the number and return the corresponding <code>Integer</code>.
   *
   * @param i the integer to parse
   * @return the corresponding Integer value or <code>null</code>
   */
  public static int integer(@Nullable String i) {
    if (i == null) return -1;
    try {
      return Integer.parseInt(i);
    } catch (NumberFormatException ex) {
      LOGGER.warn("Found suspicious integer value: {}", i);
      return -1;
    }
  }

  /**
   * Parse the notification and return the corresponding <code>PSNotification</code> instance.
   *
   * @param notification the notification to parse
   * @return the corresponding notification value or <code>null</code>
   */
  public static @Nullable PSNotification notification(@Nullable String notification) {
    if (notification == null) return null;
    try {
      return PSNotification.valueOf(notification);
    } catch (IllegalArgumentException ex) {
      LOGGER.warn("Found suspicious notification value: {}", notification);
      return null;
    }
  }

  /**
   * Parse the role and return the corresponding <code>PSRole</code> instance.
   *
   * @param role the role to parse
   * @return the corresponding role value or <code>null</code>
   */
  public static @Nullable PSRole role(@Nullable String role) {
    if (role == null) return null;
    try {
      return PSRole.valueOf(role.replace('-', '_'));
    } catch (IllegalArgumentException ex) {
      LOGGER.warn("Found suspicious role value: {}", role);
      return null;
    }
  }

}
