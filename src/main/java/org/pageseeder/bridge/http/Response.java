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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.pageseeder.berlioz.xml.XMLCopy;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.xmlwriter.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

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
 * @author Christophe Lauret
 * @version 0.9.1
 * @since 0.9.1
 */
public final class Response {

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
    consumed;
  }

  /**
   * Logger for this class.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(Response.class);

  /**
   * Holds the underlying connection.
   */
  private final HttpURLConnection _connection;

  /**
   * The HTTP status code.
   *
   * NB. "status code" is the preferred term in HTTP/1.1 RFC 7230.
   */
  private final int _statusCode;

  /**
   * Session from request or updated by 'Set-Cookie' header.
   */
  private final PSSession _session;

  /**
   * The media type returned by PageSeeder.
   */
  private final String _mediaType;

  /**
   * The list of HTTP response headers.
   */
  private final List<Header> _headers;

  /**
   * The character set detected in the response.
   */
  private final Charset _charset;

  /**
   * The state of response.
   */
  private State state = State.available;

  /**
   * Indicates whether the error stream should be consumed or not.
   */
  private boolean consumeError = false;

  /**
   * The message returned by PageSeeder.
   */
  private String message = "";

  /**
   * The error ID returned by PageSeeder (services only)
   */
  private String errorID = "";

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
  Response(HttpURLConnection connection, int statusCode, PSSession session) {
    this._connection = connection;
    this._statusCode = statusCode;
    this._session = updateSession(connection, session);
    this._headers = extractHeaders(connection);
    this._mediaType = getMediaType(connection);
    this._charset = detectCharset(connection);
    try {
      this.message = this._connection.getResponseMessage();
    } catch (IOException ex) {
      throw new IllegalStateException("Connection failed");
    }
  }

  /**
   * Construct a new response in an error state.
   *
   * @param message the explanation for the error.
   */
  Response(String message) {
    this._connection = null;
    this._statusCode = -1;
    this._headers = Collections.emptyList();
    this._session = null;
    this._mediaType = null;
    this._charset = null;
    this.state = State.failed;
    this.message = message;
  }

  // Getters
  // ----------------------------------------------------------------------------------------------

  /**
   * Return the response status code from this request.
   *
   * @return the status code from this request.
   */
  public int code() {
    return this._statusCode;
  }

  /**
   * Return the reason string.
   *
   * @return the reason string.
   */
  public String message() {
    return this.message;
  }

  /**
   * Returns the value of the specified header.
   *
   * @param name The name of the header (case insensitive)
   *
   * @return The corresponding value or <code>null</code>;
   */
  public String header(String name) {
    for (Header h : this._headers) {
      if (h.name().equalsIgnoreCase(name)) return h.value();
    }
    return null;
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
  public String etag() {
    return unwrapEtag(header("Etag"));
  }

  /**
   * Removes the quotes around the etag
   *
   * <pre>
   *  "123456789"   – A strong ETag
   *  W/"123456789" – A weak ETag
   * </pre>
   *
   * @param etag The value of the "ETag" header
   *
   * @return the corresponding etag value.
   */
  public static String unwrapEtag(String etag) {
    if (etag == null) return null;
    return etag.replaceAll("(?:W/)?\\\"([^\"]+)\\\"", "$1");
  }

  /**
   * Returns the value of "Content-Length" response header.
   *
   * <pre><code>Content-Length: 49</code></pre>
   *
   * @return the value of "Content-Length" response header or -1 if the content length is not known.
   */
  public long length() {
    if (this._connection == null) return -1;
    return this._connection.getContentLengthLong();
  }

  /**
   * Returns the value of "Date" response header.
   *
   * <pre><code>Date: Tue, 21 Jun 2016 05:10:06 GMT</code></pre>
   *
   * @return the value of "Date" response header or 0 if the date is not known.
   */
  public long date() {
    if (this._connection == null) return 0;
    return this._connection.getDate();
  }

  /**
   * Returns the value of "Last-Modified" response header.
   *
   * <pre><code>Date: Tue, 21 Jun 2016 05:10:06 GMT</code></pre>
   *
   * @return the value of "Date" response header or 0 if the date is not known.
   */
  public long modified() {
    if (this._connection == null) return 0;
    return this._connection.getLastModified();
  }

  /**
   * Returns the value of "Expires" response header.
   *
   * <pre><code>Expires: Wed, 21 Jun 2017 05:10:06 GMT</code></pre>
   *
   * @return the value of "Expires" response header or 0 if not known.
   */
  public long expires() {
    if (this._connection == null) return 0;
    return this._connection.getExpiration();
  }

  /**
   * Returns the value of "Content-Type" response header.
   *
   * <pre><code>Content-Type: application/xml;charset=utf-8</code></pre>
   *
   * @return the value of "Content-Type" response header.
   */
  public String getContentType() {
    if (this._connection == null) return null;
    return this._connection.getContentType();
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
  public String mediaType() {
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
  public Charset charset() {
    return this._charset;
  }

  /**
   * Returns the session;
   *
   * @return the session.
   */
  public PSSession session() {
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
   * @throws IOException If the thrown by the underlying connection.
   * @throws IllegalStateException If the response is not available.
   */
  public InputStream getInputStream() throws IOException {
    requireAvailable();
    if (this._connection == null) return null;
    try {
      return toInputStream(this._connection);
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
   * @throws IOException If the thrown by the underlying connection.
   * @throws IllegalStateException If the response is not available.
   */
  public Reader getReader() throws IOException {
    requireAvailable();
    if (this._connection == null) return null;
    try {
      return new InputStreamReader(toInputStream(this._connection), this._charset);
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
   * @throws IOException If an error occurs when processing the content
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
  public void consumeBytes(OutputStream out) {
    requireAvailable();
    try {
      if (processContent()) {
        int length = this._connection.getContentLength();
        try (BufferedInputStream in = new BufferedInputStream(toInputStream(this._connection))){
          copy(in, out, length);
        }
      } else {
        handleError(this);
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
  public byte[] consumeBytes() {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    consumeBytes(bytes);
    return bytes.toByteArray();
  }

  /**
   * Consumes the output of the response.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @param out Where the output should be written to.
   *
   * @throws IOException If an error occurs when processing the content
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
  public void consumeChars(Writer out) {
    requireAvailable();
    try {
      if (processContent()) {
        int length = (int)length();
        Charset charset = charset();
        try (Reader r = new InputStreamReader(new BufferedInputStream(toInputStream(this._connection)), charset)) {
          copy(r, out, length);
        }
      } else {
        handleError(this);
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
   * @returns the output as a string.
   *
   * @throws IOException If an error occurs when processing the content
   * @throws ContentException If an error occurred while consuming the content.
   */
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
   * @throws ContentException If an error occurred while consuming the content.
   */
  public void consumeXML(DefaultHandler handler) throws ContentException {
    requireAvailable();
    requireXML();
    try {
      if (processContent()) {
        handleXML(this, handler, false);
      } else {
        handleError(this);
      }
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
   *
   * @throws ContentException If an error occurred while consuming the content.
   */
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
   *
   * @throws ContentException If an error occurred while consuming the content.
   */
  public <T> T consumeItem(Handler<T> handler) throws ContentException {
    consumeXML(handler);
    return handler.get();
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
   * @throws ContentException If an error occurred while consuming the content.
   */
  public void consumeXML(XMLWriter xml) throws ContentException {
    requireAvailable();
    requireXML();
    try {
      if (processContent()) {
        // Parse with the XML Copy Handler
        XMLCopy handler = new XMLCopy(xml);
        handleXML(this, handler, true);
      } else {
        handleError(this);
      }
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
   * @throws ContentException If an error occurred while consuming the content.
   */
  public void consumeXML(XMLWriter xml, Templates templates) throws ContentException {
    consumeXML(xml, templates, null);
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
   * @param xml       The XML to copy from PageSeeder
   * @param templates A set of templates to process the XML
   *
   * @throws ContentException If an error occurred while consuming the content.
   */
  public void consumeXML(XMLWriter xml, Templates templates, Map<String, String> parameters)
      throws ContentException {
    requireAvailable();
    requireXML();
    try {
      if (processContent()) {
        transformXML(this, xml, templates, parameters);
      } else {
        handleError(this);
      }
    } catch (IOException ex) {
      throw new ContentException("Unable to transform XML", ex);
    } finally {
      this.state = State.consumed;
    }
  }

  // Extractors
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the media type from the HTTP response stripped of its charset sub-declaration.
   *
   * @param connection The HTTP connection
   * @return the media type from the HTTP response stripped of its charset sub-declaration.
   */
  private static String getMediaType(HttpURLConnection connection) {
    String mediaType = connection.getContentType();
    // Strip ";charset" declaration if any
    if (mediaType != null && mediaType.indexOf(";charset=") > 0) {
      mediaType = mediaType.substring(0, mediaType.indexOf(";charset="));
    }
    return mediaType;
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
  private static PSSession updateSession(HttpURLConnection connection, PSSession session) {
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
  private static final List<Header> extractHeaders(HttpURLConnection connection) {
    Map<String, List<String>> headerFields = connection.getHeaderFields();
    // Length is 1 less than headers from connection as we discard the status line
    List<Header> headers = new ArrayList<>(headerFields.size()-1);
    for (Entry<String, List<String>> h : headerFields.entrySet()) {
      String name = h.getKey();
      if (name != null) {
        if ("content-length".equalsIgnoreCase(name)) {
          headers.add(new Header(name, connection.getContentLengthLong()));
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
  private static final Charset detectCharset(HttpURLConnection connection) {
    String contentType = connection.getHeaderField("Content-Type");
    if (contentType == null) return null;
    int x = contentType.indexOf("charset=");
    if (x > 0) {
      String name = contentType.substring(x).replaceAll("^charset=([a-zA-Z0-8_-]+).*", "$1");
      Charset.forName(name);
    } else if (contentType.indexOf("xml") > 0) return StandardCharsets.UTF_8;
    return null;
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  private boolean processContent() {
    return isSuccessful() || (this.consumeError && isError(this._statusCode));
  }

  /**
   * Check if this response is ready to be consumed.
   *
   * @throws IllegalStateException if that isn't the case.
   */
  private void requireAvailable() {
    switch (this.state) {
      case available: return;
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
   * @param connection The HTTP URL connection.
   * @param response   Stores metadata about the response including error details.
   * @param handler    Handles the XML.
   * @param duplex     Whether to use duplex mode
   *
   * @throws IOException If an error occurs while writing the XML.
   */
  private static void handleXML(Response response, DefaultHandler handler, boolean duplex)
      throws IOException {

    // Setup the factory
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);

    HttpURLConnection connection = response._connection;
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
      HandlerDispatcher dispatcher = new HandlerDispatcher(parser.getXMLReader(), response, handler);
      dispatcher.setDuplex(duplex);
      parser.parse(source, dispatcher);

    } catch (ParserConfigurationException ex) {
      throw new ContentException("Error while configuring XML parser", ex);
    } catch (SAXException ex) {
      throw new ContentException("Error while parsing XML", ex);
    }
  }

  /**
   * Parse the response as XML and transform the output.
   *
   * @param connection The HTTP URL connection.
   * @param response   Stores metadata about the response including error details.
   * @param xml        Where the final XML goes.
   * @param templates  To transform the XML.
   * @param parameters Parameters to send to the transformer (optional).
   *
   * @return <code>true</code> if the data was parsed without error;
   *         <code>false</code> otherwise.
   *
   * @throws IOException If an error occurs while writing the XML.
   */
  private static boolean transformXML(Response response, XMLWriter xml,
      Templates templates, Map<String, String> parameters) throws IOException {
    boolean ok = true;

    // Create an XML Buffer
    StringWriter buffer = new StringWriter();
    HttpURLConnection connection = response._connection;
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
  private static boolean isXML(String mediaType) {
    if (mediaType == null) return false;
    return "text/xml".equals(mediaType)
        || "application/xml".equals(mediaType)
        || mediaType.endsWith("+xml");
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
   * Indicates whether the response failed based on the HTTP code.
   *
   * @param code the HTTP status code.
   * @return <code>true</code> if the code is greater than 400 (included);
   *         <code>false</code>.
   */
  private static boolean isError(int code) {
    return code >= HttpURLConnection.HTTP_BAD_REQUEST;
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
    if (isSuccessful(connection.getResponseCode()))
      return connection.getInputStream();
    else
      return connection.getErrorStream();
  }

  /**
   * Adds the attributes for when error occurs
   *
   * @param response The response
   *
   * @throws IOException If thrown while writing the XML.
   */
  private static void handleError(Response response) throws IOException {
    try (InputStream err = getErrorStream(response._connection)) {

      // Setup the input source
      InputSource source = new InputSource(err);
      source.setSystemId(response._connection.getURL().toString());

      // Set the character set
      Charset charset = response.charset();
      if (charset != null) {
        source.setEncoding(charset.name());
      }

      // And parse!
      PSErrorHandler.parseError(source, response);
    }
  }

  /**
   * Returns the error stream to parse.
   *
   * <p>If debug is enabled, the content of the error stream is printed onto the System error stream.
   *
   * @param connection HTTP connection.
   *
   * @return the error stream.
   *
   * @throws IOException If thrown while writing the XML.
   */
  private static InputStream getErrorStream(HttpURLConnection connection) throws IOException {
    InputStream err = null;
    if (LOGGER.isDebugEnabled()) {
      InputStream tmp = connection.getErrorStream();
      int length = connection.getContentLength();
      try {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        copy(tmp, buffer, length);
        buffer.writeTo(System.err);
        err = new ByteArrayInputStream(buffer.toByteArray());
      } finally {
        closeQuietly(tmp);
      }
    } else {
      err = connection.getErrorStream();
    }
    return err;
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
    int n = 0;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
    }
  }

  /**
   * Copy the input stream to the output stream as UTF-8 using a buffer of 4096 bytes.
   *
   * @param input  The data to copy
   * @param output The output
   * @param length The expected length
   *
   * @throws IOException Any error reported while writing on the output
   */
  private static void copy(Reader reader, Writer writer, int length) throws IOException {
    // NB. length is in bytes, there are more bytes than characters
    int bufferSize = length <= 0 || length > 4096? 4096 : length;
    char[] buffer = new char[bufferSize];
    int n = 0;
    while (-1 != (n = reader.read(buffer))) {
      writer.write(buffer, 0, n);
    }
  }

  /**
   * A handler that wraps a specified handler but will automatically dispatch to different handler
   * if an error is detected.
   *
   * @author Christophe Lauret
   * @version 0.1.0
   */
  private static class HandlerDispatcher extends DefaultHandler {

    /** Handler to use if response is not an error */
    private final DefaultHandler handler;

    /** XML Reader in use to do the dispatching */
    private final XMLReader reader;

    /** Response to forward to error handler in case of an error only */
    private final Response response;

    /**
     * In duplex mode, both
     */
    private boolean duplex = false;

    /**
     * @param reader   The XML currently processing
     * @param response The response metadata
     * @param handler  THe handler to use unless an error is detected.
     */
    public HandlerDispatcher(XMLReader reader, Response response, DefaultHandler handler) {
      this.reader = reader;
      this.handler = handler != null ? handler : new DefaultHandler();
      this.response = response;
    }

    /**
     * Whether to use duplex mode or not.
     *
     * @param duplex <code>true</code> to dispatch to a duplex handler;
     *               <code>false</code> to dispatch to single handler.
     */
    public void setDuplex(boolean duplex) {
      this.duplex = duplex;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      DefaultHandler actual = this.handler;
      // If the first element is "error" we dispatch to error handler.
      if ("error".equals(localName) || "error".equals(qName)) {
        if (this.duplex) {
          actual = new DuplexHandler(new PSErrorHandler(this.response), actual);
        } else {
          actual = new PSErrorHandler(this.response);
        }
      }
      this.reader.setContentHandler(actual);
      actual.startDocument();
      actual.startElement(uri, localName, qName, attributes);
    }

  }

  /**
   * Extracts the error message from the "message" element in the XML response returned by PageSeeder.
   *
   * @author Christophe Lauret
   * @version 0.1.0
   */
  private static class PSErrorHandler extends DefaultHandler {

    /** Name of the error element. */
    private static final String ERROR_ELEMENT = "error";

    /** Name of the message element. */
    private static final String MESSAGE_ELEMENT = "message";

    /** The error message. */
    private final StringBuilder message = new StringBuilder();

    /** The response to update */
    private Response response;

    /** State: Within "message" element. */
    private boolean isMessage = false;

    public PSErrorHandler(Response response) {
      this.response = response;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (ERROR_ELEMENT.equals(localName) || ERROR_ELEMENT.equals(qName)) {
        this.response.errorID = attributes.getValue("id");
      } else if (MESSAGE_ELEMENT.equals(localName) || MESSAGE_ELEMENT.equals(qName)) {
        this.isMessage = true;
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      if (MESSAGE_ELEMENT.equals(localName) || MESSAGE_ELEMENT.equals(qName)) {
        this.isMessage = false;
        this.response.message = this.message.toString();
      }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
      if (this.isMessage) {
        this.message.append(ch, start, length);
      }
    }

    /**
     * Returns the error message found in the specified XML Input Source.
     *
     * @param source the XML input source to parse.
     *
     * @throws IOException If unable to parse response due to IO error.
     */
    public static void parseError(InputSource source, Response response) throws IOException {
      try {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(false);

        // And parse!
        SAXParser parser = factory.newSAXParser();
        PSErrorHandler handler = new PSErrorHandler(response);
        parser.parse(source, handler);
      } catch (SAXException ex) {
        LOGGER.warn("Unable to parse error message from PS Response", ex);
      } catch (ParserConfigurationException ex) {
        LOGGER.warn("Unable to parse error message from PS Response", ex);
      }
    }

  }

  /**
   * Forwards to two separate handlers as once.
   *
   * @author Christophe Lauret
   */
  private static class DuplexHandler extends DefaultHandler {

    private final DefaultHandler a;

    private final DefaultHandler b;

    public DuplexHandler(DefaultHandler a, DefaultHandler b) {
      this.a = a;
      this.b = b;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      this.a.characters(ch, start, length);
      this.b.characters(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
      this.a.endDocument();
      this.b.endDocument();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      this.a.endElement(uri, localName, qName);
      this.b.endElement(uri, localName, qName);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
      this.a.endPrefixMapping(prefix);
      this.b.endPrefixMapping(prefix);
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
      this.a.error(ex);
      this.b.error(ex);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
      this.a.fatalError(e);
      this.b.fatalError(e);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      this.a.ignorableWhitespace(ch, start, length);
      this.b.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
      this.a.notationDecl(name, publicId, systemId);
      this.b.notationDecl(name, publicId, systemId);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
      this.a.processingInstruction(target, data);
      this.b.processingInstruction(target, data);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
      this.a.setDocumentLocator(locator);
      this.b.setDocumentLocator(locator);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
      this.a.skippedEntity(name);
      this.b.skippedEntity(name);
    }

    @Override
    public void startDocument() throws SAXException {
      this.a.startDocument();
      this.b.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      this.a.startElement(uri, localName, qName, attributes);
      this.b.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
      this.a.startPrefixMapping(prefix, uri);
      this.b.startPrefixMapping(prefix, uri);
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName)
        throws SAXException {
      this.a.unparsedEntityDecl(name, publicId, systemId, notationName);
      this.b.unparsedEntityDecl(name, publicId, systemId, notationName);
    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {
      this.a.warning(ex);
      this.b.warning(ex);
    }
  }

}
