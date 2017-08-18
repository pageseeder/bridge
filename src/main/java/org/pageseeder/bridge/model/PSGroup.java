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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSEntity;

/**
 * A PageSeeder group.
 *
 * <p>The public ID of a group is its name.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.2.0
 */
public class PSGroup implements PSEntity {

  /** As per recommendation */
  private static final long serialVersionUID = 2L;

  /**
   * Regular expression that group names must match.
   *
   * Restrictions:
   *    - first character is ALPHA
   *    - other characters are ALPHA / DIGIT / "_" / "~" / "-"
   */
  private static final String REGEX_GROUP_NAME = "^[a-z][a-z0-9_~\\-]+$";

  /** PageSeeder database ID. */
  private @Nullable Long id;

  /** The full name of the group */
  private @Nullable String name;

  /** The title of the group */
  private @Nullable String title;

  /** The owner of the group */
  private @Nullable String owner;

  /** The description of the group */
  private @Nullable String description;

  /** The default role when users are invited */
  private @Nullable PSRole defaultRole;

  /** The default role when users are invited */
  private @Nullable PSNotification defaultNotification;

  /** The type of details */
  private @Nullable String detailsType;

  /** The template folder for customizations (aka style owner)*/
  private @Nullable String template;

  /**
   * Create a new group without any identifier.
   */
  public PSGroup() {}

  /**
   * Create a new group from the specified ID.
   *
   * @param id The name of the group.
   */
  public PSGroup(Long id) {
    this.id = id;
  }

  /**
   * Create a new group with a public identifier.
   *
   * <p>Note: the constructor does not check that the name is valid.
   *
   * @param name The name of the group.
   */
  public PSGroup(String name) {
    this.name = name;
  }

  @Override
  public @Nullable Long getId() {
    return this.id;
  }

  @Override
  public @Nullable String getKey() {
    return this.name;
  }

  @Override
  public boolean isIdentifiable() {
    return this.id != null || this.name != null;
  }

  @Override
  public String getIdentifier() {
    return Objects.toString(this.id, this.name);
  }

  /**
   * Returns the full name of the group.
   *
   * @return the name of the group.
   */
  public @Nullable String getName() {
    return this.name;
  }

  /**
   * Returns the title of the group.
   *
   * @return the title of the group.
   */
  public @Nullable String getTitle() {
    return this.title;
  }

  /**
   * Returns the name of the parent project based on the name of this group.
   *
   * <p>The name of the parent is the part of the name that is before the last dash.
   *
   * @return the name of the parent or <code>null</code> if the name is <code>null</code> or does not include a dash.
   */
  public @Nullable String getParentName() {
    String n = this.name;
    if (n == null) return null;
    int dash = n.lastIndexOf('-');
    return dash > 0 ? n.substring(0, dash) : null;
  }

  /**
   * Returns the short name of this group.
   *
   * <p>The short name is the part of the name that is after the last dash.
   *
   * @return the short name or <code>null</code> if the name is <code>null</code>.
   */
  public @Nullable String getShortName() {
    String n = this.name;
    if (n == null) return null;
    int dash = n.lastIndexOf('-');
    return dash > 0 ? n.substring(dash + 1) : n;
  }

  /**
   * Sets the PageSeeder ID of this group.
   *
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Sets the full name of this group.
   *
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the title of this group.
   *
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Returns the description of this group.
   *
   * @return the description
   */
  public final @Nullable String getDescription() {
    return this.description;
  }

  /**
   * Sets the descriptions of this group.
   *
   * @param description the description to set
   */
  public final void setDescription(@Nullable String description) {
    this.description = description;
  }

  /**
   * Returns the default role assigned to members when they join the group.
   *
   * @return the default role assigned to members when they join the group.
   */
  public final @Nullable PSRole getDefaultRole() {
    return this.defaultRole;
  }

  /**
   * Returns the default notification assigned to members when they join the group.
   *
   * @return the default notification assigned to members when they join the group.
   */
  public final @Nullable PSNotification getDefaultNotification() {
    return this.defaultNotification;
  }

  /**
   * Set the default role assigned to members when they join the group.
   *
   * @param role the default role assigned to members when they join the group.
   */
  public final void setDefaultRole(@Nullable PSRole role) {
    this.defaultRole = role;
  }

  /**
   * Returns the default notification assigned to members when they join the group.
   *
   * @param notification the default notification assigned to members when they join the group.
   */
  public final void setDefaultNotification(@Nullable PSNotification notification) {
    this.defaultNotification = notification;
  }

  /**
   * Returns the type of membership details for this group.
   *
   * @return the type of membership details for this group.
   */
  public @Nullable String getDetailsType() {
    return this.detailsType;
  }

  /**
   * Return the template folder for customizations (aka style owner).
   * @return the style owner.
   */
  public @Nullable String getTemplate() {
    return this.template;
  }

  /**
   * Sets type of membership details for this group.
   *
   * <p>The details type is the name of the configuration file defining the details to
   * use in the group. For example, <code>account.xml</code>.
   *
   * @param type the type of membership details for this group.
   */
  public void setDetailsType(String type) {
    this.detailsType = type;
  }

  /**
   * Set the style owner for this group.
   * <p>The style owner is the template folder for customizations </p>
   *
   * @param template the style owner for this group.
   */
  public void setTemplate(String template) {
    this.template = template;
  }

  /**
   * Returns the owner of the group.
   *
   * @return the owner of the group.
   */
  public @Nullable String getOwner() {
    return this.owner;
  }

  /**
   * Sets the owner of the group.
   *
   * @param owner the owner to set
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * Known constraints on Member are based on SQL definition:
   *
   * <pre>
   *  GroupName VARCHAR(60) NULL
   *  GroupDesc VARCHAR(250) NULL
   *  DetailsForm VARCHAR(150) NULL
   *  Owner VARCHAR(100) NULL
   * </pre>
   *
   * {@inheritDoc}
   */
  @Override
  public EntityValidity checkValid() {
    if (checkMaxLength(this.name, 60)) return EntityValidity.GROUP_NAME_IS_TOO_LONG;
    if (checkMaxLength(this.owner, 100)) return EntityValidity.GROUP_OWNER_IS_TOO_LONG;
    if (checkMaxLength(this.description, 250)) return EntityValidity.GROUP_DESCRIPTION_IS_TOO_LONG;
    if (checkMaxLength(this.detailsType, 150)) return EntityValidity.GROUP_DETAILTYPE_IS_TOO_LONG;
    if (checkMaxLength(this.template, 60)) return EntityValidity.GROUP_TEMPLATE_IS_TOO_LONG;

    if (!isValidGroupName(this.name)) return EntityValidity.GROUP_NAME_IS_INVALID;
    // TODO Constraints on possible roles and notifications?

    return EntityValidity.OK;
  }

  @Override
  public boolean isValid() {
    return checkValid() == EntityValidity.OK;
  }

  @Override
  public String toString() {
    return "G(" + this.id + ":" + this.name + ")";
  }

  // Static helpers
  // ---------------------------------------------------------------------------------------------

  /**
   * An unmodifiable list of names which cannot be used as group names.
   */
  public static final Collection<String> RESERVED_GROUP_NAMES = Collections.unmodifiableList(
      Arrays.asList("page", "block", "tree", "uri", "fullpage", "embed", "psadmin", "bundle",
          "service", "error", "weborganic", "woconfig", "servlet", "psdoc", "filter", "group",
          "home", "member", "project"));

  /**
   * Checks if given String is a valid group name. Restrictions are:
   *    - first character is ALPHA
   *    - other characters are ALPHA / DIGIT / "_" / "~" / "-"
   *    - cannot contain "--"
   *    - not a reserved name
   *
   * @param name the group name to check.
   * @return <code>true</code> if the group name is valid;
   *         <code>false</code> otherwise.
   */
  public static boolean isValidGroupName(@Nullable String name) {
    if (name == null || name.length() == 0) return false;
    String pjname = name;
    int dash = name.indexOf('-');
    if (dash != -1) {
      pjname = name.substring(0, dash);
    }
    return name.matches(REGEX_GROUP_NAME) && !RESERVED_GROUP_NAMES.contains(pjname) && !name.endsWith("-silent") && !name.contains("--");
  }

  private static boolean checkMaxLength(@Nullable String s, int length) {
    return s != null && s.length() > length;
  }
}
