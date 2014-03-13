/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.net;

import java.net.HttpURLConnection;

/**
 * Provides metadata about a response to an HTTP request made to PageSeeder.
 *
 * <p>When possible, instances contain details such as the error ID and message when errors occur.
 *
 * @author Christophe Lauret
 * @version 0.2.0
 * @since 0.2.0
 */
public final class PSHTTPResponseInfo {

  public enum Status {

    IO_ERROR,

    CONNECTION_ERROR,

    PROCESS_ERROR,

    OK,

    REDIRECT,

    CLIENT_ERROR,

    SERVER_ERROR,

    UNKNOWN

  }

  /**
   * The HTTP code returned by PageSeeder.
   */
  private int code = -1;

  /**
   * The message returned by PageSeeder.
   */
  private String message = "";

  /**
   * The media type returned by PageSeeder.
   */
  private String mediaType = "";

  /**
   * The error ID returned by PageSeeder (services only)
   */
  private String errorID = "";

  /**
   * The status of response.
   */
  private Status status = Status.UNKNOWN;

  /**
   * @return the HTTP code
   */
  public int getCode() {
    return this.code;
  }

  /**
   * @return the error message
   */
  public String getMessage() {
    return this.message;
  }

  /**
   * @return the media type of the response
   */
  public String getMediaType() {
    return this.mediaType;
  }

  /**
   * @return the status of the response.
   */
  public Status getStatus() {
    return this.status;
  }

  /**
   * @return the error (services only)
   */
  public String getErrorID() {
    return this.errorID;
  }

  // Package private
  // ----------------------------------------------------------------------------------------------

  /**
   * @param code the code to set
   */
  void setCode(int code) {
    this.code = code;
  }

  /**
   * @param code the code to set
   */
  void setCodeAndStatus(int code) {
    this.code = code;
    if (code >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
      this.status = Status.SERVER_ERROR;
    } else if (code >= HttpURLConnection.HTTP_BAD_REQUEST) {
      this.status = Status.CLIENT_ERROR;
    } else if (code >= HttpURLConnection.HTTP_MULT_CHOICE) {
      this.status = Status.REDIRECT;
    } else if (code >= HttpURLConnection.HTTP_OK) {
      this.status = Status.OK;
    } else {
      this.status = Status.UNKNOWN;
    }
  }

  /**
   * @param message the message to set
   */
  void setMessage(String message) {
    this.message = message;
  }

  /**
   * @param mediaType the mediatype to set
   */
  void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  /**
   * @param status the status of the response
   */
  void setStatus(Status status) {
    this.status = status;
  }

  /**
   * @param status the status of the response
   * @param message the message to set
   */
  void setStatus(Status status, String message) {
    this.status = status;
    this.message = message;
  }

  /**
   * @param errorID the PageSeeder error ID (services only)
   */
  void setErrorID(String errorID) {
    this.errorID = errorID;
  }

  @Override
  public String toString() {
    return this.code+(this.errorID != null? " "+this.errorID+" " : " ")+(this.message != null? this.message : "OK");
  }
}
