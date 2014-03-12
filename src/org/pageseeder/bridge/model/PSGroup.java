/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

import java.util.ArrayList;
import java.util.Collection;

import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSEntity;

/**
 * A PageSeeder group.
 *
 * @author Christophe Lauret
 */
public class PSGroup implements PSEntity {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

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

  public PSGroup() {
  }

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
   * List of names which cannot be used as group names.
   */
  public static final Collection<String> RESERVED_GROUP_NAMES = new ArrayList<String>();
  static {
    RESERVED_GROUP_NAMES.add("page");
    RESERVED_GROUP_NAMES.add("block");
    RESERVED_GROUP_NAMES.add("tree");
    RESERVED_GROUP_NAMES.add("uri");
    RESERVED_GROUP_NAMES.add("fullpage");
    RESERVED_GROUP_NAMES.add("embed");
    RESERVED_GROUP_NAMES.add("psadmin");
    RESERVED_GROUP_NAMES.add("bundle");
    RESERVED_GROUP_NAMES.add("service");
    RESERVED_GROUP_NAMES.add("error");
    RESERVED_GROUP_NAMES.add("weborganic");
    RESERVED_GROUP_NAMES.add("woconfig");
    RESERVED_GROUP_NAMES.add("servlet");
    RESERVED_GROUP_NAMES.add("psdoc");
    RESERVED_GROUP_NAMES.add("filter");
    RESERVED_GROUP_NAMES.add("group");
    RESERVED_GROUP_NAMES.add("home");
    RESERVED_GROUP_NAMES.add("member");
    RESERVED_GROUP_NAMES.add("project");
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
