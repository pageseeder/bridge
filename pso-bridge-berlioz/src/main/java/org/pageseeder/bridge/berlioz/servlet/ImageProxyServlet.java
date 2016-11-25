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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.berlioz.auth.AuthSessions;
import org.pageseeder.bridge.berlioz.util.IOUtils;
import org.pageseeder.bridge.control.DocumentManager;
import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.net.PSHTTPResource;
import org.pageseeder.bridge.net.PSHTTPResourceType;

/**
 * Fetches images on PageSeeder on behalf of the user currently logged in.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public final class ImageProxyServlet extends HttpServlet implements Servlet {

  /** As per requirement */
  private static final long serialVersionUID = 1L;

  /** Folders where the images are cached */
  private File cache = null;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ServletContext context = config.getServletContext();
    File contextPath = new File(context.getRealPath("/"));
    File contextCache = new File(contextPath, "WEB-INF/cache/images");
    if (!contextCache.exists()) {
      contextCache.mkdirs();
    }
    this.cache = contextCache;
  }

  @Override
  public void destroy() {
    super.destroy();
    this.cache = null;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    String pathInfo = req.getPathInfo();
    String imageURIID = pathInfo;

    // TODO: We should be able to look up the URI ID from the path also
    // but we need the URI ID for the caching key.application level cache
    if (!pathInfo.toLowerCase().matches("/\\d+(\\.png|\\.jpg|\\.gif)?")) {
      if (imageURIID.matches("^.*(\\.png|\\.jpg|\\.gif)$?")) {
        PSSession session = AuthSessions.getPSSession(req);
        DocumentManager manager = new DocumentManager(session);
        PSDocument document = null;
        try {
          PSHTTPResource imageSource = new PSHTTPResource(PSHTTPResourceType.RESOURCE, imageURIID.replace("//", "/"));
          URL url = imageSource.toURL(session);
          document = manager.getDocument(url.toString().split(";")[0], new PSGroup(req.getParameter("group")));
        } catch (APIException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        if (document != null) {
          Long id = document.getId();
          StringBuffer imageNewURIID = new StringBuffer();
          imageNewURIID.append(File.separator);
          imageNewURIID.append(id);
          //get image format
          String[] getFormatArray = imageURIID.split("\\.");
          imageNewURIID.append(".");
          imageNewURIID.append(getFormatArray[getFormatArray.length-1]);
          imageURIID = imageNewURIID.toString();
        }
      } else {
        res.sendError(HttpServletResponse.SC_NOT_FOUND);
          return;
      }
    }

    // File where the data is cached
    File cached = new File(this.cache, imageURIID);
    ImageResource image = null;

    if (cached.exists()) {
      // Read from the locally cached file
      image = getFromFile(cached);

    } else {

      // Build URL to fetch from PageSeeder
      PSHTTPResource resource = new PSHTTPResource(PSHTTPResourceType.RESOURCE, "/ps/uri"+imageURIID);
      PSSession session = AuthSessions.getPSSession(req);
      URL url = resource.toURL(session);

      // Extract metadata
      image = getFromPageSeeder(url);
      if (image == null) {
        res.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      // Make a copy in the file system
      image.writeTo(cached);

    }

    // Set the headers
    res.setContentType(image.media());
    res.setContentLength(image.length());
    res.setDateHeader("Last-Modified", image.modified());

    // Then copy to the output
    ServletOutputStream out = res.getOutputStream();
    image.writeTo(out);

  }

  /**
   * Returns the image resource from PageSeeder
   *
   * @param url The URL to the image on PageSeeder
   * @return The corresponding resource
   *
   * @throws IOException If an error occurs while reading the file
   */
  private ImageResource getFromPageSeeder(URL url) throws IOException {

    // Extract metadata
    URLConnection connection = url.openConnection();
    String media  = connection.getContentType();
    int length    = connection.getContentLength();
    long modified = connection.getLastModified();

    // Copy content into buffer
    BufferedInputStream in = null;
    byte[] data = null;
    try (InputStream raw = connection.getInputStream()) {
      if (raw == null)
        return new ImageResource(modified, length, media, new byte[]{});
      in = new BufferedInputStream(raw);
      data = IOUtils.toByteArray(in, length);
    } catch (FileNotFoundException ex) {
      return null;
    }
    return new ImageResource(modified, length, media, data);
  }

  /**
   * Returns the image resource from the locally cached file
   *
   * @param file The file acting as a cache
   * @return The corresponding resource
   *
   * @throws IOException If an error occurs while reading the file
   * @throws UnsupportedOperationException If the file size is larger than MAX_INTEGER
   */
  private ImageResource getFromFile(File file) throws IOException {
    String extension = file.getName().substring(file.getName().indexOf('.')+1);
    String media = "image/"+ ("jpg".equals(extension)? "jpeg" :  extension);
    if (file.length() > Integer.MAX_VALUE)
      throw new UnsupportedOperationException("File too large");
    int length = (int)file.length(); // Should never exceed MAX_INT
    long modified = file.lastModified();
    byte[] data = Files.readAllBytes(file.toPath());
    return new ImageResource(modified, length, media, data);
  }

  /**
   * An immutable image resource.
   *
   * @author Christophe Lauret
   */
  private final static class ImageResource {
    private final long _modified;
    private final int _length;
    private final String _media;
    private final byte[] _data;

    /**
     * Sole constructor
     */
    public ImageResource(long modified, int length, String media, byte[] data) {
      this._modified = modified;
      this._length = length;
      this._media = media;
      this._data = data;
    }

    /** @return the last modified date */
    public final long modified() {
      return this._modified;
    }

    /** @return the length of resource */
    public final int length() {
      return this._length;
    }

    /** @return the media type */
    public final String media() {
      return this._media;
    }

    /** Write the data the output stream */
    public final void writeTo(OutputStream out) throws IOException {
      out.write(this._data);
    }

    /** Write the data the file */
    public final void writeTo(File file) throws IOException {
      Path target = file.toPath();
      Files.write(target, this._data);// TODO check options
    }

  }
}
