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

/**
 * A membership details field
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Field implements Serializable, XMLWritable {

  /**
   * The maximum valid index for the fields.
   */
  public static final int MAX_SIZE = 15;

  /**
   * The default type for the field when not specified.
   */
  public static final String DEFAULT_TYPE = "text";

  /**
   * The position of the field from 1 to 15
   */
  private final int _position;

  /**
   * The name for this field
   */
  private final String _name;

  /**
   * The value
   */
  private final String _value;

  /**
   * Whether this field can be edited
   */
  private final boolean _editable;

  /**
   * The title for this field
   */
  private final String _title;

  /**
   * The type of field
   */
  private final String _type;

  public Field(int position, String name, String value) {
    this(position, name, value, false, name, DEFAULT_TYPE);
  }

  public Field(int position, String name, String value, boolean editable, String title, String type) {
    if (position < 1 || position > MAX_SIZE) throw new IndexOutOfBoundsException("Field index must be between 1 and "+MAX_SIZE);
    this._position = position;
    this._name = name;
    this._value = value;
    this._editable = editable;
    this._title = title;
    this._type = type;
  }

  public int getPosition() {
    return this._position;
  }

  public String getName() {
    return this._name;
  }

  public String getValue() {
    return this._value;
  }

  public boolean isEditable() {
    return this._editable;
  }

  public String getTitle() {
    return this._title;
  }

  public String getType() {
    return this._type;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("field");
    xml.attribute("position", this._position);
    xml.attribute("name", this._name);
    xml.attribute("editable", Boolean.toString(this._editable));
    xml.attribute("title", this._title);
    xml.attribute("type", this._type);
    xml.writeText(this._value);
    xml.closeElement();
  }
}
