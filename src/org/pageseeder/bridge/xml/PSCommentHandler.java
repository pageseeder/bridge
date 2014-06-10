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
import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
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
  private StringBuilder buffer = null;

  /**
   * Indicates whether the handler is within an 'attachment' element.
   */
  private boolean attachment;

  /**
   * The fragment ID for context and attachments
   */
  private String fragment = null;

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
      this.buffer = new StringBuilder();

    } else if ("author".equals(localName)) {
      // If an 'id' is specified, it is a PageSeeder member
      if (atts.getValue("id") != null) {
        PSMember member = PSEntityFactory.toMember(atts, null);
        this.comment.setAuthor(member);
      } else {
        String name = atts.getValue("name");
        String email = atts.getValue("email");
        this.comment.setAuthor(name, email);
      }

    } else if ("assignedto".equals(localName)) {
      PSMember member = PSEntityFactory.toMember(atts, null);
      this.comment.setAssignedto(member);

    } else if ("content".equals(localName)) {
      String type = atts.getValue("type");
      this.comment.setMediaType(type);
      this.buffer = new StringBuilder();

    } else if ("attachment".equals(localName)) {
      this.attachment = true;
      this.fragment = atts.getValue("fragment");

    } else if ("group".equals(localName)) {
      PSGroup group = PSEntityFactory.toGroup(atts, null);
      this.comment.setContext(group);

    } else if ("uri".equals(localName)) {
      PSDocument document = PSEntityFactory.toDocument(atts, null);
      if (this.attachment) {
        this.comment.addAttachment(document, this.fragment);
      } else {
        this.comment.setContext(document);
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("comment".equals(localName)) {
      if (this.comment != null) this.comments.add(this.comment);

    } else if ("title".equals(localName)) {
      this.comment.setTitle(this.buffer.toString());
      this.buffer = null;

    } else if ("content".equals(localName)) {
      this.comment.setContent(this.buffer.toString());
      this.buffer = null;

    } else if ("attachment".equals(localName)) {
      this.attachment = false;
      this.fragment = null;
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (this.buffer != null) {
      this.buffer.append(ch, start, length);
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