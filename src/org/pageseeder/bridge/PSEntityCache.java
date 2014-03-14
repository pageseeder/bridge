/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge;

import java.util.List;

/**
 * Interface for all entity caches.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 *
 * @param <T> The type of objects to cache.
 */
public interface PSEntityCache<T extends PSEntity> {

  /**
   * Put a new element in the underlying cache.
   *
   * @param item The item to cache.
   */
  void put(T item);

  /**
   * Retrieve the object in the cache for the specified ID.
   *
   * @param id The ID of the entity
   *
   * @return The version of the element or <code>null</code> if the ID or element is <code>null</code>
   */
  T get(Long id);

  /**
   * Retrieve the object in the cache for the specified key.
   *
   * @param key The key
   *
   * @return The version of the element or <code>null</code> if the key or element is <code>null</code>
   */
  T get(String key);

  /**
   * Retrieve the object in the cache for the specified key.
   *
   * @param attribute An attribute that can be sufficient to identify the entity.
   * @param value     The value the value
   *
   * @return The version of the element or <code>null</code> if the key or element is <code>null</code>
   */
  T get(String attribute, String value);

  /**
   * Retrieve the object in the cache for the specified key.
   *
   * @param attribute An attribute for that entity.
   * @param value     The value the value
   *
   * @return The version of the element or <code>null</code> if the key or element is <code>null</code>
   */
  List<T> list(String attribute, String value);

  /**
   * Return the version of the element for the specified key.
   *
   * @param key The key
   *
   * @return The version of the element or <code>null</code> if the key or element is <code>null</code>
   */
  Long getVersion(String key);

  /**
   * Removes the element.
   *
   * @param key the key of the element to remove.
   */
  void remove(String key);

  /**
   * Remove all cache entries.
   */
  void removeAll();

}
