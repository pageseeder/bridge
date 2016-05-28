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
package org.pageseeder.bridge.control;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.model.PSThreadStatus;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.net.PSHTTPResponseInfo;
import org.pageseeder.bridge.xml.PSThreadHandler;

/**
 * A manager for process threads.
 *
 * @author Jean-Baptiste Reure
 * @version 0.3.10
 * @since 0.3.10
 */
public final class ThreadManager extends Sessionful {

  /**
   * Default thread delay in seconds
   */
  public final static int DEFAULT_THREAD_DELAY_SECONDS = 2;

  /**
   * Default thread timeout in seconds
   */
  public final static int DEFAULT_THREAD_TIMEOUT_SECONDS = 10;

  /**
   * @param credentials the session to use for PS connections
   */
  public ThreadManager(PSCredentials credentials) {
    super(credentials);
  }

  /**
   * Check the progress of a process thread defined by the status provided.
   *
   * @param status the current status
   *
   * @return the new status
   *
   * @throws APIException If connecting to PageSeeder failed
   */
  public PSThreadStatus checkProgress(PSThreadStatus status) throws APIException {
    if (status == null) throw new NullPointerException("status");
    PSHTTPConnector connector = PSHTTPConnectors.checkThreadProgress(status).using(this._credentials);
    PSThreadHandler handler = new PSThreadHandler();
    PSHTTPResponseInfo info = connector.get(handler);
    if (info.getCode() >= 400)
      throw new APIException("Unable to check progress of thread '"+status.getThreadID()+"': "+info.getMessage());
    return handler.getThreadStatus();
  }

  /**
   * Wait for a thread to complete.
   * Delay is 2 seconds and timeout is 10 seconds.
   *
   * @param currentStatus the current status of the thread
   *
   * @return the final status
   *
   * @throws APIException if there was an error polling the thread or the timeout was triggered
   */
  public PSThreadStatus completeThread(PSThreadStatus currentStatus) throws APIException {
    return completeThread(currentStatus, -1, -1);
  }

  /**
   * Wait for a thread to complete.
   *
   * @param currentStatus the current status of the thread
   * @param delay         the delay between each thread progress poll
   * @param timeout       the timeout for the thread completion
   *
   * @return the final status
   *
   * @throws APIException if there was an error polling the thread or the timeout was triggered
   */
  public PSThreadStatus completeThread(PSThreadStatus currentStatus, int delay, int timeout) throws APIException {
    long delayInMS   = (delay < 1 ? DEFAULT_THREAD_DELAY_SECONDS     : delay)   * 1000;
    long timeoutInMS = (timeout < 1 ? DEFAULT_THREAD_TIMEOUT_SECONDS : timeout) * 1000;
    // synchronous so keep checking for thread progress
    long started = System.currentTimeMillis();
    PSThreadStatus status = currentStatus;
    while (!status.isCompleted()) {
      try {
        Thread.sleep(delayInMS);
      } catch (InterruptedException ex) {
        throw new APIException("Interrupted thread when waiting for group rename thread to complete", ex);
      }
      if (System.currentTimeMillis() - started > timeoutInMS) throw new APIException("Failed to rename group in "+ (timeoutInMS / 1000) + " seconds");
      status = checkProgress(status);
    }
    return status;
  }
}
