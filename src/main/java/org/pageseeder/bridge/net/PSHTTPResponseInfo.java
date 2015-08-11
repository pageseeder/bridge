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
package org.pageseeder.bridge.net;

import java.net.HttpURLConnection;

/**
 * Provides metadata about a response to an HTTP request made to PageSeeder.
 *
 * <p>When possible, instances contain details such as the error ID and message when errors occur.
 *
 * @author Christophe Lauret
 * @version 0.3.0
 * @since 0.2.0
 */
public final class PSHTTPResponseInfo {

  /**
   * Status of an HTTP response.
   */
  public enum Status {

    /**
     * An I/O error was reported while trying to read the returned data from PageSeeder.
     */
    IO_ERROR,

    /**
     * The bridge API was unable to connect to PageSeeder.
     */
    CONNECTION_ERROR,

    /**
     * The returned data could not be processed successfully.
     *
     * <p>For example, when attempting to parse a non-XML stream.
     */
    PROCESS_ERROR,

    /**
     * The request has succeeded and the HTTP response code was 2xx.
     */
    SUCCESSFUL,

    /**
     * The response indicated that further action may be needed; the HTTP response code was 3xx.
     */
    REDIRECT,

    /**
     * The request failed because of the client seems to have erred (bad parameter, not found);
     * the HTTP response code was 4xx.
     */
    CLIENT_ERROR,

    /**
     * The request failed because of an error was reported by the server; the HTTP response code was 5xx.
     */
    SERVER_ERROR,

    /**
     * When unable to determine the determine the status or before the request was made.
     */
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
   * Shorthand method to check whether the response was successful.
   *
   * @return <code>true</code> is the status is SUCCESSFUL;
   *         <code>false</code> for any other status.
   */
  public boolean isSuccessful() {
    return this.status == Status.SUCCESSFUL;
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
      this.status = Status.SUCCESSFUL;
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
