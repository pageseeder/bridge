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
