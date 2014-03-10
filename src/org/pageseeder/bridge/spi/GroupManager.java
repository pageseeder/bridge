/*
 * Copyright (c) 2014 Allette Systems
 */
package org.pageseeder.bridge.spi;

import java.util.HashMap;
import java.util.Map;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.InvalidEntityException;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.core.PSGroup;
import org.pageseeder.bridge.core.PSGroupFolder;
import org.pageseeder.bridge.core.PSMember;
import org.pageseeder.bridge.core.PSNotification;
import org.pageseeder.bridge.core.PSProject;
import org.pageseeder.bridge.core.PSResource;
import org.pageseeder.bridge.core.PSRole;
import org.pageseeder.bridge.net.PSConnector;
import org.pageseeder.bridge.net.PSConnectors;
import org.pageseeder.bridge.net.PSResponseInfo;
import org.pageseeder.bridge.xml.PSGroupFolderHandler;
import org.pageseeder.bridge.xml.PSGroupHandler;

/**
 * A manager for groups and projects (based on PageSeeder Groups)
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public class GroupManager extends PSManager {

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
   * @param user The user that can connect to PageSeeder.
   */
  public GroupManager(PSSession user) {
    super(user);
  }

  /**
   * Creates the group the specified group in PageSeeder using default options and
   *
   * @param group The group to create
   * @return The corresponding instance.
   */
  public void createGroup(PSGroup group, PSMember member) throws APIException {
    createGroup(group, null, member);
  }

  /**
   * Creates the group the specified group in PageSeeder.
   *
   * @param group      The group to create
   * @param properties The group properties
   * @return The corresponding instance.
   */
  public void createGroup(PSGroup group, GroupOptions options, PSMember member) throws APIException {
    if (group == null) throw new NullPointerException("group");
    PSConnector connector = PSConnectors.createGroup(group, options, member.getUsername());
    PSGroupHandler handler = new PSGroupHandler(group);
    connector.setUser(this.user);
    PSResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400)
      throw new APIException("Unable to create group '"+group.getName()+"': "+info.getMessage());
    cache.put(group);
  }

  /**
   * Creates the group the specified group in PageSeeder.
   *
   * @param project The project to create
   * @return The corresponding instance.
   */
  public void createProject(PSProject project, PSMember member) throws APIException {
    createProject(project, null, member);
  }

  /**
   * Creates the group the specified group in PageSeeder.
   *
   * @param project The project to create
   * @return The corresponding instance.
   */
  public void createProject(PSProject project, GroupOptions options, PSMember member) throws APIException {
    if (project == null) throw new NullPointerException("project");
    if (!project.isValid()) throw new InvalidEntityException(PSProject.class, project.checkValid());
    PSConnector connector = PSConnectors.createProject(project, options, member.getUsername());
    PSGroupHandler handler = new PSGroupHandler(project);
    connector.setUser(this.user);
    PSResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400)
      throw new APIException("Unable to create project '"+project.getName()+"': "+info.getMessage());
    cache.put(project);
  }

  /**
   * Creates the group folder.
   *
   * @param group The group where the group folder should be created.
   * @return The corresponding instance.
   */
  public void createGroupFolder(PSGroup group, String url, boolean isPublic) throws APIException {
    if (!group.isValid()) throw new InvalidEntityException(PSGroup.class, group.checkValid());
    PSConnector connector = PSConnectors.createGroupFolder(group, url, isPublic);
    PSGroupFolderHandler handler = new PSGroupFolderHandler();
    connector.setUser(this.user);
    PSResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400)
      throw new APIException("Unable to create group folder '"+url+"': "+info.getMessage());
    PSGroupFolder folder = handler.getGroupFolder();
    if (folder != null)
      folders.put(folder);
  }

  /**
   * Returns the group for the specified name
   *
   * @param name The name of the group.
   * @return The corresponding instance.
   */
  public PSGroup get(String name, PSMember member) throws APIException {
    if (name == null) throw new NullPointerException("name");
    PSGroup group = cache.get(name);
    if (group == null) {
      PSConnector connector = PSConnectors.getGroup(name, member.getUsername());
      PSGroupHandler handler = new PSGroupHandler();
      connector.setUser(this.user);
      connector.get(handler);
      group = handler.getGroup();
      if (group != null)
        cache.put(group);
    }
    return group;
  }

  /**
   * Returns the project for the specified name
   *
   * @param name The name of the project.
   * @return The corresponding instance or <code>null</code>.
   */
  public PSProject getProject(String name, PSMember member) throws APIException {
    PSGroup group = get(name, member);
    if (group == null)
      return null;
    else if (group instanceof PSProject)
      return (PSProject)group;
    else
      throw new APIException("Not a project");
  }

  /**
   * gets the group folder.
   *
   * @param group The group the group folder belongs to
   *
   * @return The corresponding instance.
   */
  public PSGroupFolder getGroupFolder(PSGroup group, String url) throws APIException {
    PSGroupFolder folder = folders.get(url);
    if (folder == null) {
      PSConnector connector = PSConnectors.getGroupFolder(group, url);
      PSGroupFolderHandler handler = new PSGroupFolderHandler();
      connector.setUser(this.user);
      PSResponseInfo info = connector.get(handler);
      if (info.getCode() >= 400)
        throw new APIException("Unable to find group folder '"+url+"': "+info.getMessage());
      folder = handler.getGroupFolder();
      if (folder != null)
        folders.put(folder);
    }
    return folder;
  }

  /**
   * Adds a group as a subgroup of another
   *
   * @param group    The group accepting the subgroup.
   * @param subgroup The subgroup to add to the group.
   *
   * @throws APIException
   */
  public void addSubGroup(PSGroup group, PSGroup subgroup) throws APIException {
    PSConnector connector = PSConnectors.addSubGroup(group, subgroup);
    if (!group.isValid() && !subgroup.isValid()) throw new InvalidEntityException(PSGroup.class, group.checkValid());
    connector.setUser(this.user);
    PSResponseInfo info = connector.post();
    if (info.getCode() >= 400)
      throw new APIException("Unable to add subgroup '"+subgroup.getName()+"' to '"+group.getName()+"': "+info.getMessage());
  }

  /**
   * Adds a group as a subgroup of another
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
    if (!group.isValid() || !subgroup.isValid()) throw new InvalidEntityException(PSGroup.class, group.checkValid());
    PSConnector connector = PSConnectors.addSubGroup(group, subgroup, notification, role, listed);
    connector.setUser(this.user);
    PSResponseInfo info = connector.post();
    if (info.getCode() >= 400)
      throw new APIException("Unable to add subgroup '"+subgroup.getName()+"' to '"+group.getName()+"': "+info.getMessage());
  }

  /**
   * Put a resource on the project.
   *
   * @param project  The project the resource is associated with.
   * @param resource The actual resource
   * @param overwrite
   * @throws APIException
   */
  public void putResource(PSProject project, PSResource resource, boolean overwrite) throws APIException {
    if (!project.isValid()) throw new InvalidEntityException(PSProject.class, project.checkValid());
    PSConnector connector = PSConnectors.putResource(project.getName(), resource, overwrite);
    connector.setUser(this.user);
    PSResponseInfo info = connector.post();
    if (info.getCode() >= 400)
      throw new APIException("Unable to put project resource on '"+project.getName()+"': "+info.getMessage());
  }

  /**
   * Returns the internal cache used for the groups.
   *
   * @return
   */
  public static PSEntityCache<PSGroup> getCache() {
    return cache;
  }

  /**
   * Returns the internal cache used for the groups.
   *
   * @return
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

  /**
   * Additional options when creating a group which aren't part of the group entity.
   *
   * Most values are initialised to <code>null</code> so that it will use PageSeeder's default.
   */
  public static final class GroupOptions {

    /**
     * The subscription message
     */
    private String message = null;

    /**
     * Access to group and comments [public|member]  (default is "member")
     */
    private String access = null;

    /**
     * Who can comment [public|reviewer|contributor] (default is "public")
     */
    private String commenting = null;

    /**
     * Which comments will be moderated [reviewer|email|all] (default is "reviewer")
     */
    private String moderation = null;

    /**
     * Self-registration method [normal|moderated|confirmed] (default is "normal")
     */
    private String registration = null;

    /**
     * To set the control group.
     */
    private String visibility = null;

    /**
     * Whether to add the current member as a manager (default is "true")
     */
    private boolean addmember = true;

    /**
     * Whether to create default documents (ignored for projects) (default is "true")
     */
    private boolean createdocuments = true;

    /**
     * Whether this is a common group (default is "false")
     */
    private boolean common = false;

    /**
     * Group properties.
     */
    private Map<String, String> properties = new HashMap<String, String>();

    /**
     * @return the message
     */
    public String getMessage() {
      return this.message;
    }

    /**
     * @return the access
     */
    public String getAccess() {
      return this.access;
    }

    /**
     * @return the commenting
     */
    public String getCommenting() {
      return this.commenting;
    }

    /**
     * @return the moderation
     */
    public String getModeration() {
      return this.moderation;
    }

    /**
     * @return the registration
     */
    public String getRegistration() {
      return this.registration;
    }

    /**
     * @return the common
     */
    public boolean isCommon() {
      return this.common;
    }

    public boolean isAddmember() {
      return this.addmember;
    }

    public boolean isCreatedocuments() {
      return this.createdocuments;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
      this.message = message;
    }

    /**
     * @param access the access to set
     */
    public void setAccess(String access) {
      this.access = access;
    }

    /**
     * @param commenting the commenting to set
     */
    public void setCommenting(String commenting) {
      this.commenting = commenting;
    }

    /**
     * @param moderation the moderation to set
     */
    public void setModeration(String moderation) {
      this.moderation = moderation;
    }

    /**
     * @param registration the registration to set
     */
    public void setRegistration(String registration) {
      this.registration = registration;
    }

    /**
     * @param common the common to set
     */
    public void setCommon(boolean common) {
      this.common = common;
    }

    public void setAddmember(boolean addmember) {
      this.addmember = addmember;
    }

    public void setCreatedocuments(boolean createdocuments) {
      this.createdocuments = createdocuments;
    }

    public void setProperty(String key, String value) {
      this.properties.put(key, value);
    }

    public String getProperty(String key) {
      return this.properties.get(key);
    }

    public Map<String, String> getProperties() {
      return this.properties;
    }

    public void setVisibility(String visibility) {
      this.visibility = visibility;
    }

    public String getVisibility() {
      return this.visibility;
    }
  }

}
