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

import org.pageseeder.bridge.Requires;

/**
 * @author Christophe Lauret
 *
 * @version 0.11.3
 * @since 0.1.0
 */
public enum PSNotification {

  /**
   * No notification.
   */
  none("none"),

  /**
   * Immediate notification.
   */
  immediate("immediate"),

  /**
   * Daily digest at scheduled time.
   */
  daily("daily"),

  /**
   * Weekly digest at scheduled time.
   */
  @Requires(minVersion = 59300)
  weekly("weekly"),

  /**
   * Essential people in the task.
   */
  @Requires(minVersion = 59300)
  essential("essential");

  /**
   * The value to send as a parameter to services.
   */
  private final String _parameter;

  /**
   * Private constructor.
   *
   * @param p the name of the parameter for PageSeeder services
   */
  PSNotification(String p) {
    this._parameter = p;
  }

  /**
   * @return the parameter to use when communicating with PageSeder
   */
  public String parameter() {
    return this._parameter;
  }

}
