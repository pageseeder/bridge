/*
 * Copyright (c) 2014 Allette Systems
 */
package org.pageseeder.bridge.control;

import java.util.HashMap;
import java.util.Map;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.FailedPrecondition;
import org.pageseeder.bridge.InvalidEntityException;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.Requires;
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
 * @version 0.2.1
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
  public void createGroup(PSGroup group, PSMember creator) throws APIException {
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
  public void createGroup(PSGroup group, PSMember creator, GroupOptions options) throws APIException {
    if (group == null) throw new NullPointerException("group");
    if (creator == null) throw new NullPointerException("member");
    if (!group.isValid()) throw new InvalidEntityException(PSProject.class, group.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.createGroup(group, creator, options).using(this.session);
    PSGroupHandler handler = new PSGroupHandler(group);
    PSHTTPResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400)
      throw new APIException("Unable to create group '"+group.getName()+"': "+info.getMessage());
    cache.put(group);
  }

  /**
   * Creates the group the specified group in PageSeeder.
   *
   * @deprecated Use {@link #createGroup(PSGroup, PSMember, GroupOptions)} instead
   *
   * @param group   The group to create
   * @param creator The member making the request
   * @param options The group creation options (including group properties)
   *
   * @throws FailedPrecondition   Should a precondition fail to create a group
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If either the group or member is <code>null</code>.
   */
  @Deprecated
  public void createGroup(PSGroup group, GroupOptions options, PSMember creator) throws APIException {
    createGroup(group, creator, options);
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
  public void createProject(PSProject project, PSMember creator) throws APIException {
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
  public void createProject(PSProject project, PSMember creator, GroupOptions options) throws APIException {
    if (project == null) throw new NullPointerException("project");
    if (creator == null) throw new NullPointerException("creator");
    if (!project.isValid()) throw new InvalidEntityException(PSProject.class, project.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.createProject(project, creator, options).using(this.session);
    PSGroupHandler handler = new PSGroupHandler(project);
    PSHTTPResponseInfo info = connector.post(handler);
    if (info.getCode() >= 400)
      throw new APIException("Unable to create project '"+project.getName()+"': "+info.getMessage());
    cache.put(project);
  }

  /**
   * Creates the group the specified group in PageSeeder.
   *
   * @param project The project to create
   * @return The corresponding instance.
   */
  @Deprecated
  public void createProject(PSProject project, GroupOptions options, PSMember creator) throws APIException {
    createProject(project, creator, options);
  }

  /**
   * Creates the group folder.
   *
   * @param group The group where the group folder should be created.
   * @return The corresponding instance.
   */
  public void createGroupFolder(PSGroup group, String url, boolean isPublic) throws APIException {
    if (!group.isValid()) throw new InvalidEntityException(PSGroup.class, group.checkValid());
    PSHTTPConnector connector = PSHTTPConnectors.createGroupFolder(group, url, isPublic).using(this.session);
    PSGroupFolderHandler handler = new PSGroupFolderHandler();
    PSHTTPResponseInfo info = connector.post(handler);
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
   *
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If either the name of the group is <code>null</code>.
   */
  @Requires(minVersion=56000)
  public PSGroup get(String name) throws APIException {
    if (name == null) throw new NullPointerException("name");
    PSGroup group = cache.get(name);
    if (group == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getGroup(name).using(this.session);
      PSGroupHandler handler = new PSGroupHandler();
      connector.get(handler);
      group = handler.getGroup();
      if (group != null)
        cache.put(group);
    }
    return group;
  }

  /**
   * Returns the group for the specified name
   *
   * @param name The name of the group.
   * @return The corresponding instance.
   *
   * @throws FailedPrecondition   If the member is not identifiable
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If either the name of the group or member is <code>null</code>.
   */
  public PSGroup get(String name, PSMember member) throws APIException {
    if (name == null) throw new NullPointerException("name");
    if (member == null) throw new NullPointerException("member");
    PSGroup group = cache.get(name);
    if (group == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getGroup(name, member).using(this.session);
      PSGroupHandler handler = new PSGroupHandler();
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
   * Returns the project for the specified name
   *
   * @param name The name of the project.
   * @return The corresponding instance or <code>null</code>.
   *
   * @throws APIException         If an error occurs while communicating with PageSeeder.
   * @throws NullPointerException If either the name of the group is <code>null</code>.
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
      PSHTTPConnector connector = PSHTTPConnectors.getGroupFolder(group, url).using(this.session);
      PSGroupFolderHandler handler = new PSGroupFolderHandler();
      PSHTTPResponseInfo info = connector.get(handler);
      // TODO We should simply return null
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
    PSHTTPConnector connector = PSHTTPConnectors.addSubGroup(group, subgroup).using(this.session);
    PSHTTPResponseInfo info = connector.post();
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
    PSHTTPConnector connector = PSHTTPConnectors.addSubGroup(group, subgroup, notification, role, listed).using(this.session);
    PSHTTPResponseInfo info = connector.post();
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
    PSHTTPConnector connector = PSHTTPConnectors.putResource(project, resource, overwrite).using(this.session);
    PSHTTPResponseInfo info = connector.post();
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
