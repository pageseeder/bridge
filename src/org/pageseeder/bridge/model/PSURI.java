/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

import java.util.ArrayList;
import java.util.List;

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

  /** List of labels on the document */
  private List<String> labels = new ArrayList<String>();

  /**
   * Default constructor.
   */
  public PSURI(String url) {
    super(url);
  }

  /**
   * Default constructor.
   */
  public PSURI(String scheme, String host, int port, String path) {
    super(scheme, host, port, path);
  }

  @Override
  public final Long getId() {
    return this.id;
  }

  @Override
  public final String getKey() {
    return this.getURL();
  }

  @Override
  public boolean isIdentifiable() {
    return this.id != null || this.getURL() != null;
  }

  @Override
  public String getIdentifier() {
    return this.id != null? this.id.toString() : this.getURL();
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

  // Convenience methods
  // ----------------------------------------------------------------------------------------------

  /**
   * @return The labels as a comma-separated list.
   */
  public final String getLabelsAsString() {
    StringBuilder s = new StringBuilder();
    for (String label : this.labels) {
      if (s.length() > 0) s.append(',');
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

  public EntityValidity checkURIValid() {
    if (this.docid     != null && this.docid.length()       > 100)
      return EntityValidity.DOCUMENT_DOCID_IS_TOO_LONG;
    if (this.mediatype != null && this.mediatype.length()   > 100)
      return EntityValidity.DOCUMENT_TITLE_IS_TOO_LONG;
    if (this.title     != null && this.title.length() > 250)
      return EntityValidity.DOCUMENT_TITLE_IS_TOO_LONG;
    int length = 0;
    for (String label : this.labels) {
      length += label.length() +1;
      if (length > 250) return EntityValidity.DOCUMENT_LABELS_ARE_TOO_LONG;
    }
    return EntityValidity.OK;
  }

  @Override
  public boolean isValid() {
    return checkValid() == EntityValidity.OK;
  }

}
