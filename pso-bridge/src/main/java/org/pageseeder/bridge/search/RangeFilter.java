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

import java.util.Objects;

/**
 * A search filter on a PageSeeder group index.
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public class RangeFilter {

  private final String _field;

  private final Range _range;

  protected RangeFilter(String field, Range range) {
    this._field = Objects.requireNonNull(field);
    this._range = Objects.requireNonNull(range);
  }

  /**
   * @return The name of the field this range applies to
   */
  public final String field() {
    return this._field;
  }

  /**
   * @return The range for that filter.
   */
  public final Range range() {
    return this._range;
  }

  @Override
  public String toString() {
    return this._field+":"+this._range;
  }
}
