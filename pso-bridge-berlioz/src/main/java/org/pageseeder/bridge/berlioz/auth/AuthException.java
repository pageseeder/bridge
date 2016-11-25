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
