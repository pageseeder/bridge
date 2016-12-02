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
package org.pageseeder.bridge.http;

import java.io.Serializable;

/**
 * Encapsulate information about service errors.
 */
public final class ServiceError implements Serializable {

  /** As per requirement for serializable */
  private static final long serialVersionUID = 20161202L;

  /** Used for padding */
  private final static String ZEROES = "0000";

  /**
   * The error ID.
   */
  private final int _id;

  /**
   * The message.
   */
  private final String _message;

  /**
   * Report a new service error.
   *
   * @param id      The error ID
   * @param message The error message
   */
  public ServiceError(int id, String message) {
    this._id = id;
    this._message = message;
  }

  /**
   * Report a new service error.
   *
   * @param code    The error code
   * @param message The error message
   *
   * @throws NumberFormatException If the code is invalid.
   */
  public ServiceError(String code, String message) {
    this._id = Integer.parseInt(code, 16);
    this._message = message;
  }

  /**
   * The error ID as an integer.
   *
   * @return The error ID as an integer.
   */
  public int id() {
    return this._id;
  }

  /**
   * Returns the error ID as an hexadecimal code with padding.
   *
   * @return the error ID as an hexadecimal code with padding.
   */
  public String code() {
    return code(this._id);
  }

  /**
   * Returns the message explaining or describing the error.
   *
   * @return the message explaining or describing the error.
   */
  public String message() {
    return this._message;
  }

  @Override
  public String toString() {
    return "[0x"+code()+"]"+this._message;
  }

  /**
   * Returns the error ID as an hexadecimal code with padding.
   *
   * @param id The error ID as a number
   *
   * @return the error ID as an hexadecimal code with padding.
   */
  public static String code(int id) {
    String hex = Integer.toHexString(id).toUpperCase();
    return hex.length() <= ZEROES.length() ? ZEROES.substring(hex.length()) + hex : hex;
  }

}