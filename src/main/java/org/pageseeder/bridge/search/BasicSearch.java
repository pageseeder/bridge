/*
 * Copyright 2018 Allette Systems (Australia)
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
package org.pageseeder.bridge.search;

import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.http.*;

import java.util.List;
import java.util.Map;

/**
 * A base class for searches.
 *
 * @param <T> The search implementation (using the "Curiously Recurring Generic Pattern")
 */
abstract class BasicSearch<T extends BasicSearch> {

  /**
   * The scope of the search.
   */
  protected final Scope _scope;

  protected BasicSearch(Scope scope){
    this._scope = scope;
  }

  /**
   * Set the scope of the search to a group.
   *
   * @param group The name of the group
   *
   * @return A new instance of the implementing class
   */
  public abstract T group(String group);

  /**
   * Set the scope of the search to a project.
   *
   * @param project The name of the project
   *
   * @return A new instance of the implementing class
   */
  public abstract T project(String project);

  /**
   * Set the member making the search (required for project searches only)
   *
   * @param member The id or username of the member making the search
   *
   * @return A new instance of the implementing class
   */
  public abstract T member(String member);

  /**
   * Set the scope of the search to a project.
   *
   * @param project The name of the project
   * @param groups  The list of groups within that project
   *
   * @return A new instance of the implementing class
   */
  public abstract T project(String project, List<String> groups);

  /**
   *
   * @return The name of the service that will be invoked for this search.
   */
  public abstract String service();

  /**
   *
   * @return The parameter map corresponding to this search.
   */
  public abstract Map<String, String> toParameters();

  /**
   * Convenience method to make a request from this search.
   *
   * <p>This method automatically creates a new request using the service and parameters.</p>
   *
   * @return The corresponding the response.
   */
  public Request request() {
    Map<String, String> parameters = toParameters();
    String service = service();
    return new Request(Method.GET, service).parameters(parameters);
  }

  /**
   * Convenience method to make a request from this search.
   *
   * <p>This method automatically creates a new request using the service and parameters.
   * using the specified client.
   *
   * @param client The HTTP client
   *
   * @return The corresponding the response.
   */
  public HttpRequest request(HttpClient client) {
    Map<String, String> parameters = toParameters();
    String service = service();
    return client.newRequest(Method.GET, service).parameters(parameters);
  }

  /**
   * Convenience method to make a request from this search and directly return
   * the response using the specified credentials.
   *
   * @param credentials The credentials
   *
   * @return The corresponding the response.
   */
  public Response response(PSCredentials credentials) {
    return request().using(credentials).response();
  }

  /**
   * Convenience method to make a request from this search and directly return
   * the response using the specified credentials.
   *
   * @param credentials The credentials
   *
   * @return The corresponding the response.
   */
  public HttpResponse response(HttpClient client, PSCredentials credentials) {
    return request(client).using(credentials).response();
  }
}
