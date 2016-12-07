package org.pageseeder.bridge.http;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSCredentials;

public interface HttpRequest {

  /**
   * Sets a request header.
   *
   * @param name  The name of the HTTP header
   * @param value The value of the HTTP header.
   *
   * @return This request.
   */
  HttpRequest header(String name, String value);

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
  HttpRequest parameter(String name, String value);

  /**
   * Add all the parameters to this request.
   *
   * @param parameters A map of parameter names and values to add
   *
   * @return This request.
   */
  HttpRequest parameters(Map<String, String> parameters);

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
  HttpRequest using(PSCredentials credentials);

  /**
   * Sets the time out
   *
   * @param timeout the time out
   *
   * @return This request
   */
  HttpRequest timeout(int timeout);

  /**
   * Sets the PageSeeder configuration to use.
   *
   * @param config the time
   *
   * @return This request
   */
  HttpRequest config(PSConfig config);

  /**
   * Sets the etag on this request as the "If-None-Match" request header.
   *
   * @param etag The etag to use.
   *
   * @return this request.
   */
  HttpRequest etag(String etag);

  /**
   * Set the body of the request (used for PUT)
   *
   * <p>This is designed for small objects, this method will use UTF-8 encoding.
   *
   * @param body  The body of the request
   *
   * @return this request.
   *
   * @throws NullPointerException if the array is <code>null</code>
   */
  HttpRequest body(String body);

  /**
   * Set the body of the request (used for PUT)
   *
   * <p>This method makes a copy the specified array.
   *
   * @param body The body of the request
   *
   * @return this request.
   *
   * @throws NullPointerException if the array is <code>null</code>
   */
  HttpRequest body(byte[] body);

  /**
   * Returns the HTTP header the specified name.
   *
   * @param name The name of the HTTP header (case insensitive)
   *
   * @return The value of the corresponding parameter or <code>null</code>
   */
  @Nullable String header(String name);

  /**
   * Returns an unmodifiable list of HTTP headers for this request.
   *
   * <p>This method will also compute the content headers for the request.
   *
   * @return The list of HTTP headers.
   */
  List<Header> headers();

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
   * @return A response objects from the newly opened connection to the specified URL
   */
  HttpResponse response();

}