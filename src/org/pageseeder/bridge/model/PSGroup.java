/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSEntity;

/**
 * A PageSeeder group.
 *
 * <p>The public ID of a group is its name.
 *
 * @author Christophe Lauret
 * @version 0.2.1
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
  private Long id;

  /** The full name of the group */
  private String name;

  /** The owner of the group */
  private String owner;

  /** The description of the group */
  private String description;

  /** The default role when users are invited */
  private PSRole defaultRole;

  /** The default role when users are invited */
  private PSNotification defaultNotification;

  /** The type of details */
  private String detailsType;

  /**
   * Create a new group without any identifier.
   */
  public PSGroup() {
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
  public Long getId() {
    return this.id;
  }

  @Override
  public String getKey() {
    return this.name;
  }

  @Override
  public boolean isIdentifiable() {
    return this.id != null || this.name != null;
  }

  @Override
  public String getIdentifier() {
    return this.id != null? this.id.toString() : this.name;
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the description
   */
  public final String getDescription() {
    return this.description;
  }

  /**
   * @param description the description to set
   */
  public final void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the defaultRole
   */
  public final PSRole getDefaultRole() {
    return this.defaultRole;
  }

  /**
   * @return the defaultNotification
   */
  public final PSNotification getDefaultNotification() {
    return this.defaultNotification;
  }

  /**
   * @param defaultRole the defaultRole to set
   */
  public final void setDefaultRole(PSRole defaultRole) {
    this.defaultRole = defaultRole;
  }

  /**
   * @param defaultNotification the defaultNotification to set
   */
  public final void setDefaultNotification(PSNotification defaultNotification) {
    this.defaultNotification = defaultNotification;
  }

  public String getDetailsType() {
    return this.detailsType;
  }

  public void setDetailsType(String detailsType) {
    this.detailsType = detailsType;
  }

  /**
   * @return the owner
   */
  public String getOwner() {
    return this.owner;
  }

  /**
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
   */
  @Override
  public EntityValidity checkValid() {
    if (this.name        != null && this.name.length()        > 60) return EntityValidity.GROUP_NAME_IS_TOO_LONG;
    if (this.owner       != null && this.owner.length()       > 100) return EntityValidity.GROUP_OWNER_IS_TOO_LONG;
    if (this.description != null && this.description.length() > 250) return EntityValidity.GROUP_DESCRIPTION_IS_TOO_LONG;
    if (this.detailsType != null && this.detailsType.length() > 150) return EntityValidity.GROUP_DETAILTYPE_IS_TOO_LONG;
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
    return "G("+this.id+":"+this.name+")";
  }

  // Static helpers
  // ---------------------------------------------------------------------------------------------

  /**
   * An unmodifiable list of names which cannot be used as group names.
   */
  public static final Collection<String> RESERVED_GROUP_NAMES;
  static {
    String[] reserved = new String[]{
      "page", "block", "tree", "uri", "fullpage", "embed", "psadmin", "bundle", "service", "error",
      "weborganic","woconfig", "servlet", "psdoc", "filter","group","home","member","project"
    };
    RESERVED_GROUP_NAMES = Collections.unmodifiableList(Arrays.asList(reserved));
  }

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
  public static boolean isValidGroupName(String name) {
    if (name == null) return false;
    String pjname = name;
    int dash = name.indexOf('-');
    if (dash  != -1) pjname = name.substring(0, dash);
    return name.matches(REGEX_GROUP_NAME) && !RESERVED_GROUP_NAMES.contains(pjname) && !name.endsWith("-silent") && name.indexOf("--") == -1;
  }

}
