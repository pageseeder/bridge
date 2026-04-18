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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.xml.Handler;
import org.pageseeder.bridge.xml.stax.XMLStreamHandler;
import org.pageseeder.xmlwriter.XMLWriter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Templates;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * An HTTP response wrapping cached content
 *
 * @author Christophe Lauret
 *
 * @version 0.11.5
 * @since 0.11.4
 */
public final class CachedResponse implements HttpResponse {

  /**
   * The cached content.
   */
  private final CachedContent _content;

  public CachedResponse(CachedContent content) {
    this._content = Objects.requireNonNull(content);
  }

  @Override
  public @Nullable Charset charset() {
    String cs = this._content.charset();
    return cs != null? Charset.forName(this._content.charset()) : null;
  }

  @Override
  public int code() {
    return 200;
  }

  @Override
  public long date() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String etag() {
    return this._content.etag();
  }

  @Override
  public long expires() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getContentType() {
    String mediaType = this._content.mediaType();
    String cs = this._content.charset();
    return mediaType+(cs!=null? "charset="+cs :"");
  }

  @Override
  public InputStream getInputStream() {
    return this._content.getInputStream();
  }

  @Override
  public Reader getReader() {
    return new InputStreamReader(this._content.getInputStream(), StandardCharsets.UTF_8);
  }

  @Override
  public Reader getReader(Charset charset) {
    return new InputStreamReader(this._content.getInputStream(), charset);
  }

  @Override
  public @Nullable String header(String name) {
    switch (name) {
      case "Content-Type": return getContentType();
      case "Content-Length": return Long.toString(length());
      case "Etag": return etag();
      default: return null;
    }
  }

  @Override
  public @NonNull List<Header> headers() {
    List<Header> headers = new ArrayList<>();
    headers.add(new Header("Content-Type", getContentType()));
    headers.add(new Header("Content-Length", Long.toString(length())));
    headers.add(new Header("Etag", etag()));
    return headers;
  }

  @Override
  public boolean isAvailable() {
    return this._content.length() > 0;
  }

  @Override
  public boolean isSuccessful() {
    return this._content.length() > 0;
  }

  @Override
  public boolean isXML() {
    String mediaType = this._content.mediaType();
    return "text/xml".equals(mediaType)
        || "application/xml".equals(mediaType)
        || mediaType.endsWith("+xml");
  }

  @Override
  public long length() {
    return this._content.length();
  }

  @Override
  public @Nullable String mediaType() {
    return this._content.mediaType();
  }

  @Override
  public @Nullable String message() {
    return "OK (Cached response)";
  }

  @Override
  public long modified() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public @Nullable PSSession session() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void consume() {
    // Do nothing
  }

  @Override
  public byte[] consumeBytes() {
    return Arrays.copyOf(this._content.bytes(), this._content.length());
  }

  @Override
  public void consumeBytes(OutputStream out) throws ContentException {
    try {
      out.write(this._content.bytes());
    } catch (IOException ex) {
      throw new ContentException(ex);
    }
  }

  @Override
  public void consumeChars(Writer out)  throws ContentException {
    try (Reader r = new InputStreamReader(this._content.getInputStream(), StandardCharsets.UTF_8)) {
      copy(r, out, this._content.length());
    } catch (IOException ex) {
      throw new ContentException(ex);
    }
  }

  @Override
  public void consumeXML(XMLWriter xml) throws ContentException {
    try (Reader r = new InputStreamReader(this._content.getInputStream(), StandardCharsets.UTF_8)) {
      copy(r, xml, this._content.length());
    } catch (IOException ex) {
      throw new ContentException(ex);
    }
  }

  @Override
  public @NonNull String consumeString() throws ContentException {
    byte[] bytes = this._content.bytes();
    return new String(bytes, StandardCharsets.UTF_8);
  }

  @Override
  public void consumeXML(DefaultHandler handler) throws ContentException {
    try {
      handleXML(this._content, handler);
    } catch (IOException ex) {
      throw new ContentException("Unable to consume XML", ex);
    }
  }

  @Override
  public <T> @Nullable T consumeItem(Handler<T> handler) throws ContentException {
    consumeXML(handler);
    return handler.get();
  }

  @Override
  public <T> @NonNull List<T> consumeList(Handler<T> handler) throws ContentException {
    consumeXML(handler);
    return handler.list();
  }

  @Override
  public <T> @NonNull List<T> consumeList(XMLStreamHandler<T> handler) throws ContentException {
    try {
      return parseXMLStream(this._content, handler);
    } catch (IOException ex) {
      throw new ContentException("Unable to consume XML", ex);
    }
  }

  @Override
  public <T> @Nullable T consumeItem(XMLStreamHandler<T> handler) throws ContentException {
    List<T> list = consumeList(handler);
    return list.size() > 0? list.get(0) : null;
  }

  @Override
  public void consumeXML(XMLWriter xml, Templates templates) throws ContentException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override
  public void consumeXML(XMLWriter xml, Templates templates, Map<String, String> parameters) throws ContentException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override
  public @Nullable ServiceError getServiceError() {
    return null;
  }

  @Override
  public @Nullable ServiceError consumeServiceError() {
    return getServiceError();
  }

  @Override
  public void close() {
  }

  /**
   * Parse the response as XML.
   *
   * @param content The content from the cache
   * @param handler Handles the XML.
   *
   * @throws IllegalStateException If the response is not available.
   * @throws IOException If an error occurs while writing the XML.
   */
  private static void handleXML(CachedContent content, DefaultHandler handler)
      throws IOException {

    // Setup the factory
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);

    try (InputStream in = content.getInputStream()) {
      InputSource source = new InputSource(in);
      source.setSystemId(content.url());

      // Ensure the character encoding is correct
      String charset = content.charset();
      if (charset != null) {
        source.setEncoding(charset);
      }

      // And parse!
      SAXParser parser = factory.newSAXParser();
      parser.parse(source, handler);

    } catch (ParserConfigurationException ex) {
      throw new ContentException("Error while configuring XML parser", ex);
    } catch (SAXException ex) {
      throw new ContentException("Error while parsing XML", ex);
    }
  }

  /**
   * Parse the response as XML using a StAX stream handler.
   *
   * @param content  The cached content
   * @param handler  Handles a StAX stream
   *
   * @throws IllegalStateException If the response is not available.
   * @throws IOException If an error occurs while writing the XML.
   */
  private static <T> List<T> parseXMLStream(CachedContent content, XMLStreamHandler<T> handler) throws IOException {
    // Setup the factory
    XMLInputFactory factory = XMLInputFactory.newInstance();
    factory.setProperty(XMLInputFactory.IS_COALESCING, true);
    factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, true);
    factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

    // Get character encoding is correct
    String charset = content.charset();
    if (charset != null) {
      charset = "utf-8";
    }

    // And parse the stream
    List<T> list = new ArrayList<>();
    try (InputStream in = content.getInputStream()) {
      XMLStreamReader source = factory.createXMLStreamReader(in, charset);
      while (handler.find(source)) {
        T next = handler.get(source);
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
   * Copy the input stream to the output stream as UTF-8 using a buffer of 4096 bytes.
   *
   * @param reader The data to copy
   * @param output The output
   * @param length The expected length
   *
   * @throws IOException Any error reported while writing on the output
   */
  private static void copy(Reader reader, Writer output, int length) throws IOException {
    // NB. length is in bytes, there are more bytes than characters
    int bufferSize = length <= 0 || length > 4096 ? 4096 : length;
    char[] buffer = new char[bufferSize];
    int n;
    while (-1 != (n = reader.read(buffer))) {
      output.write(buffer, 0, n);
    }
  }

  /**
   * Copy the input stream to the output stream as UTF-8 using a buffer of 4096 bytes.
   *
   * @param reader The data to copy
   * @param xml The output
   * @param length The expected length
   *
   * @throws IOException Any error reported while writing on the output
   */
  private static void copy(Reader reader, XMLWriter xml, int length) throws IOException {
    // NB. length is in bytes, there are more bytes than characters
    int bufferSize = length <= 0 || length > 4096 ? 4096 : length;
    char[] buffer = new char[bufferSize];
    int n;
    while (-1 != (n = reader.read(buffer))) {
      xml.writeXML(buffer, 0, n);
    }
  }

}
