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

import java.io.Serializable;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.EntityValidity;

/**
 * The details of a membership.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @version 0.1.0
 */
public final class PSDetails implements Serializable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * The maximum valid index for the fields.
   */
  public static final int MAX_SIZE = 15;

  /**
   * Internal array to store the field values.
   */
  private String[] fields = new String[MAX_SIZE];

  /**
   * Returns the detail field on that membership.
   *
   * @param i the 1-based index of the field.
   * @return the field value or <code>null</code> if none.
   *
   * @throws IndexOutOfBoundsException If the index is less than 1 or greater than 15.
   */
  public @Nullable String getField(int i) {
    if (i < 1 || i > MAX_SIZE) throw new IndexOutOfBoundsException("Field index must be between 1 and "+MAX_SIZE);
    return this.fields[i-1];
  }

  /**
   * Sets the detail field on that membership.
   *
   * @param i     the 1-based index of the field.
   * @param value the value to set.
   *
   * @throws IndexOutOfBoundsException If the index is less than 1 or greater than 15.
   */
  public void setField(int i, @Nullable String value) {
    if (i < 1 || i > MAX_SIZE) throw new IndexOutOfBoundsException("Field index must be between 1 and "+MAX_SIZE);
    this.fields[i-1] = value;
  }

  /**
   * Check that the membership details are valid.
   *
   * @return the validitity of the entity
   */
  public EntityValidity checkValid() {
    for (String f : this.fields) {
      if (f != null && f.length() > 100) return EntityValidity.DETAIL_FIELD_VALUE_IS_TOO_LONG;
    }
    return EntityValidity.OK;
  }

  /**
   * @return <code>true</code> if any of the field values exceed 100 characters.
   */
  public boolean isValid() {
    return checkValid() == EntityValidity.OK;
  }

}
