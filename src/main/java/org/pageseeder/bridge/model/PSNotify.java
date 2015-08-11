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
    return name();
  }

}
