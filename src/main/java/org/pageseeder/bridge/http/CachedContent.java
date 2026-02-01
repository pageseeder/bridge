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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * A simple object to hold the content of a cached response.
 *
 * <p>Implementation not: this class only considers the Etag and discards the last modified date.
 *
 * @author Christophe Lauret
 *
 * @version 0.11.4
 * @since 0.11.4
 */
public final class CachedContent implements Serializable {

  /** As per requirement for Serializable */
  private static final long serialVersionUID = 1L;

  /**
   * The full URL of the content that was cached.
   */
  private final String _url;

  /**
   * The content in bytes;
   */
  private final byte[] _bytes;

  /**
   * The etag returned by the server
   */
  private final String _etag;

  /**
   * The mediatype of the response.
   */
  private final String  _mediaType;

  /**
   * The character set of the content if text.
   */
  private final @Nullable String _charset;

  protected CachedContent(String url, byte[] bytes, String contentType, String etag) {
    this._url = url;
    this._bytes = bytes;
    this._etag = etag;
    this._mediaType = Objects.requireNonNull(Header.toMediaType(contentType));
    Charset charset = Objects.requireNonNull(Header.toCharset(contentType));
    this._charset = charset != null? charset.name() : null;
  }

  public String url() {
    return this._url;
  }

  public @Nullable String charset() {
    return this._charset;
  }

  public String mediaType() {
    return this._mediaType;
  }

  public InputStream getInputStream() {
    return new ByteArrayInputStream(this._bytes);
  }

  public int length() {
    return this._bytes.length;
  }

  public String etag() {
    return this._etag;
  }

  protected byte[] bytes() {
    return this._bytes;
  }

}
