/*
 * Copyright 2015-16 Allette Systems (Australia)
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.FailedPrecondition;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.PSEntityCache;
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
import org.pageseeder.bridge.xml.PSDocumentHandler;
import org.pageseeder.bridge.xml.PSFragmentHandler;
import org.pageseeder.xmlwriter.XMLWriter;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A manager for documents and folders (based on PageSeeder URIs).
 *
 * @author Philip Rutherford
 * @author Christophe Lauret
 *
 * @version 0.10.2
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
   * @param credentials The credentials to connect to PageSeeder.
   */
  public DocumentManager(PSCredentials credentials) {
    super(credentials);
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
    PSHTTPConnector connector = PSHTTPConnectors.createDocument(document, group, creator, null).using(this._credentials);
    PSDocumentHandler handler = new PSDocumentHandler(document);
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
    PSHTTPConnector connector = PSHTTPConnectors.createDocument(document, group, creator, parameters).using(this._credentials);
    PSDocumentHandler handler = new PSDocumentHandler(document);
    PSHTTPResponseInfo info = connector.post(handler);
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Edit the properties for specified document in PageSeeder.
   *
   * @param document The document to create
   * @param group    The group the document is part of
   * @param creator  The member creating the document
   *
   * @return <code>true</code> if the document properties was edit.
   */
  public boolean editDocumentProperties(PSDocument document, PSGroup group, PSMember creator)
      throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.patchDocumentProperties(document, group, creator).using(this._credentials);
    PSHTTPResponseInfo info = connector.patch();
    // FIXME the return xml from "editDocumentProperties" is not a complete XML to create Document object.
    if (info.getStatus() == Status.SUCCESSFUL) {
      cache.remove(String.valueOf(document.getId()));
    }
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Identify a document from a specific URI ID.
   *
   * @param id    The URI ID of the document.
   * @param group The group the document is accessible from.
   */
  public @Nullable PSDocument getDocument(long id, PSGroup group) throws APIException {
    PSDocument document = cache.get(id);
    if (document == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getURI(id, group).using(this._credentials);
      PSDocumentHandler handler = new PSDocumentHandler();
      connector.get(handler);
      document = handler.getDocument();
      if (document != null) {
        cache.put(document);
      }
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
  public @Nullable PSDocument getDocument(String url, PSGroup group) throws APIException {
    PSDocument document = cache.get(url);
    if (document == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getURI(url, group).using(this._credentials);
      PSDocumentHandler handler = new PSDocumentHandler();
      connector.get(handler);
      document = handler.getDocument();
      if (document != null) {
        cache.put(document);
      }
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
  public @Nullable PSFolder getFolder(String url, PSGroup group) throws APIException {
    PSFolder folder = folders.get(url);
    if (folder == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getURI(url, group).using(this._credentials);
      PSDocumentHandler handler = new PSDocumentHandler();
      connector.get(handler);
      folder = handler.getFolder();
      if (folder != null) {
        folders.put(folder);
      }
    }
    return folder;
  }

  /**
   * List the documents in the specified group in PageSeeder at the top level (maximum returned 200).
   *
   * @param group The group instance.
   * @return the documents
   */
  public List<PSDocument> listDocuments(PSGroup group) throws APIException {
    String groupName = checkNotNull(group.getName(), "group name");
    PSConfig p = PSConfig.getDefault();
    String url = p.getScheme() + "://" + p.getHost() + p.getSitePrefix() + "/" + groupName.replace('-', '/');
    PSHTTPConnector connector = PSHTTPConnectors.listDocumentsInGroup(group, url, 200).using(this._credentials);
    PSDocumentHandler handler = new PSDocumentHandler();
    connector.get(handler);
    return handler.listDocuments();
  }

  /**
   * List the documents in the specified group in PageSeeder under a folder.
   *
   * @param group    the group
   * @param folder   the relative folder path (e.g. documents/myfolder)
   * @param max      the maximum number of return documents.
   *
   * @return the documents/folders.
   */
  public List<PSDocument> listDocuments(PSGroup group, String folder, int max) throws APIException {
    String groupName = checkNotNull(group.getName(), "group name");
    PSConfig p = PSConfig.getDefault();
    String url = p.getScheme() + "://" + p.getHost() + p.getSitePrefix() + "/" + groupName.replace('-', '/')
        + "/" + folder;
    PSHTTPConnector connector = PSHTTPConnectors.listDocumentsInGroup(group, url, max).using(this._credentials);
    PSDocumentHandler handler = new PSDocumentHandler();
    connector.get(handler);
    return handler.listDocuments();
  }

  /**
   * List the folders in the specified group in PageSeeder under a folder.
   *
   * @param group    the group
   * @param folder   the relative folder path (e.g. documents/myfolder)
   * @param max      the maximum number of return documents.
   *
   * @return the folders.
   */
  public List<PSFolder> listFolders(PSGroup group, String folder, int max) throws APIException {
    String groupName = checkNotNull(group.getName(), "group name");
    PSConfig p = PSConfig.getDefault();
    String url = p.getScheme() + "://" + p.getHost() + p.getSitePrefix() + "/" + groupName.replace('-', '/')
        + "/" + folder;
    PSHTTPConnector connector = PSHTTPConnectors.listFoldersInGroup(group, url, max).using(this._credentials);
    PSDocumentHandler handler = new PSDocumentHandler();
    connector.get(handler);
    return handler.listFolders();
  }

  /**
   * List the documents in the specified group in PageSeeder under a parent URL.
   *
   * @param group the group
   * @param url   the parent URL
   * @param max   the maximum number of return documents.
   *
   * @return the documents/folders.
   */
  public List<PSDocument> listDocumentsForURL(PSGroup group, String url, int max) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.listDocumentsInGroup(group, url, max).using(this._credentials);
    PSDocumentHandler handler = new PSDocumentHandler();
    connector.get(handler);
    return handler.listDocuments();
  }

  /**
   * List the folders in the specified group in PageSeeder under a parent URL.
   *
   * @param group the group
   * @param url   the parent URL
   * @param max   the maximum number of return documents.
   *
   * @return the documents/folders.
   */
  public List<PSFolder> listFoldersForURL(PSGroup group, String url, int max) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.listFoldersInGroup(group, url, max).using(this._credentials);
    PSDocumentHandler handler = new PSDocumentHandler();
    connector.get(handler);
    return handler.listFolders();
  }

  /**
   * Uploads a file on the server at the specified URL.
   *
   * @param group The group the file should be uploaded to
   * @param url   The URL of the folder receiving the file
   * @param file  The file to upload
   *
   * @return The uploaded document.
   */
  public @Nullable PSDocument upload(PSGroup group, String url, File file) throws APIException {
    String groupName = checkNotNull(group.getName(), "group name");
    PSHTTPResponseInfo response = new PSHTTPResponseInfo();
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVLET, Servlets.UPLOAD_SERVLET).using(this._credentials);
    PSDocumentHandler handler = new PSDocumentHandler();
    try {
      connector.addParameter("autoload", "true");
      connector.addParameter("group", groupName);
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
   * Uploads a file on the server at the specified URL.
   *
   * @param group       The group the file should be uploaded to
   * @param url         The URL of the folder receiving the file
   * @param in          The input stream for file content
   * @param filename    The filename for the file
   *
   * @return The uploaded document.
   */
  public @Nullable PSDocument upload(PSGroup group, String url, InputStream in, String filename) throws APIException {
    String groupName = checkNotNull(group.getName(), "group name");
    PSHTTPResponseInfo response = new PSHTTPResponseInfo();
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVLET, Servlets.UPLOAD_SERVLET).using(this._credentials);
    PSDocumentHandler handler = new PSDocumentHandler();
    try {
      connector.addParameter("autoload", "true");
      connector.addParameter("group", groupName);
      connector.addParameter("url", url);

      // Attach part
      PSHTTPConnection connection = connector.connect(Method.MULTIPART);
      connection.addPart(in, filename);

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
  public @Nullable PSMLFragment getFragment(PSDocument document, PSGroup group, PSMember editor, String fragment)
      throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.getFragment(document, group, editor, fragment).using(this._credentials);
    PSFragmentHandler handler = new PSFragmentHandler(document);
    connector.get(handler);
    return handler.getFragment();
  }

  /**
   * Edit the specified fragment in PageSeeder by using PUT method.
   *
   * @param document The document to create
   * @param group    The group the document is part of
   * @param editor   The member editing the document
   * @param fragment The fragment to edit
   *
   * @return the updated fragment.
   */
  public @Nullable PSMLFragment putFragment(PSDocument document, PSGroup group, PSMember editor, PSMLFragment fragment) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.putFragment(document, group, editor, fragment).using(this._credentials);
    PSFragmentHandler handler = new PSFragmentHandler(document);
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
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.RESOURCE, "/ps/uri/" + uri).using(this._credentials);
    connector.get(xml);
  }

  /**
   * Write the content of the specified document onto the XML writer.
   *
   * @param uri     The URI ID to find
   * @param handler The handler to handle the content
   */
  public void getContent(Long uri, DefaultHandler handler) throws APIException {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.RESOURCE, "/ps/uri/" + uri).using(this._credentials);
    connector.get(handler);
  }

  /**
   * Write the content of the specified document onto the XML writer.
   *
   * @param docid The document ID to find
   * @param xml   The XML to write the content
   */
  public void getContent(String docid, XMLWriter xml) throws APIException {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.RESOURCE, "/ps/docid/" + docid).using(this._credentials);
    connector.get(xml);
  }

  /**
   * Write the content of the specified document onto the XML writer.
   *
   * @param docid   The document ID to find
   * @param handler The handler to handle the content
   */
  public void getContent(String docid, DefaultHandler handler) throws APIException {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.RESOURCE, "/ps/docid/" + docid).using(this._credentials);
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

  /**
   * Precondition requiring the specified object to be non-null.
   *
   * @param o    The object to check for <code>null</code>
   * @param name The name of the object to generate the message.
   *
   * @return The object (not <code>null</code>)
   *
   * @throws FailedPrecondition If the pre-condition failed.
   */
  private static <T> @NonNull T checkNotNull(@Nullable T o, String name) throws FailedPrecondition {
    if (o == null) throw new FailedPrecondition(name + " must not be null");
    return o;
  }

}
