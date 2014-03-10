/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge;

import java.io.Serializable;

/**
 * @author Christophe Lauret
 * @version 0.1.0
 */
public interface PSEntity extends Serializable {

  /**
   * @return the private ID of the PageSeeder entity in the database.
   */
  Long getId();

  /**
   * @return the public ID for the PageSeeder entity.
   */
  String getKey();

  /**
   * Determines whether this object is valid based on known PageSeeder constraints.
   *
   * @return <code>true</code> if the object is intrinsically valid based on its internal values;
   *         <code>false</code> if any of its attributes violates a PageSeeder constraint.
   */
  boolean isValid();

  /**
   * Determines whether this object is valid based on known PageSeeder constraints.
   *
   * @return <code>true</code> if the object is intrinsically valid based on its internal values;
   *         <code>false</code> if any of its attributes violates a PageSeeder constraint.
   */
  EntityValidity checkValid();

}
