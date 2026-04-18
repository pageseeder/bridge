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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple boolean query for searches.
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Question implements Serializable {

  /** As per recommendation */
  private static final long serialVersionUID = 20260418L;

  /**
   * An empty question
   */
  public static final Question EMPTY = new Question("", List.of(), -1);

  /**
   * The question for full-text searches.
   */
  private final String question;

  /**
   * The list of fields to search the question in.
   */
  private final List<String> fields;

  /**
   * The max number of suggestions to load (only for question query).
   */
  private final int suggestSize;

  /**
   * Create a new question
   *
   * @param question     The question for full-text searches.
   * @param fields       The list of fields to search the question in.
   * @param suggestSize  The max number of suggestions to load (only for question query).
   */
  public Question(String question, List<String> fields, int  suggestSize) {
    this.question = Objects.requireNonNull(question);
    this.fields = Objects.requireNonNull(fields);
    this.suggestSize = suggestSize;
  }

  /**
   * @return The question for full-text searches.
   */
  public String question() {
    return this.question;
  }

  /**
   * Returns the list of field names the question applies to.
   *
   * <p>If the list is empty, it will use PageSeeder's default list of fields.
   *
   * @return The list of field names the question applies to.
   */
  public List<String> fields() {
    return this.fields;
  }

  /**
   * @return The number of suggested results based on the question.
   */
  public int suggestSize() {
    return this.suggestSize;
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
    if (!this.question.isEmpty()) {
      parameters.put("question", this.question);
      if (!this.fields.isEmpty()) {
        parameters.put("questionfields", Search.join(this.fields, ','));
      }
      if (this.suggestSize >= 0) {
        parameters.put("suggestsize", Integer.toString(this.suggestSize));
      }
    }
    return parameters;
  }
}
