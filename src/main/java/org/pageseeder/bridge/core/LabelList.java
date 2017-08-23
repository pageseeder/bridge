/*
 * Copyright 2017 Allette Systems (Australia)
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

package org.pageseeder.bridge.core;

import org.eclipse.jdt.annotation.Nullable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A list of labels that can be applied to a comment, URI, etc...
 *
 * @author Christophe Lauret
 * @since 0.12.0
 * @version 0.12.0
 */
public final class LabelList implements Serializable, Iterable<String> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static Pattern VALID_LABEL = Pattern.compile("[a-zA-Z0-9_\\-]+");

  /**
   * An empty list of labels.
   */
  public static final LabelList NO_LABELS = new LabelList();

  /**
   * Internal representation of labels - DO NOT EXPOSE.
   */
  private final String[] _labels;

  /**
   * Private constructor to construct an empty list of labels.
   */
  private LabelList() {
    this._labels = new String[]{};
  }

  /**
   * Used by parser to create a new instance. Keep private.
   *
   * @param labels the array to use for this class.
   */
  private LabelList(String[] labels) {
    this._labels = labels;
  }

  /**
   * Use by parser to
   *
   * @param labels the list of labels
   */
  public LabelList(List<String> labels) {
    this._labels = labels.stream().filter(label ->
        label != null && label.length() > 0
    ).map(String::trim).filter(label ->
        VALID_LABEL.matcher(label).matches()
    ).toArray(String[]::new);
  }

  public boolean isEmpty() {
    return this._labels.length == 0;
  }

  /**
   * Indicates how many labels are defined.
   *
   * @return the number of labels.
   */
  public int size() {
    return this._labels.length;
  }

  /**
   * @return a copy of the list of labels
   */
  public List<String> toList() {
    return Arrays.asList(this._labels);
  }

  @Override
  public Iterator<String> iterator() {
    return Arrays.stream(this._labels).iterator();
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LabelList other = (LabelList) o;
    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    return Arrays.equals(this._labels, other._labels);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(this._labels);
  }

  @Override
  public String toString() {
    return String.join(",", this._labels);
  }

  /**
   * Parse the list of labels and returns the corresponding instance.
   *
   * <p>If the list is empty, this method will return the <code>NO_LABELS</code> constant.</p>
   *
   * @param labels The labels as a string
   *
   * @return the corresponding instance.
   */
  public static LabelList parse(String labels) {
    // Let's short-circuit the filtering if the list is empty
    if (labels == null || labels.length() == 0) return NO_LABELS;
    // Filter empty or invalid label values
    String[] filtered = Arrays.stream(labels.split(","))
        .filter(label -> label.length() > 0)
        .map(String::trim)
        .filter(label -> VALID_LABEL.matcher(label).matches()
    ).toArray(String[]::new);
    return filtered.length > 0? new LabelList(filtered): NO_LABELS;
  }

}
