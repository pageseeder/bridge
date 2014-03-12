/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSEntity;

/**
 * Represents a PageSeeder folder.
 *
 * A folder is a URI which acts as a container for other folders and documents.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public final class PSFolder extends PSURI implements PSEntity {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  public PSFolder(String url) {
    super(url);
  }

  /**
   * Known constraints on Member are based on SQL definition:
   *
   * <pre>
   *  DocID VARCHAR(100) NULL,
   *  Scheme VARCHAR(20) CHARACTER SET ascii NULL,
   *  Path VARCHAR(500) CHARACTER SET ascii NULL,
   *  HostID INTEGER NULL,
   *  Port INTEGER NULL,
   *  Title VARCHAR(250) NULL,
   *  Behavior VARCHAR(80) NULL,
   *  LastModified DATETIME NULL,
   *  Description TEXT NULL,
   *  UserTitle VARCHAR(250) NULL,
   *  Type VARCHAR(100) NULL,
   *  Status VARCHAR(40) NULL,
   *  Labels VARCHAR(250) NULL,
   * </pre>
   */
  @Override
  public EntityValidity checkValid() {
    EntityValidity validity = checkURIValid();
    if (!"folder".equals(this.getMediaType()))
      validity = EntityValidity.FOLDER_IS_NOT_A_FOLDER;
    return validity;
  }

  @Override
  public String toString() {
    return "F("+this.getId()+":"+this.getURL()+")";
  }
}
