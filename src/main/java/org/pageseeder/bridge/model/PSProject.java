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
 * A PageSeeder project.
 *
 * <p>The public ID of a project is its name.
 *
 * @author Christophe Lauret
 */
public final class PSProject extends PSGroup {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * Create a new project without any identifier.
   */
  public PSProject() {
  }

  /**
   * Create a new project with a private identifier.
   *
   * @param id The id of the project.
   */
  public PSProject(Long id) {
    super(id);
  }

  /**
   * Create a new project with a public identifier.
   *
   * <p>Not: this constructor does not check whether the name is valid.
   *
   * @param name The name of the project.
   */
  public PSProject(String name) {
    super(name);
  }

  @Override
  public String toString() {
    return "P("+getId()+":"+getName()+")";
  }
}
