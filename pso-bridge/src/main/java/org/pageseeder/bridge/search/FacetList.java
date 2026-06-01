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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An immutable list of facets to use in a request.
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public class FacetList extends ImmutableList<Facet> implements Iterable<Facet> {

  /**
   * An empty list of facets.
   */
  public static final FacetList EMPTY = new FacetList(List.of(), -1);

  /**
   * The max number of facet values to load (max 1000).
   */
  private final int facetSize;

  private FacetList(List<Facet> facets, int facetSize) {
    super(facets);
    this.facetSize = facetSize;
  }

  /**
   * Create a new facet list.
   *
   * @param fields A array of field names
   *
   * @return An instance using corresponding list of field names
   */
  public static FacetList newFacetList(String... fields) {
    List<Facet> facets = Arrays.stream(fields).map(f -> new Facet(f, false)).collect(Collectors.toList());
    return new FacetList(facets, -1);
  }

  /**
   * Create a new flexible facet list.
   *
   * @param fields A array of field names
   *
   * @return An instance using corresponding list of field names
   */
  public static FacetList newFlexibleFacetList(String... fields) {
    List<Facet> facets = Arrays.stream(fields).map(f -> new Facet(f, true)).collect(Collectors.toList());
    return new FacetList(facets, -1);
  }

  /**
   * Add a facet to compute and be returns in the results.
   *
   * <p>This method can be used to add multiple facets, however there should only be one
   * facet per field.
   *
   * @param field The name of the index field
   * @param flexible The value it should match (for that field)
   *
   * @return A new <code>FacetList</code> instance including the specified facet.
   */
  public FacetList facet(String field, boolean flexible) {
    List<Facet> facets = this.list;
    if (!facets.isEmpty()) facets = minus(facets, f -> f.field().equals(field));
    facets = plus(facets, new Facet(field, flexible));
    return new FacetList(facets, this.facetSize);
  }


  /**
   * Create a new facet list with the specified facet.
   *
   * @param facet The facet to add to this facet list
   *
   * @return A new <code>FacetList</code> instance including the specified facet.
   */
  public FacetList facet(Facet facet) {
    List<Facet> facets = plus(this.list, facet);
    return new FacetList(facets, this.facetSize);
  }

  /**
   * @param facetSize the max number of facet values to load (max 1000)
   *
   * @return A new <code>FacetList</code> instance unless the current instance already has the same facet size.
   */
  public FacetList facetSize(int facetSize) {
    if (facetSize == this.facetSize) return this;
    return new FacetList(this.list, facetSize);
  }

  /**
   * @return The max number of facet values to load (max 1000)
   */
  public int facetSize() {
    return this.facetSize;
  }

  /**
   * Update the specified parameters include the facets in this object.
   *
   * <p>More specifically, this class adds the following parameters:</p>
   * <ul>
   *   <li><code>facets</code></li>
   *   <li><code>flexiblefacets</code></li>
   * </ul>
   *
   * @param parameters The parameters to send to the search service
   * @return The updated parameters
   */
  public Map<String, String> toParameters(Map<String, String> parameters) {
    // Facets
    if (!isEmpty()) {
      String facets = this.list.stream().filter(f -> !f.isFlexible()).map(Facet::field).collect(Collectors.joining(","));
      if (!facets.isEmpty())
        parameters.put("facets", facets);
      String flexible = this.list.stream().filter(Facet::isFlexible).map(Facet::field).collect(Collectors.joining(","));
      if (!flexible.isEmpty())
        parameters.put("flexiblefacets", flexible);
    }
    if (this.facetSize > 0) parameters.put("facetsize", String.valueOf(this.facetSize));
    return parameters;
  }

}
