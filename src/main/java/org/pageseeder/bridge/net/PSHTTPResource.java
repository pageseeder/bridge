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
package org.pageseeder.bridge.net;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.PSSession;

/**
 * Defines a resource to retrieve from PageSeeder.
 *
 * <p>Note: This class was initially forked from Bastille 0.8.29
 *
 * @author Christophe Lauret
 * @version 0.2.27
 * @since 0.2.0
 */
public final class PSHTTPResource {

  /**
   * The type of resource accessed.
   */
  private final PSHTTPResourceType _type;

  /**
   * The name of the resource to access.
   */
  private final String _name;

  /**
   * The body of the resource (used for PUT requests).
   */
  private final @Nullable String _body;

  /**
   * The parameters to send.
   */
  private final Map<String, String> _parameters;

  /**
   * The name of the resource to access.
   */
  private final boolean _includeErrorContent;

  /**
   * The pageseeder configuration that will be used to login and logout. By default, it will use the default
   * configuration. However, it will be initialized as null. Only when request it will be set to default in case the
   * caller does not specify one.
   */
  private final PSConfig _config;
  /**
   * Creates a new connection to the specified resource.
   *
   * @param type The type of resource.
   * @param name The name of the resource to access (depends on the type of resource)
   */
  public PSHTTPResource(PSHTTPResourceType type, String name) {
    this (type, name, null, Collections.emptyMap(), type == PSHTTPResourceType.SERVICE,
        PSConfig.getDefault());

  }

  /**
   * Creates a new connection to the specified resource.
   *
   * @param type       The type of resource.
   * @param name       The name of the resource to access (depends on the type of resource)
   * @param body       The body of the resource (used for PUT requests).
   * @param parameters The parameters to access the resource.
   * @param include    Whether to include the response content.
   * @param config     The pageseeder that will be accessed.
   */
  private PSHTTPResource(PSHTTPResourceType type, String name, @Nullable String body, Map<String, String> parameters,
                         boolean include, PSConfig config) {
    this._type = type;
    this._name = name;
    this._body = body;
    this._parameters = parameters;
    this._includeErrorContent = include;
    this._config = config;
  }

  // Getters
  // ----------------------------------------------------------------------------------------------

  /**
   * @return The type of resource requested.
   */
  public PSHTTPResourceType type() {
    return this._type;
  }

  /**
   * Returns the name of the resource to access.
   *
   * <dl>
   *   <dt>servlet</dt>
   *   <dd>The full class name of the servlet (may include parameters)</dd>
   *   <dt>service</dt>
   *   <dd>The path name of the servlet (may include parameters)</dd>
   *   <dt>resource</dt>
   *   <dd>The full path of the resource (may include parameters)</dd>
   * </dl>
   *
   * @return the name of the resource to access.
   */
  public String name() {
    return this._name;
  }

  /**
   * Returns the body of the resource (used for PUT requests).
   *
   * @return the body of the resource.
   */
  public @Nullable String body() {
    return this._body;
  }

  /**
   * Add a parameter to this request.
   *
   * @param name  The name of the parameter
   * @param value The value of the parameter
   */
  public void addParameter(String name, String value) {
    this._parameters.put(name, value);
  }

  /**
   * Indicates whether this resource should include the error content.
   *
   * @return <code>true</code> to include the content of response even when the response code is greater than 400;
   *         <code>false</code> to only include the response when the response code is between 200 and 299.
   */
  protected boolean includeErrorContent() {
    return this._includeErrorContent;
  }

  /**
   * The pageseeder configuration.
   * @return <code>PSConfig</code>
   */
  public PSConfig config() {
    return this._config;
  }
  /**
   * Returns the URL to access this resource.
   *
   * <p>If the user is specified, its details will be included in the URL so that the resource can
   * be accessed on his behalf.
   *
   * @param session A PageSeeder session to access this resource.
   *
   * @return the URL to access this resource.
   *
   * @throws MalformedURLException If the URL is not well-formed
   */
  public URL toURL(PSSession session) throws MalformedURLException {
    return toURL(session, true);
  }

  @Override
  public String toString() {
    return toURLString(null, false);
  }

  // Protected helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the URL to access this resource.
   *
   * <p>If the user is specified, its details will be included in the URL so that the resource can
   * be accessed on his behalf.
   *
   * @param credentials           Authentication method to generate the URL
   * @param includePOSTParameters Whether to include the parameters for POST requests.
   *
   * @return the URL to access this resource.
   *
   * @throws MalformedURLException If the URL is not well-formed
   */
  protected URL toURL(@Nullable PSCredentials credentials, boolean includePOSTParameters) throws MalformedURLException {
    return new URL(toURLString(credentials, includePOSTParameters));
  }

  /**
   * Returns the string to write the parameters sent via POST as <code>application/x-www-form-urlencoded</code>.
   *
   * @return the string to write the parameters sent via POST.
   */
  protected String getPOSTFormURLEncodedContent() {
    StringBuilder q = new StringBuilder();
    try {
      for (Entry<String, String> p : this._parameters.entrySet()) {
        if (q.length() > 0) {
          q.append("&");
        }
        q.append(URLEncoder.encode(p.getKey(), "utf-8"));
        q.append("=").append(URLEncoder.encode(p.getValue(), "utf-8"));
      }
    } catch (UnsupportedEncodingException ex) {
      // Should never happen as UTF-8 is supported
      ex.printStackTrace();
    }
    return q.toString();
  }

  /**
   * @return the parameters used in this connector
   */
  protected Map<String, String> parameters() {
    return this._parameters;
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the URL to access this resource.
   *
   * <p>If the user is specified, its details will be included in the URL so that the resource can
   * be accessed on his behalf.
   *
   * @param credentials       PageSeeder credentials to generate the query string
   * @param includeParameters Whether to include the parameters for POST requests.
   *
   * @return the URL to access this resource.
   */
  private String toURLString(@Nullable PSCredentials credentials, boolean includeParameters) {
    // Start building the URL
    StringBuilder url = this.config().getAPIURLBuilder();

    // Decompose the resource (in case it contains a query or fragment part)
    String path  = getURLPath(this._name);
    String query = getURLQuery(this._name);
    String frag  = getURLFragment(this._name);

    // Servlets
    if (this._type == PSHTTPResourceType.SERVLET) {
      url.append(this.config().getSitePrefix()).append("/servlet/");
      url.append(path);

    // Services
    } else if (this._type == PSHTTPResourceType.SERVICE) {
      url.append(this.config().getSitePrefix()).append("/service");
      url.append(path);

    // Any other resource
    } else {
      url.append(path);
    }

    // If the session ID is available
    if (credentials instanceof PSSession) {
      // Use the specified user if available
      url.append(";jsessionid=").append(credentials);
    }

    // Query Part
    if (query != null) {
      url.append(query);
    }

    // XFormat (for servlets / resources only)
    if (this._type != PSHTTPResourceType.SERVICE) {
      url.append(query != null? '&' : '?').append("xformat=xml");
    }

    // When not using the "application/x-www-form-urlencoded"
    boolean firstParameter = query == null && this._type == PSHTTPResourceType.SERVICE;
    if (includeParameters) {
      for (Entry<String, String> p : this._parameters.entrySet()) {
        url.append(firstParameter? '?' : '&').append(encodeWithUTF8(p.getKey()));
        url.append("=").append(encodeWithUTF8(p.getValue()));
        firstParameter = false;
      }
    }

    // PageSeeder prior to version 5.6
    if (credentials instanceof UsernamePassword) {
      UsernamePassword up = (UsernamePassword)credentials;
      url.append(firstParameter? '?' : '&').append("username");
      url.append("=").append(encodeWithUTF8(up.username()));
      url.append("&password=").append(encodeWithUTF8(up.password()));
    }

    // Fragment if any
    if (frag != null) {
      url.append(frag);
    }
    return url.toString();
  }

  /**
   * Returns the fragment part of the URL.
   *
   * @param resource the path to the resource
   * @return the part before any '#' or '?'.
   */
  private static String getURLPath(String resource) {
    int h = resource.lastIndexOf('#');
    String r = h > 0? resource.substring(0, h) : resource;
    int q = r.indexOf('?');
    if (q > 0) return r.substring(0, q);
    else return r;
  }

  /**
   * Returns the query part of the URL.
   *
   * @param resource the path to the resource
   * @return the part after and including '?' if it exists; otherwise <code>null</code>
   */
  private static @Nullable String getURLQuery(String resource) {
    int q = resource.indexOf('?');
    int h = resource.lastIndexOf('#');
    if (q < 0 || (h > 0 && h < q)) return null;
    if (h > q) return resource.substring(q, h);
    else return resource.substring(q);
  }

  /**
   * Returns the fragment part of the URL.
   *
   * @param resource the path to the resource
   * @return the part after and including '#' if it exists; otherwise <code>null</code>
   */
  private static @Nullable String getURLFragment(String resource) {
    int h = resource.indexOf('#');
    return h >= 0 ? resource.substring(h) : null;
  }

  /**
   * URL encode using UTF-8 without throwing a <code>UnsupportedEncodingException</code> which
   * can never occur.
   *
   * @param s string to encoded (e.g. parameter)
   * @return the URL encoded string
   */
  private static String encodeWithUTF8(String s) {
    try {
      return URLEncoder.encode(s, "utf-8");
    } catch (UnsupportedEncodingException ex) {
      // this should never happen since "utf" support is required by JDK.
      throw new RuntimeException(ex);
    }
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * A builder for this resource.
   *
   * @author Christophe Lauret
   * @version 0.1.0
   */
  public static final class Builder {

    /** The type of resource accessed. */
    private @Nullable PSHTTPResourceType type;

    /**
     * The name of the resource to access.
     */
    private @Nullable String name;

    /**
     * The body of the resource (used for PUT requests).
     */
    private @Nullable String body;

    /**
     * Whether to include errors
     */
    private boolean includeError = false;

    /**
     * The pageseeder configuration.
     */
    private PSConfig config;
    /**
     * The parameters to send.
     */
    private final Map<String, String> _parameters = new HashMap<>();

    /**
     * Creates a new builder for a PageSeeder resource.
     */
    public Builder() {
    }

    /**
     * Creates a new builder for a PageSeeder resource.
     *
     * @param type The type of resource.
     * @param name The name of the resource to access (depends on the type of resource)
     */
    public Builder(PSHTTPResourceType type, String name) {
      this.type = type;
      this.name = name;
      // Only include the error if we are connecting to a service as we can get some useful info from the XML
      this.includeError = type == PSHTTPResourceType.SERVICE;
    }

    /**
     * Sets the type of the resource.
     * @param type the type of the resource to retrieve.
     * @return this builder
     */
    public Builder type(PSHTTPResourceType type) {
      this.type = type;
      return this;
    }

    /**
     * Sets the name of the resource.
     * @param name the name of the resource to retrieve.
     * @return this builder
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the body of the resource (used for PUT requests).
     * @param body the body of the resource.
     * @return this builder
     */
    public Builder body(String body) {
      this.body = body;
      return this;
    }

    /**
     * Indicates whether this resource should include the error content.
     *
     * @param include <code>true</code> to include the content of response even when the response code is greater than 400;
     *                <code>false</code> to only include the response when the response code is between 200 and 299.
     */
    protected void includeErrorContent(boolean include) {
      this.includeError = include;
    }

    /**
     * Define which pageseeder will be used.
     * @param config The Pageseeder configuration.
     * @return this builder.
     */
    public Builder config (PSConfig config) {
      this.config = config;
      return this;
    }
    /**
     * Add a parameter to this request.
     *
     * @param name  The name of the parameter
     * @param value The value of the parameter
     * @return this builder.
     */
    public Builder addParameter(String name, String value) {
      this._parameters.put(name, value);
      return this;
    }

    /**
     * Build the resource from the specified arguments.
     * @return The corresponding resource.
     */
    public PSHTTPResource build() {
      PSHTTPResourceType t = this.type;
      String n = this.name;
      if (t == null) throw new IllegalStateException("Unable to build PSResource, type is not set.");
      if (n == null) throw new IllegalStateException("Unable to build PSResource, name is not set.");

      Map<String, String> parameters;
      if (this._parameters.isEmpty()) {
        parameters = Collections.emptyMap();
      } else {
        parameters = new HashMap<>(this._parameters);
      }

      if (this.config == null) {
        config = PSConfig.getDefault();
      }
      return new PSHTTPResource(t, n, this.body, parameters, this.includeError, this.config);
    }

  }

}
