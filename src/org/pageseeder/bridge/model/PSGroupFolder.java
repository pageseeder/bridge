/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSEntity;

/**
 * A group folder (as opposed to simple folder).
 *
 * @author Christophe Lauret
 * @version 0.2.1
 * @since 0.2.0
 */
public class PSGroupFolder extends PSAddressable implements PSEntity {

  /** As per recommendation */
  private static final long serialVersionUID = 2L;

  /** ID of the Group URI in PageSeeder */
  private Long id;

  /** Whether it points to an external location */
  private boolean isExternal = false;

  /** Whether it is public */
  private boolean isPublic = false;

  /**
   * Creates a new group folder from the specified URL.
   *
   * @param url The URL for this group folder.
   */
  public PSGroupFolder(String url) {
    super(url);
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
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the isExternal
   */
  public final boolean isExternal() {
    return this.isExternal;
  }

  /**
   * @return the isPublic
   */
  public final boolean isPublic() {
    return this.isPublic;
  }

  /**
   * @param isExternal the isExternal to set
   */
  public final void setExternal(boolean isExternal) {
    this.isExternal = isExternal;
  }

  /**
   * @param isPublic the isPublic to set
   */
  public final void setPublic(boolean isPublic) {
    this.isPublic = isPublic;
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
    // TODO
//    if (this.scheme != null && this.scheme.length()        > 60) return EntityValidity.GROUP_NAME_IS_TOO_LONG;
//    if (this.host   != null && this.host.length()       > 100) return EntityValidity.GROUP_OWNER_IS_TOO_LONG;
//    if (this.port   != null && this.port.length() > 250) return EntityValidity.GROUP_DESCRIPTION_IS_TOO_LONG;
//    if (this.path   != null && this.path.length() > 150) return EntityValidity.GROUP_DETAILTYPE_IS_TOO_LONG;

    return EntityValidity.OK;
  }

  @Override
  public boolean isValid() {
    return checkValid() == EntityValidity.OK;
  }

  @Override
  public String toString() {
    return "GF("+this.id+":"+this.getURL()+")";
  }

  // Static helpers
  // ---------------------------------------------------------------------------------------------

  public static PSGroupFolder forPath(String path) {
    PSGroupFolder f = new PSGroupFolder(path);
    PSConfig p = PSConfig.singleton();
    f.setScheme(p.getScheme());
    f.setHost(p.getHost());
    f.setPort(p.getPort());
    f.setPath(path);
    return f;
  }

}
