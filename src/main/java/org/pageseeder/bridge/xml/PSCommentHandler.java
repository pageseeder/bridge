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
package org.pageseeder.bridge.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.pageseeder.bridge.model.PSComment;
import org.pageseeder.bridge.model.PSComment.Author;
import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.util.Rules;
import org.pageseeder.xmlwriter.XMLStringWriter;
import org.pageseeder.xmlwriter.XMLWriter;
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
   * To capture XML content.
   */
  private XMLWriter xmlContent = null;

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

  /**
   * Whether inside author element
   */
  private boolean inAuthor = false;
  
  /**
   * Author email (non member)
   */
  private String authorEmail = null;

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if (this.xmlContent != null) {
      try {
        this.xmlContent.openElement(uri, localName, true);
        for (int i = 0; i < atts.getLength(); i++) {
          this.xmlContent.attribute(atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
        }
      } catch (IOException ex) {
        // shouldn't happen as internal writer
      }
    }
    if ("comment".equals(localName)) {
      PSComment comment = PSEntityFactory.toComment(atts, this.comment);
      this.comment = comment;

    } else if ("title".equals(localName) || "labels".equals(localName) || ("fullname".equals(localName) && inAuthor)) {
      this.buffer = new StringBuilder();

    } else if ("author".equals(localName)) {
      // If an 'id' is specified, it is a PageSeeder member
      if (atts.getValue("id") != null) {
        PSMember member = PSEntityFactory.toMember(atts, null);
        this.comment.setAuthor(member);
      } else {
        this.inAuthor = true;
        this.authorEmail = atts.getValue("email");
      }

    } else if ("assignedto".equals(localName)) {
      PSMember member = PSEntityFactory.toMember(atts, null);
      this.comment.setAssignedto(member);

    } else if ("content".equals(localName)) {
      String type = atts.getValue("type");
      this.comment.setMediaType(type);
      if (Rules.isXMLMediaType(type)) {
        this.xmlContent = new XMLStringWriter(true);
      } else {
        this.buffer = new StringBuilder();
      }

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
      if (this.comment != null) {
        this.comments.add(this.comment);
      }
      this.comment = null;

    } else if ("title".equals(localName)) {
      this.comment.setTitle(this.buffer.toString());
      this.buffer = null;

    } else if ("labels".equals(localName)) {
      this.comment.setLabels(this.buffer.toString());
      this.buffer = null;

    } else if (("fullname".equals(localName)  && inAuthor)) {
      this.comment.setAuthor(this.buffer.toString(), this.authorEmail);
      this.buffer = null;

    } else if ("author".equals(localName)) {
      this.inAuthor = false;

    } else if ("content".equals(localName)) {
      this.comment.setContent(this.xmlContent == null ? this.buffer.toString() : this.xmlContent.toString());
      this.buffer = null;
      this.xmlContent = null;

    } else if ("attachment".equals(localName)) {
      this.attachment = false;
      this.fragment = null;
    }
    if (this.xmlContent != null) {
      try {
        this.xmlContent.closeElement();
      } catch (IOException ex) {
        // shouldn't happen as internal writer
      }
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (this.xmlContent != null) {
      try {
        this.xmlContent.writeText(ch, start, length);
      } catch (IOException ex) {
        // shouldn't happen as internal writer
      }
    } else if (this.buffer != null) {
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
