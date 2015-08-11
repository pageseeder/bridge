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
package org.pageseeder.bridge.net;

/**
 * A low-level utility class providing constants for the PageSeeder Servlets.
 *
 * <p>There is generally no reason to access this class directly, higher-level class should be
 * accessed instead.
 *
 * <p>Also, <i>services</i> should be used in preference to servlets which usage is
 * deprecated for the most part.
 *
 * @author Christophe Lauret
 * @version 0.3.0
 * @since 0.2.4
 */
public final class Servlets {

  /** Utility class */
  private Servlets() {
  }

  /**
   * The document browser Servlet <code>com.pageseeder.review.DocumentBrowser</code>.
   */
  public static final String DOCUMENT_BROWSER = "com.pageseeder.review.DocumentBrowser";

  /**
   * The generic search Servlet <code>com.pageseeder.search.GenericSearch</code>.
   */
  public static final String GENERIC_SEARCH = "com.pageseeder.search.GenericSearch";

  /**
   * The login Servlet.
   */
  public static final String LOGIN_SERVLET = "com.pageseeder.Login";

  /**
   * The upload search Servlet <code>com.pageseeder.upload.servlets.UploadServlet</code>.
   */
  public static final String UPLOAD_SERVLET = "com.pageseeder.upload.servlets.UploadServlet";

}
