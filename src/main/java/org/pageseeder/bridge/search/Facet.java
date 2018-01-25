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

import java.util.Objects;

/**
 * A facet to be computed and returned in the results.
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Facet {

  private final String _definition;
  private final boolean _flexible;

  /**
   * The facet
   *
   * @param definition The name of the field or facet definition (range or interval)
   * @param flexible   <code>true</code> if the facet should be a flexible facet.
   */
  public Facet(String definition, boolean flexible) {
    this._definition = Objects.requireNonNull(definition);
    this._flexible = flexible;
  }

  /**
   *
   * @param flexible
   * @return A new facet with the same definition.
   */
  public Facet flexible(boolean flexible) {
    return new Facet(this._definition, flexible);
  }

  /**
   * @return The definition of the facet
   */
  public String definition() {
    return this._definition;
  }

  /**
   * Return the name of the field for this facet.
   *
   * <p>If the definition of the facet includes a colon it is the substring that precedes the colon.</p>
   *
   * @return The name of the field for the facet
   */
  public String field() {
    int colon = this._definition.indexOf(':');
    return colon < 0? this._definition : this._definition.substring(0, colon);
  }

  /**
   * @return <code>true</code> if the facet should be a flexible facet.
   */
  public boolean isFlexible() {
    return this._flexible;
  }

  // Experimental facets
  // ---------------------------------------------------------------------------------------------

  public static Facet newRangeFacet(String field, String... values) {
    return newRangeFacet(field, true, true, values);
  }

  public static Facet newRangeFacet(String field, boolean minInclusive, boolean maxInclusive, String... values) {
    String definition = field+':'+(minInclusive? '[' : '{')+String.join(",", values)+(maxInclusive? ']' : '}');
    return new Facet(definition, false);
  }

  public static Facet newIntervalFacet(String field, boolean minInclusive, boolean maxInclusive, String from, String to, String interval) {
    String definition = field+':'+(minInclusive? '[' : '{')+from+";"+to+"|"+interval+(maxInclusive? ']' : '}');
    return new Facet(definition, false);
  }



}