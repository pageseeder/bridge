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
package org.pageseeder.bridge.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.util.ISO8601;

/**
 * The common base class for folders and documents.
 *
 * @author Christophe Lauret
 * @version 0.2.1
 * @since 0.2.0
 */
public abstract class PSURI extends PSAddressable {

  /** As per recommendation */
  private static final long serialVersionUID = 2L;

  /** The URI ID of the document */
  private @Nullable Long id;

  /** The document ID of the document */
  private @Nullable String docid;

  /** The description of the document */
  private @Nullable String description;

  /** The user title of the document */
  private @Nullable String title;

  /** The media type */
  private @Nullable String mediatype;

  /** The created date */
  private @Nullable Date created;

  /** The modified date */
  private @Nullable Date modified;

  /** List of labels on the document */
  private List<String> labels = new ArrayList<>();

  /**
   * Default constructor.
   *
   * @param scheme The scheme "http" or "https"
   * @param host   Where the resource is hosted.
   * @param port   The port (or negative to use the default port).
   * @param path   The path to the resource.
   */
  public PSURI(String scheme, String host, int port, String path) {
    super(scheme, host, port, path);
  }

  /**
   * Construct a new URI from the specified URL.
   *
   * <p>The URL may omit the scheme or authority part, it which case it will default
   * on the default values from the configuration.
   *
   * <p>Implementation note: this constructor will decompose the URL into its components.
   *
   * @param url The url.
   *
   * @throws IllegalArgumentException If the specified URL is invalid
   */
  public PSURI(String url) {
    super(url);
  }

  @Override
  public final @Nullable Long getId() {
    return this.id;
  }

  @Override
  public final String getKey() {
    return getURL();
  }

  @Override
  public boolean isIdentifiable() {
    return this.id != null || getURL() != null;
  }

  @Override
  public String getIdentifier() {
    return Objects.toString(this.id, getURL());
  }

  /**
   * @return the mediatype
   */
  public final @Nullable String getMediaType() {
    return this.mediatype;
  }

  /**
   * @return the Document ID
   */
  public final @Nullable String getDocid() {
    return this.docid;
  }

  /**
   * @return the description
   */
  public final @Nullable String getDescription() {
    return this.description;
  }

  /**
   * @return the title
   */
  public final @Nullable String getTitle() {
    return this.title;
  }

  /**
   * The display title is the title if it's specified,
   * otherwise the url if it's external or else the filename..
   *
   * @return this URI's display title
   */
  public @Nullable String getDisplayTitle() {
    String t = this.title;
    if (t != null && !t.trim().isEmpty())
      return t;
    String path = getPath();
    if (path == null) return "";
    try {
      path = URLDecoder.decode(path, "utf-8");
    } catch (UnsupportedEncodingException ex) {
      // Should not happen
    }
    if (path.indexOf('/') == -1) return path;
    return path.substring(path.lastIndexOf('/')+1);
  }

  /**
   * @return the created date
   */
  public final @Nullable Date getCreatedDate() {
    return this.created;
  }

  /**
   * @return the modified date
   */
  public final @Nullable Date getModifiedDate () {
    return this.modified;
  }


  /**
   * @return the labels
   */
  public final List<String> getLabels() {
    return this.labels;
  }

  /**
   * @param id the id to set
   */
  public final void setId(Long id) {
    this.id = id;
  }

  /**
   * @param docid the docid to set
   */
  public final void setDocid(@Nullable String docid) {
    this.docid = docid;
  }

  /**
   * @param description the description to set
   */
  public final void setDescription(@Nullable String description) {
    this.description = description;
  }

  /**
   * @param title the title to set
   */
  public final void setTitle(@Nullable String title) {
    this.title = title;
  }

  /**
   * @param labels the labels to set
   */
  public final void setLabels(List<String> labels) {
    this.labels = Objects.requireNonNull(labels, "Labels must not be null, use empty list");
  }

  /**
   * @param mediatype the mediatype to set
   */
  public final void setMediaType(@Nullable String mediatype) {
    this.mediatype = mediatype;
  }

  /**
   * @param date the ISO8601 date
   */
  public final void setCreatedDate(@Nullable String date) {
    this.created = date != null? toDate(date) : null;
  }

  /**
  * @param date the ISO8601 date
  */
  public final void setModifiedDate(@Nullable String date) {
    this.modified = date != null? toDate(date) : null;
  }

  // Convenience methods
  // ----------------------------------------------------------------------------------------------

  /**
   * @return The labels as a comma-separated list.
   */
  public final String getLabelsAsString() {
    StringBuilder s = new StringBuilder();
    for (String label : this.labels) {
      if (s.length() > 0) {
        s.append(',');
      }
      s.append(label);
    }
    return s.toString();
  }

  /**
   * @param labels The labels as a comma-separated list.
   */
  public final void setLabels(String labels) {
    this.labels.clear();
    Collections.addAll(this.labels, labels.split(","));
  }

  /**
   * Check whether this URI is valid.
   *
   * @return the validity of the URI.
   */
  public EntityValidity checkURIValid() {
    if (checkMaxLength(this.docid , 100)) return EntityValidity.DOCUMENT_DOCID_IS_TOO_LONG;
    if (checkMaxLength(this.mediatype, 100)) return EntityValidity.DOCUMENT_TITLE_IS_TOO_LONG;
    if (checkMaxLength(this.title, 250)) return EntityValidity.DOCUMENT_TITLE_IS_TOO_LONG;
    int length = 0;
    for (String label : this.labels) {
      length += label.length() + 1;
      if (length > 250) return EntityValidity.DOCUMENT_LABELS_ARE_TOO_LONG;
    }
    return EntityValidity.OK;
  }

  @Override
  public boolean isValid() {
    return checkValid() == EntityValidity.OK;
  }

  /**
   * @param date the ISO8601 date format
   * @return the
   */
  private static Date toDate(String date) {
    try {
      return ISO8601.parseAuto(date);
    } catch (ParseException ex) {
      // it should not happen but set to "the epoch" in case.
      return new Date(0);
    }
  }
  private static boolean checkMaxLength(@Nullable String s, int length) {
    return s != null && s.length() > length;
  }
}
