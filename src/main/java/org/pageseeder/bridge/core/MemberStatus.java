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

import org.eclipse.jdt.annotation.Nullable;

import java.io.Serializable;

/**
 * A status for a process thread.
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public enum MemberStatus implements Serializable {

  /**
   * The "activated" status.
   */
  activated("activated"),

  /**
   * The "set-password" status.
   */
  set_password("set-password"),

  /**
   * The "unactivated" status.
   */
  unactivated("unactivated"),

  /**
   * The "unactivated" status.
   */
  unknown("unknown");

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /** the attribute value of the status. */
  private final String _attribute;

  MemberStatus(String attribute) {
    this._attribute = attribute;
  }

  /**
   * @return the attribute value of the status.
   */
  public String attribute() {
    return this._attribute;
  }

  /**
   * @return the attribute value of the status.
   */
  @Override
  public String toString() {
    return this._attribute;
  }

  /**
   * Load a member status from its string value
   *
   * @param s the string value
   * @return the status if found, <code>null</code> otherwise
   */
  public static MemberStatus forAttribute(@Nullable String s) {
    for (MemberStatus st : values()) {
      if (st._attribute.equalsIgnoreCase(s)) return st;
    }
    return unknown;
  }

}
