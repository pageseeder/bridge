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

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.spi.CachingProvider;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.PSEntity;
import org.pageseeder.bridge.PSEntityCache;

/**
 * A cache for a PageSeeder entity backed by EHCache.
 *
 * @param <E> The type of entity
 *
 * @author Christophe Lauret
 * @version 0.2.4
 * @since 0.2.0
 */
final class EntityCache<E extends PSEntity> implements PSEntityCache<E> {

  /**
   * Reuse the same cache manager to avoid I/O problems (configuration seems to be parsed for each getInstance).
   */
  private static volatile @Nullable CacheManager manager = null;

  /**
   * The underlying cache.
   */
  private final Cache<Long, CachedEntity<E>> _cacheById;

  /**
   * To lookup cache by identifier.
   */
  private final @Nullable Cache<String, Long> _cacheByKey;

  /**
   * Create a new cache wrapper.
   *
   * @param cache The Ehcache to wrap.
   */
  private EntityCache(Cache<Long, CachedEntity<E>> cache) {
    this._cacheById = cache;
    this._cacheByKey = null;
  }

  /**
   * Create a new cache wrapper.
   *
   * @param cache The Ehcache to wrap.
   */
  private EntityCache(Cache<Long, CachedEntity<E>> cache, Cache<String, Long> byPublicId) {
    this._cacheById = cache;
    this._cacheByKey = byPublicId;
  }

  /**
   * Retrieve the object in the cache for the specified key.
   *
   * @param key The key
   *
   * @return The version of the element or <code>null</code> if the key or element is <code>null</code>
   */
  @Override
  public synchronized @Nullable E get(String key) {
    if (key == null)
      return null;
    CachedEntity<E> o = getCachedEntity(key);
    return o != null? o.entity() : null;
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
  public synchronized @Nullable E get(Long id) {
    if (id == null)
      return null;
    CachedEntity<E> o = getCachedEntity(id);
    return o != null? o.entity() : null;
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
    CachedEntity<E> o = getCachedEntity(key);
    if (o != null) return o.version();
    else return null;
  }

  /**
   * Put a new element in the underlying cache.
   *
   * @param entity The corresponding element.
   */
  @Override
  public synchronized void put(final E entity) {
    if (entity == null)
      throw new NullPointerException("entity");
    String key = entity.getKey();
    if (key == null)
      throw new IllegalArgumentException("key");
    Long id = entity.getId();
    if (id == null)
      throw new IllegalArgumentException("id");
    CachedEntity<E> e = new CachedEntity<>(entity);
    this._cacheById.put(id, e);
    Cache<String, Long> keyCache = this._cacheByKey;
    if (keyCache != null) {
      keyCache.put(key, id);
    }
  }

  /**
   * Removes the element.
   *
   * @param key the key of the element to remove.
   */
  @Override
  public synchronized void remove(String key) {
    if (key == null) return;
    CachedEntity<E> e = getCachedEntity(key);
    if (e != null) {
      Long id = e.entity().getId();
      if (id != null) {
        this._cacheById.remove(id);
      }
      Cache<String, Long> keyCache = this._cacheByKey;
      if (keyCache != null) {
        keyCache.remove(key);
      }
    }
  }

  /**
   * Remove all cache entries.
   */
  @Override
  public synchronized void removeAll() {
    this._cacheById.removeAll();
    Cache<String, Long> keyCache = this._cacheByKey;
    if (keyCache != null) {
      keyCache.removeAll();
    }
  }

  // Lifecycle
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns a new cache instance.
   *
   * @param type the type of the cache
   * @param keys the keys to use for this entity
   *
   * @param <E> The type of element to store
   *
   * @return A new cache wrapper instance.
   */
//  @SuppressWarnings("rawtypes")
  protected static synchronized <E extends PSEntity> EntityCache<E> newInstance(Class<E> type, String... keys) {
    CachingProvider provider = Caching.getCachingProvider();
    CacheManager manager = provider.getCacheManager();
    Cache<Long, CachedEntity<E>> byId = manager.getCache(type.getSimpleName()+".id");
    Cache<String, Long> byKey = manager.getCache(type.getSimpleName()+".key");
    if (byId == null) {
      MutableConfiguration<? extends Object, ? extends Object> config = new MutableConfiguration<>();
      //config.setTypes(Long.class, CachedEntity.class);
      //config.setTypes(Object.class, Object.class);
      config.setStatisticsEnabled(true);
      config.setExpiryPolicyFactory(EternalExpiryPolicy.factoryOf());
      byId = (Cache<Long, CachedEntity<E>>)manager.createCache(type.getSimpleName()+".id", config);
      byKey = (Cache<String, Long>)manager.createCache(type.getSimpleName()+".key", config);
    }
    return new EntityCache<>(byId, byKey);
  }

  /**
   * Shutdown the caching for users.
   */
  public static synchronized void shutdown() {
    // shutdown the manager to release resources
    CacheManager man = manager;
    if (man != null) {
      manager = null;
      man.close();
    }
  }

  /**
   * Returns the cached entity for the specified key.
   *
   * This is a two-pass lookup, first we look for the primary key, then for the object.
   *
   * @param key The key
   *
   * @return The corresponding cached entity
   */
  private @Nullable CachedEntity<E> getCachedEntity(String key) {
    Long id = null;
    Cache<String, Long> keyCache = this._cacheByKey;
    if (keyCache != null) {
      id = keyCache.get(key);
    }
    if (id == null) return null;
    return this._cacheById.get(id);
  }

  private @Nullable CachedEntity<E> getCachedEntity(Long id) {
    return this._cacheById.get(id);
  }

  /**
   * Wraps an entity and keep track of its version.
   *
   * @param <E> The PageSeeder entity to wrap
   */
  private static final class CachedEntity<E> {

    /** The wrapped entity (never <code>null</code>) */
    private final E _entity;

    /** The version of the entity */
    private final long _version = System.currentTimeMillis();

    public CachedEntity(E entity) {
      this._entity = entity;
    }

    /** @return the cached entity */
    public E entity() {
      return this._entity;
    }

    /** @return the version of this entity */
    public long version() {
      return this._version;
    }

  }
}
