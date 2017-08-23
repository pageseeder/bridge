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
package org.pageseeder.bridge.core;

import org.pageseeder.bridge.PSConfig;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Base class for objects which can have a URL.
 *
 * @author Christophe Lauret
 * @version 0.12.0
 */
public abstract class Addressable implements Serializable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * The maximum valid port number.
   */
  public static final int MAX_PORT_NUMBER = 65535;

  /**
   * The default scheme to use.
   */
  public static final String DEFAULT_SCHEME = "http";

  /**
   * The default host.
   */
  public static final String DEFAULT_HOST = "localhost";

  /**
   * The default port.
   */
  public static final int DEFAULT_PORT = 80;

  /**
   * A Pattern to decompose a URL into components.
   *
   * The groups are:
   *  1. Scheme
   *  2. Host
   *  3. Port
   *  4. Path
   */
  private static final Pattern URL_DECOMPOSER = Pattern.compile("^(?:(https?):)?(?://([\\da-z.-]+)(?::(\\d{1,4}))?)?(/[^?]*)?$");

  /**
   * The scheme.
   */
  private final String _scheme;

  /**
   * The host.
   */
  private final String _host;

  /**
   * The host.
   */
  private final int _port;

  /**
   * The path.
   */
  private final String _path;

  /**
   * Instantiate a new addressable object from the specified url.
   *
   * <p>The URL may omit the scheme or authority part, it which case it will default
   * on the default values from the configuration.
   *
   * @param url The url.
   *
   * @throws IllegalArgumentException If the specified URL is invalid
   */
  Addressable(String url) {
    Matcher m = URL_DECOMPOSER.matcher(url);
    PSConfig p = PSConfig.getDefault();
    if (m.matches()) {
      String scheme = m.group(1);
      String host = m.group(2);
      String port = m.group(3);
      String path = m.group(4);
      this._scheme = scheme != null? scheme : p.getScheme();
      this._host = host != null? host : p.getHost();
      this._port = port != null? Integer.parseInt(port) : p.getPort();
      this._path = path != null? path : "";
    } else throw new IllegalArgumentException("Invalid url");
  }

  /**
   * Instantiate a new addressable object from the specified scheme, host, port and path.
   *
   * @param scheme The scheme "http" or "https"
   * @param host   Where the resource is hosted.
   * @param port   The port (or negative to use the default port).
   * @param path   The path to the resource.
   */
  public Addressable(String scheme, String host, int port, String path) {
    // We use the methods to ensure that the values are correctly checked
    this._scheme = Objects.requireNonNull(scheme, "Scheme is required");
    this._host = Objects.requireNonNull(host, "Host is required");
    this._port = Objects.requireNonNull(port, "Port is required");
    this._path = Objects.requireNonNull(path, "Path is required");
  }

  public final String getScheme() {
    return this._scheme;
  }

  public final String getHost() {
    return this._host;
  }

  public final int getPort() {
    return this._port;
  }

  public final String getPath() {
    return this._path;
  }

  public final String getDecodedPath() {
    return Arrays.stream(this._path.split("/")).map(Addressable::decode).collect(Collectors.joining("/"));
  }

  private static String decode(String step) {
    try {
      return URLDecoder.decode(step, "utf-8");
    } catch (UnsupportedEncodingException ex) {
      // Will not occur as "utf-8" must be supported according to Java spec
      throw new RuntimeException(ex);
    }
  }

  public final String getHostURL() {
    StringBuilder url = new StringBuilder();
    url.append(this._scheme).append(':');
    url.append("//");
    url.append(this._host);
    if (this._port > 0) {
      url.append(':').append(this._port);
    }
    return url.toString();
  }

  public final String getURL() {
    return toURL();
  }

  /**
   * @return Recomputes the URL from the attributes in this class.
   */
  public final String toURL() {
    StringBuilder url = new StringBuilder();
    if (this._scheme != null) {
      url.append(this._scheme).append(':');
    }
    url.append("//");
    if (this._host != null) {
      url.append(this._host);
    }
    if (this._port > 0) {
      url.append(':').append(this._port);
    }
    if (this._path != null) {
      url.append(this._path);
    }
    return url.toString();
  }

  static class Builder {
    private String scheme = DEFAULT_SCHEME;
    private String host = DEFAULT_HOST;
    private int port = DEFAULT_PORT;
    private String path = "";

    public String scheme() {
      return this.scheme;
    }

    public String host() {
      return this.host;
    }

    public int port() {
      return this.port;
    }

    public String path() {
      return this.path;
    }

    public Builder scheme(String scheme) {
      this.scheme = scheme;
      return this;
    }

    public Builder host(String host) {
      this.host = host;
      return this;
    }

    public Builder port(int port) {
      this.port = port;
      return this;
    }

    public Builder path(String path) {
      this.path = path;
      return this;
    }

//    /**
//     * @param filename the filename to set
//     */
//    public Builder filename(String filename) {
//      String path = this.path();
//      if (path.length() == 0) {
//        this.path = "/"+filename;
//      } else {
//        this.path = getFolder(path)+'/'+filename;
//      }
//      return this;
//    }

  }

}
