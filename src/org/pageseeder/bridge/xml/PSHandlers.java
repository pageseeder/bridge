/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.xml;

import org.pageseeder.bridge.model.PSNotification;
import org.pageseeder.bridge.model.PSRole;
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
   * Parse the id and return the corresponding <code>Long</code>.
   *
   * @param id the ID
   * @return the corresponding Long value or <code>null</code>
   */
  public static Long id(String id) {
    if (id == null) return null;
    try {
      return Long.valueOf(id);
    } catch (NumberFormatException ex) {
      LOGGER.warn("Found suspicious ID value: {}", id);
      return null;
    }
  }

  /**
   * Parse the number and return the corresponding <code>Integer</code>.
   *
   * @param i the integer to parse
   * @return the corresponding Integer value or <code>null</code>
   */
  public static int integer(String i) {
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
  public static PSNotification notification(String notification) {
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
  public static PSRole role(String role) {
    if (role == null) return null;
    try {
      return PSRole.valueOf(role);
    } catch (NumberFormatException ex) {
      LOGGER.warn("Found suspicious role value: {}", role);
      return null;
    }
  }

}
