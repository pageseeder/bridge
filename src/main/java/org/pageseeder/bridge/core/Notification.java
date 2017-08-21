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
import org.pageseeder.bridge.Requires;

/**
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public enum Notification {

  /**
   * No notification.
   */
  none,

  /**
   * Immediate notification.
   */
  immediate,

  /**
   * Daily digest at scheduled time.
   */
  daily,

  /**
   * Weekly digest at scheduled time.
   */
  @Requires(minVersion = 59300)
  weekly,

  /**
   * Essential people in the task.
   */
  @Requires(minVersion = 59300)
  essential;

  /**
   * @return the parameter to use when communicating with PageSeder
   */
  public String parameter() {
    return this.name();
  }

  // TODO Check actual values to be passed
  // TODO Provide safe value
  // TODO Provide `unknown` option

  /**
   * Match a group access value
   *
   * @param s the string value
   * @return the corresponding Notification (defaults to none)
   */
  public static Notification forName(@Nullable String s) {
    for (Notification notification : values()) {
      if (notification.name().equalsIgnoreCase(s)) return notification;
    }
    return none;
  }
}
