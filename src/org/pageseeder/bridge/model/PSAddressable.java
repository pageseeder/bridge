/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSEntity;

/**
 * @author Christophe Lauret
 * @version 0.1.0
 */
public abstract class PSAddressable implements Addressable, PSEntity {

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
  public static final int DEFAULT_PORT = 8080;

  /**
   * A Pattern to decompose a URL into components.
   *
   * The groups are:
   *  1. Scheme
   *  2. Host
   *  3. Port
   *  4. Path
   */
  private static final Pattern URL_DECOMPOSER = Pattern.compile("^(?:(https?):)?(?://([\\da-z\\.-]+)(?:\\:(\\d{1,4}))?)?(/[^\\?]*)?$");

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
  protected PSAddressable(String url) {
    Matcher m = URL_DECOMPOSER.matcher(url);
    PSConfig p = PSConfig.getDefault();
    if (m.matches()) {
      String scheme = m.group(1);
      String host = m.group(2);
      String port = m.group(3);
      String path = m.group(4);
      setScheme(scheme != null? scheme : p.getScheme());
      setHost(scheme != null? host : p.getHost());
      setPort(port != null? Integer.parseInt(port) : p.getPort());
      setPath(path);
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
  public PSAddressable(String scheme, String host, int port, String path) {
    // We use the methods to ensure that the values are correctly checked
    setScheme(scheme);
    setHost(host);
    setPort(port);
    setPath(path);
  }

  /**
   * The scheme.
   */
  private String scheme;

  /**
   * The host.
   */
  private String host;

  /**
   * The host.
   */
  private int port;

  /**
   * The path.
   */
  private String path;

  /**
   * The URL corresponding to the other attributes.
   */
  private transient String url = null;

  @Override
  public final String getScheme() {
    return this.scheme;
  }

  @Override
  public final String getHost() {
    return this.host;
  }

  @Override
  public final int getPort() {
    return this.port;
  }

  @Override
  public final String getPath() {
    return this.path;
  }

  @Override
  public final String getHostURL() {
    StringBuilder url = new StringBuilder();
    if (this.scheme != null) {
      url.append(this.scheme).append(':');
    }
    url.append("//");
    if (this.host != null) {
      url.append(this.host);
    }
    if (this.port > 0) {
      url.append(':').append(this.port);
    }
    return url.toString();
  }

  @Override
  public final String getURL() {
    if (this.url == null) {
      this.url = toURL();
    }
    return this.url;
  }

  /**
   * @param scheme the scheme to set
   */
  public final void setScheme(String scheme) {
    this.scheme = scheme;
    this.url = null;
  }

  /**
   * @param host the host to set
   */
  public final void setHost(String host) {
    this.host = host;
    this.url = null;
  }

  /**
   * @param port the port to set
   *
   * @throws IllegalArgumentException If the port number is too large
   */
  public final void setPort(int port) {
    if (port > MAX_PORT_NUMBER) throw new IllegalArgumentException("Port too large (> 65535)");
    this.port = port;
    this.url = null;
  }

  /**
   * @param path the path to set
   */
  public final void setPath(String path) {
    if (path != null && !path.startsWith("/"))
      throw new IllegalArgumentException("Path must start with '/'");
    this.path = path;
    this.url = null;
  }

  /**
   * @return Recomputes the URL from the attributes in this class.
   */
  public final String toURL() {
    StringBuilder url = new StringBuilder();
    if (this.scheme != null) {
      url.append(this.scheme).append(':');
    }
    url.append("//");
    if (this.host != null) {
      url.append(this.host);
    }
    if (this.port > 0) {
      url.append(':').append(this.port);
    }
    if (this.path != null) {
      url.append(this.path);
    }
    return url.toString();
  }

}
