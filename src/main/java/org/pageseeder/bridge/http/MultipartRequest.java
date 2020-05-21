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
package org.pageseeder.bridge.http;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.PSSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple fluent class to define HTTP multipart requests to PageSeeder.
 *
 * <h3>Examples</h3>
 *
 * <p>Invoking a service
 * <pre>
 *   // Uploading files
 *   Response r = new MultipartRequest(Servlet.UPLOAD)
 *                    .parameter(name, value)
 *                    .using(token)
 *                    .addPart(file1)
 *                    .addPart(file2)
 *                    .response();
 * </pre>
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.9.1
 */
public final class MultipartRequest extends BasicRequest {

  /**
   * Logger for this class.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(MultipartRequest.class);

  /**
   * Byte for carriage return + line feed.
   */
  private static final byte[] CRLF = "\r\n".getBytes(StandardCharsets.UTF_8);

  /**
   * The part boundary.
   */
  private final String _boundary;

  /**
   * The underlying HTTP connection.
   */
  private @Nullable HttpURLConnection connection = null;

  /**
   * The output stream used to write the data to push through the connection (e.g. Multipart).
   */
  private @Nullable DataOutputStream out = null;

  /**
   * Creates a new multipart request to PageSeeder.
   *
   * @param path   The path without the site prefix (e.g. <code>/ps</code>)
   */
  public MultipartRequest(String path) {
    super(Method.POST, path);
    this._boundary = newBoundary();
    this._headers.add(new Header("Content-Type", "multipart/form-data; boundary=\"" + this._boundary+ "\""));
  }

  /**
   * Creates a new multipart request to PageSeeder.
   *
   * @param servlet The PageSeeder servlet to use
   */
  public MultipartRequest(Servlet servlet) {
    super(Method.POST, servlet);
    this._boundary = newBoundary();
    this._headers.add(new Header("Content-Type", "multipart/form-data; boundary=\"" + this._boundary+ "\""));
  }

  // Setters (return Request)
  // --------------------------------------------------------------------------

  /**
   * Adds a request header.
   *
   * @param name  The name of the HTTP header
   * @param value The value of the HTTP header.
   *
   * @return This request.
   *
   * @throws IllegalStateException If the connection has already been established and it is too late.
   */
  @Override
  public MultipartRequest header(String name, String value) {
    if (this.connection != null) throw new IllegalStateException("Too late to set headers to this multipart request!");
    super.setHeader(name, value);
    return this;
  }

  /**
   * Adds a request parameter.
   *
   * <p>These parameters will be added to the <code>multipart/form-data</code> part before the other parts.
   *
   * @param name  The name of the HTTP parameters
   * @param value The value of the HTTP parameters.
   *
   * @return This request.
   *
   * @throws IllegalStateException If the connection has already been established and it is too late.
   */
  @Override
  public MultipartRequest parameter(String name, String value) {
    if (this.connection != null) throw new IllegalStateException("Too late to set parameters for this multipart request!");
    return (MultipartRequest)super.parameter(name, value);
  }

  /**
   * Specify which credentials to use with this request.
   *
   * <p>Only one set of credentials can be used a time, this method will replace
   * any credentials that may have been set priority
   *
   * @param credentials The username/password, token or session to use as credentials
   *
   * @return This request.
   *
   * @throws IllegalStateException If the connection has already been established and it is too late.
   */
  @Override
  public MultipartRequest using(PSCredentials credentials) {
    if (this.connection != null) throw new IllegalStateException("Too late to specify credentials to this multipart request!");
    return (MultipartRequest)super.using(credentials);
  }

  /**
   * Add a part to the request (write the contents directly to the stream).
   *
   * @param part The encoding to specify in the Part's header
   *
   * @return This multipart request for easy chaining
   *
   * @throws IOException Should any error occur while writing the part on the output
   */
  public MultipartRequest addXMLPart(String part) throws IOException {
    addXMLPart(part, null);
    return this;
  }

  /**
   * Add a part to the request (write the contents directly to the stream).
   *
   * @param part    The encoding to specify in the Part's header
   * @param headers A list of headers added to this XML Part ('content-type' header is ignored)
   *
   * @return This multipart request for easy chaining
   *
   * @throws IOException Should any error occur while writing
   */
  public MultipartRequest addXMLPart(String part, @Nullable Map<String, String> headers) throws IOException {
    DataOutputStream o = this.out;
    if (o == null) {
      o = init();
    }
    try {
      // Start with boundary
      write("--", o);
      write(this._boundary, o);
      writeCRLF(o);

      // Headers if specified
      if (headers != null) {
        for (Entry<String, String> h : headers.entrySet()) {
          String name = h.getKey();
          if (!"content-type".equalsIgnoreCase(name)) {
            write(name + ": " + headers.get(h.getValue()), o);
            writeCRLF(o);
          }
        }
      }

      // Write content type
      write("Content-Type: text/xml; charset=\"utf-8\"", o);
      writeCRLF(o);
      writeCRLF(o);
      write(part, o);
      writeCRLF(o);
      o.flush();

    } catch (IOException ex) {
      closeQuietly(o);
      this.out = null;
      throw ex;
    }
    return this;
  }

  /**
   * Add a part to the request from a file (write the contents directly to the stream).
   * NOTE: Parameter name is hard coded to "file-1".
   *
   * @param part File for part
   *
   * @return This multipart request for easy chaining
   *
   * @deprecated Use {@link #addPart(String, File)} instead.
   *
   * @throws IOException Should any error occur while writing
   */
  @Deprecated
  public MultipartRequest addPart(File part) throws IOException {
    addPart(new FileInputStream(part), part.getName());
    return this;
  }

  /**
   * Add a part to the request from input stream (write the contents directly to the stream).
   * NOTE: Parameter name is hard coded to "file-1".
   *
   * @param in          Input stream for part content
   * @param filename    The filename for the part
   *
   * @return This multipart request for easy chaining
   *
   * @deprecated Use {@link #addPart(String, InputStream, String)} instead.
   *
   * @throws IOException Should any error occur while writing
   */
  @Deprecated
  public MultipartRequest addPart(InputStream in, String filename) throws IOException {
    return addPart("file-1", in, filename);
  }

  /**
   * Add a part to the request from a file (write the contents directly to the stream).
   *
   * @param name        the name of the parameter
   * @param part        the File for part
   *
   * @return This multipart request for easy chaining
   *
   * @throws IOException Should any error occur while writing
   */
  public MultipartRequest addPart(String name, File part) throws IOException {
    addPart(name, new FileInputStream(part), part.getName());
    return this;
  }

  /**
   * Add a part to the request from input stream (write the contents directly to the stream).
   *
   * @param name        the name of the parameter
   * @param in          Input stream for part content
   * @param filename    The filename for the part
   *
   * @return This multipart request for easy chaining
   *
   * @throws IOException Should any error occur while writing
   */
  public MultipartRequest addPart(String name, InputStream in, String filename) throws IOException {
    DataOutputStream o = this.out;
    if (o == null) {
      o = init();
    }
    try {

      // Start with boundary
      write("--", o);
      write(this._boundary, o);
      writeCRLF(o);

      // Write headers
      write("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"", o);
      writeCRLF(o);
      write("Content-Type: " + URLConnection.guessContentTypeFromName(filename), o);
      writeCRLF(o);
      writeCRLF(o);
      o.flush();

      // Copy binary file content
      try {
        copy(in, o);
      } finally {
        closeQuietly(in);
      }

      writeCRLF(o);
      o.flush();

    } catch (IOException ex) {
      closeQuietly(o);
      this.out = null;
      throw ex;
    }
    return this;
  }

  /**
   * Add a parameter as part of a multipart request.
   *
   * @param name  the name of the parameter
   * @param value the value of the parameter
   *
   * @return This multipart request for easy chaining
   *
   * @throws IOException Should any error occur while writing
   */
  public MultipartRequest addParameterPart(String name, String value) throws IOException {
    DataOutputStream o = this.out;
    if (o == null) {
      o = init();
    }
    try {

      // Start with boundary
      write("--", o);
      write(this._boundary, o);
      writeCRLF(o);

      // Write Parameter
      write("Content-Disposition: form-data; name=\"" + name + "\"", o);
      writeCRLF(o);
      writeCRLF(o);
      write(value, o);
      writeCRLF(o);
      o.flush();

    } catch (IOException ex) {
      closeQuietly(o);
      this.out = null;
      throw ex;
    }
    return this;
  }

  /**
   * Closes the output stream when writing to the connection.
   *
   * <p>This method does nothing if the output stream hasn't been created.
   *
   * @throws IOException If thrown by the close method.
   */
  public void closeOutput() throws IOException {
    DataOutputStream o = this.out;
    if (o != null) {
      o.close();
    }
  }

  // Execute
  // --------------------------------------------------------------------------

  /**
   * Returns the response to this multipart request.
   *
   * <p>The connection is configured to:
   * <ul>
   *   <li>Follow redirects</li>
   *   <li>Be used for output</li>
   *   <li>Ignore cache by default</li>
   * </ul>
   *
   * @return The response
   */
  @Override
  public Response response() {
    int status = -1;
    long t = System.currentTimeMillis();
    try {
      endMultipart();
      PSSession session = null;
      if (this.credentials instanceof PSSession) {
        session = (PSSession)this.credentials;
      }
      HttpURLConnection con = this.connection;
      if (con != null)  {
        // Trigger the connection
        status = this.connection.getResponseCode();

        return new Response(con, con.getResponseCode(), session);
      }
      else return new Response("No connection or connection was not established yet.");
    } catch (IOException ex) {
      return new Response(ex.getMessage());
    } finally {
      LOGGER.info("{} [{}] -> {} in {}ms", toURLString(this.config, this._path), this._method, status, System.currentTimeMillis() -t);
    }
  }

  // Private
  // --------------------------------------------------------------------------

  /**
   * Initializes the connection and return the output stream.
   *
   * @return the data output stream on the connection to write data to upload.
   *
   * @throws IOException If a network or I/O error occurs on the connection.
   */
  private DataOutputStream init() throws IOException {
    URL url = toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoOutput(true);
    connection.setInstanceFollowRedirects(true);
    if (this.timeout >= 0) {
      connection.setConnectTimeout(this.timeout);
    }

    // Multipart is always POST
    connection.setRequestMethod("POST");
    connection.setDefaultUseCaches(false);

    // Set the headers
    for (Header h : this._headers) {
      connection.addRequestProperty(h.name(), h.value());
    }

    // Prepare the connection for
    connection.setDoInput(true);
    DataOutputStream out = new DataOutputStream(connection.getOutputStream());

    // We serialize the HTTP parameters first
//    if (!this._parameters.isEmpty()) {
//      for (Parameter p : this._parameters) {
//        addParameterPart(p.name(), p.value());
//      }
//    }

    // Returns the connection
    this.connection = connection;
    this.out = out;

    // Return the output stream
    return out;
  }

  /**
   * @return a new multipart boundary.
   */
  private static String newBoundary() {
    long b = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE/36, Long.MAX_VALUE);
    return Long.toString(b, 36);
  }

  /**
   * Create a PageSeeder connection for the specified URL and method.
   *
   * <p>The connection is configured to:
   * <ul>
   *   <li>Follow redirects</li>
   *   <li>Be used for output</li>
   *   <li>Ignore cache by default</li>
   * </ul>
   *
   * @throws IOException Should an exception be returns while opening the connection
   */
  private void endMultipart() throws IOException {
    OutputStream o = this.out;
    if (o != null) {
      write("--", o);
      write(this._boundary, o);
      write("--", o);
      writeCRLF(o);
      o.flush();
    }
  }

  /**
   * If data is <code>null</code> the string content is copied to the output stream as UTF-8.
   *
   * @param data   The data to write
   * @param output The output
   *
   * @throws IOException Any error reported while writing on the output
   */
  private static void write(String data, OutputStream output) throws IOException {
    if (data != null) {
      output.write(data.getBytes(StandardCharsets.UTF_8));
    }
  }

  /**
   * Write the CR LF bytes to the output.
   *
   * @param output The output
   *
   * @throws IOException Any error reported while writing on the output
   */
  private static void writeCRLF(OutputStream output) throws IOException {
    output.write(CRLF);
  }

  /**
   * Copy the input stream to the output stream as UTF-8 using a buffer of 4096 bytes.
   *
   * @param input  The data to copy
   * @param output The output
   *
   * @throws IOException Any error reported while writing on the output
   */
  private static void copy(InputStream input, OutputStream output) throws IOException {
    byte[] buffer = new byte[4096];
    int n;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
    }
  }

  /**
   * Returns the URL to access this resource.
   *
   * <p>If the user is specified, its details will be included in the URL so that the resource can
   * be accessed on his behalf.
   *
   * @return the URL to access this resource.
   *
   * @throws MalformedURLException If the URL is not well-formed
   */
  @Override
  public URL toURL() throws MalformedURLException {
    String url = toURLString();
    // We serialize the HTTP parameters first and put them on the query
    if (!this._parameters.isEmpty()) {
      url = url+'?'+ encodeParameters();
    }
    return new URL(url);
  }

  /**
   * Close and catch any exception (reported in logs instead)
   *
   * @param closeable The object to close.
   */
  private static void closeQuietly(Closeable closeable) {
    try {
      if (closeable != null) {
        closeable.close();
      }
    } catch (IOException ex) {
      // FIXME LOGGER.debug("thrown when attempting to close quietly: {}", ex.getMessage(), ex);
    }
  }

}
