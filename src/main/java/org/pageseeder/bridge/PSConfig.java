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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
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

  /**
   * The default URI for Website access.
   */
  protected static final URI DEFAULT_WEBSITE = URI.create("http://localhost:8080");

  /**
   * The default port for PageSeeder documents.
   */
  public static final int DEFAULT_DOCUMENT_PORT = 80;

  /**
   * The default PageSeeder site prefix.
   */
  public static final String DEFAULT_PREFIX = "/ps";

  /**
   * Default config instance.
   */
  private static volatile @Nullable PSConfig singleton = null;

  /**
   * The base URL for the publicly available Website.
   */
  private final URL _website;

  /**
   * The base URL for the API.
   */
  private final URL _api;

  /**
   * The base URL for documents.
   */
  private final URL _document;

  /**
   * Prefix of the PageSeeder Web application (usually "/ps")
   */
  private final String _sitePrefix;

  /**
   * The PageSeeder version for this configuration.
   */
  private volatile @Nullable Version version;

  /**
   * The PageSeeder version to use for Services.
   */
  private @Nullable Version serviceUse;

  /**
   * Whether to use the strict mode for service requests.
   */
  private boolean serviceStrict;

  // Constructors
  // ---------------------------------------------------------------------------------------------

  /**
   * Create a new configuration.
   *
   * @param website  The base URI for the PageSeeder Website
   * @param api      The base URI for PageSeeder API access
   * @param document The base URI for PageSeeder documents
   * @param prefix   The prefix of the PageSeeder application.
   */
  private PSConfig(URL website, URL api, URL document, String prefix) {
    this._website = website;
    this._api = api;
    this._document = document;
    this._sitePrefix = prefix;
  }

  // Getters
  // ---------------------------------------------------------------------------------------------

  /**
   * Returns the base URL to use to access the PageSeeder Website.
   *
   * <p>This is the URL that end-users would know in order to access the PageSeeder user interface.
   *
   * <p>For example "https://example.localhost"
   *
   * @return the default base URL for the Website.
   */
  public URL getWebsiteBaseURL() {
    return this._website;
  }

  /**
   * Returns the base URL to use for the PageSeeder API.
   *
   * <p>For example "http://example.localhost:8282"
   *
   * @return the base URL to use for the API.
   */
  public URL getAPIBaseURL() {
    return this._api;
  }

  /**
   * Returns the base URL to use to access raw PageSeeder documents.
   *
   * <p>It normally corresponds to the default host of PageSeeder documents.
   *
   * <p>For example "http://example.localhost"
   *
   * @return The default host URL for PageSeeder documents
   */
  public URL getDocumentBaseURL() {
    return this._document;
  }

  /**
   * The scheme for the Website.
   *
   * @return the site prefix used by PageSeeder.
   */
  public String getScheme() {
    return this._website.getProtocol();
  }

  /**
   * The hostname for the Website.
   *
   * @return the site prefix used by PageSeeder.
   */
  public String getHost() {
    return this._website.getHost();
  }

  /**
   * The port used for the Website.
   *
   * @return the site prefix used by PageSeeder.
   */
  public int getPort() {
    return getActualPort(this._website);
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
   * The scheme for PageSeeder documents.
   *
   * @return the site prefix used by PageSeeder.
   */
  public String getDocumentScheme() {
    return this._document.getProtocol();
  }

  /**
   * The hostname for PageSeeder documents.
   *
   * @return the site prefix used by PageSeeder.
   */
  public String getDocumentHost() {
    return this._document.getHost();
  }

  /**
   * The port used to access PageSeeder documents.
   *
   * @return the site prefix used by PageSeeder.
   */
  public int getDocumentPort() {
    return getActualPort(this._document);
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
  public synchronized @Nullable Version getVersion() {
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
  public @Nullable Version getServiceAPIVersion() {
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
   * Returns a builder to constructs a URL to access the Website.
   *
   * <p>The returned value has the form <code>[scheme]://[host]:[port]</code>; or
   * <code>[scheme]://[host]</code> if using the default port for the protocol.
   *
   * <p>The returned value is normalized so that it does not include the default
   * port or any trailing '/'. It does not include the site prefix.
   *
   * @return the host URL as a string builder.
   */
  public StringBuilder getWebsiteURLBuilder() {
    return toURLBuilder(this._website);
  }

  /**
   * Returns a builder to constructs a URL to access the API.
   *
   * <p>The returned value has the form <code>[scheme]://[host]:[port]</code>; or
   * <code>[scheme]://[host]</code> if using the default port for the protocol.
   *
   * <p>The returned value is normalized so that it does not include the default
   * port or any trailing '/'. It does not include the site prefix.
   *
   * @return the API URL as a string builder.
   */
  public StringBuilder getAPIURLBuilder() {
    return toURLBuilder(this._api);
  }

  /**
   * Returns a builder to constructs a URL to access documents.
   *
   * <p>The returned value has the form <code>[scheme]://[host]:[port]</code>; or
   * <code>[scheme]://[host]</code> if using the default port for the protocol.
   *
   * <p>The returned value is normalized so that it does not include the default
   * port or any trailing '/'. It does not include the site prefix.
   *
   * @return the document URL as a string builder.
   */
  public StringBuilder getDocumentURLBuilder() {
    return toURLBuilder(this._document);
  }

  /**
   * Short-hand method to build a URL to access the Website using the specified path.
   *
   * <p>It builds the URL as follows:
   * <pre>
   *   [scheme]://[host]:[port?][site_prefix][path]
   * </pre>
   *
   * <p>The path must not include the site prefix as it is automatically prepended,
   * but may include the query and fragment part.
   *
   * @param path The path to append to the Website URL and prefix.
   *
   * @return the constructed URL
   */
  public String buildWebsiteURL(String path) {
    return toURLBuilder(this._website).append(this._sitePrefix).append(path).toString();
  }

  /**
   * Short-hand method to build a URL to access the API using the specified path.
   *
   * <p>It builds the URL as follows:
   * <pre>
   *   [scheme]://[host]:[port?][site_prefix][path]
   * </pre>
   *
   * <p>The path must not include the site prefix as it is automatically prepended,
   * but may include the query and fragment part.
   *
   * @param path The path to append to the API URL and prefix.
   *
   * @return the constructed URL
   */
  public String buildAPIURL(String path) {
    return toURLBuilder(this._website).append(this._sitePrefix).append(path).toString();
  }

  /**
   * Short-hand method to build a URL to access the document using the specified path.
   *
   * <p>It builds the URL as follows:
   * <pre>
   *   [scheme]://[host]:[port?][site_prefix][path]
   * </pre>
   *
   * <p>The path must not include the site prefix as it is automatically prepended,
   * but may include the query and fragment part.
   *
   * @param path The path to append to the document URL and prefix.
   *
   * @return the constructed URL
   */
  public String buildDocumentURL(String path) {
    return toURLBuilder(this._website).append(this._sitePrefix).append(path).toString();
  }

  /**
   * Returns a builder to constructs a URL to access the API.
   *
   * <p>The returned value has the form <code>[scheme]://[host]:[port]</code>; or
   * <code>[scheme]://[host]</code> if using the default port for the protocol.
   *
   * <p>The returned value is normalized so that it does not include the default
   * port or any trailing '/'. It does not include the site prefix.
   *
   * @return the API URL as a string builder.
   *
   * @deprecated Use {@link #getAPIURLBuilder()} instead
   */
  @Deprecated
  public StringBuilder buildAPIURL() {
    return toURLBuilder(this._api);
  }

  /**
   * @return the host URL as a string builder.
   *
   * @deprecated Use {@link #buildWebsiteURL()} or {@link #buildDocumentURL()} instead.
   */
  @Deprecated
  public StringBuilder buildHostURL() {
    return toURLBuilder(this._website);
  }

  /**
   * @return the host URL.
   *
   * @deprecated Use {@link #buildWebsiteURL()} or {@link #buildDocumentURL()} instead.
   */
  @Deprecated
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
    PSConfig config = singleton;
    if (config == null) {
      config = loadFromService();
      if (config == null) throw new IllegalStateException("PSConfig is not configured");
      singleton = config;
    }
    return config;
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
    @SuppressWarnings("null")
    @NonNull String prefix = p.getProperty("siteprefix", DEFAULT_PREFIX);
    try {
      URL website  = toBaseURL(p, "url", DEFAULT_WEBSITE.toString());
      URL api      = toBaseURL(p, "api-url", website.toString());
      URL document = toBaseURL(p, "document-url",  deriveURL(website, DEFAULT_DOCUMENT_PORT));
      PSConfig config = new PSConfig(website, api, document, prefix);

      // API version and handling
      String apiVersion = p.getProperty("api-version");
      if (apiVersion != null) {
        config.setServiceAPIVersion(Version.parse(apiVersion));
      }
      if ("true".equals(p.getProperty("api-strict"))) {
        config.setServiceAPIStrict(true);
      }
      return config;
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("PageSeeder properties are not configured properly", ex);
    }
  }

  /**
   * For use by service providers to create a new PSConfig instance.
   *
   * @param website  The URL to use as the base URI to access the PageSeeder Website
   * @param api      The URL to use as the base URI for the service API
   * @param document The default host URL for PageSeeder documents
   *
   * @return The corresponding configuration instance.
   *
   * @throws IllegalArgumentException If any or the properties yield to an malformed URL
   */
  public static PSConfig newInstance(String website, String api, String document) {
    URL w = toBaseURL(website);
    URL a = toBaseURL(api);
    URL d = toBaseURL(document);
    return new PSConfig(w, a, d, DEFAULT_PREFIX);
  }

  /**
   * For use by service providers to create a new PSConfig instance.
   *
   * @param website  The URL to use as the base URI to access the PageSeeder Website
   * @param api      The URL to use as the base URI for the service API
   *
   * @return The corresponding configuration instance.
   *
   * @throws IllegalArgumentException If any or the properties yield to an malformed URL
   */
  public static PSConfig newInstance(String website, String api) {
    URL w = toBaseURL(website);
    String document = deriveURL(w, DEFAULT_DOCUMENT_PORT);
    return newInstance(website, api, document);
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
   * @param p        The properties
   * @param property The name of the property
   * @param fallback The fallback value
   *
   * @return the URL THe corresponding base URL
   */
  private static URL toBaseURL(Properties p, String property, String fallback) {
    String url = p.getProperty(property);
    if (url != null && url.length() > 0) return toBaseURL(url);
    return toBaseURL(fallback);
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
  private static URL toBaseURL(String url) {
    try {
      return URI.create(url).resolve("/").toURL();
    } catch (MalformedURLException ex) {
      throw new IllegalArgumentException("Specified PageSeeder configuration URL is malformed");
    }
  }

  /**
   * Load the configuration instance to providers using a service loaders.
   *
   * @return the configuration from the SPI
   */
  private static @Nullable PSConfig loadFromService() {
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
   * Generate a string builder from the specified URL
   *
   * @param url The URL to construct the builder.
   *
   * @return A string builder using the url as a base.
   */
  private static String deriveURL(URL url, int port) {
    StringBuilder s = new StringBuilder();
    s.append("http://").append(url.getHost());
    if (port != -1 && port != url.getDefaultPort()) {
      s.append(":").append(port);
    }
    return s.toString();
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
