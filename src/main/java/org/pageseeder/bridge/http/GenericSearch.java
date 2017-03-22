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
package org.pageseeder.bridge.http;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.PSCredentials;

/**
 * A convenience class to make requests to the generic search servlet.
 *
 * @version 0.11.4
 * @since 0.11.4
 */
public final class GenericSearch implements Serializable {

  /** As per recommendation */
  private static final long serialVersionUID = 3L;

  public enum Type {
    document, url, comment, task, folder;
  }

  /**
   * The question for full-text searches.
   */
  private final @Nullable String _question;

  /**
   * Facets.
   */
  private final Map<String, String> _facets;

  /**
   * The list of groups to search.
   */
  private final List<String> _groups;

  /**
   * The types of search result requested.
   */
  private final List<Type> _types;

  /**
   * Requested page
   */
  private final int _page;

  /**
   * Results per page
   */
  private final int _pageSize;

  /**
   * Lower interval in date range search.
   */
  private final LocalDateTime _from;

  /**
   * Higher interval in date range.
   */
  private final LocalDateTime _to;

  /**
   * Field for sorting.
   */
  private SortBy _sortBy;

  public GenericSearch() {
    this(null, emptyList(), 1, 1000, emptyList(), emptyMap(), LocalDateTime.MIN, LocalDateTime.MAX, SortBy.RELEVANCE);
  }

  /**
   * Search for a particular type in the specified group
   *
   * @param type  The type of item being searched
   * @param group The group result.
   */
  public GenericSearch(Type type, String group) {
    this(null, singleton(type), 1, 1000, singleton(group), emptyMap(), LocalDateTime.MIN, LocalDateTime.MAX, SortBy.RELEVANCE);
  }

  // DO not make this constructor public as it takes the raw parameters without ensuring that lists and maps are unmodifiable
  private GenericSearch(@Nullable String question, List<Type> types, int page, int pageSize, List<String> groups, Map<String, String> facets, LocalDateTime from, LocalDateTime to, SortBy sortBy) {
    this._question = question;
    this._types = types;
    this._page = page;
    this._groups = groups;
    this._pageSize = pageSize;
    this._facets = facets;
    this._from = from;
    this._to = to;
    this._sortBy = sortBy;
  }

  /**
   * Create a new GenericSearch with the specified question (for full-text searches)
   *
   * @param question The question.
   *
   * @return A new <code>GenericSearch</code> instance.
   */
  public GenericSearch question(String question) {
    return new GenericSearch(question, this._types, this._page, this._pageSize, this._groups, this._facets, this._from, this._to, this._sortBy);
  }

  /**
   * @return the question if any; <code>null</code> otherwise.
   */
  public @Nullable String question() {
    return this._question;
  }

  /**
   * Sets the type of results this search should return.
   *
   * <p>Do not use this method to add multiple types, use {@link #types(Collection)} or {@link #types(Type...)} instead.
   *
   * @param type the type to set
   *
   * @return A new <code>GenericSearch</code> instance unless the current instance already has the same type.
   */
  public GenericSearch type(Type type) {
    if (this._types.size() == 1 && this._types.contains(type)) return this;
    return new GenericSearch(this._question, singleton(type), this._page, this._pageSize, this._groups, this._facets, this._from, this._to, this._sortBy);
  }

  /**
   * Sets the types of results this search should return.
   *
   * @param types the type to set
   *
   * @return A new <code>GenericSearch</code> instance unless the current instance already has the same types.
   */
  public GenericSearch types(Collection<Type> types) {
    if (this._types.containsAll(types)) return this;
    return new GenericSearch(this._question, copyOf(types), this._page, this._pageSize, this._groups, this._facets, this._from, this._to, this._sortBy);
  }

  /**
   * Sets the type of results this search should return.
   *
   * @param types the type to set
   *
   * @return A new <code>GenericSearch</code> instance unless the current instance already has the same types.
   */
  @SafeVarargs
  public final GenericSearch types(Type... types) {
    return types(Arrays.asList(types));
  }

  /**
   * @return the list of types that the search should return.
   */
  public List<Type> types() {
    return this._types;
  }

  /**
   * Set the group to search.
   *
   * <p>Do not use this method to search multiple group, use {@link #groups(Collection)} or {@link #groups(String...)} instead.
   *
   * @param group the group to search in
   *
   * @return A new <code>GenericSearch</code> instance unless the current instance already has the same group.
   */
  public GenericSearch group(String group) {
    if (this._groups.size() == 1 && this._groups.contains(group)) return this;
    return new GenericSearch(this._question, this._types, this._page, this._pageSize, singleton(group), this._facets, this._from, this._to, this._sortBy);
  }

  /**
   * @param groups the collection of groups to search in
   */
  public GenericSearch groups(Collection<String> groups) {
    List<String> list = copyOf(groups);
    return new GenericSearch(this._question, this._types, this._page, this._pageSize, list, this._facets, this._from, this._to, this._sortBy);
  }

  /**
   * @param groups the groups to search in.
   */
  @SafeVarargs
  public final GenericSearch groups(String... groups) {
    List<String> list = copyOf(Arrays.asList(groups));
    return new GenericSearch(this._question, this._types, this._page, this._pageSize, list, this._facets, this._from, this._to, this._sortBy);
  }

  /**
   * @return the list of groups to search in.
   */
  public List<String> groups() {
    return this._groups;
  }

  /**
   * @param page the page to set
   *
   * @throws IndexOutOfBoundsException if the page is zero or negative.
   *
   * @return A new <code>GenericSearch</code> instance unless the current instance already has the same page.
   */
  public GenericSearch page(int page) {
    if (page <= 0) throw new IndexOutOfBoundsException("page must be greater than 0");
    if (page == this._page) return this;
    return new GenericSearch(this._question, this._types, page, this._pageSize, this._groups, this._facets, this._from, this._to, this._sortBy);
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
   * @return A new <code>GenericSearch</code> instance unless the current instance already has the same number of results per page.
   */
  public GenericSearch pageSize(int pageSize) {
    if (pageSize <= 0) throw new IndexOutOfBoundsException("pageSize must be greater than 0");
    if (pageSize == this._pageSize) return this;
    return new GenericSearch(this._question, this._types, this._page, pageSize, this._groups, this._facets, this._from, this._to, this._sortBy);
  }

  /**
   * @return the pageSize
   */
  public int pageSize() {
    return this._pageSize;
  }

  /**
   * Add a facet for the specified index field.
   *
   * <p>This method can be used to add multiple facets, however there can only be one
   * facet per field.
   *
   * @param field The name of the index field
   * @param value The value it should match (for that facet)
   *
   * @return A new <code>GenericSearch</code> instance including the specified facet.
   */
  public GenericSearch facet(String field, String value) {
    Map<String, String> facets;
    if (this._facets.size() == 0) {
      facets = Collections.singletonMap(field, value);
    } else {
      Map<String, String> map = new LinkedHashMap<>(this._facets);
      map.put(field, value);
      facets = Collections.unmodifiableMap(map);
    }
    return new GenericSearch(this._question, this._types, this._page, this._pageSize, this._groups, facets, this._from, this._to, this._sortBy);
  }

  /**
   * Returns the facet for the specified index field.
   *
   * @param field The name of the index field
   *
   * @return The value the facet must match (for that facet); <code>null</code> if that facet isn't set for the search
   */
  public @Nullable String facet(String field) {
    return this._facets.get(field);
  }

  /**
   * Add a facet for the specified property.
   *
   * <p>This method is a shorthand for <code>facet("psproperty-"+property, value)</code>.
   *
   * @see #facet(String, String)
   *
   * @param property The name of the PSML property
   * @param value    The value it should match (for that facet)
   *
   * @return A new <code>GenericSearch</code> instance including the specified property facet.
   */
  public GenericSearch property(String property, String value) {
    return facet("psproperty-"+property, value);
  }

  /**
   * Add a facet for the specified property.
   *
   * <p>This method is a shorthand for <code>facet("psmetadata-"+property, value)</code>.
   *
   * @see #addFacet(String, String)
   *
   * @param property The name of the PSML property
   * @param value    The value it should match (for that facet)
   *
   * @return A new <code>GenericSearch</code> instance including the specified metadata facet.
   */
  public GenericSearch metadata(String property, String value) {
    return facet("psmetadata-"+property, value);
  }

  // Shorthand for common facets
  // --------------------------------------------------------------------------

  /**
   * Set the facet "psmediatype" to the specified mediatype.
   *
   * @param mediatype The value of the mediatype facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified mediatype facet.
   */
  public GenericSearch mediatype(String mediatype) {
    return facet("psmediatype", mediatype);
  }

  public @Nullable String mediatype() {
    return facet("psmediatype");
  }

  /**
   * Set the facet "psstatus" to the specified status.
   *
   * @param mediatype The value of the status facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified status facet.
   */
  public GenericSearch status(String status) {
    return facet("psstatus", status);
  }

  /**
   * @return the value of the status facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String status() {
    return facet("psstatus");
  }

  /**
   * Set the facet "pspriority" to the specified priority.
   *
   * @param mediatype The value of the priority facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified priority facet.
   */
  public GenericSearch priority(String priority) {
    return facet("pspriority", priority);
  }

  /**
   * @return the value of the priority facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String priority() {
    return facet("pspriority");
  }

  /**
   * Set the facet "psassignedto" to the specified assignedto.
   *
   * @param mediatype The value of the assignedto facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified assignedto facet.
   */
  public @Nullable String assignedTo() {
    return facet("psassignedto");
  }

  /**
   * @return the value of the assignedto facet or <code>null</code> if that facet isn't defined.
   */
  public GenericSearch assignedTo(String assignedTo) {
    return facet("psassignedto", assignedTo);
  }

  /**
   * Set the facet "psfolder" to the specified folder.
   *
   * @param mediatype The value of the folder facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified folder facet.
   */
  public GenericSearch folder(String folder) {
    return facet("psfolder", folder);
  }

  /**
   * @return the value of the folder facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String folder() {
    return facet("psfolder");
  }

  /**
   * Set the facet "psdocumenttype" to the specified PSML document type.
   *
   * @param mediatype The value of the document type facet.
   *
   * @return A new <code>GenericSearch</code> instance including the specified document type facet.
   */
  public GenericSearch documentType(String documenttype) {
    return facet("psdocumenttype", documenttype);
  }

  /**
   * @return the value of the document type facet or <code>null</code> if that facet isn't defined.
   */
  public @Nullable String documentType() {
    return facet("psdocumenttype");
  }

  // Date range search
  // --------------------------------------------------------------------------

  /**
   * Set the lower range for a date range search on the modified date.
   *
   * @param from The lower range for a date range search on the modified date.
   *
   *@return A new <code>GenericSearch</code> instance with the updated date range.
   */
  public GenericSearch from(LocalDateTime from) {
    if (from.equals(this._from)) return this;
    return new GenericSearch(this._question, this._types, this._page, this._pageSize, this._groups, this._facets, from, this._to, this._sortBy);
  }

  /**
   * Set the upper range for a date range search on the modified date.
   *
   * @param to The upper range for a date range search on the modified date.
   *
   * @return A new <code>GenericSearch</code> instance with the updated date range.
   */
  public GenericSearch to(LocalDateTime to) {
    if (to.equals(this._to)) return this;
    return new GenericSearch(this._question, this._types, this._page, this._pageSize, this._groups, this._facets, this._from, to, this._sortBy);
  }

  /**
   * Set the upper range for a date range search on the modified date.
   *
   * @param from The lower range value.
   * @param to   The upper range value.
   *
   * @return A new <code>GenericSearch</code> instance with the updated date range.
   */
  public GenericSearch between(LocalDateTime from, LocalDateTime to) {
    if (from.equals(this._from) && to.equals(this._to)) return this;
    return new GenericSearch(this._question, this._types, this._page, this._pageSize, this._groups, this._facets, from, to, this._sortBy);
  }

  // Sorting
  // --------------------------------------------------------------------------

  /**
   * Define how the results should be sorted
   *
   * @return A new <code>GenericSearch</code> instance with the updated sorting.
   */
  public GenericSearch sortBy(String field) {
    return sortBy(new SortBy(field));
  }

  /**
   * Define how the results should be sorted
   *
   * @return A new <code>GenericSearch</code> instance with the updated sorting.
   */
  public GenericSearch sortBy(SortBy sortBy) {
    return new GenericSearch(this._question, this._types, this._page, this._pageSize, this._groups, this._facets, this._from, this._to, sortBy);
  }

  /**
   * @return How the results should be sorted.
   */
  public SortBy sortBy() {
    return this._sortBy;
  }

  /**
   * Build the parameter map for a generic search.
   *
   * @return This predicate a as valid parameter for the Generic Search Servlet in PageSeeder.
   */
  public Map<String, String> toParameters() {
    Map<String, String> parameters = new LinkedHashMap<>();
    String q = this._question;
    if (q != null) {
      parameters.put("question", q);
    }
    parameters.put("groups", join(this._groups, ','));
    if (this._types.size() > 0) {
      parameters.put("types",  join(this._types, ','));
    }
    if (this._from != LocalDateTime.MIN) {
      parameters.put("from", format(this._from));
    }
    if (this._to != LocalDateTime.MAX) {
      parameters.put("to", format(this._to));
    }
    if (!this._facets.isEmpty()) {
      StringBuilder select = new StringBuilder();
      for (Entry<String, String> f : this._facets.entrySet()) {
        if (select.length() > 0) {
          select.append(',');
        }
        String value = f.getValue().replaceAll(",", "\\,");
        select.append(f.getKey()).append(':').append(value);
      }
      parameters.put("select", select.toString());
    }
    // Paging
    if (this._page > 1) {
      parameters.put("page", Integer.toString(this._page));
    }
    parameters.put("page-size", Integer.toString(this._pageSize));
    if (!this._sortBy.equals(SortBy.RELEVANCE)) {
      parameters.put("sortby", this._sortBy._field);
    }
    return parameters;
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    if (this._types.size() > 0) {
      s.append("types=").append(join(this._types, '.')).append(';');
    }
    if (!this._facets.isEmpty()) {
      for (Entry<String, String> f : this._facets.entrySet()) {
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
    return new Request(Method.GET, Servlet.GENERIC_SEARCH)
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

  /**
   * Defines a sorting criteria.
   */
  public static final class SortBy {

    /**
     * Return results by relevance (most relevant first)
     */
    public static final SortBy RELEVANCE = new SortBy("");

    /**
     * Return results by modified date (most recent first)
     */
    public static final SortBy MODIFIED_DATE= new SortBy("psmodifieddate-numeric");

    /**
     * Return results by title (alphabetical)
     */
    public static final SortBy TITLE = new SortBy("pstitle-sort");

    /**
     * The field to sort on.
     */
    private final String _field;

    public SortBy(String field) {
      this._field = Objects.requireNonNull(field);
    }

    public String field() {
      return this._field;
    }

    @Override
    public int hashCode() {
      return  this._field.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object o) {
      if (this == o) return true;
      if (o == null) return false;
      if (getClass() != o.getClass()) return false;
      SortBy other = (SortBy)o;
      return this._field.equals(other._field);
    }

    @Override
    public String toString() {
      return this._field;
    }
  }

}
