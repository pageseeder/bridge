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

import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility class to manipulate comment properties.
 *
 * @version 0.11.2
 * @since 0.11.2
 */
public final class CommentProperties implements Serializable, XMLWritable {

  /**
   * Empty comment properties.
   */
  public static final CommentProperties EMPTY = new CommentProperties();

  /**
   * Ordered map of properties
   */
  private Map<String, String> _properties;

  private CommentProperties() {
    this._properties = Collections.emptyMap();
  }

  private CommentProperties(Map<String, String> properties) {
    this._properties = properties;
  }

  public boolean isEmpty() {
    return this._properties.isEmpty();
  }

  public CommentProperties plus(String name, String value) {
    Map<String, String> updated = new LinkedHashMap<>(this._properties);
    updated.put(name, value);
    return new CommentProperties(updated);
  }

  public CommentProperties remove(String name) {
    Map<String, String> updated = new LinkedHashMap<>(this._properties);
    updated.remove(name);
    return new CommentProperties(updated);
  }

  public static CommentProperties parse(String properties) {
    String[] prp = properties.split("\\|");
    Map<String, String> map = new LinkedHashMap<>(prp.length);
    for (String p : prp) {
      if (p.length() > 0) {
        int eq = p.indexOf('=');
        if (eq >= 0) {
          String name = p.substring(0, eq);
          String value = p.substring(eq+1);
          map.put(name, value);
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

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("properties");
    for (Entry<String, String> e : this._properties.entrySet()) {
      xml.openElement("property");
      xml.attribute("name", e.getKey());
      xml.attribute("name", e.getValue());
      xml.closeElement();
    }
    xml.closeElement();
  }

  /*
   *
   *
   toString() {
     Map<String, String> props = this.properties;
     if (props == null) return "";
     StringBuilder s = new StringBuilder();
     for (Map.Entry<String, String> p : props.entrySet()) {
       if (s.length() > 0) {
         s.append('|');
       }
       // TODO handle escaped pipes
       s.append(p.getKey()).append('=').append(p.getValue());
     }
     return s.toString();
   }
   */

  /*
  /**
   * @param properties the properties to set

  public void setProperties(String properties) {
    Map<String, String> p = new HashMap<>();
    if (properties != null) {
      for (String property: properties.split("(?<!\\|)\\|(?!\\|)")) {
        property = property.replaceAll("\\|\\|", "\\|");
        int eq = property.indexOf('=');
        //There must be a name and a value
        if (eq > 0) {
          String name = property.substring(0, eq);
          String value = property.substring(eq + 1);
          if (!"label".equals(name)) {
            p.put(name, value);
          }
        }
        //Otherwise, just use the name without any value
        else if (eq == -1 && !property.isEmpty()) {
          if (!"label".equals(property)) {
            p.put(property, "");
          }
        }
      }
    }
    this.properties = p;
  }

   */



}
