/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pageseeder.bridge.EntityValidity;
import org.pageseeder.bridge.PSEntity;

/**
 * Represents a PageSeeder comment.
 *
 * @author Christophe Lauret
 * @version 28 April 2014
 */
public final class PSComment implements PSEntity {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /** The XLink ID of the comment. */
  private Long id;

  /** The title of the comment (required) */
  private String title;

  /** The content of the comment (required) */
  private String content;

  /** The content type of the comment, defaults to 'text/plain' */
  private String mediatype = "text/plain";

  /** The type of the comment to further qualify the comment. */
  private String type = null;

  /** The list of labels on the comment. */
  private List<String> labels = new ArrayList<String>();

  /** A pipe-separated list of properties as value pairs (e.g. x=1|y=2|) */
  private Map<String, String> properties = new HashMap<String, String>();

  /** The author of the comment. */
  private Author author;

  /** The status for task e.g. 'Open', 'Resolved', 'Closed' */
  private String status;

  /** The priority for task e.g. 'High', 'Medium', 'Low' */
  private String priority;

  /** The member ID of the member the task should be assigned to. */
  private PSMember assignedto;

  /** The task due date format is ISO-8601 e.g. 2010-10-25, 2010-10-25T12:26 (defaults to T18:00) */
  private Date due;

  /** The context of this comment */
  private Context context;

  /** The list of attachments for this comment */
  private List<Attachment> attachments;

  /**
   * @return the id
   */
  @Override
  public Long getId() {
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
  public String getContent() {
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
  public String getType() {
    return this.type;
  }

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
      if (s.length() > 0) s.append(',');
      s.append(label);
    }
    return s.toString();
  }

  /**
   * @return the labels
   */
  public boolean hasProperties() {
    return this.properties.size() > 0;
  }

  /**
   * @return the properties
   */
  public Map<String, String> getProperties() {
    return this.properties;
  }

  /**
   * @return The labels as a comma-separated list.
   */
  public String getPropertiesAsString() {
    StringBuilder s = new StringBuilder();
    for (Entry<String, String> p : this.properties.entrySet()) {
      if (s.length() > 0) s.append('|');
      s.append(p.getKey()).append('=').append(p.getValue());
    }
    return s.toString();
  }

  /**
   * @return the author
   */
  public Author getAuthor() {
    return this.author;
  }

  /**
   * @return the status
   */
  public String getStatus() {
    return this.status;
  }

  /**
   * @return the priority
   */
  public String getPriority() {
    return this.priority;
  }

  /**
   * @return the assignedto
   */
  public PSMember getAssignedTo() {
    return this.assignedto;
  }

  /**
   * @return the due
   */
  public Date getDue() {
    return this.due;
  }

  /**
   * @return the list of attachments
   */
  public boolean hasAttachments() {
    return this.attachments != null && this.attachments.size() > 0;
  }

  /**
   * @return the list of attachments
   */
  public List<Attachment> getAttachments() {
    if (this.attachments == null) this.attachments = new ArrayList<Attachment>();
    return this.attachments;
  }

  /**
   * Adds the specified document as an attachment.
   *
   * @param document The document to attach to the comment.
   */
  public void addAttachment(PSDocument document) {
    if (this.attachments == null) this.attachments = new ArrayList<Attachment>();
    this.attachments.add(new Attachment(document));
  }

  /**
   * Adds the specified document as an attachment.
   *
   * @param document The document to attach to the comment.
   * @param fragment The fragment ID of where the comment is attached (<code>null</code> for default fragment)
   */
  public void addAttachment(PSDocument document, String fragment) {
    if (this.attachments == null) this.attachments = new ArrayList<Attachment>();
    this.attachments.add(new Attachment(document, fragment));
  }

  /**
   * @param attachments the attachments to set
   */
  public void setAttachments(List<Attachment> attachments) {
    this.attachments = attachments;
  }

  /**
   * @return the context
   */
  public Context getContext() {
    return this.context;
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
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @param labels the labels to set
   */
  public void setLabels(List<String> labels) {
    this.labels = labels;
  }

  /**
   * @param labels The labels as a comma-separated list.
   */
  public final void setLabels(String labels) {
    if (labels == null) return;
    this.labels.clear();
    for (String label : labels.split(",")) {
      this.labels.add(label);
    }
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  /**
   * @param author the author to set
   */
  public void setAuthor(Author author) {
    this.author = author;
  }

  /**
   * @param member the member to set as the author
   */
  public void setAuthor(PSMember member) {
    this.author = new Author(member);
  }

  /**
   * @param name  the name of the author
   * @param email the email of the author
   */
  public void setAuthor(String name, String email) {
    this.author = new Author(name, email);
  }

  /**
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * @param priority the priority to set
   */
  public void setPriority(String priority) {
    this.priority = priority;
  }

  /**
   * @param assignedto the assignedto to set
   */
  public void setAssignedto(PSMember assignedto) {
    this.assignedto = assignedto;
  }

  /**
   * @param due the due to set
   */
  public void setDue(Date due) {
    this.due = due;
  }


  public void setContext(PSGroup group) {
    this.context = new Context(group);
  }

  public void setContext(PSDocument document) {
    this.context = new Context(document);
  }

  public void setContext(PSDocument document, String fragment) {
    this.context = new Context(document, fragment);
  }


  @Override
  public String getKey() {
    return this.id != null? this.id.toString() : null;
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
  public String getIdentifier() {
    return this.id != null? this.id.toString() : null;
  }

  @Override
  public EntityValidity checkValid() {
    // TODO Constraints on comments title, content and author required
    return EntityValidity.OK;
  }

  @Override
  public String toString() {
    return "C("+this.getId()+":"+this.getTitle()+")";
  }

  // Inner classes
  // ----------------------------------------------------------------------------------------------

  /**
   * The author of a comment
   */
  public static class Author {

    /**
     * The name of the author (required) yes string
     */
    private final String _name;

    /**
     * When the author is a member
     */
    private final PSMember _member;

    /**
     * The email of the author yes email
     */
    private final String _email;

    /**
     *
     */
    public Author(String name, String email) {
      this._name = name;
      this._member = null;
      this._email = email;
    }

    /**
     *
     */
    public Author(PSMember member) {
      this._member = member;
      this._name = null;
      this._email = member.getEmail();
    }

    /**
     * @return the member
     */
    public PSMember member() {
      return this._member;
    }

    /**
     * @return the name
     */
    public String name() {
      return this._name;
    }

    /**
     * @return the email
     */
    public String email() {
      return this._email;
    }

  }

  /**
   * An attachment to the comment.
   */
  public static class Attachment {

    /**
     * The URI of the attachment.
     */
    private PSURI _uri;

    /**
     * The fragment the comment is attached to.
     */
    private String _fragment;

    /**
     *
     */
    public Attachment(PSURI uri) {
      this._uri = uri;
    }

    /**
     *
     */
    public Attachment(PSURI uri, String fragment) {
      this._uri = uri;
      this._fragment = fragment;
    }

    /**
     * @return the _uri
     */
    public PSURI uri() {
      return this._uri;
    }

    /**
     * @return the fragment
     */
    public String fragment() {
      return this._fragment;
    }

  }

  /**
   * The context of the comment.
   */
  public static class Context {

    /** The group the comment is attached to. */
    private final PSGroup _group;

    /** The URI the comment is attached to. */
    private final PSURI _uri;

    /** The fragment (for a URI only) */
    private final String _fragment;

    /**
     *
     */
    public Context(PSGroup group) {
      this._group = group;
      this._uri = null;
      this._fragment = null;
    }

    /**
     *
     */
    public Context(PSURI uri) {
      this._group = null;
      this._uri = uri;
      this._fragment = null;
    }

    /**
     *
     */
    public Context(PSURI uri, String fragment) {
      this._group = null;
      this._uri = uri;
      this._fragment = fragment;
    }

    /**
     * @return the group
     */
    public PSGroup group() {
      return this._group;
    }

    /**
     * @return the _uri
     */
    public PSURI uri() {
      return this._uri;
    }

    /**
     * @return the fragment
     */
    public String fragment() {
      return this._fragment;
    }
  }

}
