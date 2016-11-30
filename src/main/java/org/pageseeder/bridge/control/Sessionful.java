/*
 * Copyright 2015 Allette Systems (Australia)
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
package org.pageseeder.bridge.control;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.PSToken;

/**
 * A base class for all the core object managers.
 *
 * <p>All managers should specify a user to connect to PageSeeder.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.2.0
 */
abstract class Sessionful {

  /**
   * The user connecting to the server.
   */
  protected final PSCredentials _credentials;

  /**
   * Create a new manager using the specified user session.
   *
   * @param session the user session using making the connections.
   */
  public Sessionful(PSCredentials credentials) {
    this._credentials = credentials;
  }

  /**
   * @return the session used by the class.
   */
  public @Nullable PSSession session() {
    return (this._credentials instanceof PSSession)? (PSSession)this._credentials : null;
  }

  /**
   * @return the session used by the class.
   */
  public @Nullable PSToken token() {
    return (this._credentials instanceof PSToken)? (PSToken)this._credentials : null;
  }

}
