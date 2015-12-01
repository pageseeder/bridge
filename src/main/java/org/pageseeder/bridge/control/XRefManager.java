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
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSURI;
import org.pageseeder.bridge.model.PSXRef;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.net.PSHTTPResponseInfo;
import org.pageseeder.bridge.net.PSHTTPResponseInfo.Status;
import org.pageseeder.bridge.xml.PSXRefHandler;

/**
 * A manager for XRefs (based on PageSeeder XRefs).
 *
 * @author Philip Rutherford
 * @since 0.8.1
 */
public final class XRefManager extends Sessionful {

  /**
   * Where the documents are cached.
   */
  private static volatile PSEntityCache<PSXRef> cache = EHEntityCache.newInstance("psxrefs");;


  /**
   * Creates a new manager for PageSeeder XRefs.
   *
   * @param user The user that can connect to PageSeeder.
   */
  public XRefManager(PSSession user) {
    super(user);
  }

  /**
   * Create the specified XRef in PageSeeder.
   *
   * @param xref        The XRef to create
   * @param group       The group the external URI is part of
   * @param creator     The member creating the external URI
   *
   * @return <code>true</code> if the xref was created.
   */
  public boolean create(PSXRef xref, PSGroup group, PSMember creator)
      throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.createXRef(xref, group, creator).using(this._session);
    PSXRefHandler handler = new PSXRefHandler(xref);
    PSHTTPResponseInfo info = connector.post(handler);
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * List forward XRefs for a URI.
   *
   * @param group     The context group
   * @param uri       The URI
   *
   * @return the list of XRefs found (never <code>null</code>)
   */
  public List<PSXRef> findComments(PSGroup group, PSURI uri) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.listXRefs(group, uri).using(this._session);
    PSXRefHandler handler = new PSXRefHandler();
    connector.get(handler);
    List<PSXRef> xrefs = handler.listXRefs();
    // store them for later TODO??
    for (PSXRef xref : xrefs) {
      cache.put(xref);
    }
    return xrefs;
  }
  
  /**
   * @return the internal cache used for the external URIs.
   */
  public static PSEntityCache<PSXRef> getCache() {
    return cache;
  }

}
