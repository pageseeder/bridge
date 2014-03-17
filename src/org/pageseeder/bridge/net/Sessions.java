/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.net;

import org.pageseeder.bridge.PSSession;

/**
 * A utility class for PageSeeder Sessions.
 *
 * @author Christophe Lauret
 */
public final class Sessions {

  /**
   * One minute in millis seconds.
   */
  private static final long ONE_MINUTE_IN_MS = 60000;

  /**
   * A JSession ID to recycle when not using a PageSeeder User.
   */
  private static volatile PSSession anonymous = null;

  /**
   * Utility class.
   */
  private Sessions() {
  }

  /**
   * Indicates whether the session is still valid for the specified session.
   *
   * @param session The PageSeeder session to check.
   *
   * @return <code>true</code> if the session is still valid;
   *         <code>false</code> otherwise.
   */
  public static boolean isValid(PSSession session) {
    if (session == null) return false;
    final int _minutes = 60;
    long maxSessionAge = _minutes * ONE_MINUTE_IN_MS;
    return session.age() < maxSessionAge;
  }

  /**
   * @return the anonymous
   */
  public static PSSession getAnonymous() {
    return anonymous;
  }

  /**
   * Setting a reusable session for anonymous users.
   *
   * @param session a session to use for anonymous connections.
   */
  public static void setAnonymous(PSSession session) {
    Sessions.anonymous = session;
  }
}
