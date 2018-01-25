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

import org.pageseeder.bridge.http.ServicePath;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A convenience class to make requests to the search service using a Lucene predicate.
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class PredicateSearch extends BasicSearch<PredicateSearch> implements Serializable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * The predicate
   */
  private final Predicate _predicate;

  /**
   * A list of field:term pairs to use as filters	no	strings
   */
  private final FacetList _facets;

  /**
   * Requested page
   */
  private final Page _page;

  /**
   * A comma-separated list of fields to sort the results	no	strings
   */
  private final FieldList _sortFields;

  public PredicateSearch(Scope scope) {
    this(scope, Predicate.EMPTY, FacetList.EMPTY, Page.DEFAULT_PAGE, FieldList.EMPTY);
  }

  // DO not make this constructor public as it takes the raw parameters without ensuring that lists and maps are unmodifiable
  private PredicateSearch(Scope scope, Predicate predicate, FacetList facets, Page page, FieldList sortFields) {
    super(scope);
    this._predicate = predicate;
    this._facets = facets;
    this._page = page;
    this._sortFields = sortFields;
  }

  /**
   * Create a new GenericSearch with the specified question (for full-text searches)
   *
   * @param predicate The predicate.
   *
   * @return A new <code>PredicateSearch</code> instance.
   */
  public PredicateSearch predicate(String predicate) {
    Predicate q = new Predicate(predicate, this._predicate.defaultField());
    return new PredicateSearch(this._scope, q, this._facets, this._page, this._sortFields);
  }

  /**
   * @return the predicate
   */
  public String predicate() {
    return this._predicate.predicate();
  }

  @Override
  public PredicateSearch group(String group) {
    return new PredicateSearch(Scope.group(group), this._predicate, this._facets, this._page, this._sortFields);
  }

  @Override
  public PredicateSearch project(String project) {
    return new PredicateSearch(Scope.project(project), this._predicate, this._facets, this._page, this._sortFields);
  }

  @Override
  public PredicateSearch project(String project, List<String> groups) {
    return new PredicateSearch(Scope.project(project, groups), this._predicate, this._facets, this._page, this._sortFields);
  }

  /**
   * @param page the page to set
   *
   * @throws IndexOutOfBoundsException if the page is zero or negative.
   *
   * @return A new <code>QuestionSearch</code> instance unless the current instance already has the same page.
   */
  public PredicateSearch page(int page) {
    Page p = this._page.number(page);
    return new PredicateSearch(this._scope, this._predicate, this._facets, p, this._sortFields);
  }

  /**
   * @param pageSize the number of results per page
   *
   * @return A new <code>QuestionSearch</code> instance unless the current instance already has the same number of results per page.
   */
  public PredicateSearch pageSize(int pageSize) {
    Page p = this._page.number(pageSize);
    return new PredicateSearch(this._scope, this._predicate, this._facets, p, this._sortFields);
  }

  /**
   * @return the page
   */
  public Page page() {
    return this._page;
  }

  /**
   * Define how the results should be sorted
   *
   * @return A new <code>QuestionSearch</code> instance with the updated sorting.
   */
  public PredicateSearch sortFields(String... fields) {
    FieldList sortFields = FieldList.newList(fields);
    return new PredicateSearch(this._scope, this._predicate, this._facets, this._page, sortFields);
  }

  /**
   * Define how the results should be sorted
   *
   * @return A new <code>QuestionSearch</code> instance with the updated sorting.
   */
  public PredicateSearch sortFields(FieldList sortFields) {
    return new PredicateSearch(this._scope, this._predicate, this._facets, this._page, sortFields);
  }

  /**
   * Build the parameter map for a generic search.
   *
   * @return This predicate a as valid parameter for the QuestionSearch service in PageSeeder.
   */
  public Map<String, String> toParameters() {
    Map<String, String> parameters = new LinkedHashMap<>();
    parameters = this._predicate.toParameters(parameters);
    parameters = this._facets.toParameters(parameters);
    parameters = this._page.toParameters(parameters);
    if (this._scope.isProject() && this._scope.getGroups().size() > 0)
      parameters.put("groups", Search.join(this._scope.getGroups(), ','));
    if (!_sortFields.isEmpty()) {
      parameters.put("sortfields",this._sortFields.toString());
    }
    return parameters;
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    // TODO
    s.append(this._predicate.toString());
    return s.toString();
  }


  public String service() {
    if (this._scope.isProject())
      return ServicePath.newPath("/project/{project}/search/predicate", this._scope.getName());
    else
      return ServicePath.newPath("/groups/{group}/search/predicate", this._scope.getName());
  }
}
