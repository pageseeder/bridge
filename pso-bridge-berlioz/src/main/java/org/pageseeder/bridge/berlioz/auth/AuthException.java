/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.auth;

import java.io.Serializable;

/**
 * For errors occurring during authentication or authorization.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public final class AuthException extends Exception {

  /** As per requirement for {@link Serializable} */
  private static final long serialVersionUID = 1L;

  public AuthException(String message) {
    super(message);
  }

  public AuthException(String message, Throwable throwable) {
    super(message, throwable);
  }

}
