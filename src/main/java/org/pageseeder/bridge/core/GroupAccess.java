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

import org.jspecify.annotations.Nullable;

public enum GroupAccess {

  MEMBER,

  PUBLIC;

  @Override
  public String toString() {
    return name().toLowerCase();
  }

  /**
   * Match a group access value
   *
   * @param s the string value
   * @return the corresponding GroupAccess
   */
  public static GroupAccess forName(@Nullable String s) {
    for (GroupAccess access : values()) {
      if (access.name().equalsIgnoreCase(s)) return access;
    }
    return MEMBER;
  }

}
