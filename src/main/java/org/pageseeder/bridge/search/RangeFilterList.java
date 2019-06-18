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

import org.eclipse.jdt.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * An immutable list of range filters to apply to a search.
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public class RangeFilterList extends ImmutableList<RangeFilter> implements Iterable<RangeFilter> {

  /**
   * An empty list of range filters.
   */
  public static final RangeFilterList EMPTY = new RangeFilterList(Collections.emptyList());

  private RangeFilterList(List<RangeFilter> ranges) {
    super(ranges);
  }

  /**
   * Add a filter for the specified index field.
   *
   * <p>This method can be used to add multiple filters, however there can only be one
   * filter per field.
   *
   * @param field The name of the index field
   * @param range The value it should match (for that field)
   *
   * @return A new <code>QuestionSearch</code> instance including the specified facet.
   */
  public RangeFilterList filter(String field, Range range) {
    RangeFilter filter = new RangeFilter(field, range);
    List<RangeFilter> ranges = this._list;
    if (!isEmpty()) ranges = minus(ranges, f -> f.field().equals(field));
    ranges = plus(ranges, filter);
    return new RangeFilterList(ranges);
  }

  /**
   * Returns the range filter for the specified index field.
   *
   * @param field The name of the index field
   *
   * @return The corresponding range filter or <code>null</code> if no filter is set for that field
   */
  public @Nullable RangeFilter find(String field) {
    for (RangeFilter f : this)
      if (f.field().equals(field)) return f;
    return null;
  }

  /**
   * Returns the filter for the specified index field.
   *
   * @param field The name of the index field
   */
  public @Nullable Range rangeOf(String field) {
    RangeFilter f = find(field);
    return f != null? f.range() : null;
  }

  /**
   * Add a facet for the specified property.
   *
   * <p>This method is a shorthand for <code>facet("psproperty-"+property, value)</code>.
   *
   * @see #filter(String, Range)
   *
   * @param property The name of the PSML property
   * @param range    The value it should match (for that facet)
   *
   * @return A new <code>GenericSearch</code> instance including the specified property facet.
   */
  public RangeFilterList property(String property, Range range) {
    return filter("psproperty-"+property, range);
  }

  /**
   * Add a facet for the specified property.
   *
   * <p>This method is a shorthand for <code>facet("psmetadata-"+property, value)</code>.
   *
   * @see #filter(String, Range)
   *
   * @param property The name of the PSML property
   * @param range    The value it should match (for that facet)
   *
   * @return A new <code>GenericSearch</code> instance including the specified metadata facet.
   */
  public RangeFilterList metadata(String property, Range range) {
    return filter("psmetadata-"+property, range);
  }


  /**
   * Set the lower range for a date range search on the modified date.
   *
   * @param from The lower range for a date range search on the modified date.
   *
   *@return A new <code>QuestionSearch</code> instance with the updated date range.
   */
  public RangeFilterList from(LocalDateTime from) {
    Range range = rangeOf("psmodifieddate");
    range = range != null? range.min(Search.format(from), true) : Range.from(from, true);
    return filter("psmodifieddate", range);
  }

  /**
   * Set the upper range for a date range search on the modified date.
   *
   * @param to The upper range for a date range search on the modified date.
   *
   * @return A new <code>QuestionSearch</code> instance with the updated date range.
   */
  public RangeFilterList to(LocalDateTime to) {
    Range range = rangeOf("psmodifieddate");
    range = range != null? range.max(Search.format(to), true) : Range.to(to, true);
    return filter("psmodifieddate", range);
  }

  /**
   * Set the upper range for a date range search on the modified date.
   *
   * @param from The lower range value.
   * @param to   The upper range value.
   *
   * @return A new <code>QuestionSearch</code> instance with the updated date range.
   */
  public RangeFilterList between(LocalDateTime from, LocalDateTime to) {
    Range range = Range.between(from, to, true, true);
    return filter("psmodifieddate", range);
  }

  /**
   *
   * @return
   */
  Map<String, String> toParameters(Map<String, String> parameters) {
    // Filters and ranges
    if (!isEmpty()) {
      StringBuilder filters = new StringBuilder();
      for (RangeFilter f : this._list) {
        if (filters.length() > 0) {
          filters.append(',');
        }
        String value = f.range().toString().replaceAll(",", "\\,");
        filters.append(f.field()).append(':').append(value);
      }
      parameters.put("ranges", filters.toString());
    }
    return parameters;
  }

}
