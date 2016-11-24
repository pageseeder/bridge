package org.pageseeder.bridge.berlioz.auth;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.pageseeder.bridge.berlioz.auth.spi.AuthProvider;

/**
 * Defines all the built-in authenticators.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public final class AuthDefaults extends AuthProvider {

  /**
   * List of supported authenticators.
   */
  private static final Set<String> SUPPORTED_AUTHENTICATORS = Collections.singleton("pageseeder");

  /**
   * Required by service provider.
   */
  public AuthDefaults() {
  }

  @Override
  public Authenticator<?> authenticatorForName(final String name) {
    switch (name) {
      case "pageseeder": return new PSAuthenticator();
    }
    // All other cases return null
    return null;
  }

  @Override
  public Iterator<String> authenticators() {
    return SUPPORTED_AUTHENTICATORS.iterator();
  }

}
