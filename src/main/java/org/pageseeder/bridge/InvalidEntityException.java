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

/**
 * Class of exceptions used for when a PageSeeder entity cannot be stored in PageSeeder because
 * it is considered invalid.
 *
 * <p>This exception should be thrown when the intrinsic attributes of an entity are known to
 * violate one of the constraints on a PageSeeder.
 *
 * <p>Examples: an invalid email address, a string value exceeding a maximum length.
 *
 * <p>Note: this exception may not be appropriate for when extrinsic constraints are violated
 * for example, for unique identifier.
 *
 * @author Christophe Lauret
 */
public final class InvalidEntityException extends APIException {

  /** As per requirement */
  private static final long serialVersionUID = 1L;

  /**
   * The type of entity.
   */
  private final Class<? extends PSEntity> _entityType;

  /**
   * The type of entity.
   */
  private final EntityValidity _validity;

  /**
   * Create a new exception.
   *
   * @param entity   The entity that is invalid.
   * @param validity The entity validity status.
   */
  public InvalidEntityException(Class<? extends PSEntity> entity, EntityValidity validity) {
    super(entity.getSimpleName());
    this._entityType = entity;
    this._validity = validity;
  }

  /**
   * @return the kind of entity
   */
  public Class<? extends PSEntity> getEntityType() {
    return this._entityType;
  }

  /**
   * @return the validity
   */
  public EntityValidity getValidity() {
    return this._validity;
  }

}
