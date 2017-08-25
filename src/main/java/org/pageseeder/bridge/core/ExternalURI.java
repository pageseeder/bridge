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

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.xmlwriter.XMLWritable;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.OffsetDateTime;

/**
 * Represents a PageSeeder URL.
 *
 * A URL is an external URI.
 *
 * @author Jean-Baptiste Reure
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class ExternalURI extends URI implements Serializable, XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * If this URL is a folder
   */
  private boolean folder = false;

  public ExternalURI(long id, String scheme, String host, int port, String path, @Nullable String title, @Nullable String docid, @Nullable String description, String mediatype, @Nullable OffsetDateTime created, @Nullable OffsetDateTime modified, LabelList labels, boolean folder) {
    super(id, scheme, host, port, path, title, docid, description, mediatype, created, modified, labels);
    this.folder = folder;
  }

  /**
   * The display title is the title if it's specified,
   * otherwise the url if it's external or else the filename..
   * 
   * @return this URI's display title
   */
  public String getDisplayTitle() {
    String title = getTitle();
    if (title != null && !title.trim().isEmpty())
      return title;   
    try {
      return URLDecoder.decode(getURL(), "utf-8");
    } catch (UnsupportedEncodingException ex) {
      // Should not happen
    }
    return "";
  }

  @Override
  public boolean isExternal() {
    return true;
  }

  /**
   * @return the folder flag
   */
  public boolean isFolder() {
    return this.folder;
  }

  /**
   * @param folder the folder flag to set
   */
  public void setFolder(boolean folder) {
    this.folder = folder;
  }

  @Override
  public String toString() {
    return "ExternalURI("+getId()+":"+getURL()+")";
  }


  public static class Builder extends URI.Builder<Builder> {

    private boolean isFolder = false;

    public Builder isFolder(boolean folder) {
      this.isFolder = folder;
      return this;
    }

    public ExternalURI build() {
      return new ExternalURI(id, scheme(), host(), port(), path(), title, docid, description, mediatype, created, modified, labels, isFolder);
    }
  }


}
