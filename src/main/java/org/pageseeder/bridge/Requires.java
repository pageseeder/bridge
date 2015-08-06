/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge;

/**
 * Documents which version of PageSeeder is necessary
 *
 * @author Christophe Lauret
 * @version 14 March 2014
 */
public @interface Requires {

  /**
   * The minimum PageSeeder version required.
   */
  int minVersion() default 55000;

  /**
   * The maximum PageSeeder version supported.
   */
  int maxVersion() default Integer.MAX_VALUE;

}
