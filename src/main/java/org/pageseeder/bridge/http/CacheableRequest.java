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
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSCredentials;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Creates a new request using an HTTP cache and only on GET method.
 *
 * @author Christophe Lauret
 *
 * @version 0.11.12
 * @since 0.11.4
 */
public final class CacheableRequest implements HttpRequest {

  /**
   * Max size to cache a reponse.
   */
  private static final int CACHE_THRESHOLD = 1_000_000;

  /**
   * HttpCache to check
   */
  private final HttpCache _cache;

  /**
   * Path to server
   */
  private final String _path;

  /**
   * List of get parameters
   */
  private final List<Parameter> _parameters = new ArrayList<>();

  private PSConfig config = PSConfig.getDefault();

  /**
   * We just record if gzip is going to be used if we need to make a request
   */
  private boolean gzip = false;

  private @Nullable PSCredentials credentials = null;

  /**
   * Creates a new request to PageSeeder.
   *
   * @param path   The path without the site prefix (e.g. <code>/ps</code>)
   */
  public CacheableRequest(HttpCache cache, String path) {
    this._cache = cache;
    this._path = path;
  }

  @Override
  public CacheableRequest parameter(String name, String value) {
    this._parameters.add(new Parameter(name, value));
    return this;
  }

  /**
   * Add all the parameters to this request.
   *
   * @param parameters A map of parameter names and values to add
   *
   * @return This request.
   */
  @Override
  public CacheableRequest parameters(Map<String, String> parameters) {
    for (Entry<String, String> p : parameters.entrySet()) {
      parameter(p.getKey(), p.getValue());
    }
    return this;
  }

  @Override
  public CacheableRequest using(PSCredentials credentials) {
    this.credentials = credentials;
    return this;
  }

  @Override
  public CacheableRequest gzip(boolean enable) {
    this.gzip = enable;
    return this;
  }

  @Override
  public HttpResponse response() {
    String url = Request.toURLString(this._path);
    if (this._parameters.size() > 0) {
      StringBuilder q = new StringBuilder();
      for (Parameter p : this._parameters) {
        if (q.length() > 0) {
          q.append("&");
        }
        p.append(q);
      }
      url = url + (url.indexOf('?') > 0 ? '&':'?')+q.toString();
    }

    // Retrieve content from Cache
    CachedContent content = this._cache.get(url);
    if (content == null) return fetch(url);
    else return refresh(content);
  }

  /**
   * Fetch the content from PageSeeder and return the corresponding request.
   */
  private HttpResponse fetch(String url) {
    Response response = toRequest().response();
    if (response.isSuccessful()) {
      // OK check we have an etag an the response is not too long
      String etag = response.etag();
      long length = response.length();
      String contentType = response.getContentType();
      if (etag != null && contentType != null && length < CACHE_THRESHOLD) {
        byte[] bytes = response.consumeBytes();
        CachedContent updated = new CachedContent(url, bytes, contentType, etag);
        this._cache.put(updated);
        return new CachedResponse(updated);
      }
    }
    // If we haven't returned we send a proxy response
    return response;
  }


  private HttpResponse refresh(CachedContent content) {
    Response response = toRequest().etag(content.etag()).response();
    int code = response.code();
    if (code == 304) // Not modified
      return new CachedResponse(content);
    else if (code == 200) {
      // OK check we have an etag an the response is not too long
      String etag = response.etag();
      long length = response.length();
      String contentType = response.getContentType();
      if (etag != null && contentType != null && length < CACHE_THRESHOLD) {
        byte[] bytes = response.consumeBytes();
        CachedContent updated = new CachedContent(content.url(), bytes, contentType, etag);
        this._cache.put(updated);
        return new CachedResponse(updated);
      }
    }
    // If we haven't returned we send the actual response
    return response;
  }

  @Override
  public HttpRequest config(PSConfig config) {
    this.config = config;
    return this;
  }

  @Override
  public HttpRequest timeout(int timeout) {
    // TODO Auto-generated method stub
    return this;
  }

  @Override
  public HttpRequest body(String body) {
    return toRequest().body(body);
  }

  @Override
  public HttpRequest body(byte [] body) {
    return toRequest().body(body);
  }

  @Override
  public HttpRequest etag(String etag) {
    return toRequest().etag(etag);
  }

  @Override
  public HttpRequest header(String name, String value) {
    return toRequest().header(name, value);
  }

  @Override
  public @Nullable String header(String name) {
    return toRequest().header(name);
  }

  @Override
  public List<Header> headers() {
    return toRequest().headers();
  }

  /**
   * @return the actual request corresponding to the current cached request.
   */
  private Request toRequest() {
    Request request = new Request(Method.GET, this._path);
    PSCredentials credentials = this.credentials;
    if (credentials != null) {
      request.using(credentials);
    }
    for (Parameter p : this._parameters) {
      request.parameter(p.name(), p.value());
    }
    if (this.gzip)
      request.gzip(true);
    request.config(this.config);
    return request;
  }

}
