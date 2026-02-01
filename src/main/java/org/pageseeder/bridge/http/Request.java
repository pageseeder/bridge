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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.PSSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Simple fluent class to define HTTP requests to PageSeeder.
 *
 * <h3>Examples</h3>
 *
 * <p>Invoking a service
 * <pre>
 *   // Retrieve a member
 *   Response r = new Request(Method.GET, Service.get_member, member)
 *                    .using(token)
 *                    .response();
 *
 *   // Edit a member
 *   Response r = new Request(Method.PATCH, Service.edit_member, member)
 *                    .parameter("firstname", "John")
 *                    .parameter("firstname", "Doe")
 *                    .using(token)
 *                    .response();
 * </pre>
 *
 * <p>Accessing a resource directly</p>
 * <pre>
 *   // Static file on PageSeeder
 *   Response r = new Request(Method.GET, "/test/images/hello.jpg")
 *                    .using(session)
 *                    .response();
 *
 *   // Member service
 *   Response r = new Request(Method.GET, "/service/members/~jdoe")
 *                    .using(token)
 *                    .response();
 * </pre>
 *
 * <p>Calling a servlet</p>
 * <pre>
 *   Response r = new Request(Method.POST, Servlet.GENERIC_SEARCH)
 *                .parameter("question", "test")
 *                .parameter("type", "document")
 *                .parameter("from", "2016-06-01")
 *                .using(token)
 *                .response();
 * </pre>
 *
 * @author Christophe Lauret
 *
 * @version 0.11.12
 * @since 0.9.1
 */
public final class Request extends BasicRequest implements HttpRequest {

  /**
   * Logger for this class.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(Request.class);

  /**
   * Used for POST and PATCH request.
   */
  private static final Header CONTENT_FORM_URLENCODED_UTF8 =
      new Header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

  /**
   * Used for PUT request.
   */
  private static final Header CONTENT_TEXT_PLAIN_UTF8 =
      new Header("Content-Type", "text/plain; charset=UTF-8");

  /**
   * The body of the resource (used for PUT requests).
   */
  private byte @Nullable[] body;

  /**
   * Creates a new request to PageSeeder.
   *
   * @param method The HTTP method
   * @param path   The path without the site prefix (e.g. <code>/ps</code>)
   */
  public Request(Method method, String path) {
    super(method, path);
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * <p>This method will automatically constructs the correct URL for the requested
   * service using the URI variables.
   *
   * @param method    The HTTP method
   * @param service   The PageSeeder service to use
   * @param variables The variables to inject in the URL path.
   */
  @SafeVarargs
  public Request(Method method, Service service, @NonNull Object... variables) {
    super(method, service, variables);
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * @param method   The HTTP method
   * @param servlet  The PageSeeder servlet to use
   */
  public Request(Method method, Servlet servlet) {
    super(method, servlet);
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * <p>This method will automatically constructs the correct URL for the requested
   * service using the URI variables.
   *
   * @param method   The HTTP method
   * @param template The PageSeeder service URL template to use
   * @param variables The variables to inject in the URL path.
   *
   * @return The corresponding request
   */
  public static Request newService(Method method, String template, @NonNull Object... variables) {
    return new Request(method, ServicePath.newPath(template, variables));
  }

  /**
   * Creates a new request to a PageSeeder document with the specified URI ID.
   *
   * @param uri The document URI ID
   *
   * @return The corresponding request
   *
   * @throws IllegalArgumentException If the URI ID is not a positive long value.
   */
  public static Request newDocument(long uri) {
    if (uri <= 0) throw new IllegalArgumentException("URI ID must be strictly positive.");
    return new Request(Method.GET, "/uri/"+uri);
  }

  /**
   * Creates a new request to a PageSeeder document with the specified path.
   *
   * @param path The HTTP method
   *
   * @return The corresponding request
   */
  public static Request newDocument(String path) {
    return new Request(Method.GET, path);
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * <p>This method will automatically constructs the correct URL for the requested
   * service using the URI variables.
   *
   * @param group The PageSeeder group
   * @param path  The URL path
   *
   * @return The corresponding request
   */
  public static Request newDocument(String group, String path) {
    return new Request(Method.GET, DocumentPath.newLocalPath(group, path).path());
  }

  // Setters (return Request)
  // --------------------------------------------------------------------------

  @Override
  public Request header(String name, String value) {
    setHeader(name, value);
    return this;
  }

  @Override
  public Request parameter(String name, String value) {
    return (Request)super.parameter(name, value);
  }

  @Override
  public Request parameters(Map<String, String> parameters) {
    for (Entry<String, String> p : parameters.entrySet()) {
      parameter(p.getKey(), p.getValue());
    }
    return this;
  }

  @Override
  public Request using(PSCredentials credentials) {
    return (Request)super.using(credentials);
  }

  @Override
  public Request timeout(int timeout) {
    return (Request)super.timeout(timeout);
  }

  @Override
  public Request config(PSConfig config) {
    return (Request)super.config(config);
  }

  /**
   * Sets the etag on this request as the "If-None-Match" request header.
   *
   * @param etag The etag to use.
   *
   * @return this request.
   */
  @Override
  public Request etag(String etag) {
    return header("If-None-Match", '"'+etag+'"');
  }

  /**
   * Sets the request as the "Accept-Encoding" request header to "gzip" if enabled and removes
   * it otherwise.
   *
   * @param enable <code>true</code> to accept gzipped response; <code>false</code> otherwise.
   *
   * @return this request.
   */
  @Override
  public Request gzip(boolean enable) {
    if (enable)
      return header("Accept-Encoding", "gzip");
    else {
      removeHeader("Accept-Encoding");
      return this;
    }
  }

  /**
   * Set the body of the request (used for PUT)
   *
   * <p>This is designed for small objects, this method will use UTF-8 encoding.
   *
   * @param body  The body of the request
   *
   * @return this request.
   *
   * @throws NullPointerException if the array is <code>null</code>
   */
  @Override
  public Request body(String body) {
    this.body = body.getBytes(StandardCharsets.UTF_8);
    return this;
  }

  /**
   * Set the body of the request (used for PUT)
   *
   * <p>This method makes a copy the specified array.
   *
   * @param body The body of the request
   *
   * @return this request.
   *
   * @throws NullPointerException if the array is <code>null</code>
   */
  @Override
  public Request body(byte[] body) {
    this.body = Arrays.copyOf(body, body.length);
    return this;
  }

  // Getters
  // --------------------------------------------------------------------------

  /**
   * Returns the HTTP header the specified name.
   *
   * @param name The name of the HTTP header (case insensitive)
   *
   * @return The value of the corresponding parameter or <code>null</code>
   */
  @Override
  public @Nullable String header(String name) {
    // If a content header is requested, we recompute them
    if (name.toLowerCase().startsWith("content-")) {
      computeBodyContent();
    }
    return super.header(name);
  }

  /**
   * Returns an unmodifiable list of HTTP headers for this request.
   *
   * <p>This method will also compute the content headers for the request.
   *
   * @return The list of HTTP headers.
   */
  @Override
  public List<Header> headers() {
    // We recompute the header for the body content
    computeBodyContent();
    return Collections.unmodifiableList(this._headers);
  }

  // Execute
  // --------------------------------------------------------------------------

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
   * @return A response objects from the newly opened connection to the specified URL
   */
  @Override
  public Response response() {
    int status = -1;
    long t = System.currentTimeMillis();
    try {
      URL url = toURL();

      // Setup the connection
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setInstanceFollowRedirects(true);
      connection.setDefaultUseCaches(false);
      if (this.timeout >= 0) {
        connection.setConnectTimeout(this.timeout);
      }

      // Tunnel PATCH through POST as HttpUrlConnection does not support PATCH
      if (this._method == Method.PATCH) {
        connection.setRequestMethod("POST");
      } else {
        connection.setRequestMethod(this._method.name());
      }

      // Compute the body content (this might set some headers so must be done BEFORE we send the headers)
      byte[] data = computeBodyContent();

      // Send the headers
      for (Header h : this._headers) {
        connection.addRequestProperty(h.name(), h.value());
      }

      // Write the body content if any
      if (data != null) {
        connection.setDoInput(true);
        writeData(connection, data);
      }

      // Session handling
      PSSession session = null;
      if (this.credentials instanceof PSSession) {
        session = (PSSession)this.credentials;
      }

      // Trigger the connection
      status = connection.getResponseCode();

      // Create the response object after requesting status
      return new Response(connection, status, session);

    } catch (IOException ex) {
      return new Response(ex.getMessage());
    } finally {
      LOGGER.info("{} [{}] -> {} in {}ms", toURLString(this.config, this._path), this._method, status, System.currentTimeMillis() -t);
    }
  }

  // Convenience methods
  // --------------------------------------------------------------------------

  /**
   * Creates a new request to PageSeeder and immediately execute it.
   *
   * @param method The HTTP method
   * @param path   The path (without the site prefix)
   *
   * @return the corresponding response
   */
  public static Response response(Method method, String path) {
    return new Request(method, path).response();
  }

  /**
   * Creates a new request to a PageSeeder service and immediately execute it.
   *
   * <p>This method will automatically constructs the correct URL for the requested
   * service using the URI variables.
   *
   * @param method    The HTTP method
   * @param service   The PageSeeder service to use
   * @param variables The variables to inject in the URL path.
   *
   * @return the corresponding response
   */
  @SafeVarargs
  public static Response response(Method method, Service service, @NonNull Object... variables) {
    return new Request(method, service, variables).response();
  }

  // Private methods
  // --------------------------------------------------------------------------

  /**
   * Set the "Content-Type" and "Content-Length" headers if required and return
   * the content.
   *
   * @return the body if any.
   */
  private byte @Nullable[] computeBodyContent() {
    byte[] data = null;
    // Compute the data
    if (this._method == Method.POST || this._method == Method.PATCH) {
      data = encodeParameters().getBytes(StandardCharsets.UTF_8);
      this._headers.add(CONTENT_FORM_URLENCODED_UTF8);
    } else if (this.body != null) {
      data = this.body;
      if (getHeader("Content-Type") == null) {
        // XXX: Why assume utf-8 text, could be binary!! Content sniffing? Maybe we should display a warning
        this._headers.add(CONTENT_TEXT_PLAIN_UTF8);
      }
    }
    // Set the "Content-Length" if we have some data
    if (data != null) {
      setHeader("Content-Length", data.length);
    }
    return data;
  }

  /**
   * Write the request body content.
   *
   * @param connection The URL connection
   * @param data       The data to write
   *
   * @throws IOException Should any error occur while writing.
   */
  private static void writeData(HttpURLConnection connection, byte[] data) throws IOException {
    try (OutputStream post = connection.getOutputStream()){
      post.write(data);
      post.flush();
    }
  }

}
