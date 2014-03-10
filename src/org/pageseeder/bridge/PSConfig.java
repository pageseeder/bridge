/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge;

import java.util.Properties;

/**
 *
 * Configuration of the PageSeeder server that the API should use to connect.
 *
 * <p>Bastille can be configured to connect to a PageSeeder service using the properties are defined
 * in the <code>config-[mode].xml</code> for the key {@value BASTILLE_PAGESEEDER}.
 *
 * <p>To retrieve the PageSeeder server properties, use the {@link #getProperties()}.
 *
 * <p>The properties can contain the following keys:
 * <ul>
 *   <li><code>scheme</code>: the scheme of the PageSeeder server (for example "http")</li>
 *   <li><code>host</code>: the host of the PageSeeder server (for example "localhost")</li>
 *   <li><code>port</code>: the port of the PageSeeder server (for example "8080")</li>
 *   <li><code>siteprefix</code>: the site prefix for PageSeeder server (for example "/ps")</li>
 *   <li><code>servletprefix</code>: the servlet prefix for  the PageSeeder server (for example "/ps/servlet")</li>
 * </ul>
 *
 * <p>For example:
 * <pre>
 *   Properties pageseeder = PSConfiguration.getProperties();
 *   String host = pageseeder.getProperty("host");
 *   String port = pageseeder.getProperty("port", 8080);
 *   ...
 * </pre>
 *
 * <p>The Bastille-PageSeeder properties can also be accessed directly using the Berlioz global
 * settings.
 *
 * <p>For example: <code>GlobalSettings.get("bastille.pageseeder.host")</code>
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public class PSConfig {

  private volatile static PSConfig singleton = null;

  private final Properties config;

  private final String scheme;

  private final String apiScheme;

  private final String host;

  private final String apiHost;

  private final int port;

  private final int apiPort;

  private final String sitePrefix;

  private final String servletPrefix;

  private PSConfig(Properties p) {
    this.config = p;
    this.scheme = this.config.getProperty("urischeme", "http");
    this.apiScheme = this.config.getProperty("scheme", "http");
    this.host = this.config.getProperty("urihost", "localhost");
    this.apiHost = this.config.getProperty("host", "localhost");
    this.port = Integer.parseInt(this.config.getProperty("uriport", "8080"));
    this.apiPort = Integer.parseInt(this.config.getProperty("port", "8282"));
    this.sitePrefix = this.config.getProperty("siteprefix", "/ps");
    this.servletPrefix = this.config.getProperty("servletprefix", "/ps/servlet");
  }

  public String getScheme() {
    return this.scheme;
  }

  public String getHost() {
    return this.host;
  }

  public int getPort() {
    return this.port;
  }

  public int getApiPort() {
    return this.apiPort;
  }

  public String getSitePrefix() {
    return this.sitePrefix;
  }

  public String getServletPrefix() {
    return this.servletPrefix;
  }

  /**
   * @return the API URL as a string builder.
   */
  public StringBuilder buildAPIURL() {
    StringBuilder url = new StringBuilder();
    url.append(this.apiScheme).append("://");
    url.append(this.apiHost);
    if (!isDefaultPort(this.scheme, this.apiPort)) {
      url.append(":").append(this.apiPort);
    }
    return url;
  }

  /**
   * @return the host URL as a string builder.
   */
  public StringBuilder buildHostURL() {
    StringBuilder url = new StringBuilder();
    url.append(this.scheme).append("://");
    url.append(this.host);
    if (!isDefaultPort(this.scheme, this.port)) {
      url.append(":").append(this.port);
    }
    return url;
  }

  /**
   * @return the host URL.
   */
  public String getHostURL() {
    return buildHostURL().toString();
  }

  public static PSConfig singleton() {
// FIXME    if (singleton == null) singleton = new PSConfig(PSConfiguration.getProperties());
    return singleton;
  }

  private static boolean isDefaultPort(String scheme, int port) {
    return "http".equals(scheme) && port == 80
        || "https".equals(scheme) && port == 443;
  }

}
