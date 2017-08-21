/*
 * Copyright 2017 Allette Systems (Australia)
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
package org.pageseeder.bridge.core;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A membership role.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public enum Role {

  /** 'Guest' role PageSeeder. */
  guest,

  /** 'Reviewer' role PageSeeder. */
  reviewer,

  /** 'Contributor' role PageSeeder. */
  contributor,

  /** 'Manager' role PageSeeder. */
  manager,

  /** 'Moderator' role PageSeeder. */
  moderator,

  /** 'Approver' role PageSeeder. */
  approver,

  /** 'Mod &amp; App' role PageSeeder. */
  moderator_and_approver("moderator-and-approver"),

  /** Unknown role PageSeeder. */
  unknown;

  /**
   * Parameter that can be sent to the service.
   */
  private final String _parameter;

  /**
   * When the role name is the same as the parameter
   */
  Role() {
    this._parameter = this.name();
  }

  /**
   * Sole constructor.
   *
   * @param parameter The name of the parameter for the PageSeeder services.
   */
  Role(String parameter) {
    this._parameter = parameter;
  }

  /**
   * @return the parameter to use when communicating with PageSeder
   */
  public String parameter() {
    return this._parameter;
  }

  // TODO Check actual values to be passed
  // TODO Provide safe value
  // TODO Provide `unknown` option

  /**
   * Match a role value
   *
   * @param name the string value
   * @return the corresponding Role
   */
  public static Role forName(@Nullable String name) {
    for (Role role : values()) {
      if (role.name().equalsIgnoreCase(name)) return role;
    }
    return unknown;
  }

  /**
   * Match a role value
   *
   * @param name the string value
   * @return the corresponding Role
   */
  public static Role forParameter(@Nullable String name) {
    for (Role role : values()) {
      if (role.parameter().equalsIgnoreCase(name)) return role;
    }
    return unknown;
  }
}
