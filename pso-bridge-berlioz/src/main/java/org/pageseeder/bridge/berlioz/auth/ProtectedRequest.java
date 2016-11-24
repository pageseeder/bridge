/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.auth;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

/**
 * A URL to save.
 *
 * @author Christophe Lauret
 *
 * @version 0.3.1
 * @since 0.1.0
 */
public final class ProtectedRequest implements Serializable {

  /**
   * As per requirement for the {@link Serializable} interface.
   */
  private static final long serialVersionUID = 129183325321391637L;

  /**
   * The protected URL
   */
  private final String _url;

  /**
   * Creates a new protected request.
   *
   * @param url the protected URL to access.
   */
  public ProtectedRequest(String url) {
    this._url =  url;
  }

  /**
   * @return The protected URL to access.
   */
  public String url() {
    return this._url;
  }

  /**
   * @return Same as {@link #url()}
   */
  @Override
  public String toString() {
    return this._url;
  }

  /**
   * Create a new protected request from the specified servlet request.
   *
   * <p>The url is created from the request URI and query if there is one.
   *
   * @param request The HTTP request.
   */
  public static ProtectedRequest create(HttpServletRequest request) {
    String url  = request.getRequestURI();
    String query = request.getQueryString();
    if (query != null) {
      url = url + '?' +query;
    }
    return new ProtectedRequest(url);
  }

}
