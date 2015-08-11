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

import org.pageseeder.bridge.PSSession;

/**
 * A base class for all the core object managers.
 *
 * <p>All managers should specify a user to connect to PageSeeder.
 *
 * @author Christophe Lauret
 * @version 0.2.0
 * @since 0.2.0
 */
abstract class Sessionful {

  /**
   * The user connecting to the server.
   */
  protected final PSSession _session;

  /**
   * Create a new manager using the specified user.
   *
   * @param user the using making the connections.
   */
  public Sessionful(PSSession user) {
    this._session = user;
  }

  /**
   * @return the session used by the class.
   */
  public PSSession session() {
    return this._session;
  }

}
