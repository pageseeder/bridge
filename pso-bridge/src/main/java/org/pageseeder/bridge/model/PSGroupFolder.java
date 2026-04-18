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

import java.util.Objects;

import org.jspecify.annotations.Nullable;
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
  private @Nullable Long id;

  /** Whether it points to an external location */
  private boolean isExternal = false;

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
  public @Nullable Long getId() {
    return this.id;
  }

  @Override
  public String getKey() {
    return getURL();
  }

  @Override
  public boolean isIdentifiable() {
    return this.id != null || getURL() != null;
  }

  @Override
  public String getIdentifier() {
    return Objects.toString(this.id, getURL());
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
   * @param isExternal the isExternal to set
   */
  public final void setExternal(boolean isExternal) {
    this.isExternal = isExternal;
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
    return "GF("+this.id+":"+getURL()+")";
  }

  // Static helpers
  // ---------------------------------------------------------------------------------------------

  public static PSGroupFolder forPath(String path) {
    PSGroupFolder f = new PSGroupFolder(path);
    PSConfig p = PSConfig.getDefault();
    f.setScheme(p.getScheme());
    f.setHost(p.getHost());
    f.setPort(p.getPort());
    f.setPath(path);
    return f;
  }

}
