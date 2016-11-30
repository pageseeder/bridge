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
package org.pageseeder.bridge.control;

import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.FailedPrecondition;
import org.pageseeder.bridge.InvalidEntityException;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.Requires;
import org.pageseeder.bridge.model.GroupOptions;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSGroupFolder;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSNotification;
import org.pageseeder.bridge.model.PSProject;
import org.pageseeder.bridge.model.PSResource;
import org.pageseeder.bridge.model.PSRole;
import org.pageseeder.bridge.model.PSThreadStatus;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.net.PSHTTPResponseInfo;
import org.pageseeder.bridge.xml.PSGroupFolderHandler;
import org.pageseeder.bridge.xml.PSGroupHandler;
import org.pageseeder.bridge.xml.PSThreadHandler;

/**
 * A manager for groups, projects and group folders.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
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
   * @param PSCredentials A valid session to connect to PageSeeder.
   */
  public GroupManager(PSCredentials PSCredentials) {
    super(PSCredentials);
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
  public void createGroup(PSGroup group, PSMember creator, @Nullable GroupOptions options)
      throws FailedPrecondition, APIException {
    Objects.requireNonNull(group, "Group must be specified");
    Objects.requireNonNull(creator, "Member must be specified");
    PSHTTPConnector connector = PSHTTPConnectors.createGroup(group, creator, options).using(this._credentials);
    PSGroupHandler handler = new PSGroupHandler(group);
    PSHTTPResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400) throw new APIException("Unable to create group '" + group.getName() + "': " + info.getMessage());
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
  public void createProject(PSProject project, PSMember creator, @Nullable GroupOptions options)
      throws FailedPrecondition, APIException {
    Objects.requireNonNull(project, "Project must be specified");
    Objects.requireNonNull(creator, "Member must be specified");
    PSHTTPConnector connector = PSHTTPConnectors.createProject(project, creator, options).using(this._credentials);
    PSGroupHandler handler = new PSGroupHandler(project);
    PSHTTPResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400) throw new APIException("Unable to create project '" + project.getName() + "': " + info.getMessage());
    cache.put(project);
  }

  /**
   * Creates the group folder.
   *
   * @param group   The group where the group folder should be created.
   * @param url     The url of the group folder.
   *
   * @throws InvalidEntityException If the group is invalid.
   * @throws APIException If an error occurs while communicating with PageSeeder.
   */
  public void createGroupFolder(PSGroup group, String url)
      throws InvalidEntityException, APIException {
    if (!group.isValid()) throw new InvalidEntityException(PSGroup.class, group.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.createGroupFolder(group, url).using(this._credentials);
    PSGroupFolderHandler handler = new PSGroupFolderHandler();
    PSHTTPResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400) throw new APIException("Unable to create group folder '" + url + "': " + info.getMessage());
    PSGroupFolder folder = handler.get();
    if (folder != null) {
      folders.put(folder);
    }
  }

  /**
   * Creates a personal group for specified member.
   *
   * @param member The member needs to create a personal group.
   *
   * @throws APIException If an error occurs while communicating with PageSeeder.
   */
  public void createPersonalGroup(PSMember member) throws APIException {
    Objects.requireNonNull(member, "Member must be specified");
    PSHTTPConnector connector = PSHTTPConnectors.createPersonalGroup(member).using(this._credentials);
    PSGroup group = new PSGroup("member-" + member.getId() + "-home");
    PSGroupHandler handler = new PSGroupHandler(group);
    PSHTTPResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400) throw new APIException("Unable to create personal group '" + group.getName() + "': " + info.getMessage());
    cache.put(group);
  }

  /**
   * Renames the specified group in PageSeeder.
   *
   * <p>Renaming a group is an asynchronous operation on PageSeeder so this method returns a {@link PSThreadStatus} object.
   *
   * <p>The {@link #groupIsRenamed(PSGroup)} must be called when the thread is completed successfully.
   *
   * @param group   The group to rename
   * @param editor  The member making the request
   * @param newname The new group name
   *
   * @throws FailedPrecondition   Should a precondition fail
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If the group, editor or newname is <code>null</code>.
   */
  public @Nullable PSThreadStatus renameGroup(PSGroup group, PSMember editor, String newname) throws FailedPrecondition, APIException {
    Objects.requireNonNull(group, "Group must be specified");
    Objects.requireNonNull(editor, "Member must be specified");
    Objects.requireNonNull(newname, "New group name must be specified");
    PSHTTPConnector connector = PSHTTPConnectors.renameGroup(group, editor, newname).using(this._credentials);
    PSThreadHandler handler = new PSThreadHandler();
    PSHTTPResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400) throw new APIException("Unable to rename group '" + group.getName() + "': " + info.getMessage());
    return handler.getThreadStatus();
  }

  /**
   * Should be called when the rename group thread is completed, to update the local cache
   *
   * @param group the group that was renamed
   *
   * @throws NullPointerException If the group is <code>null</code>.
   */
  public void groupIsRenamed(PSGroup group) {
    cache.put(Objects.requireNonNull(group, "group"));
  }

  /**
   * Archives the specified group in PageSeeder.
   *
   * <p>Archiving a group is an asynchronous operation on PageSeeder so this method returns a {@link PSThreadStatus} object.
   *
   * <p>The {@link #groupIsArchived(PSGroup)} must be called when the thread is completed successfully.
   *
   * @param group   The group to archive
   * @param editor  The member making the request
   *
   * @throws FailedPrecondition   Should a precondition fail
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If the group or editor is <code>null</code>.
   */
  public @Nullable PSThreadStatus archiveGroup(PSGroup group, PSMember editor) throws FailedPrecondition, APIException {
    Objects.requireNonNull(group, "Group must be specified");
    Objects.requireNonNull(editor, "Member must be specified");
    PSHTTPConnector connector = PSHTTPConnectors.archiveGroup(group, editor).using(this._credentials);
    PSThreadHandler handler = new PSThreadHandler();
    PSHTTPResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400) throw new APIException("Unable to archive group '" + group.getName() + "': " + info.getMessage());
    return handler.getThreadStatus();
  }

  /**
   * Should be called when the archive group thread is completed, to update the local cache
   *
   * @param group the group that was archived
   *
   * @throws NullPointerException If the group is <code>null</code>.
   */
  public void groupIsArchived(PSGroup group) {
    String groupKey = Objects.requireNonNull(group.getKey());
    cache.remove(groupKey);
  }

  /**
   * Edit the specified group in PageSeeder (name not included, use {@link #renameGroup(PSGroup, PSMember, String)} to rename a group).
   *
   * @param group   The group to edit
   * @param editor  The member making the request
   *
   * @throws FailedPrecondition   Should a precondition fail to edit the group
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If the group or editor is <code>null</code>.
   */
  public void editGroup(PSGroup group, PSMember editor) throws FailedPrecondition, APIException {
    editGroup(group, editor, null);
  }

  /**
   * Edit the specified group in PageSeeder (name not included, use {@link #renameGroup(PSGroup, PSMember, String)} to rename a group).
   *
   * <p>The optional group options parameters can be used to specify additional options to edit the group
   * or set some group properties.
   *
   * @param group   The group to edit
   * @param editor  The member making the request
   * @param options The group creation options (including group properties)
   *
   * @throws FailedPrecondition   Should a precondition fail to edit the group
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If the group or editor is <code>null</code>.
   */
  public void editGroup(PSGroup group, PSMember editor, @Nullable GroupOptions options) throws FailedPrecondition, APIException {
    Objects.requireNonNull(group, "Group must be specified");
    Objects.requireNonNull(editor, "Member must be specified");
    PSHTTPConnector connector = PSHTTPConnectors.patchGroup(group, editor, options).using(this._credentials);
    PSThreadHandler handler = new PSThreadHandler();
    PSHTTPResponseInfo info = connector.patch(handler);
    if (info.getCode() >= 400) throw new APIException("Unable to edit group '" + group.getName() + "': " + info.getMessage());
    // update cache
    cache.put(group);
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
  public @Nullable PSGroup get(String name) throws APIException {
    PSGroup group = cache.get(Objects.requireNonNull(name));
    if (group == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getGroup(name).using(this._credentials);
      PSGroupHandler handler = new PSGroupHandler();
      connector.get(handler);
      group = handler.get();
      if (group != null) {
        cache.put(group);
      }
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
  public @Nullable PSProject getProject(String name) throws APIException {
    PSGroup group = get(name);
    if (group == null) return null;
    else if (group instanceof PSProject) return (PSProject) group;
    else throw new APIException("Not a project");
  }

  /**
   * Returns the  group folder for the specified URL.
   *
   * @param group The group the group folder belongs to
   * @param url   The URL of the group folder
   *
   * @return The corresponding instance or <code>null</code>.
   */
  public @Nullable PSGroupFolder getGroupFolder(PSGroup group, String url) throws APIException {
    Objects.requireNonNull(group, "Group must be specified");
    Objects.requireNonNull(url, "URL must be specified");
    PSGroupFolder folder = folders.get(url);
    if (folder == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getGroupFolder(group, url).using(this._credentials);
      PSGroupFolderHandler handler = new PSGroupFolderHandler();
      PSHTTPResponseInfo info = connector.get(handler);
      // TODO We should simply return null
      if (info.getCode() >= 400) throw new APIException("Unable to find group folder '" + url + "': " + info.getMessage());
      folder = handler.get();
      if (folder != null) {
        folders.put(folder);
      }
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
    PSHTTPConnector connector = PSHTTPConnectors.addSubGroup(group, subgroup).using(this._credentials);
    PSHTTPResponseInfo info = connector.post();
    if (info.getCode() >= 400) throw new APIException("Unable to add subgroup '" + subgroup.getName() + "' to '" + group.getName() + "': " + info.getMessage());
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
    PSHTTPConnector connector = PSHTTPConnectors.addSubGroup(group, subgroup, notification, role, listed).using(this._credentials);
    PSHTTPResponseInfo info = connector.post();
    if (info.getCode() >= 400) throw new APIException("Unable to add subgroup '" + subgroup.getName() + "' to '" + group.getName() + "': " + info.getMessage());
  }

  /**
   * Removes a subgroup from another group.
   *
   * @param group    The "parent" group.
   * @param subgroup The subgroup to remove.
   *
   * @throws APIException
   */
  public void deleteSubGroup(PSGroup group, PSGroup subgroup) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.deleteSubGroup(group, subgroup).using(this._credentials);
    PSHTTPResponseInfo info = connector.post();
    if (info.getCode() >= 400) throw new APIException("Unable to remove subgroup '" + subgroup.getName() + "' from '" + group.getName() + "': " + info.getMessage());
  }

  /**
   * Returns the list of subgroups for the specified group.
   *
   * @param group The group.
   *
   * @throws APIException
   */
  public List<PSGroup> listSubGroups(PSGroup group) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.listSubGroups(group).using(this._credentials);
    PSGroupHandler handler = new PSGroupHandler();
    PSHTTPResponseInfo info = connector.get(handler);
    // TODO We should simply return null?
    if (info.getCode() >= 400) throw new APIException("Unable to list subgroups of '" + group.getName() + "': " + info.getMessage());
    List<PSGroup> subgroups = handler.list();
    return subgroups;
  }

  /**
   * Returns the list of projects and groups for the given member.
   * This is less efficient than findGroups methods.
   *
   * @param member The member making the request.
   * @param nameprefix the prefix of project/group.
   * @param max the maximum number of projects/groups to return (may be more results as ancestors are added).
   *
   * @return the list of projects and groups for the given member.
   *
   * @throws APIException
   */
  public List<PSGroup> listProjectTree(PSMember member, String nameprefix, int max) throws APIException {
    return listProjectTree(member, nameprefix, max, true, false);
  }

  /**
   * Returns the list of projects and groups for the given member.
   * This is less efficient than findGroups methods.
   *
   * @param member The member making the request.
   * @param nameprefix the prefix of project/group.
   * @param max the maximum number of projects/groups to return (may be more results as ancestors are added).
   * @param showGroup whether to return groups
   * @param showAll Whether to return all projects/groups for server (Administrator only)
   *
   * @return the list of projects and groups for the given member.
   *
   * @throws APIException
   */
  public List<PSGroup> listProjectTree(PSMember member, String nameprefix, int max, boolean showGroup, boolean showAll) throws APIException {
    Objects.requireNonNull(member);
    PSHTTPConnector connector = PSHTTPConnectors.listProjectsTree(member, nameprefix, max, showGroup, showAll).using(this._credentials);
    PSGroupHandler handler = new PSGroupHandler();
    PSHTTPResponseInfo info = connector.get(handler);
    if (info.getCode() >= 400) throw new APIException("Unable to list groups for member '" + member.getId() + "': " + info.getMessage());
    List<PSGroup> groups = handler.list();
    return groups;
  }

  /**
   * Returns the list of projects and groups (max 1000) for the given member.
   * This is more efficient than listProjectTree methods.
   *
   * @param member      The member
   * @param prefix      The prefix of project/group name
   * @param includeAll  Whether to return all projects/groups for server (Administrator only)
   *
   * @throws APIException
   */
  public List<PSGroup> findGroups(PSMember member, String prefix, boolean includeAll) throws APIException {
    return findGroups(member, prefix, 1000, includeAll);
  }

  /**
   * Returns the list of projects and groups for the given member.
   * This is more efficient than listProjectTree methods.
   *
   * @param member      The member
   * @param prefix      The prefix of project/group name
   * @param max         The maximum number of projects/groups to return.
   * @param includeAll  Whether to return all projects/groups for server (Administrator only)
   *
   * @throws APIException
   */
  public List<PSGroup> findGroups(PSMember member, String prefix, int max, boolean includeAll) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.findProjects(member, prefix, max, includeAll).using(this._credentials);
    PSGroupHandler handler = new PSGroupHandler();
    PSHTTPResponseInfo info = connector.get(handler);
    // TODO We should simply return null?
    if (info.getCode() >= 400) throw new APIException("Unable to find groups of '" + member.getId() + "': " + info.getMessage());
    List<PSGroup> groups = handler.list();
    // cache them
    for (PSGroup group : groups) {
      cache.put(group);
    }
    return groups;
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
    PSHTTPConnector connector = PSHTTPConnectors.putResource(project, resource, overwrite).using(this._credentials);
    PSHTTPResponseInfo info = connector.put();
    if (info.getCode() >= 400) throw new APIException("Unable to put project resource on '" + project.getName() + "': " + info.getMessage());
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

}
