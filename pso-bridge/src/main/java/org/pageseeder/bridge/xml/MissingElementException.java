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

/**
 * Exception thrown when an expected / required element is missing.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.10.2
 */
public final class MissingElementException extends ElementException {

  /** As per required for Serializable */
  private static final long serialVersionUID = 1L;

  /**
   * @param name The name of the missing element.
   */
  public MissingElementException(String name) {
    super(name, "Missing required element `"+name+"`");
  }

}
