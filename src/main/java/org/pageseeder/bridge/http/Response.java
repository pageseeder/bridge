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
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

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
import org.pageseeder.bridge.net.PSHTTPResponseInfo.Status;
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
 * @author Christophe Lauret
 * @version 0.9.1
 * @since 0.9.1
 */
public final class Response {

  /**
   * Logger for this class.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(Response.class);

  /**
   * Holds the connection.
   */
  private final HttpURLConnection _connection;

  /**
   * The HTTP response code.
   */
  private int responseCode;

  /**
   *
   */
  private boolean includeError = false;

  /**
   * Session from request or updated by 'Set-Cookie' header.
   */
  private PSSession session;

  /**
   * The message returned by PageSeeder.
   */
  private String message = "";

  /**
   * The media type returned by PageSeeder.
   */
  private String mediaType = "";

  /**
   * The error ID returned by PageSeeder (services only)
   */
  private String errorID = "";

  /**
   * The status of response.
   */
  private Status status = Status.UNKNOWN;

  /**
   * The list of HTTP response headers.
   */
  private List<Header> headers = new ArrayList<>();

  /**
   * Construct a new response wrapping a HTTP URL connection.
   *
   * @param connection
   * @throws IOException
   */
  Response(HttpURLConnection connection) throws IOException{
    this(connection, null);
  }

  /**
   * Construct a new response wrapping a HTTP URL connection.
   *
   * @param connection
   * @throws IOException
   */
  Response(HttpURLConnection connection, PSSession session) throws IOException {
    this._connection = connection;
    this.session = session;
    init();
  }

  /**
   * Construct a new response in an error state.
   */
  Response(Status status, String message) {
    this._connection = null;
    this.status = status;
    this.message = message;
  }

  // Getters
  // ----------------------------------------------------------------------------------------------

  public int code() {
    return this.responseCode;
  }

  public String message() {
    return this.message;
  }

  /**
   * Returns the value of the
   *
   * @param name The name of the header (case insensitive)
   *
   * @return The correponding value or <code>null</code>;
   */
  public String header(String name) {
    for (Header h : this.headers) {
      if (h.name().equalsIgnoreCase(name)) return h.value();
    }
    return null;
  }

  /**
   * Returns the value of "Content-Length" response header.
   *
   * <pre><code>ETag: 5.8908</code></pre>
   *
   * @return the value of "Content-Length" response header.
   */
  public String etag() {
    return unwrapEtag(header("Etag"));
  }

  /**
   * Removes the quotes around the etag
   *
   * <pre>
   *  "123456789"   – A strong ETag validator
   *  W/"123456789" – A weak ETag validator
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
   * @return the session.
   */
  public PSSession session() {
    return this.session;
  }

  /**
   *
   */
  public InputStream getInputStream() {
    if (this._connection == null) return null;
    try {
      if (isOK(this._connection.getResponseCode()))
        return this._connection.getInputStream();
      else
        return this._connection.getErrorStream();
    } catch (IOException ex) {
      throw new IllegalStateException("Too late content has already been consumed");
    }
  }

  // State
  // ----------------------------------------------------------------------------------------------

  /**
   * Indicates whether the response is XML based on the content type.
   *
   * <p>It is considered XML the mediatype is equal to "text/xml" or "application/xml" or
   * ends with "+xml".
   *
   * @return <code>true</code> if XML;
   *         <code>false</code> otherwise.
   */
  public boolean isXML() {
    return isXML(this.mediaType);
  }

  public boolean isSuccessful() {
    return isOK(this.responseCode);
  }

  // Processors
  // ----------------------------------------------------------------------------------------------

  /**
   * Consumes the output of the response.
   *
   * @param out Where the output should be copied.
   *
   * @throws IOException If an error occurs when processing the content
   */
  public void consumeBytes(OutputStream out) throws IOException {
    try {
      if (processContent()) {
        copy(this._connection, out);

      } else {
        LOGGER.info("PageSeeder returned {}: {}", this.status, this._connection.getResponseMessage());
        parseError(this._connection, this);
      }

    } catch (IOException ex) {
      LOGGER.warn("Unable to connect to PageSeeder: {}", this.message);
      setStatus(Status.CONNECTION_ERROR, Objects.toString(ex.getMessage(), "Unable to connect"));
      throw ex;
    }
  }

  /**
   * Consumes the output of the response.
   *
   * @return the output content as a byte array.
   *
   * @throws IOException If an error occurs when processing the content
   */
  public byte[] consumeBytes() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    consumeBytes(bytes);
    return bytes.toByteArray();
  }

  /**
   * Consumes the output of the response.
   *
   * @param out Where the output should be written to.
   *
   * @throws IOException If an error occurs when processing the content
   */
  public void consumeChars(Writer out) throws IOException {
    try {
      if (processContent()) {
        copy(this._connection, out);

      } else {
        LOGGER.info("PageSeeder returned {}: {}", this.status, this._connection.getResponseMessage());
        parseError(this._connection, this);
      }

    } catch (IOException ex) {
      LOGGER.warn("Unable to connect to PageSeeder: {}", this.message);
      setStatus(Status.CONNECTION_ERROR, Objects.toString(ex.getMessage(), "Unable to connect"));
      throw ex;
    }
  }

  /**
   * Consumes the output of the response.
   *
   * @returns the output as a string.
   *
   * @throws IOException If an error occurs when processing the content
   */
  public String consumeString() throws IOException {
    StringWriter out = new StringWriter();
    consumeChars(out);
    return out.toString();
  }

  /**
   * Consumes the output of the response using a SAX handler.
   *
   * @param handler The SAX handler for the XML
   *
   * @throws IOException If an error occurs when processing the content
   */
  public void consumeSAX(DefaultHandler handler) throws IOException {
    try {
      if (processContent()) {

        // Return content is XML try to parse it
        if (isXML(this.mediaType)) {
          handleXML(this._connection, this, handler, false);
        } else {
          setStatus(Status.PROCESS_ERROR, "Unable to parse non-XML media type");
        }

      } else {
        parseError(this._connection, this);
        LOGGER.info("PageSeeder returned {} {}: {} {}", this.status, this._connection.getResponseMessage(), this.errorID, this.message);
      }

      // Could not connect to the server
    } catch (ConnectException ex) {
      LOGGER.warn("Unable to connect to PageSeeder: {}", this.message);
      setStatus(Status.CONNECTION_ERROR, Objects.toString(ex.getMessage(), "Unable to connect"));
      throw ex;
    }
  }

  /**
   * Consumes the output of the response using a handler and returns the collection
   * of objects from it.
   *
   * @param handler The object handler for the XML
   *
   * @throws IOException If an error occurs when processing the content
   */
  public <T> List<T> consumeCollection(Handler<T> handler) throws IOException {
    consumeSAX(handler);
    return handler.list();
  }

  /**
   * Consumes the output of the response using a handler and returns a single
   * object from it.
   *
   * @param handler The object handler for the XML
   *
   * @throws IOException If an error occurs when trying to write the XML.
   */
  public <T> T consumeItem(Handler<T> handler) throws IOException {
    consumeSAX(handler);
    return handler.get();
  }

  /**
   * Process the specified PageSeeder connection.
   *
   * <p>The xml writer receives a copy of the PageSeeder XML.
   *
   * @param response The response object.
   * @param xml      The XML to copy from PageSeeder
   *
   * @throws IOException If an error occurs when trying to write the XML.
   */
  public void consumeXML(XMLWriter xml) throws IOException {
    try {
      if (processContent()) {

        // Return content is XML try to parse it
        if (isXML(this.mediaType)) {
          // Parse with the XML Copy Handler
          XMLCopy handler = new XMLCopy(xml);
          handleXML(this._connection, this, handler, true);
        } else {
          setStatus(Status.PROCESS_ERROR, "Unable to copy non-XML media type");
        }

      } else {
        LOGGER.info("PageSeeder returned {}: {}", this.status, this._connection.getResponseMessage());
        parseError(this._connection, this);
      }

      // Could not connect to the server
    } catch (ConnectException ex) {
      LOGGER.warn("Unable to connect to PageSeeder: {}", this.message);
      setStatus(Status.CONNECTION_ERROR, Objects.toString(ex.getMessage(), "Unable to connect"));
      throw ex;
    }
  }

  /**
   * Process the specified PageSeeder connection.
   *
   * <p>Templates can be specified to transform the XML.
   *
   * @param response  The PageSeeder response metadata to update
   * @param xml       The XML to copy from PageSeeder
   * @param templates A set of templates to process the XML (optional)
   *
   * @throws IOException If an error occurs when trying to write the XML.
   *
   * @return The updated response metadata.
   */
  public void consumeAndTransform(XMLWriter xml, Templates templates) throws IOException {
    consumeAndTransform(xml, templates, null);
  }

  /**
   * Process the specified PageSeeder connection.
   *
   * <p>Templates can be specified to transform the XML.
   *
   * @param response  The PageSeeder response metadata to update
   * @param xml        The XML to copy from PageSeeder
   * @param templates  A set of templates to process the XML (optional)
   * @param parameters Parameters to send to the XSLT transformer (optional)
   *
   * @throws IOException If an error occurs when trying to write the XML.
   *
   * @return The updated response metadata.
   */
  public void consumeAndTransform(XMLWriter xml, Templates templates, Map<String, String> parameters)
      throws IOException {
    try {
      if (processContent()) {

        // Return content is XML try to parse it
        if (isXML(this.mediaType)) {
          transformXML(this._connection, this, xml, templates, parameters);
        } else {
          setStatus(Status.PROCESS_ERROR, "Unable to copy non-XML media type");
        }

      } else {
        LOGGER.info("PageSeeder returned {}: {}", this.status, this._connection.getResponseMessage());
        parseError(this._connection, this);
      }

      // Could not connect to the server
    } catch (ConnectException ex) {
      LOGGER.warn("Unable to connect to PageSeeder: {}", this.message);
      setStatus(Status.CONNECTION_ERROR, Objects.toString(ex.getMessage(), "Unable to connect"));
      throw ex;
    }
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  private void init() {
    try {
      this.responseCode = this._connection.getResponseCode();
      this.message = this._connection.getResponseMessage();
      setCodeAndStatus(this.responseCode);

      // Extract the mediatype
      this.mediaType = getMediaType(this._connection);

      // Extract the other headers
      for (Entry<String, List<String>> h : this._connection.getHeaderFields().entrySet()) {
        String name = h.getKey();
        if (name != null) {
          for (String value : h.getValue()) {
            this.headers.add(new Header(name, value));
          }
        }
      }

      // Update the session
      updateSession(this._connection);

    } catch (IOException ex) {
      this.responseCode = -1;
      // TODO
    } finally {
      LOGGER.info("{} [{}] -> {}", this._connection.getURL(), this._connection.getRequestMethod(), this.responseCode);
    }
  }

  /**
   * @param status the status of the response
   * @param message the message to set
   */
  private void setStatus(Status status, String message) {
    this.status = status;
    this.message = message;
  }

  private boolean processContent() {
    return isOK(this.responseCode) || (this.includeError && isError(this.responseCode));
  }

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
   * @param connection the HTTP connection
   */
  private void updateSession(HttpURLConnection connection) {
    // Updating the session
    if (this.session != null) {
      this.session.update();
    } else {
      String cookie = connection.getHeaderField("Set-Cookie");
      this.session = PSSession.parseSetCookieHeader(cookie);
    }
  }

  /**
   * Indicates if the content type corresponds to XML content.
   *
   * @param contentType The content type
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
   * @param code the code to set
   */
  void setCodeAndStatus(int code) {
    if (code >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
      this.status = Status.SERVER_ERROR;
    } else if (code >= HttpURLConnection.HTTP_BAD_REQUEST) {
      this.status = Status.CLIENT_ERROR;
    } else if (code >= HttpURLConnection.HTTP_MULT_CHOICE) {
      this.status = Status.REDIRECT;
    } else if (code >= HttpURLConnection.HTTP_OK) {
      this.status = Status.SUCCESSFUL;
    } else {
      this.status = Status.UNKNOWN;
    }
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
  private static void handleXML(HttpURLConnection connection, Response response, DefaultHandler handler, boolean duplex)
      throws IOException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);

    InputStream in = null;
    try {
      // Get the source as input stream
      in = isOK(connection.getResponseCode()) ? connection.getInputStream() : connection.getErrorStream();
      InputSource source = new InputSource(in);
      source.setSystemId(connection.getURL().toString());

      // Ensure the encoding is correct
      String encoding = connection.getContentEncoding();
      if (encoding != null) {
        source.setEncoding(encoding);
      }

      // And parse!
      SAXParser parser = factory.newSAXParser();
      HandlerDispatcher dispatcher = new HandlerDispatcher(parser.getXMLReader(), response, handler);
      dispatcher.setDuplex(duplex);
      parser.parse(source, dispatcher);

    } catch (IOException ex) {
      LOGGER.warn("Error while parsing XML data from URL", ex);
      response.setStatus(Status.IO_ERROR, ex.getMessage());

    } catch (ParserConfigurationException ex) {
      LOGGER.warn("Error while configuring parser for PageSeeder data", ex);
      response.setStatus(Status.PROCESS_ERROR, ex.getMessage());

    } catch (SAXException ex) {
      LOGGER.info("Error parsing XML response!", ex);
      response.setStatus(Status.PROCESS_ERROR, ex.getMessage());

    } finally {
      closeQuietly(in);
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
  private static boolean transformXML(HttpURLConnection connection, Response response, XMLWriter xml,
      Templates templates, Map<String, String> parameters) throws IOException {
    boolean ok = true;

    // Create an XML Buffer
    StringWriter buffer = new StringWriter();

    InputStream in = null;
    try {
      in = isOK(connection.getResponseCode()) ? connection.getInputStream() : connection.getErrorStream();

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
      LOGGER.warn("Error while transforming XML data from URL", ex);
      response.setStatus(Status.PROCESS_ERROR, ex.getMessageAndLocation());

    } catch (IOException ex) {
      LOGGER.warn("Error while parsing XML data from URL", ex);
      response.setStatus(Status.PROCESS_ERROR, ex.getMessage());

    } finally {
      closeQuietly(in);
    }

    // Write as XML
    xml.writeXML(buffer.toString());

    return ok;
  }

  /**
   * Parse the response as text.
   *
   * @param connection The HTTP URL connection.
   * @param out        Where the raw output should be copied to.
   *
   * @return <code>true</code> if the data was parsed without error;
   *         <code>false</code> otherwise.
   *
   * @throws IOException If an error occurs while writing the XML.
   */
  private static boolean copy(HttpURLConnection connection, OutputStream out) throws IOException {
    // Get the source as input stream
    boolean ok = true;
    int length = connection.getContentLength();
    try (BufferedInputStream in = new BufferedInputStream(toInputStream(connection))){
      copy(in, out, length);
    } catch (IOException ex) {
      LOGGER.warn("Error while parsing text data from URL", ex);
      ok = false;
    }
    return ok;
  }

  /**
   * Parse the response as text.
   *
   * @param connection The HTTP URL connection.
   * @param out        Where the raw output should be copied to.
   *
   * @return <code>true</code> if the data was parsed without error;
   *         <code>false</code> otherwise.
   *
   * @throws IOException If an error occurs while writing the XML.
   */
  private static boolean copy(HttpURLConnection connection, Writer out) throws IOException {
    boolean ok = true;
    String encoding = connection.getContentEncoding();
    if (encoding == null) {
      encoding = "utf-8";
    }
    int length = connection.getContentLength();
    try (Reader r = new InputStreamReader(new BufferedInputStream(toInputStream(connection)), encoding)) {
      copy(r, out, length);

    } catch (IOException ex) {
      LOGGER.warn("Error while parsing text data from URL", ex);
      ok = false;
    }
    return ok;
  }

  /**
   * Indicates whether the response was successful based on the HTTP code.
   *
   * @param code the HTTP status code.
   * @return <code>true</code> if the code is between 200 and 299 (included);
   *         <code>false</code>.
   */
  private static boolean isOK(int code) {
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

  private static InputStream toInputStream(HttpURLConnection connection) throws IOException {
    if (isOK(connection.getResponseCode()))
      return connection.getInputStream();
    else
      return connection.getErrorStream();
  }

  /**
   * Adds the attributes for when error occurs
   *
   * @param connection The PS Connection.
   * @param info       The response info.
   *
   * @throws IOException If thrown while writing the XML.
   */
  private static void parseError(HttpURLConnection connection, Response info) throws IOException {
    try (InputStream err = getErrorStream(connection)) {

      // Setup the input source
      InputSource source = new InputSource(err);
      String encoding = connection.getContentEncoding();
      if (encoding != null) {
        source.setEncoding(encoding);
      }

      // And parse!
      PSErrorHandler.parseError(source, info);

    } catch (IOException ex) {
      ex.printStackTrace();
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
   * Copy the input stream to the output stream as UTF-8 using a buffer of 4096 bytes.
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
