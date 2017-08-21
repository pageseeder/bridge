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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Identifier for a group or project
 */
public final class GroupID extends ID implements Serializable {

  /** As per requirement for {@link Serializable} */
  private static final long serialVersionUID = 1L;

  /**
   * The root group ID (empty string)
   */
  public static final GroupID ROOT = new GroupID("");

  /**
   * Create a new Group ID.
   *
   * @param name the full name of the group.
   */
  public GroupID(String name) {
    super(name);
  }

  public GroupID parent() {
    String name = toString();
    int dash = name.lastIndexOf('-');
    return dash > 0 ? new GroupID(name.substring(0, dash)) : ROOT;
  }

  /**
   * An unmodifiable list of names which cannot be used as group names.
   */
  public static final Collection<String> RESERVED_GROUP_NAMES = Collections.unmodifiableList(
      Arrays.asList("page", "block", "tree", "uri", "fullpage", "embed", "psadmin", "bundle",
          "service", "error", "weborganic", "woconfig", "servlet", "psdoc", "filter", "group",
          "home", "member", "project"));

  /**
   * Regular expression that group names must match.
   *
   * Restrictions:
   *    - first character is ALPHA
   *    - other characters are ALPHA / DIGIT / "_" / "~" / "-"
   */
  private static final String REGEX_GROUP_NAME = "^[a-z][a-z0-9_~\\-]+$";

  /**
   * Checks if given String is a valid group name. Restrictions are:
   *    - first character is ALPHA
   *    - other characters are ALPHA / DIGIT / "_" / "~" / "-"
   *    - cannot contain "--"
   *    - not a reserved name
   *
   * @param name the group name to check.
   * @return <code>true</code> if the group name is valid;
   *         <code>false</code> otherwise.
   */
  public static boolean isValidGroupName(@Nullable String name) {
    if (name == null || name.length() == 0) return false;
    String pjname = name;
    int dash = name.indexOf('-');
    if (dash != -1) {
      pjname = name.substring(0, dash);
    }
    return name.matches(REGEX_GROUP_NAME) && !RESERVED_GROUP_NAMES.contains(pjname) && !name.endsWith("-silent") && !name.contains("--");
  }

}