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
