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

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A base class for immutable lists
 *
 * @param <E> The type of element in the list
 */
abstract class ImmutableList<E> implements Iterable<E> {

  protected final List<E> _list;

  protected ImmutableList(List<E> list) {
    this._list = list;
  }

  public Stream<E> stream() {
    return this._list.stream();
  }

  /**
   * Returns the number of elements in this list.
   *
   * @return the number of elements in this list
   */
  public int size() {
    return this._list.size();
  }

  /**
   * Returns <code>true</code> if this list contains no elements.
   *
   * @return <code>true</code> if this list contains no elements
   */
  public boolean isEmpty() {
    return this._list.isEmpty();
  }

  /**
   * Returns the element at the specified position in this list.
   *
   * @param index index of the element to return
   * @return the element at the specified position in this list
   * @throws IndexOutOfBoundsException if the index is out of range
   *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
   */
  public E get(int index) {
    return this._list.get(index);
  }

  /**
   * Create a new list including the specified element at the end of this list.
   *
   * @param element the element to add
   *
   * @return a new ummodifiable list by adding the specified element.
   */
//  protected T plus(E element) {
//    return plus(this._list, element);
//  }

  /**
   * Create a new list without elements matching the specified predicate.
   *
   * @param predicate the condition to remove the element
   *
   * @return a new list by removing the element matching the specified predicate
   */
//  protected List<E> minus(java.util.function.Predicate<? super E> predicate) {
//    return minus(this._list, predicate);
//  }

  /**
   * Create a new list without elements matching the specified predicate.
   *
   * @param list The list to transform
   * @param predicate the condition to remove the element
   *
   * @return a new list by removing the element matching the specified predicate
   */
  protected static <E> List<E> minus(List<E> list, java.util.function.Predicate<? super E> predicate) {
    return list.stream().filter(predicate.negate()).collect(Collectors.toList());
  }

  /**
   * Create a new list including the specified element at the end of this list.
   *
   * @param element the element to add
   *
   * @return a new ummodifiable list by adding the specified element.
   */
  protected static <E> List<E> plus(List<E> list, E element) {
    if (list.size() == 0) {
      return Collections.singletonList(element);
    } else {
      List<E> updated = new ArrayList<>(list);
      updated.add(element);
      return Collections.unmodifiableList(updated);
    }
  }

  // Iterable implementations

  @Override
  public void forEach(Consumer<? super E> action) {
    this._list.forEach(action);
  }

  @Override
  public Spliterator<E> spliterator() {
    return this._list.spliterator();
  }

  @Override
  public Iterator<E> iterator() {
    return this._list.iterator();
  }

}
