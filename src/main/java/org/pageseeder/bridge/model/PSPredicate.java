/*
 * Copyright 2015 Allette Systems (Australia)
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
package org.pageseeder.bridge.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.util.ISO8601;

/**
 * A predicate for a search.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public final class PSPredicate implements Serializable {

  /** As per recommendation */
  private static final long serialVersionUID = 2L;

  /**
   * The type of search result requested.
   */
  private @Nullable String type = null;

  /**
   * Facets.
   */
  private Map<String, String> facets = new HashMap<>();

  /**
   * Facets.
   */
  private int page = 1;

  /**
   * Facets.
   */
  private int pageSize = 100;

  /**
   */
  private long from = Long.MIN_VALUE;

  /** */
  private long to = Long.MAX_VALUE;

  /** */
  private @Nullable String sortBy = null;


  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @return the type
   */
  public @Nullable String getType() {
    return this.type;
  }

  /**
   * @param page the page to set
   *
   * @throws IndexOutOfBoundsException if the page is zero or negative.
   */
  public void setPage(int page) {
    if (page <= 0) throw new IndexOutOfBoundsException("page must be greater than 0");
    this.page = page;
  }

  /**
   * @return the page
   */
  public int getPage() {
    return this.page;
  }

  /**
   * @param pageSize the pageSize to set
   */
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * @return the pageSize
   */
  public int getPageSize() {
    return this.pageSize;
  }

  /**
   * Add a facet for the specified index field.
   *
   * @param field The name of the index field
   * @param value The value it should match (for that facet)
   */
  public void addFacet(String field, String value) {
    this.facets.put(field, value);
  }

  public void setFrom(long from) {
    this.from = from;
  }

  public void setTo(long to) {
    this.to = to;
  }

  public void setFrom(Date from) {
    this.from = from != null? from.getTime() : Long.MIN_VALUE;
  }

  public void setTo(Date to) {
    this.to = to != null? to.getTime() :Long.MAX_VALUE;
  }

  public void setBetween(Date from, Date to) {
    setFrom(from);
    setTo(to);
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public @Nullable String getSortBy() {
    return this.sortBy;
  }

  /**
   * Add a facet for the specified property.
   *
   * <p>This method is a shorthand for <code>addFacet("psproperty-"+property, value)</code>.
   *
   * @see #addFacet(String, String)
   *
   * @param property The name of the PSML property
   * @param value    The value it should match (for that facet)
   */
  public void addPropertyFacet(String property, String value) {
    this.facets.put("psproperty-"+property, value);
  }

  /**
   *
   * @return This predicate a as valid parameter for the Generic Search Servlet in PageSeeder.
   */
  public Map<String, String> toParameters() {
    Map<String, String> parameters = new HashMap<>();
    String t = this.type;
    if (t != null) {
      parameters.put("types", t);
    }
    if (this.from != Long.MIN_VALUE) {
      parameters.put("from", ISO8601.DATETIME.format(this.from));
    }
    if (this.to != Long.MAX_VALUE) {
      parameters.put("to", ISO8601.DATETIME.format(this.to));
    }
    if (!this.facets.isEmpty()) {
      StringBuilder select = new StringBuilder();
      for (Entry<String, String> f : this.facets.entrySet()) {
        if (select.length() > 0) {
          select.append(',');
        }
        // TODO URL encoding
        select.append(f.getKey()).append(':').append(f.getValue());
      }
      parameters.put("select", select.toString());
    }
    // Paging
    if (this.page > 1) {
      parameters.put("page", Integer.toString(this.page));
    }
    parameters.put("page-size", Integer.toString(this.pageSize));
    String s = this.sortBy;
    if (s != null) {
      parameters.put("sortby", s);
    }
    return parameters;
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    if (this.type != null) {
      s.append("types=").append(this.type).append(';');
    }
    if (!this.facets.isEmpty()) {
      for (Entry<String, String> f : this.facets.entrySet()) {
        s.append(f.getKey()).append('=').append(f.getValue()).append(';');
      }
    }
    return s.toString();
  }

}
