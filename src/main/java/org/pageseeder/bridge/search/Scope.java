/*
 * Copyright 2018 Allette Systems (Australia)
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

package org.pageseeder.bridge.search;

import java.util.Collections;
import java.util.List;

/**
 * Defines the scope of the search which may be:
 *
 * <ul>
 *   <li>A single group</li>
 *   <li>A project</li>
 *   <li>Specific groups within a project</li>
 * </ul>
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public class Scope {

  protected static final Scope EMPTY = new Scope(false, "", Collections.emptyList());

  private final boolean _project;

  private final String _name;

  private final List<String> _groups;

  private Scope(boolean project, String name, List<String> groups) {
    this._project = project;
    this._name = name;
    this._groups = groups;
  }

  public static Scope group(String group) {
    return new Scope(false, group, Collections.emptyList());
  }

  public static Scope project(String project) {
    return new Scope(true, project, Collections.emptyList());
  }

  public static Scope project(String project, List<String> groups) {
    return new Scope(true, project, groups);
  }

  public boolean isProject() {
    return this._project;
  }

  public String getName() {
    return this._name;
  }

  public List<String> getGroups() {
    return this._groups;
  }
}
