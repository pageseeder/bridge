/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.control;

import java.util.List;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.PSSession;
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
  public PSSearch(PSSession session) {
    super(session);
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
    PSHTTPConnector connector = PSHTTPConnectors.find(predicate, group).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.find(predicate, groups).using(this._session);
    PSResultHandler handler = new PSResultHandler();
    connector.get(handler);
    return handler.listResults();
  }

}
