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
import org.pageseeder.xmlwriter.XMLWritable;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Represents a PageSeeder folder.
 *
 * A folder is a URI which acts as a container for other folders and documents.
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Folder extends URI implements Serializable, XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  public Folder(long id, String scheme, String host, int port, String path, @Nullable String title, @Nullable String docid, @Nullable String description, String mediatype, @Nullable OffsetDateTime created, @Nullable OffsetDateTime modified, LabelList labels) {
    super(id, scheme, host, port, path, title, docid, description, mediatype, created, modified, labels);
  }

//
//  /**
//   * Construct a new folder from the specified URL.
//   *
//   * <p>The URL may omit the scheme or authority part, it which case it will default
//   * on the default values from the configuration.
//   *
//   * <p>Implementation note: this constructor will decompose the URL into its components.
//   *
//   * @param url The URL of the folder.
//   *
//   * @throws IllegalArgumentException If the specified URL is invalid
//   */
//  public Folder(String url) {
//    super(url);
//  }

  @Override
  public boolean isFolder() {
    return true;
  }

  @Override
  public boolean isExternal() {
    return false;
  }

  /**
   * @return the foldername
   */
  public @Nullable String getFoldername() {
    return getFoldername(getPath());
  }

  @Override
  public String toString() {
    return "Folder("+getId()+":"+getURL()+")";
  }

  // Convenience methods
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the foldername from the path or URL.
   *
   * @param url The path or URL
   *
   * @return the text after the last '/' in the path or URL
   */
  public static @Nullable String getFoldername(@Nullable String url) {
    if (url == null) return null;
    int solidus = url.lastIndexOf('/');
    return url.substring(solidus+1);
  }

  public static class Builder extends URI.Builder<Builder> {

    public Folder build() {
      return new Folder(id, scheme(), host(), port(), path(), title, docid, description, mediatype, created, modified, labels);
    }
  }

}
