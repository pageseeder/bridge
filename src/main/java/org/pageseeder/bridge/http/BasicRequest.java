package org.pageseeder.bridge.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.PSSession;

/**
 * Base class for HTTP requests to PageSeeder.
 *
 * @author Christophe Lauret
 * @version 0.9.1
 * @since 0.9.1
 */
abstract class BasicRequest {

  /** Version of the API */
  protected static final String API_VERSION;
  static {
    Package p = Package.getPackage("org.pageseeder.bridge");
    API_VERSION = p != null ? p.getImplementationVersion() : "dev";
  }

  /**
   * The "xformat=xml" parameter for servlets.
   */
  private static final Parameter XFORMAT = new Parameter("xformat", "xml");

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
  protected final Map<String, String> _headers = new LinkedHashMap<>();

  /**
   * Any credentials used when making the request.
   */
  protected PSCredentials credentials;

  /**
   * Creates a new request to PageSeeder.
   *
   * @param method The HTTP method
   * @param path   The path
   */
  public BasicRequest(Method method, String path) {
    this._method = Objects.requireNonNull(method, "the HTTP method is required");
    this._path = getURLPath(path);
    String query = getURLQuery(path);
    if (query != null && query.length() > 0) {
      addQueryToParameters(query, this._parameters);
    }
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * @param method   The HTTP method
   * @param service  The PageSeeder service to use
   * @param variable The variables to inject in the URL path.
   */
  public BasicRequest(Method method, Service service, Object... variables) {
    this._method = Objects.requireNonNull(method, "the HTTP method is required");
    this._path = service.toPath(variables);
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * @param method   The HTTP method
   * @param servlet  The PageSeeder servlet to use
   */
  public BasicRequest(Method method, Servlet servlet) {
    this._method = Objects.requireNonNull(method, "the HTTP method is required");
    this._path = servlet.toPath();
    this._parameters.add(XFORMAT);
  }

  // Setters (return Request)
  // --------------------------------------------------------------------------

  /**
   * Adds a request header.
   *
   * @param name  The name of the HTTP header
   * @param value The value of the HTTP header.
   *
   * @return This request.
   */
  public BasicRequest header(String name, String value) {
    this._headers.put(name, value);
    return this;
  }

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
   * any credentials that may have been set priori
   *
   * @param credentials The username/password, token or session to use as credentials
   *
   * @return This request.
   */
  public BasicRequest using(PSCredentials credentials) {
    this.credentials = credentials;
    return this;
  }

  /**
   * Set the body of the request (used for PUT)
   *
   * @param body  The body of the request
   */
  public BasicRequest body(String body) {
    // TODO
    return this;
  }

  // Getters (return objects)
  // --------------------------------------------------------------------------

  /**
   * Returns the HTTP header the specified name.
   *
   * @param name The name of the parameter
   *
   * @return The value of the corresponding parameter or <code>null</code>
   */
  public String header(String name) {
    return this._headers.get(name);
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
   * @return
   */
  public PSCredentials credentials() {
    return this.credentials;
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
   * @param credentials           Authentication method to generate the URL
   * @param includePOSTParameters Whether to include the parameters for POST requests.
   *
   * @return the URL to access this resource.
   *
   * @throws MalformedURLException If the URL is not well-formed
   */
  protected URL toURL() throws MalformedURLException {
    return new URL(toURLString());
  }

  /**
   * Returns the URL to access this resource.
   *
   * <p>If the user is specified, its details will be included in the URL so that the resource can
   * be accessed on his behalf.
   *
   * @param credentials       PageSeeder credentials to generathe query string
   * @param includeParameters Whether to include the parameters for POST requests.
   *
   * @return the URL to access this resource.
   */
  protected String toURLString() {
    PSConfig ps = PSConfig.getDefault();

    // Start building the URL
    StringBuilder url = ps.buildAPIURL();

    // Path
    url.append(ps.getSitePrefix()).append(this._path);

    // If the session ID is available
    if (this.credentials instanceof PSSession) {
      // Use the specified user if available
      url.append(";jsessionid=").append(this.credentials);
    }

    // When not using the "application/x-www-form-urlencoded"
    if (this._method != Method.POST && this._method != Method.PATCH) {
      url.append('?').append(encodeParameters());
    }

    return url.toString();
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the path part of the specified URI.
   *
   * @param uri the path to the resource
   * @return the part before any '#' or '?'.
   */
  private static String getURLPath(String uri) {
    int h = uri.lastIndexOf('#');
    String r = h > 0? uri.substring(0, h) : uri;
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
  private static String getURLQuery(String uri) {
    int q = uri.indexOf('?');
    int h = uri.lastIndexOf('#');
    if (q < 0 || (h > 0 && h < q)) return null;
    if (h > q) return uri.substring(q, h);
    else return uri.substring(q);
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
