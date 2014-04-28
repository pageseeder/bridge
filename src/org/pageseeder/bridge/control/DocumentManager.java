/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.control;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSFolder;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.net.PSHTTPConnection;
import org.pageseeder.bridge.net.PSHTTPConnection.Method;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.net.PSHTTPResourceType;
import org.pageseeder.bridge.net.PSHTTPResponseInfo;
import org.pageseeder.bridge.net.PSHTTPResponseInfo.Status;
import org.pageseeder.bridge.net.Servlets;
import org.pageseeder.bridge.psml.PSMLFragment;
import org.pageseeder.bridge.xml.PSDocumentBrowseHandler;
import org.pageseeder.bridge.xml.PSDocumentHandler;
import org.pageseeder.bridge.xml.PSFragmentHandler;
import org.pageseeder.bridge.xml.UploadHandler;
import org.xml.sax.helpers.DefaultHandler;

import com.topologi.diffx.xml.XMLWriter;

/**
 * A manager for documents and folders (based on PageSeeder URIs).
 *
 * @author Christophe Lauret
 * @version 0.3.0
 * @since 0.2.0
 */
public final class DocumentManager extends Sessionful {

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
   * @param group    The group the document is part of
   * @param creator  The member creating the document
   *
   * @return <code>true</code> if the document was created.
   */
  public boolean create(PSDocument document, PSGroup group, PSMember creator)
      throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.createDocument(document, group, creator, null);
    PSDocumentHandler handler = new PSDocumentHandler(document);
    connector.setUser(this._session);
    PSHTTPResponseInfo info = connector.post(handler);
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Create the specified document in PageSeeder passing the specified template parameters.
   *
   * @param document   The document to create
   * @param group    The group the document is part of
   * @param creator    The member creating the document
   * @param parameters The parameters for the PSML template
   *
   * @return <code>true</code> if the document was created.
   */
  public boolean create(PSDocument document, PSGroup group, PSMember creator, Map<String, String> parameters)
      throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.createDocument(document, group, creator, parameters);
    PSDocumentHandler handler = new PSDocumentHandler(document);
    connector.setUser(this._session);
    PSHTTPResponseInfo info = connector.post(handler);
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Identify a document from a specific URI ID.
   *
   * @param id    The URI ID of the document.
   * @param group The group the document is accessible from.
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
   * @param url   The URL
   * @param group The group the document is accessible from.
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
   * @param url   The URL
   * @param group The group the folder is part of
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
   * List the documents in the specified group in PageSeeder.
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


  /**
   * Uploads a file on the server at the specified URL.
   *
   * @param group The group the file should be uploaded to
   * @param url   The URL of the folder receiving the file
   * @param file  The file to upload
   *
   * @return The uploaded document.
   *
   * @throws APIException
   */
  public PSDocument upload(PSGroup group, String url, File file) throws APIException {
    PSHTTPResponseInfo response = new PSHTTPResponseInfo();
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVLET, Servlets.UPLOAD_SERVLET).using(this._session);
    UploadHandler handler = new UploadHandler();
    try {
      connector.addParameter("autoload", "true");
      connector.addParameter("group", group.getName());
      connector.addParameter("url", url);

      // Attach part
      PSHTTPConnection connection = connector.connect(Method.MULTIPART);
      connection.addPart(file);

      // Process
      connection.process(response, handler);

    } catch (IOException ex) {
      throw new APIException(ex);
    }
    return handler.getDocument();
  }

  /**
   * Create the specified document in PageSeeder.
   *
   * @param document The document to create
   * @param group    The group the document is part of
   * @param editor   The member editing the document
   * @param fragment The fragment ID
   *
   * @return the corresponding fragment.
   */
  public PSMLFragment getFragment(PSDocument document, PSGroup group, PSMember editor, String fragment)
      throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.getFragment(document, group, editor, fragment).using(this._session);
    PSFragmentHandler handler = new PSFragmentHandler();
    connector.get(handler);
    return handler.getFragment();
  }

  /**
   * Create the specified document in PageSeeder.
   *
   * @param document The document to create
   * @param group    The group the document is part of
   * @param editor   The member editing the document
   * @param fragment The fragment to edit
   *
   * @return the updated fragment.
   */
  public PSMLFragment putFragment(PSDocument document, PSGroup group, PSMember editor, PSMLFragment fragment)
      throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.putFragment(document, group, editor, fragment).using(this._session);
    PSFragmentHandler handler = new PSFragmentHandler();
    connector.put(handler);
    return handler.getFragment();
  }

  /**
   * Write the content of the specified document onto the XML writer.
   *
   * @param uri The URI ID of the document.
   * @param xml The XML to write the content
   */
  public void getContent(Long uri, XMLWriter xml) throws APIException {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.RESOURCE, "/ps/uri/"+uri).using(this._session);
    connector.get(xml);
  }

  /**
   * Write the content of the specified document onto the XML writer.
   *
   * @param docid The document ID to find
   * @param xml   The XML to write the content
   */
  public void getContent(String docid, XMLWriter xml) throws APIException {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.RESOURCE, "/ps/docid/"+docid).using(this._session);
    connector.get(xml);
  }

  /**
   * Write the content of the specified document onto the XML writer.
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
