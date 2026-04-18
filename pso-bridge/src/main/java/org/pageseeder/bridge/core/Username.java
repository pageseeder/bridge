/*
 * Copyright 2017 Allette Systems (Australia)
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

package org.pageseeder.bridge.core;

import java.io.Serializable;

/**
 * A valid username: unique system-wide, system generated and immutable in practice.
 */
public final class Username extends MemberID implements Serializable {

  /**
   * Constant for when the username is not available
   */
  public static final Username NO_USERNAME = new Username("");

  /** As per requirement for {@link Serializable} */
  private static final long serialVersionUID = 1L;

  /**
   * Construct a new username
   *
   * @param username The username
   *
   * @throws NullPointerException if the username is <code>null</code>
   * @throws IllegalArgumentException if the username is longer than 100 characters
   */
  public Username(String username) {
    super(checkMaxLength(username));
  }

  private static String checkMaxLength(String value) {
    if (value != null && value.length() > 100) throw new IllegalArgumentException("Username must not exceed 100 characters");
    return value;
  }
}