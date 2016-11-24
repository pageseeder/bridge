package org.pageseeder.bridge.berlioz.auth.spi;

import java.util.Iterator;

import org.pageseeder.bridge.berlioz.auth.Authenticator;

/**
 * Used to provides a specific authenticator.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public abstract class AuthProvider {

  /**
   * Initializes a new Auth provider.
   */
  protected AuthProvider() {
  }

  /**
   * Returns an authenticator for the specified name.
   *
   * @param name The name of the authenticator
   *
   * @return The corresponding authenticator instance or <code>null</code>
   */
  public abstract Authenticator<?> authenticatorForName(String name);

  /**
   * Creates an iterator that iterates over the authenticators supported by this provider.
   */
  public abstract Iterator<String> authenticators();

}
