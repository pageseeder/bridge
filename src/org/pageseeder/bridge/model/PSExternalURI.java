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
 * Represents a PageSeeder URL.
 *
 * A URL is an external URI.
 *
 * @author Jean-Baptiste Reure
 * @author Christophe Lauret
 * @version 0.1.0
 */
public final class PSExternalURI extends PSURI implements PSEntity {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * If this URL is a folder
   */
  private boolean folder = false;

  /**
   * Construct a new document from the specified URL.
   *
   * <p>The URL may omit the scheme or authority part, it which case it will default
   * on the default values from the configuration.
   *
   * <p>Implementation note: this constructor will decompose the URL into its components.
   *
   * @param url The URL to the document.
   *
   * @throws IllegalArgumentException If the specified URL is invalid
   */
  public PSExternalURI(String url) {
    super(url);
  }

  /**
   * Default constructor.
   *
   * @param scheme The scheme "http" or "https"
   * @param host   Where the resource is hosted.
   * @param port   The port (or negative to use the default port).
   * @param path   The path to the resource.
   */
  public PSExternalURI(String scheme, String host, int port, String path) {
    super(scheme, host, port, path);
  }

  /**
   * @return the folder flag
   */
  public boolean isFolder() {
    return this.folder;
  }

  /**
   * @param folder the folder flag to set
   */
  public void setFolder(boolean folder) {
    this.folder = folder;
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
   *
   * {@inheritDoc}
   */
  @Override
  public EntityValidity checkValid() {
    EntityValidity validity = super.checkURIValid();
    return validity;
  }

  @Override
  public boolean isValid() {
    return checkValid() == EntityValidity.OK;
  }

  @Override
  public String toString() {
    return "U("+this.getId()+":"+this.getURL()+")";
  }

}
