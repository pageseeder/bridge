/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.auth;

import java.io.Serializable;
import java.security.Principal;

import org.pageseeder.xmlwriter.XMLWritable;

/**
 * An interface to represent a user.
 *
 * <p>To ease interoperability, the XML returned should match:
 * <pre>{@code <user type="[type]"> ... </user>}</pre>
 *
 * <p>All <code>User</code> implementations must be {@link java.io.Serializable}.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public interface User extends XMLWritable, Principal, Serializable {

  /**
   * @return the name of the user.
   */
  @Override
  String getName();

  /**
   * Indicates where the user has a specified role.
   *
   * @param role the roles to check.s
   */
  boolean hasRole(String role);

}
