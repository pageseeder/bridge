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

import java.util.List;

import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSEntity;

/**
 * Represents a PageSeeder XRef.
 *
 * @author Philip Rutherford
 *
 * @since 0.8.1
 */
public final class PSXRef implements PSEntity {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * XRef XML element.
   */
  public static enum ELEMENT {
    /** xref */
    XREF,
    /** reversexref */
    REVERSEXREF,
    /** blockxref */
    BLOCKXREF,
    /** image */
    IMAGE;
    
    /**
     * Create the element from the PSML element.
     * 
     * @param name the element name
     * @return the type
     */
    public static ELEMENT fromString(String name) {
      if ("xref".equalsIgnoreCase(name))        return XREF;
      if ("reversexref".equalsIgnoreCase(name)) return REVERSEXREF;
      if ("blockxref".equalsIgnoreCase(name))   return BLOCKXREF;
      if ("image".equalsIgnoreCase(name))       return IMAGE;
      return XREF;
    }
    
    /**
     * Convert the type to the element name.
     * 
     * @return the element name
     */
    public String toString() {
      if (this == BLOCKXREF)   return "blockxref";
      if (this == XREF)        return "xref";
      if (this == REVERSEXREF) return "reversexref";
      if (this == IMAGE)       return "image";
      return "xref"; // default to xref
    }
  };

  /**
   * XRef titles to display.
   */
  public static enum DISPLAY {
    /** manual */
    MANUAL,
    /** document */
    DOCUMENT,
    /** document+fragment */
    DOCUMENT_FRAGMENT,
    /** document+manual. */
    DOCUMENT_MANUAL;
    
    /**
     * Create the display from a string.
     * 
     * @param value the string value
     * 
     * @return the display
     */
    public static DISPLAY fromString(String value) {
      if ("manual".equalsIgnoreCase(value))            return MANUAL;
      if ("document".equalsIgnoreCase(value))          return DOCUMENT;
      if ("document+fragment".equalsIgnoreCase(value)) return DOCUMENT_FRAGMENT;
      if ("document+manual".equalsIgnoreCase(value))   return DOCUMENT_MANUAL;
      return DOCUMENT;
    }
    
    /**
     * Convert the display to a string.
     * 
     * @return the string
     */
    public String toString() {
      if (this == MANUAL)            return "manual";
      if (this == DOCUMENT)          return "document";
      if (this == DOCUMENT_FRAGMENT) return "document+fragment";
      if (this == DOCUMENT_MANUAL)   return "document+manual";
      return "document"; // default to document
    }
  };

  /**
   * XRef type.
   */
  public static enum TYPE {
    /** none */
    NONE,
    /** embed */
    EMBED,
    /** transclude */
    TRANSCLUDE;
    
    /**
     * Create the type from a string.
     * 
     * @param value the string value
     * 
     * @return the type
     */
    public static TYPE fromString(String value) {
      if ("none".equalsIgnoreCase(value))        return NONE;
      if ("embed".equalsIgnoreCase(value))       return EMBED;
      if ("transclude".equalsIgnoreCase(value))  return TRANSCLUDE;
      return NONE;
    }
    
    /**
     * Convert the type to a string.
     * 
     * @return the string
     */
    public String toString() {
      if (this == NONE)         return "none";
      if (this == EMBED)        return "embed";
      if (this == TRANSCLUDE)   return "transclude";
      return "none"; // default to document
    }
  };

  /**
   * The XML element for the XRef
   */
  private ELEMENT element;

  /**
   * The XRef ID
   */
  private Long id;

  /**
   * The source document.
   */
  private PSURI sourceURI;

  /**
   * The ID of the source document.
   */
  private Long sourceURIId;

  /**
   * The fragment source.
   */
  private String sourceFragment;

  /**
   * The source document tile
   */
  private String sourceURITitle;

  /**
   * The target document.
   */
  private PSURI targetURI;

  /**
   * The ID of the target document.
   */
  private Long targetURIId;

  /**
   * The target document tile
   */
  private String targetURITitle;
  
  /**
   * The Document ID of the target document.
   */
  private String targetDocid;

  /**
   * The href pointing to the target document.
   */
  private String targetHref;

  /**
   * The target fragment.
   */
  private String targetFragment;

  /**
   * A flag specifying if the xref is a reverse link.
   */
  private boolean reverseLink;

  /**
   * The reverse title.
   */
  private String reverseTitle;

  /**
   * The manually entered tile
   */
  private String title;

  /**
   * The level of numbering.
   */
  private String level;

  /**
   * The display type.
   */
  private DISPLAY display;

  /**
   * The type of the xref.
   */
  private TYPE type;

  /**
   * The reverse type.
   */
  private TYPE reverseType;

  /**
   * The XRef's labels.
   */
  private List<String> labels;

  /**
   * The content of the xref element.
   */
  private String content;

  /**
   * Constructor
   */
  public PSXRef() {
  }

  /**
   * Constructor
   * 
   * @type the xref element
   */
  public PSXRef(ELEMENT element) {
    this.element = element;
  }
  
  /**
   * @return the id
   */
  @Override
  public Long getId() {
    return this.id;
  }

  @Override
  public String getKey() {
    return this.id != null? this.id.toString() : null;
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
  public String getIdentifier() {
    return this.id != null? this.id.toString() : null;
  }

  @Override
  public EntityValidity checkValid() {
    // image xrefs must point to an image
    if (this.targetURI != null && this.element == ELEMENT.IMAGE) {
      if (this.targetURI.getMediaType() == null || !this.targetURI.getMediaType().startsWith("image/")) {
        return EntityValidity.IMAGE_XREF_TARGET_NOT_IMAGE;    
      }      
    }
    // TODO More constraints on xref type, etc.
    return EntityValidity.OK;
  }
  
  /**
   * @return the element for the XRef
   */
  public ELEMENT getElement() {
    return this.element;
  }
  
  /**
   * @return the target URI.
   */
  public PSURI getTargetURI() {
    return this.targetURI;
  }

  /**
   * @return the target URI ID.
   */
  public Long getTargetURIId() {
    return this.targetURIId;
  }

  /**
   * @return the target URI title.
   */
  public String getTargetURITitle() {
    return this.targetURITitle;
  }

  /**
   * @return The source document.
   */
  public PSURI getSourceURI() {
    return this.sourceURI;
  }

  /**
   * @return the source URI ID.
   */
  public Long getSourceURIId() {
    return this.sourceURIId;
  }

  /**
   * @return the source URI title.
   */
  public String getSourceURITitle() {
    return this.sourceURITitle;
  }

  /**
   * @return the source fragment.
   */
  public String getSourceFragment() {
    return this.sourceFragment;
  }

  /**
   * @return the XRef's type
   */
  public TYPE getType() {
    return this.type;
  }

  /**
   * @return the reverse type
   */
  public TYPE getReverseType() {
    return this.reverseType;
  }

  /**
   * @return the display
   */
  public DISPLAY getDisplay() {
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
  public String getReverseTitle() {
    return this.reverseTitle;
  }

   /**
   * @return the numbering level
   */
  public String getLevel() {
    return this.level;
  }

  /**
   * @return the targetDocid
   */
  public String getTargetDocid() {
    return targetDocid;
  }

  /**
   * @return the targetHref
   */
  public String getTargetHref() {
    return targetHref;
  }

  /**
   * @return the target fragment.
   */
  public String getTargetFragment() {
    return this.targetFragment;
  }

  /**
   * @return the title.
   */
  public String getTitle() {
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
  public String getContent() {
    return this.content;
  }

  /**
   * @param turi the new target URI
   */
  public void setTargetURI(PSURI turi) {
    this.targetURI = turi;
  }

  /**
   * @param title the new title to set
   */
  public void setTitle(String title) {
    this.title = "".equals(title) ? null : title;
  }

  /**
   * @param cont the content to set
   */
  public void setContent(String cont) {
    this.content = cont;
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
   * @param element the element to set
   */
  public void setElement(ELEMENT element) {
    this.element = element;
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
    this.sourceURI = sourceURI;
  }

  /**
   * @param sourceURIId the source URI Id to set
   */
  public void setSourceURIId(Long sourceURIId) {
    this.sourceURIId = sourceURIId;
  }

  /**
   * @param sourceFragment the source Fragment to set
   */
  public void setSourceFragment(String sourceFragment) {
    this.sourceFragment = sourceFragment;
  }

  /**
   * @param sourceURITitle the source URI Title to set
   */
  public void setSourceURITitle(String sourceURITitle) {
    this.sourceURITitle = sourceURITitle;
  }

  /**
   * @param targetURIId the target URI Id to set
   */
  public void setTargetURIId(Long targetURIId) {
    this.targetURIId = targetURIId;
  }

  /**
   * @param targetURITitle the target URI Title to set
   */
  public void setTargetURITitle(String targetURITitle) {
    this.targetURITitle = targetURITitle;
  }

  /**
   * @param targetDocid the target Docid to set
   */
  public void setTargetDocid(String targetDocid) {
    this.targetDocid = targetDocid;
  }

  /**
   * @param targetHref the target Href to set
   */
  public void setTargetHref(String targetHref) {
    this.targetHref = targetHref;
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
  public void setReverseTitle(String reverseTitle) {
    this.reverseTitle = reverseTitle;
  }

  /**
   * @param level the level to set
   */
  public void setLevel(String level) {
    this.level = level;
  }

  /**
   * @param display the display to set
   */
  public void setDisplay(DISPLAY display) {
    this.display = display;
  }

  /**
   * @param type the type to set
   */
  public void setType(TYPE type) {
    this.type = type;
  }

  /**
   * @param reverseType the reverse Type to set
   */
  public void setReverseType(TYPE reverseType) {
    this.reverseType = reverseType;
  }

  @Override
  public String toString() {
    return (this.element)
      + "(target_urititle = " + this.targetURITitle
      + ", target_uriid = " + this.targetURIId
      + ", target_frag = " + this.targetFragment
      + ", target_docid = " + this.targetDocid
      + ", target_href = " + this.targetHref
      + ", target_display = " + this.display
      + ", type = " + this.type
      + ", level = " + this.level
      + ", labels = " + getLabelsAsString()
      + ", reverselink = " + this.reverseLink
      + ", reverse_title = " + this.reverseTitle
      + ", reverse_type = " + this.reverseType + ")";
  }

}
