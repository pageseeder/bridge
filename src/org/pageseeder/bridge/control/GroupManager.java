/*
 * Copyright (c) 2014 Allette Systems
 */
package org.pageseeder.bridge.control;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.FailedPrecondition;
import org.pageseeder.bridge.InvalidEntityException;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.Requires;
import org.pageseeder.bridge.model.GroupOptions;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSGroupFolder;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSNotification;
import org.pageseeder.bridge.model.PSProject;
import org.pageseeder.bridge.model.PSResource;
import org.pageseeder.bridge.model.PSRole;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.net.PSHTTPResponseInfo;
import org.pageseeder.bridge.xml.PSGroupFolderHandler;
import org.pageseeder.bridge.xml.PSGroupHandler;

/**
 * A manager for groups, projects and group folders.
 *
 * @author Christophe Lauret
 * @version 0.2.4
 * @since 0.2.0
 */
public final class GroupManager extends Sessionful {

  /**
   * Where the groups (and projects) are cached.
   */
  private static volatile PSEntityCache<PSGroup> cache = EHEntityCache.newInstance("psgroups");

  /**
   * Where the group folders are cached.
   */
  private static volatile PSEntityCache<PSGroupFolder> folders = EHEntityCache.newInstance("psgroupfolders");

  /**
   * Creates a new manager for PageSeeder groups.
   *
   * @param session A valid session to connect to PageSeeder.
   */
  public GroupManager(PSSession session) {
    super(session);
  }

  /**
   * Creates the specified group in PageSeeder using default PageSeeder options.
   *
   * <p>The member creating the group will automatically be made a member of the group.
   *
   * @param group   The group to create
   * @param creator The member creating the request
   *
   * @throws FailedPrecondition   Should a precondition fail to create a group
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If either the group or member is <code>null</code>.
   */
  public void createGroup(PSGroup group, PSMember creator) throws FailedPrecondition, APIException {
    createGroup(group, creator, null);
  }

  /**
   * Creates the specified group in PageSeeder.
   *
   * <p>The optional group options parameters can be used to specify additional option to create the group
   * or set some group properties.
   *
   * @param group   The group to create
   * @param creator The member making the request
   * @param options The group creation options (including group properties)
   *
   * @throws FailedPrecondition   Should a precondition fail to create a group
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If either the group or member is <code>null</code>.
   */
  public void createGroup(PSGroup group, PSMember creator, GroupOptions options)
      throws FailedPrecondition, APIException {
    if (group == null) throw new NullPointerException("group");
    if (creator == null) throw new NullPointerException("member");
    PSHTTPConnector connector = PSHTTPConnectors.createGroup(group, creator, options).using(this._session);
    PSGroupHandler handler = new PSGroupHandler(group);
    PSHTTPResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400)
      throw new APIException("Unable to create group '"+group.getName()+"': "+info.getMessage());
    cache.put(group);
  }

  /**
   * Creates the specified project in PageSeeder.
   *
   * @param project The project to create
   * @param creator The member making the request
   *
   * @throws FailedPrecondition   Should a precondition fail to create a project
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If either the project or member is <code>null</code>.
   */
  public void createProject(PSProject project, PSMember creator) throws FailedPrecondition, APIException {
    createProject(project, creator, null);
  }

  /**
   * Creates the specified project in PageSeeder.
   *
   * <p>The optional project options parameters can be used to specify additional option to create the project
   * or set some group properties.
   *
   * @param project The project to create
   * @param creator The member making the request
   * @param options The group creation options (including group properties)
   *
   * @throws FailedPrecondition   Should a precondition fail to create a project
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If either the project or member is <code>null</code>.
   */
  public void createProject(PSProject project, PSMember creator, GroupOptions options)
     throws FailedPrecondition, APIException {
    if (project == null) throw new NullPointerException("project");
    if (creator == null) throw new NullPointerException("creator");
    PSHTTPConnector connector = PSHTTPConnectors.createProject(project, creator, options).using(this._session);
    PSGroupHandler handler = new PSGroupHandler(project);
    PSHTTPResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400)
      throw new APIException("Unable to create project '"+project.getName()+"': "+info.getMessage());
    cache.put(project);
  }

  /**
   * Creates the group folder.
   *
   * @param group The group where the group folder should be created.
   */
  public void createGroupFolder(PSGroup group, String url, boolean isPublic)
      throws InvalidEntityException, APIException {
    if (!group.isValid()) throw new InvalidEntityException(PSGroup.class, group.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.createGroupFolder(group, url, isPublic).using(this._session);
    PSGroupFolderHandler handler = new PSGroupFolderHandler();
    PSHTTPResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400)
      throw new APIException("Unable to create group folder '"+url+"': "+info.getMessage());
    PSGroupFolder folder = handler.get();
    if (folder != null)
      folders.put(folder);
  }

  /**
   * Returns the group for the specified name.
   *
   * @param name The name of the group.
   * @return The corresponding instance.
   *
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If either the name of the group is <code>null</code>.
   */
  @Requires(minVersion = 56000)
  public PSGroup get(String name) throws APIException {
    if (name == null) throw new NullPointerException("name");
    PSGroup group = cache.get(name);
    if (group == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getGroup(name).using(this._session);
      PSGroupHandler handler = new PSGroupHandler();
      connector.get(handler);
      group = handler.get();
      if (group != null)
        cache.put(group);
    }
    return group;
  }

  /**
   * Returns the project for the specified name.
   *
   * @param name The name of the project.
   * @return The corresponding instance or <code>null</code>.
   *
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If either the name of the group is <code>null</code>.
   */
  public PSProject getProject(String name) throws APIException {
    PSGroup group = get(name);
    if (group == null)
      return null;
    else if (group instanceof PSProject)
      return (PSProject)group;
    else
      throw new APIException("Not a project");
  }

  /**
   * Returns the  group folder for the specified URL.
   *
   * @param group The group the group folder belongs to
   * @param url   The URL of the group folder
   *
   * @return The corresponding instance or <code>null</code>.
   */
  public PSGroupFolder getGroupFolder(PSGroup group, String url) throws APIException {
    if (group == null) throw new NullPointerException("group");
    if (url == null) throw new NullPointerException("url");
    PSGroupFolder folder = folders.get(url);
    if (folder == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getGroupFolder(group, url).using(this._session);
      PSGroupFolderHandler handler = new PSGroupFolderHandler();
      PSHTTPResponseInfo info = connector.get(handler);
      // TODO We should simply return null
      if (info.getCode() >= 400)
        throw new APIException("Unable to find group folder '"+url+"': "+info.getMessage());
      folder = handler.get();
      if (folder != null)
        folders.put(folder);
    }
    return folder;
  }

  /**
   * Adds a group as a subgroup of another.
   *
   * @param group    The group accepting the subgroup.
   * @param subgroup The subgroup to add to the group.
   *
   * @throws APIException
   */
  public void addSubGroup(PSGroup group, PSGroup subgroup) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.addSubGroup(group, subgroup).using(this._session);
    PSHTTPResponseInfo info = connector.post();
    if (info.getCode() >= 400)
      throw new APIException("Unable to add subgroup '"+subgroup.getName()+"' to '"+group.getName()+"': "+info.getMessage());
  }

  /**
   * Adds a group as a subgroup of another.
   *
   * @param group        The group accepting the subgroup.
   * @param subgroup     The subgroup to add to the group.
   * @param notification The notification options for users added via the subgroup.
   * @param role         The role for users added via the subgroup.
   * @param listed       Whether users added via the subgroup should have their email address listed.
   *
   * @throws APIException
   */
  public void addSubGroup(PSGroup group, PSGroup subgroup, PSNotification notification, PSRole role, boolean listed)
      throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.addSubGroup(group, subgroup, notification, role, listed).using(this._session);
    PSHTTPResponseInfo info = connector.post();
    if (info.getCode() >= 400)
      throw new APIException("Unable to add subgroup '"+subgroup.getName()+"' to '"+group.getName()+"': "+info.getMessage());
  }

  /**
   * Put a resource on the project.
   *
   * <p>Implementation note: this method only supports text resources, not binary resources.
   *
   * @param project   The project the resource is associated with.
   * @param resource  The actual resource
   * @param overwrite <code>true</code> to overwrite the resource; <code>false</code> otherwise.
   *
   * @throws FailedPrecondition If the location is empty;
   *                            or the project is not identifiable;
   *                            or the content is binary;
   *                            or the content is empty.
   * @throws APIException For any other error communicating with PageSeeder.
   */
  public void putResource(PSProject project, PSResource resource, boolean overwrite)
      throws FailedPrecondition, APIException {
    PSHTTPConnector connector = PSHTTPConnectors.putResource(project, resource, overwrite).using(this._session);
    PSHTTPResponseInfo info = connector.post();
    if (info.getCode() >= 400)
      throw new APIException("Unable to put project resource on '"+project.getName()+"': "+info.getMessage());
  }

  /**
   * Returns the internal cache used for the groups.
   *
   * @return the internal cache used for the groups.
   */
  public static PSEntityCache<PSGroup> getCache() {
    return cache;
  }

  /**
   * Returns the internal cache used for group folders.
   *
   * @return the internal cache used for group folders
   */
  public static PSEntityCache<PSGroupFolder> getFoldersCache() {
    return folders;
  }

  /**
   * @return the host URL.
   */
  public static String getHostURL() {
    return PSConfig.singleton().getHostURL();
  }

}
