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

import java.io.IOException;
import java.io.Serializable;

import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

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
  private final long _id;

  /** The full name of the group */
  private final GroupID _name;

  /** The title of the group */
  private final String _title;

  /** The description of the group */
  private final String _description;

  /** The owner of the group */
  private final String _owner;

  /** Who has access to the group */
  private final GroupAccess _access;

  /** If it is a common group */
  private final boolean _common;

  /** URL to a related Website */
  private final String _relatedURL;

  /**
   * Create a new group
   */
  public BasicGroup(long id, GroupID name, String title, String description, String owner, GroupAccess access, boolean common, String relatedURL) {
    this._id = id;
    this._name = name;
    this._title = title;
    this._description = description;
    this._owner = owner;
    this._access = access;
    this._common = common;
    this._relatedURL = relatedURL;
  }

  public long getId() {
    return this._id;
  }

  /**
   * Returns the full name of the group.
   *
   * @return the name of the group.
   */
  public GroupID getName() {
    return this._name;
  }

  /**
   * Returns the title of the group.
   *
   * @return the title of the group.
   */
  public String getTitle() {
    return this._title;
  }

  /**
   * Returns the access of the group.
   *
   * @return the access of the group.
   */
  public GroupAccess getAccess() {
    return this._access;
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
    return this._common;
  }

  /**
   * Returns the related URL of the group.
   *
   * @return the related URL of the group.
   */
  public String getRelatedURL() {
    return this._relatedURL;
  }

  /**
   * Returns the description of this group.
   *
   * @return the description
   */
  public final String getDescription() {
    return this._description;
  }

  /**
   * Returns the owner of the group.
   *
   * @return the owner of the group.
   */
  public String getOwner() {
    return this._owner;
  }

  /**
   * Returns the name of the parent project based on the name of this group.
   *
   * <p>The name of the parent is the part of the name that is before the last dash.
   *
   * @return the name of the parent or <code>null</code> if the name is <code>null</code> or does not include a dash.
   */
  public GroupID getParentName() {
    return this._name.parent();
  }

  /**
   * Returns the short name of this group.
   *
   * <p>The short name is the part of the name that is after the last dash.
   *
   * @return the short name
   */
  public String getShortName() {
    String n = this._name.toString();
    int dash = n.lastIndexOf('-');
    return dash > 0 ? n.substring(dash + 1) : n;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement(isProject()? "project" : "group");
    if (this._id > 0) {
      xml.attribute("id", Long.toString(this._id));
    }
    xml.attribute("name", this._name.toString());
    if (this._title.length() > 0) {
      xml.attribute("title", this._title);
    }
    xml.attribute("description", this._description);
    xml.attribute("access", this._access.name().toLowerCase());
    xml.attribute("common", Boolean.toString(this._common));
    xml.attribute("owner", this._owner);
    if (this._relatedURL.length() > 0) {
      xml.attribute("relatedurl", this._relatedURL);
    }
    xml.closeElement();
  }

  public static class Builder {

    long id = -1;
    GroupID name = GroupID.ROOT;
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
     */
    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    /**
     * Sets the full name of this group.
     *
     * @param name the name to set
     */
    public Builder name(String name) {
      this.name = new GroupID(name);
      return this;
    }

    /**
     * Sets the title of this group.
     *
     * @param title the title to set
     */
    public Builder title(String title) {
      this.title = title;
      return this;
    }

    /**
     * Sets the descriptions of this group.
     *
     * @param description the description to set
     */
    public final Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the owner of the group.
     *
     * @param owner the owner to set
     */
    public Builder owner(String owner) {
      this.owner = owner;
      return this;
    }

    /**
     * Sets the access of the group.
     *
     * @param access the owner to set
     */
    public Builder access(String access) {
      this.access = GroupAccess.forName(access);
      return this;
    }

    /**
     * Sets the common of the group.
     *
     * @param common the owner to set
     */
    public Builder common(boolean common) {
      this.common = common;
      return this;
    }

    public Builder relatedURL(String relatedURL) {
      this.relatedURL = relatedURL;
      return this;
    }
  }

}
