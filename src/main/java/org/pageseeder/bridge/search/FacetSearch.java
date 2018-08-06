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
  private final Question _question;

  /**
   * A list of facets to compute
   */
  private final FacetList _facets;

  /**
   * A list of field:term pairs to use as filters
   */
  private final FilterList _filters;

  /**
   * List of range searches.
   */
  private final RangeFilterList _ranges;

  public FacetSearch() {
    this(Scope.EMPTY, Question.EMPTY, FacetList.EMPTY, FilterList.EMPTY, RangeFilterList.EMPTY);
  }

  // DO not make this constructor public as it takes the raw parameters without ensuring that lists and maps are unmodifiable
  private FacetSearch(Scope scope, Question question, FacetList facets, FilterList filters, RangeFilterList ranges) {
    super(scope);
    this._question = question;
    this._facets = facets;
    this._filters = filters;
    this._ranges = ranges;
  }

  /**
   * Create a new GenericSearch with the specified question (for full-text searches)
   *
   * @param question The question.
   *
   * @return A new <code>QuestionSearch</code> instance.
   */
  public FacetSearch question(String question) {
    Question q = new Question(question, this._question.fields(), this._question.suggestSize());
    return new FacetSearch(this._scope, q, this._facets, this._filters, this._ranges);
  }

  /**
   * @return the question if any; <code>null</code> otherwise.
   */
  public @Nullable String question() {
    return this._question.question();
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
    return new FacetSearch(this._scope, this._question, facets, this._filters, this._ranges);
  }
  
  /**
   * Sets the filters to use in this search
   *
   * @param filters The filters to use in this search
   *
   * @return A new <code>FacetSearch</code> instance with the specified filter.
   */
  public FacetSearch filters(FilterList filters) {
    return new FacetSearch(this._scope, this._question, this._facets, filters, this._ranges);
  }

  public FilterList filters() {
    return this._filters;
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
    RangeFilterList ranges = this._ranges.filter(field, range);
    return new FacetSearch(this._scope, this._question, this._facets, this._filters, ranges);
  }

  @Override
  public FacetSearch group(String group) {
    return new FacetSearch(this._scope.group(group), this._question, this._facets, this._filters, this._ranges);
  }

  @Override
  public FacetSearch project(String project) {
    return new FacetSearch(this._scope.project(project), this._question, this._facets, this._filters, this._ranges);
  }

  @Override
  public FacetSearch project(String project, List<String> groups) {
    return new FacetSearch(this._scope.project(project, groups), this._question, this._facets, this._filters, this._ranges);
  }

  @Override
  public FacetSearch member(String member) {
    return new FacetSearch(this._scope.member(member), this._question, this._facets, this._filters, this._ranges);
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
    if (this._scope.isProject() && this._scope.groups().size() > 0)
      parameters.put("groups", Search.join(this._scope.groups(), ','));
    return parameters;
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    // TODO
    s.append(this._question.toString());
    s.append(this._filters.toString());
    s.append(this._ranges.toString());
    return s.toString();
  }

  @Override
  public String service() {
    this._scope.checkReady();
    if (this._scope.isProject())
      return ServicePath.newPath("/members/{member}/projects/{project}/facets", this._scope.member(), this._scope.name());
    else
      return ServicePath.newPath("/groups/{group}/search/facets", this._scope.name());
  }

}
