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

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.pageseeder.berlioz.aeson.JSONWriter;
import org.pageseeder.bridge.http.Response;
import org.pageseeder.bridge.http.ServiceError;
import org.pageseeder.bridge.oauth.TokenResponse;

/**
 * Utility class to write JSON response.
 *
 * @author Christophe Lauret
 *
 * @since 0.9.9
 * @version 0.9.9
 */
public final class JSONResponses {

  /**
   * Writes a successful response for the action.
   *
   * <pre>
   *  {
   *    "action": "[action_name]"
   *    "result": {
   *      "[key_1]": "[value_1]",
   *      "[key_2]": "[value_2]",
   *      "[key_3]": "[value_3]",
   *      "[key_4]": "[value_4]"
   *      ...
   *    }
   *  }
   * </pre>
   *
   * @param action The action triggered
   * @param json   The JSON writer for the response
   * @param result Values to include in the result object
   *
   * @return Always 200 (OK)
   */
  public static int ok(AppAction action, JSONWriter json, Map<String, String> result) {
    json.startObject()
    .property("action", action.getName())
    .startObject("result");
    for (Entry<String, String> property : result.entrySet()) {
      json.property(property.getKey(), property.getValue());
    }
    json.end()
    .end();
    return HttpServletResponse.SC_OK;
  }

  /**
   * Writes an error response for the action.
   *
   * <pre>
   *  {
   *    "action": "[action_name]"
   *    "error": {
   *      "description": "[description]"
   *    }
   *  }
   * </pre>
   *
   * @param action      The action triggered
   * @param json        The JSON writer for the response
   * @param description The error description
   *
   * @return Always 400 (BAD REQUEST)
   */
  public static int error(AppAction action, JSONWriter json, String description) {
    json.startObject()
    .property("action", action.getName())
    .startObject("error").property("description", description).end()
    .end();
    return HttpServletResponse.SC_BAD_REQUEST;
  }

  /**
   * Writes an error response for the action.
   *
   * <pre>
   *  {
   *    "action": "[action_name]"
   *    "error": {
   *      "parameter": "[name]"
   *      "description": "The parameter '[name]' was not specified"
   *    }
   *  }
   * </pre>
   *
   * @param action The action triggered
   * @param json   The JSON writer for the response
   * @param name   The name of the missing parameter
   *
   * @return Always 400 (BAD REQUEST)
   */
  public static int requiresParameter(AppAction action, JSONWriter json, String name) {
    json.startObject()
    .property("action", action.getName())
    .startObject("error")
    .property("parameter", name)
    .property("description", "The parameter '"+name+"' was not specified")
    .end()
    .end();
    return HttpServletResponse.SC_BAD_REQUEST;
  }

  /**
   * Writes an error response for the action when a PageSeeder service error has occurred.
   *
   * <pre>
   *  {
   *    "action": "[action_name]"
   *    "error": {
   *      "code": "[error_code]"
   *      "description": "[error_message]"
   *    }
   *  }
   * </pre>
   *
   * @param action   The action triggered
   * @param json     The JSON writer for the response
   * @param response The PageSeeder service response
   *
   * @return Always 400 (BAD REQUEST)
   */
  public static int serviceError(AppAction action, JSONWriter json, Response response) {
    ServiceError error = response.consumeServiceError();
    json.startObject()
    .property("action", action.getName())
    .startObject("error")
    .property("code", error.code())
    .property("description", error.message())
    .end()
    .end();
    return HttpServletResponse.SC_BAD_REQUEST;
  }

  /**
   * Writes an error response for the action when a PageSeeder service error has occurred.
   *
   * <pre>
   *  {
   *    "action": "[action_name]"
   *    "error": {
   *      "code": "[error_code]"
   *      "description": "[error_message]"
   *    }
   *  }
   * </pre>
   *
   * @param action   The action triggered
   * @param json     The JSON writer for the response
   * @param response The PageSeeder service response
   *
   * @return Always 400 (BAD REQUEST)
   */
  public static int serviceError(AppAction action, JSONWriter json, TokenResponse response) {
    String error = response.getError() == null ? "" : response.getError();
    String description = response.getErrorDescription() == null ? "" : (" : " + response.getErrorDescription());
    json.startObject()
            .property("action", action.getName())
            .startObject("error")
            .property("code", response.getResponseCode())
            .property("description", error + description )
            .end()
            .end();
    return HttpServletResponse.SC_BAD_REQUEST;
  }

}
