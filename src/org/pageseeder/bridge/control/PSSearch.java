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
 * @version 0.1.0
 */
public final class PSSearch {

  /**
   * The user connecting to the server.
   */
  private final PSSession user;

  /**
   * Sole constructor;
   */
  public PSSearch(PSSession user) {
    this.user = user;
  }

  /**
   * @return the user
   */
  public PSSession user() {
    return this.user;
  }

  /**
   * Makes
   *
   * @param predicate The search predicate
   * @param group     The group within which the search is conducted
   *
   * @return The search ressults.
   *
   * @throws APIException
   */
  public List<PSResult> find(PSPredicate predicate, PSGroup group) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.find(predicate, group);
    PSResultHandler handler = new PSResultHandler();
    connector.setUser(this.user);
    connector.get(handler);
    return handler.listResults();
  }

  /**
  *
  *
  * @param predicate The search predicate
  * @param group     The group within which the search is conducted
  *
  * @return The search ressults.
  *
  * @throws APIException
  */
 public List<PSResult> find(PSPredicate predicate, List<PSGroup> groups) throws APIException {
   PSHTTPConnector connector = PSHTTPConnectors.find(predicate, groups);
   PSResultHandler handler = new PSResultHandler();
   connector.setUser(this.user);
   connector.get(handler);
   return handler.listResults();
 }

}
