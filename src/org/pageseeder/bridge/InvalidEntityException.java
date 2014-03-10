/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
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
public class InvalidEntityException extends APIException {

  /** As per requirement */
  private static final long serialVersionUID = -521141112817296283L;

  /**
   * The type of entity.
   */
  private final Class<? extends PSEntity> entityType;

  /**
   * The type of entity.
   */
  private final EntityValidity validity;

  /**
   * Create a new exception.
   *
   * @param message A message explaining what's wrong with the entity.
   */
  public InvalidEntityException(Class<? extends PSEntity> entity, EntityValidity validity) {
    super(entity.getSimpleName());
    this.entityType = entity;
    this.validity = validity;
  }

  /**
   * @return the kind of entity
   */
  public Class<? extends PSEntity> getEntityType() {
    return this.entityType;
  }

  /**
   * @return the validity
   */
  public EntityValidity getValidity() {
    return this.validity;
  }

}
