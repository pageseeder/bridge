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

import java.util.Date;

import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.control.CommentManager;
import org.pageseeder.bridge.control.DocumentManager;
import org.pageseeder.bridge.control.ExternalURIManager;
import org.pageseeder.bridge.control.GroupManager;
import org.pageseeder.bridge.control.MemberManager;
import org.pageseeder.bridge.control.MembershipManager;
import org.pageseeder.bridge.model.PSComment;
import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSExternalURI;
import org.pageseeder.bridge.model.PSFolder;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSGroupFolder;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMembership;
import org.pageseeder.bridge.model.PSNotification;
import org.pageseeder.bridge.model.PSProject;
import org.pageseeder.bridge.model.PSRole;
import org.xml.sax.Attributes;

/**
 * A utility class used to generate objects from the XML returned by services.
 *
 * @author Christophe Lauret
 * @version 0.3.10
 */
public final class PSEntityFactory {

  /** Utility */
  private PSEntityFactory() {}

  /**
   * Generates the membership object from the attributes of a "membership" element.
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
  public static PSMembership toMembership(Attributes atts, PSMembership membership) {
    // Grab attributes values
    Long id = PSHandlers.id(atts.getValue("id"));
    String listed = atts.getValue("email-listed");
    PSNotification notification = PSHandlers.notification(atts.getValue("notification"));
    PSRole role = PSHandlers.role(atts.getValue("role"));
    Date created = PSHandlers.datetime(atts.getValue("created")); // since 5.7

    // XXX: Unused attributes
    // String flags = atts.getValue("flags");
    // String status = atts.getValue("status");

    PSMembership m = membership;
    if (m == null) {
      PSEntityCache<PSMembership> cache = MembershipManager.getCache();
      m = cache.get(id);
      if (m == null) {
        m = new PSMembership();
      }
    }
    m.setId(id);
    m.setListed("true".equals(listed));
    m.setNotification(notification);
    m.setRole(role);
    m.setCreated(created);

    return m;
  }

  /**
   * Generates the member object from the attributes of a "member" element.
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
  public static PSMember toMember(Attributes atts, PSMember member) {
    String id = atts.getValue("id");
    String firstname = atts.getValue("firstname");
    String surname = atts.getValue("surname");
    String username = atts.getValue("username");
    String email = atts.getValue("email");
    String status = atts.getValue("status");

    PSMember m = member;
    if (m == null) {
      PSEntityCache<PSMember> cache = MemberManager.getCache();
      m = cache.get(id);
      if (m == null) {
        m = new PSMember();
      }
    }
    m.setId(PSHandlers.id(id));
    m.setFirstname(firstname);
    m.setSurname(surname);
    m.setUsername(username);
    if (email != null) {
      m.setEmail(email);
    }
    if (status != null) {
      m.setActivated("activated".equals(status));
    }

    return m;
  }

  /**
   * Generates the group object from the attributes of a "group" element.
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
  public static PSGroup toGroup(Attributes atts, PSGroup group) {

    String id = atts.getValue("id");
    String name = atts.getValue("name");
    String description = atts.getValue("description");
    String owner = atts.getValue("owner");
    String detailstype = atts.getValue("detailstype");
    String template = atts.getValue("template");
    PSRole defaultRole = PSHandlers.role(atts.getValue("defaultrole"));
    PSNotification defaultNotification = PSHandlers.notification(atts.getValue("defaultnotify"));

    PSGroup g = group;
    if (g == null) {
      PSEntityCache<PSGroup> cache = GroupManager.getCache();
      g = cache.get(id);
      if (g == null) {
        g = new PSGroup(name);
      }
    }

    g.setId(PSHandlers.id(id));
    g.setName(name);
    g.setDescription(description);
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
   * Generates the project object from the attributes of a "project" element.
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
  public static PSProject toProject(Attributes atts, PSGroup group) {

    String id = atts.getValue("id");
    String name = atts.getValue("name");
    String description = atts.getValue("description");
    PSRole defaultRole = PSHandlers.role(atts.getValue("defaultrole"));
    PSNotification defaultNotification = PSHandlers.notification(atts.getValue("defaultnotify"));

    PSProject p = group instanceof PSProject ? (PSProject) group : null;
    if (p == null) {
      PSEntityCache<PSGroup> cache = GroupManager.getCache();
      // FIXME: If project was previous a group??
      p = (PSProject) cache.get(id);
      if (p == null) {
        p = new PSProject();
      }
    }

    p.setId(PSHandlers.id(id));
    p.setName(name);
    p.setDescription(description);
    if (defaultRole != null) {
      p.setDefaultRole(defaultRole);
    }
    if (defaultNotification != null) {
      p.setDefaultNotification(defaultNotification);
    }
    return p;
  }

  /**
   *
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
   *
   * @param atts     The attributes the "uri" element
   * @param document The PSDocument instance (may be <code>null</code>).
   *
   * @return The corresponding PSDocument.
   */
  public static PSDocument toDocument(Attributes atts, PSDocument document) {
    String id = atts.getValue("id");
    String path = atts.getValue("path");
    String description = atts.getValue("description");
    String docid = atts.getValue("docid");
    String filename = atts.getValue("filename");
    String labels = atts.getValue("labels");
    String title = atts.getValue("title");
    String type = atts.getValue("type");
    if (type == null) {
      type = atts.getValue("documenttype");
    }
    String mediatype = atts.getValue("mediatype");
    String created = atts.getValue("created");

    PSDocument d = document;
    if (d == null) {
      PSEntityCache<PSDocument> cache = DocumentManager.getCache();
      d = cache.get(id);
      if (d == null) {
        d = new PSDocument(path);
      }
    }
    d.setId(PSHandlers.id(id));
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
    return d;
  }

  /**
   *
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
   *
   * @param atts     The attributes the "uri" element
   * @param document The PSDocument instance (may be <code>null</code>).
   *
   * @return The corresponding PSDocument.
   */
  public static PSExternalURI toExternalURI(Attributes atts, PSExternalURI externaluri) {
    String id = atts.getValue("id");
    String path = atts.getValue("path");
    String description = atts.getValue("description");
    String docid = atts.getValue("docid");
    String filename = atts.getValue("filename");
    String labels = atts.getValue("labels");
    String title = atts.getValue("title");
    String mediatype = atts.getValue("mediatype");
    boolean folder = "true".equals(atts.getValue("folder"));

    PSExternalURI u = externaluri;
    if (u == null) {
      PSEntityCache<PSExternalURI> cache = ExternalURIManager.getCache();
      u = cache.get(id);
      if (u == null) {
        u = new PSExternalURI(path);
      }
    }
    u.setId(PSHandlers.id(id));
    u.setDescription(description);
    u.setDocid(docid);
    u.setLabels(labels);
    u.setTitle(title);
    u.setMediaType(mediatype);
    u.setFolder(folder);
    return u;
  }

  /**
   *
   * @param atts    The attributes the "comment" element
   * @param comment The PSComment instance (may be <code>null</code>).
   *
   * @return The corresponding PSComment
   */
  public static PSComment toComment(Attributes atts, PSComment comment) {
    String id = atts.getValue("id");
    String status = atts.getValue("status");
    String priority = atts.getValue("priority");
    // TODO Due date
    String due = atts.getValue("due");
    String labels = atts.getValue("labels");
    String type = atts.getValue("type");
    String properties = atts.getValue("properties");

    PSComment c = comment;
    if (c == null) {
      PSEntityCache<PSComment> cache = CommentManager.getCache();
      c = cache.get(id);
      if (c == null) {
        c = new PSComment();
      }
    }
    c.setId(PSHandlers.id(id));
    c.setLabels(labels);
    c.setStatus(status);
    c.setPriority(priority);
    c.setType(type);
    c.setProperties(properties);
    return c;
  }

  /**
   *
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
   *
   * @param atts   The attributes of the "uri" element
   * @param folder The folder instance (may be <code>null</code>).
   *
   * @return the corresponding folder instance.
   */
  public static PSFolder toFolder(Attributes atts, PSFolder folder) {
    String id = atts.getValue("id");
    String path = atts.getValue("path");
    String description = atts.getValue("description");
    String docid = atts.getValue("docid");
    String labels = atts.getValue("labels");
    String title = atts.getValue("title");
    String mediatype = atts.getValue("mediatype");

    PSFolder d = folder;
    if (d == null) {
      PSEntityCache<PSFolder> cache = DocumentManager.getFoldersCache();
      d = cache.get(id);
      if (d == null) {
        d = new PSFolder(path);
      }
    }
    d.setId(PSHandlers.id(id));
    d.setDescription(description);
    d.setDocid(docid);
    d.setLabels(labels);
    d.setTitle(title);
    d.setMediaType(mediatype);
    return d;
  }

  /**
   * Generates the group folder object from the attributes of a "groupfolder" element.
   *
   *
   * @param atts   the attributes of the "groupfolder" element.
   * @param folder an existing group folder instance to reuse.
   *
   * @return The group folder instance.
   */
  public static PSGroupFolder toGroupFolder(Attributes atts, PSGroupFolder folder) {

    String id = atts.getValue("id");
    String scheme = atts.getValue("scheme");
    String host = atts.getValue("host");
    String port = atts.getValue("port");
    String path = atts.getValue("path");
    boolean isExternal = "true".equals(atts.getValue("external"));

    PSGroupFolder f = folder;
    if (f == null) {
      PSEntityCache<PSGroupFolder> cache = GroupManager.getFoldersCache();
      f = cache.get(id);
      if (f == null) {
        f = new PSGroupFolder(path);
      }
    }

    f.setId(PSHandlers.id(id));
    f.setScheme(scheme);
    f.setHost(host);
    f.setPort(PSHandlers.integer(port));
    f.setPath(path);
    f.setExternal(isExternal);
    return f;
  }
}
