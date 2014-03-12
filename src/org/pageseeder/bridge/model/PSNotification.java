/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

/**
 * @author Christophe Lauret
 * @version 0.1.0
 */
public enum PSNotification {

  /**
   * No notification.
   */
  none("None"),

  /**
   * Immediate notification.
   */
  immediate("Immediate"),

  /**
   * Daily digest at scheduled time.
   */
  daily("Daily");

  /**
   * The value to send as a parameter to services.
   */
  private final String parameter;

  /**
   * Pricate constructor.
   */
  private PSNotification(String p) {
    this.parameter = p;
  }

  /**
   * @return the parameter
   */
  public String parameter() {
    return this.parameter;
  }

}
