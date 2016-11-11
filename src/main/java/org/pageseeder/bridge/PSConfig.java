/*
 * Copyright 2015 Allette Systems (Australia)
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
package org.pageseeder.bridge;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import java.util.ServiceLoader;

import org.pageseeder.bridge.spi.ConfigProvider;

/**
 * Configuration of the PageSeeder server that the API should use to connect.
 *
 * @author Christophe Lauret
 *
 * @version 0.9.6
 * @since 0.1.0
 */
public final class PSConfig {

  /** The default URI for user access. */
  protected static final URI DEFAULT_WEBSITE = URI.create("http://localhost:8080");

  /** The default URI for API access. */
  protected static final  URI DEFAULT_API = URI.create("http://localhost:8282");

  /** The default PageSeeder site prefix. */
  protected static final String DEFAULT_PREFIX = "/ps";

  /** Default config instance. */
  private static volatile PSConfig singleton = null;

  /**
   * The base URL for the API.
   */
  private final URL _api;

  /**
   * The base URL for the server and URIs.
   */
  private final URL _uri;

  /**
   * Prefix of the PageSeeder Web application (usually "/ps")
   */
  private final String _sitePrefix;

  /**
   * The PageSeeder version for this configuration.
   */
  private volatile Version version;

  /**
   * The PageSeeder version to use for Services.
   */
  private Version serviceUse;

  /**
   * Whether to use the strict mode for service requests.
   */
  private boolean serviceStrict;

  // Constructors
  // ---------------------------------------------------------------------------------------------

  /**
   * Create a new configuration.
   *
   * @param uri    The base URI for PageSeeder user access and URIs
   * @param api    The base URI for PageSeeder API access
   * @param prefix The prefix of the PageSeeder application.
   */
  private PSConfig(URL uri, URL api, String prefix) {
    this._uri = uri;
    this._api = api;
    this._sitePrefix = prefix;
  }

  // Getters
  // ---------------------------------------------------------------------------------------------

  /**
   * The scheme for the Website and documents.
   *
   * @return the site prefix used by PageSeeder.
   */
  public String getScheme() {
    return this._uri.getProtocol();
  }

  /**
   * The hostname for the Website and documents.
   *
   * @return the site prefix used by PageSeeder.
   */
  public String getHost() {
    return this._uri.getHost();
  }

  /**
   * The port used for the Website and documents.
   *
   * @return the site prefix used by PageSeeder.
   */
  public int getPort() {
    return getActualPort(this._uri);
  }

  /**
   * The scheme for the API.
   *
   * @return the site prefix used by PageSeeder.
   */
  public String getAPIScheme() {
    return this._api.getProtocol();
  }

  /**
   * The hostname for the API.
   *
   * @return the site prefix used by PageSeeder.
   */
  public String getAPIHost() {
    return this._api.getHost();
  }

  /**
   * The port used to communicate with the API.
   *
   * @return the site prefix used by PageSeeder.
   */
  public int getAPIPort() {
    return getActualPort(this._api);
  }

  /**
   * The site prefix.
   *
   * <p>Default is <code>/ps</code>.
   *
   * @return the site prefix used by PageSeeder.
   */
  public String getSitePrefix() {
    return this._sitePrefix;
  }

  /**
   * Return the PageSeeder version for the default config.
   *
   * Implementation note: the version is lazily loaded and stored
   * on this object once it has been retrieved.
   */
  public synchronized Version getVersion() {
    if (this.version == null) {
      this.version = Version.getVersion(this);
    }
    return this.version;
  }

  /**
   * Return which API version should be requested for the services
   *
   * @return which API version should be requested for the services
   *       or <code>null</code> to use the current PageSeeder version.
   */
  public Version getServiceAPIVersion() {
    return this.serviceUse;
  }

  /**
   * Indicates whether the PageSeeder service API runs in strict mode,
   * that is make service requests fails for deprecated services or services
   * which have been introduced after the requested version.
   *
   * @return <code>true</code> when strict mode is enabled;
   *         <code>false</code> otherwise.
   */
  public boolean getServiceAPIStrict() {
    return this.serviceStrict;
  }

  /**
   * Return which API version should be requested for the services
   *
   * <p>Updating this method will affect all request using this config and
   * will cause request to services to include the <code>v</code> parameter
   * with a value starting with the version.
   *
   * @param version the API version should be requested for the services
   *         or <code>null</code> to use the current PageSeeder version.
   */
  public void setServiceAPIVersion(Version version) {
    this.serviceUse = version;
  }

  /**
   * Sets whether the PageSeeder service API runs in strict mode,
   * that is make service requests fails for deprecated services or services
   * which have been introduced after the requested version.
   *
   * <p>Updating this method will affect all request using this config and
   * will cause request to services to include the <code>v</code> parameter
   * with a value ending in <code>;strict</code>
   *
   * @param strict <code>true</code> when strict mode is enabled;
   *               <code>false</code> otherwise.
   */
  public void setServiceAPIStrict(boolean strict) {
    this.serviceStrict = strict;
  }


  // Builders
  // ---------------------------------------------------------------------------------------------

  /**
   * @return the base URL to use to connect.
   */
  public URL getAPIBaseURL() {
    return this._api;
  }

  /**
   * @return the default base URL for documents.
   */
  public URL getDocumentBaseURL() {
    return this._uri;
  }

  /**
   * @return the default base URL for the website.
   */
  public URL getWebsiteBaseURL() {
    return this._uri;
  }

  /**
   * @return the API URL as a string builder.
   */
  public StringBuilder buildAPIURL() {
    return toURLBuilder(this._api);
  }

  /**
   * @return the host URL as a string builder.
   */
  public StringBuilder buildHostURL() {
    return toURLBuilder(this._uri);
  }

  /**
   * @return the host URL.
   */
  public String getHostURL() {
    return buildHostURL().toString();
  }

  // Configuration
  // ---------------------------------------------------------------------------------------------

  /**
   * To configure the bridge manually.
   *
   * @param p The properties to use to configure the bridge manually.
   */
  public static void setDefault(PSConfig p) {
    singleton = p;
  }

  /**
   * Returns the default configuration.
   *
   * <p>This method will attempt to use the SPI for providing a configuration.
   *
   * @return the default configuration.
   */
  public static PSConfig getDefault() {
    if (singleton == null) {
      singleton = loadFromService();
      if (singleton == null) throw new IllegalStateException("PSConfig is not configured");
    }
    return singleton;
  }

  // Factory methods
  // ----------------------------------------------------------------------------------------------

  /**
   * For use by service providers to create a new PSConfig instance.
   *
   * @param p The set of properties to parse
   *
   * @return The corresponding configuration instance.
   *
   * @throws IllegalArgumentException If any or the properties yield to an malformed URL
   */
  public static PSConfig newInstance(Properties p) {
    String prefix = p.getProperty("siteprefix", DEFAULT_PREFIX);
    try {
      // Compute URL
      URL uri = toBaseURL(p, "uri", DEFAULT_WEBSITE);
      URL api = toBaseURL(p, "api", DEFAULT_API);
      PSConfig config = new PSConfig(uri, api, prefix);
      if (p.containsKey("api-version")) {
        config.setServiceAPIVersion(Version.parse(p.getProperty("api-version")));
      }
      if ("true".equals(p.getProperty("api-strict"))) {
        config.setServiceAPIStrict(true);
      }
      return config;
    } catch (MalformedURLException | IllegalArgumentException ex) {
      throw new IllegalArgumentException("PageSeeder properties are not configured properly", ex);
    }
  }

  /**
   * For use by service providers to create a new PSConfig instance.
   *
   * @param uri The URL to use as the base URI for PageSeeder URIs
   * @param api The URL to use as the base URI for API access
   *
   * @return The corresponding configuration instance.
   *
   * @throws IllegalArgumentException If any or the properties yield to an malformed URL
   */
  public static PSConfig newInstance(String uri, String api) {
    try {
      // Compute URL
      URL u = toBaseURL(uri);
      URL a = toBaseURL(api);
      return new PSConfig(u, a, DEFAULT_PREFIX);
    } catch (MalformedURLException ex) {
      throw new IllegalArgumentException("PageSeeder configuration URLs are not configured properly");
    }
  }

  /**
   * For use by service providers to create a new PSConfig instance.
   *
   * @param url The URL to use for both documents and the API.
   *
   * @return The corresponding configuration instance.
   *
   * @throws IllegalArgumentException If any or the properties yield to an malformed URL
   */
  public static PSConfig newInstance(String url) {
    return newInstance(url, url);
  }

  // private helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * Compute the base URL
   *
   * @param p
   * @param start
   * @param fallback
   * @return the URL
   * @throws MalformedURLException
   */
  private static URL toBaseURL(Properties p, String start, URI fallback) throws MalformedURLException {
    if (p.containsKey(start+"-url")) return toBaseURL(p.getProperty(start+"-url"));
    if (p.containsKey("url")) return toBaseURL(p.getProperty("url"));
    return fallback.toURL();
  }

  /**
   * Compute the base URL from the specified string
   *
   * @param url The URL
   *
   * @return the URL
   *
   * @throws MalformedURLException
   */
  private static URL toBaseURL(String url) throws MalformedURLException {
    return URI.create(url).resolve("/").toURL();
  }

  /**
   * Load the configuration instance to providers using a service loaders.
   *
   * @return the configuration from the SPI
   */
  private static PSConfig loadFromService() {
    ServiceLoader<ConfigProvider> services = ServiceLoader.load(ConfigProvider.class);
    PSConfig config = null;
    for (ConfigProvider provider : services) {
      if (config != null)
        throw new IllegalStateException("Multiple providers for the configuration, configure manually");
      config = provider.getConfig();
    }
    return config;
  }

  /**
   * Generate a string builder from the specified URL
   *
   * @param url The URL to construct the builder.
   *
   * @return A string builder using the url as a base.
   */
  private static StringBuilder toURLBuilder(URL url) {
    StringBuilder s = new StringBuilder();
    s.append(url.getProtocol()).append("://");
    s.append(url.getHost());
    int port = url.getPort();
    if (port != -1 && port != url.getDefaultPort()) {
      s.append(":").append(port);
    }
    return s;
  }

  /**
   * Returns the port actually in use by the URL so that if the URL is configured with the default
   * port it does not return -1.
   *
   * @param url The URL
   * @return the actual port.
   */
  private static int getActualPort(URL url) {
    int port = url.getPort();
    return port != -1? port : url.getDefaultPort();
  }

}
