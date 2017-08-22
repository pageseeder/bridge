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
package org.pageseeder.bridge.xml;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Superclass for exceptions caused by error during the processing of element.
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public abstract class ElementException extends RuntimeException {

  /** As per required for Serializable */
  private static final long serialVersionUID = 1L;

  /**
   * The name of the element.
   */
  private final String _name;

  /**
   * @param name The name of the element.
   */
  protected ElementException(String name) {
    this._name = name;
  }

  /**
   * Constructs a new element exception with the specified detail message and
   * cause.
   *
   * @param name    the name of the element.
   * @param message the detail message.
   */
  protected ElementException(String name, String message) {
    super(message);
    this._name = name;
  }

  /**
     * Constructs a new element exception with the specified detail message and
     * cause.
     *
     * @param name    the name of the element.
     * @param message the detail message.
     * @param cause   the cause (<tt>null</tt> value is permitted)
   */
  protected ElementException(String name, String message, @Nullable Throwable cause) {
    super(message, cause);
    this._name = name;
  }

  /**
   * @return the name of the element
   */
  public String getElementName() {
    return this._name;
  }

}
