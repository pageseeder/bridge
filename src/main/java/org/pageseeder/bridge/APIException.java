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
package org.pageseeder.bridge;

/**
 * Class of exception wrapping all exceptions that may be thrown by entity managers and other
 * high-level class.
 *
 * <p>They may wrap the root cause of the problem such as an IO or parsing error.
 *
 * <p>Or they may be thrown when an would have been caused in PageSeeder and the API
 * pre-emptively threw an exception to abort the method.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public class APIException extends Exception {

  /** As per requirement */
  private static final long serialVersionUID = 1L;

  /**
   * A new exception thrown by the API with no message or other exception causing the problem.
   */
  public APIException() {
    super();
  }

  /**
   * Creates a new exception with the specified message.
   *
   * @param message the explanation for the error.
   */
  public APIException(String message) {
    super(message);
  }

  /**
   * Create a new exception from a different error.
   *
   * @param cause the cause of the error.
   */
  public APIException(Throwable cause) {
    super(cause);
  }

  /**
   * Create a new exception from a different error and an explanation.
   *
   * @param message the explanation for the error.
   * @param cause   the cause of the error.
   */
  public APIException(String message, Throwable cause) {
    super(message, cause);
  }
}
