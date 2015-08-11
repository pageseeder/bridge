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
package org.pageseeder.bridge.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A status for a process thread.
 *
 * @author Jean-Baptiste Reure
 * @version 0.3.10
 */
public final class PSThreadStatus implements Serializable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * List of possible status.
   *
   * @author Jean-Baptiste Reure
   * @version 22 March 2011
   *
   */
  public enum Status {
    /**
     * When a thread has been initialised but not yet started.
     */
    INITIALISED,
    /**
     * When a thread is currently running.
     */
    INPROGRESS,
    /**
     * When a thread has an error.
     */
    ERROR,
    /**
     * When a thread has a warning.
     */
    WARNING,
    /**
     * When a thread has been cancelled.
     */
    CANCELLED,
    /**
     * When a thread is finished.
     */
    COMPLETED,
    /**
     * When a thread has finished with an error.
     */
    FAILED;

    /**
     * Load a status from it's string value
     * @param s the string value
     * @return the status if found, null otherwise
     */
    static Status fromString(String s) {
      for (Status st : values()) {
        if (st.toString().equalsIgnoreCase(s)) return st;
      }
      return null;
    }
  }
  /**
   * The thread ID.
   */
  private final String _threadID;

  /**
   * The thread name.
   */
  private String name = null;

  /**
   * The author's username.
   */
  private String username = null;

  /**
   * The context group id.
   */
  private Long groupID;

  /**
   * The thread status.
   */
  private Status status;

  /**
   * The thread's list of messages.
   */
  private List<String> messages = new ArrayList<String>();

  /**
   * @param threadid the ID of the process thread
   */
  public PSThreadStatus(String threadid) {
    this._threadID = threadid;
  }

  /**
   * @param groupid the context group ID
   */
  public void setGroupID(Long groupid) {
    this.groupID = groupid;
  }

  /**
   * @param n the thread name
   */
  public void setName(String n) {
    this.name = n;
  }

  /**
   * @param uname The author's username
   */
  public void setUsername(String uname) {
    this.username = uname;
  }

  /**
   * @param thestatus the thread status
   */
  public void setStatus(String thestatus) {
    this.status = Status.fromString(thestatus);
  }

  /**
   * @param thestatus the thread status
   */
  public void setStatus(Status thestatus) {
    this.status = thestatus;
  }

  /**
   * @param msg a new message to add
   */
  public void addMessage(String msg) {
    this.messages.add(msg);
  }

  /**
   * @return the ID of the process thread
   */
  public String getThreadID() {
    return this._threadID;
  }

  /**
   * @return the context group ID
   */
  public Long getGroupID() {
    return this.groupID;
  }

  /**
   * @return the thread name
   */
  public String getThreadName() {
    return this.name;
  }

  /**
   * @return the username of the author
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * @return the thread status
   */
  public Status getStatus() {
    return this.status;
  }

  /**
   * @return the list of messages
   */
  public List<String> getMessages() {
    return this.messages;
  }

  /**
   * @return <code>true</code> if the thread is finished (success, failed or cancelled)
   */
  public boolean isCompleted() {
    return this.status == Status.CANCELLED || this.status == Status.COMPLETED || this.status == Status.FAILED;
  }

  /**
   * @return <code>true</code> if the thread has failed
   */
  public boolean hasFailed() {
    return this.status == Status.FAILED;
  }

  /**
   * @return the last message if there was any, <code>null</code> otherwise
   */
  public String getLastMessage() {
    if (this.messages.isEmpty()) return null;
    return this.messages.get(this.messages.size()-1);
  }

}
