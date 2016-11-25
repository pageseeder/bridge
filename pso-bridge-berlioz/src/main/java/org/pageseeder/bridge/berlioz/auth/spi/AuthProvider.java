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
