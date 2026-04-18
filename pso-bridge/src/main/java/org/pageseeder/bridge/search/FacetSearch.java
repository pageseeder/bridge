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

import org.jspecify.annotations.Nullable;
import org.pageseeder.bridge.http.ServicePath;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A convenience class to make requests to the search service to extract facet values.
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class FacetSearch extends BasicSearch<FacetSearch> implements Serializable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * The question
   */
  private final Question question;

  /**
   * A list of facets to compute
   */
  private final FacetList facets;

  /**
   * A list of field:term pairs to use as filters
   */
  private final FilterList filters;

  /**
   * List of range searches.
   */
  private final RangeFilterList ranges;

  public FacetSearch() {
    this(Scope.EMPTY, Question.EMPTY, FacetList.EMPTY, FilterList.EMPTY, RangeFilterList.EMPTY);
  }

  // DO not make this constructor public as it takes the raw parameters without ensuring that lists and maps are unmodifiable
  private FacetSearch(Scope scope, Question question, FacetList facets, FilterList filters, RangeFilterList ranges) {
    super(scope);
    this.question = question;
    this.facets = facets;
    this.filters = filters;
    this.ranges = ranges;
  }

  /**
   * Create a new GenericSearch with the specified question (for full-text searches)
   *
   * @param question The question.
   *
   * @return A new <code>QuestionSearch</code> instance.
   */
  public FacetSearch question(String question) {
    Question q = new Question(question, this.question.fields(), this.question.suggestSize());
    return new FacetSearch(this.scope, q, this.facets, this.filters, this.ranges);
  }

  /**
   * @return the question if any; <code>null</code> otherwise.
   */
  public @Nullable String question() {
    return this.question.question();
  }


  /**
   * Sets the facets to use in this search
   *
   * @param facets The facets to use in this search
   *
   * @return A new <code>FacetSearch</code> instance with the specified facet.
   */
  public FacetSearch facets(FacetList facets) {
    if (facets == null) facets = FacetList.EMPTY;
    return new FacetSearch(this.scope, this.question, facets, this.filters, this.ranges);
  }

  /**
   * Sets the filters to use in this search
   *
   * @param filters The filters to use in this search
   *
   * @return A new <code>FacetSearch</code> instance with the specified filter.
   */
  public FacetSearch filters(FilterList filters) {
    return new FacetSearch(this.scope, this.question, this.facets, filters, this.ranges);
  }

  public FilterList filters() {
    return this.filters;
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
  public FacetSearch range(String field, Range range) {
    RangeFilterList updatedRanges = this.ranges.filter(field, range);
    return new FacetSearch(this.scope, this.question, this.facets, this.filters, updatedRanges);
  }

  @Override
  public FacetSearch group(String group) {
    return new FacetSearch(this.scope.group(group), this.question, this.facets, this.filters, this.ranges);
  }

  @Override
  public FacetSearch project(String project) {
    return new FacetSearch(this.scope.project(project), this.question, this.facets, this.filters, this.ranges);
  }

  @Override
  public FacetSearch project(String project, List<String> groups) {
    return new FacetSearch(this.scope.project(project, groups), this.question, this.facets, this.filters, this.ranges);
  }

  @Override
  public FacetSearch member(String member) {
    return new FacetSearch(this.scope.member(member), this.question, this.facets, this.filters, this.ranges);
  }

  /**
   * Build the parameter map for a generic search.
   *
   * @return This predicate a as valid parameter for the QuestionSearch service in PageSeeder.
   */
  @Override
  public Map<String, String> toParameters() {
    Map<String, String> parameters = new LinkedHashMap<>();
    parameters = this.question.toParameters(parameters);
    parameters = this.facets.toParameters(parameters);
    parameters = this.filters.toParameters(parameters);
    parameters = this.ranges.toParameters(parameters);
    if (this.scope.isProject() && !this.scope.groups().isEmpty())
      parameters.put("groups", Search.join(this.scope.groups(), ','));
    return parameters;
  }

  @Override
  public String toString() {
    return this.question.toString() + this.filters.toString() + this.ranges.toString();
  }

  @Override
  public String service() {
    this.scope.checkReady();
    if (this.scope.isProject())
      return ServicePath.newPath("/members/{member}/projects/{project}/facets", this.scope.member(), this.scope.name());
    else
      return ServicePath.newPath("/groups/{group}/search/facets", this.scope.name());
  }

}
