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
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.model.PSExternalURI;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.net.PSHTTPResponseInfo;
import org.pageseeder.bridge.net.PSHTTPResponseInfo.Status;
import org.pageseeder.bridge.xml.PSExternalURIHandler;

/**
 * A manager for documents and folders (based on PageSeeder URIs).
 *
 * @author Christophe Lauret
 * @author Jean-Baptiste Reure
 * @version 0.3.0
 * @since 0.2.0
 */
public final class ExternalURIManager extends Sessionful {

  /**
   * Where the documents are cached.
   */
  private static volatile PSEntityCache<PSExternalURI> cache = EHEntityCache.newInstance("psexternaluris", "docid");


  /**
   * Creates a new manager for PageSeeder groups.
   *
   * @param user The user that can connect to PageSeeder.
   */
  public ExternalURIManager(PSSession user) {
    super(user);
  }

  /**
   * Create the specified external URI in PageSeeder.
   *
   * @param externaluri The external URI to create
   * @param group       The group the external URI is part of
   * @param creator     The member creating the external URI
   *
   * @return <code>true</code> if the document was created.
   */
  public boolean create(PSExternalURI externaluri, PSGroup group, PSMember creator)
      throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.createExternalURI(externaluri, group, creator).using(this._session);
    PSExternalURIHandler handler = new PSExternalURIHandler(externaluri);
    PSHTTPResponseInfo info = connector.post(handler);
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Identify an external URI from a specific URI ID.
   *
   * @param id    The URI ID of the external URI.
   * @param group The group the external URI is accessible from.
   *
   * @return the corresponding external URI
   */
  public PSExternalURI getExternalURI(long id, PSGroup group) throws APIException {
    PSExternalURI externaluri = cache.get(Long.valueOf(id));
    if (externaluri == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getURI(id, group).using(this._session);
      PSExternalURIHandler handler = new PSExternalURIHandler();
      connector.get(handler);
      externaluri = handler.getExternalURI();
      if (externaluri != null) {
        cache.put(externaluri);
      }
    }
    return externaluri;
  }

  /**
   * Identify an external URI from a specified URL.
   *
   * @param url   The URL
   * @param group The group the external URI is accessible from.
   *
   * @return the corresponding external URI
   */
  public PSExternalURI getExternalURI(String url, PSGroup group) throws APIException {
    PSExternalURI externaluri = cache.get(url);
    if (externaluri == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getURI(url, group).using(this._session);
      PSExternalURIHandler handler = new PSExternalURIHandler();
      connector.get(handler);
      externaluri = handler.getExternalURI();
      if (externaluri != null) {
        cache.put(externaluri);
      }
    }
    return externaluri;
  }

  /**
   * @return the internal cache used for the external URIs.
   */
  public static PSEntityCache<PSExternalURI> getCache() {
    return cache;
  }

}
