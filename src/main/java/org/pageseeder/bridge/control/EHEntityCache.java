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
package org.pageseeder.bridge.control;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.PSEntity;
import org.pageseeder.bridge.PSEntityCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.SearchAttribute;
import net.sf.ehcache.config.Searchable;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;

/**
 * A cache for a PageSeeder entity backed by EHCache.
 *
 * @param <E> The type of entity
 *
 * @author Christophe Lauret
 * @version 0.2.4
 * @since 0.2.0
 */
final class EHEntityCache<E extends PSEntity> implements PSEntityCache<E> {

  /** Logger */
  private static final Logger LOGGER = LoggerFactory.getLogger(EHEntityCache.class);

  /**
   * Reuse the same cache manager to avoid I/O problems (configuration seems to be parsed for each getInstance).
   */
  private static volatile @Nullable CacheManager manager = null;

  /**
   * The underlying cache.
   */
  private final Ehcache _cache;

  /**
   * Create a new cache wrapper.
   *
   * @param cache The Ehcache to wrap.
   */
  private EHEntityCache(Ehcache cache) {
    this._cache = cache;
  }

  /**
   * Retrieve the object in the cache for the specified key.
   *
   * @param key The key
   *
   * @return The version of the element or <code>null</code> if the key or element is <code>null</code>
   */
  @Override
  @SuppressWarnings("unchecked")
  public synchronized @Nullable E get(String key) {
    if (key == null)
      return null;
    @Nullable E o = null;
    Element element = this._cache.get(key);
    if (element != null && !element.isExpired()) {
      try {
        o = (E)element.getObjectValue();
      } catch (ClassCastException ex) {
        LOGGER.warn("Element of type {} found in cache {}", element.getObjectValue().getClass().getName(), this._cache.getName());
      }
    }
    return o;
  }

  @Override
  public @Nullable E get(E entity) {
    if (!entity.isIdentifiable()) return null;
    String key = entity.getKey();
    Long id = entity.getId();
    if (key != null) return get(key);
    if (id != null) return get(id);
    return null;
  }

  /**
   * Retrieve the object in the cache for the specified key.
   *
   * @param id The ID of the PageSeeder entity in the PageSeeder database.
   *
   * @return The version of the element or <code>null</code> if the key or element is <code>null</code>
   */
  @Override
  @SuppressWarnings("unchecked")
  public synchronized @Nullable E get(Long id) {
    if (id == null)
      return null;
    @Nullable E o = null;
    Query query =  this._cache.createQuery();
    Attribute<Long> byId = this._cache.getSearchAttribute("id");
    query.includeValues().addCriteria(byId.eq(id));
    Results results = query.execute();
    List<Result> all = results.all();
    if (all.size() > 0) {
      Result r = all.get(0);
      o = (E)r.getValue();
    }
    return o;
  }

  /**
   * Retrieve the object in the cache for the specified key.
   *
   * @param attribute The name of the attribute to match.
   * @param value     The value of the attribute to match.
   *
   * @return The version of the element or <code>null</code> if the key or element is <code>null</code>
   */
  @SuppressWarnings("unchecked")
  public @Nullable E get(String attribute, String value) {
    if (value == null)
      return null;
    @Nullable E o = null;
    Query query =  this._cache.createQuery();
    Attribute<String> byId = this._cache.getSearchAttribute(attribute);
    query.includeValues().addCriteria(byId.eq(value));
    Results results = query.execute();
    List<Result> all = results.all();
    if (all.size() > 0) {
      Result r = all.get(0);
      o = (E)r.getValue();
    }
    return o;
  }

  /**
   * Retrieve the object in the cache for the specified key.
   *
   * @param attribute The name of the attribute to match.
   * @param value       The value of the attribute to match.
   *
   * @return The list of matching element or <code>null</code> if the key or element is <code>null</code>
   */
  @SuppressWarnings("unchecked")
  public @Nullable List<E> list(String attribute, String value) {
    if (value == null)
      return null;
    Query query =  this._cache.createQuery();
    Attribute<String> byId = this._cache.getSearchAttribute(attribute);
    query.addCriteria(byId.eq(value));
    Results results = query.execute();
    List<Result> all = results.all();
    List<E> entities = new ArrayList<>();
    for (Result r : all) {
      entities.add((E)r.getValue());
    }
    return entities;
  }

  /**
   * Return the version of the element for the specified key.
   *
   * @param key The key
   *
   * @return The version of the element or <code>null</code> if the key or element is <code>null</code>
   */
  @Override
  public synchronized @Nullable Long getVersion(String key) {
    if (key == null)
      return null;
    Element element = this._cache.get(key);
    if (element != null) return element.getVersion();
    else return null;
  }

  /**
   * Put a new element in the underlying cache.
   *
   * @param value The corresponding element.
   */
  @Override
  public synchronized void put(final E value) {
    if (value == null)
      throw new NullPointerException("value");
    String key = value.getKey();
    if (key == null)
      throw new IllegalArgumentException("key");
    Element element = new Element(key, value, System.currentTimeMillis());
    this._cache.put(element);
  }

  /**
   * Removes the element.
   *
   * @param key the key of the element to remove.
   */
  @Override
  public synchronized void remove(String key) {
    if (key == null)
      return;
    this._cache.remove(key);
  }

  /**
   * Remove all cache entries.
   */
  @Override
  public synchronized void removeAll() {
    this._cache.removeAll();
  }

  // Lifecycle
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns a new cache instance.
   *
   * @param name the name of the cache
   * @param keys the keys to use for this entity
   *
   * @param <E> The type of element to store
   *
   * @return A new cache wrapper instance.
   */
  protected static synchronized <E extends PSEntity> PSEntityCache<E> newInstance(String name, String... keys) {
    CacheManager man = manager;
    if (man == null) {
      man = CacheManager.create();
      manager = man;
    }
    Ehcache cache = man.getEhcache(name);
    if (cache == null) {
      CacheConfiguration config = new CacheConfiguration(name, 1000).eternal(true);
      Searchable searchable = new Searchable();
      searchable.setKeys(false);
      searchable.setValues(false);
      searchable.addSearchAttribute(new SearchAttribute().name("id"));
      if (keys != null) {
        for (String key : keys) {
          searchable.addSearchAttribute(new SearchAttribute().name(key));
        }
      }
      config.addSearchable(searchable);
      man.addCache(new Cache(config));
      cache = man.getEhcache(name);
    }
    return new EHEntityCache<>(cache);
  }

  /**
   * Shutdown the caching for users.
   */
  public static synchronized void shutdown() {
    // shutdown the mananer to release resources
    CacheManager man = manager;
    if (man != null) {
      manager = null;
      for (String name : man.getCacheNames()) {
        Ehcache cache = man.getEhcache(name);
        cache.flush();
      }
      man.shutdown();
    }
  }
}
