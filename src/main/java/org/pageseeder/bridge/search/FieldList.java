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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An immutable list of field names.
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public class FieldList extends ImmutableList<String> implements Iterable<String> {

  /**
   * Constant for an empty list of field names.
   */
  public static final FieldList EMPTY = new FieldList(Collections.emptyList());

  private FieldList(List<String> fields) {
    super(fields);
  }

  /**
   * Create a new list of field names.
   *
   * @param fields A array of field names
   *
   * @return An instance using corresponding list of field names
   */
  public static FieldList newList(String... fields) {
    return new FieldList(Arrays.asList(fields));
  }

  /**
   * Returns a new list with the specified field appended a the end.
   *
   * @param field The name of the index field
   *
   * @return A new <code>FieldList</code> instance including the specified field.
   */
  public FieldList field(String field) {
    Objects.requireNonNull(field, "The field name must be specified");
    List<String> fields = plus(this._list, field);
    return new FieldList(fields);
  }

  @Override
  public String toString() {
    return Search.join(this._list, ',');
  }

}
