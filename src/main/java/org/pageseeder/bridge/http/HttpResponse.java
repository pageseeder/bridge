/*
 * Copyright 2017 Allette Systems (Australia)
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

import org.jspecify.annotations.Nullable;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.xml.Handler;
import org.pageseeder.bridge.xml.stax.XMLStreamHandler;
import org.pageseeder.xmlwriter.XMLWriter;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.transform.Templates;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 *
 * @version 0.11.0
 * @since 0.11.0
 */
public interface HttpResponse extends AutoCloseable {

  /**
   * Return the response status code from this request.
   *
   * @return the status code from this request.
   */
  int code();

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
   * @return the etag of the response derived from the "Etag" response header.
   */
  @Nullable String etag();

  /**
   * Returns the value of "Content-Length" response header.
   *
   * <pre><code>Content-Length: 49</code></pre>
   *
   * @return the value of "Content-Length" response header or -1 if the content length is not known.
   */
  long length();

  /**
   * Returns the value of "Date" response header.
   *
   * <pre><code>Date: Tue, 21 Jun 2016 05:10:06 GMT</code></pre>
   *
   * @return the value of "Date" response header or 0 if the date is not known.
   */
  long date();

  /**
   * Returns the value of "Expires" response header.
   *
   * <pre><code>Expires: Wed, 21 Jun 2017 05:10:06 GMT</code></pre>
   *
   * @return the value of "Expires" response header or 0 if not known.
   */
  long expires();

  /**
   * Returns the value of the specified header.
   *
   * @param name The name of the header (case insensitive)
   *
   * @return The corresponding value or <code>null</code>;
   */
  @Nullable String header(String name);

  /**
   * Returns the list of headers
   *
   * @return The corresponding value or <code>null</code>;
   */
  List<Header> headers();

  /**
   * Return the reason string.
   *
   * @return the reason string.
   */
  @Nullable String message();

  /**
   * Returns the value of "Last-Modified" response header.
   *
   * <pre><code>Date: Tue, 21 Jun 2016 05:10:06 GMT</code></pre>
   *
   * @return the value of "Date" response header or 0 if the date is not known.
   */
  long modified();

  /**
   * Returns the value of "Content-Type" response header.
   *
   * <pre><code>Content-Type: application/xml;charset=utf-8</code></pre>
   *
   * @return the value of "Content-Type" response header.
   */
  @Nullable String getContentType();

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
  @Nullable String mediaType();

  /**
   * Returns the character set used for character-based content and derived
   * from the "Content-Type" response header.
   *
   * <p>NB. The HTTP header "Content-Encoding" has nothing to do with character
   * encoding, do not use it for that purpose!
   *
   * @return the charset used in the response if detected and applicable.
   */
  @Nullable Charset charset();

  /**
   * Returns the session.
   *
   * @return the session if any.
   */
  @Nullable PSSession session();

  /**
   * Indicates whether the response is XML based on the content type.
   *
   * <p>It is considered XML the mediatype is equal to "text/xml" or
   * "application/xml" or ends with "+xml".
   *
   * @return <code>true</code> if XML;
   *         <code>false</code> otherwise.
   */
  boolean isXML();

  /**
   * Indicates whether the response was successful based on the HTTP code.
   *
   * <p>When this method is called BEFORE a consumer, this generally imply
   * that the content is <i>available</i>.
   *
   * @return <code>true</code> if the code is between 200 and 299 (included);
   *         <code>false</code>.
   */
  boolean isSuccessful();

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
  boolean isAvailable();

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
  @Nullable InputStream getInputStream() throws IOException;

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
  @Nullable Reader getReader() throws IOException;

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
  @Nullable Reader getReader(Charset charset) throws IOException;

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
  void consumeBytes(OutputStream out);

  /**
   * Short-hand method to consume the output of the response and return a byte array.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @return the output content as a byte array.
   *
   * @throws ContentException If an error occurred while consuming the content.
   */
  byte[] consumeBytes();

  /**
   * Simply consumes the output of the response.
   *
   * <p>After calling this method the response content will no longer be available.
   *
   * @throws IllegalStateException If the response is not available.
   * @throws ContentException If an error occurred while consuming the content.
   */
  void consume();

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
  void consumeChars(Writer out);

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
  String consumeString();

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
  void consumeXML(DefaultHandler handler);

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
  <T> List<T> consumeList(Handler<T> handler);

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
  <T> @Nullable T consumeItem(Handler<T> handler);


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
  <T> List<T> consumeList(XMLStreamHandler<T> handler) throws ContentException;

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
  <T> @Nullable T consumeItem(XMLStreamHandler<T> handler) throws ContentException;

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
  void consumeXML(XMLWriter xml);

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
  void consumeXML(XMLWriter xml, Templates templates);

  /**
   * Consumes the output of the response, transforms it directly with XSLT and
   * writes the results of the transformation to the XML writer.
   *
   * <p>Templates must be specified to transform the XML. The parameters are
   * transmitted to the XSLT transformer.
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
  void consumeXML(XMLWriter xml, Templates templates, Map<String, String> parameters);

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
  @Nullable ServiceError consumeServiceError();

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
  @Nullable ServiceError getServiceError();

  @Override
  void close();

}
