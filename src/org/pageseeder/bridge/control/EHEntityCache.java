/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.control;

import java.util.ArrayList;
import java.util.List;

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

import org.pageseeder.bridge.PSEntity;
import org.pageseeder.bridge.PSEntityCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A cache for a PageSeeder entity backed by EHCache.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public final class EHEntityCache<T extends PSEntity> implements PSEntityCache<T> {

  /** Logger */
  private static final Logger LOGGER = LoggerFactory.getLogger(EHEntityCache.class);

  /**
   * Reuse the same cache manager to avoid I/O problems (configuration seems to be parsed for each getInstance).
   */
  private static volatile CacheManager manager = null;

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
  public synchronized T get(String key) {
    if (key == null)
      return null;
    T o = null;
    Element element = this._cache.get(key);
    if (element != null && !element.isExpired()) {
      try {
        o = (T)element.getValue();
      } catch (ClassCastException ex) {
        LOGGER.warn("Element of type {} found in cache {}", element.getValue().getClass().getName(), this._cache.getName());
      }
    }
    return o;
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
  public synchronized T get(Long id) {
    if (id == null)
      return null;
    T o = null;
    Query query =  this._cache.createQuery();
    Attribute<Long> byId = this._cache.getSearchAttribute("id");
    query.includeValues().addCriteria(byId.eq(id));
    Results results = query.execute();
    List<Result> all = results.all();
    if (all.size() > 0) {
      Result r = all.get(0);
      o = (T)r.getValue();
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
  @Override
  @SuppressWarnings("unchecked")
  public T get(String attribute, String value) {
    if (value == null)
      return null;
    T o = null;
    Query query =  this._cache.createQuery();
    Attribute<String> byId = this._cache.getSearchAttribute(attribute);
    query.includeValues().addCriteria(byId.eq(value));
    Results results = query.execute();
    List<Result> all = results.all();
    if (all.size() > 0) {
      Result r = all.get(0);
      o = (T)r.getValue();
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
  @Override
  @SuppressWarnings("unchecked")
  public List<T> list(String attribute, String value) {
    if (value == null)
      return null;
    Query query =  this._cache.createQuery();
    Attribute<String> byId = this._cache.getSearchAttribute(attribute);
    query.addCriteria(byId.eq(value));
    Results results = query.execute();
    List<Result> all = results.all();
    List<T> entities = new ArrayList<T>();
    for (Result r : all) {
      entities.add((T)r.getValue());
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
  public synchronized Long getVersion(String key) {
    if (key == null)
      return null;
    Element element = this._cache.get(key);
    if (element != null) {
      return element.getVersion();
    } else {
      return null;
    }
  }

  /**
   * Put a new element in the underlying cache.
   *
   * @param key   The key
   * @param value The corresponding element.
   */
  @Override
  public synchronized void put(final T value) {
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
   *
   * @param <E> The type of element to store
   *
   * @return A new cache wrapper instance.
   */
  protected static synchronized <E extends PSEntity> PSEntityCache<E> newInstance(String name, String... keys) {
    if (manager == null)
      manager = CacheManager.create();
    Ehcache cache = manager.getEhcache(name);
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
      manager.addCache(new Cache(config));
      cache = manager.getEhcache(name);
    }
    PSEntityCache<E> ocache = new EHEntityCache<E>(cache);
    return ocache;
  }

  /**
   * Shutdown the caching for users.
   */
  public static synchronized void shutdown() {
    // shutdown the mananer to release resources
    if (manager != null) {
      for (String name : manager.getCacheNames()) {
        Ehcache cache = manager.getEhcache(name);
        cache.flush();
      }
      manager.shutdown();
      manager = null;
    }
  }
}
