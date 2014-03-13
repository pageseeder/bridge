/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
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

import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSSession;

/**
 * Defines a resource to retrieve from PageSeeder.
 *
 * <p>Note: This class was initially forked from Bastille 0.8.29
 *
 * @author Christophe Lauret
 * @version 0.2.0
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
   * The parameters to send.
   */
  private final Map<String, String> _parameters;

  /**
   * The name of the resource to access.
   */
  private final boolean _includeErrorContent;

  /**
   * Creates a new connection to the specified resource.
   *
   * @param type The type of resource.
   * @param name The name of the resource to access (depends on the type of resource)
   */
  public PSHTTPResource(PSHTTPResourceType type, String name) {
    this._type = type;
    this._name = name;
    this._parameters = Collections.emptyMap();
    this._includeErrorContent = this._type == PSHTTPResourceType.SERVICE;
  }

  /**
   * Creates a new connection to the specified resource.
   *
   * @param type       The type of resource.
   * @param name       The name of the resource to access (depends on the type of resource)
   * @param parameters The parameters to access the resource.
   * @param include    Whether to include the response content.
   */
  private PSHTTPResource(PSHTTPResourceType type, String name, Map<String, String> parameters, boolean include) {
    this._type = type;
    this._name = name;
    this._parameters = parameters;
    this._includeErrorContent = include;
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

  /**
   * Returns the URL to access this resource.
   *
   * <p>If the user is specified, its details will be included in the URL so that the resource can
   * be accessed on his behalf.
   *
   * @param session               A PageSeeder session to access this resource.
   * @param includePOSTParameters Whether to include the parameters for POST requests.
   *
   * @return the URL to access this resource.
   *
   * @throws MalformedURLException If the URL is not well-formed
   */
  protected URL toURL(PSSession session, boolean includePOSTParameters) throws MalformedURLException {
    return new URL(toURLString(session, includePOSTParameters));
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the string to write the parameters sent via POST as <code>application/x-www-form-urlencoded</code>.
   *
   * @return the string to write the parameters sent via POST.
   */
  protected String getPOSTFormURLEncodedContent() {
    StringBuilder q = new StringBuilder();
    try {
      for (Entry<String, String> p : this._parameters.entrySet()) {
        if (q.length() > 0) q.append("&");
        q.append(URLEncoder.encode(p.getKey(), "utf-8"));
        q.append("=").append(URLEncoder.encode(p.getValue(), "utf-8"));
      }
    } catch (UnsupportedEncodingException ex) {
      // Should never happen as UTF-8 is supported
      ex.printStackTrace();
    }
    return q.toString();
  }

  @Override
  public String toString() {
    return toURLString(null, false);
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the URL to access this resource.
   *
   * <p>If the user is specified, its details will be included in the URL so that the resource can
   * be accessed on his behalf.
   *
   * @param session               A PageSeeder session to access this resource.
   * @param includePOSTParameters Whether to include the parameters for POST requests.
   *
   * @return the URL to access this resource.
   */
  private String toURLString(PSSession session, boolean includePOSTParameters) {
    PSConfig pageseeder = PSConfig.singleton();

    // Start building the URL
    StringBuilder url = pageseeder.buildAPIURL();

    // Decompose the resource (in case it contains a query or fragment part)
    String path  = getURLPath(this._name);
    String query = getURLQuery(this._name);
    String frag  = getURLFragment(this._name);

    // Servlets
    if (this._type == PSHTTPResourceType.SERVLET) {
      url.append(pageseeder.getServletPrefix()).append('/');
      url.append(path);

    // Services
    } else if (this._type == PSHTTPResourceType.SERVICE) {
      url.append(pageseeder.getSitePrefix());
      url.append("/service");
      url.append(path);

    // Any other resource
    } else {
      url.append(path);
    }

    // If the session ID is available
    if (session != null) {
      // Use the specified user if available
      url.append(";jsessionid=").append(session);
    } else if (Sessions.getAnonymous() != null) {
      // Recycle an anonymous session ID if possible
      url.append(";jsessionid=").append(Sessions.getAnonymous());
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
    if (includePOSTParameters) {
      try {
        for (Entry<String, String> p : this._parameters.entrySet()) {
          url.append(firstParameter? '?' : '&').append(URLEncoder.encode(p.getKey(), "utf-8"));
          url.append("=").append(URLEncoder.encode(p.getValue(), "utf-8"));
          firstParameter = false;
        }
      } catch (UnsupportedEncodingException ex) {
        // Should never happen as UTF-8 is supported
        ex.printStackTrace();
      }
    }
    // Fragment if any
    if (frag != null)
      url.append(frag);
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
  private static String getURLQuery(String resource) {
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
  private static String getURLFragment(String resource) {
    int h = resource.indexOf('#');
    return h >= 0 ? resource.substring(h) : null;
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
    private PSHTTPResourceType type;

    /**
     * The name of the resource to access.
     */
    private String name;

    /**
     * Whether to include errors
     */
    private boolean includeError = false;

    /**
     * The parameters to send.
     */
    private final Map<String, String> _parameters = new HashMap<String, String>();

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
     * Indicates whether this resource should include the error content.
     *
     * @param include <code>true</code> to include the content of response even when the response code is greater than 400;
     *                <code>false</code> to only include the response when the response code is between 200 and 299.
     */
    protected void includeErrorContent(boolean include) {
      this.includeError = include;
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
      if (this.type == null) throw new IllegalStateException("Unable to build PSResource, type is not set.");
      if (this.name == null) throw new IllegalStateException("Unable to build PSResource, name is not set.");
      Map<String, String> parameters = null;
      if (this._parameters.isEmpty()) {
        parameters = Collections.<String, String>emptyMap();
      } else {
        parameters = new HashMap<String, String>(this._parameters);
      }
      return new PSHTTPResource(this.type, this.name, parameters, this.includeError);
    }

  }

}
