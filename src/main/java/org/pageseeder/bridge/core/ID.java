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

import java.io.Serializable;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Define an identifier.
 *
 * <p>This class is designed to be used as a base class for most identifiers in PageSeeder.
 */
public abstract class ID implements Serializable {

  /** As per requirement for {@link Serializable} */
  private static final long serialVersionUID = 1L;

  /** Actual ID. */
  private final String _id;

  ID(String id) {
    this._id = Objects.requireNonNull(id, "ID must not be null");
  }

  @Override
  public final int hashCode() {
    return this._id.hashCode();
  }

  @Override
  public final boolean equals(@Nullable Object o) {
    if (this == o) return true;
    if (o == null) return false;
    if (getClass() != o.getClass()) return false;
    ID other = (ID)o;
    return this._id.equals(other._id);
  }

  @Override
  public final String toString() {
    return this._id;
  }

}