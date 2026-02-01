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

import org.jspecify.annotations.Nullable;
import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * The details of a membership.
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Details implements Iterable<Field>, Serializable, XMLWritable {

  /**
   * An immutable
   */
  public static final Details NO_DETAILS = new Details();

  /**
   * As per recommendation
   */
  private static final long serialVersionUID = 1L;

  /**
   * Internal array to store the field values.
   */
  private final Field[] _fields = new Field[Field.MAX_SIZE];

  /**
   * The details are empty.
   */
  private final boolean _empty;

  /**
   * Used to create empty details.
   */
  private Details() {
    this._empty = true;
  }

  /**
   * Create a new set of detail fields.
   *
   * <p>Implementation note: if there are multiple fields at the same position the last one will
   * overwrite the others.</p>
   *
   * @param fields The fields to add
   */
  public Details(Field... fields) {
    for (Field field : fields) {
      this._fields[field.getPosition() - 1] = field;
    }
    this._empty = fields.length == 0;
  }

  /**
   * Create a new set of detail fields.
   *
   * <p>Implementation note: if there are multiple fields at the same position the last one will
   * overwrite the others.</p>
   *
   * @param fields The fields to add
   */
  public Details(List<Field> fields) {
    for (Field field : fields) {
      this._fields[field.getPosition() - 1] = field;
    }
    this._empty = fields.size() == 0;
  }

  /**
   * Returns the detail field on that membership.
   *
   * @param position the 1-based index of the field.
   * @return the field value or <code>null</code> if none.
   * @throws IndexOutOfBoundsException If the index is less than 1 or greater than 15.
   */
  public @Nullable Field getField(int position) {
    if (position < 1 || position > Field.MAX_SIZE)
      throw new IndexOutOfBoundsException("Field index must be between 1 and " + Field.MAX_SIZE);
    return this._fields[position - 1];
  }

  @Override
  public Iterator<Field> iterator() {
    return Arrays.stream(this._fields).filter(Objects::nonNull).iterator();
  }

  /**
   * Indicates whether this membership has any fields
   *
   * @return true if this object contains any field; false otherwise
   */
  public boolean isEmpty() {
    return this._empty;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("details");
    for (Field field : this) {
      field.toXML(xml);
    }
    xml.closeElement();
  }
}
