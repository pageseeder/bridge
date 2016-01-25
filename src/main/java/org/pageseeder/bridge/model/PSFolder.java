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

  /**
   * Construct a new folder from the specified URL.
   *
   * <p>The URL may omit the scheme or authority part, it which case it will default
   * on the default values from the configuration.
   *
   * <p>Implementation note: this constructor will decompose the URL into its components.
   *
   * @param url The URL of the folder.
   *
   * @throws IllegalArgumentException If the specified URL is invalid
   */
  public PSFolder(String url) {
    super(url);
  }

  /**
   * @return the foldername
   */
  public String getFoldername() {
    return getFoldername(getPath());
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
    EntityValidity validity = checkURIValid();
    if (!"folder".equals(getMediaType())) {
      validity = EntityValidity.FOLDER_IS_NOT_A_FOLDER;
    }
    return validity;
  }

  @Override
  public String toString() {
    return "F("+getId()+":"+getURL()+")";
  }

  // Convenience methods
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the foldername from the path or URL.
   *
   * @param url The path or URL
   *
   * @return the text after the last '/' in the path or URL
   */
  public static String getFoldername(String url) {
    if (url == null) return null;
    int solidus = url.lastIndexOf('/');
    return url.substring(solidus+1);
  }
}
