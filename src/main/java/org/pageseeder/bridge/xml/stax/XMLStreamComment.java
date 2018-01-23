/*
 * Copyright 2017 Allette Systems (Australia)
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
package org.pageseeder.bridge.xml.stax;

import org.pageseeder.bridge.core.*;
import org.pageseeder.bridge.xml.InvalidElementException;
import org.pageseeder.bridge.xml.MissingAttributeException;
import org.pageseeder.bridge.xml.MissingElementException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public final class XMLStreamComment extends BasicXMLStreamHandler<Comment> implements XMLStreamHandler<Comment> {

  public XMLStreamComment() {
    super("comment");
  }

  @Override
  public Comment get(XMLStreamReader xml) throws XMLStreamException {
    if (isOnElement(xml)) {
      Comment.Builder comment = new Comment.Builder();
      long id = attribute(xml, "id", -1L);
      if (id == -1L) throw new MissingAttributeException("Missing comment ID");
      long discussionId = attribute(xml, "discussionid", -1L);
      String contentRole = attribute(xml, "contentrole", "");
      String status = attribute(xml, "status", "");
      String priority = attribute(xml, "priority", "");
      String type = attribute(xml, "type", "");
      CommentProperties properties = CommentProperties.parse(attribute(xml, "properties", ""));

      boolean isDraft = "true".equals(attribute(xml, "draft", "false"));
      boolean isModerated = "true".equals(attribute(xml, "moderated", "false"));

      String created = optionalAttribute(xml, "created");
      String due = optionalAttribute(xml, "due");

      OffsetDateTime createdDate = null;
      OffsetDateTime dueDate = null;

      if (created != null) createdDate = OffsetDateTime.parse(created);
      if (due != null) dueDate = OffsetDateTime.parse(due);

//      created	xs:dateTime	no	When the comment was created
//      due	xs:dateTime	no	When the task is due when comment was made

      String title = "";
      LabelList labels = LabelList.NO_LABELS;
      Author author = null;
      ModifiedBy modifiedBy = null;
      AssignedTo assignedTo = null;
      List<Content> contents = new ArrayList<>(1);
      List<Attachment> attachments = new ArrayList<>(1);
      Context context = null;
      List<Group> groups = new ArrayList<>(1);

      do {
        xml.next();
        if (xml.isStartElement()) {
          String localName = xml.getLocalName();
          if ("title".equals(localName)) {
            title = xml.getElementText();
          } else if ("labels".equals(localName)) {
            labels = LabelList.parse(xml.getElementText());
          } else if ("author".equals(localName)) {
            author = toAuthor(xml);
          } else if ("modifiedby".equals(localName)) {
            modifiedBy = toModifiedBy(xml);
          } else if ("assignedto".equals(localName)) {
            assignedTo = toAssignedTo(xml);
          } else if ("properties".equals(localName)) {
            // Ignore (we parse from the attribute value)
          } else if ("content".equals(localName)) {
            Content content = toContent(xml);
            contents.add(content);
          } else if ("context".equals(localName)) {
            context = toContext(xml);
          } else if ("attachment".equals(localName)) {
            Attachment attachment = toAttachment(xml);
            attachments.add(attachment);
          } else if ("group".equals(localName)) {
            Group group = new XMLStreamGroup().get(xml);
            groups.add(group);
          }
        }
      } while (!(xml.isEndElement() &&  element().equals(xml.getLocalName())));

      // TODO Fix

      comment.id(id)
             .title(title)
             .type(type)
             .labels(labels)
             .properties(properties)
             .author(author);
  //    comment.groups(groups);
      comment.contents(contents);
      comment.attachments(attachments);
      if (discussionId > 0) comment.discussionId(discussionId);
      if (contentRole != null) comment.contentRole(contentRole);
      if (!priority.isEmpty()) comment.priority(priority);
      if (!status.isEmpty()) comment.status(status);
      if (created != null) comment.created(createdDate);
      if (due != null) comment.due(dueDate);
      if (isDraft) comment.isDraft(true);
      if (isModerated) comment.isModerated(true);
      if (modifiedBy != null) comment.modifiedBy(modifiedBy);
      if (assignedTo != null) comment.assignedTo(assignedTo);
      if (context != null) comment.context(context);

      return comment.build();
    } else throw new InvalidElementException("not a member");
  }

  private Author toAuthor(XMLStreamReader xml) throws XMLStreamException {
    boolean isMember = optionalAttribute(xml, "id") != null;
    if (isMember) {
      Member member = toMember(xml);
      skipToEndElement(xml, "author");
      return new Author(member);
    } else {
      String email = optionalAttribute(xml, "email");
      skipToStartElement(xml);
      String fullname = xml.getElementText();
      skipToEndElement(xml, "author");
      return new Author(fullname, email != null ? new Email(email) : null);
    }
  }

  private ModifiedBy toModifiedBy(XMLStreamReader xml) throws XMLStreamException {
    OffsetDateTime date = OffsetDateTime.parse(attribute(xml, "date"));
    Member member = toMember(xml);
    skipToEndElement(xml, "modifiedby");
    return new ModifiedBy(member, date);
  }

  private AssignedTo toAssignedTo(XMLStreamReader xml) throws XMLStreamException {
    OffsetDateTime date = OffsetDateTime.parse(attribute(xml, "date"));
    Member member = toMember(xml);
    skipToEndElement(xml, "assignedto");
    return new AssignedTo(member, date);
  }

  private Content toContent(XMLStreamReader xml) throws XMLStreamException {
    String mediaType = attribute(xml, "type");
    String content = xml.getElementText();
    // TODO handle XML content types!
    return new Content(content, mediaType);
  }

  private Context toContext(XMLStreamReader xml) throws XMLStreamException {
    skipToStartElement(xml);
    String element = xml.getLocalName();
    if ("group".equals(element)) {
      Group group = new XMLStreamGroup().get(xml);
      skipToEndElement(xml, "context");
      return new Context(group);
    } else if ("uri".equals(element)) {
      String fragment = optionalAttribute(xml, "fragment");
      URI uri = new XMLStreamURI().get(xml);
      skipToEndElement(xml, "context");
      return new Context(uri, fragment);
    }
    skipToEndElement(xml, "context");
    throw new MissingElementException("There should be at least one group or uri in context");
  }

  private Attachment toAttachment(XMLStreamReader xml) throws XMLStreamException {
    String fragment = optionalAttribute(xml, "fragment");
    skipToStartElement(xml);
    if ("uri".equals(xml.getLocalName())) {
      URI uri = new XMLStreamURI().get(xml);
      return new Attachment(uri, fragment);
    } else throw new MissingElementException("Attachment require URI");
  }

  private Member toMember(XMLStreamReader xml) throws XMLStreamException {
    long id = attribute(xml, "id", -1L);
    if (id == -1L) throw new MissingAttributeException("Missing member ID");
    Username username = new Username(attribute(xml, "username"));
    String firstname = attribute(xml, "firstname");
    String surname   = attribute(xml, "surname");
    Email email = new Email(attribute(xml, "email", ""));
    MemberStatus status = MemberStatus.forAttribute(attribute(xml, "status", "unknown"));
    boolean locked = "true".equals(attribute(xml, "locked", "true"));
    boolean onVacation = "true".equals(attribute(xml, "onvacation", "true"));
    boolean attachments = "true".equals(attribute(xml, "attachments", "true"));

    Member member = new Member(id, username, email, firstname, surname, status);
    // these flags aren't frequent so it's OK to create new instances when true
    if (locked) member = member.lock();
    if (onVacation) member = member.isOnVacation(true);
    if (attachments) member = member.hasAttachments(true);
    return member;
  }

}
