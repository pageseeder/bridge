/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.core;

/**
 * A PageSeeder project.
 *
 * @author Christophe Lauret
 */
public final class PSProject extends PSGroup {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  public PSProject() {
  }

  public PSProject(String name) {
    super(name);
  }

  @Override
  public String toString() {
    return "P("+this.getId()+":"+this.getName()+")";
  }
}
