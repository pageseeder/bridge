/*
 * Copyright 2016 Allette Systems (Australia)
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

import java.util.Arrays;
import java.util.Objects;

/**
 * Computes the path of a PageSeeder document.
 *
 * <p>The path to a PageSeeder document excludes the site prefix.
 *
 * <p>This class is designed to construction of paths to PageSeeder document
 * by ensuring that the path never includes empty steps and returning a
 * consistent representation.
 *
 * <p>The path never ends with a '/'.
 *
 * <p>Implementation note: the path components are assumed to be valid path
 * components and no attempt is made to validate or escape them, the values
 * are used verbatim.
 *
 * @author Christophe Lauret
 * @version 0.9.4
 * @since 0.9.4
 */
public final class DocumentPath {

  /**
   * The steps to a document.
   */
  private final String[] _steps;

  /**
   * Creates a new document path.
   *
   * @param steps The URI template used for this service.
   */
  public DocumentPath(String path) {
    String[] steps = path.split("/");
    this._steps = normalize(steps);
  }

  /**
   * Creates a new document path.
   *
   * @param steps The URI template used for this service.
   */
  private DocumentPath(String... steps) {
    this._steps = Objects.requireNonNull(steps, "Steps must not be null");
  }

  /**
   * Factory method to create a document path from a group name and local
   * path from within the group.
   *
   * @param group The name of the group.
   * @param path  The path local to the group.
   *
   * @throws NullPointerException if either the group or path is <code>null</code>.
   */
  public static DocumentPath newLocalPath(String group, String path) {
    String[] g = group.split("-");
    String[] p = path.split("/");
    String[] steps = Arrays.copyOf(g, g.length+p.length);
    System.arraycopy(p, 0, steps, g.length, p.length);
    return new DocumentPath(normalize(steps));
  }

  // Class methods
  // --------------------------------------------------------------------------

  /**
   * Joins the steps using '/' as a separator.
   *
   * <p>The returned value always starts with a "/" if there is at least one step.
   *
   * @return the path to the document excluding the site prefix ("/ps").
   */
  public String path() {
    if (this._steps.length == 0) return "/";
    StringBuilder path = new StringBuilder();
    for (String step : this._steps) {
      path.append('/').append(step);
    }
    return path.toString();
  }

  /**
   * A copy of the underlying array making the steps in this path.
   *
   * @return a copy of the steps.
   */
  public String[] steps() {
    return Arrays.copyOf(this._steps, this._steps.length);
  }

  /**
   * Returns the number of steps.
   *
   * @return the number of steps in this path.
   */
  public int size() {
    return this._steps.length;
  }

  /**
   * Returns the parent document path.
   *
   * @return the last step or <code>null</code> if empty.
   */
  public String filename() {
    if (this._steps.length == 0) return null;
    String[] steps = this._steps;
    return steps[steps.length-1];
  }

  /**
   * Returns the parent document path.
   *
   * @return the path corresponding to the parent or <code>null</code>
   */
  public DocumentPath parent() {
    if (this._steps.length == 0) return null;
    String[] current = this._steps;
    String[] parent = Arrays.copyOf(current, current.length-1);
    return new DocumentPath(parent);
  }

  /**
   * Returns the parent document path.
   *
   * @return the path corresponding to the parent.
   */
  public DocumentPath child(String child) {
    String[] current = this._steps;
    String[] parent = Arrays.copyOf(current, current.length+1);
    parent[parent.length-1] = child;
    return new DocumentPath(parent);
  }


  /**
   * Remove any empty values.
   *
   * @param array The array to normalize
   *
   * @return a new array without any empty values.
   */
  private static String[] normalize(String[] array) {
    // We only copy non-empty steps
    int actual = 0;
    String[] steps = new String[array.length];
    for (String element : array) {
      if (element.length() > 0) {
        steps[actual++] = element;
      }
    }
    // Readjust the size of the array
    if (actual < array.length) {
      steps = Arrays.copyOf(steps, actual);
    }
    return steps;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(this._steps);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    DocumentPath other = (DocumentPath)obj;
    if (!Arrays.equals(this._steps, other._steps)) return false;
    return true;
  }

}
