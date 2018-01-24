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
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.http.Method;
import org.pageseeder.bridge.http.Request;
import org.pageseeder.bridge.http.Response;
import org.pageseeder.bridge.http.ServicePath;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * A convenience class to make requests to the search service.
 *
 * @see <a href="https://dev.pageseeder.com/api/web_services/services/group-search-GET.html">/groups/{group}/search [GET]</a>
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Search implements Serializable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  private static final Question EMPTY_QUESTION = new Question(null, emptyList(), -1);

  /**
   * The group or project.
   */
  private final String _group;

  /**
   * The question
   */
  private final Question _question;

  /**
   * List of fields to use as result facets.
   */
  private final List<Facet> _facets;

  /**
   * The max number of facet values to load (max 1000).
   */
  private final int _facetSize;

  /**
   * A list of field:term pairs to use as filters	no	strings
   */
  private final Map<String, String> _filters;

  /**
   * List of range searches.
   */
  private final Map<String, Range> _ranges;

  /**
   * Requested page
   */
  private final int _page;

  /**
   * Results per page
   */
  private final int _pageSize;

  /**
   * A comma-separated list of fields to sort the results	no	strings
   */
  private final List<String> _sortFields;

  public Search(String group) {
    this(group, EMPTY_QUESTION, emptyList(), -1, emptyMap(), emptyMap(), 1, 1000, emptyList());
  }

  // DO not make this constructor public as it takes the raw parameters without ensuring that lists and maps are unmodifiable
  private Search(String group, Question question,
                 List<Facet> facets, int facetSize,
                 Map<String, String> filters, Map<String, Range> ranges,
                 int page, int pageSize,
                 List<String> sortFields) {
    this._question = question;
    this._group = group;
    this._facets = facets;
    this._facetSize = facetSize;
    this._filters = filters;
    this._ranges = ranges;
    this._page = page;
    this._pageSize = pageSize;
    this._sortFields = sortFields;
  }

  /**
   * Create a new GenericSearch with the specified question (for full-text searches)
   *
   * @param question The question.
   *
   * @return A new <code>Search</code> instance.
   */
  public Search question(String question) {
    Question q = new Question(question, this._question._fields, this._question._suggestSize);
    return new Search(this._group, q, this._facets, -1,
        this._filters, this._ranges, // Filters and ranges
        this._page, this._pageSize, this._sortFields);
  }

  /**
   * @return the question if any; <code>null</code> otherwise.
   */
  public @Nullable String question() {
    return this._question._question;
  }

  /**
   * Set the group to search.
   *
   * @param group the group to search in
   *
   * @return A new <code>Search</code> instance unless the current instance already has the same group.
   */
  public Search group(String group) {
    return new Search(group, this._question, this._facets, -1,
        this._filters, this._ranges, // Filters and ranges
        this._page, this._pageSize, this._sortFields);
  }

  /**
   * @param page the page to set
   *
   * @throws IndexOutOfBoundsException if the page is zero or negative.
   *
   * @return A new <code>Search</code> instance unless the current instance already has the same page.
   */
  public Search page(int page) {
    if (page <= 0) throw new IndexOutOfBoundsException("page must be greater than 0");
    if (page == this._page) return this;
    return new Search(this._group, this._question, this._facets, -1,
        this._filters, this._ranges,
        page, this._pageSize, this._sortFields);
  }

  /**
   * @return the page
   */
  public int page() {
    return this._page;
  }

  /**
   * @param pageSize the number of results per page
   *
   * @return A new <code>Search</code> instance unless the current instance already has the same number of results per page.
   */
  public Search pageSize(int pageSize) {
    if (pageSize <= 0) throw new IndexOutOfBoundsException("pageSize must be greater than 0");
    if (pageSize == this._pageSize) return this;
    return new Search(this._group, this._question, this._facets, -1,
        this._filters, this._ranges,
        this._page, pageSize, this._sortFields);
  }

  /**
   * @return the pageSize
   */
  public int pageSize() {
    return this._pageSize;
  }

  /**
   * Add a facet to compute and be returns in the results.
   *
   * <p>This method can be used to add multiple filters, however there can only be one
   * facet per field.
   *
   * @param field The name of the index field
   * @param flexible The value it should match (for that field)
   *
   * @return A new <code>Search</code> instance including the specified facet.
   */
  public Search facet(String field, boolean flexible) {
    Objects.requireNonNull(field, "The field name must be specified");
    List<Facet> facets;
    if (this._facets.size() == 0) {
      facets = Collections.singletonList(new Facet(field, flexible));
    } else {
      List<Facet> list = new ArrayList<>(this._facets);
      list.add(new Facet(field, flexible));
      facets = Collections.unmodifiableList(list);
    }
    return new Search(this._group, this._question, facets, -1,
        this._filters, this._ranges,
        this._page, this._pageSize, this._sortFields);
  }

  /**
   * @param facetSize the max number of facet values to load (max 1000)
   *
   * @return A new <code>Search</code> instance unless the current instance already has the same number of results per page.
   */
  public Search facetSize(int facetSize) {
    if (facetSize == this._facetSize) return this;
    return new Search(this._group, this._question, this._facets, facetSize,
        this._filters, this._ranges,
        this._page, this._pageSize, this._sortFields);
  }

  /**
   * @return The max number of facet values to load (max 1000)
   */
  public int facetSize() {
    return this._facetSize;
  }

  /**
   * Add a filter for the specified index field.
   *
   * <p>This method can be used to add multiple filters, however there can only be one
   * filter per field.
   *
   * @param field The name of the index field
   * @param value The value it should match (for that field)
   *
   * @return A new <code>Search</code> instance including the specified facet.
   */
  public Search filter(String field, String value) {
    Objects.requireNonNull(field, "The field name must be specified");
    Objects.requireNonNull(value, "The value to filter must be specified");
    Map<String, String> filters;
    if (this._filters.size() == 0) {
      filters = Collections.singletonMap(field, value);
    } else {
      Map<String, String> map = new LinkedHashMap<>(this._filters);
      map.put(field, value);
      filters = Collections.unmodifiableMap(map);
    }
    return new Search(this._group, this._question, this._facets, -1,
        filters, this._ranges,
        this._page, this._pageSize, this._sortFields);
  }

  /**
   * Returns the filter for the specified index field.
   *
   * @param field The name of the index field
   *
   * @return The value the field must match; <code>null</code> if no filter is set for that field
   */
  public @Nullable String filter(String field) {
    return this._filters.get(field);
  }


  /**
   * Add a filter for the specified index field.
   *
   * <p>This method can be used to add multiple filters, however there can only be one
   * range per field.
   *
   * @param field The name of the index field
   * @param value The value it should match (for that field)
   *
   * @return A new <code>Search</code> instance including the specified facet.
   */
  public Search range(String field, Range value) {
    Objects.requireNonNull(field, "The field name must be specified");
    Objects.requireNonNull(value, "The value to filter must be specified");
    Map<String, Range> ranges;
    if (this._ranges.size() == 0) {
      ranges = Collections.singletonMap(field, value);
    } else {
      Map<String, Range> map = new LinkedHashMap<>(this._ranges);
      map.put(field, value);
      ranges = Collections.unmodifiableMap(map);
    }
    return new Search(this._group, this._question, this._facets, -1,
        this._filters, ranges, // Filters and ranges
        this._page, this._pageSize, this._sortFields);
  }

  /**
   * Returns the range for the specified index field if any.
   *
   * @param field The name of the index field
   *
   * @return The value the field must match; <code>null</code> if no filter is set for that field
   */
  public @Nullable Range range(String field) {
    return this._ranges.get(field);
  }

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
  public Search property(String property, String value) {
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
  public Search metadata(String property, String value) {
    return filter("psmetadata-"+property, value);
  }

  // Shorthand for common filters
  // --------------------------------------------------------------------------


  /**
   * Set the facet "pstype" to the specified type of result.
   *
   * @param type The value of the mediatype facet.
   *
   * @return A new <code>Search</code> instance including the specified mediatype facet.
   */
  public Search type(String type) {
    return filter("pstype", type);
  }

  public @Nullable String type() {
    return filter("pstype");
  }

  /**
   * Set the facet "psmediatype" to the specified mediatype.
   *
   * @param mediatype The value of the mediatype facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified mediatype facet.
   */
  public Search mediatype(String mediatype) {
    return filter("psmediatype", mediatype);
  }

  public @Nullable String mediatype() {
    return filter("psmediatype");
  }

  /**
   * Set the facet "psstatus" to the specified status.
   *
   * @param status The value of the status facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified status facet.
   */
  public Search status(String status) {
    return filter("psstatus", status);
  }

  /**
   * @return the value of the status facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String status() {
    return filter("psstatus");
  }

  /**
   * Set the facet "pspriority" to the specified priority.
   *
   * @param priority The value of the priority facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified priority facet.
   */
  public Search priority(String priority) {
    return filter("pspriority", priority);
  }

  /**
   * @return the value of the priority facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String priority() {
    return filter("pspriority");
  }

  /**
   * Set the facet "psassignedto" to the specified assignedto.
   *
   * @return A new <code>GenericSearch</code> instance including the specified assignedto facet.
   */
  public Search assignedTo(String assignedTo) {
    return filter("psassignedto", assignedTo);
  }

  /**
   * @return the value of the assignedto facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String assignedTo() {
    return filter("psassignedto");
  }

  /**
   * Set the facet "psfolder" to the specified folder.
   *
   * @param folder The value of the folder facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified folder facet.
   */
  public Search folder(String folder) {
    return filter("psfolder", folder);
  }

  /**
   * @return the value of the folder facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String folder() {
    return filter("psfolder");
  }

  /**
   * Set the facet "psdocumenttype" to the specified PSML document type.
   *
   * @param documenttype The value of the document type facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified document type facet.
   */
  public Search documentType(String documenttype) {
    return filter("psdocumenttype", documenttype);
  }

  /**
   * @return the value of the document type facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String documentType() {
    return filter("psdocumenttype");
  }

  // Date range search
  // --------------------------------------------------------------------------

//  /**
//   * Set the lower range for a date range search on the modified date.
//   *
//   * @param from The lower range for a date range search on the modified date.
//   *
//   *@return A new <code>Search</code> instance with the updated date range.
//   */
//  public Search from(LocalDateTime from) {
//
//    return new Search(this._question, this._types, this._page, this._pageSize, this._groups, this._facets, from, this._to, this._sortBy);
//  }
//
//  /**
//   * Set the upper range for a date range search on the modified date.
//   *
//   * @param to The upper range for a date range search on the modified date.
//   *
//   * @return A new <code>Search</code> instance with the updated date range.
//   */
//  public Search to(LocalDateTime to) {
//
//    return new Search(this._question, this._types, this._page, this._pageSize, this._groups, this._facets, this._from, to, this._sortBy);
//  }
//
//  /**
//   * Set the upper range for a date range search on the modified date.
//   *
//   * @param from The lower range value.
//   * @param to   The upper range value.
//   *
//   * @return A new <code>Search</code> instance with the updated date range.
//   */
//  public Search between(LocalDateTime from, LocalDateTime to) {
//    if (from.equals(this._from) && to.equals(this._to)) return this;
//    return new Search(this._question, this._types, this._page, this._pageSize, this._groups, this._facets, from, to, this._sortBy);
//  }
//
//  // Sorting
//  // --------------------------------------------------------------------------

  /**
   * Define how the results should be sorted
   *
   * @return A new <code>GenericSearch</code> instance with the updated sorting.
   */
  public Search sortFields(List<String> sortFields) {
    return new Search(this._group, this._question, this._facets, -1,
        this._filters, this._ranges, // Filters and ranges
        this._page, this._pageSize, sortFields);
  }

  /**
   * Build the parameter map for a generic search.
   *
   * @return This predicate a as valid parameter for the Search service in PageSeeder.
   */
  public Map<String, String> toParameters() {
    Map<String, String> parameters = new LinkedHashMap<>();

    // Question
    Question q = this._question;
    if (q._question != null) {
      parameters.put("question", q._question);
      if (!q._fields.isEmpty()) {
        parameters.put("questionfields", join(q._fields, ','));
      }
      if (q._suggestSize >= 0) {
        parameters.put("suggestsize", Integer.toString(q._suggestSize));
      }
    }

    // Facets
    if (!this._facets.isEmpty()) {
      String facets = this._facets.stream().filter(f -> !f.isFlexible()).map(f -> f.field()).collect(Collectors.joining());
      if (!facets.isEmpty())
        parameters.put("facets", facets.toString());
      String flexible = this._facets.stream().filter(f -> f.isFlexible()).map(f -> f.field()).collect(Collectors.joining());
      if (!flexible.isEmpty())
        parameters.put("flexiblefacets", flexible.toString());
    }

    // Filters and ranges
    if (!this._filters.isEmpty()) {
      StringBuilder filters = new StringBuilder();
      for (Entry<String, String> f : this._filters.entrySet()) {
        if (filters.length() > 0) {
          filters.append(',');
        }
        String value = f.getValue().replaceAll(",", "\\,");
        filters.append(f.getKey()).append(':').append(value);
      }
      parameters.put("filters", filters.toString());
    }

    if (!this._ranges.isEmpty()) {
      StringBuilder ranges = new StringBuilder();
      for (Entry<String, Range> f : this._ranges.entrySet()) {
        if (ranges.length() > 0) {
          ranges.append(',');
        }
        String value = f.getValue().toString().replaceAll(",", "\\,");
        ranges.append(f.getKey()).append(':').append(value);
      }
      parameters.put("ranges", ranges.toString());
    }

    // Paging
    if (this._page > 1) {
      parameters.put("page", Integer.toString(this._page));
    }
    parameters.put("pagesize", Integer.toString(this._pageSize));

    // Sorting
    if (!_sortFields.isEmpty()) {
      parameters.put("sortfields", join(this._sortFields, ','));
    }

    return parameters;
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    if (!this._filters.isEmpty()) {
      for (Entry<String, String> f : this._filters.entrySet()) {
        s.append(f.getKey()).append('=').append(f.getValue()).append(';');
      }
    }
    return s.toString();
  }

  /**
   * Convenience method to make a request from this search and directly return
   * the response using the specified credentials.
   *
   * @param credentials The credentials
   *
   * @return The corresponding the response.
   */
  public Response response(PSCredentials credentials) {
    Map<String, String> parameters = toParameters();
    String service = ServicePath.newPath("/groups/{group}/search", this._group);
    return new Request(Method.GET, service)
        .using(credentials)
        .parameters(parameters)
        .response();
  }

  // Private helpers
  // --------------------------------------------------------------------------

  private static String format(LocalDateTime datetime) {
    // We format using second resolutions in UTC
    return datetime.atZone(ZoneOffset.systemDefault())
        .withZoneSameInstant(ZoneOffset.UTC)
        .truncatedTo(ChronoUnit.SECONDS)
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+"Z";
  }

  private static <K,V> Map<K,V> emptyMap() {
    return Collections.emptyMap();
  }

  private static <T> List<T> emptyList() {
    return Collections.emptyList();
  }

  private static <T> List<T> singleton(T o) {
    return Collections.singletonList(o);
  }

  private static <T> List<T> copyOf(Collection<T> list) {
    return Collections.unmodifiableList(new ArrayList<>(list));
  }

  private static String join(Collection<?> values, char separator) {
    StringBuilder s = new StringBuilder();
    for (Object o : values) {
      if (s.length() > 0) {
        s.append(separator);
      }
      s.append(o);
    }
    return s.toString();
  }

  private static final class Question {

    /**
     * The question for full-text searches.
     */
    private final @Nullable String _question;

    /**
     * The list of fields to search the question in.
     */
    private final List<String> _fields;

    /**
     * The max number of suggestions to load (only for question query).
     */
    private final int _suggestSize;

    public Question(String question, List<String> fields, int  suggestSize) {
      this._question = question;
      this._fields = fields;
      this._suggestSize = suggestSize;
    }

    public String question() {
      return this._question;
    }

    public List<String> fields() {
      return this._fields;
    }
  }

  private static final class Facet {
    private final String _field;
    private final boolean _flexible;

    public Facet(String field, boolean flexible) {
      this._field = field;
      this._flexible = flexible;
    }

    public String field() {
      return this._field;
    }

    public boolean isFlexible() {
      return this._flexible;
    }
  }



}
