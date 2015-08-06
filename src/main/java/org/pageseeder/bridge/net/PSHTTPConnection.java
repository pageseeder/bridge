/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.net;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.pageseeder.berlioz.xml.XMLCopy;
import org.pageseeder.bridge.PSCredentials;
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
 * Wraps an HTTP connection to PageSeeder.
 *
 * <p>Note: This class was initially forked from Bastille 0.8.29
 *
 * @author Christophe Lauret
 *
 * @version 0.3.32
 * @since 0.2.0
 */
public final class PSHTTPConnection {

  /** Logger for this class */
  private static final Logger LOGGER = LoggerFactory.getLogger(PSHTTPConnection.class);

  /**
   * Not strictly HTTP method as multipart request use a separate method.
   */
  public enum Method {

    /** For request using the HTTP GET method. */
    GET,

    /** For request using the HTTP POST method. */
    POST,

    /** For request using the HTTP PUT method. */
    PUT,

    /** For request using the HTTP DELETE method. */
    DELETE,

    /** A Form-Multipart request via POST. */
    MULTIPART

  };

  /** Version of the API */
  private static final String API_VERSION;
  static {
    Package p = Package.getPackage("org.pageseeder.api");
    API_VERSION = p != null ? p.getImplementationVersion() : "dev";
  }

  /**
   * UTF-8.
   */
  private static final Charset UTF8 = Charset.forName("utf-8");

  /**
   * Byte for carriage return + line feed.
   */
  private static final byte[] CRLF = "\r\n".getBytes(UTF8);

  /**
   * Used to generate boundary parts.
   */
  private static Random random = new Random();

  /**
   * The wrapped HTTP Connection.
   */
  private final HttpURLConnection _connection;

  /**
   * The PageSeeder resource corresponding to the target of the URL.
   */
  private final PSHTTPResource _resource;

  /**
   * The method for the connection.
   */
  private final Method _method;

  /**
   * The part boundary.
   */
  private final String _boundary;

  /**
   * The output stream used to write the data to push through the connection (e.g. Multipart).
   */
  private DataOutputStream out = null;

  /**
   * The user who initiated the connection.
   *
   * A <code>null</code> value indicates an anonymous connection.
   */
  private PSSession session;

  /**
   * Can only be created by the factory method.
   *
   * @param connection The wrapped HTTP Connection.
   * @param resource   The underlying resource to access.
   * @param method     The method of connection.
   * @param session    The user who initiated the connection (may be <code>null</code>)
   * @param boundary   The boundary to use for multipart only (may be <code>null</code>)
   */
  private PSHTTPConnection(HttpURLConnection connection, PSHTTPResource resource, Method method, PSSession session, String boundary) {
    this._connection = connection;
    this._resource = resource;
    this._method = method;
    this._boundary = boundary;
    this.session = session;
  }

  /**
   * Close the multipart boundary.
   *
   * @throws IOException
   */
  private void endMultipart() throws IOException {
    if (this.out != null) {
      write(this._boundary, this.out);
      write("--", this.out);
      writeCRLF(this.out);
      this.out.flush();
    }
  }

  /**
   * Add a part to the request (write the contents directly to the stream).
   *
   * @param part The encoding to specify in the Part's header
   * @throws IOException Should any error occur while writing the part on the output
   */
  public void addXMLPart(String part) throws IOException {
    addXMLPart(part, null);
  }

  /**
   * Add a part to the request (write the contents directly to the stream).
   *
   * @param part    The encoding to specify in the Part's header
   * @param headers A list of headers added to this XML Part ('content-type' header is ignored)
   *
   * @throws IOException Should any error occur while writing
   */
  public void addXMLPart(String part, Map<String, String> headers) throws IOException {
    if (this.out == null) {
      this.out = new DataOutputStream(this._connection.getOutputStream());
    }
    try {
      if (this._method != Method.MULTIPART) throw new IOException("Cannot add XML part unless connection type is set to Multipart");

      // Start with boundary
      write(this._boundary, this.out);
      writeCRLF(this.out);

      // Headers if specified
      if (headers != null) {
        for (Entry<String, String> h : headers.entrySet()) {
          String name = h.getKey();
          if (!"content-type".equalsIgnoreCase(name)) {
            write(name + ": " + headers.get(h.getValue()), this.out);
            writeCRLF(this.out);
          }
        }
      }

      // Write content type
      write("Content-Type: text/xml; charset=\"utf-8\"", this.out);
      writeCRLF(this.out);
      writeCRLF(this.out);
      write(part, this.out);
      writeCRLF(this.out);
      this.out.flush();

    } catch (IOException ex) {
      closeQuietly(this.out);
      this.out = null;
      throw ex;
    }
  }

  /**
   * Add a part to the request from a file(write the contents directly to the stream).
   *
   * @param part    The encoding to specify in the Part's header
   *
   * @throws IOException Should any error occur while writing
   */
  public void addPart(File part) throws IOException {
    if (this.out == null) {
      this.out = new DataOutputStream(this._connection.getOutputStream());
    }
    try {
      if (this._method != Method.MULTIPART) throw new IOException("Cannot add file part unless connection type is set to Multipart");

      // Start with boundary
      write(this._boundary, this.out);
      writeCRLF(this.out);

      // Write headers
      write("Content-Disposition: form-data; name=\"file-1\"; filename=\"" + part.getName() + "\"", this.out);
      writeCRLF(this.out);
      write("Content-Type: " + URLConnection.guessContentTypeFromName(part.getName()), this.out);
      writeCRLF(this.out);
      write("Content-Transfer-Encoding: binary", this.out);
      writeCRLF(this.out);
      writeCRLF(this.out);
      this.out.flush();

      // Copy binary file content
      FileInputStream fis = null;
      try {
        fis = new FileInputStream(part);
        copy(fis, this.out);
      } finally {
        closeQuietly(fis);
      }

      writeCRLF(this.out);
      this.out.flush();

    } catch (IOException ex) {
      closeQuietly(this.out);
      this.out = null;
      throw ex;
    }
  }

  /**
   * Add a parameter as part of a multipart request.
   *
   * @param name  the name of the parameter
   * @param value the value of the parameter
   *
   * @throws IOException Should any error occur while writing
   */
  public void addParameterPart(String name, String value) throws IOException {
    if (this.out == null) {
      this.out = new DataOutputStream(this._connection.getOutputStream());
    }
    try {
      if (this._method != Method.MULTIPART) throw new IOException("Cannot add parameter connection type is set to Multipart");

      // Start with boundary
      write(this._boundary, this.out);
      writeCRLF(this.out);

      // Write Parameter
      write("Content-Disposition: form-data; name=\"" + name + "\"", this.out);
      writeCRLF(this.out);
      writeCRLF(this.out);
      write(value, this.out);
      writeCRLF(this.out);
      this.out.flush();

    } catch (IOException ex) {
      closeQuietly(this.out);
      this.out = null;
      throw ex;
    }
  }

  /**
   * Closes the output stream when writing to the connection.
   *
   * <p>This method does nothing if the output stream hasn't been created.
   *
   * @throws IOException If thrown by the close method.
   */
  public void closeOutput() throws IOException {
    if (this.out != null) {
      this.out.close();
    }
  }

  /**
   * Returns the response code of the underlying HTTP connection.
   *
   * @see HttpURLConnection#getResponseCode()
   * @return the response code of the underlying HTTP connection.
   * @throws IOException If thrown by the underlying HTTP connection.
   */
  public int getResponseCode() throws IOException {
    return this._connection.getResponseCode();
  }

  /**
   * Returns the response message of the underlying HTTP connection.
   *
   * @see HttpURLConnection#getResponseMessage()
   * @return the response message of the underlying HTTP connection.
   * @throws IOException If thrown by the underlying HTTP connection.
   */
  public String getResponseMessage() throws IOException {
    return this._connection.getResponseMessage();
  }

  /**
   * Returns the content type of the underlying HTTP connection.
   *
   * @see HttpURLConnection#getContentType()
   * @return the content type of the underlying HTTP connection.
   * @throws IOException If thrown by the underlying HTTP connection.
   */
  public String getContentType() throws IOException {
    return this._connection.getContentType();
  }

  /**
   * Returns the underlying HTTP connection.
   *
   * <p>This method can be useful to perform additional operations on the connection which are not
   * provided by this class.
   *
   * @return the underlying HTTP connection.
   */
  public HttpURLConnection connection() {
    return this._connection;
  }

  /**
   * Returns the PageSeeder resource corresponding to the URL.
   *
   * @return the PageSeeder resource corresponding to the URL.
   */
  public PSHTTPResource resource() {
    return this._resource;
  }

  /**
   * Returns the type of connection.
   *
   * @return the type of connection.
   */
  public Method method() {
    return this._method;
  }

  /**
   * The session may have been updated after the connection was made.
   *
   * @return the session
   */
  public PSSession getSession() {
    return this.session;
  }

  // Process methods
  // ----------------------------------------------------------------------------------------------

  /**
   * Process the specified PageSeeder connection.
   *
   * @param response the response info
   * @param out      where the output should be copied.
   *
   * @throws IOException If an error occurs when trying to write the XML.
   */
  public void process(PSHTTPResponseInfo response, OutputStream out) throws IOException {
    if (this._method == Method.MULTIPART) {
      endMultipart();
    }
    try {
      // Retrieve the content of the response
      int status = this._connection.getResponseCode();
      response.setCodeAndStatus(status);

      if (isOK(status) || (this._resource.includeErrorContent() && isError(status))) {
        String mediaType = getMediaType(this._connection);
        response.setMediaType(mediaType);
        copy(this._connection, out);
        updateSession(this._connection);

      } else {
        LOGGER.info("PageSeeder returned {}: {}", status, this._connection.getResponseMessage());
        parseError(this._connection, response);
      }

      // Could not connect to the server
    } catch (ConnectException ex) {
      String message = ex.getMessage();
      LOGGER.warn("Unable to connect to PageSeeder: {}", message);
      response.setStatus(Status.CONNECTION_ERROR, message != null ? message : "Unable to connect");
      throw ex;
    }
  }

  /**
   * Process the specified PageSeeder connection.
   *
   * <p>If the handler is not specified, the xml writer receives a copy of the PageSeeder XML.
   *
   * @param response the response info
   * @param handler  the handler for the XML (can be used to rewrite the XML)
   *
   * @throws IOException If an error occurs when trying to write the XML.
   */
  public void process(PSHTTPResponseInfo response, DefaultHandler handler) throws IOException {
    if (this._method == Method.MULTIPART) {
      endMultipart();
    }
    try {
      // Retrieve the content of the response
      int status = this._connection.getResponseCode();
      response.setCodeAndStatus(status);

      if (isOK(status) || (this._resource.includeErrorContent() && isError(status))) {
        String mediaType = getMediaType(this._connection);
        response.setMediaType(mediaType);

        // Return content is XML try to parse it
        if (isXML(mediaType)) {
          handleXML(this._connection, response, handler, false);
        } else {
          response.setStatus(Status.PROCESS_ERROR, "Unable to parse non-XML media type");
        }

        // Ensure the session is updated for that user
        updateSession(this._connection);

      } else {
        LOGGER.info("PageSeeder returned {}: {}", status, this._connection.getResponseMessage());
        parseError(this._connection, response);
      }

      // Could not connect to the server
    } catch (ConnectException ex) {
      String message = ex.getMessage();
      LOGGER.warn("Unable to connect to PageSeeder: {}", message);
      response.setStatus(Status.CONNECTION_ERROR, message != null ? message : "Unable to connect");
      throw ex;
    }
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
  public void process(PSHTTPResponseInfo response, XMLWriter xml) throws IOException {
    if (this._method == Method.MULTIPART) {
      endMultipart();
    }
    try {
      // Retrieve the content of the response
      int status = this._connection.getResponseCode();
      response.setCodeAndStatus(status);

      if (isOK(status) || (this._resource.includeErrorContent() && isError(status))) {

        String mediaType = getMediaType(this._connection);
        response.setMediaType(mediaType);

        // Return content is XML try to parse it
        if (isXML(mediaType)) {
          // Parse with the XML Copy Handler
          XMLCopy handler = new XMLCopy(xml);
          handleXML(this._connection, response, handler, true);
        } else {
          response.setStatus(Status.PROCESS_ERROR, "Unable to copy non-XML media type");
        }

        // Ensure the session is updated for that user
        updateSession(this._connection);

      } else {
        LOGGER.info("PageSeeder returned {}: {}", status, this._connection.getResponseMessage());
        parseError(this._connection, response);
      }

      // Could not connect to the server
    } catch (ConnectException ex) {
      String message = ex.getMessage();
      LOGGER.warn("Unable to connect to PageSeeder: {}", message);
      response.setStatus(Status.CONNECTION_ERROR, message != null ? message : "Unable to connect");
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
  public PSHTTPResponseInfo process(PSHTTPResponseInfo response, XMLWriter xml, Templates templates)
      throws IOException {
    return process(response, xml, templates, null);
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
  public PSHTTPResponseInfo process(PSHTTPResponseInfo response, XMLWriter xml, Templates templates, Map<String, String> parameters)
      throws IOException {
    if (this._method == Method.MULTIPART) {
      endMultipart();
    }
    try {
      // Retrieve the content of the response
      int status = this._connection.getResponseCode();
      response.setCodeAndStatus(status);

      if (isOK(status) || (this._resource.includeErrorContent() && isError(status))) {

        String mediaType = getMediaType(this._connection);
        response.setMediaType(mediaType);

        // Return content is XML try to parse it
        if (isXML(mediaType)) {
          transformXML(this._connection, response, xml, templates, parameters);
        } else {
          response.setStatus(Status.PROCESS_ERROR, "Unable to copy non-XML media type");
        }

        // Ensure the session is updated for that user
        updateSession(this._connection);

      } else {
        LOGGER.info("PageSeeder returned {}: {}", status, this._connection.getResponseMessage());
        parseError(this._connection, response);
      }

      // Could not connect to the server
    } catch (ConnectException ex) {
      String message = ex.getMessage();
      LOGGER.warn("Unable to connect to PageSeeder: {}", message);
      response.setStatus(Status.CONNECTION_ERROR, message != null ? message : "Unable to connect");
      throw ex;
    }

    // Return the final status
    return response;
  }

  // Static methods
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
  private static boolean isXML(String contentType) {
    if (contentType == null) return false;
    return "text/xml".equals(contentType)
        || "application/xml".equals(contentType)
        || contentType.endsWith("+xml");
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
   * @param resource    The resource to connect to.
   * @param type        The type of connection.
   * @param credentials The user login to use (optional).
   *
   * @return A newly opened connection to the specified URL
   * @throws IOException Should an exception be returns while opening the connection
   */
  protected static PSHTTPConnection connect(PSHTTPResource resource, Method type, PSCredentials credentials)
      throws IOException {
    URL url = resource.toURL(credentials, type == Method.POST ? false : true);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoOutput(true);
    connection.setInstanceFollowRedirects(true);
    connection.setRequestMethod(type == Method.MULTIPART ? "POST" : type.name());
    connection.setDefaultUseCaches(false);
    connection.setRequestProperty("X-Requester", "PS-Bridge-" + API_VERSION);
    if (credentials instanceof UsernamePassword) {
      // Use Basic Auth (5.6+)
      connection.addRequestProperty("Authorization", ((UsernamePassword) credentials).toBasicAuthorization());
    }

    PSSession session = credentials instanceof PSSession ? (PSSession) credentials : null;
    PSHTTPConnection instance = null;

    // POST using "application/x-www-form-urlencoded"
    if (type == Method.POST) {
      String parameters = resource.getPOSTFormURLEncodedContent();
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
      connection.setRequestProperty("Content-Length", Integer.toString(parameters.length()));
      connection.setDoInput(true);
      writePOSTData(connection, parameters);
      instance = new PSHTTPConnection(connection, resource, type, session, null);

      // POST using "multipart/form-data"
    } else if (type == Method.MULTIPART) {
      String boundary = "--------------------" + Long.toString(Math.abs(random.nextLong()), 36);
      connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
      connection.setDoInput(true);
      instance = new PSHTTPConnection(connection, resource, type, session, "--" + boundary);
      Map<String, String> parameters = resource.parameters();
      if (!parameters.isEmpty()) {
        for (Entry<String, String> p : parameters.entrySet()) {
          instance.addParameterPart(p.getKey(), p.getValue());
        }
      }

      // GET, PUT and DELETE
    } else {
      instance = new PSHTTPConnection(connection, resource, type, session, null);
    }

    return instance;
  }

  /**
   * Write the POST content.
   *
   * @param connection The URL connection
   * @param data       The data to write
   *
   * @throws IOException Should any error occur while writing.
   */
  private static void writePOSTData(HttpURLConnection connection, String data) throws IOException {
    OutputStream post = null;
    try {
      post = connection.getOutputStream();
      post.write(data.getBytes(UTF8));
      post.flush();
    } catch (IOException ex) {
      LOGGER.error("An error occurred while writing POST data", ex);
      closeQuietly(post);
      throw ex;
    }
  }

  // Processors
  // ----------------------------------------------------------------------------------------------

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
  private static void handleXML(HttpURLConnection connection, PSHTTPResponseInfo response, DefaultHandler handler, boolean duplex)
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
  private static boolean transformXML(HttpURLConnection connection, PSHTTPResponseInfo response, XMLWriter xml,
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
    BufferedInputStream in = null;
    boolean ok = true;

    try {
      InputStream raw = isOK(connection.getResponseCode()) ? connection.getInputStream() : connection.getErrorStream();
      in = new BufferedInputStream(raw);
      copy(in, out);

    } catch (IOException ex) {
      LOGGER.warn("Error while parsing text data from URL", ex);
      ok = false;
    } finally {
      closeQuietly(in);
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

  /**
   * Adds the attributes for when error occurs
   *
   * @param connection The PS Connection.
   * @param info       The response info.
   *
   * @throws IOException If thrown while writing the XML.
   */
  private static void parseError(HttpURLConnection connection, PSHTTPResponseInfo info) throws IOException {
    InputStream err = null;
    try {
      err = getErrorStream(connection);

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
    } finally {
      closeQuietly(err);
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
      try {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        copy(tmp, buffer);
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
   * If data is <code>null</code> the string content is copied to the output stream as UTF-8.
   *
   * @param data   The data to write
   * @param output The output
   *
   * @throws IOException Any error reported while writing on the output
   */
  private static void write(String data, OutputStream output) throws IOException {
    if (data != null) {
      output.write(data.getBytes(UTF8));
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
    int n = 0;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
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
    private final PSHTTPResponseInfo response;

    /**
     * In duplex mode, both
     */
    private boolean duplex = false;

    /**
     * @param reader   The XML currently processing
     * @param response The response metadata
     * @param handler  THe handler to use unless an error is detected.
     */
    public HandlerDispatcher(XMLReader reader, PSHTTPResponseInfo response, DefaultHandler handler) {
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
    private PSHTTPResponseInfo response;

    /** State: Within "message" element. */
    private boolean isMessage = false;

    public PSErrorHandler(PSHTTPResponseInfo response) {
      this.response = response;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (ERROR_ELEMENT.equals(localName) || ERROR_ELEMENT.equals(qName)) {
        this.response.setErrorID(attributes.getValue("id"));
      } else if (MESSAGE_ELEMENT.equals(localName) || MESSAGE_ELEMENT.equals(qName)) {
        this.isMessage = true;
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      if (MESSAGE_ELEMENT.equals(localName) || MESSAGE_ELEMENT.equals(qName)) {
        this.isMessage = false;
        this.response.setMessage(this.message.toString());
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
    public static void parseError(InputSource source, PSHTTPResponseInfo response) throws IOException {
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
