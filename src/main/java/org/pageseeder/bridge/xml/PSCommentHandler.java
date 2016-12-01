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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.model.PSComment;
import org.pageseeder.bridge.model.PSComment.Attachment;
import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSExternalURI;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSURI;
import org.pageseeder.bridge.util.Rules;
import org.pageseeder.xmlwriter.XML.NamespaceAware;
import org.pageseeder.xmlwriter.XMLStringWriter;
import org.pageseeder.xmlwriter.XMLWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles XML for services returning comments.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.3.0
 */
public final class PSCommentHandler extends DefaultHandler {

  /**
   * The current comment being processed.
   */
  private @Nullable PSComment comment = null;

  /**
   * Attachments for current comment
   */
  private List<Attachment> attachments = new ArrayList<>();

  /**
   * The list of comments returned by the servlet.
   */
  private List<PSComment> comments = new ArrayList<>();

  /**
   * To capture text data.
   */
  private @Nullable StringBuilder buffer = null;

  /**
   * To capture XML content.
   */
  private @Nullable XMLStringWriter xmlContent = null;

  /**
   * Indicates whether the handler is within an 'attachment' element.
   */
  private boolean attachment;

  /**
   * The fragment ID for context and attachments
   */
  private @Nullable String fragment = null;

  /**
   * Whether inside author element
   */
  private boolean inAuthor = false;

  /**
   * Author email (non member)
   */
  private @Nullable String authorEmail = null;

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
    XMLStringWriter xml = this.xmlContent;
    if (xml != null) {
      xml.openElement(uri, localName, true);
      for (int i = 0; i < atts.getLength(); i++) {
        String name = atts.getLocalName(i);
        String value = atts.getValue(i);
        if (name != null && value != null) {
          xml.attribute(atts.getURI(i), name, value);
        }
      }
    }
    if ("comment".equals(localName)) {
      PSComment comment = PSEntityFactory.toComment(atts, this.comment);
      this.comment = comment;

    } else {
      PSComment com = this.comment;
      if (com != null) {
        if ("title".equals(localName) || "labels".equals(localName) || ("fullname".equals(localName) && this.inAuthor)) {
          this.buffer = new StringBuilder();

        } else if ("author".equals(localName)) {
          // If an 'id' is specified, it is a PageSeeder member
          if (atts.getValue("id") != null) {
            PSMember member = PSEntityFactory.toMember(atts, null);
            com.setAuthor(member);
          } else {
            this.inAuthor = true;
            this.authorEmail = atts.getValue("email");
          }

        } else if ("assignedto".equals(localName)) {
          PSMember member = PSEntityFactory.toMember(atts, null);
          com.setAssignedto(member);

        } else if ("content".equals(localName)) {
          String type = atts.getValue("type");
          if (type == null) {
            type= "text/plain";
          }
          com.setMediaType(type);
          if (Rules.isXMLMediaType(type)) {
            this.xmlContent = new XMLStringWriter(NamespaceAware.No);
          } else {
            this.buffer = new StringBuilder();
          }

        } else if ("context".equals(localName)) {
          this.fragment = atts.getValue("fragment");

        } else if ("attachment".equals(localName)) {
          this.attachment = true;
          this.fragment = atts.getValue("fragment");

        } else if ("group".equals(localName)) {
          PSGroup group = PSEntityFactory.toGroup(atts, null);
          com.setContext(group);

        } else if ("uri".equals(localName)) {
          PSURI u = null;
          if ("true".equals(atts.getValue("external"))) {
            u = PSEntityFactory.toExternalURI(atts, null);
          } else {
            u = PSEntityFactory.toDocument(atts, null);
          }
          String frag = this.fragment; // XXX Should we default to `default` instead of null?
          if (frag != null) {
            if (this.attachment) {
              Attachment attach = new Attachment(u, frag);
              this.attachments.add(attach);
            } else if ("true".equals(atts.getValue("external"))) {
              com.setContext((PSExternalURI)u, frag);
            } else {
              com.setContext((PSDocument)u, frag);
            }
          }
        }
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    PSComment comment = this.comment;
    if ("comment".equals(localName)) {
      if (comment != null) {
        comment.setAttachments(this.attachments);
        this.attachments = new ArrayList<>();
        this.comments.add(comment);
      }
      this.comment = null;
    } else if (comment != null) {
      StringBuilder buf = this.buffer;
      if ("title".equals(localName)) {
        if (buf != null) {
          comment.setTitle(buf.toString());
          this.buffer = null;
        }

      } else if ("labels".equals(localName)) {
        if (buf != null) {
          comment.setLabels(buf.toString());
          this.buffer = null;
        }

      } else if (("fullname".equals(localName) && this.inAuthor)) {
        String email = this.authorEmail;
        if (buf != null && email != null) {
          comment.setAuthor(buf.toString(), email);
          this.buffer = null;
        }

      } else if ("author".equals(localName)) {
        this.inAuthor = false;

      } else if ("content".equals(localName)) {
        XMLWriter xml = this.xmlContent;
        if (xml != null) {
          comment.setContent(xml.toString());
        } else if (buf != null) {
          comment.setContent(buf.toString());
        }
        this.buffer = null;
        this.xmlContent = null;

      } else if ("context".equals(localName)) {
        this.fragment = null;
      } else if ("attachment".equals(localName)) {
        this.attachment = false;
        this.fragment = null;
      }
    }

    XMLStringWriter xml = this.xmlContent;
    if (xml != null) {
      xml.closeElement();
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    XMLStringWriter xml = this.xmlContent;
    StringBuilder buf = this.buffer;
    if (xml != null) {
      xml.writeText(ch, start, length);
    } else if (buf != null) {
      buf.append(ch, start, length);
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
  public @Nullable PSComment getComment() {
    int size = this.comments.size();
    return size > 0? this.comments.get(size-1) : null;
  }

}
