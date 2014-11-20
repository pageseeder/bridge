/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

/**
 * A membership role.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public enum PSRole {

  /** 'Guest' role PageSeeder. */
  guest("Guest"),

  /** 'Reviewer' role PageSeeder. */
  reviewer("Reviewer"),

  /** 'Contributor' role PageSeeder. */
  contributor("Contributor"),

  /** 'Manager' role PageSeeder. */
  manager("Manager"),

  /** 'Moderator' role PageSeeder. */
  moderator("Moderator"),

  /** 'Approver' role PageSeeder. */
  approver("Approver"),

  /** 'Mod & App' role PageSeeder. */
  moderator_and_approver("Mod & App");

  /**
   * Parameter that can be sent to the service.
   */
  private final String _parameter;

  /**
   * Sole constructor.
   *
   * @param p The name of the parameter for the PageSeeder services.
   */
  private PSRole(String p) {
    this._parameter = p;
  }

  /**
   * @return the parameter to use when communicating with PageSeder
   */
  public String parameter() {
    return this._parameter.toLowerCase();
  }

  /**
   *
   * @deprecated Mixed case is deprecated, but some services still require it.
   *
   * @return the parameter to use when communicating with PageSeder using mixed case
   */
  @Deprecated
  public String parameterMixed() {
    return this._parameter;
  }

}
