package org.pageseeder.bridge.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map.Entry;

import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.PSToken;
import org.pageseeder.bridge.net.UsernamePassword;

/**
 * Simple fluent class to define HTTP requests to PageSeeder.
 *
 * <h3>Examples</h3>
 *
 * <p>Invoking a service
 * <pre>
 *   // Retrieve a member
 *   Response r = new Request(Method.GET, Service.get_member, member)
 *                    .using(token)
 *                    .execute();
 *
 *   // Edit a member
 *   Response r = new Request(Method.PATCH, Service.edit_member, member)
 *                    .parameter("firstname", "John")
 *                    .parameter("firstname", "Doe")
 *                    .using(token)
 *                    .execute();
 * </pre>
 *
 * <p>Accessing a resource directly</p>
 * <pre>
 *   // Static file on PageSeeder
 *   Response r = new Request(Method.GET, "/test/images/hello.jpg")
 *                    .using(session)
 *                    .execute();
 *
 *   // Member service
 *   Response r = new Request(Method.GET, "/service/members/~jdoe")
 *                    .using(token)
 *                    .execute();
 * </pre>
 *
 * <p>Calling a servlet</p>
 * <pre>
 *   Response r = new Request(Method.POST, Servlet.GENERIC_SEARCH)
 *                .parameter("question", "test")
 *                .parameter("type", "document")
 *                .parameter("from", "2016-06-01")
 *                .using(token)
 *                .execute();
 * </pre>
 *
 * @author Christophe Lauret
 * @version 0.9.1
 * @since 0.9.1
 */
public final class Request extends BasicRequest {

  /**
   * The body of the resource (used for PUT requests).
   */
  private String _body;

  /**
   * Creates a new request to PageSeeder.
   *
   * @param method The HTTP method
   * @param path   The path without the site prefix (e.g. <code>/ps</code>)
   */
  public Request(Method method, String path) {
    super(method, path);
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * <p>This method will automatically constructs the correct URL for the requested
   * service using the URI variables.
   *
   * @param method   The HTTP method
   * @param service  The PageSeeder service to use
   * @param variable The variables to inject in the URL path.
   */
  public Request(Method method, Service service, Object... variables) {
    super(method, service, variables);
  }

  /**
   * Creates a new request to a PageSeeder service.
   *
   * @param method   The HTTP method
   * @param servlet  The PageSeeder servlet to use
   */
  public Request(Method method, Servlet servlet) {
    super(method, servlet);
  }

  // Setters (return Request)
  // --------------------------------------------------------------------------

  @Override
  public Request header(String name, String value) {
    return (Request)super.header(name, value);
  }

  @Override
  public Request parameter(String name, String value) {
    return (Request)super.parameter(name, value);
  }

  @Override
  public Request using(PSCredentials credentials) {
    return (Request)super.using(credentials);
  }

  /**
   * Set the body of the request (used for PUT)
   *
   * <p>This is designed for small objects.
   *
   * @param body  The body of the request
   */
  @Override
  public Request body(String body) {
    this._body = body;
    return this;
  }

  // Execute
  // --------------------------------------------------------------------------

  /**
   * Create a PageSeeder connection for the specified URL and method.
   *
   * <p>The connection is configured to:
   * <ul>
   *   <li>Follow redirects</li>
   *   <li>Be used for output</li>
   *   <li>Ignore cache by default</li>
   * </ul>
   *
   * @param resource    The resource to connect to.
   * @param type        The type of connection.
   * @param credentials The user login to use (optional).
   *
   * @return A newly opened connection to the specified URL
   * @throws IOException Should an exception be returns while opening the connection
   */
  protected Response execute() throws IOException {
    URL url = toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoOutput(true);
    connection.setInstanceFollowRedirects(true);

    // Tunnel PATCH through POST as HttpUrlConnection does not support PATCH
    if (this._method == Method.PATCH) {
      connection.setRequestMethod("POST");
      connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
    } else {
      connection.setRequestMethod(this._method.name());
    }
    connection.setDefaultUseCaches(false);
    connection.setRequestProperty("X-Requester", "PS-Bridge-" + API_VERSION);

    // Handling of credentials
    PSSession session = null;
    if (this.credentials instanceof PSToken) {
      // Use OAuth bearer token (PageSeeder 5.9+)
      connection.addRequestProperty("Authorization", "Bearer "+((PSToken) this.credentials).token());
    } else if (this.credentials instanceof UsernamePassword) {
      // Basic authorization (PageSeeder 5.6+)
      connection.addRequestProperty("Authorization", ((UsernamePassword) this.credentials).toBasicAuthorization());
    } else if (this.credentials instanceof PSSession) {
      // We will supply the session to the response if it exists
      session = (PSSession)this.credentials;
    }

    // Set other headers
    for (Entry<String, String> h : this._headers.entrySet()) {
      connection.addRequestProperty(h.getKey(), h.getValue());
    }

    // POST using "application/x-www-form-urlencoded"
    if (this._method == Method.POST || this._method == Method.PATCH) {
      byte[] parameters = encodeParameters().getBytes("utf-8");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
      connection.setRequestProperty("Content-Length", Integer.toString(parameters.length));
      connection.setDoInput(true);
      writeData(connection, parameters);

    // PUT using "text/plain"
    } else if (this._method == Method.PUT) {
      String body = this._body;
      if (body != null) {
        byte[] data = body.getBytes("utf-8");
        connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
        connection.setRequestProperty("Content-Length", Integer.toString(body.length()));
        connection.setDoInput(true);
        writeData(connection, data);
      }
    }

    return new Response(connection, session);
  }

  /**
   * Write the request body content.
   *
   * @param connection The URL connection
   * @param data       The data to write
   *
   * @throws IOException Should any error occur while writing.
   */
  private static void writeData(HttpURLConnection connection, byte[] data) throws IOException {
    try (OutputStream post = connection.getOutputStream()){
      post.write(data);
      post.flush();
    }
  }

}
