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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.PSToken;
import org.pageseeder.bridge.Version;
import org.pageseeder.bridge.net.UsernamePassword;

/**
 * Base class for HTTP requests to PageSeeder.
 *
 * @author Christophe Lauret
 * @version 0.9.6
 * @since 0.9.1
 */
abstract class BasicRequest {

  /**
   * The "xformat=xml" parameter for servlets.
   */
  private static final Parameter XFORMAT = new Parameter("xformat", "xml");

  /**
   * The "User-Agent" String sent from the Bridge.
   *
   * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.5.3">HTTP/1.1: Semantics and Content - User-Agent</a>
   */
  private static final Header USER_AGENT = new Header("User-Agent", getUserAgentString());

  /**
   * The "X-HTTP-Method-Override" header to tunnel a PATCH request via POST.
   */
  private static final Header HTTP_METHOD_OVERRIDE_PATCH = new Header("X-HTTP-Method-Override", "PATCH");

  /**
   * HTTP method
   */
  protected final Method _method;

  /**
   * Path to the resources
   */
  protected final String _path;

  /**
   * List of parameters.
   */
  protected final List<Parameter> _parameters = new ArrayList<>();

  /**
   * List of HTTP request headers.
   */
  protected List<Header> _headers = new ArrayList<>();

  /**
   * Any credentials used when making the request.
   */
  protected PSCredentials credentials;

  /**
   * The PageSeeder configuration to use.
   */
  protected PSConfig config = PSConfig.getDefault();

  /**
   * The connect timeout on the request.
   */
  protected int timeout = -1;

  /**
   * Creates a new request to PageSeeder.
   *
   * @param method The HTTP method
   * @param path   The path
   */
  public BasicRequest(Method method, String path) {
    this._method = Objects.requireNonNull(method, "the HTTP method is required");
    this._path = extractPath(path);
    String query = extractQuery(path);
    if (query.length() > 0) {
      addQueryToParameters(query, this._parameters);
    }
    if (method == Method.PATCH) {
      this._headers.add(HTTP_METHOD_OVERRIDE_PATCH);
    }
    this._headers.add(USER_AGENT);
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * @param method    The HTTP method
   * @param service   The PageSeeder service to use
   * @param variables The variables to inject in the URL path.
   */
  public BasicRequest(Method method, Service service, Object... variables) {
    this(method, service.toPath(variables));
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * @param method   The HTTP method
   * @param servlet  The PageSeeder servlet to use
   */
  public BasicRequest(Method method, Servlet servlet) {
    this(method, servlet.toPath());
    this._parameters.add(XFORMAT);
  }

  // Setters (return Request)
  // --------------------------------------------------------------------------

  /**
   * Sets a request header.
   *
   * @param name  The name of the HTTP header
   * @param value The value of the HTTP header.
   *
   * @return This request.
   */
  public abstract BasicRequest header(String name, String value);

  /**
   * Adds a request parameter.
   *
   * <p>When using HTTP method <code>POST</code>, these parameters will be encoded as
   * <code>application/x-www-form-urlencoded</code>.
   *
   * <p>For all other HTTP methods, these parameters will be added to the query part.
   *
   * @param name  The name of the HTTP parameters
   * @param value The value of the HTTP parameters.
   *
   * @return This request.
   */
  public BasicRequest parameter(String name, String value) {
    this._parameters.add(new Parameter(name, value));
    return this;
  }

  /**
   * Specify which credentials to use with this request.
   *
   * <p>Only one set of credentials can be used a time, this method will replace
   * any credentials that may have been set priori.
   *
   * <p>This method will automatically update the "Authorization" header field.
   *
   * @param credentials The username/password, token or session to use as credentials
   *
   * @return This request.
   */
  public BasicRequest using(PSCredentials credentials) {
    this.credentials = credentials;
    // Let's update the headers
    if (this.credentials instanceof PSToken) {
      // Use OAuth bearer token (PageSeeder 5.9+)
      header("Authorization", "Bearer "+((PSToken) this.credentials).token());
    } else if (this.credentials instanceof UsernamePassword) {
      // Basic authorization (PageSeeder 5.6+)
      header("Authorization", ((UsernamePassword) this.credentials).toBasicAuthorization());
    } else {
      removeHeader("Authorization");
    }
    return this;
  }

  /**
   * Sets the time out
   *
   * @param timeout the time out
   *
   * @return This request
   */
  public BasicRequest timeout(int timeout) {
    this.timeout = timeout;
    return this;
  }

  /**
   * Sets the PageSeeder configuration to use.
   *
   * @param config the time
   *
   * @return This request
   */
  public BasicRequest config(PSConfig config) {
    this.config = config;
    return this;
  }

  // Getters (return objects)
  // --------------------------------------------------------------------------

  /**
   * Returns the PageSeeder path that is the part after the site prefix as
   * entered in the constructor.
   *
   * @return The path AFTER the site prefix.
   */
  public String path() {
    return this._path;
  }

  /**
   * Returns the HTTP header the specified name.
   *
   * @param name The name of the HTTP header (case insensitive)
   *
   * @return The value of the corresponding parameter or <code>null</code>
   */
  public String header(String name) {
    for (Header h : this._headers) {
      if (h.name().equalsIgnoreCase(name)) return h.value();
    }
    return null;
  }

  /**
   * Returns the value of the first HTTP parameter matching the specified name.
   *
   * @param name The name of the parameter
   *
   * @return The value of the corresponding parameter or <code>null</code>
   */
  public String parameter(String name) {
    for (Parameter p : this._parameters) {
      if (p.name().equals(name)) return p.value();
    }
    return null;
  }

  /**
   * Returns the credentials in use.
   *
   * @return The credentials used by this request
   */
  public PSCredentials credentials() {
    return this.credentials;
  }

  /**
   * Returns the PageSeeder configuration is use.
   *
   * @return the configuration used by this request
   */
  public PSConfig config() {
    return this.config;
  }

  /**
   * Returns the string to write the parameters sent via POST as <code>application/x-www-form-urlencoded</code>.
   *
   * @return the string to write the parameters sent via POST.
   */
  public String encodeParameters() {
    StringBuilder q = new StringBuilder();
    for (Parameter p : this._parameters) {
      if (q.length() > 0) {
        q.append("&");
      }
      p.append(q);
    }
    return q.toString();
  }

  /**
   * Returns the URL to access this resource.
   *
   * <p>If the user is specified, its details will be included in the URL so that the resource can
   * be accessed on his behalf.
   *
   * @return the URL to access this resource.
   *
   * @throws MalformedURLException If the URL is not well-formed
   */
  public URL toURL() throws MalformedURLException {
    return new URL(toURLString());
  }

  /**
   * Returns the URL to access this resource.
   *
   * <p>If the user is specified, its details will be included in the URL so that the resource can
   * be accessed on his behalf.
   *
   * @return the URL to access this resource.
   */
  public String toURLString() {
    // Start building the URL
    StringBuilder url = this.config.buildAPIURL();

    // Path
    url.append(this.config.getSitePrefix()).append(this._path);

    // If the session ID is available
    if (this.credentials instanceof PSSession) {
      // Use the specified user if available
      url.append(";jsessionid=").append(this.credentials);
    }

    // Add the API version if necessary
    if (!this._parameters.contains("v")) {
      boolean strict = this.config.getServiceAPIStrict();
      Version api = this.config.getServiceAPIVersion();
      if (strict || api != null) {
        StringBuilder value = new StringBuilder();
        if (api != null) {
          value.append(api.version());
        }
        if (strict) {
          value.append(";strict");
        }
        this._parameters.add(new Parameter("v", value.toString()));
      }
    }

    // When not using the "application/x-www-form-urlencoded"
    if (this._method != Method.POST && this._method != Method.PATCH) {
      if (!this._parameters.isEmpty()) {
        url.append('?').append(encodeParameters());
      }
    }

    return url.toString();
  }

  /**
   * @return The connection timeout on the request
   */
  public int timeout() {
    return this.timeout;
  }

  /**
   * Implementations must generate the response object by connecting to PageSeeder
   * and returning an instantiated response that includes the status of the response.
   *
   * @return The response corresponding to this request.
   */
  public abstract Response response();

  // Convenience methods
  // ----------------------------------------------------------------------------------------------

  /**
   * Compute the User Agent string as
   *
   * @return The "User-Agent" header string used by PageSeeder
   */
  public static String getUserAgentString() {
    Package p = Package.getPackage("org.pageseeder.bridge");
    String version = p != null ? Objects.toString(p.getImplementationVersion(), "SNAPSHOT") : "SNAPSHOT";
    String osname = System.getProperty("os.name");
    String osarch = System.getProperty("os.arch");
    String jversion = System.getProperty("java.version");
    String jvendor = System.getProperty("java.vendor");
    return "Bridge/"+version+" ("+osname+"; "+osarch+") Java/"+jversion+" ("+jvendor+")";
  }

  /**
   * Returns the full URL for a path on PageSeeder
   *
   * @param path The path to the request resource.
   *
   * @return the full URL to access this resource.
   */
  public static String toURLString(String path) {
    PSConfig ps = PSConfig.getDefault();
    return toURLString(ps, path);
  }

  /**
   * Returns the full URL for a path on PageSeeder
   *
   * @param config The PageSeeder configuration
   * @param path   The path to the request resource.
   *
   * @return the full URL to access this resource.
   */
  public static String toURLString(PSConfig config, String path) {
    return config.buildAPIURL().append(config.getSitePrefix()).append(path).toString();
  }

  // Protected methods (for use by implementations)
  // ----------------------------------------------------------------------------------------------

  /**
   * Sets a request header.
   *
   * @param name  The name of the HTTP header
   * @param value The value of the HTTP header.
   */
  protected void setHeader(String name, String value) {
    removeHeader(name);
    this._headers.add(new Header(name, value));
  }

  /**
   * Sets a request header.
   *
   * @param name  The name of the HTTP header
   * @param value The value of the HTTP header.
   */
  protected void setHeader(String name, int value) {
    removeHeader(name);
    this._headers.add(new Header(name, value));
  }

  /**
   * Removes the specified header.
   *
   * @param name the of the header to remove (not case sensitive)
   */
  protected void removeHeader(String name) {
    for (Iterator<Header> i = this._headers.iterator(); i.hasNext();) {
      Header h = i.next();
      if (h.name().equalsIgnoreCase(name)) {
        i.remove();
        break;
      }
    }
  }

  /**
   * Removes the specified header.
   *
   * @param name the of the header to remove (not case sensitive)
   *
   * @return The header that was removed
   */
  protected Header getHeader(String name) {
    for (Header h : this._headers) {
      if (h.name().equalsIgnoreCase(name)) return h;
    }
    return null;
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the path part of the specified URI.
   *
   * @param uri the path to the resource
   * @return the part before any '#' or '?'.
   */
  private static String extractPath(String uri) {
    // Remove the fragment part
    int f = uri.lastIndexOf('#');
    String p = f > 0? uri.substring(0, f) : uri;
    // Remove the query
    int q = p.indexOf('?');
    if (q >= 0) return p.substring(0, q);
    else return p;
  }

  /**
   * Returns the query part of the URL.
   *
   * @param resource the path to the resource
   *
   * @return the part after and including '?' if it exists; otherwise "".
   */
  private static String extractQuery(String uri) {
    int q = uri.indexOf('?');
    int f = uri.indexOf('#');
    if (q < 0 || (f >= 0 && f < q)) return "";
    if (f > q) return uri.substring(q+1, f);
    else return uri.substring(q+1);
  }

  /**
   * Adds the parameters specified in the query to the parameters.
   *
   * @param query      The query part of the URL
   * @param parameters The parameters to update
   */
  private static void addQueryToParameters(String query, List<Parameter> parameters) {
    String[] pair = query.split("&");
    if (pair.length > 0) {
      for (String p : pair) {
        Parameter param = Parameter.newParameter(p);
        parameters.add(param);
      }
    }
  }

}
