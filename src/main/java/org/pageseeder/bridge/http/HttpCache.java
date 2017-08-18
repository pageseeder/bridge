/*
 * Copyright 2017 Allette Systems (Australia)
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
package org.pageseeder.bridge.http;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

/**
 *
 * @author Christophe Lauret
 *
 * @version 0.11.4
 * @since 0.11.4
 */
public class HttpCache {

  private final Cache _cache;

  protected HttpCache(String name) {
    CacheManager manager = CacheManager.getInstance();
    this._cache = new Cache(
        new CacheConfiguration(name, 10000)
          .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
          .eternal(false)
          .timeToLiveSeconds(3600)
          .timeToIdleSeconds(1200)
          .diskExpiryThreadIntervalSeconds(0)
          .persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));
    manager.addCache(this._cache);
  }

  public @Nullable CachedContent get(String url) {
    Element element = this._cache.get(url);
    if (element == null) return null;
    return (CachedContent)element.getObjectValue();
  }

  public void put(CachedContent content) {
    Element element = new Element(content.url(), content);
    this._cache.put(element);
  }

}
