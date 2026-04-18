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

  /** 'Mod &amp; App' role PageSeeder. */
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
  PSRole(String p) {
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
