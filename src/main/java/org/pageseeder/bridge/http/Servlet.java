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
package org.pageseeder.bridge.http;

/**
 * A enumeration of PageSeeder Servlets.
 *
 * @author Christophe Lauret
 * @version 0.9.1
 * @since 0.9.1
 */
public enum Servlet {

  /**
   * The document browser Servlet <code>com.pageseeder.review.DocumentBrowser</code>.
   */
  DOCUMENT_BROWSER("com.pageseeder.review.DocumentBrowser"),

  /**
   * The generic search Servlet <code>com.pageseeder.search.GenericSearch</code>.
   */
  GENERIC_SEARCH("com.pageseeder.search.GenericSearch"),

  /**
   * The login Servlet.
   */
  LOGIN("com.pageseeder.Login"),

  /**
   * The upload search Servlet <code>com.pageseeder.upload.servlets.UploadServlet</code>.
   */
  UPLOAD("com.pageseeder.upload.servlets.UploadServlet");


  private final String _path;

  Servlet(String name) {
    this._path = "/servlet/"+name;
  }

  public String toPath() {
    return this._path;
  }

}
