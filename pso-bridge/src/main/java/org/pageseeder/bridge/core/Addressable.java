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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Base class for objects which can have a URL.
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
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
   * <p>The groups are:
   *  1. Scheme
   *  2. Host
   *  3. Port
   *  4. Path
   */
  private static final Pattern URL_DECOMPOSER = Pattern.compile("^(?:(https?):)?(?://([\\da-z.-]+)(?::(\\d{1,5}))?)?(/[^?]*)?$");

  /**
   * The scheme.
   */
  private final String scheme;

  /**
   * The host.
   */
  private final String host;

  /**
   * The port.
   */
  private final int port;

  /**
   * The path.
   */
  private final String path;

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
      String portString = m.group(3);
      this.scheme = Objects.toString(m.group(1), p.getScheme());
      this.host = Objects.toString(m.group(2), p.getHost());
      this.port = portString != null ? parsePort(portString) : validatePort(p.getPort());
      this.path = Objects.toString(m.group(4), "");
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
  protected Addressable(String scheme, String host, int port, String path) {
    // We use the methods to ensure that the values are correctly checked
    this.scheme = Objects.requireNonNull(scheme, "Scheme is required");
    this.host = Objects.requireNonNull(host, "Host is required");
    this.port = validatePort(port);
    this.path = Objects.requireNonNull(path, "Path is required");
  }

  public final String getScheme() {
    return this.scheme;
  }

  public final String getHost() {
    return this.host;
  }

  public final int getPort() {
    return this.port;
  }

  public final String getPath() {
    return this.path;
  }

  public final String getDecodedPath() {
    return Arrays.stream(this.path.split("/")).map(Addressable::decode).collect(Collectors.joining("/"));
  }

  private static String decode(String step) {
    return URLDecoder.decode(step, StandardCharsets.UTF_8);
  }

  public final String getHostURL() {
    return buildURL(false);
  }

  public final String getURL() {
    return toURL();
  }

  /**
   * @return Recomputes the URL from the attributes in this class.
   */
  public final String toURL() {
    return buildURL(true);
  }

  private String buildURL(boolean includePath) {
    StringBuilder url = new StringBuilder();
    url.append(this.scheme).append(':');
    url.append("//");
    url.append(this.host);
    if (this.port > 0) {
      url.append(':').append(this.port);
    }
    if (includePath) {
      url.append(this.path);
    }
    return url.toString();
  }

  private static int parsePort(String port) {
    return validatePort(Integer.parseInt(port));
  }

  private static int validatePort(int port) {
    if (port > MAX_PORT_NUMBER) {
      throw new IllegalArgumentException("Invalid port: " + port);
    }
    return port;
  }

  static class Builder<B extends Builder<B>> {
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

    @SuppressWarnings("unchecked")
    public B scheme(String scheme) {
      this.scheme = scheme;
      return (B)this;
    }

    @SuppressWarnings("unchecked")
    public B host(String host) {
      this.host = host;
      return (B)this;
    }

    @SuppressWarnings("unchecked")
    public B port(int port) {
      this.port = port;
      return (B)this;
    }

    @SuppressWarnings("unchecked")
    public B path(String path) {
      this.path = path;
      return (B)this;
    }
  }

}
