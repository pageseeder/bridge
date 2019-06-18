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
 * <p>It is a field name-value pair that the question must match or not match depending on the requirement.</p>
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public class Filter {

  /**
   * The requirement on the occurrence of the name-value pair in the index.
   */
  public enum Occur {

    /**
     * Only results that match the field name will be included in the index if they match this value or any other value
     * from the same field with the default occurrence.
     *
     * <p>In other words, the DEFAULT performs and OR of the filters on same field but AND with filters on other
     * fields.</p>
     */
    DEFAULT(""),

    /**
     * Only results that match the field name and value pair will be included.
     *
     * <p>In other words, the MUST performs and AND with the other filters.</p>
     */
    MUST("+"),

    /**
     * Only results that do not match the field name and value pair will be included.
     */
    MUST_NOT("-");

    private final String _symbol;

    Occur(String symbol) {
      this._symbol = symbol;
    }

    @Override
    public String toString() {
      return this._symbol;
    }
  }

  private final String _field;

  private final String _value;

  private final Occur _occur;

  /**
   * Create a new filter.
   *
   * @param field The name of the field
   * @param value The value of the field to match
   * @param occur the requirement on that filter
   *
   * @throws NullPointerException If any of the values is <code>null</code>.
   */
  public Filter(String field, String value, Occur occur) {
    this._field = Objects.requireNonNull(field, "The field name must be specified");
    this._value = Objects.requireNonNull(value, "The value to filter must be specified");
    this._occur = Objects.requireNonNull(occur);
  }

  /**
   * Create a new filter with DEFAULT occurrence.
   *
   * @param field The name of the field
   * @param value The value of the field to match
   *
   * @throws NullPointerException If any of the values is <code>null</code>.
   */
  public Filter(String field, String value) {
    this(field, value, Occur.DEFAULT);
  }

  /**
   * @return The name of the field
   */
  public final String field() {
    return this._field;
  }

  /**
   * @return The value of the field to match
   */
  public final String value() {
    return this._value;
  }

  /**
   * @return The requirement on that filter
   */
  public Occur occur() {
    return _occur;
  }

  @Override
  public String toString() {
    return this._occur+this._field+":"+this._value;
  }
}


