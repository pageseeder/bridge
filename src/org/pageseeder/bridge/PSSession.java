/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge;

import java.io.Serializable;

/**
 * A simple object to represent a PageSeeder session.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public final class PSSession implements Serializable {

  /**
   * As per recommendation for the {@link Serializable} interface.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The Member's jsession id.
   */
  private String _jsessionid = null;

  /**
   * Indicates when the user was last successfully connected to PageSeeder.
   *
   * <p>This time stamp is used to determine whether the session is still likely to be valid.
   */
  private long timestamp;

  /**
   * Create a new session with the specified ID.
   *
   * @param session The session ID.
   */
  public PSSession(String session) {
    this._jsessionid = session;
    this.timestamp = System.currentTimeMillis();
  }

  /**
   * @return the jsessionid
   */
  public String getJSessionId() {
    return this._jsessionid;
  }

  /**
   * Update the timestamp for this session.
   */
  public void update() {
    this.timestamp = System.currentTimeMillis();
  }

  /**
   * Returns the age of this session.
   *
   * @return The current time minus the timestamp.
   */
  public long age() {
    return System.currentTimeMillis() - this.timestamp;
  }

  @Override
  public String toString() {
    return this._jsessionid;
  }
}
