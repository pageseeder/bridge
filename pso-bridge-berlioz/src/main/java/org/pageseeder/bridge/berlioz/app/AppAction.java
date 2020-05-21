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
package org.pageseeder.bridge.berlioz.app;

import javax.servlet.http.HttpServletRequest;

import org.pageseeder.berlioz.aeson.JSONWriter;

/**
 * Defines an action used by the installation process.
 *
 * @author Christophe Lauret
 *
 * @since 0.9.9
 * @version 0.9.9
 */
public interface AppAction {

  /**
   * @return the name of the action.
   */
  String getName();

  /**
   * Executes the action based on the request and write the response as JSON.
   *
   * @param req  The request
   * @param json The JSON response.
   *
   * @return The HTTP response code as a result of the action.
   */
  int process(HttpServletRequest req, JSONWriter json);
}
