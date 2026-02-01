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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSEntity;

/**
 * Represents a PageSeeder XRef.
 *
 * @author Philip Rutherford
 *
 * @version 0.10.2
 * @since 0.8.1
 */
public final class PSXRef implements PSEntity {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * XRef titles to display.
   */
  public enum Display {

    /** manual */
    MANUAL("manual"),

    /** document */
    DOCUMENT("document"),

    /** document+fragment */
    DOCUMENT_FRAGMENT("document+fragment"),

    /** document+manual. */
    DOCUMENT_MANUAL("document+manual");

    private final String _value;

    Display(String value) {
      this._value = value;
    }

    public final String value(){
      return this._value;
    }

    /**
     * Create the display from a string.
     *
     * @param value the string value
     *
     * @return the display
     */
    public static Display fromString(@Nullable String value) {
      for (Display display : values()) {
        if (display._value.equalsIgnoreCase(value)) return display;
      }
      // Fallback on document
      return DOCUMENT;
    }

    /**
     * Convert the display to a string.
     *
     * @return the string
     */
    @Override
    public String toString() {
      return this._value;
    }
  }

  /**
   * XRef type.
   */
  public enum Type {

    /** none */
    NONE,
    /** embed */
    EMBED,
    /** transclude */
    TRANSCLUDE,
    /** image **/
    IMAGE;

    /**
     * Create the type from a string.
     *
     * @param value the string value
     *
     * @return the type
     */
    public static Type fromString(@Nullable String value) {
      if ("none".equalsIgnoreCase(value))        return NONE;
      if ("embed".equalsIgnoreCase(value))       return EMBED;
      if ("transclude".equalsIgnoreCase(value))  return TRANSCLUDE;
      if ("image".equalsIgnoreCase(value))       return IMAGE;
      return NONE;
    }

    /**
     * Convert the type to a string.
     *
     * @return the string
     */
    @Override
    public String toString() {
      if (this == EMBED)        return "embed";
      if (this == TRANSCLUDE)   return "transclude";
      if (this == IMAGE)        return "image";
      return "none";
    }
  }

  /**
   * The XRef ID
   */
  private @Nullable Long id;

  /**
   * The ID of the source document.
   */
  private @Nullable Long sourceURIId;

  /**
   * The source fragment.
   */
  private @Nullable String sourceFragment;

  /**
   * The source document tile
   */
  private @Nullable String sourceURITitle;

  /**
   * The Document ID of the source document.
   */
  private @Nullable String sourceDocid;

  /**
   * The source document media type
   */
  private @Nullable String sourceMediaType;

  /**
   * The href pointing to the source document.
   */
  private @Nullable String sourceHref;

  /**
   * The ID of the target document.
   */
  private @Nullable Long targetURIId;

  /**
   * The target document tile
   */
  private @Nullable String targetURITitle;

  /**
   * The Document ID of the target document.
   */
  private @Nullable String targetDocid;

  /**
   * The target document media type
   */
  private @Nullable String targetMediaType;

  /**
   * The href pointing to the target document.
   */
  private @Nullable String targetHref;

  /**
   * The target fragment.
   */
  private @Nullable String targetFragment = "default";

  /**
   * A flag specifying if the xref is a reverse link.
   */
  private boolean reverseLink = true;

  /**
   * The reverse title.
   */
  private @Nullable String reverseTitle;

  /**
   * The manually entered tile
   */
  private @Nullable String title;

  /**
   * The level of numbering.
   */
  private @Nullable Integer level;

  /**
   * The display type.
   */
  private @Nullable Display display;

  /**
   * The type of the xref.
   */
  private @Nullable Type type;

  /**
   * The reverse type.
   */
  private @Nullable Type reverseType;

  /**
   * The XRef's labels.
   */
  private List<String> labels = new ArrayList<>();

  /**
   * Constructor
   */
  public PSXRef() {
  }

  /**
   * @return the id
   */
  @Override
  public @Nullable Long getId() {
    return this.id;
  }

  @Override
  public @Nullable String getKey() {
    Long id = this.id;
    return id != null? id.toString() : null;
  }

  @Override
  public boolean isValid() {
    return checkValid() == EntityValidity.OK;
  }

  @Override
  public boolean isIdentifiable() {
    return this.id != null;
  }

  @Override
  public @Nullable String getIdentifier() {
    Long id = this.id;
    return id != null? id.toString() : null;
  }

  @Override
  public EntityValidity checkValid() {
    // image xrefs must point to an image
    String tmt = this.targetMediaType;
    if (this.type == Type.IMAGE && tmt != null && !tmt.startsWith("image/"))
      return EntityValidity.IMAGE_XREF_TARGET_NOT_IMAGE;
    // TODO More constraints on xref type, etc.
    return EntityValidity.OK;
  }

  /**
   * @return the target URI ID.
   */
  public @Nullable Long getTargetURIId() {
    return this.targetURIId;
  }

  /**
   * @return the target URI title.
   */
  public @Nullable String getTargetURITitle() {
    return this.targetURITitle;
  }

  /**
   * @return the target Media Type
   */
  public @Nullable String getTargetMediaType() {
    return this.targetMediaType;
  }

  /**
   * @return the source URI ID.
   */
  public @Nullable Long getSourceURIId() {
    return this.sourceURIId;
  }

  /**
   * @return the source URI title.
   */
  public @Nullable String getSourceURITitle() {
    return this.sourceURITitle;
  }

  /**
   * @return the source Media Type
   */
  public @Nullable String getSourceMediaType() {
    return this.sourceMediaType;
  }

  /**
   * @return the sourceDocid
   */
  public @Nullable String getSourceDocid() {
    return this.sourceDocid;
  }

  /**
   * @return the source Href
   */
  public @Nullable String getSourceHref() {
    return this.sourceHref;
  }

  /**
   * @return the source fragment.
   */
  public @Nullable String getSourceFragment() {
    return this.sourceFragment;
  }

  /**
   * @return the XRef's type
   */
  public @Nullable Type getType() {
    return this.type;
  }

  /**
   * @return the reverse type
   */
  public @Nullable Type getReverseType() {
    return this.reverseType;
  }

  /**
   * @return the display
   */
  public @Nullable Display getDisplay() {
    return this.display;
  }

  /**
   * @return true if this XRef is a reverse xref.
   */
  public boolean getReverseLink() {
    return this.reverseLink;
  }

  /**
   * @return the reverse title.
   */
  public @Nullable String getReverseTitle() {
    return this.reverseTitle;
  }

   /**
   * @return the numbering level
   */
  public @Nullable Integer getLevel() {
    return this.level;
  }

  /**
   * @return the target Docid
   */
  public @Nullable String getTargetDocid() {
    return this.targetDocid;
  }

  /**
   * @return the target Href
   */
  public @Nullable String getTargetHref() {
    return this.targetHref;
  }

  /**
   * @return the target fragment.
   */
  public @Nullable String getTargetFragment() {
    return this.targetFragment;
  }

  /**
   * @return the title.
   */
  public@Nullable String getTitle() {
    return this.title;
  }

  /**
   * @return the labels
   */
  public final List<String> getLabels() {
    return this.labels;
  }

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
   * @return the content of the XRef.
   */
  public @Nullable String getContent() {
    if (this.display == Display.MANUAL)
      return this.title;
    if (this.display == Display.DOCUMENT_FRAGMENT)
      return this.targetURITitle + ": " + this.targetFragment;
    if (this.display == Display.DOCUMENT_MANUAL)
      return this.targetURITitle + ": " + this.title;
    return this.targetURITitle;
  }

  /**
   * @param targetURI  the new target URI
   */
  public void setTargetURI(PSURI targetURI) {
    this.targetDocid = targetURI.getDocid();
    this.targetURIId = targetURI.getId();
    this.targetURITitle = targetURI.getDisplayTitle();
    this.targetMediaType = targetURI.getMediaType();
    this.targetHref = (targetURI instanceof PSExternalURI) ? targetURI.getURL() : targetURI.getPath();
  }

  /**
   * @param title the new title to set
   */
  public void setTitle(@Nullable String title) {
    this.title = title;
  }

  /**
   * @param labels The labels as a comma-separated list.
   */
  public final void setLabels(String labels) {
    this.labels.clear();
    Collections.addAll(this.labels, labels.split(","));
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @param sourceURI the source URI to set
   */
  public void setSourceURI(PSURI sourceURI) {
    this.sourceDocid = sourceURI.getDocid();
    this.sourceURIId = sourceURI.getId();
    this.sourceURITitle = sourceURI.getDisplayTitle();
    this.sourceMediaType = sourceURI.getMediaType();
    this.sourceHref = (sourceURI instanceof PSExternalURI) ? sourceURI.getURL() : sourceURI.getPath();
  }

  /**
   * @param sourceFragment the source Fragment to set
   */
  public void setSourceFragment(String sourceFragment) {
    this.sourceFragment = sourceFragment;
  }

  /**
   * @param targetFragment the target Fragment to set
   */
  public void setTargetFragment(String targetFragment) {
    this.targetFragment = targetFragment;
  }

  /**
   * @param reverseLink the reverseLink to set
   */
  public void setReverseLink(boolean reverseLink) {
    this.reverseLink = reverseLink;
  }

  /**
   * @param reverseTitle the reverse Title to set
   */
  public void setReverseTitle(@Nullable String reverseTitle) {
    this.reverseTitle = reverseTitle;
  }

  /**
   * @param level the level to set
   */
  public void setLevel(@Nullable Integer level) {
    this.level = level;
  }

  /**
   * @param display the display to set
   */
  public void setDisplay(Display display) {
    this.display = display;
  }

  /**
   * @param type the type to set
   */
  public void setType(Type type) {
    this.type = type;
  }

  /**
   * @param reverseType the reverse Type to set
   */
  public void setReverseType(Type reverseType) {
    this.reverseType = reverseType;
  }

  /**
   * @param labels the labels to set
   */
  public void setLabels(List<String> labels) {
    this.labels = Objects.requireNonNull(labels, "Labels must not be null, use empty list");
  }

  @Override
  public String toString() {
    return "XRef(id = " + this.id
      + ", target_urititle = " + this.targetURITitle
      + ", target_uriid = " + this.targetURIId
      + ", target_frag = " + this.targetFragment
      + ", target_docid = " + this.targetDocid
      + ", target_href = " + this.targetHref
      + ", target_mediatype = " + this.targetMediaType
      + ", title = " + this.title
      + ", display = " + this.display
      + ", type = " + this.type
      + ", level = " + this.level
      + ", labels = " + getLabelsAsString()
      + ", reverselink = " + this.reverseLink
      + ", source_urititle = " + this.sourceURITitle
      + ", source_uriid = " + this.sourceURIId
      + ", source_frag = " + this.sourceFragment
      + ", source_docid = " + this.sourceDocid
      + ", source_href = " + this.sourceHref
      + ", source_mediatype = " + this.sourceMediaType
      + ", reverse_title = " + this.reverseTitle
      + ", reverse_type = " + this.reverseType + ")";
  }

}
