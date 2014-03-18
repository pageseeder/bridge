/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.xml;

import java.util.ArrayList;
import java.util.List;

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
  protected final List<E> _items = new ArrayList<E>();

  /**
   * The entity being currently processed.
   */
  protected E current = null;

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
  final E current() {
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
  public final E get() {
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
