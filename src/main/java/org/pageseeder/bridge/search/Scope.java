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
 * This is a low-level API and it is recommended that you use the methods defined in the search
 * implementations.
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public class Scope {

  protected static final Scope EMPTY = new Scope(false, "", Collections.emptyList(), "");

  private final boolean _project;

  private final String _name;

  private final List<String> _groups;

  private final String _member;

  private Scope(boolean project, String name, List<String> groups, String member) {
    this._project = project;
    this._name = name;
    this._groups = groups;
    this._member = member;
  }

  public Scope group(String group) {
    return new Scope(false, group, Collections.emptyList(), this._member);
  }

  public Scope project(String project) {
    return new Scope(true, project, Collections.emptyList(), this._member);
  }

  public Scope project(String project, List<String> groups) {
    return new Scope(true, project, groups, this._member);
  }

  public Scope member(String member) {
    return new Scope(this._project, this._name, this._groups, member);
  }

  public boolean isProject() {
    return this._project;
  }

  public String name() {
    return this._name;
  }

  public List<String> groups() {
    return this._groups;
  }

  public String member() {
    return this._member;
  }

  public boolean hasMember() {
    return this._member.length() > 0;
  }

  /**
   * Check that the scope is ready to make a search
   *
   * @throws IllegalStateException if it is empty or it is a project search and no member was specified
   */
  public void checkReady() {
    if (this == Scope.EMPTY)
      throw new IllegalStateException("You must specify the scope of this search (group or project)");
    if (this.isProject() && !this.hasMember())
      throw new IllegalStateException("You must specify the member for project-level searches");
  }

}
