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
package org.pageseeder.bridge.xml;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.pageseeder.bridge.PSEntity;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A base class for handlers used to create PageSeeder entities.
 *
 * @param <E> The PageSeeder entity this handler can produce.
 *
 * @author Christophe Lauret
 * @version 0.2.2
 * @since 0.2.2
 */
abstract class PSEntityHandler<E extends PSEntity> extends DefaultHandler {

  /**
   * The list of entities processed by this handler.
   */
  protected final List<E> _items = new ArrayList<>();

  /**
   * The entity being currently processed.
   */
  protected @Nullable E current = null;

  /**
   * Creates a new handler without setting an initial entity to update.
   */
  public PSEntityHandler() {
  }

  /**
   * Creates a new handler with an initial entity to update.
   *
   * @param entity The entity to modify from the XML returned.
   */
  public PSEntityHandler(E entity) {
    this.current = entity;
  }

  /**
   * Returns the entity being currently processed.
   *
   * <p>It may be the entity the handler was initialised with.
   *
   * @return the entity being currently processed.
   */
  final @Nullable E current() {
    return this.current;
  }

  /**
   * @return the list of group folders
   */
  public final List<E> list() {
    return this._items;
  }

  /**
   * @return the list of group folders
   */
  public final @Nullable E get() {
    int size = this._items.size();
    return size > 0? this._items.get(size-1) : null;
  }

  /**
   * Generates an instance of the entity from the specified attributes.
   *
   * <p>If the specified entity corresponds to the entity to build from the specified arguments
   * that object is updated and returned.
   *
   * <p>Otherwise, if the specified entity is <code>null</code> or does not match the attributes
   * a new instance is created an returned.
   *
   * @param atts   the attributes
   * @param entity an existing entity instance to reuse.
   *
   * @return The entity instance.
   */
  public abstract E make(Attributes atts, E entity);

}
