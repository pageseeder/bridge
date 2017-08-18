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

import org.eclipse.jdt.annotation.NonNull;

/**
 *
 * @author Christophe Lauret
 *
 * @version 0.11.4
 * @since 0.11.4
 */
public final class HttpClient {

  private static final HttpClient SINGLETON = new HttpClient();

  private final HttpCache _cache;

  private HttpClient() {
    this._cache = new HttpCache("HttpCache");
  }

  public static HttpClient singleton() {
    return SINGLETON;
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * @param path  The PageSeeder servlet to use
   */
  public HttpRequest newRequest(String path) {
    return new CacheableRequest(this._cache, path);
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * @param method The HTTP method to use
   * @param path   The PageSeeder servlet to use
   */
  public HttpRequest newRequest(Method method, String path) {
    if (method == Method.GET) return new CacheableRequest(this._cache, path);
    return new Request(method, path);
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * @param servlet  The PageSeeder servlet to use
   */
  public HttpRequest newRequest(Method method, Servlet servlet) {
    if (method == Method.GET) return new CacheableRequest(this._cache, servlet.toPath()).parameter("xformat", "xml");
    return new Request(method, servlet);
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * <p>This method will automatically constructs the correct URL for the requested
   * service using the URI variables.
   *
   * @param template The PageSeeder service URL template to use
   * @param variables The variables to inject in the URL path.
   *
   * @return The corresponding request
   */
  public HttpRequest newService(String template, @NonNull Object... variables) {
    return new CacheableRequest(this._cache, ServicePath.newPath(template, variables));
  }

  /**
   * Creates a new request to a PageSeeder document with the specified URI ID.
   *
   * @param uri The document URI ID
   *
   * @return The corresponding request
   *
   * @throws IllegalArgumentException If the URI ID is not a positive long value.
   */
  public HttpRequest newDocument(long uri) {
    if (uri <= 0) throw new IllegalArgumentException("URI ID must be strictly positive.");
    return new CacheableRequest(this._cache, "/uri/"+uri);
  }

  /**
   * Creates a new request to a PageSeeder document with the specified path.
   *
   * @param path The HTTP method
   *
   * @return The corresponding request
   */
  public HttpRequest newDocument(String path) {
    return new CacheableRequest(this._cache, path);
  }

}
