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

import java.util.List;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSPredicate;
import org.pageseeder.bridge.model.PSResult;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.xml.PSResultHandler;

/**
 * A search tool backed by the GenericSearch servlet to search entities in PageSeeder.
 *
 * <p>The search results do not attempt to reconstruct objects but merely invoke the search in
 * PageSeeder and results the index results as fields.
 *
 * @author Christophe Lauret
 * @version 0.2.0
 * @since 0.2.0
 */
public final class PSSearch extends Sessionful {

  /**
   * Creates a new member manager using the specified session.
   *
   * @param session The session used to connect to PageSeeder.
   */
  public PSSearch(PSCredentials credentials) {
    super(credentials);
  }

  /**
   * Returns the results of a search on the specified group on PageSeeder.
   *
   * @param predicate The search predicate
   * @param group     The group within which the search is conducted
   *
   * @return The search results.
   */
  public List<PSResult> find(PSPredicate predicate, PSGroup group) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.find(predicate, group).using(this._credentials);
    PSResultHandler handler = new PSResultHandler();
    connector.get(handler);
    return handler.listResults();
  }

  /**
   * Returns the results of a search on the specified groups on PageSeeder.
   *
   * @param predicate The search predicate
   * @param groups    The list of groups within which the search is conducted
   *
   * @return The search results.

   */
  public List<PSResult> find(PSPredicate predicate, List<PSGroup> groups) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.find(predicate, groups).using(this._credentials);
    PSResultHandler handler = new PSResultHandler();
    connector.get(handler);
    return handler.listResults();
  }

}
