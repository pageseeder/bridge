/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.xml;

import org.pageseeder.bridge.core.PSNotification;
import org.pageseeder.bridge.core.PSRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of utility functions for PageSeeder handler.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public final class PSHandlers {

  private final static Logger LOGGER = LoggerFactory.getLogger(PSHandlers.class);

  /** Utility class. */
  private PSHandlers() {
  }

  public static Long id(String id) {
    if (id == null) return null;
    try {
      return Long.valueOf(id);
    } catch (NumberFormatException ex) {
      LOGGER.warn("Found suspicious ID value: {}", id);
      return null;
    }
  }

  public static int integer(String i) {
    if (i == null) return -1;
    try {
      return Integer.parseInt(i);
    } catch (NumberFormatException ex) {
      LOGGER.warn("Found suspicious integer value: {}", i);
      return -1;
    }
  }

  public static PSNotification notification(String notification) {
    if (notification == null) return null;
    try {
      return PSNotification.valueOf(notification);
    } catch (IllegalArgumentException ex) {
      LOGGER.warn("Found suspicious notification value: {}", notification);
      return null;
    }
  }

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
