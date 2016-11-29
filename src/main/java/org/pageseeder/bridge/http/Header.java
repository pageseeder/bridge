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

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A HTTP header
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.9.1
 */
public final class Header {

  /**
   * The header name.
   */
  private final String _name;

  /**
   * The header value.
   *
   * The Object can be an instance of String, Long or Date.
   */
  private final Object _value;

  /**
   * Create a new parameter
   *
   * @param name  The parameter name (not URL encoded)
   * @param value The parameter value (not URL encoded)
   */
  public Header(String name, String value) {
    this._name = Objects.requireNonNull(name, "Header name must not be null");
    this._value = Objects.requireNonNull(value, "Header value must not be null");
  }

  /**
   * Create a new parameter
   *
   * @param name  The parameter name (not URL encoded)
   * @param value The parameter value (not URL encoded)
   */
  public Header(String name, long value) {
    this._name = Objects.requireNonNull(name, "Header name must not be null");
    this._value = value;
  }

  /**
   * @return The header name
   */
  public String name() {
    return this._name;
  }

  /**
   * @return The header string value
   */
  public String value() {
    if (this._value instanceof Date) return formatHTTPDate(((Date)this._value));
    return this._value.toString();
  }

  /**
   * @return The header long value
   */
  public long longValue() {
    if (this._value instanceof Long) return ((Long)this._value).longValue();
    if (this._value instanceof Date) return ((Date)this._value).getTime();
    return Long.parseLong(toString());
  }

  @Override
  public String toString() {
    return this._name+":"+value();
  }

  /**
   * Format the specified date using the HTTP-Date format.
   *
   * @param date Date to format
   *
   * @return The corresponding string.
   *
   * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">HTTP Date format</a>
   */
  public static String formatHTTPDate(Date date) {
    SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
    format.setTimeZone(TimeZone.getTimeZone("GMT"));
    return format.format(date);
  }

  /**
   * Parse the specified date using the HTTP-Date format.
   *
   * @param date Date to parse.
   *
   * @return The corresponding string.
   *
   * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">HTTP Date format</a>
   *
   * @throws ParseException If the date cannot be parsed as a HTTP date string.
   */
  public static Date parseHTTPDate(String date) throws ParseException {
    SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
    format.setTimeZone(TimeZone.getTimeZone("GMT"));
    return format.parse(date);
  }

  /**
   * Returns the type and subtype of the value of the "content-type" header.
   *
   * <p>For example, it will return "application/xml" for "application/xml; charset=utf-8".
   *
   * @param contentType The value of the "Content-Type" header.
   *
   * @return the corresponding mediatype.
   */
  public static @Nullable String toMediaType(@Nullable String contentType) {
    // Strip any content type parameter ";" if any
    if (contentType != null && contentType.indexOf(";") > 0) return contentType.substring(0, contentType.indexOf(";")).trim();
    return contentType;
  }

  /**
   * Returns the character set used based on the value of the "content-type" header.
   *
   * <p>This method will first try the charset parameeter.
   * <p>For example, it will return "iso-8859-1" for "text/html; charset=iso-8859-1".
   *
   * <p>If there is no charset parameter, but the subtype is xml, it will assume UTF-8.
   * <p>For example, it will return "utf-8" for "application/xml".
   *
   * @param contentType The value of the "Content-Type" header.
   *
   * @return the corresponding charset instance or <code>null</code>
   *
   * @throws IllegalCharsetNameException If the given charset name is illegal
   * @throws UnsupportedCharsetException If no support for the named charset is available
   *         in this instance of the Java virtual machine
   */
  public static @Nullable Charset toCharset(@Nullable String contentType) {
    if (contentType == null) return null;
    Charset charset = null;
    String lc = contentType.toLowerCase();
    int charsetParameter = lc.indexOf("charset=");
    if (charsetParameter > 0) {
      String name = lc.substring(charsetParameter).replaceAll("^charset=([a-zA-Z0-9_-]+).*", "$1");
      charset = Charset.forName(name);
    } else if (lc.indexOf("xml") > 0) {
      charset = StandardCharsets.UTF_8;
    }
    return charset;
  }

}
