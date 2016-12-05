/*
 * Copyright 2016 Allette Systems (Australia)
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
 * Exception thrown when an attribute is invalid.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.10.2
 */
public class InvalidAttributeException extends AttributeException {

  /** As per required for Serializable */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new attribute exception caused by an invalid attribute value.
   *
   * @param name the name of the attribute that is invalid
   */
  public InvalidAttributeException(String name) {
    this(name, null);
  }

  /**
   * Constructs a new attribute exception caused by an invalid attribute value.
   *
   * @param name  the name of the attribute that is invalid
   * @param cause the cause of the error
   */
  public InvalidAttributeException(String name, @Nullable Throwable cause) {
    super(name, "Invalid attribute `"+name+"`", cause);
  }

}
