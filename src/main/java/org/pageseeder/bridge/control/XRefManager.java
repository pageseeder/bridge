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
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSURI;
import org.pageseeder.bridge.model.PSXRef;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.xml.PSXRefHandler;

/**
 * A manager for XRefs (based on PageSeeder XRefs).
 *
 * @author Philip Rutherford
 *
 * @version 0.10.2
 * @since 0.8.1
 */
public final class XRefManager extends Sessionful {

  /**
   * Where the documents are cached.
   */
  private static volatile PSEntityCache<PSXRef> cache = EHEntityCache.newInstance("psxrefs");


  /**
   * Creates a new manager for PageSeeder XRefs.
   *
   * @param credentials The user that can connect to PageSeeder.
   */
  public XRefManager(PSCredentials credentials) {
    super(credentials);
  }

  /**
   * List forward XRefs for a URI (up to 1000).
   *
   * @param group     The context group
   * @param uri       The URI
   *
   * @return the list of XRefs found (never <code>null</code>)
   */
  public List<PSXRef> listXRefs(PSGroup group, PSURI uri) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.listXRefs(group, uri, null, true, false, null, 1, 1000).using(this._credentials);
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
   * List XRefs for a URI.
   *
   * @param group          The context group
   * @param uri            The URI
   * @param includetypes   list of types of XRef to includes (null means all)
   * @param forward        whether to include forward XRefs
   * @param reverse        whether to include reverse XRefs
   * @param version        version of document (null means current)
   * @param page           the page to load
   * @param pagesize       the number of results per page
   *
   * @return the list of XRefs found (never <code>null</code>)
   */
  public List<PSXRef> listXRefs(PSGroup group, PSURI uri, List<PSXRef.Type> includetypes,
      boolean forward, boolean reverse, String version, int page, int pagesize) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.listXRefs(group, uri, includetypes,
        forward, reverse, version, page, pagesize).using(this._credentials);
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
