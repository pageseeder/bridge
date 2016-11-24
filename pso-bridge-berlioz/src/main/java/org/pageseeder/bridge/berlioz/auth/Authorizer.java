/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.auth;

/**
 * Defines whether is allowed to access specific resources.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Authorizer {

  /**
   * Indicates whether a user is allowed to access a given resource.
   *
   * @param user A user.
   * @param uri  The URI the user is trying to access.
   *
   * @return <code>true</code> is the user can access the resource;
   *         <code>false</code> otherwise.
   */
  AuthorizationResult isUserAuthorized(User user, String uri);

}
