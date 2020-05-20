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
package org.pageseeder.bridge.xml;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.xml.sax.Attributes;

/**
 * A base class to construct objects from search results.
 *
 * @author Christophe Lauret
 *
 * @param <T> The type of object to build from the search result
 */
@SuppressWarnings("EmptyMethod")
public abstract class BasicResultHandler<T> extends BasicHandler<T> {

  /**
   * The list of fields to extract, if empty, all fields are extracted.
   */
  private final List<String> _fields;

  /**
   * State variable to indicate the group the current result belong to
   */
  private @Nullable String group = null;

  /**
   * State variable to indicate the name of the current field.
   */
  private @Nullable String fieldname = null;

  /**
   * This method is called whenever a new result document starts.
   *
   * <p>If the group is not known, a <code>null</code> value is sent.
   *
   * @param group  The group this document is part of.
   */
  public void startResult(@Nullable String group) {
  }

  /**
   * This method is called whenever a result document ends.
   */
  public void endResult() {
  }

  /**
   * This method is called for each field.
   *
   * @param name  The name of the search results field.
   * @param value The value of the search results field.
   */
  public abstract void field(String name, String value);

  /**
   * This method is called for each score element on a document.
   *
   * @param score the score of the current document.
   */
  @SuppressWarnings("EmptyMethod")
  public void score(double score) {
  }

  /**
   * Creates a basic handler for result capturing every field.
   */
  public BasicResultHandler() {
    this._fields = Collections.emptyList();
  }

  /**
   * Creates a basic handler for result only capturing fields listed by the
   * parameter.
   *
   * <p>Only the fields which name match one of the names specified will be
   * reported by the {@link #field(String, String)} method.
   *
   * <p>Implementation note: this method avoid creating unnecessary strings
   * for fields the extending class is not interested in.
   *
   * @param fields The names of the fields to capture.
   *
   * @throws NullPointerException if fields is <code>null</code>.
   */
  public BasicResultHandler(@NonNull String... fields) {
    this._fields = Arrays.asList(fields);
  }

  @Override
  public final void startElement(String element, Attributes attributes) {
    if ("document".equals(element)) {
      startResult(this.group);

    } else if (isParent("document")) {
      if ("field".equals(element)) {
        String name = getString(attributes, "name");
        List<String> fields = this._fields;
        if (fields.contains(name) || fields.isEmpty()) {
          this.fieldname = name;
          newBuffer();
        }
      } else if ("score".equals(element)) {
        newBuffer();
      }

    } else if ("search".equals(element)) {
      String groupname = getOptionalString(attributes, "groupname");
      if (groupname != null) {
        this.group = groupname;
      }
    }
  }

  @Override
  public final void endElement(String element) {
    if (isElement("document")) {
      endResult();
    } else if (isElement("field") && isParent("document")) {
      String name = this.fieldname;
      if (name != null) {
        String value = buffer(true);
        if (value != null) {
          field(name, value);
        }
        this.fieldname = null;
      }
    } else if ("score".equals(element) && isParent("document")) {
      String value = buffer(true);
      if (value != null) {
        try {
          double score = Double.parseDouble(value);
          score(score);
        } catch (NumberFormatException ex) {
          // Do nothing.
        }
      }
    }
  }

}
