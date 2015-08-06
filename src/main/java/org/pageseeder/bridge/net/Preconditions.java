/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.net;

import org.pageseeder.bridge.FailedPrecondition;
import org.pageseeder.bridge.InvalidEntityException;
import org.pageseeder.bridge.PSEntity;

/**
 * A utility class to check the preconditions.
 *
 * <p>Preconditions are used to ensure that no connections are made to PageSeeder needlessly
 *
 * @author Christophe Lauret
 * @version 0.2.1
 * @since 0.1.0
 */
public final class Preconditions {

  /** Utility class. */
  private Preconditions(){}

  /**
   * Precondition requiring the specified entity to be identifiable.
   *
   * @param entity The entity to check
   * @param name   The name of the entity
   *
   * @throws NullPointerException If the entity is <code>null</code>
   * @throws FailedPrecondition If the the {@link PSEntity#isIdentifiable()} method returns <code>false</code>.
   */
  static void isIdentifiable(PSEntity entity, String name) throws FailedPrecondition {
    if (!entity.isIdentifiable()) throw new FailedPrecondition(name+" must be identifiable");
  }

  /**
   * Precondition requiring the specified entity to be valid.
   *
   * @param entity The entity to check
   * @param name   The name of the entity
   *
   * @throws NullPointerException If the entity is <code>null</code>
   * @throws InvalidEntityException If the the {@link PSEntity#isValid()} method returns <code>false</code>.
   */
  static void isValid(PSEntity entity, String name) throws InvalidEntityException {
    if (!entity.isValid()) throw new InvalidEntityException(entity.getClass(), entity.checkValid());
  }

  /**
   * Precondition requiring the specified string to be non-null and at least 1 character long.
   *
   * @param s    The string to check
   * @param name The name of the object to generate the message.
   *
   * @throws FailedPrecondition If the pre-condition failed.
   */
  static void isNotEmpty(String s, String name) throws FailedPrecondition {
    if (s == null || s.length() == 0) throw new FailedPrecondition(name+" must not be empty");
  }

  /**
   * Precondition requiring the specified object to be non-null.
   *
   * @param o    The object to check for <code>null</code>
   * @param name The name of the object to generate the message.
   *
   * @throws FailedPrecondition If the pre-condition failed.
   */
  static void isNotNull(Object o, String name) throws FailedPrecondition {
    if (o == null) throw new FailedPrecondition(name+" must not be null");
  }

  /**
   * Precondition requiring the specified string include a 'dash' (usually for a group name).
   *
   * @param s    The string to check
   * @param name The name of the object to generate the message.
   *
   * @throws NullPointerException If the specified string is <code>null</code>
   * @throws FailedPrecondition If the pre-condition failed.
   */
  static void includesDash(String s, String name) throws FailedPrecondition {
    if (s.indexOf('-') < 0) throw new FailedPrecondition(name+" must include a '-'");
  }

}
