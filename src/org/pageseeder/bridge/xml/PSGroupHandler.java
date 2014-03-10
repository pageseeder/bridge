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

import org.pageseeder.bridge.core.PSGroup;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for PageSeeder groups and projects.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public class PSGroupHandler extends DefaultHandler {

  /**
   * The current group being processed.
   */
  PSGroup group = null;

  /**
   * The list of groups found during last parsing (may include projects)
   */
  List<PSGroup> groups = new ArrayList<PSGroup>();

  public PSGroupHandler() {
  }

  /**
   * A new handler to fill up the values of an incomplete group (or project).
   *
   * @param group the group or project to update
   */
  public PSGroupHandler(PSGroup group) {
    this.group = group;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("group".equals(localName)) {
      PSGroup group = PSEntityFactory.toGroup(atts, this.group);
      this.group = group;
    } else if ("project".equals(localName)) {
      PSGroup group = PSEntityFactory.toProject(atts, this.group);
      this.group = group;
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("group".equals(localName)) {
      this.groups.add(this.group);
    }
  }

  /**
   * @return the list of groups
   */
  public List<PSGroup> listGroups() {
    return this.groups;
  }

  /**
   * @return the last group that was processed
   */
  public PSGroup getGroup() {
    return this.group;
  }

}
