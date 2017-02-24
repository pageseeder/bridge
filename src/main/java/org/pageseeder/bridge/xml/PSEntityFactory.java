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

import java.text.ParseException;
import java.util.Date;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.control.CommentManager;
import org.pageseeder.bridge.control.DocumentManager;
import org.pageseeder.bridge.control.ExternalURIManager;
import org.pageseeder.bridge.control.GroupManager;
import org.pageseeder.bridge.control.MemberManager;
import org.pageseeder.bridge.control.MembershipManager;
import org.pageseeder.bridge.control.XRefManager;
import org.pageseeder.bridge.model.PSComment;
import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSExternalURI;
import org.pageseeder.bridge.model.PSFolder;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSGroupFolder;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMemberStatus;
import org.pageseeder.bridge.model.PSMembership;
import org.pageseeder.bridge.model.PSNotification;
import org.pageseeder.bridge.model.PSProject;
import org.pageseeder.bridge.model.PSRole;
import org.pageseeder.bridge.model.PSURI;
import org.pageseeder.bridge.model.PSXRef;
import org.pageseeder.bridge.util.ISO8601;
import org.xml.sax.Attributes;

/**
 * A utility class used to generate objects from the XML returned by services.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.3.10
 */
public final class PSEntityFactory {

  /** Utility */
  private PSEntityFactory() {}

  /**
   * Generate a <code>PSMembership</code> from a <code>{@code <membership>}</code> element.
   *
   * <p>Will use the following attributes:
   * <ul>
   *   <li><b>id</b> - the membership unique database ID
   *   <li><b>email-listed</b> - whether the email is listed
   *   <li><b>notification</b> - notification settings
   *   <li><b>flags</b>
   *   <li><b>status</b>
   * </ul>
   *
   * @param atts       the attributes of the "membership" element.
   * @param membership an existing membership instance to reuse.
   *
   * @return The membership instance.
   */
  public static PSMembership toMembership(Attributes atts, @Nullable PSMembership membership) {
    // The ID is not always specified (e.g. subgroups)
    Long id = getOptionalLong(atts, "id");
    String listed = getOptionalString(atts, "email-listed");
    PSNotification notification = PSHandlers.notification(atts.getValue("notification"));
    PSRole role = PSHandlers.role(atts.getValue("role"));
    Date created = PSHandlers.datetime(atts.getValue("created")); // since 5.7
    PSMembership m = tryMembershipCache(membership, id);
    m.setId(id);
    m.setListed("true".equals(listed));
    m.setNotification(notification);
    m.setRole(role);
    m.setCreated(created);
    return m;
  }

  /**
   * Generate a <code>PSMember</code> from a <code>{@code <member>}</code> element.
   *
   * <p>Will use the following attributes:
   * <ul>
   *   <li><b>id</b> - the member unique database ID
   *   <li><b>username</b> - username (primary key)
   *   <li><b>firstname</b> - first name of user
   *   <li><b>surname</b> - last name of user
   *   <li><b>email</b> - email address if available only
   *   <li><b>status</b> - status of user
   * </ul>
   *
   * @param atts   the attributes of the "member" element.
   * @param member an existing member instance to reuse.
   *
   * @return The member instance.
   */
  public static PSMember toMember(Attributes atts, @Nullable PSMember member) {
    Long id = getId(atts);
    String username = getString(atts, "username");
    String firstname = getString(atts, "firstname");
    String surname = getString(atts, "surname");
    String email = getOptionalString(atts, "email");
    String status = getOptionalString(atts, "status");
    PSMember m = tryMemberCache(member, id);
    m.setId(id);
    m.setUsername(username);
    m.setFirstname(firstname);
    m.setSurname(surname);
    if (email != null) {
      m.setEmail(email);
    }
    if (status != null) {
      m.setStatus(PSMemberStatus.fromAttribute(status));
    }
    return m;
  }

  /**
   * Generate a <code>PSGroup</code> from a <code>{@code <group>}</code> element.
   *
   * <p>Will use the following attributes:
   * <ul>
   *   <li><b>id</b> - the group unique database ID
   *   <li><b>name</b> - name of the group (primary key)
   *   <li><b>description</b> - description of the group
   *   <li><b>owner</b> - owner of the group
   *   <li><b>defaultrole</b> - default role of members (if available)
   *   <li><b>defaultnotify</b> - default notification of members (if available)
   * </ul>
   *
   * @param atts  the attributes of the "member" element.
   * @param group an existing group instance to reuse.
   *
   * @return The group instance.
   */
  public static PSGroup toGroup(Attributes atts, @Nullable PSGroup group) {
    Long id = getId(atts);
    String name = getString(atts, "name");
    // Core attributes
    String description = getOptionalString(atts, "description");
    String owner = getOptionalString(atts, "owner");
    String title = getOptionalString(atts, "title");
    // Extended attributes
    String detailstype = getOptionalString(atts, "detailstype");
    String template = getOptionalString(atts, "template");
    PSRole defaultRole = PSHandlers.role(atts.getValue("defaultrole"));
    PSNotification defaultNotification = PSHandlers.notification(atts.getValue("defaultnotify"));

    PSGroup g = tryGroupCache(group, id);
    g.setId(id);
    g.setName(name);
    if (description != null) {
      g.setDescription(description);
    }
    if (title != null) {
      g.setTitle(title);
    }
    if (owner != null) {
      g.setOwner(owner);
    }
    if (detailstype != null) {
      g.setDetailsType(detailstype);
    }
    if (template != null) {
      g.setTemplate(template);
    }
    if (defaultRole != null) {
      g.setDefaultRole(defaultRole);
    }
    if (defaultNotification != null) {
      g.setDefaultNotification(defaultNotification);
    }
    return g;
  }

  /**
   * Generate a <code>PSProject</code> from a <code>{@code <project>}</code> element.
   *
   * <p>Will use the following attributes:
   * <ul>
   *   <li><b>id</b> - the project unique database ID
   *   <li><b>name</b> - name of the project (primary key)
   *   <li><b>description</b> - description of the project
   *   <li><b>defaultrole</b> - default role of members (if available)
   *   <li><b>defaultnotify</b> - default notification of members (if available)
   * </ul>
   *
   * @param atts  the attributes of the "group" or "project" element.
   * @param group an existing group instance to reuse (should be and instance of <code>PSProject</code>)
   *
   * @return The project instance.
   */
  public static PSProject toProject(Attributes atts, @Nullable PSGroup group) {
    Long id = getId(atts);
    String name = getString(atts, "name");
    // Core attributes
    String description = getOptionalString(atts, "description");
    String owner = getOptionalString(atts, "owner");
    String title = getOptionalString(atts, "title");
    // Extended attributes
    String detailstype = getOptionalString(atts, "detailstype");
    String template = getOptionalString(atts, "template");
    PSRole defaultRole = PSHandlers.role(atts.getValue("defaultrole"));
    PSNotification defaultNotification = PSHandlers.notification(atts.getValue("defaultnotify"));
    PSProject p = tryProjectCache(group instanceof PSProject ? (PSProject) group : null, id);
    p.setId(id);
    p.setName(name);
    if (description != null) {
      p.setDescription(description);
    }
    if (title != null) {
      p.setTitle(title);
    }
    if (defaultRole != null) {
      p.setDefaultRole(defaultRole);
    }
    if (owner != null) {
      p.setOwner(owner);
    }
    if (detailstype != null) {
      p.setDetailsType(detailstype);
    }
    if (template != null) {
      p.setTemplate(template);
    }
    if (defaultNotification != null) {
      p.setDefaultNotification(defaultNotification);
    }
    return p;
  }

  /**
   * Generate a <code>PSDocument</code> from a <code>{@code <uri>}</code> element.
   *
   * <pre>{@code
   * <uri id="2439"
   *  scheme="http"
   *    host="localhost"
   *    port="8080"
   *    path="/ps/acme/data/documents"
   * decodedpath="/ps/acme/data/documents"
   * external="false"
   *  folder="true"
   * mediatype="folder"
   * created="2014-01-31T16:19:12+11:00"
   * }</pre>
   *
   * @param atts     The attributes the "uri" element
   * @param document The PSDocument instance (may be <code>null</code>).
   *
   * @return The corresponding PSDocument.
   */
  public static PSDocument toDocument(Attributes atts, @Nullable PSDocument document) {
    Long id = getId(atts);
    String scheme = getString(atts, "scheme");
    String host = getString(atts, "host");
    int port = getInt(atts, "port", 80); // XXX Is this the correct default port to use???
    String path = getString(atts, "path");
    String description = getOptionalString(atts, "description");
    String docid = getOptionalString(atts, "docid");
    String filename = getOptionalString(atts, "filename");
    String labels = getString(atts, "labels", "");
    String title = getOptionalString(atts, "title");
    String type = getOptionalString(atts, "type");
    if (type == null) {
      type = getString(atts, "documenttype", "default");
    }
    String mediatype = getOptionalString(atts, "mediatype");
    String created = getOptionalString(atts, "created");
    String modified = getOptionalString(atts, "modified");

    PSDocument d = document;
    if (d == null) {
      PSEntityCache<PSDocument> cache = DocumentManager.getCache();
      d = cache.get(id);
      if (d == null) {
        d = new PSDocument(scheme, host, port, path);
      }
    }
    d.setId(id);
    d.setPath(path);
    d.setDescription(description);
    d.setDocid(docid);
    if (filename != null) {
      d.setFilename(filename);
    }
    d.setLabels(labels);
    d.setTitle(title);
    d.setType(type);
    d.setMediaType(mediatype);
    d.setCreatedDate(created);
    // set the last modified date to created date if not found.
    if (modified != null) {
      d.setModifiedDate(modified);
    } else {
      d.setModifiedDate(created);
    }
    return d;
  }

  /**
   * Generate a <code>PSExternalURI</code> from a <code>{@code <uri>}</code> element.
   *
   *<pre>{@code
   * <uri id="2439"
   *  scheme="http"
   *    host="localhost"
   *    port="8080"
   *    path="/ps/acme/data/documents"
   * decodedpath="/ps/acme/data/documents"
   * external="true"
   *  folder="true"
   * mediatype="folder"
   * created="2014-01-31T16:19:12+11:00"
   * }</pre>
   *
   * @param atts         The attributes the "uri" element
   * @param externaluri  The PSExternalURI instance (may be <code>null</code>).
   *
   * @return The corresponding PSDocument.
   */
  public static PSExternalURI toExternalURI(Attributes atts, @Nullable PSExternalURI externaluri) {
    Long id = getId(atts);
    String scheme = getString(atts, "scheme");
    String host = getString(atts, "host");
    int port = getInt(atts, "port", 80); // XXX Is this the correct default port to use???
    String path = getString(atts, "path");
    String description = getOptionalString(atts, "description");
    String docid = getOptionalString(atts, "docid");
    String labels = getString(atts, "labels", "");
    String title = getOptionalString(atts, "title");
    String mediatype = getOptionalString(atts, "mediatype");
    boolean folder = "true".equals(atts.getValue("folder"));

    PSExternalURI u = externaluri;
    if (u == null) {
      PSEntityCache<PSExternalURI> cache = ExternalURIManager.getCache();
      u = cache.get(id);
      if (u == null) {
        u = new PSExternalURI(scheme, host, port, path);
      }
    }
    u.setId(id);
    u.setDescription(description);
    u.setDocid(docid);
    u.setLabels(labels);
    u.setTitle(title);
    u.setMediaType(mediatype);
    u.setFolder(folder);
    return u;
  }

  /**
   * Generate a <code>PSComment</code> from a <code>{@code <comment>}</code> element.
   *
   * @param atts    The attributes the "comment" element
   * @param comment The PSComment instance (may be <code>null</code>).
   *
   * @return The corresponding PSComment
   */
  public static PSComment toComment(Attributes atts, @Nullable PSComment comment) {
    Long id = getId(atts);
    String status = getOptionalString(atts, "status");
    String priority = getOptionalString(atts, "priority");
    Date due = getOptionalDate(atts, "due");
    String type = getOptionalString(atts, "type");
    String properties = getString(atts, "properties", "");

    PSComment c = tryCommentCache(comment, id);
    c.setId(id);
    c.setStatus(status);
    c.setPriority(priority);
    if (due != null) {
      c.setDue(due);
    }
    c.setType(type);
    c.setProperties(properties);
    return c;
  }

  /**
   * Generate a <code>PSFolder</code> from a <code>{@code <uri>}</code> element.
   *
   * <pre>{@code
   * <uri id="2439"
   *  scheme="http"
   *    host="localhost"
   *    port="8080"
   *    path="/ps/pharmaclaim/forms/registrations/documents"
   * decodedpath="/ps/pharmaclaim/forms/registrations/documents"
   * external="false"
   *  folder="true"
   * mediatype="folder"
   * created="2014-01-31T16:19:12+11:00"
   * }</pre>
   *
   * @param atts   The attributes of the "uri" element
   * @param folder The folder instance (may be <code>null</code>).
   *
   * @return the corresponding folder instance.
   */
  public static PSFolder toFolder(Attributes atts, @Nullable PSFolder folder) {
    Long id = getId(atts);
    String path = getString(atts, "path");
    String description = getOptionalString(atts, "description");
    String docid = getOptionalString(atts, "docid");
    String labels = getString(atts, "labels", "");
    String title = getOptionalString(atts, "title");
    String mediatype = getOptionalString(atts, "mediatype");

    PSFolder f = tryFolderCache(folder, id, path);
    f.setId(id);
    f.setDescription(description);
    f.setDocid(docid);
    f.setLabels(labels);
    f.setTitle(title);
    f.setMediaType(mediatype);
    return f;
  }

  /**
   * Generate a <code>PSGroupFolder</code> from a <code>{@code <groupfolder>}</code> element.
   *
   * @param atts   the attributes of the "groupfolder" element.
   * @param folder an existing group folder instance to reuse.
   *
   * @return The group folder instance.
   */
  public static PSGroupFolder toGroupFolder(Attributes atts, @Nullable PSGroupFolder folder) {
    Long id = getId(atts);
    String scheme = getString(atts, "scheme");
    String host = getString(atts, "host");
    int port = getInt(atts, "port", 80);
    String path = getString(atts, "path");
    boolean isExternal = "true".equals(atts.getValue("external"));

    PSGroupFolder f = tryGroupFolderCache(folder, id, path);
    f.setId(id);
    f.setScheme(scheme);
    f.setHost(host);
    f.setPort(port);
    f.setPath(path);
    f.setExternal(isExternal);
    return f;
  }

  /**
   * Generate a <code>PSXRef</code> from a <code>{@code <xref>}</code> or
   * a <code>{@code <blockxref>}</code> element.
   *
   * @param atts    the attributes of the element.
   * @param source  the source URI.
   * @param xref    an existing xref instance to reuse.
   *
   * @return The group folder instance.
   */
  public static PSXRef toXRef(Attributes atts, PSURI source, @Nullable PSXRef xref) {
    Long id = getId(atts);
    Long targetURIId = getLong(atts, "uriid");
    String targetHref = getString(atts, "href");
    String targetDocid = getOptionalString(atts, "docid");
    String targetFragment = getString(atts, "frag", "default");
    String targetURITitle = getOptionalString(atts, "urititle");
    String targetMediaType = getOptionalString(atts, "mediatype");
    PSXRef.Type type = PSXRef.Type.fromString(getOptionalString(atts, "type"));
    boolean reverseLink = !"false".equals(getOptionalString(atts, "reverselink"));
    String reverseTitle = getOptionalString(atts, "reversetitle");
    String sourceFragment = getString(atts, "reversefrag", "default");
    PSXRef.Type reverseType = PSXRef.Type.fromString(getOptionalString(atts, "reversetype"));
    String title = getOptionalString(atts, "title");
    PSXRef.Display display = PSXRef.Display.fromString(getOptionalString(atts, "display"));
    String labels = getString(atts, "labels", "");
    int level = getInt(atts, "level", 0);
    boolean external = "true".equals(getOptionalString(atts, "external"));

    PSXRef x = tryXRefCache(xref, id);
    x.setId(id);
    x.setSourceURI(source);
    x.setSourceFragment(sourceFragment);
    PSURI target = external ? new PSExternalURI(targetHref) : new PSDocument(targetHref);
    target.setDocid(targetDocid);
    target.setId(targetURIId);
    target.setTitle(targetURITitle);
    target.setMediaType(targetMediaType);
    x.setTargetURI(target);
    x.setTargetFragment(targetFragment);
    x.setType(type);
    x.setReverseLink(reverseLink);
    x.setReverseTitle(reverseTitle);
    x.setReverseType(reverseType);
    x.setTitle(title);
    x.setDisplay(display);
    x.setLabels(labels);
    if (level > 0) {
      x.setLevel(level);
    }
    return x;
  }

  /**
   * Generate a <code>PSXRef</code> from a <code>{@code <reversexref>}</code> element.
   *
   * @param atts    the attributes of the element.
   * @param target  the source URI.
   * @param xref    an existing xref instance to reuse.
   *
   * @return The group folder instance.
   */
  public static PSXRef toReverseXRef(Attributes atts, PSURI target, @Nullable PSXRef xref) {
    Long id = getId(atts);
    Long sourceURIId = getLong(atts, "uriid");
    String sourceHref = getString(atts, "href");
    String sourceDocid = getOptionalString(atts, "docid");
    String sourceFragment = getString(atts, "frag", "default");
    String sourceURITitle = getOptionalString(atts, "urititle");
    String sourceMediaType = getOptionalString(atts, "mediatype");
    boolean reverseLink = true;
    PSXRef.Type reverseType = PSXRef.Type.fromString(getOptionalString(atts, "type"));
    String reverseTitle = getOptionalString(atts, "title");
    PSXRef.Type type = PSXRef.Type.fromString(getOptionalString(atts, "forwardtype"));
    String title = getOptionalString(atts, "forwardtitle");
    String targetFragment = getString(atts, "forwardfrag", "default");
    PSXRef.Display display = PSXRef.Display.fromString(getOptionalString(atts, "forwarddisplay"));
    String labels = getString(atts, "labels", "");
    int level = getInt(atts, "level", 0);
    boolean external = "true".equals(getOptionalString(atts, "external"));

    PSXRef x = tryXRefCache(xref, id);
    x.setId(id);
    x.setTargetURI(target);
    x.setTargetFragment(targetFragment);
    PSURI source = external ? new PSExternalURI(sourceHref) : new PSDocument(sourceHref);
    source.setDocid(sourceDocid);
    source.setId(sourceURIId);
    source.setTitle(sourceURITitle);
    source.setMediaType(sourceMediaType);
    x.setSourceURI(source);
    x.setSourceFragment(sourceFragment);
    x.setType(type);
    x.setReverseLink(reverseLink);
    x.setReverseTitle(reverseTitle);
    x.setReverseType(reverseType);
    x.setTitle(title);
    x.setDisplay(display);
    x.setLabels(labels);
    if (level > 0) {
      x.setLevel(level);
    }
    return x;
  }

  // Attribute retrieval
  // --------------------------------------------------------------------------

  private static Long getId(Attributes atts) {
    return BasicHandler.getLong(atts, "id");
  }

  private static Long getLong(Attributes atts, String name) {
    return BasicHandler.getLong(atts, name);
  }

  private static String getString(Attributes atts, String name) {
    return BasicHandler.getString(atts, name);
  }

  private static String getString(Attributes atts, String name, String fallback) {
    return BasicHandler.getString(atts, name, fallback);
  }

  private static int getInt(Attributes atts, String name, int fallback) {
    return BasicHandler.getInt(atts, name, fallback);
  }

  private static @Nullable Long getOptionalLong(Attributes atts, String name) {
    return BasicHandler.getOptionalLong(atts, name);
  }

  private static @Nullable String getOptionalString(Attributes atts, String name) {
    return atts.getValue(name);
  }

  private static @Nullable Date getOptionalDate(Attributes atts, String name) {
    String value = atts.getValue(name);
    if (value == null) return null;
    try {
      return ISO8601.parseAuto(value);
    } catch (ParseException ex) {
      throw new InvalidAttributeException(name, ex);
    }
  }




  // Cache utility methods
  // --------------------------------------------------------------------------

  /**
   * Return the membership from the cache if found.
   * 
   * @param membership A membership matching the ID
   * @param id         The ID of the membership
   * 
   * @return the existing membership or a new one if the ID is <code>null</code>
   *         or the membership wasn't in the cache.
   */
  private static PSMembership tryMembershipCache(@Nullable PSMembership membership, @Nullable Long id) {
    PSMembership m = membership;
    if (m == null) {
      PSEntityCache<PSMembership> cache = MembershipManager.getCache();
      if (id != null) {
        m = cache.get(id);
      }
      if (m == null) {
        m = new PSMembership();
      }
    }
    return m;
  }

  /**
   * Return the member from the cache if found.
   * 
   * @param member A member matching the ID
   * @param id     The ID of the member
   * 
   * @return the existing member if <code>null</code> or not in cache.
   */
  private static PSMember tryMemberCache(@Nullable PSMember member, Long id) {
    PSMember m = member;
    if (m == null) {
      PSEntityCache<PSMember> cache = MemberManager.getCache();
      m = cache.get(id);
      if (m == null) {
        m = new PSMember(id);
      }
    }
    return m;
  }

  /**
   * Return the group from the cache if found.
   * 
   * @param group A group matching the ID
   * @param id    The ID of the group
   * 
   * @return the existing member if <code>null</code> or not in cache.
   */
  private static PSGroup tryGroupCache(@Nullable PSGroup group, Long id) {
    PSGroup g = group;
    if (g == null) {
      PSEntityCache<PSGroup> cache = GroupManager.getCache();
      g = cache.get(id);
      if (g == null) {
        g = new PSGroup(id);
      }
    }
    return g;
  }

  /**
   * Return the project from the cache if found.
   * 
   * @param project A project matching the ID
   * @param id      The ID of the project
   * 
   * @return the existing project if <code>null</code> or not in cache.
   */
  private static PSProject tryProjectCache(@Nullable PSProject project, Long id) {
    PSProject p = project;
    if (p == null) {
      PSEntityCache<PSGroup> cache = GroupManager.getCache();
      try {
        p = (PSProject) cache.get(id);
      } catch (ClassCastException ex) {
        // This should never occur: it is when a group has become a project
        // TODO Log??
      }
      if (p == null) {
        p = new PSProject(id);
      }
    }
    return p;
  }

  /**
   * Return the comment from the cache if found.
   * 
   * @param comment A comment matching the ID
   * @param id      The ID of the comment
   * 
   * @return the existing comment if <code>null</code> or not in cache.
   */
  private static PSComment tryCommentCache(@Nullable PSComment comment, Long id) {
    PSComment g = comment;
    if (g == null) {
      PSEntityCache<PSComment> cache = CommentManager.getCache();
      g = cache.get(id);
      if (g == null) {
        g = new PSComment(); // Add new constructor
      }
    }
    return g;
  }

  /**
   * Return the folder from the cache if found.
   * 
   * @param folder A folder matching the ID
   * @param id     The ID of the folder
   * 
   * @return the existing folder if <code>null</code> or not in cache.
   */
  private static PSFolder tryFolderCache(@Nullable PSFolder folder, Long id, String path) {
    PSFolder f = folder;
    if (f == null) {
      PSEntityCache<PSFolder> cache = DocumentManager.getFoldersCache();
      f = cache.get(id);
      if (f == null) {
        f = new PSFolder(path);
      }
    }
    return f;
  }

  /**
   * Return the group folder from the cache if found.
   * 
   * @param group A group folder matching the ID
   * @param id    The ID of the group folder
   * 
   * @return the existing group folder if <code>null</code> or not in cache.
   */
  private static PSGroupFolder tryGroupFolderCache(@Nullable PSGroupFolder folder, Long id, String path) {
    PSGroupFolder f = folder;
    if (f == null) {
      PSEntityCache<PSGroupFolder> cache = GroupManager.getFoldersCache();
      f = cache.get(id);
      if (f == null) {
        f = new PSGroupFolder(path); // TODO This does not seem right
      }
    }
    return f;
  }

  /**
   * Return the cross-reference from the cache if found.
   * 
   * @param xref A existing cross-reference matching the ID
   * @param id   The ID of the cross-reference
   * 
   * @return the existing cross-reference if <code>null</code> or not in cache.
   */
  private static PSXRef tryXRefCache(@Nullable PSXRef xref, Long id) {
    PSXRef x = xref;
    if (x == null) {
      PSEntityCache<PSXRef> cache = XRefManager.getCache();
      x = cache.get(id);
      if (x == null) {
        x = new PSXRef();
      }
    }
    return x;
  }


}
