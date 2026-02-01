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
  private static final long serialVersionUID = 1L;

  /** The XLink ID of the comment. */
  private final long _id;

  /** The Discussion ID of the comment. */
  private final long _discussionId;

  /** The title of the comment (required) */
  private final String _title;

  /** The type of the comment to further qualify the comment. */
  private final @Nullable String _type;

  /** The list of labels on the comment. */
  private final LabelList _labels;

  /** The author of the comment. */
  private final Author _author;

  /** The list of labels on the comment. */
  private final List<Content> _contents;

  /** Who and when it was modified */
  private final @Nullable ModifiedBy _modified;

  /** A pipe-separated list of properties as value pairs (e.g. x=1|y=2|) */
  private final CommentProperties _properties;

  /** The member ID of the member the task should be assigned to. */
  private @Nullable AssignedTo _assignedTo;

  /** The type of the comment to further qualify the comment. */
  private final @Nullable String _contentRole;
  // TODO Consider using an enum for this

  private final @Nullable OffsetDateTime _created;

  /** The status for task e.g. 'Open', 'Resolved', 'Closed' */
  private final @Nullable String _status;

  /** The priority for task e.g. 'High', 'Medium', 'Low' */
  private final @Nullable String _priority;

  /** The task due date format is ISO-8601 e.g. 2010-10-25, 2010-10-25T12:26 (defaults to T18:00) */
  private final @Nullable OffsetDateTime _due;

  private final boolean _draft;

  private final boolean _moderated;

  /** The context of this comment */
  private final Context _context;

  /** The list of attachments for this comment */
  private final List<Attachment> _attachments;

  private Comment(long id, long discussionId, String title, @Nullable String type, LabelList labels, Author author, List<Content> contents, @Nullable ModifiedBy modified, CommentProperties properties, @Nullable AssignedTo assignedTo, @Nullable String contentRole, @Nullable OffsetDateTime created, @Nullable String status, @Nullable String priority, @Nullable OffsetDateTime due, boolean draft, boolean moderated, Context context, List<Attachment> attachments) {
    this._id = id;
    this._discussionId = discussionId;
    this._title = title;
    this._type = type;
    this._labels = labels;
    this._author = author;
    this._contents = contents;
    this._modified = modified;
    this._properties = properties;
    this._assignedTo = assignedTo;
    this._contentRole = contentRole;
    this._created = created;
    this._status = status;
    this._priority = priority;
    this._due = due;
    this._draft = draft;
    this._moderated = moderated;
    this._context = context;
    this._attachments = attachments;
  }

  /**
   * @return the id
   */
  public long getId() {
    return this._id;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return this._title;
  }

  /**
   * @return the content
   */
  public Content getContent() {
    return this._contents.iterator().next();
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
    return this._type;
  }

  /**
   * @return the list of attachments
   */
  public boolean hasAttachments() {
    return !this._attachments.isEmpty();
  }

  /**
   * @return the list of attachments (unmodifiable)
   */
  public List<Attachment> getAttachments() {
    return Collections.unmodifiableList(this._attachments);
  }

  /**
   * @return the labels
   */
  public boolean hasLabels() {
    return !this._labels.isEmpty();
  }

  /**
   * @return the labels
   */
  public LabelList getLabels() {
    return this._labels;
  }

  /**
   * @return the properties
   */
  public CommentProperties getProperties() {
    return this._properties;
  }

  /**
   * @return the author
   */
  public Author getAuthor() {
    return this._author;
  }

  /**
   * @return the context
   */
  public @Nullable Context getContext() {
    return this._context;
  }

  /**
   * @return the status
   */
  public @Nullable String getStatus() {
    return this._status;
  }

  /**
   * @return the priority
   */
  public @Nullable String getPriority() {
    return this._priority;
  }

  /**
   * @return the assignedto
   */
  public @Nullable AssignedTo getAssignedTo() {
    return this._assignedTo;
  }

  /**
   * @return the due
   */
  public @Nullable OffsetDateTime getDue() {
    return this._due;
  }

  public long getDiscussionId() {
    return this._discussionId;
  }

  public ModifiedBy getModified() {
    return _modified;
  }

  public String getContentRole() {
    return _contentRole;
  }

  public OffsetDateTime getCreated() {
    return _created;
  }

  public boolean isDraft() {
    return _draft;
  }

  public boolean isModerated() {
    return _moderated;
  }

  @Override
  public String toString() {
    return "Comment("+getId()+":"+getTitle()+")";
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("comment");
    //    id	xs:long	yes	The ID of the comment in PageSeeder
    if (this._id > 0)
      xml.attribute("id", Long.toString(this._id));
    if (this._discussionId > 0)
      xml.attribute("discussionid", Long.toString(this._discussionId));
    if (this._contentRole != null)
      xml.attribute("contentrole", this._contentRole);
    if (this._created != null)
      xml.attribute("created", this._created.toString()); // TODO date format
    if (this._draft)
      xml.attribute("draft", "true");
    if (this._moderated)
      xml.attribute("moderated", "true");
    if (!this._properties.isEmpty())
      xml.attribute("properties", this._properties.toString());
    if (this._due != null)
      xml.attribute("due", this._due.toString());
    if (this._status != null)
      xml.attribute("status", this._status);
    if (this._priority != null)
      xml.attribute("priority", this._priority);
    if (this._type != null)
      xml.attribute("type", this._type);
    // TODO
    xml.element("title", this._title);
    if (!this._labels.isEmpty())
      xml.element("labels", this._labels.toString());
    this._author.toXML(xml);
    if (this._modified != null)
      this._modified.toXML(xml);
    if (this._assignedTo != null)
      this._assignedTo.toXML(xml);
    if (!this._properties.isEmpty())
      this._properties.toXML(xml);
    for (Content c : this._contents) {
      c.toXML(xml);
    }
    this._context.toXML(xml);
    for (Attachment a : _attachments) {
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
    private List<Content> contents = null;
    private List<Attachment> attachments = null;

    /**
     * @param id the id to set
     */
    public Builder id(long id) {
      this.id = id;
      return this;
    }

    /**
     * @param id the id to set
     */
    public Builder discussionId(long id) {
      this.discussionId = id;
      return this;
    }

    /**
     * @param title the title to set
     */
    public Builder title(String title) {
      this.title = title;
      return this;
    }

    /**
     * @param contentRole the content role to set
     */
    public Builder contentRole(String contentRole) {
      this.contentRole = contentRole;
      return this;
    }

    /**
     * @param content the content to set
     */
    public Builder content(String content, String type) {
      // TODO
      this.contents = Collections.singletonList(new Content(content, type));
      return this;
    }

    /**
     * @param type the type to set
     */
    public Builder type(@Nullable String type) {
      this.type = type;
      return this;
    }

    /**
     * @param status the status to set
     */
    public Builder status(@Nullable String status) {
      this.status = status;
      return this;
    }

    /**
     * @param priority the priority to set
     */
    public Builder priority(@Nullable String priority) {
      this.priority = priority;
      return this;
    }

    /**
     * @param assignedto the assignedto to set
     */
    public Builder assignedTo(Member assignedto, OffsetDateTime date) {
      this.assignedto = new AssignedTo(assignedto, date);
      return this;
    }

    /**
     * @param assignedTo the assignedto to set
     */
    public Builder assignedTo(AssignedTo assignedTo) {
      this.assignedto = assignedTo;
      return this;
    }

    /**
     * @param modifiedBy the assignedto to set
     */
    public Builder modifiedBy(ModifiedBy modifiedBy) {
      this.modifiedBy = modifiedBy;
      return this;
    }

    /**
     * @param created the created to set
     */
    public Builder created(@Nullable OffsetDateTime created) {
      this.created = created;
      return this;
    }

    /**
     * @param due the due to set
     */
    public Builder due(@Nullable OffsetDateTime due) {
      this.due = due;
      return this;
    }

    /**
     * Adds the specified document as an attachment.
     *
     * @param document The document to attach to the comment.
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
     *
     */
    public Builder contents(List<Content> contents) {
      if (contents.isEmpty()) this.contents = Collections.emptyList();
      else if (contents.size() == 1) this.contents = Collections.singletonList(contents.get(0));
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
