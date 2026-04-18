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

import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

/**
 * A base class for PageSeeder groups and projects.
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public abstract class BasicGroup implements Serializable, XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /** PageSeeder database ID. */
  private final long id;

  /** The full name of the group */
  private final GroupName name;

  /** The title of the group */
  private final String title;

  /** The description of the group */
  private final String description;

  /** The owner of the group */
  private final String owner;

  /** Who has access to the group */
  private final GroupAccess access;

  /** If it is a common group */
  private final boolean common;

  /** URL to a related Website */
  private final String relatedURL;

  /**
   * Create a new group
   */
  public BasicGroup(long id, GroupName name, String title, String description, String owner, GroupAccess access, boolean common, String relatedURL) {
    this.id = id;
    this.name = Objects.requireNonNull(name, "Group name is required");
    this.title = Objects.requireNonNull(title, "Group title is required");
    this.description = Objects.requireNonNull(description, "Description is required");
    this.owner = Objects.requireNonNull(owner, "Owner is required");
    this.access = Objects.requireNonNull(access, "Access is required");
    this.common = common;
    this.relatedURL = Objects.requireNonNull(relatedURL, "Related URL is required");
  }

  public long getId() {
    return this.id;
  }

  /**
   * Returns the full name of the group.
   *
   * @return the name of the group.
   */
  public GroupName getName() {
    return this.name;
  }

  /**
   * Returns the title of the group.
   *
   * @return the title of the group.
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Returns the access of the group.
   *
   * @return the access of the group.
   */
  public GroupAccess getAccess() {
    return this.access;
  }

  /**
   * Indicates whether this is a project.
   */
  public abstract boolean isProject();

  /**
   * Indicates whether this is a common group
   *
   * @return the title of the group.
   */
  public boolean isCommon() {
    return this.common;
  }

  /**
   * Returns the related URL of the group.
   *
   * @return the related URL of the group.
   */
  public String getRelatedURL() {
    return this.relatedURL;
  }

  /**
   * Returns the description of this group.
   *
   * @return the description
   */
  public final String getDescription() {
    return this.description;
  }

  /**
   * Returns the owner of the group.
   *
   * @return the owner of the group.
   */
  public String getOwner() {
    return this.owner;
  }

  /**
   * Returns the name of the parent project based on the name of this group.
   *
   * <p>The name of the parent is the part of the name that is before the last dash.
   *
   * @return the name of the parent or <code>null</code> if the name is <code>null</code> or does not include a dash.
   */
  public GroupName getParentName() {
    return this.name.parent();
  }

  /**
   * Returns the short name of this group.
   *
   * <p>The short name is the part of the name that is after the last dash.
   *
   * @return the short name
   */
  public String getShortName() {
    String n = this.name.toString();
    int dash = n.lastIndexOf('-');
    return dash > 0 ? n.substring(dash + 1) : n;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement(isProject()? "project" : "group");
    if (this.id > 0) {
      xml.attribute("id", Long.toString(this.id));
    }
    xml.attribute("name", this.name.toString());
    if (!this.title.isEmpty()) {
      xml.attribute("title", this.title);
    }
    xml.attribute("description", this.description);
    xml.attribute("access", this.access.name().toLowerCase());
    xml.attribute("common", Boolean.toString(this.common));
    xml.attribute("owner", this.owner);
    if (!this.relatedURL.isEmpty()) {
      xml.attribute("relatedurl", this.relatedURL);
    }
    xml.closeElement();
  }

  public static class Builder {

    long id = -1;
    GroupName name = GroupName.ROOT;
    String title = "";
    String description = "";
    String owner = "";
    GroupAccess access = GroupAccess.MEMBER;
    boolean common;
    String relatedURL = "";

    /**
     * Sets the PageSeeder ID of this group.
     *
     * @param id the id to set
     *
     * @return this builder for chaining
     */
    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    /**
     * Sets the full name of this group.
     *
     * @param name the name to set
     *
     * @return this builder for chaining
     */
    public Builder name(String name) {
      this.name = new GroupName(name);
      return this;
    }

    /**
     * Sets the title of this group.
     *
     * @param title the title to set
     *
     * @return this builder for chaining
     */
    public Builder title(String title) {
      this.title = title;
      return this;
    }

    /**
     * Sets the descriptions of this group.
     *
     * @param description the description to set
     *
     * @return this builder for chaining
     */
    public final Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the owner of the group.
     *
     * @param owner the owner to set
     *
     * @return this builder for chaining
     */
    public Builder owner(String owner) {
      this.owner = owner;
      return this;
    }

    /**
     * Sets the access of the group.
     *
     * @param access the owner to set
     *
     * @return this builder for chaining
     */
    public Builder access(String access) {
      this.access = GroupAccess.forName(access);
      return this;
    }

    /**
     * Sets the common of the group.
     *
     * @param common the owner to set
     *
     * @return this builder for chaining
     */
    public Builder common(boolean common) {
      this.common = common;
      return this;
    }

    /**
     * Sets the related URL of the group.
     *
     * @param relatedURL the related URL to set
     *
     * @return this builder for chaining
     */
    public Builder relatedURL(String relatedURL) {
      this.relatedURL = relatedURL;
      return this;
    }
  }

}
