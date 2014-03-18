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
  private final String _parameter;

  /**
   * Private constructor.
   *
   * @param p the name of the parameter for PageSeeder services
   */
  private PSNotification(String p) {
    this._parameter = p;
  }

  /**
   * @return the parameter to use when communicating with PageSeder
   */
  public String parameter() {
    return this._parameter;
  }

}
