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

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.xml.DuplexHandler;
import org.pageseeder.bridge.xml.Handler;
import org.pageseeder.bridge.xml.ServiceErrorHandler;
import org.pageseeder.bridge.xml.XMLCopy;
import org.pageseeder.bridge.xml.stax.XMLStreamHandler;
import org.pageseeder.xmlwriter.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The response to a connection to PageSeeder.
 *
 * <p>The response is a stateful object. The state of this response affects
 * the availability of the content for consumption.
 * <ul>
 *   <li>If the connection resulted in a response, then this response is "available".</li>
 *   <li>If the connection failed, this response is in a "failed" state.</li>
 *   <li>Once the response have been consumed, it is in a "consumed" state.</li>
 * </ul>
 *
 * <p>To simplify the task of error handling, consumer methods only return runtime exceptions.
 * <ul>
 *   <li><code>IllegalArgumentException</code> for preventable errors based on the state of the
 *   response (if content is not available or not XML)</li>
 *   <li><code>ContentException</code> if an error occurred while processing the content.
 *   these will generally wrap an I/O exception, SAX and transform exception.</li>
 * </ul>
 *
 * <p>This object is <code>AutoCloseable</code>, it is a good idea to close the
 * response. The {@link #close()} method ensures that the response content is
 * fully read and that the stream is closed so that the underlying can be reused
 * as soon as possible for persistent connections.
 *
 * <p>To help debug the output, the response can copy the output to
 * <code>System.out</code> (for successful responses) or <code>System.err</code>
 * for errors. To enable response debug set the <code>bridge.http.responseDebug</code>
 * to <code>true</code> or use the static method.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.9.1
 */
public final class Response implements HttpResponse, AutoCloseable {

  /**
   * State of this response.
   */
  private enum State {

    /**
     * The response is available and can be consumed.
     *
     * <p>Calling any of the consume methods will change the state to 'consumed'
     */
    available,

    /**
     * The connection failed, and not content can be read from it.
     */
    failed,

    /**
     * The response has already been consumed and the content is no longer available.
     */
    consumed
  }

  /**
   * Logger for this class.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(Response.class);

  /**
   * Counts warnings to ensure that no more than 100 are returned.
   */
  private static final AtomicInteger WARNING_COUNTER = new AtomicInteger(0);

  /**
   * Ensures this is enabled only once.
   */
  private static volatile boolean debugEnabled = false;
  static {
    // If the system property "bridge.http.responseDebug" is set to "true"
    if ("true".equals(System.getProperty("bridge.http.responseDebug", "false"))) {
      debugEnabled = true;
    }
  }

  /**
   * Holds the underlying connection.
   */
  private final @Nullable HttpURLConnection _connection;

  /**
   * The HTTP status code.
   *
   * NB. "status code" is the preferred term in HTTP/1.1 RFC 7230.
   */
  private final int _statusCode;

  /**
   * The state of response.
   */
  private State state = State.available;

  /**
   * Session from request or updated by 'Set-Cookie' header.
   */
  private final @Nullable PSSession _session;

  /**
   * The media type returned by PageSeeder.
   */
  private final @Nullable String _mediaType;

  /**
   * The list of HTTP response headers.
   */
  private final List<Header> _headers;

  /**
   * The character set detected in the response.
   */
  private final @Nullable Charset _charset;

  /**
   * The message returned by PageSeeder.
   */
  private final @Nullable String _message;

  /**
   * A service error instance if an error occurred while invoking a service.
   */
  private @Nullable ServiceError error = null;

  // Constructors
  // ----------------------------------------------------------------------------------------------

  /**
   * Construct a new response wrapping a HTTP URL connection without a session.
   *
   * <p>The status code, must be supplied as the response object should not constructed
   * with a connection if the connection failed.
   *
   * @param connection The underlying HTTP URL connection.
   * @param statusCode The response status code for that connection
   */
  Response(HttpURLConnection connection, int statusCode) {
    this(connection, statusCode, null);
  }

  /**
   * Construct a new response wrapping a HTTP URL connection.
   *
   * <p>The status code, must be supplied as the response object should not constructed
   * with a connection if the connection failed.
   *
   * @param connection The underlying HTTP URL connection.
   * @param statusCode The response status code for that connection
   * @param session    The session used to make the request.
   */
  Response(HttpURLConnection connection, int statusCode, @Nullable PSSession session) {
    this._connection = connection;
    this._statusCode = statusCode;
    this._session = updateSession(connection, session);
    this._headers = extractHeaders(connection);
    this._mediaType = getMediaType(connection);
    this._charset = detectCharset(connection);
    try {
      this._message = connection.getResponseMessage();
    } catch (IOException ex) {
      throw new IllegalStateException("Connection failed");
    }
  }

  /**
   * Construct a new response in an error state.
   *
   * @param message the explanation for the error.
   */
  Response(@Nullable String message) {
    this._connection = null;
    this._statusCode = -1;
    this._headers = Collections.emptyList();
    this._session = null;
    this._mediaType = null;
    this._charset = null;
    this._message = message;
    this.state = State.failed;
  }

  // Getters
  // ----------------------------------------------------------------------------------------------

  /**
   * Return the response status code from this request.
   *
   * @return the status code from this request.
   */
  @Override
  public int code() {
    return this._statusCode;
  }

  /**
   * Return the reason string.
   *
   * @return the reason string.
   */
  @Override
  public @Nullable String message() {
    return this._message;
  }

  /**
   * Returns the value of the specified header.
   *
   * @param name The name of the header (case insensitive)
   *
   * @return The corresponding value or <code>null</code>;
   */
  @Override
  public @Nullable String header(String name) {
    for (Header h : this._headers) {
      if (h.name().equalsIgnoreCase(name)) return h.value();
    }
    return null;
  }

  /**
   * Returns the list of headers
   *
   * @return The corresponding value or <code>null</code>;
   */
  @Override
  public List<Header> headers() {
    return Collections.unmodifiableList(this._headers);
  }

  /**
   * Returns the unwrapped value of "Etag" response header.
   *
   * <p>This method removes the quotes and weak etag flag if any, to expose the actual etag.
   *
   * <p>For example, if the header is <code>ETag: W/"123456789"</code>, this method
   * will return the string "<code>123456789</code>" (without quotes).
   *
   * <p>Note: PageSeeder only uses strong etags.
   *
   * @see #unwrapEtag(String)
   *
   * @return the etag of the response derived from the "Etag" response header.
   */
  @Override
  public @Nullable String etag() {
    return unwrapEtag(header("Etag"));
  }

  /**
   * Removes the quotes around the etag
   *
   * <pre>
   *  "123456789"   A strong ETag
   *  W/"123456789" A weak ETag
   * </pre>
   *
   * @param etag The value of the "ETag" header
   *
   * @return the corresponding etag value.
   */
  public static @Nullable String unwrapEtag(@Nullable String etag) {
    if (etag == null) return null;
    return etag.replaceAll("(?:W/)?\"([^\"]+)\"", "$1");
  }

  /**
   * Returns the value of "Content-Length" response header.
   *
   * <pre><code>Content-Length: 49</code></pre>
   *
   * @return the value of "Content-Length" response header or -1 if the content length is not known.
   */
  @Override
  public long length() {
    HttpURLConnection con = this._connection;
    if (con == null) return -1;
    return con.getContentLengthLong();
  }

  /**
   * Returns the value of "Date" response header.
   *
   * <pre><code>Date: Tue, 21 Jun 2016 05:10:06 GMT</code></pre>
   *
   * @return the value of "Date" response header or 0 if the date is not known.
   */
  @Override
  public long date() {
    HttpURLConnection con = this._connection;
    if (con == null) return 0;
    return con.getDate();
  }

  /**
   * Returns the value of "Last-Modified" response header.
   *
   * <pre><code>Date: Tue, 21 Jun 2016 05:10:06 GMT</code></pre>
   *
   * @return the value of "Date" response header or 0 if the date is not known.
   */
  @Override
  public long modified() {
    HttpURLConnection con = this._connection;
    if (con == null) return 0;
    return con.getLastModified();
  }

  /**
   * Returns the value of "Expires" response header.
   *
   * <pre><code>Expires: Wed, 21 Jun 2017 05:10:06 GMT</code></pre>
   *
   * @return the value of "Expires" response header or 0 if not known.
   */
  @Override
  public long expires() {
    HttpURLConnection con = this._connection;
    if (con == null) return 0;
    return con.getExpiration();
  }

  /**
   * Returns the value of "Content-Type" response header.
   *
   * <pre><code>Content-Type: application/xml;charset=utf-8</code></pre>
   *
   * @return the value of "Content-Type" response header.
   */
  @Override
  public @Nullable String getContentType() {
    HttpURLConnection con = this._connection;
    if (con == null) return null;
    return con.getContentType();
  }

  /**
   * Returns the value of media type of response content.
   *
   * <p>The media type is derived from the value of the "Content-Type" response
   * header without the additional parameters (e.g. charset).
   *
   * <p>For example, this method will return "application/xml" for the header
   * below:
   * <pre><code>Content-Type: application/xml;charset=utf-8</code></pre>
   *
   * @return the media type of the response content.
   */
  @Override
  public @Nullable String mediaType() {
    return this._mediaType;
  }

  /**
   * Returns the character set used for character-based content and derived
   * from the "Content-Type" response header.
   *
   * <p>NB. The HTTP header "Content-Encoding" has nothing to do with character
   * encoding, do not use it for that purpose!
   *
   * @return the charset used in the response if detected and applicable.
   */
  @Override
  public @Nullable Charset charset() {
    return this._charset;
  }

  /**
   * Returns the session.
   *
   * @return the session if any.
   */
  @Override
  public @Nullable PSSession session() {
    return this._session;
  }

  // State
  // ---------------------------------------------------------------------------

  /**
   * Indicates whether the response is XML based on the content type.
   *
   * <p>It is considered XML the mediatype is equal to "text/xml" or
   * "application/xml" or ends with "+xml".
   *
   * @return <code>true</code> if XML;
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean isXML() {
    return isXML(this._mediaType);
  }

  /**
   * Indicates whether the response was successful based on the HTTP code.
   *
   * <p>When this method is called BEFORE a consumer, this generally imply
   * that the content is <i>available</i>.
   *
   * @return <code>true</code> if the code is between 200 and 299 (included);
   *         <code>false</code>.
   */
  @Override
  public boolean isSuccessful() {
    return isSuccessful(this._statusCode);
  }

  /**
   * Indicates whether the response is in a state that would allow it to
   * consume the content.
   *
   * <p>This method returns <code>false</code> if the content has already been
   * consumed or if the connection failed and there is no content to process.
   *
   * @return <code>true</code> if the connection can ;
   *         <code>false</code>.
   */
  @Override
  public boolean isAvailable() {
    return this.state == State.available;
  }

  // Consumers
  // --------------------------------------------------------------------------

  /**
   * Returns the InputStream on the response content.
   *
   * <p>Even though method does not strictly consume the content, it assumes that caller will, so
   * after calling this method the response content will no longer be available.
   *
   * <p>The input stream is NOT buffered.
   *
   * @return An input stream on the connection response if any.
   *
   * @throws IOException If the thrown by the underlying connection.
   * @throws IllegalStateException If the response is not available.
   */
  @Override
  public @Nullable InputStream getInputStream() throws IOException {
    HttpURLConnection con = requireAvailable();
    try {
      return toInputStream(con);
    } finally {
      this.state = State.consumed;
    }
  }

  /**
   * Returns the Reader on the response content using the detected character set.
   *
   * <p>Do not use this method unless this class was able to detect the character set.
   *
   * <p>Even though method does not strictly consume the content, it assumes that caller will, so
   * after calling this method the response content will no longer be available.
   *
   * @return A reader on the connection response if any.
   *
   * @throws IOException If the thrown by the underlying connection.
   * @throws IllegalStateException If the response is not available.
   */
  @Override
  public @Nullable Reader getReader() throws IOException {
    Charset charset = this._charset;
    if (charset == null)
      throw new IllegalStateException("Unable to determine the charset for this resource.");
    return getReader(charset);
  }

  /**
   * Returns the Reader on the response content using the detected character set.
   *
   * <p>Do not use this method unless this class was able to detect the character set.
   *
   * <p>Even though method does not strictly consume the content, it assumes that caller will, so
   * after calling this method the response content will no longer be available.
   *
   * @param charset The charset to use to read the content
   *
   * @throws IOException If the thrown by the underlying connection.
   * @throws IllegalStateException If the response is not available.
   */
  @Override
  public @Nullable Reader getReader(Charset charset) throws IOException {
    HttpURLConnection con = requireAvailable();
    try {
      return new InputStreamReader(toInputStream(con), charset);
    } finally {
      this.state = State.consumed;
    }
  }

  /**
   * Consumes the output of the response.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @param out Where the output should be copied.
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
  @Override
  public void consumeBytes(OutputStream out) {
    HttpURLConnection con = requireAvailable();
    try {
      int length = con.getContentLength();
      try (BufferedInputStream in = new BufferedInputStream(toInputStream(con))){
        copy(in, out, length);
      }
    } catch (IOException ex) {
      throw new ContentException("Unable to consume bytes", ex);
    } finally {
      this.state = State.consumed;
    }
  }

  /**
   * Short-hand method to consume the output of the response and return a byte array.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @return the output content as a byte array.
   *
   * @throws ContentException If an error occurred while consuming the content.
   */
  @Override
  @SuppressWarnings("null")
  public byte[] consumeBytes() {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    consumeBytes(bytes);
    return bytes.toByteArray();
  }

  /**
   * Simply consumes the output of the response.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
  @Override
  public void consume() {
    HttpURLConnection con = requireAvailable();
    try {
      try (InputStream in = toInputStream(con)){
        //noinspection StatementWithEmptyBody
        while (-1 != in.read()) {
          // do nothing
        }
      }
    } catch (IOException ex) {
      throw new ContentException("Unable to consume response content", ex);
    } finally {
      this.state = State.consumed;
    }
  }


  /**
   * Consumes the output of the response.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @param out Where the output should be written to.
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
  @Override
  public void consumeChars(Writer out) {
    HttpURLConnection con = requireAvailable();
    try {
      int length = (int)length();
      Charset charset = charset() != null ? charset() : StandardCharsets.UTF_8;
      try (Reader r = new InputStreamReader(new BufferedInputStream(toInputStream(con)), charset)) {
        copy(r, out, length);
      }
    } catch (IOException ex) {
      throw new ContentException("Unable to consume bytes", ex);
    } finally {
      this.state = State.consumed;
    }
  }

  /**
   * Short-hand method to consume the output of the response and return a string.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @return the output as a string.
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
  @Override
  public String consumeString() throws ContentException {
    StringWriter out = new StringWriter();
    consumeChars(out);
    return out.toString();
  }

  /**
   * Consumes the output of the response using a SAX handler.
   *
   * <p>The SAX Parser is non-validating and namespace aware:
   * <pre>
   *   factory.setValidating(false);
   *   factory.setNamespaceAware(true);
   * </pre>
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @param handler The SAX handler for the XML
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
  @Override
  public void consumeXML(DefaultHandler handler) throws ContentException {
    try {
      handleXML(this, handler);
    } catch (IOException ex) {
      throw new ContentException("Unable to consume XML", ex);
    } finally {
      this.state = State.consumed;
    }
  }

  /**
   * Consumes the output of the response using a handler and returns the collection
   * of objects from it.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @param handler The object handler for the XML
   * @param <T> The type of object returned in the list.
   *
   * @return A list of items from the XML.
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
  @Override
  public <T> List<T> consumeList(Handler<T> handler) throws ContentException {
    consumeXML(handler);
    return handler.list();
  }

  /**
   * Consumes the output of the response using a handler and returns a single
   * object from it.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @param handler The object handler for the XML
   * @param <T> The type of object returned as the item.
   *
   * @return A single item from the parsed XML.
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
  @Override
  public <T> @Nullable T consumeItem(Handler<T> handler) throws ContentException {
    consumeXML(handler);
    return handler.get();
  }

  /**
   * Consumes the output of the response using a handler and returns a list of objects
   * from it.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @param handler The object handler for the XML
   * @param <T> The type of object returned as the item.
   *
   * @return A single item from the parsed XML.
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
//  @Override
  public <T> @Nullable List<T> consumeList(XMLStreamHandler<T> handler) throws ContentException {
    try {
      return parseXMLStream(this, handler);
    } catch (IOException ex) {
      throw new ContentException("Unable to consume XML", ex);
    } finally {
      this.state = State.consumed;
    }
  }

  /**
   * Consumes the output of the response using a handler and returns a single
   * object from it.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @param handler The object handler for the XML
   * @param <T> The type of object returned as the item.
   *
   * @return A single item from the parsed XML.
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
//  @Override
  public <T> @Nullable T consumeItem(XMLStreamHandler<T> handler) throws ContentException {
    List<T> list = consumeList(handler);
    return list.size() > 0? list.get(0) : null;
  }

  /**
   * Consumes the output of the response and copies it to the specified XML writer.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * <p>The XML writer receives a copy of the PageSeeder XML.
   *
   * @param xml The XML to copy from PageSeeder
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
  @Override
  public void consumeXML(XMLWriter xml) throws ContentException {
    try {
      // Parse with the XML Copy Handler
      XMLCopy handler = new XMLCopy(xml);
      handleXML(this, handler);
    } catch (IOException ex) {
      throw new ContentException("Unable to copy XML", ex);
    } finally {
      this.state = State.consumed;
    }
  }

  /**
   * Consumes the output of the response, transforms it directly with XSLT and
   * writes the results of the transformation to the XML writer.
   *
   * <p>Templates must be specified to transform the XML.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @param xml       The XML to copy from PageSeeder
   * @param templates A set of templates to process the XML
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
  @Override
  @SuppressWarnings("null")
  public void consumeXML(XMLWriter xml, Templates templates) throws ContentException {
    consumeXML(xml, templates, Collections.emptyMap());
  }

  /**
   * Consumes the output of the response, transforms it directly with XSLT and
   * writes the results of the transformation to the XML writer.
   *
   * <p>Templates must be specified to transform the XML. The parameters are
   * transmitted to the XSLT tranformer.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @param xml        The XML to copy from PageSeeder
   * @param templates  A set of templates to process the XML
   * @param parameters Parameters to supply to the templates
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
  @Override
  public void consumeXML(XMLWriter xml, Templates templates, Map<String, String> parameters)
      throws ContentException {
    try {
      transformXML(this, xml, templates, parameters);
    } catch (IOException ex) {
      throw new ContentException("Unable to transform XML", ex);
    } finally {
      this.state = State.consumed;
    }
  }

  /**
   * Consumes the output of the response and returns the corresponding
   * <code>ServiceError</code> if the response was an error.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @return The service error details.
   *
   * @throws ContentException If an error occurred while consuming the content.
   * @throws IllegalStateException If the response is not available.
   */
  @Override
  public @Nullable ServiceError consumeServiceError() {
    this.error = consumeItem(new ServiceErrorHandler());
    return this.error;
  }

  /**
   * Returns the <code>ServiceError</code> instance if the response was an error.
   *
   * <p>If the response has already been consumed, any service error instance
   * will be returned if already processed by an XML handler.
   *
   * <p>If the response is still available, it will be consumed to get the error,
   * details, and the response content will no longer be available.
   *
   * @return The service error details.
   *
   * @throws ContentException If an error occurred while consuming the content.
   * @throws IllegalStateException If the response is in a failed state.
   */
  @Override
  public @Nullable ServiceError getServiceError() {
    if (this.state == State.consumed)
      return this.error;
    else
      return consumeItem(new ServiceErrorHandler());
  }

  @Override
  public void close() {
    if (this.state == State.available) {
      consume();
    }
  }

  /**
   * Enable response content debug, causing response content to be copied
   * to <code>System.out</code> or <code>System.err</code>.
   *
   * <p>Response content debug is disabled by default, unless the
   * <code>bridge.http.responseDebug</code> was set to <code>true</code>.
   */
  public static void enableDebug() {
    debugEnabled = true;
  }

  /**
   * Disable response content debug.
   */
  public static void disableDebug() {
    debugEnabled = false;
  }

  // Extractors
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the media type from the HTTP response stripped of its charset sub-declaration or
   * any content type parameter.
   *
   * @param connection The HTTP connection
   * @return the media type from the HTTP response stripped of its charset sub-declaration.
   */
  private static @Nullable String getMediaType(HttpURLConnection connection) {
    String contentType = connection.getContentType();
    return Header.toMediaType(contentType);
  }

  /**
   * Updates the user session ID and last connection time from the HTTP response headers.
   *
   * <p>This method returns the new session is the response contains the "Set-Cookie" header.
   *
   * <p>Otherwise, if a session was supplied, it is updated.
   *
   * <p>This method returns <code>null</code> if no session was specified via a
   * "Set-Cookie" header or provided as an argument.
   *
   * @param connection The HTTP connection
   * @param session    The original session from the request if any
   *
   * @return a new session, the updated session or <code>null</code>
   */
  private static @Nullable PSSession updateSession(HttpURLConnection connection, @Nullable PSSession session) {
    String cookie = connection.getHeaderField("Set-Cookie");
    // A new session has been created.
    if (cookie != null)
      return PSSession.parseSetCookieHeader(cookie);
    else if (session != null) {
      // Update the n
      session.update();
      return session;
    } else return null;
  }

  /**
   * Extract the headers from the connection
   *
   * @param connection The HTTP connection to use
   *
   * @return a new list of headers
   */
  private static List<Header> extractHeaders(HttpURLConnection connection) {
    Map<String, List<String>> headerFields = connection.getHeaderFields();
    // Length is 1 less than headers from connection as we discard the status line
    List<Header> headers = new ArrayList<>(headerFields.size()-1);
    for (Entry<String, List<String>> h : headerFields.entrySet()) {
      String name = h.getKey();
      if (name != null) {
        if ("content-length".equalsIgnoreCase(name)) {
          headers.add(new Header(name, connection.getContentLengthLong()));
        } else if ("warning".equalsIgnoreCase(name) && WARNING_COUNTER.get() < 100) {
          // Report deprecation and other API warnings in the logs
          for (String value : h.getValue()) {
            if (WARNING_COUNTER.incrementAndGet() < 100) {
              LOGGER.warn(value);
            } else {
              LOGGER.warn("Reached max 100 HTTP Warnings - no more warnings will be displayed");
              break;
            }
          }
        }
        for (String value : h.getValue()) {
          headers.add(new Header(name, value));
        }
      }
    }
    return headers;
  }

  /**
   * Detect the character set from the "Content-Type" response header.
   *
   * @param connection The HTTP connection to use
   *
   * @return a new list of headers
   */
  private static @Nullable Charset detectCharset(HttpURLConnection connection) {
    String contentType = connection.getHeaderField("Content-Type");
    return Header.toCharset(contentType);
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * Check if this response is ready to be consumed.
   *
   * @throws IllegalStateException if that isn't the case.
   */
  private HttpURLConnection requireAvailable() {
    switch (this.state) {
      case available:
        HttpURLConnection connection = this._connection;
        if (connection == null)
          throw new IllegalArgumentException("This response cannot be consumed because there is not connection!");
        return connection;
      case failed:
        throw new IllegalArgumentException("This response cannot be consumed because the connection failed.");
      case consumed:
        throw new IllegalArgumentException("This response has already been consumed!");
      default:
        throw new IllegalArgumentException("This response is not available");
    }
  }

  /**
   * Check that the content is XML otherwise throw an Illegal state exception.
   *
   * @throws IllegalStateException if the media type is not XML.
   */
  private void requireXML() {
    if (!isXML())
      throw new IllegalStateException("Unable to parse non-XML media type");
  }

  /**
   * Parse the response as XML.
   *
   * @param response   Stores metadata about the response including error details.
   * @param handler    Handles the XML.
   *
   * @throws IllegalStateException If the response is not available.
   * @throws IOException If an error occurs while writing the XML.
   */
  private static void handleXML(Response response, DefaultHandler handler)
      throws IOException {
    // Ensure we a connection that returned XML content
    HttpURLConnection connection = response.requireAvailable();
    response.requireXML();

    // Setup the factory
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);

    try (InputStream in = toInputStream(connection)) {
      InputSource source = new InputSource(in);
      source.setSystemId(connection.getURL().toString());

      // Ensure the character encoding is correct
      Charset charset = response._charset;
      if (charset != null) {
        source.setEncoding(charset.name());
      }

      // And parse!
      SAXParser parser = factory.newSAXParser();
      HandlerDispatcher dispatcher = new HandlerDispatcher(parser.getXMLReader(), handler);
      parser.parse(source, dispatcher);
      response.error = dispatcher.getServiceError();

    } catch (ParserConfigurationException ex) {
      throw new ContentException("Error while configuring XML parser", ex);
    } catch (SAXException ex) {
      throw new ContentException("Error while parsing XML", ex);
    }
  }

  /**
   * Parse the response as XML using a StAX stream handler.
   *
   * @param response   Stores metadata about the response including error details.
   * @param handler    Handles a StAX stream
   *
   * @throws IllegalStateException If the response is not available.
   * @throws IOException If an error occurs while writing the XML.
   */
  private static <T> List<T> parseXMLStream(Response response, XMLStreamHandler<T> handler) throws IOException {
    // Ensure we a connection that returned XML content
    HttpURLConnection connection = response.requireAvailable();
    response.requireXML();

    // Setup the factory
    XMLInputFactory factory = XMLInputFactory.newInstance();
    factory.setProperty(XMLInputFactory.IS_COALESCING, true);
    factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, true);
    factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

    // Ensure the character encoding is correct
    Charset charset = response._charset;
    if (charset == null) {
      charset = StandardCharsets.UTF_8;
    }

    List<T> list = new ArrayList<>();
    try (InputStream in = toInputStream(connection)) {
      XMLStreamReader source = factory.createXMLStreamReader(in, charset.name());
      while (handler.find(source)) {
        T next = handler.get(source);
        if (next != null)
          list.add(next);
      }

    } catch (IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException
           | NoSuchElementException | XMLStreamException | UnsupportedOperationException ex) {
      // The XMLStreamReader throws a number of runtime exception that we need to catch
      throw new ContentException("Error while parsing XML", ex);
    }

    return list;
  }

  /**
   * Parse the response as XML and transform the output.
   *
   * @param response   Stores metadata about the response including error details.
   * @param xml        Where the final XML goes.
   * @param templates  To transform the XML.
   * @param parameters Parameters to send to the transformer (optional).
   *
   * @return <code>true</code> if the data was parsed without error;
   *         <code>false</code> otherwise.
   *
   * @throws IllegalStateException If the response is not available.
   * @throws IOException If an error occurs while writing the XML.
   */
  private static boolean transformXML(Response response, XMLWriter xml,
      Templates templates, Map<String, String> parameters) throws IOException {
    // Ensure we a connection that returned XML content
    HttpURLConnection connection = response.requireAvailable();
    response.requireXML();

    boolean ok = true;
    // Create an XML Buffer
    StringWriter buffer = new StringWriter();

    try (InputStream in = toInputStream(connection)) {

      // Setup the source
      StreamSource source = new StreamSource(in);
      source.setSystemId(connection.getURL().toString());

      // Setup the result
      StreamResult result = new StreamResult(buffer);

      // Create a transformer from the templates
      Transformer transformer = templates.newTransformer();

      // Add parameters
      if (parameters != null) {
        for (Entry<String, String> p : parameters.entrySet()) {
          transformer.setParameter(p.getKey(), p.getValue());
        }
      }

      // Process, write directly to the result
      transformer.transform(source, result);

    } catch (TransformerException ex) {
      throw new ContentException("Error while transforming XML data", ex);
    }

    // Write as XML
    xml.writeXML(buffer.toString());

    return ok;
  }

  // I/O utility methods
  // --------------------------------------------------------------------------

  /**
   * Indicates if the content type corresponds to XML content.
   *
   * @param mediaType The media type
   * @return <code>true</code> if equal to "text/xml" or "application/xml" or end with "+xml";
   *         <code>false</code> otherwise.
   */
  private static boolean isXML(@Nullable String mediaType) {
    return mediaType != null && ("text/xml".equals(mediaType) || "application/xml".equals(mediaType) || mediaType.endsWith("+xml"));
  }

  /**
   * Indicates whether the response was successful based on the HTTP code.
   *
   * @param code the HTTP status code.
   * @return <code>true</code> if the code is between 200 and 299 (included);
   *         <code>false</code>.
   */
  private static boolean isSuccessful(int code) {
    return code >= HttpURLConnection.HTTP_OK && code < HttpURLConnection.HTTP_MULT_CHOICE;
  }

  /**
   * Returns the appropriate input stream
   *
   * @param connection The URL connection
   *
   * @return the input stream on the response content
   *
   * @throws IOException If the thrown by the underlying connection.
   */
  private static InputStream toInputStream(HttpURLConnection connection) throws IOException {
    if (isSuccessful(connection.getResponseCode())) {
      InputStream in = connection.getInputStream();
      if (in == null)
        throw new IllegalArgumentException("Unable to read connection output");
      return debugStream(in, connection.getContentLength(), System.out);
    } else {
      InputStream err = connection.getErrorStream();
      if (err == null)
        throw new IllegalArgumentException("Unable to read connection error output");
      return debugStream(err, connection.getContentLength(), System.err);
    }
  }

  /**
   * Returns the input stream.
   *
   * <p>If debug is enabled, the content of the error stream is printed onto the System error stream.
   *
   * @param in     The input stream
   * @param length The content length
   * @param out    Where the output should go.
   *
   * @return the input stream
   *
   * @throws IOException If thrown while processing the stream
   */
  private static InputStream debugStream(InputStream in, int length, PrintStream out) throws IOException {
    InputStream actual;
    if (debugEnabled) {
      try {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        copy(in, buffer, length);
        buffer.writeTo(out);
        actual = new ByteArrayInputStream(buffer.toByteArray());
      } finally {
        closeQuietly(in);
      }
    } else {
      actual = in;
    }
    return actual;
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
      LOGGER.debug("thrown when attempting to close quietly: {}", ex.getMessage(), ex);
    }
  }

  /**
   * Copy the input stream to the output stream as UTF-8 using a buffer of at most 4096 bytes.
   *
   * @param input  The data to copy
   * @param output The output
   * @param length The expected length
   *
   * @throws IOException Any error reported while writing on the output
   */
  private static void copy(InputStream input, OutputStream output, int length) throws IOException {
    int bufferSize = length <= 0 || length > 4096? 4096 : length;
    byte[] buffer = new byte[bufferSize];
    int n;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
    }
  }

  /**
   * Copy the input stream to the output stream as UTF-8 using a buffer of 4096 bytes.
   *
   * @param reader  The data to copy
   * @param writer The output
   * @param length The expected length
   *
   * @throws IOException Any error reported while writing on the output
   */
  private static void copy(Reader reader, Writer writer, int length) throws IOException {
    // NB. length is in bytes, there are more bytes than characters
    int bufferSize = length <= 0 || length > 4096? 4096 : length;
    char[] buffer = new char[bufferSize];
    int n;
    while (-1 != (n = reader.read(buffer))) {
      writer.write(buffer, 0, n);
    }
  }

  /**
   * A handler that wraps a specified handler but will automatically dispatch
   * to the service error handler if an error is detected.
   */
  private static class HandlerDispatcher extends DefaultHandler {

    /** Handler to use if response is not an error */
    private final DefaultHandler handler;

    /** XML Reader in use to do the dispatching */
    private final XMLReader reader;

    /** Service error handler will be set if an error is detected */
    private @Nullable ServiceErrorHandler seHandler = null;

    /**
     * @param reader   The XML currently processing
     * @param handler  THe handler to use unless an error is detected.
     */
    public HandlerDispatcher(XMLReader reader, DefaultHandler handler) {
      this.reader = reader;
      this.handler = handler != null ? handler : new DefaultHandler();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      DefaultHandler actual = this.handler;
      // If the first element is "error" we capture the details with the different handler in duplex mode.
      if ("error".equals(localName) || "error".equals(qName)) {
        ServiceErrorHandler error = new ServiceErrorHandler();
        actual = new DuplexHandler(error, actual);
        this.seHandler = error;
      }
      // Update the reader
      this.reader.setContentHandler(actual);
      this.reader.setErrorHandler(actual);
      this.reader.setDTDHandler(actual);
      this.reader.setErrorHandler(actual);
      // Start actual parsing
      actual.startDocument();
      actual.startElement(uri, localName, qName, attributes);
    }

    /**
     * Returns the service error instance if an error was detected and the
     * {@link ServiceErrorHandler} was triggered.
     *
     * @return the service error instance or <code>null</code>
     */
    public @Nullable ServiceError getServiceError() {
      ServiceErrorHandler seh = this.seHandler;
      if (seh != null) return seh.get();
      else return null;
    }

  }

}
