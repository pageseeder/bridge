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
package org.pageseeder.bridge.http;

import java.util.Objects;

/**
 * A HTTP header
 *
 * @author Christophe Lauret
 * @version 0.9.1
 * @since 0.9.1
 */
public final class Header {

  /**
   * The parameter name (not URL encoded)
   */
  private final String _name;

  /**
   * The parameter value (not URL encoded)
   */
  private final Object _value;

  /**
   * Create a new parameter
   *
   * @param name  The parameter name (not URL encoded)
   * @param value The parameter value (not URL encoded)
   */
  public Header(String name, String value) {
    this._name = Objects.requireNonNull(name, "Header name must not be null");
    this._value = Objects.requireNonNull(value, "Header value must not be null");
  }

  /**
   * Create a new parameter
   *
   * @param name  The parameter name (not URL encoded)
   * @param value The parameter value (not URL encoded)
   */
  public Header(String name, long value) {
    this._name = Objects.requireNonNull(name, "Header name must not be null");
    this._value = value;
  }

  /**
   * @return The header name
   */
  public String name() {
    return this._name;
  }

  /**
   * @return The header string value
   */
  public String value() {
    return this._value.toString();
  }

  /**
   * @return The header string value
   */
  public long longValue() {
    if (this._value instanceof Long) return ((Long)this._value).longValue();
    return Long.parseLong(toString());
  }

  @Override
  public String toString() {
    return this._name+":"+this._value;
  }

}
