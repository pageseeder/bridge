/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.control;

import java.util.List;
import java.util.Map;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSFolder;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSGroupFolder;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.net.PSHTTPResourceType;
import org.pageseeder.bridge.net.PSHTTPResponseInfo;
import org.pageseeder.bridge.net.PSHTTPResponseInfo.Status;
import org.pageseeder.bridge.psml.PSMLFragment;
import org.pageseeder.bridge.xml.PSDocumentBrowseHandler;
import org.pageseeder.bridge.xml.PSDocumentHandler;
import org.pageseeder.bridge.xml.PSFragmentHandler;
import org.xml.sax.helpers.DefaultHandler;

import com.topologi.diffx.xml.XMLWriter;

/**
 * A manager for documents and folders (based on PageSeeder URIs)
 *
 * @author Christophe Lauret
 * @version 0.2.0
 * @since 0.2.0
 */
public class DocumentManager extends Sessionful {

  /**
   * Where the documents are cached.
   */
  private static volatile PSEntityCache<PSDocument> cache = EHEntityCache.newInstance("psdocuments", "docid");

  /**
   * Where the folders are cached.
   */
  private static volatile PSEntityCache<PSFolder> folders = EHEntityCache.newInstance("psfolders", "docid");

  /**
   * Creates a new manager for PageSeeder groups.
   *
   * @param user The user that can connect to PageSeeder.
   */
  public DocumentManager(PSSession user) {
    super(user);
  }

  /**
   * Create the specified document in PageSeeder.
   *
   * @param document The document to create
   * @param creator  The member creating the document
   */
  public void create(PSDocument document, PSGroup group, PSMember creator) throws APIException {
    PSFolder folder = getFolder(document.getFolderURL(), group);
    PSHTTPConnector connector;
    if (folder != null) {
      connector = PSHTTPConnectors.createDocument(document, group, folder, creator, null);
    } else {
      connector = PSHTTPConnectors.createDocument(document, group, creator, null);
    }
    PSDocumentHandler handler = new PSDocumentHandler(document);
    connector.setUser(this._session);
    connector.post(handler);
  }

  /**
   * Create the specified document in PageSeeder passing the specified template parameters.
   *
   * @param document   The document to create
   * @param creator    The member creating the document
   * @param parameters The parameters for the PSML template
   */
  public boolean create(PSDocument document, PSGroup group, PSMember creator, Map<String, String> parameters) throws APIException {
    PSFolder folder = getFolder(document.getFolderURL(), group);
    PSHTTPConnector connector;
    if (folder != null) {
      connector = PSHTTPConnectors.createDocument(document, group, folder, creator, parameters);
    } else {
      connector = PSHTTPConnectors.createDocument(document, group, creator, parameters);
    }
    PSDocumentHandler handler = new PSDocumentHandler(document);
    connector.setUser(this._session);
    PSHTTPResponseInfo info = connector.post(handler);
    return info.getStatus() == Status.OK;
  }

  /**
   * Create the specified document in PageSeeder.
   *
   * @param document The document to create
   * @param creator  The member creating the document
   */
  public boolean create(PSDocument document, PSGroup group, PSGroupFolder folder, PSMember creator) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.createDocument(document, group, folder, creator, null).using(this._session);
    PSDocumentHandler handler = new PSDocumentHandler(document);
    PSHTTPResponseInfo info = connector.post(handler);
    return info.getStatus() == Status.OK;
  }

  /**
   * Create the specified document in PageSeeder.
   *
   * @param document The document to create
   * @param creator  The member creating the document
   */
  public boolean create(PSDocument document, PSGroup group, PSGroupFolder folder, PSMember creator, Map<String, String> parameters) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.createDocument(document, group, folder, creator, parameters).using(this._session);
    PSDocumentHandler handler = new PSDocumentHandler(document);
    PSHTTPResponseInfo info = connector.post(handler);
    return info.getStatus() == Status.OK;
  }

  /**
   * Identify a document from a specific URI ID.
   */
  public PSDocument getDocument(long id, PSGroup group) throws APIException {
    PSDocument document = cache.get(Long.valueOf(id));
    if (document == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getURI(id, group).using(this._session);
      PSDocumentHandler handler = new PSDocumentHandler();
      connector.get(handler);
      document = handler.getDocument();
      if (document != null)
        cache.put(document);
    }
    return document;
  }

  /**
   * Identify a document from a specified URL.
   *
   * @param url The URL
   *
   * @return the corresponding document
   */
  public PSDocument getDocument(String url, PSGroup group) throws APIException {
    PSDocument document = cache.get(url);
    if (document == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getURI(url, group).using(this._session);
      PSDocumentHandler handler = new PSDocumentHandler();
      connector.get(handler);
      document = handler.getDocument();
      if (document != null)
        cache.put(document);
    }
    return document;
  }

  /**
   * Identify a document from a specified URL.
   *
   * @param url The URL
   *
   * @return the corresponding folder
   */
  public PSFolder getFolder(String url, PSGroup group) throws APIException {
    PSFolder folder = folders.get(url);
    if (folder == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getURI(url, group).using(this._session);
      PSDocumentHandler handler = new PSDocumentHandler();
      connector.get(handler);
      folder = handler.getFolder();
      if (folder != null)
        folders.put(folder);
    }
    return folder;
  }

  /**
   * Creates the group the specified group in PageSeeder.
   *
   * @param group The group instance.
   * @return The corresponding instance.
   */
  public List<PSDocument> listDocuments(PSGroup group) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.listDocumentsInGroup(group).using(this._session);
    PSDocumentBrowseHandler handler = new PSDocumentBrowseHandler();
    connector.get(handler);
    return handler.listDocuments();
  }

  public PSMLFragment getFragment(PSDocument document, PSGroup group, PSMember editor, String fragment) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.getFragment(document, group, editor, fragment).using(this._session);
    PSFragmentHandler handler = new PSFragmentHandler();
    connector.get(handler);
    return handler.getFragment();
  }

  public PSMLFragment putFragment(PSDocument document, PSGroup group, PSMember editor, PSMLFragment fragment) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.putFragment(document, group, editor, fragment).using(this._session);
    PSFragmentHandler handler = new PSFragmentHandler();
    connector.post(handler);
    return handler.getFragment();
  }

  /**
   * Create the specified document in PageSeeder.
   */
  public void getContent(Long uri, XMLWriter xml) throws APIException {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.RESOURCE, "/ps/uri/"+uri).using(this._session);
    connector.get(xml);
  }

  /**
   * Create the specified document in PageSeeder.
   *
   * @param docid The document ID to find
   * @param xml   The XML to write the content
   */
  public void getContent(String docid, XMLWriter xml) throws APIException {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.RESOURCE, "/ps/docid/"+docid).using(this._session);
    connector.get(xml);
  }

  /**
   * Create the specified document in PageSeeder.
   *
   * @param docid   The document ID to find
   * @param handler The handler to handle the content
   */
  public void getContent(String docid, DefaultHandler handler) throws APIException {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.RESOURCE, "/ps/docid/"+docid).using(this._session);
    connector.get(handler);
  }

  /**
   * @return the internal cache used for the groups.
   */
  public static PSEntityCache<PSDocument> getCache() {
    return cache;
  }

  /**
   * @return the internal cache used for the group folders.
   */
  public static PSEntityCache<PSFolder> getFoldersCache() {
    return folders;
  }
}
