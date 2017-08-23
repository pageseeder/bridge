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
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * The common base class for folders, documents and external URIs (URLS).
 *
 * @author Christophe Lauret
 * @version 0.12.0
 * @since 0.12.0
 */
public abstract class URI extends Addressable implements Serializable, XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /** The URI ID */
  private final long _id;

  /** The document ID */
  private final @Nullable String _docid;

  /** The description */
  private final @Nullable String _description;

  /** The user title */
  private final @Nullable String _title;

  /** The media type */
  private final String _mediatype;

  /** The created date */
  private final @Nullable OffsetDateTime _created;

  /** The modified date */
  private final @Nullable OffsetDateTime _modified;

  /** List of labels on the document */
  private final LabelList _labels;

  /**
   * Default constructor.
   *
   * @param scheme The scheme "http" or "https"
   * @param host   Where the resource is hosted.
   * @param port   The port (or negative to use the default port).
   * @param path   The path to the resource.
   */
  URI(long id, String scheme, String host, int port, String path, @Nullable String title, @Nullable String docid, @Nullable String description, String mediatype, @Nullable OffsetDateTime created, @Nullable OffsetDateTime modified, LabelList labels) {
    super(scheme, host, port, path);
    this._id = id;
    this._docid = docid;
    this._description = description;
    this._title = title;
    this._mediatype = mediatype;
    this._created = created;
    this._modified = modified;
    this._labels = labels;
  }

  public final long getId() {
    return this._id;
  }

  /**
   * @return the mediatype
   */
  public final String getMediaType() {
    return this._mediatype;
  }

  /**
   * @return the Document ID
   */
  public final @Nullable String getDocid() {
    return this._docid;
  }

  /**
   * @return the description
   */
  public final @Nullable String getDescription() {
    return this._description;
  }

  /**
   * @return the title
   */
  public final @Nullable String getTitle() {
    return this._title;
  }

  /**
   * The display title is the title if it's specified,
   * otherwise the url if it's external or else the filename..
   *
   * @return this URI's display title
   */
  public String getDisplayTitle() {
    String t = this._title;
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
  public final @Nullable OffsetDateTime getCreatedDate() {
    return this._created;
  }

  /**
   * @return the modified date
   */
  public final @Nullable OffsetDateTime getModifiedDate () {
    return this._modified;
  }

  /**
   * @return the labels
   */
  public final LabelList getLabels() {
    return this._labels;
  }

  public abstract boolean isExternal();

  public abstract boolean isFolder();

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("uri");
    if (this._id > 0)
      xml.attribute("id", Long.toString(this._id));
    xml.attribute("scheme", this.getScheme());
    xml.attribute("host", this.getHost());
    xml.attribute("port", this.getPort());
    xml.attribute("path", this.getPath());
    xml.attribute("decodedpath", this.getDecodedPath());

    // Optional attributes
    if (this._title != null)
      xml.attribute("title", this._title);
    if (this._created != OffsetDateTime.MIN)
      xml.attribute("created", this._created.toString()); // TODO date format
    if (this._modified != OffsetDateTime.MIN)
      xml.attribute("created", this._modified.toString()); // TODO date format
    if (this._mediatype != null)
      xml.attribute("mediatype", this._mediatype);

    // Attributes for documents, urls and folders
    if (this.isExternal()) {
      xml.attribute("external", "true");
      if (isFolder())
        xml.attribute("folder", "true");
      // TODO archived boolean	no	If "true" this URI is archived (for URLs only)
    } else {
      if (this._docid != null)
        xml.attribute("docid", this._docid);
      // documenttype document-type	no	Document type (not for URLs)
      // size	xs:long	no	The file size in bytes (URLs or non-PSML documents only)
    }
    xml.closeElement();
  }

  static class Builder extends Addressable.Builder {

    long id = -1;
    @Nullable String docid = null;
    String description = "";
    @Nullable String title = null;
    String mediatype = "";
    OffsetDateTime created = OffsetDateTime.MIN;
    OffsetDateTime modified = OffsetDateTime.MIN;
    LabelList labels = LabelList.NO_LABELS;

    /**
     * @param id the id to set
     */
    public final Builder id(Long id) {
      this.id = id;
      return this;
    }

    /**
     * @param docid the docid to set
     */
    public final Builder docid(@Nullable String docid) {
      this.docid = docid;
      return this;
    }

    /**
     * @param description the description to set
     */
    public final Builder description(@Nullable String description) {
      this.description = description;
      return this;
    }

    /**
     * @param title the title to set
     */
    public final Builder title(@Nullable String title) {
      this.title = title;
      return this;
    }

    /**
     * @param labels the labels to set
     */
    public final Builder labels(List<String> labels) {
      this.labels = new LabelList(labels);
      return this;
    }

    /**
     * @param labels The labels as a comma-separated list.
     */
    public final Builder labels(String labels) {
      this.labels = LabelList.parse(labels);
      return this;
    }

    /**
     * @param mediatype the mediatype to set
     */
    public final Builder mediaType(@Nullable String mediatype) {
      this.mediatype = mediatype;
      return this;
    }

    /**
     * @param date the ISO8601 date
     */
    public final Builder created(@Nullable String date) {
      this.created = date != null? OffsetDateTime.parse(date) : null;
      return this;
    }

    /**
     * @param date the ISO8601 date
     */
    public final Builder modified(@Nullable String date) {
      this.modified = date != null? OffsetDateTime.parse(date) : null;
      return this;
    }

  }

}
