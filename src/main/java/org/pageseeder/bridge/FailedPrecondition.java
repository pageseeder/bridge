/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge;

/**
 * Class of exceptions used for preconditions to an API call.
 *
 * <p>This exception should be thrown when API call could not be made because it would result in an error.
 *
 * @author Christophe Lauret
 */
public class FailedPrecondition extends APIException {

  /** As per requirement */
  private static final long serialVersionUID = -521141112817296283L;

  /**
   * Create a new precondition.
   *
   * @param message A message explaining what precondition failed.
   */
  public FailedPrecondition(String message) {
    super(message);
  }

}
