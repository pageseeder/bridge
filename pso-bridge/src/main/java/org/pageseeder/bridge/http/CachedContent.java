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

import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * A simple object to hold the content of a cached response.
 *
 * <p>Implementation not: this class only considers the Etag and discards the last modified date.
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.11.4
 */
public final class CachedContent implements Serializable {

  /** As per requirement for Serializable */
  private static final long serialVersionUID = 1L;

  /**
   * The full URL of the content that was cached.
   */
  private final String url;

  /**
   * The content in bytes;
   */
  private final byte[] bytes;

  /**
   * The etag returned by the server
   */
  private final String etag;

  /**
   * The mediatype of the response.
   */
  private final String mediaType;

  /**
   * The character set of the content if text.
   */
  private final @Nullable String charset;

  CachedContent(String url, byte[] bytes, String contentType, String etag) {
    this.url = url;
    this.bytes = bytes;
    this.etag = etag;
    this.mediaType = Objects.requireNonNull(Header.toMediaType(contentType));
    // Preserve an unspecified charset; callers decide their fallback when decoding text.
    Charset headerCharset = Header.toCharset(contentType);
    this.charset = headerCharset != null? headerCharset.name() : null;
  }

  public String url() {
    return this.url;
  }

  public @Nullable String charset() {
    return this.charset;
  }

  public String mediaType() {
    return this.mediaType;
  }

  public InputStream getInputStream() {
    return new ByteArrayInputStream(this.bytes);
  }

  public int length() {
    return this.bytes.length;
  }

  public String etag() {
    return this.etag;
  }

  byte[] bytes() {
    return this.bytes;
  }

}
