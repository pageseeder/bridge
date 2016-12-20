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
package org.pageseeder.bridge.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSEntity;

/**
 * Represents a PageSeeder comment.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.2.0
 */
public final class PSComment implements PSEntity {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /** The XLink ID of the comment. */
  private @Nullable Long id;

  /** The title of the comment (required) */
  private @Nullable String title;

  /** The content of the comment (required) */
  private @Nullable String content;

  /** The content type of the comment, defaults to 'text/plain' */
  private String mediatype = "text/plain";

  /** The type of the comment to further qualify the comment. */
  private @Nullable String type = null;

  /** The list of labels on the comment. */
  private List<String> labels = new ArrayList<>();

  /** A pipe-separated list of properties as value pairs (e.g. x=1|y=2|) */
  private @Nullable Map<String, String> properties;

  /** The author of the comment. */
  private @Nullable Author author;

  /** The status for task e.g. 'Open', 'Resolved', 'Closed' */
  private @Nullable String status;

  /** The priority for task e.g. 'High', 'Medium', 'Low' */
  private @Nullable String priority;

  /** The member ID of the member the task should be assigned to. */
  private @Nullable PSMember assignedto;

  /** The task due date format is ISO-8601 e.g. 2010-10-25, 2010-10-25T12:26 (defaults to T18:00) */
  private @Nullable Date due;

  /** The context of this comment */
  private @Nullable Context context;

  /** The list of attachments for this comment */
  private @Nullable List<Attachment> attachments;

  /**
   * @return the id
   */
  @Override
  public @Nullable Long getId() {
    return this.id;
  }

  /**
   * @return the title
   */
  public @Nullable String getTitle() {
    return this.title;
  }

  /**
   * @return the content
   */
  public @Nullable String getContent() {
    return this.content;
  }

  /**
   * @return the mediatype
   */
  public String getMediaType() {
    return this.mediatype;
  }

  /**
   * @return the type
   */
  public @Nullable String getType() {
    return this.type;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @param content the content to set
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * @param mediatype the mediatype to set
   */
  public void setMediaType(String mediatype) {
    this.mediatype = mediatype;
  }

  /**
   * @param type the type to set
   */
  public void setType(@Nullable String type) {
    this.type = type;
  }

  // Attachments
  // ----------------------------------------------------------------------------------------------

  /**
   * @return the list of attachments
   */
  public boolean hasAttachments() {
    List<Attachment> a = this.attachments;
    return a != null && a.size() > 0;
  }

  /**
   * @return the list of attachments
   */
  public List<Attachment> getAttachments() {
    List<Attachment> a = this.attachments;
    if (a == null) {
      a = new ArrayList<>();
      this.attachments = a;
    }
    return a;
  }

  /**
   * Adds the specified document as an attachment.
   *
   * @param document The document to attach to the comment.
   */
  public void addAttachment(PSDocument document) {
    List<Attachment> a = this.attachments;
    if (a == null) {
      a = new ArrayList<>();
      this.attachments = a;
    }
    a.add(new Attachment(document));
  }

  /**
   * Adds the specified document as an attachment.
   *
   * @param document The document to attach to the comment.
   * @param fragment The fragment ID of where the comment is attached (<code>null</code> for default fragment)
   */
  public void addAttachment(PSDocument document, String fragment) {
    List<Attachment> a = this.attachments;
    if (a == null) {
      a = new ArrayList<>();
      this.attachments = a;
    }
    a.add(new Attachment(document, fragment));
  }

  /**
   * @param attachments the attachments to set
   */
  public void setAttachments(List<Attachment> attachments) {
    this.attachments = attachments;
  }

  // Labels
  // ----------------------------------------------------------------------------------------------

  /**
   * @return the labels
   */
  public boolean hasLabels() {
    return this.labels.size() > 0;
  }

  /**
   * @return the labels
   */
  public List<String> getLabels() {
    return this.labels;
  }

  /**
   * @return The labels as a comma-separated list.
   */
  public String getLabelsAsString() {
    StringBuilder s = new StringBuilder();
    for (String label : this.labels) {
      if (s.length() > 0) {
        s.append(',');
      }
      s.append(label);
    }
    return s.toString();
  }

  /**
   * @param labels the labels to set
   */
  public void setLabels(List<String> labels) {
    this.labels = Objects.requireNonNull(labels, "Labels must not be null, use empty list");
  }

  /**
   * @param labels The labels as a comma-separated list.
   */
  public void setLabels(String labels) {
    this.labels = new ArrayList<>();
    for (String label : labels.split(",")) {
      this.labels.add(label);
    }
  }

  // Properties
  // ----------------------------------------------------------------------------------------------

  /**
   * @return the labels
   */
  public boolean hasProperties() {
    Map<String, String> p = this.properties;
    return p != null && p.size() > 0;
  }

  /**
   * @return the properties
   */
  public Map<String, String> getProperties() {
    Map<String, String> p = this.properties;
    if (p == null) {
      p = new HashMap<>();
      this.properties = p;
    }
    return p;
  }

  /**
   * @return The labels as a comma-separated list.
   */
  public String getPropertiesAsString() {
    Map<String, String> props = this.properties;
    if (props == null) return "";
    StringBuilder s = new StringBuilder();
    for (Entry<String, String> p : props.entrySet()) {
      if (s.length() > 0) {
        s.append('|');
      }
      // TODO handle escaped pipes
      s.append(p.getKey()).append('=').append(p.getValue());
    }
    return s.toString();
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(String properties) {
    Map<String, String> p = new HashMap<>();
    if (properties != null) {
      for (String property: properties.split("(?<!\\|)\\|(?!\\|)")) {
        property = property.replaceAll("\\|\\|", "\\|");
        int eq = property.indexOf('=');
        //There must be a name and a value
        if (eq > 0) {
          String name = property.substring(0, eq);
          String value = property.substring(eq + 1);
          if ("label".equals(name) == false) {
            p.put(name, value);
          }
        }
        //Otherwise, just use the name without any value
        else if (eq == -1 && !property.isEmpty()) {
          if ("label".equals(property) == false) {
            p.put(property, "");
          }
        }
      }
    }
    this.properties = p;
  }

  // Author
  // ----------------------------------------------------------------------------------------------

  /**
   * @return the author
   */
  public @Nullable Author getAuthor() {
    return this.author;
  }

  /**
   * Sets the author directly.
   *
   * @param author the author to set
   */
  public void setAuthor(Author author) {
    this.author = author;
  }

  /**
   * Sets the author as a PageSeeder member.
   *
   * <p>Implementation note: This method creates a new <code>Author</code> instance.
   *
   * @param member the member to set as the author
   */
  public void setAuthor(PSMember member) {
    this.author = new Author(member);
  }

  /**
   * Sets the author as an external user.
   *
   * <p>Implementation note: This method creates a new <code>Author</code> instance.
   *
   * @param name  the name of the author (required)
   * @param email the email of the author
   */
  public void setAuthor(String name, @Nullable String email) {
    this.author = new Author(name, email);
  }

  // Context
  // ----------------------------------------------------------------------------------------------

  /**
   * @return the context
   */
  public @Nullable Context getContext() {
    return this.context;
  }

  /**
   * Set the context as a group.
   *
   * <p>Implementation note: This method creates a new context instance.
   *
   * @param group The group to use as context.
   */
  public void setContext(PSGroup group) {
    this.context = new Context(group);
  }

  /**
   * Set the context as a document.
   *
   * <p>Implementation note: This method creates a new context instance.
   *
   * @param document The document to use as context
   */
  public void setContext(PSDocument document) {
    this.context = new Context(document);
  }

  /**
   * Set the context as an external URI.
   *
   * <p>Implementation note: This method creates a new context instance.
   *
   * @param externaluri The external URI to use as context
   */
  public void setContext(PSExternalURI externaluri) {
    this.context = new Context(externaluri);
  }

  /**
   * Set the context as a document fragment.
   *
   * <p>Implementation note: This method creates a new context instance.
   *
   * @param document The document
   * @param fragment The document fragment to use as context
   */
  public void setContext(PSDocument document, @Nullable String fragment) {
    this.context = new Context(document, fragment);
  }

  /**
   * Set the context as an external URI fragment.
   *
   * <p>Implementation note: This method creates a new context instance.
   *
   * @param externaluri The external URI to use as context
   * @param fragment    The external URI fragment to use as context
   */
  public void setContext(PSExternalURI externaluri, @Nullable String fragment) {
    this.context = new Context(externaluri, fragment);
  }

  // Task attributes
  // ----------------------------------------------------------------------------------------------

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
  public @Nullable PSMember getAssignedTo() {
    return this.assignedto;
  }

  /**
   * @return the due
   */
  public @Nullable Date getDue() {
    return this.due;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(@Nullable String status) {
    this.status = status;
  }

  /**
   * @param priority the priority to set
   */
  public void setPriority(@Nullable String priority) {
    this.priority = priority;
  }

  /**
   * @param assignedto the assignedto to set
   */
  public void setAssignedto(@Nullable PSMember assignedto) {
    this.assignedto = assignedto;
  }

  /**
   * @param due the due to set
   */
  public void setDue(@Nullable Date due) {
    this.due = due;
  }

  @Override
  public @Nullable String getKey() {
    Long id = this.id;
    return id != null? id.toString() : null;
  }

  @Override
  public boolean isValid() {
    return checkValid() == EntityValidity.OK;
  }

  @Override
  public boolean isIdentifiable() {
    return this.id != null;
  }

  @Override
  public @Nullable String getIdentifier() {
    Long id = this.id;
    return id != null? id.toString() : null;
  }

  @Override
  public EntityValidity checkValid() {
    // TODO Constraints on comments title, content and author required
    return EntityValidity.OK;
  }

  @Override
  public String toString() {
    return "C("+getId()+":"+getTitle()+")";
  }

  // Inner classes
  // ----------------------------------------------------------------------------------------------

  /**
   * The author of a comment.
   */
  public static final class Author implements Serializable {

    /** As per recommendation */
    private static final long serialVersionUID = 1L;

    /**
     * The name of the author (required) yes string
     */
    private final @Nullable String _name;

    /**
     * When the author is a member
     */
    private final @Nullable PSMember _member;

    /**
     * The email of the author yes email
     */
    private final @Nullable String _email;

    /**
     * Set the author name and email for when the author is not a PageSeeder member.
     *
     * @param name  The name of the author (required)
     * @param email The email of the author
     */
    public Author(String name, @Nullable String email) {
      this._name = name;
      this._member = null;
      this._email = email;
    }

    /**
     * Set the author as a PageSeeder member.
     *
     * <p>The member must be identifiable and exist in PageSeeder.
     *
     * @param member the member.
     */
    public Author(PSMember member) {
      this._member = member;
      this._name = null;
      this._email = member.getEmail();
    }

    /**
     * @return the member or <code>null</code> if the author is not specified or an external user.
     */
    public @Nullable PSMember member() {
      return this._member;
    }

    /**
     * Returns the name for when the author is an external user.
     *
     * @return the email of the external user
     */
    public @Nullable String name() {
      return this._name;
    }

    /**
     * Returns the email for when the author is an external user.
     *
     * @return the email of the external user
     */
    public @Nullable String email() {
      return this._email;
    }

  }

  /**
   * An attachment to the comment.
   */
  public static final class Attachment implements Serializable {

    /** As per recommendation */
    private static final long serialVersionUID = 1L;

    /**
     * The URI of the attachment.
     */
    private final PSURI _uri;

    /**
     * The fragment the comment is attached to.
     */
    private final @Nullable String _fragment;

    /**
     * @param uri The URI to attach
     */
    public Attachment(PSURI uri) {
      this._uri = uri;
      this._fragment = null;
    }

    /**
     * @param uri      The URI to attach
     * @param fragment The fragment ID to attach it to.
     */
    public Attachment(PSURI uri, @Nullable String fragment) {
      this._uri = uri;
      this._fragment = fragment;
    }

    /**
     * @return the attached URI
     */
    public PSURI uri() {
      return this._uri;
    }

    /**
     * @return the fragment of the URI where the comment is attached
     */
    public @Nullable String fragment() {
      return this._fragment;
    }

  }

  /**
   * The context of the comment which may be either a group or a URI.
   */
  public static final class Context implements Serializable {

    /** As per recommendation */
    private static final long serialVersionUID = 1L;

    /** The group the comment is attached to. */
    private final @Nullable PSGroup _group;

    /** The URI the comment is attached to. */
    private final @Nullable PSURI _uri;

    /** The fragment (for a URI only) */
    private final @Nullable String _fragment;

    /**
     * Create a group context.
     *
     * @param group The group to use as the context.
     */
    public Context(PSGroup group) {
      this._group = group;
      this._uri = null;
      this._fragment = null;
    }

    /**
     * Create a URI context.
     *
     * @param uri The uri to use as the context.
     */
    public Context(PSURI uri) {
      this._group = null;
      this._uri = uri;
      this._fragment = null;
    }

    /**
     * Create a URI context at a particular fragment.
     *
     * @param uri      The uri to use as the context.
     * @param fragment The fragment of the URI to use as context
     */
    public Context(PSURI uri, @Nullable String fragment) {
      this._group = null;
      this._uri = uri;
      this._fragment = fragment;
    }

    /**
     * @return the group
     */
    public @Nullable PSGroup group() {
      return this._group;
    }

    /**
     * @return the _uri
     */
    public @Nullable PSURI uri() {
      return this._uri;
    }

    /**
     * @return the fragment
     */
    public @Nullable String fragment() {
      return this._fragment;
    }
  }

}
