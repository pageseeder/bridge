/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
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

  public APIException(String message) {
    super(message);
  }

  public APIException(Throwable cause) {
    super(cause);
  }

  public APIException(String message, Throwable cause) {
    super(message, cause);
  }
}
