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

public class Range {

  private final String _min;

  private final String _max;

  private boolean _minInclusive;

  private boolean _maxInclusive;

  public Range(String min, String max) {
    this._min = min;
    this._max = max;
  }

  public Range(String min, String max, boolean minInclusive, boolean maxInclusive) {
    this._min = min;
    this._max = max;
    this._minInclusive = minInclusive;
    this._maxInclusive = maxInclusive;
  }

  public String max() {
    return _max;
  }

  public String min() {
    return _min;
  }

  public boolean isMaxInclusive() {
    return _maxInclusive;
  }

  public boolean isMinInclusive() {
    return _minInclusive;
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
