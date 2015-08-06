/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

/**
 * Use for the notify parameter when creating or replying to comments.
 *
 * @author Christophe Lauret
 * @version 0.3.0
 */
public enum PSNotify {

  /**
   * Notify based on each member's notification settings.
   */
  normal,

  /**
   * will ignore members' individual notifications settings.
   */
  announce,

  /**
   * No notification.
   */
  silent;

  /**
   * @return the parameter to use when communicating with PageSeder
   */
  public String parameter() {
    return this.name();
  }

}
