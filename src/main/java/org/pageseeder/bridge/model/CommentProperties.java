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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility class to manipulate comment properties.
 *
 * @version 0.11.2
 * @since 0.11.2
 */
public final class CommentProperties {

  /**
   * Ordered map of properties
   */
  private Map<String, String> _properties;

  public CommentProperties() {
    this._properties = new LinkedHashMap<>();
  }

  private CommentProperties(Map<String, String> properties) {
    this._properties = properties;
  }

  public CommentProperties put(String name, String value) {
    this._properties.put(name, value);
    return this;
  }

  public CommentProperties remove(String name) {
    this._properties.remove(name);
    return this;
  }

  public static final CommentProperties parse(String properties) {
    String[] prp = properties.split("\\|");
    Map<String, String> map = new LinkedHashMap<>(prp.length);
    for (String p : prp) {
      if (p.length() > 0) {
        int eq = p.indexOf('=');
        if (eq >= 0) {
          String name = p.substring(0, eq);
          String value = p.substring(eq+1);
          if (name != null && value != null) {
            map.put(name, value);
          }
        } else {
          map.put(p, "");
        }
      }
    }
    return new CommentProperties(map);
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    for (Entry<String, String> e : this._properties.entrySet()) {
      s.append(e.getKey()).append('=').append(e.getValue()).append('|');
    }
    return s.toString();
  }

}
