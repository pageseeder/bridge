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

import java.util.Map;
import java.util.Objects;

/**
 * Specifies which page in the search results.
 */
public class Page {

  /**
   * The default page number is 1.
   */
  public static final int DEFAULT_PAGE_NUMBER = 1;

  /**
   * The default page number is 100.
   */
  public static final int DEFAULT_PAGE_SIZE = 100;

  /**
   * The default page
   */
  public static final Page DEFAULT_PAGE = new Page();

  /**
   * Requested page (a natural integer)
   */
  private final int _number;

  /**
   * Results per page
   */
  private final int _size;

  /**
   * Create a new page using the default page number and attribute.
   */
  public Page() {
    this._number = DEFAULT_PAGE_NUMBER;
    this._size = DEFAULT_PAGE_SIZE;
  }

  /**
   * Create a new page using the specified number and size.
   *
   * @param number The request page number
   * @param size   The maximum number of results per page
   *
   * @throws IllegalArgumentException if the number or size is not greater than zero.
   */
  public Page(int number, int size) {
    this._number = checkNatural(number);
    this._size = checkNatural(size);
  }

  /**
   * @return The requested page (a natural integer)
   */
  public int number() {
    return this._number;
  }

  /**
   * @return The number of results per page.
   */
  public int size() {
    return this._size;
  }

  public Page number(int page) {
    return new Page(page, this._size);
  }

  public Page size(int size) {
    return new Page(this._number, size);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Page page = (Page) o;
    return this._number == page._number && this._size == page._size;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this._number, this._size);
  }

  @Override
  public String toString() {
    return this._number +"("+this._size+")";
  }

  /**
   * @return The page options as a set of parameters for the search services.
   */
  public Map<String, String> toParameters(Map<String, String> parameters) {
    // Paging
    if (this._number > 0 && this._number != Page.DEFAULT_PAGE_NUMBER) {
      parameters.put("page", Integer.toString(this._number));
    }
    if (this._size > 0 && this._size != Page.DEFAULT_PAGE_SIZE) {
      parameters.put("pagesize", Integer.toString(this._size));
    }
    return parameters;
  }

  private static final int checkNatural(int n) {
    if (n <= 0) throw new IllegalArgumentException("Page and page size must be greater than 0)");
    return n;
  }

}
