/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.setup;

import java.io.Serializable;

/**
 * Class of exceptions thrown during setup.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.5
 * @since 0.1.5
 */
public final class SetupException extends Exception{

  /** As per requirement for {@link Serializable} */
  private static final long serialVersionUID = 1L;

  public SetupException(String message) {
    super(message);
  }

  public SetupException(String message, Throwable throwable) {
    super(message, throwable);
  }

}
