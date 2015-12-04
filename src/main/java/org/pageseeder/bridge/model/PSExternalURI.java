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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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
   * The display title is the title if it's specified,
   * otherwise the url if it's external or else the filename..
   * 
   * @return this URI's display title
   */
  public String getDisplayTitle() {
    String title = getTitle();
    if (title != null && !title.trim().isEmpty())
      return title;   
    try {
      return URLDecoder.decode(getURL(), "utf-8");
    } catch (UnsupportedEncodingException ex) {
      // Should not happen
    }
    return "";
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
    return "U("+getId()+":"+getURL()+")";
  }

}
