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
package org.pageseeder.bridge.xml.stax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Create an object from from search results.
 *
 * <p>Implementation note: this is an experimental class: DO NOT USE IN PRODUCTION!</p>
 *
 * <p>Implementations must supply an implementation of `result(Map<String,String)`</p>
 *
 * @param <T> The type of object that can be created from a single result
 */
public abstract class ResultStreamHandler<T> extends ElementXMLStreamHandler<T> {

  /**
   * List of fields to extract from the search results.
   */
  private List<String> _fieldNames;

  public ResultStreamHandler() {
    super("result");
    this._fieldNames = Collections.emptyList();
  }

  public ResultStreamHandler(List<String> fieldNames) {
    super("result");
    this._fieldNames = fieldNames;
  }

  public T get(XMLStreamReader xml) throws XMLStreamException {
    List<Field> fields = new ArrayList<>();
    do {
        xml.next();
        if (xml.isStartElement() && "field".equals(xml.getLocalName())) {
          String name = attribute(xml, "name");
           if (this._fieldNames.isEmpty() || this._fieldNames.contains(name)) {
             String value = optionalAttribute(xml, "datetime");
             if (value == null)
               value = optionalAttribute(xml, "date");
             if (value == null)
               value = xml.getElementText();
              fields.add(new Field(name, value));
           }
        }
    } while (!(xml.isEndElement() && "result" == xml.getLocalName()));
    return result(fields);
  }

  /**
   * @param fields the field-value pairs for that result
   */
  public abstract T result(List<Field> fields);

  /**
   * A field value pair
   */
  public static final class Field {

    private final String name;
    private final String value;

    public Field(String name, String value) {
      this.name = Objects.requireNonNull(name, "name");
      this.value = Objects.requireNonNull(value, "value");
    }

    public final String getName() {
      return this.name;
    }

    public final String getValue() {
      return this.value;
    }

    public String toString() {
      return "Field(name=" + this.name + ", value=" + this.value + ")";
    }

    public int hashCode() {
      return this.name.hashCode()* 31 + this.value.hashCode();
    }
  }
}
