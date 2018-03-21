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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An immutable list of filters to apply to a search.
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public class FilterList extends ImmutableList<Filter> implements Iterable<Filter> {

  /**
   * An empty filter list.
   */
  public static final FilterList EMPTY = new FilterList(Collections.emptyList());

  /**
   * Create a new filter list.
   *
   * <p>Keep this private to avoid exposing list.</p>
   *
   * @param filters The list of filter to add (should be unmodifiable)
   */
  private FilterList(List<Filter> filters) {
    super(filters);
  }

  /**
   * Create a new filters list.
   *
   * @param filters A array of filters
   *
   * @return An instance using corresponding list of filters
   */
  public static FilterList newList(Filter... filters) {
    return new FilterList(Collections.unmodifiableList(Arrays.asList(filters)));
  }

  /**
   * Add a filter for the specified index field.
   *
   * <p>This method can be used to add multiple filters.
   *
   * @param field The name of the index field
   * @param value The value the field should match
   *
   * @return A new <code>FilterList</code> instance including the specified filter.
   */
  public FilterList filter(String field, String value) {
    List<Filter> filters = plus(this._list, new Filter(field, value));
    return new FilterList(filters);
  }

  /**
   * Create a new filter list with the specified filter.
   *
   * @param filter The name of the index field
   *
   * @return A new <code>FilterList</code> instance including the specified filter.
   */
  public FilterList filter(Filter filter) {
    List<Filter> filters = plus(this._list, filter);
    return new FilterList(filters);
  }

  /**
   * Create a new filter list with the specified filter.
   *
   * @param filter The name of the index field
   *
   * @return A new <code>FilterList</code> instance including the specified filter.
   */
  public FilterList plus(Filter filter) {
    return filter(filter);
  }

  /**
   * Returns the field filter for the specified index field.
   *
   * @param field The name of the index field
   *
   * @return The first filter the field must match; <code>null</code> if no filter is set for that field
   */
  public @Nullable Filter find(String field) {
    for (Filter f : this._list)
      if (f.field().equals(field)) return f;
    return null;
  }

  /**
   * Returns all the filter for the specified index field.
   *
   * @param field The name of the index field
   *
   * @return The list of filters applied to this field
   */
  public List<Filter> findAll(String field) {
    return this._list.stream().filter(it -> it.field().equals(field)).collect(Collectors.toList());
  }

  /**
   * Returns the value of the filter for the specified field in the filter list.
   *
   * @param field The name of the index field
   *
   * @return The first matching filter
   */
  private @Nullable String valueOf(String field) {
    Filter filter = find(field);
    return filter != null? filter.value() : null;
  }

  // Shorthand for common filters
  // --------------------------------------------------------------------------

  /**
   * Add a facet for the specified property.
   *
   * <p>This method is a shorthand for <code>facet("psproperty-"+property, value)</code>.
   *
   * @see #filter(String, String)
   *
   * @param property The name of the PSML property
   * @param value    The value it should match (for that facet)
   *
   * @return A new <code>GenericSearch</code> instance including the specified property facet.
   */
  public FilterList property(String property, String value) {
    return filter("psproperty-"+property, value);
  }

  /**
   * Add a facet for the specified property.
   *
   * <p>This method is a shorthand for <code>facet("psmetadata-"+property, value)</code>.
   *
   * @see #filter(String, String)
   *
   * @param property The name of the PSML property
   * @param value    The value it should match (for that facet)
   *
   * @return A new <code>GenericSearch</code> instance including the specified metadata facet.
   */
  public FilterList metadata(String property, String value) {
    return filter("psmetadata-"+property, value);
  }

  /**
   * Set the facet "pstype" to the specified type of result.
   *
   * @param type The value of the mediatype facet.
   *
   * @return A new <code>QuestionSearch</code> instance including the specified mediatype facet.
   */
  public FilterList type(String type) {
    return filter("pstype", type);
  }

  public @Nullable String type() {
    return valueOf("pstype");
  }

  /**
   * Set the facet "psmediatype" to the specified mediatype.
   *
   * @param mediatype The value of the mediatype facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified mediatype facet.
   */
  public FilterList mediatype(String mediatype) {
    return filter("psmediatype", mediatype);
  }

  public @Nullable String mediatype() {
    return valueOf("psmediatype");
  }

  /**
   * Set the facet "psstatus" to the specified status.
   *
   * @param status The value of the status facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified status facet.
   */
  public FilterList status(String status) {
    return filter("psstatus", status);
  }

  /**
   * @return the value of the status facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String status() {
    return valueOf("psstatus");
  }

  /**
   * Set the facet "pspriority" to the specified priority.
   *
   * @param priority The value of the priority facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified priority facet.
   */
  public FilterList priority(String priority) {
    return filter("pspriority", priority);
  }

  /**
   * @return the value of the priority facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String priority() {
    return valueOf("pspriority");
  }

  /**
   * Set the facet "psassignedto" to the specified assignedto.
   *
   * @return A new <code>GenericSearch</code> instance including the specified assignedto facet.
   */
  public FilterList assignedTo(String assignedTo) {
    return filter("psassignedto", assignedTo);
  }

  /**
   * @return the value of the assignedto facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String assignedTo() {
    return valueOf("psassignedto");
  }

  /**
   * Set the facet "psfolder" to the specified folder.
   *
   * @param folder The value of the folder facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified folder facet.
   */
  public FilterList folder(String folder) {
    return filter("psfolder", folder);
  }

  /**
   * @return the value of the folder facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String folder() {
    return valueOf("psfolder");
  }

  /**
   * Set the facet "psdocumenttype" to the specified PSML document type.
   *
   * @param documenttype The value of the document type facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified document type facet.
   */
  public FilterList documentType(String documenttype) {
    return filter("psdocumenttype", documenttype);
  }

  /**
   * @return the value of the document type facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String documentType() {
    return valueOf("psdocumenttype");
  }

  /**
   *
   * @return
   */
  Map<String, String> toParameters(Map<String, String> parameters) {
    if (!isEmpty()) {
      parameters.put("filters", this.toString());
    }
    return parameters;
  }

  @Override
  public String toString() {
    StringBuilder filters = new StringBuilder();
    for (Filter f : this) {
      if (filters.length() > 0) {
        filters.append(',');
      }
      // We must escape the ',' in values
      String value = f.value().replaceAll(",", "\\,");
      filters.append(f.occur()).append(f.field()).append(':').append(value);
    }
    return filters.toString();
  }

}
