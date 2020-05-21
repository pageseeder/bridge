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
import org.pageseeder.bridge.http.ServicePath;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A convenience class to make requests to the search service.
 *
 * @see <a href="https://dev.pageseeder.com/api/web_services/services/group-search-GET.html">/groups/{group}/search [GET]</a>
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class QuestionSearch extends BasicSearch<QuestionSearch> implements Serializable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * The question
   */
  private final Question _question;

  /**
   * A list of field:term pairs to use as filters	no	strings
   */
  private final FacetList _facets;

  /**
   * A list of field:term pairs to use as filters	no	strings
   */
  private final FilterList _filters;

  /**
   * List of range searches.
   */
  private final RangeFilterList _ranges;

  /**
   * Requested page
   */
  private final Page _page;

  /**
   * A comma-separated list of fields to sort the results	no	strings
   */
  private final FieldList _sortFields;

  public QuestionSearch() {
    this(Scope.EMPTY, Question.EMPTY, FacetList.EMPTY, FilterList.EMPTY, RangeFilterList.EMPTY, Page.DEFAULT_PAGE, FieldList.EMPTY);
  }

  // DO not make this constructor public as it takes the raw parameters without ensuring that lists and maps are unmodifiable
  private QuestionSearch(Scope scope, Question question, FacetList facets, FilterList filters, RangeFilterList ranges,
                         Page page, FieldList sortFields) {
    super(scope);
    this._question = question;
    this._facets = facets;
    this._filters = filters;
    this._ranges = ranges;
    this._page = page;
    this._sortFields = sortFields;
  }

  /**
   * Create a new GenericSearch with the specified question (for full-text searches)
   *
   * @param question The question.
   *
   * @return A new <code>QuestionSearch</code> instance.
   */
  public QuestionSearch question(String question) {
    return question(new Question(question, this._question.fields(), this._question.suggestSize()));
  }

  /**
   * Create a new GenericSearch with the specified question (for full-text searches)
   *
   * @param question The question.
   *
   * @return A new <code>QuestionSearch</code> instance.
   */
  public QuestionSearch question(Question question) {
    return new QuestionSearch(this._scope, question, this._facets, this._filters, this._ranges, this._page, this._sortFields);
  }

  /**
   * @return the question if any; <code>null</code> otherwise.
   */
  public @Nullable String question() {
    return this._question.question();
  }

  /**
   * @param page the page to set
   *
   * @throws IndexOutOfBoundsException if the page is zero or negative.
   *
   * @return A new <code>QuestionSearch</code> instance unless the current instance already has the same page.
   */
  public QuestionSearch page(Page page) {
    return new QuestionSearch(this._scope, this._question, this._facets, this._filters, this._ranges, page, this._sortFields);
  }

  /**
   * @param page the page to set
   *
   * @throws IndexOutOfBoundsException if the page is zero or negative.
   *
   * @return A new <code>QuestionSearch</code> instance unless the current instance already has the same page.
   */
  public QuestionSearch page(int page) {
    return page (this._page.number(page));
  }

  /**
   * @param pageSize the number of results per page
   *
   * @return A new <code>QuestionSearch</code> instance unless the current instance already has the same number of results per page.
   */
  public QuestionSearch pageSize(int pageSize) {
    return page(this._page.size(pageSize));
  }

  /**
   * @return the page
   */
  public Page page() {
    return this._page;
  }

  /**
   * Sets the filters to use in this search
   *
   * @param filters The filters to use in this search
   *
   * @return A new <code>QuestionSearch</code> instance with the specified filters.
   */
  public QuestionSearch filters(FilterList filters) {
    return new QuestionSearch(this._scope, this._question, this._facets, filters, this._ranges, this._page, this._sortFields);
  }

  /**
   * Adds a single filter to use in this search
   *
   * @param filter The filter to add to this search
   *
   * @return A new <code>QuestionSearch</code> instance with this additional specified filter.
   */
  public QuestionSearch filter(Filter filter) {
    return filters(this._filters.plus(filter));
  }

  /**
   * Adds a single filter to use in this search with DEFAULT Occur.
   *
   * @param field The field name of the filter to add
   * @param value The value of the field to add
   *
   * @return A new <code>QuestionSearch</code> instance with this additional specified filter.
   */
  public QuestionSearch filter(String field, String value) {
    return filters(this._filters.plus(new Filter(field, value)));
  }

  /**
   * Adds a single filter to use in this search with DEFAULT Occur.
   *
   * @param field The field name of the filter to add
   * @param value The value of the field to add
   * @param occur the requirement on that filter
   *
   * @return A new <code>QuestionSearch</code> instance with this additional specified filter.
   */
  public QuestionSearch filter(String field, String value, Filter.Occur occur) {
    return filters(this._filters.plus(new Filter(field, value, occur)));
  }

  /**
   * @return The filter list
   */
  public FilterList filters() {
    return this._filters;
  }


  /**
   * Sets the facets to use in this search
   *
   * @param facets The facets to use in this search
   *
   * @return A new <code>QuestionSearch</code> instance with the specified facets.
   */
  public QuestionSearch facets(FacetList facets) {
    return new QuestionSearch(this._scope, this._question, facets, this._filters, this._ranges, this._page, this._sortFields);
  }


  /**
   * Adds a single facet to use in this search
   *
   * @param facet The facet to add to this search
   *
   * @return A new <code>QuestionSearch</code> instance with this additional specified facet.
   */
  public QuestionSearch facet(Facet facet) {
    return facets(this._facets.facet(facet));
  }

  /**
   * @return The filter list
   */
  public FacetList facets() {
    return this._facets;
  }


  /**
   * Add a filter for the specified index field.
   *
   * <p>This method can be used to add multiple filters, however there can only be one
   * range per field.
   *
   * @param field The name of the index field
   * @param range The range for that field
   *
   * @return A new <code>QuestionSearch</code> instance including the specified facet.
   */
  public QuestionSearch range(String field, Range range) {
    RangeFilterList ranges = this._ranges.filter(field, range);
    return new QuestionSearch(this._scope, this._question, this._facets, this._filters, ranges, this._page, this._sortFields);
  }

  // Date range search
  // --------------------------------------------------------------------------

//  /**
//   * Set the lower range for a date range search on the modified date.
//   *
//   * @param from The lower range for a date range search on the modified date.
//   *
//   *@return A new <code>QuestionSearch</code> instance with the updated date range.
//   */
//  public QuestionSearch from(LocalDateTime from) {
//
//    return new QuestionSearch(this._question, this._types, this._page, this._pageSize, this._groups, this._facets, from, this._to, this._sortBy);
//  }
//
//  /**
//   * Set the upper range for a date range search on the modified date.
//   *
//   * @param to The upper range for a date range search on the modified date.
//   *
//   * @return A new <code>QuestionSearch</code> instance with the updated date range.
//   */
//  public QuestionSearch to(LocalDateTime to) {
//
//    return new QuestionSearch(this._question, this._types, this._page, this._pageSize, this._groups, this._facets, this._from, to, this._sortBy);
//  }
//
//  /**
//   * Set the upper range for a date range search on the modified date.
//   *
//   * @param from The lower range value.
//   * @param to   The upper range value.
//   *
//   * @return A new <code>QuestionSearch</code> instance with the updated date range.
//   */
//  public QuestionSearch between(LocalDateTime from, LocalDateTime to) {
//    if (from.equals(this._from) && to.equals(this._to)) return this;
//    return new QuestionSearch(this._question, this._types, this._page, this._pageSize, this._groups, this._facets, from, to, this._sortBy);
//  }
//
//  // Sorting
//  // --------------------------------------------------------------------------

  /**
   * Define how the results should be sorted
   *
   * @return A new <code>QuestionSearch</code> instance with the updated sorting.
   */
  public QuestionSearch sortFields(String... fields) {
    FieldList sortFields = FieldList.newList(fields);
    return new QuestionSearch(this._scope, this._question, this._facets, this._filters, this._ranges, this._page, sortFields);
  }

  /**
   * Define how the results should be sorted
   *
   * @return A new <code>QuestionSearch</code> instance with the updated sorting.
   */
  public QuestionSearch sortFields(FieldList sortFields) {
    return new QuestionSearch(this._scope, this._question, this._facets, this._filters, this._ranges, this._page, sortFields);
  }

  @Override
  public QuestionSearch group(String group) {
    return new QuestionSearch(this._scope.group(group), this._question, this._facets, this._filters, this._ranges, this._page, this._sortFields);
  }

  @Override
  public QuestionSearch project(String project) {
    return new QuestionSearch(this._scope.project(project), this._question, this._facets, this._filters, this._ranges, this._page, this._sortFields);
  }

  @Override
  public QuestionSearch project(String project, List<String> groups) {
    return new QuestionSearch(this._scope.project(project, groups), this._question, this._facets, this._filters, this._ranges, this._page, this._sortFields);
  }

  @Override
  public QuestionSearch member(String member) {
    return new QuestionSearch(this._scope.member(member), this._question, this._facets, this._filters, this._ranges, this._page, this._sortFields);
  }

  /**
   * Build the parameter map for a generic search.
   *
   * @return This predicate a as valid parameter for the QuestionSearch service in PageSeeder.
   */
  @Override
  public Map<String, String> toParameters() {
    Map<String, String> parameters = new LinkedHashMap<>();
    parameters = this._question.toParameters(parameters);
    parameters = this._facets.toParameters(parameters);
    parameters = this._filters.toParameters(parameters);
    parameters = this._ranges.toParameters(parameters);
    parameters = this._page.toParameters(parameters);
    if (this._scope.isProject() && this._scope.groups().size() > 0)
      parameters.put("groups", Search.join(this._scope.groups(), ','));
    if (!this._sortFields.isEmpty()) {
      parameters.put("sortfields",this._sortFields.toString());
    }
    return parameters;
  }

  @Override
  public String toString() {
    return this._question.toString() + this._filters.toString() + this._ranges.toString();
  }

  @Override
  public String service() {
    this._scope.checkReady();
    if (this._scope.isProject())
      return ServicePath.newPath("/members/{member}/projects/{project}/search", this._scope.member(), this._scope.name());
    else
      return ServicePath.newPath("/groups/{group}/search", this._scope.name());
  }
}
