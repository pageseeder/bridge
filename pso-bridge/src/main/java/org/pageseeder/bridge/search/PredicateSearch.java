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
  private final Predicate predicate;

  /**
   * A list of field:term pairs to use as filters	no	strings
   */
  private final FacetList facets;

  /**
   * Requested page
   */
  private final Page page;

  /**
   * A comma-separated list of fields to sort the results	no	strings
   */
  private final FieldList sortFields;

  public PredicateSearch(Scope scope) {
    this(scope, Predicate.EMPTY, FacetList.EMPTY, Page.DEFAULT_PAGE, FieldList.EMPTY);
  }

  // DO not make this constructor public as it takes the raw parameters without ensuring that lists and maps are unmodifiable
  private PredicateSearch(Scope scope, Predicate predicate, FacetList facets, Page page, FieldList sortFields) {
    super(scope);
    this.predicate = predicate;
    this.facets = facets;
    this.page = page;
    this.sortFields = sortFields;
  }

  /**
   * Create a new GenericSearch with the specified question (for full-text searches)
   *
   * @param predicate The predicate.
   *
   * @return A new <code>PredicateSearch</code> instance.
   */
  public PredicateSearch predicate(String predicate) {
    Predicate q = new Predicate(predicate, this.predicate.defaultField());
    return new PredicateSearch(this.scope, q, this.facets, this.page, this.sortFields);
  }

  /**
   * @return the predicate
   */
  public String predicate() {
    return this.predicate.predicate();
  }

  @Override
  public PredicateSearch group(String group) {
    return new PredicateSearch(this.scope.group(group), this.predicate, this.facets, this.page, this.sortFields);
  }

  @Override
  public PredicateSearch project(String project) {
    return new PredicateSearch(this.scope.project(project), this.predicate, this.facets, this.page, this.sortFields);
  }

  @Override
  public PredicateSearch project(String project, List<String> groups) {
    return new PredicateSearch(this.scope.project(project, groups), this.predicate, this.facets, this.page, this.sortFields);
  }

  @Override
  public PredicateSearch member(String member) {
    return new PredicateSearch(this.scope.member(member), this.predicate, this.facets, this.page, this.sortFields);
  }

  /**
   * @param page the page to set
   *
   * @throws IndexOutOfBoundsException if the page is zero or negative.
   *
   * @return A new <code>QuestionSearch</code> instance unless the current instance already has the same page.
   */
  public PredicateSearch page(int page) {
    Page p = this.page.number(page);
    return new PredicateSearch(this.scope, this.predicate, this.facets, p, this.sortFields);
  }

  /**
   * @param pageSize the number of results per page
   *
   * @return A new <code>QuestionSearch</code> instance unless the current instance already has the same number of results per page.
   */
  public PredicateSearch pageSize(int pageSize) {
    Page p = this.page.size(pageSize);
    return new PredicateSearch(this.scope, this.predicate, this.facets, p, this.sortFields);
  }

  /**
   * @return the page
   */
  public Page page() {
    return this.page;
  }

  /**
   * Define how the results should be sorted
   *
   * @return A new <code>QuestionSearch</code> instance with the updated sorting.
   */
  public PredicateSearch sortFields(String... fields) {
    FieldList sortFields = FieldList.newList(fields);
    return new PredicateSearch(this.scope, this.predicate, this.facets, this.page, sortFields);
  }

  /**
   * Define how the results should be sorted
   *
   * @return A new <code>QuestionSearch</code> instance with the updated sorting.
   */
  public PredicateSearch sortFields(FieldList sortFields) {
    return new PredicateSearch(this.scope, this.predicate, this.facets, this.page, sortFields);
  }

  /**
   * Build the parameter map for a generic search.
   *
   * @return This predicate a as valid parameter for the QuestionSearch service in PageSeeder.
   */
  public Map<String, String> toParameters() {
    Map<String, String> parameters = new LinkedHashMap<>();
    parameters = this.predicate.toParameters(parameters);
    parameters = this.facets.toParameters(parameters);
    parameters = this.page.toParameters(parameters);
    if (this.scope.isProject() && this.scope.groups().size() > 0)
      parameters.put("groups", Search.join(this.scope.groups(), ','));
    if (!sortFields.isEmpty()) {
      parameters.put("sortfields",this.sortFields.toString());
    }
    return parameters;
  }

  @Override
  public String toString() {
    return this.predicate.toString();
  }

  public String service() {
    this.scope.checkReady();
    if (this.scope.isProject())
      return ServicePath.newPath("/members/{member}/projects/{project}/predicate", this.scope.member(), this.scope.name());
    else
      return ServicePath.newPath("/groups/{group}/search/predicate", this.scope.name());
  }
}
