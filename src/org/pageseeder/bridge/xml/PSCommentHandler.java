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

import org.pageseeder.bridge.model.PSComment;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles XML for services returning comments.
 *
 * @author Christophe Lauret
 * @version 0.3.0
 */
public final class PSCommentHandler extends DefaultHandler {

  /**
   * The current comment being processed.
   */
  private PSComment comment = null;

  /**
   * The list of comments returned by the servlet.
   */
  private List<PSComment> comments = new ArrayList<PSComment>();

  /**
   * To capture text data.
   */
  private StringBuilder buffer = new StringBuilder();

  /**
   * Create a new handler for comments.
   */
  public PSCommentHandler() {
  }

  /**
   * Create a new handler for document belong to a specific group.
   *
   * @param comment A comment to update.
   */
  public PSCommentHandler(PSComment comment) {
    this.comment = comment;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("comment".equals(localName)) {
      PSComment comment = PSEntityFactory.toComment(atts, this.comment);
      this.comment = comment;
    } else if ("title".equals(localName)) {
      // TODO
    } else if ("author".equals(localName)) {
      // TODO
    } else if ("assignedto".equals(localName)) {
      // TODO
    } else if ("content".equals(localName)) {
      // TODO
    } else if ("attachment".equals(localName)) {
      // TODO
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("comment".equals(localName)) {
      if (this.comment != null) this.comments.add(this.comment);
    }
  }

  /**
   * @return the list of comments
   */
  public List<PSComment> listComments() {
    return this.comments;
  }

  /**
   * @return the last comment processed
   */
  public PSComment getComment() {
    int size = this.comments.size();
    return size > 0? this.comments.get(size-1) : null;
  }

}
