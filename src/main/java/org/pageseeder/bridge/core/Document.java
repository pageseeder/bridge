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
package org.pageseeder.bridge.core;

import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;

/**
 * Represents a PageSeeder document.
 *
 * A document is a URI which can have some content and can be typed.
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Document extends URI {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  public static final String DEFAULT_DOCUMENT_TYPE = "default";

  /** The document type (PSML only) */
  private String type = "default";

  public Document(long id, String scheme, String host, int port, String path, @Nullable String title, @Nullable String docid, String description, String mediatype, @Nullable OffsetDateTime created, @Nullable OffsetDateTime modified, LabelList labels, String documentType) {
    super(id, scheme, host, port, path, title, docid, description, mediatype, created, modified, labels);
    this.type = documentType;
  }

  /**
   * @return the filename
   */
  public @Nullable String getFilename() {
    return getFilename(getPath());
  }

  /**
   * @return the filename
   */
  public @Nullable String getFolderURL() {
    return getFolder(getURL());
  }

  @Override
  public boolean isExternal() {
    return false;
  }

  @Override
  public boolean isFolder() {
    return "folder".equals(getMediaType());
  }

  /**
   * @return the type
   */
  public String getType() {
    return this.type;
  }

  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "Document("+getId()+":"+getURL()+")";
  }

  // Convenience methods
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the filename from the path or URL.
   *
   * @param url The path or URL
   *
   * @return the text after the last '/' in the path or URL
   */
  public static @Nullable String getFilename(@Nullable String url) {
    if (url == null) return null;
    int solidus = url.lastIndexOf('/');
    return url.substring(solidus+1);
  }

  /**
   * Returns the folder path from the path or URL.
   *
   * @param url The path or URL
   *
   * @return the text before the last '/' in the path or URL
   */
  public static @Nullable String getFolder(@Nullable String url) {
    if (url == null) return null;
    int solidus = url.lastIndexOf('/');
    return url.substring(0, solidus);
  }

  public static class Builder extends URI.Builder<Document.Builder> {

    private String documentType = DEFAULT_DOCUMENT_TYPE;

    public Builder documentType(String documentType) {
      this.documentType = documentType;
      return this;
    }

    public Document build() {
      return new Document(id, scheme(), host(), port(), path(), title, docid, description, mediatype, created, modified, labels, documentType);
    }
  }

}
