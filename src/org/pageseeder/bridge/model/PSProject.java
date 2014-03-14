/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
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
   * Create a new project with a public identifier.
   */
  public PSProject(String name) {
    super(name);
  }

  @Override
  public String toString() {
    return "P("+this.getId()+":"+this.getName()+")";
  }
}
