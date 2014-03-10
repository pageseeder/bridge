/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.xml;

import java.util.ArrayList;
import java.util.List;

import org.pageseeder.bridge.core.PSGroupFolder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for group folders.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public class PSGroupFolderHandler extends DefaultHandler {

  /**
   * The current group folder being processed.
   */
  private PSGroupFolder folder = null;

  /**
   * The list of group folders returned by the servlet.
   */
  List<PSGroupFolder> folders = new ArrayList<PSGroupFolder>();

  /**
   * Create a new handler.
   */
  public PSGroupFolderHandler() {
  }

  /**
   * Create a new handler for document belong to a specific group.
   *
   * @param folder The group folder
   */
  public PSGroupFolderHandler(PSGroupFolder folder) {
    this.folder = folder;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("groupfolder".equals(localName)) {
      PSGroupFolder folder = PSEntityFactory.toGroupFolder(atts, this.folder);
      this.folder = folder;
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("groupfolder".equals(localName) && this.folder != null) {
      this.folders.add(this.folder);
    }
  }

  /**
   * @return the list of group folders
   */
  public List<PSGroupFolder> listGroupFolders() {
    return this.folders;
  }

  /**
   * @return the list of group folders
   */
  public PSGroupFolder getGroupFolder() {
    int size = this.folders.size();
    return size > 0? this.folders.get(size-1) : null;
  }
}
