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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.pageseeder.berlioz.util.ISO8601;
import org.pageseeder.bridge.EntityValidity;

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
  private Long id;

  /** The document ID of the document */
  private String docid;

  /** The description of the document */
  private String description;

  /** The user title of the document */
  private String title;

  /** The media type */
  private String mediatype;

  /** The created date */
  private Date created;

  /** The modified date */
  private Date modified;

  /** List of labels on the document */
  private List<String> labels = new ArrayList<String>();

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
  public final Long getId() {
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
    return this.id != null ? this.id.toString() : getURL();
  }

  /**
   * @return the mediatype
   */
  public final String getMediaType() {
    return this.mediatype;
  }

  /**
   * @return the Document ID
   */
  public final String getDocid() {
    return this.docid;
  }

  /**
   * @return the description
   */
  public final String getDescription() {
    return this.description;
  }

  /**
   * @return the title
   */
  public final String getTitle() {
    return this.title;
  }

  /**
   * The display title is the title if it's specified,
   * otherwise the url if it's external or else the filename..
   *
   * @return this URI's display title
   */
  public String getDisplayTitle() {
    if (this.title != null && !this.title.trim().isEmpty())
      return this.title;
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
  public final Date getCreatedDate() {
    return this.created;
  }

  /**
   * @return the modified date
   */
  public final Date getModifiedDate () {
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
  public final void setDocid(String docid) {
    this.docid = docid;
  }

  /**
   * @param description the description to set
   */
  public final void setDescription(String description) {
    this.description = description;
  }

  /**
   * @param title the title to set
   */
  public final void setTitle(String title) {
    this.title = title;
  }

  /**
   * @param labels the labels to set
   */
  public final void setLabels(List<String> labels) {
    this.labels = labels;
  }

  /**
   * @param mediatype the mediatype to set
   */
  public final void setMediaType(String mediatype) {
    this.mediatype = mediatype;
  }

  /**
   * @param date the ISO8601 date
   */
  public final void setCreatedDate(String date) {
    this.created = toDate(date);
  }

  /**
  * @param date the ISO8601 date
  */
  public final void setModifiedDate(String date) {
    this.modified = toDate(date);
  }

  // Convenience methods
  // ----------------------------------------------------------------------------------------------

  /**
   * @return The labels as a comma-separated list.
   */
  public final String getLabelsAsString() {
    if (this.labels == null) return "";
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
    if (labels == null) return;
    this.labels.clear();
    for (String label : labels.split(",")) {
      this.labels.add(label);
    }
  }

  /**
   * Check whether this URI is valid.
   *
   * @return the validity of the URI.
   */
  public EntityValidity checkURIValid() {
    if (this.docid != null && this.docid.length() > 100) return EntityValidity.DOCUMENT_DOCID_IS_TOO_LONG;
    if (this.mediatype != null && this.mediatype.length() > 100) return EntityValidity.DOCUMENT_TITLE_IS_TOO_LONG;
    if (this.title != null && this.title.length() > 250) return EntityValidity.DOCUMENT_TITLE_IS_TOO_LONG;
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

}
