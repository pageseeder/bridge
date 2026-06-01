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

package org.pageseeder.bridge.core;

import org.jspecify.annotations.Nullable;
import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Comment implements Serializable, XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 2L;

  /** The XLink ID of the comment. */
  private final long id;

  /** The Discussion ID of the comment. */
  private final long discussionId;

  /** The title of the comment (required) */
  private final String title;

  /** The type of the comment to further qualify the comment. */
  private final @Nullable String type;

  /** The list of labels on the comment. */
  private final LabelList labels;

  /** The author of the comment. */
  private final Author author;

  /** The list of labels on the comment. */
  private final List<Content> contents;

  /** Who and when it was modified */
  private final @Nullable ModifiedBy modified;

  /** A pipe-separated list of properties as value pairs (e.g. x=1|y=2|) */
  private final CommentProperties properties;

  /** The member ID of the member the task should be assigned to. */
  private @Nullable AssignedTo assignedTo;

  /** The type of the comment to further qualify the comment. */
  private final @Nullable String contentRole;

  private final @Nullable OffsetDateTime created;

  /** The status for task e.g. 'Open', 'Resolved', 'Closed' */
  private final @Nullable String status;

  /** The priority for task e.g. 'High', 'Medium', 'Low' */
  private final @Nullable String priority;

  /** The task due date format is ISO-8601 e.g. 2010-10-25, 2010-10-25T12:26 (defaults to T18:00) */
  private final @Nullable OffsetDateTime due;

  private final boolean isDraft;

  private final boolean isModerated;

  /** The context of this comment */
  private final Context context;

  /** The list of attachments for this comment */
  private final List<Attachment> attachments;

  private Comment(long id, long discussionId, String title, @Nullable String type, LabelList labels, Author author, List<Content> contents, @Nullable ModifiedBy modified, CommentProperties properties, @Nullable AssignedTo assignedTo, @Nullable String contentRole, @Nullable OffsetDateTime created, @Nullable String status, @Nullable String priority, @Nullable OffsetDateTime due, boolean isDraft, boolean isModerated, Context context, List<Attachment> attachments) {
    this.id = id;
    this.discussionId = discussionId;
    this.title = title;
    this.type = type;
    this.labels = labels;
    this.author = author;
    this.contents = contents;
    this.modified = modified;
    this.properties = properties;
    this.assignedTo = assignedTo;
    this.contentRole = contentRole;
    this.created = created;
    this.status = status;
    this.priority = priority;
    this.due = due;
    this.isDraft = isDraft;
    this.isModerated = isModerated;
    this.context = context;
    this.attachments = attachments;
  }

  /**
   * @return the id
   */
  public long getId() {
    return this.id;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * @return the content
   */
  public Content getContent() {
    return this.contents.iterator().next();
  }

  /**
   * @return the content
   */
  public String getContentAsString() {
    return getContent().getContent();
  }

  /**
   * @return the mediatype
   */
  public String getContentType() {
    return getContent().getType();
  }

  /**
   * @return the type
   */
  public @Nullable String getType() {
    return this.type;
  }

  /**
   * @return the list of attachments
   */
  public boolean hasAttachments() {
    return !this.attachments.isEmpty();
  }

  /**
   * @return the list of attachments (unmodifiable)
   */
  public List<Attachment> getAttachments() {
    return Collections.unmodifiableList(this.attachments);
  }

  /**
   * @return the labels
   */
  public boolean hasLabels() {
    return !this.labels.isEmpty();
  }

  /**
   * @return the labels
   */
  public LabelList getLabels() {
    return this.labels;
  }

  /**
   * @return the properties
   */
  public CommentProperties getProperties() {
    return this.properties;
  }

  /**
   * @return the author
   */
  public Author getAuthor() {
    return this.author;
  }

  /**
   * @return the context
   */
  public @Nullable Context getContext() {
    return this.context;
  }

  /**
   * @return the status
   */
  public @Nullable String getStatus() {
    return this.status;
  }

  /**
   * @return the priority
   */
  public @Nullable String getPriority() {
    return this.priority;
  }

  /**
   * @return the assignedto
   */
  public @Nullable AssignedTo getAssignedTo() {
    return this.assignedTo;
  }

  /**
   * @return the due
   */
  public @Nullable OffsetDateTime getDue() {
    return this.due;
  }

  public long getDiscussionId() {
    return this.discussionId;
  }

  public ModifiedBy getModified() {
    return modified;
  }

  public String getContentRole() {
    return contentRole;
  }

  public OffsetDateTime getCreated() {
    return created;
  }

  public boolean isDraft() {
    return isDraft;
  }

  public boolean isModerated() {
    return isModerated;
  }

  @Override
  public String toString() {
    return "Comment("+getId()+":"+getTitle()+")";
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("comment");
    //    id	xs:long	yes	The ID of the comment in PageSeeder
    if (this.id > 0)
      xml.attribute("id", Long.toString(this.id));
    if (this.discussionId > 0)
      xml.attribute("discussionid", Long.toString(this.discussionId));
    if (this.contentRole != null)
      xml.attribute("contentrole", this.contentRole);
    if (this.created != null)
      xml.attribute("created", this.created.toString()); // TODO date format
    if (this.isDraft)
      xml.attribute("draft", "true");
    if (this.isModerated)
      xml.attribute("moderated", "true");
    if (!this.properties.isEmpty())
      xml.attribute("properties", this.properties.toString());
    if (this.due != null)
      xml.attribute("due", this.due.toString());
    if (this.status != null)
      xml.attribute("status", this.status);
    if (this.priority != null)
      xml.attribute("priority", this.priority);
    if (this.type != null)
      xml.attribute("type", this.type);
    // TODO
    xml.element("title", this.title);
    if (!this.labels.isEmpty())
      xml.element("labels", this.labels.toString());
    this.author.toXML(xml);
    if (this.modified != null)
      this.modified.toXML(xml);
    if (this.assignedTo != null)
      this.assignedTo.toXML(xml);
    if (!this.properties.isEmpty())
      this.properties.toXML(xml);
    for (Content c : this.contents) {
      c.toXML(xml);
    }
    this.context.toXML(xml);
    for (Attachment a : attachments) {
      a.toXML(xml);
    }
// TODO  <group />

    xml.closeElement();
  }

  public static class Builder {

    private long id = -1;
    private long discussionId = -1;
    private String title = "";
    private @Nullable String type;
    private Author author = null;
    private @Nullable ModifiedBy modifiedBy;
    private CommentProperties properties = CommentProperties.EMPTY;
    private @Nullable AssignedTo assignedto;
    private @Nullable String contentRole;
    private @Nullable OffsetDateTime created;
    private @Nullable String status;
    private @Nullable String priority;
    private @Nullable OffsetDateTime due;
    private boolean draft;
    private boolean moderated;
    private Context context;
    private LabelList labels = LabelList.NO_LABELS;
    private @Nullable List<Content> contents = null;
    private @Nullable List<Attachment> attachments = null;

    /**
     * @param id the id to set
     *
     * @return this builder for chaining
     */
    public Builder id(long id) {
      this.id = id;
      return this;
    }

    /**
     * @param id the id to set
     *
     * @return this builder for chaining
     */
    public Builder discussionId(long id) {
      this.discussionId = id;
      return this;
    }

    /**
     * @param title the title to set
     *
     * @return this builder for chaining
     */
    public Builder title(String title) {
      this.title = title;
      return this;
    }

    /**
     * @param contentRole the content role to set
     *
     * @return this builder for chaining
     */
    public Builder contentRole(String contentRole) {
      this.contentRole = contentRole;
      return this;
    }

    /**
     * @param content the content to set
     * @param type the type of the content
     *
     * @return this builder for chaining
     */
    public Builder content(String content, String type) {
      // TODO
      this.contents = List.of(new Content(content, type));
      return this;
    }

    /**
     * @param type the type to set
     *
     * @return this builder for chaining
     */
    public Builder type(@Nullable String type) {
      this.type = type;
      return this;
    }

    /**
     * @param status the status to set
     *
     * @return this builder for chaining
     */
    public Builder status(@Nullable String status) {
      this.status = status;
      return this;
    }

    /**
     * @param priority the priority to set
     *
     * @return this builder for chaining
     */
    public Builder priority(@Nullable String priority) {
      this.priority = priority;
      return this;
    }

    /**
     * @param assignedTo the assignee to set
     *
     * @return this builder for chaining
     */
    public Builder assignedTo(Member assignedTo, OffsetDateTime date) {
      this.assignedto = new AssignedTo(assignedTo, date);
      return this;
    }

    /**
     * @param assignedTo the assignee to set
     *
     * @return this builder for chaining
     */
    public Builder assignedTo(AssignedTo assignedTo) {
      this.assignedto = assignedTo;
      return this;
    }

    /**
     * @param modifiedBy the modifiedBy to set
     *
     * @return this builder for chaining
     */
    public Builder modifiedBy(ModifiedBy modifiedBy) {
      this.modifiedBy = modifiedBy;
      return this;
    }

    /**
     * @param created the created date to set
     *
     * @return this builder for chaining
     */
    public Builder created(@Nullable OffsetDateTime created) {
      this.created = created;
      return this;
    }

    /**
     * @param due the due to set
     *
     * @return this builder for chaining
     */
    public Builder due(@Nullable OffsetDateTime due) {
      this.due = due;
      return this;
    }

    /**
     * Adds the specified document as an attachment.
     *
     * @param document The document to attach to the comment.
     *
     * @return this builder for chaining
     */
    public Builder attachment(Document document) {
      List<Attachment> a = this.attachments;
      if (a == null) {
        a = new ArrayList<>();
        this.attachments = a;
      }
      a.add(new Attachment(document));
      return this;
    }

    /**
     * Sets the contents for the builder.
     *
     * @param contents the list of {@link Content} objects to set
     * @return this builder for method chaining
     */
    public Builder contents(List<Content> contents) {
      if (contents.isEmpty()) this.contents = List.of();
      else if (contents.size() == 1) this.contents = List.of(contents.get(0));
      else {
        this.contents = new ArrayList<>(contents);
      }
      return this;
    }

    /**
     * Adds the specified document as an attachment.
     *
     * @param document The document to attach to the comment.
     * @param fragment The fragment ID of where the comment is attached (<code>null</code> for default fragment)
     */
    public Builder attachment(Document document, String fragment) {
      List<Attachment> a = this.attachments;
      if (a == null) {
        a = new ArrayList<>();
        this.attachments = a;
      }
      a.add(new Attachment(document, fragment));
      return this;
    }

    /**
     * @param attachments the attachments to set
     */
    public Builder attachments(List<Attachment> attachments) {
      this.attachments = attachments;
      return this;
    }

    /**
     * @param labels the labels to set
     */
    public Builder labels(List<String> labels) {
      this.labels = new LabelList(labels);
      return this;
    }

    /**
     * @param labels The labels as a comma-separated list.
     */
    public Builder labels(LabelList labels) {
      this.labels = labels;
      return this;
    }

    /**
     * @param labels The labels as a comma-separated list.
     */
    public Builder labels(String labels) {
      this.labels = LabelList.parse(labels);
      return this;
    }

    /**
     * @param properties the properties to set
     */
    public Builder properties(String properties) {
      this.properties = CommentProperties.parse(properties);
      return this;
    }

    /**
     * @param properties the properties to set
     */
    public Builder properties(CommentProperties properties) {
      this.properties = properties;
      return this;
    }

    public Builder isDraft(boolean draft) {
      this.draft = draft;
      return this;
    }

    public Builder isModerated(boolean moderated) {
      this.moderated = moderated;
      return this;
    }

    /**
     * Sets the author directly.
     *
     * @param author the author to set
     */
    public Builder author(Author author) {
      this.author = author;
      return this;
    }

    /**
     * Sets the author as a PageSeeder member.
     *
     * <p>Implementation note: This method creates a new <code>Author</code> instance.
     *
     * @param member the member to set as the author
     */
    public Builder setAuthor(Member member) {
      this.author = new Author(member);
      return this;
    }

    /**
     * Sets the author as an external user.
     *
     * <p>Implementation note: This method creates a new <code>Author</code> instance.
     *
     * @param name  the name of the author (required)
     * @param email the email of the author
     */
    public Builder author(String name, @Nullable Email email) {
      this.author = new Author(name, email);
      return this;
    }

    /**
     * Set the context as a group.
     *
     * <p>Implementation note: This method creates a new context instance.
     *
     * @param context The group to use as context.
     */
    public Builder context(Context context) {
      this.context = context;
      return this;
    }

    /**
     * Set the context as a group.
     *
     * <p>Implementation note: This method creates a new context instance.
     *
     * @param group The group to use as context.
     */
    public Builder context(Group group) {
      this.context = new Context(group);
      return this;
    }

    /**
     * Set the context as a document.
     *
     * <p>Implementation note: This method creates a new context instance.
     *
     * @param document The document to use as context
     */
    public Builder context(Document document) {
      this.context = new Context(document);
      return this;
    }

    /**
     * Set the context as an external URI.
     *
     * <p>Implementation note: This method creates a new context instance.
     *
     * @param externaluri The external URI to use as context
     */
    public Builder context(ExternalURI externaluri) {
      this.context = new Context(externaluri);
      return this;
    }

    /**
     * Set the context as a document fragment.
     *
     * <p>Implementation note: This method creates a new context instance.
     *
     * @param document The document
     * @param fragment The document fragment to use as context
     */
    public Builder context(Document document, @Nullable String fragment) {
      this.context = new Context(document, fragment);
      return this;
    }

    /**
     * Set the context as an external URI fragment.
     *
     * <p>Implementation note: This method creates a new context instance.
     *
     * @param externaluri The external URI to use as context
     * @param fragment    The external URI fragment to use as context
     */
    public Builder context(ExternalURI externaluri, @Nullable String fragment) {
      this.context = new Context(externaluri, fragment);
      return this;
    }

    public Comment build() {
      return new Comment(id, discussionId, title,type, labels, author,contents, modifiedBy, properties, assignedto, contentRole, created, status, priority, due, draft, moderated, context, attachments);
    }

  }

}
