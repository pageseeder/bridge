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
 * Interface for all entity caches.
 *
 * @author Christophe Lauret
 * @version 0.2.4
 * @since 0.1.0
 *
 * @param <E> The type of entity to cache.
 */
public interface PSEntityCache<E extends PSEntity> {

  /**
   * Put a new element in the underlying cache.
   *
   * @param entity The entity to cache.
   */
  void put(E entity);

  /**
   * Retrieve the object in the cache for the specified ID.
   *
   * @param id The ID of the entity
   *
   * @return The version of the element or <code>null</code> if the ID or element is <code>null</code>
   */
  E get(Long id);

  /**
   * Retrieve the object in the cache for the specified key.
   *
   * @param key The key
   *
   * @return The version of the element or <code>null</code> if the key or element is <code>null</code>
   */
  E get(String key);

  /**
   * Retrieve the object in the cache from an instance.
   *
   * @param entity The entity
   *
   * @return The version of the element or <code>null</code> if the key or element is <code>null</code>
   */
  E get(E entity);

  /**
   * Retrieve the object in the cache for the specified key.
   *
   * @param attribute An attribute that can be sufficient to identify the entity.
   * @param value     The value the value
   *
   * @return The version of the element or <code>null</code> if the key or element is <code>null</code>
   */
//  E get(String attribute, String value);

  /**
   * Retrieve the object in the cache for the specified key.
   *
   * @param attribute An attribute for that entity.
   * @param value     The value the value
   *
   * @return The version of the element or <code>null</code> if the key or element is <code>null</code>
   */
//  List<E> list(String attribute, String value);

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
