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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * A status for a process thread.
 *
 * @author Jean-Baptiste Reure
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @version 0.3.10
 */
public final class PSThreadStatus implements Serializable, XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * List of possible status.
   *
   * The error and warning are only transitory states, and the thread will always terminate only with one of completed,
   * failed, or cancelled which are final states.
   *
   * This is because there may be several errors or warnings.
   *
   * Threads that include warnings would usually result in a completed state and errors in a failed state.
   *
   * There is only one initial state: initialised.
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
    static @Nullable Status fromString(@Nullable String s) {
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
  private @Nullable String name = null;

  /**
   * The author's username.
   */
  private @Nullable String username = null;

  /**
   * The context group id.
   */
  private @Nullable Long groupID;

  /**
   * The thread status.
   */
  private @Nullable Status status;

  /**
   * The thread's list of messages.
   */
  private List<String> messages = new ArrayList<>();

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
  public void setName(@Nullable String n) {
    this.name = n;
  }

  /**
   * @param uname The author's username
   */
  public void setUsername(@Nullable String uname) {
    this.username = uname;
  }

  /**
   * @param thestatus the thread status
   */
  public void setStatus(@Nullable String thestatus) {
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
  public @Nullable Long getGroupID() {
    return this.groupID;
  }

  /**
   * @return the thread name
   */
  public @Nullable String getThreadName() {
    return this.name;
  }

  /**
   * @return the username of the author
   */
  public @Nullable String getUsername() {
    return this.username;
  }

  /**
   * @return the thread status
   */
  public @Nullable Status getStatus() {
    return this.status;
  }

  /**
   * @return the list of messages
   */
  public List<String> getMessages() {
    return this.messages;
  }

  /**
   * The error and warning are only transitory states, and the thread will always terminate only with one of completed,
   * failed, or cancelled which are final states.
   *
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
  public @Nullable String getLastMessage() {
    if (this.messages.isEmpty()) return null;
    return this.messages.get(this.messages.size()-1);
  }

  /**
   *
   * it writes the username in the XML if it is a sensitive information in your application you should avoid.
   *
   * @param writer
   * @throws IOException
   */
  public void toXML (XMLWriter writer) throws IOException {
    writer.openElement("thread");
    writer.attribute("id", this.getThreadID() != null ? this.getThreadID() : "");
    writer.attribute("name", this.getThreadName() != null ? this.getThreadName() : "");
    writer.attribute("username", this.getUsername() != null ? this.getUsername() : "");
    writer.attribute("groupid", this.getGroupID() != null ? String.valueOf(this.getGroupID()) : "");
    writer.attribute("inprogress", this.getStatus() != null ? this.getStatus().name().toLowerCase() : "");

    //messages
    for (String message : this.getMessages()) {
      if (message != null && !message.isEmpty()) {
        writer.element("message", message);
      }
    }
    writer.closeElement();//thread
  }

}
