/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.core;

import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSEntity;

/**
 * Represents a PageSeeder document.
 *
 * A document is a URI which can have some content and can be typed.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public final class PSDocument extends PSURI implements PSEntity {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /** The document type (PSML only) */
  private String type = "default";

  /**
   * Default constructor.
   */
  public PSDocument(String url) {
    super(url);
  }

  /**
   * Default constructor.
   */
  public PSDocument(String scheme, String host, int port, String url) {
    super(scheme, host, port, url);
  }

  /**
   * @return the filename
   */
  public final String getFilename() {
    return getFilename(getPath());
  }

  /**
   * @return the filename
   */
  public final String getFolderURL() {
    return getFolder(getURL());
  }

  /**
   * @return the type
   */
  public final String getType() {
    return this.type;
  }

  /**
   * @param filename the filename to set
   */
  public final void setFilename(String filename) {
    String path = getPath();
    if (path != null) {
      setPath(getFolder(path)+'/'+filename);
    } else {
      setPath("/"+filename);
    }
  }

  /**
   * @param type the type to set
   */
  public final void setType(String type) {
    this.type = type;
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
    if ("folder".equals(this.getMediaType()))
      validity = EntityValidity.DOCUMENT_IS_A_FOLDER;
    return validity;
  }

  @Override
  public boolean isValid() {
    return checkValid() == EntityValidity.OK;
  }

  @Override
  public String toString() {
    return "D("+this.getId()+":"+this.getURL()+")";
  }

  // Convenience methods
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the filename from the path or URL.
   *
   * @param url The path or URL
   *
   * @return the text after the last '/' in the path or URL
   */
  public static String getFilename(String url) {
    if (url == null) return null;
    int solidus = url.lastIndexOf('/');
    return url.substring(solidus+1);
  }

  /**
   * Returns the folder path from the path or URL.
   *
   * @param url The path or URL
   *
   * @return the text before the last '/' in the path or URL
   */
  public static String getFolder(String url) {
    if (url == null) return null;
    int solidus = url.lastIndexOf('/');
    return url.substring(0, solidus);
  }

}
