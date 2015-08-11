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
package org.pageseeder.bridge;

import java.io.Serializable;

/**
 * @author Christophe Lauret
 * @version 0.2.1
 * @since 0.1.0
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
   * Determines whether the entity is valid based on known PageSeeder constraints.
   *
   * @return <code>true</code> if the object is intrinsically valid based on its internal values;
   *         <code>false</code> if any of its attributes violates a PageSeeder constraint.
   */
  boolean isValid();

  /**
   * Determines whether the entity can be identified in PageSeeder.
   *
   * <p>To be identifiable, the entity must have either a private id or a public id.
   *
   * <p>If the public ID does not correspond to a single attribute in PageSeeder,
   * then a combination of attributes may be used to determine whether it is possible
   * to identify the entity.
   *
   * @return <code>true</code> if the object can be identified in PageSeeder;
   *         <code>false</code> if it is not possible to identify the entity form its attributes or keys.
   */
  boolean isIdentifiable();

  /**
   * Returns the identifier to use when connecting to PageSeeder based on the ID available.
   *
   * <p>When both the private and public identifier are available, the private identifier
   * takes precedence over the public one.
   *
   * <p>When the public identifier cannot be used on its own, then only the private ID is
   * returned.
   *
   * <p>In general, if the {@link #isIdentifiable()} method return <code>true</code>, this
   * method should return a value. When it is not the case, it should be clearly documented.
   *
   * @return the identifier to use when connecting to PageSeeder.
   */
  String getIdentifier();

  /**
   * Determines whether this object is valid based on known PageSeeder constraints.
   *
   * @return <code>true</code> if the object is intrinsically valid based on its internal values;
   *         <code>false</code> if any of its attributes violates a PageSeeder constraint.
   */
  EntityValidity checkValid();

}
