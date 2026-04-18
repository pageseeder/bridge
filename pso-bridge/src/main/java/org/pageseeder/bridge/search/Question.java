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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple boolean query for searches.
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Question {

  /**
   * An empty question
   */
  public static final Question EMPTY = new Question("", Collections.emptyList(), -1);

  /**
   * The question for full-text searches.
   */
  private final String _question;

  /**
   * The list of fields to search the question in.
   */
  private final List<String> _fields;

  /**
   * The max number of suggestions to load (only for question query).
   */
  private final int _suggestSize;

  /**
   * Create a new question
   *
   * @param question     The question for full-text searches.
   * @param fields       The list of fields to search the question in.
   * @param suggestSize  The max number of suggestions to load (only for question query).
   */
  public Question(String question, List<String> fields, int  suggestSize) {
    this._question = Objects.requireNonNull(question);
    this._fields = Objects.requireNonNull(fields);
    this._suggestSize = suggestSize;
  }

  /**
   * @return The question for full-text searches.
   */
  public String question() {
    return this._question;
  }

  /**
   * Returns the list of field names the question applies to.
   *
   * <p>If the list is empty, it will use PageSeeder's default list of fields.
   *
   * @return The list of field names the question applies to.
   */
  public List<String> fields() {
    return this._fields;
  }

  /**
   * @return The number of suggested results based on the question.
   */
  public int suggestSize() {
    return this._suggestSize;
  }

  /**
   * Updates the specified parameter map to include question parameters.
   *
   * <p>More specifically, this method will add the following parameters if necessary:</p>
   * <ul>
   *   <li><code>question</code></li>
   *   <li><code>questionfields</code></li>
   *   <li><code>suggestsize</code></li>
   * </ul>
   *
   * @return The same parameter map
   */
  public Map<String, String> toParameters(Map<String, String> parameters) {
    if (!this._question.isEmpty()) {
      parameters.put("question", this._question);
      if (!this._fields.isEmpty()) {
        parameters.put("questionfields", Search.join(this._fields, ','));
      }
      if (this._suggestSize >= 0) {
        parameters.put("suggestsize", Integer.toString(this._suggestSize));
      }
    }
    return parameters;
  }
}
