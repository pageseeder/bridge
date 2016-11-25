/*
 * Copyright 2016 Allette Systems (Australia)
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
package org.pageseeder.bridge.berlioz.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.pageseeder.bridge.berlioz.util.IOUtils;
import org.slf4j.LoggerFactory;


/**
 * A Servlet to log the user out by invalidating the session.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public final class LogoutServlet extends HttpServlet {

  /**
   * As per requirement for the <code>Serializable</code> interface.
   */
  private static final long serialVersionUID = -3343755604269705856L;

  /**
   * URL of resource of the ping.
   */
  private static final String RESOURCE_URL = "/org.pageseeder.bridge.berlioz/servlet/FFFFFF-0.png";

  /**
   * If the content type is specified.
   */
  private String _contentType = null;

  /**
   * The corresponding data.
   */
  private byte[] _data = null;

  @Override
  public void init(ServletConfig config) throws ServletException {
    String content = config.getInitParameter("content-type");
    if ("image/png".equals(content)) {
      this._contentType = content;
      this._data = IOUtils.getResource(RESOURCE_URL);
      if (this._data == null) {
        LoggerFactory.getLogger(LogoutServlet.class).warn("Unable to get resource "+RESOURCE_URL);
      }
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    // Get the authenticator
    HttpSession session = req.getSession();

    // Only if there is a session
    if (session != null) {
      session.invalidate();
      session = null;
    }

    // Make it uncacheable
    res.setHeader("Cache-Control", "no-cache, no-store");

    // If the data is defined and found
    if (this._data != null) {

      // Set the headers
      res.setContentType(this._contentType);
      res.setContentLength(this._data.length);

      // Copy the data
      ServletOutputStream out = res.getOutputStream();
      out.write(this._data);
      out.close();

    } else {

      // No data
      res.setStatus(HttpServletResponse.SC_NO_CONTENT);
      res.setContentLength(0);

    }

  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    doGet(req, res);
  }

}
