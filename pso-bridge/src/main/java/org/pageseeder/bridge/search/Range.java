/*
 * Copyright 2018 Allette Systems (Australia)
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
package org.pageseeder.bridge.search;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A range of values
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public class Range {

  private final String _min;
  private final String _max;
  private boolean _minInclusive;
  private boolean _maxInclusive;

  public Range(String min, boolean minInclusive, String max, boolean maxInclusive) {
    this._min = Objects.requireNonNull(min);
    this._minInclusive = minInclusive;
    this._max = Objects.requireNonNull(max);
    this._maxInclusive = maxInclusive;
  }

  /**
   * @return The maximum value in the range
   */
  public String max() {
    return this._max;
  }

  /**
   * @return The minimum value in the range
   */
  public String min() {
    return this._min;
  }

  /**
   * Indicates whether the maximum value is included in the range.
   *
   * @return <tt>true</tt> If the maximum value is included in the range
   */
  public boolean isMaxInclusive() {
    return this._maxInclusive;
  }

  /**
   * Indicates whether the minimum value is included in the range.
   *
   * @return <tt>true</tt> If the minimum value is included in the range
   */
  public boolean isMinInclusive() {
    return this._minInclusive;
  }

  /**
   * Create a new range with the new minimum value.
   *
   * @return A new <code>Range</code> instance with the updated minimum.
   */
  public Range min(String min, boolean inclusive) {
    return new Range(min, inclusive, this._max,  this._maxInclusive);
  }

  /**
   * Create a new range with the new maximum value.
   *
   * @return A new <code>Range</code> instance with the updated maximum.
   */
  public Range max(String max, boolean inclusive) {
    return new Range(this._min, this._minInclusive, max, inclusive);
  }

  // Date range search
  // --------------------------------------------------------------------------

  /**
   * Create a new date range.
   *
   * @param from The lower range for a date range search.
   *
   *@return A new <code>Range</code> instance with the updated date range.
   */
  public static Range from(LocalDateTime from, boolean inclusive) {
    return new Range(Search.format(from), inclusive,"",  false);
  }

  /**
   * Create a new date range.
   *
   * @param to The upper range for a date range search.
   *
   * @return A new <code>Range</code> instance with the updated date range.
   */
  public static Range to(LocalDateTime to, boolean inclusive) {
    return new Range("", false, Search.format(to), inclusive);
  }

  /**
   * Create a new date range.
   *
   * @param from The lower range value.
   * @param to   The upper range value.
   *
   * @return A new <code>Range</code> instance with the updated date range.
   */
  public static Range between(LocalDateTime from, LocalDateTime to, boolean fromInclusive, boolean toInclusive) {
    return new Range(Search.format(from), fromInclusive, Search.format(to), toInclusive);
  }

  @Override
  public String toString() {
    // TODO Check if min/max values require escaping
    StringBuilder out = new StringBuilder();
    out.append(this._minInclusive? "[" : "{");
    out.append(this._min);
    out.append(";");
    out.append(this._max);
    out.append(this._maxInclusive? "]" : "}");
    return out.toString();
  }

}
