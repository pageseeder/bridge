/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
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
