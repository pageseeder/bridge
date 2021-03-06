/*
 * Copyright 2016 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    if ("pageseeder".equals(name)) {
      return new PSAuthenticator();
    }
    // All other cases return null
    return null;
  }

  @Override
  public Iterator<String> authenticators() {
    return SUPPORTED_AUTHENTICATORS.iterator();
  }

}
