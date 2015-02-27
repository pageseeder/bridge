/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.net;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pageseeder.bridge.FailedPrecondition;
import org.pageseeder.bridge.InvalidEntityException;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.Requires;
import org.pageseeder.bridge.model.GroupOptions;
import org.pageseeder.bridge.model.MailOptions;
import org.pageseeder.bridge.model.MemberOptions;
import org.pageseeder.bridge.model.MemberOptions.Invitation;
import org.pageseeder.bridge.model.PSComment;
import org.pageseeder.bridge.model.PSComment.Attachment;
import org.pageseeder.bridge.model.PSComment.Author;
import org.pageseeder.bridge.model.PSComment.Context;
import org.pageseeder.bridge.model.PSDetails;
import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSExternalURI;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMembership;
import org.pageseeder.bridge.model.PSNotification;
import org.pageseeder.bridge.model.PSNotify;
import org.pageseeder.bridge.model.PSPredicate;
import org.pageseeder.bridge.model.PSProject;
import org.pageseeder.bridge.model.PSResource;
import org.pageseeder.bridge.model.PSRole;
import org.pageseeder.bridge.model.PSThreadStatus;
import org.pageseeder.bridge.model.PSURI;
import org.pageseeder.bridge.model.PasswordResetOptions;
import org.pageseeder.bridge.psml.PSMLFragment;
import org.weborganic.berlioz.util.ISO8601;

/**
 * A utility class to provide predefined connectors to PageSeeder via HTTP.
 *
 * <p>This is a low-level class which role is to find the appropriate service to handle an entity and
 * return the corresponding connector. It also sets up the connector to include the necessary parameters.
 *
 * <p>This class does not specify which session should be used to connect to PageSeeder or the
 * HTTP method to use to connect to PageSeeder. This is left to higher level classes.
 *
 * <p>There is generally no reason to invoke this class directly. It is preferable to use the corresponding
 * entity manager which will handle the response, update the internal cache and handle error more consistently.
 *
 * <h3>Exceptions</h3>
 *
 * <p>Methods in this class will try to return a connector only if there is a reasonable chance that
 * the call to PageSeeder will succeed. In cases, it can be known that the call to the underlying service
 * will methods will throw an exception.
 *
 * <p>Unless specified otherwise, most methods will throw a {@link NullPointerException} if the
 * supplied entity is <code>null</code>. Higher-level classes should ensure that no <code>null</code>
 * parameters are passed.
 *
 * <p>Some methods will throw a {@link FailedPrecondition} if a precondition on the entity
 * fails. For example, if a particular entity attribute is required or if a parameter must validate
 * specific criteria.
 *
 * <p>They may also throw an {@link InvalidEntityException} if the supplied entity is not valid. This
 * exception is more likely to be thrown when an entity needs to be created or its details amended in
 * PageSeeder.
 *
 * <h3>PageSeeder compatibility</h3>
 *
 * <p>The {@link Requires} annotation is used to indicate whether a particular version of PageSeeder is
 * required to support that method.
 *
 * @author Christophe Lauret
 * @version 0.3.3
 * @since 0.2.0
 */
public final class PSHTTPConnectors {

  /**
   * Utility class.
   */
  private PSHTTPConnectors() {}

  // Member
  // ----------------------------------------------------------------------------------------------

  /**
   * Reset the session for the current user.
   *
   * @see <a href="https://dev.pageseeder.com/api/web/services/reset-session-POST.html">Reset Session (POST)</a>
   *
   * @return the corresponding connector
   */
  public static PSHTTPConnector resetSession() {
    String service = Services.toResetSession();
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

  /**
   * Returns the connector to retrieve a member from PageSeeder.
   *
   * @param member The member instance to use.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If the member is not identifiable.
   */
  public static PSHTTPConnector getMember(PSMember member) throws FailedPrecondition {
    Preconditions.isIdentifiable(member, "member");
    return getMember(member.getIdentifier());
  }

  /**
   * Returns the connector to retrieve a member from PageSeeder.
   *
   * @param member The username or ID of the member to retrieve.
   *
   * @return The corresponding connector
   */
  public static PSHTTPConnector getMember(String member) {
    String service = Services.toMemberDetails(member);
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

  /**
   * Returns the connector to edit the details of a member.
   *
   * @param member     The member instance containing the new details.
   * @param forceEmail <code>true</code> to force the email address to be changed;
   *                   <code>false</code> to use default behaviour.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If the member is not ientifiable.
   */
  public static PSHTTPConnector editMember(PSMember member, boolean forceEmail)
      throws FailedPrecondition, InvalidEntityException {
    Preconditions.isIdentifiable(member, "member");
    Preconditions.isValid(member, "member");
    String service = Services.toMemberEdit(member.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("member-username", member.getUsername());
    connector.addParameter("firstname", member.getFirstname());
    connector.addParameter("surname", member.getSurname());
    connector.addParameter("email", member.getEmail());
    connector.addParameter("force-email-change", Boolean.toString(forceEmail));
    return connector;
  }

  /**
   * Returns the connector to edit the details of a member.
   *
   * @param member  The member instance containing the new details.
   * @param options The member options
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If the member is not ientifiable.
   */
  public static PSHTTPConnector createMember(PSMember member, MemberOptions options)
      throws FailedPrecondition, InvalidEntityException {
    return createMember(member, options, null);
  }

  /**
   * Returns the connector to edit the details of a member.
   *
   * @param member   The member instance containing the new details.
   * @param options  The member options
   * @param password The member's password (if null, PS will generate it)
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If the member is not ientifiable.
   */
  public static PSHTTPConnector createMember(PSMember member, MemberOptions options, String password)
      throws FailedPrecondition, InvalidEntityException {
    Preconditions.isIdentifiable(member, "member");
    Preconditions.isValid(member, "member");
    String service = Services.toCreateMember();
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("member-username", member.getUsername());
    if (password != null) {
      connector.addParameter("member-password", password);
    }
    connector.addParameter("firstname", member.getFirstname());
    connector.addParameter("surname", member.getSurname());
    connector.addParameter("email", member.getEmail());
    // Member options
    connector.addParameter("welcome-email", Boolean.toString(options.hasWelcomeEmail()));
    connector.addParameter("personal-group", Boolean.toString(options.hasPersonalGroup()));
    connector.addParameter("auto-activate", Boolean.toString(options.isAutoActivate()));
    return connector;
  }

  /**
   * Returns the connector to force the password of a user to be reset (administrators only).
   *
   * @param member The member instance containing the new details.
   *
   * @return the corresponding connector
   */
  public static PSHTTPConnector forceResetPassword(String member) {
    String service = "/members/forceresetpassword";
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    addMemberParameter(connector, member);
    return connector;
  }

  /**
   * Returns the connector to force the password of a user to be reset (administrators only).
   *
   * @param group  The group to use for the email templates.
   * @param member The member instance containing the new details.
   *
   * @return the corresponding connector
   *
   * @throws FailedPrecondition If the group is note identifiable.
   */
  public static PSHTTPConnector forceResetPassword(PSGroup group, String member) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    String service = "/groups/" + group.getKey() + "/members/forceresetpassword";
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    addMemberParameter(connector, member);
    return connector;
  }

  /**
   * Returns the connector to reset the password of a user.
   *
   * @param member The member instance containing the new details.
   *
   * @return the corresponding connector
   */
  public static PSHTTPConnector resetPassword(String member) {
    String service = "/members/resetpassword";
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    addMemberParameter(connector, member);
    return connector;
  }

  /**
   * Returns the connector to reset the password of a user.
   *
   * @param group  The group to use for the email templates.
   * @param member The member instance containing the new details.
   *
   * @return the corresponding connector
   *
   * @throws FailedPrecondition If the group is note identifiable.
   */
  public static PSHTTPConnector resetPassword(PSGroup group, String member) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    String service = "/groups/" + group.getKey() + "/members/resetpassword";
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    addMemberParameter(connector, member);
    return connector;
  }

  /**
   * Returns the connector to confirm a password reset for the specified user with a key.
   *
   * @param member  The username or email of the member.
   * @param options The password reset options, must include the key
   *
   * @return <code>true</code> if the request was successful; <code>false</code> otherwise.
   *
   * @throws FailedPrecondition If the key is not specified.
   */
  public static PSHTTPConnector resetPassword(String member, PasswordResetOptions options) throws FailedPrecondition {
    PSHTTPConnector connector = PSHTTPConnectors.resetPassword(member);
    addPasswordResetParameters(connector, options);
    return connector;
  }

  /**
   * Returns the connector to confirm a password reset for the specified user with a key.
   *
   * @param group   The group to use for the email templates
   * @param member  The username or email of the member.
   * @param options The password reset options, must include the key
   *
   * @return <code>true</code> if the request was successful; <code>false</code> otherwise.
   *
   * @throws FailedPrecondition If the group is not identifiable or the key is not specified.
   */
  public static PSHTTPConnector resetPassword(PSGroup group, String member, PasswordResetOptions options) throws FailedPrecondition {
    PSHTTPConnector connector = PSHTTPConnectors.resetPassword(group, member);
    addPasswordResetParameters(connector, options);
    return connector;
  }

  /**
   * Add the appropriate parameter to identify the member depending on whether it is the username or email.
   *
   * @param connector the connector to add the parameter to
   * @param member username or email
   */
  private static void addMemberParameter(PSHTTPConnector connector, String member) {
    if (member.indexOf('@') > 0) {
      connector.addParameter("email", member);
    } else {
      connector.addParameter("member-username", member);
    }
  }

  /**
   * Add the appropriate parameter to identify the member depending on whether it is the username or email.
   *
   * @param connector the connector to add the parameter to
   * @param options password reset options.
   *
   * @throws FailedPrecondition if the key is not specified.
   */
  private static void addPasswordResetParameters(PSHTTPConnector connector, PasswordResetOptions options)
      throws FailedPrecondition {
    Preconditions.isNotEmpty(options.getKey(), "key");
    connector.addParameter("key", options.getKey());
    if (options.getSignificantDate() != null) {
      connector.addParameter("significant-date", options.getSignificantDateAsString());
    }
    if (options.getPassword() != null) {
      connector.addParameter("member-password", options.getPassword());
    }
  }

  // Thread
  // ----------------------------------------------------------------------------------------------

  /**
   * Add the appropriate parameter to identify the member depending on whether it is the username or email.
   *
   * @param connector the connector to add the parameter to
   * @param options password reset options.
   *
   * @throws FailedPrecondition if the thread ID is missing from the status.
   */
  public static PSHTTPConnector checkThreadProgress(PSThreadStatus status) throws FailedPrecondition {
    Preconditions.isNotEmpty(status.getThreadID(), "threadid");
    String service = Services.toThreadProgress(status.getThreadID());
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

  // Group
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the connector to create a group.
   *
   * <p>The group name must include '-'.
   *
   * @param group    The group to create.
   * @param creator  The user creating the group.
   * @param options  The additional group options.
   *
   * @return the corresponding connector
   *
   * @throws FailedPrecondition If the creator is not identifiable;
   *                            or the group name does not include a dash;
   *                            or the description is <code>null</code> or empty.
   * @throws InvalidEntityException if the group is not valid.
   */
  public static PSHTTPConnector createGroup(PSGroup group, PSMember creator, GroupOptions options)
      throws FailedPrecondition, InvalidEntityException {
    Preconditions.isIdentifiable(creator, "creator");
    Preconditions.isValid(group, "group");
    Preconditions.includesDash(group.getName(), "group name");
    Preconditions.isNotEmpty(group.getDescription(), "group description");
    String project = group.getParentName();
    String shortname = group.getShortName();
    String service = Services.toCreateGroup(creator.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("projectname", project);
    connector.addParameter("shortname", shortname);
    connector.addParameter("description", group.getDescription());
    if (group.getOwner() != null) {
      connector.addParameter("owner", group.getOwner());
    }
    // style owner
    if (group.getTemplate() != null) {
      connector.addParameter("template", group.getTemplate());
    }
    if (group.getDetailsType() != null) {
      connector.addParameter("detailstype", group.getDetailsType());
    }
    if (group.getDefaultRole() != null) {
      connector.addParameter("defaultrole", group.getDefaultRole().toString());
    }
    if (group.getDefaultNotification() != null) {
      connector.addParameter("defaultnotification", group.getDefaultNotification().toString());
    }
    // Group options
    if (options != null) {
      connector.addParameter("addmember", Boolean.toString(options.doAddCreatorAsMember()));
      connector.addParameter("common", Boolean.toString(options.isCommon()));
      connector.addParameter("createdocuments", Boolean.toString(options.doCreateDocuments()));
      if (options.getAccess() != null) {
        connector.addParameter("access", options.getAccess());
      }
      if (options.getCommenting() != null) {
        connector.addParameter("commenting", options.getCommenting());
      }
      if (options.getMessage() != null) {
        connector.addParameter("message", options.getMessage());
      }
      if (options.getModeration() != null) {
        connector.addParameter("moderation", options.getModeration());
      }
      if (options.getRegistration() != null) {
        connector.addParameter("registration", options.getRegistration());
      }
      if (options.getVisibility() != null) {
        connector.addParameter("visibility", options.getVisibility());
      }
      // Group properties
      for (Entry<String, String> property : options.getProperties().entrySet()) {
        connector.addParameter("property." + property.getKey(), property.getValue().toString());
      }
    }

    return connector;
  }

  /**
   * Returns the connector to create a project.
   *
   * @param project The project to create in PageSeeder
   * @param creator The member creating the project.
   *
   * @return the corresponding connector
   *
   * @throws FailedPrecondition If the creator is not identifiable;
   *                            or the description is <code>null</code> or empty.
   * @throws InvalidEntityException if the project is not valid.
   */
  public static PSHTTPConnector createProject(PSProject project, PSMember creator, GroupOptions options)
      throws FailedPrecondition, InvalidEntityException {
    Preconditions.isIdentifiable(creator, "creator");
    Preconditions.isValid(project, "project");
    Preconditions.isNotEmpty(project.getDescription(), "project description");
    String service = Services.toCreateProject(creator.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    String parent = project.getParentName();
    String shortname = project.getShortName();
    if (parent != null) {
      connector.addParameter("projectname", parent);
    } else {
      // we must compute the host URL
      String hosturl = PSConfig.getDefault().getHostURL();
      connector.addParameter("hosturl", hosturl.toString());
    }
    connector.addParameter("shortname", shortname);
    connector.addParameter("description", project.getDescription());
    if (project.getOwner() != null) {
      connector.addParameter("owner", project.getOwner());
    }
    if (project.getDetailsType() != null) {
      connector.addParameter("detailstype", project.getDetailsType());
    }
    // style owner
    if (project.getTemplate() != null) {
      connector.addParameter("template", project.getTemplate());
    }

    // Group options
    if (options != null) {
      connector.addParameter("addmember", Boolean.toString(options.doAddCreatorAsMember()));
      connector.addParameter("common", Boolean.toString(options.isCommon()));
      if (options.getAccess() != null) {
        connector.addParameter("access", options.getAccess());
      }
      if (options.getCommenting() != null) {
        connector.addParameter("commenting", options.getCommenting());
      }
      if (options.getMessage() != null) {
        connector.addParameter("message", options.getMessage());
      }
      if (options.getModeration() != null) {
        connector.addParameter("moderation", options.getModeration());
      }
      if (options.getRegistration() != null) {
        connector.addParameter("registration", options.getRegistration());
      }
      if (options.getVisibility() != null) {
        connector.addParameter("visibility", options.getVisibility());
      }
      // Group properties
      for (Entry<String, String> property : options.getProperties().entrySet()) {
        connector.addParameter("property." + property.getKey(), property.getValue().toString());
      }
    }
    return connector;
  }

  /**
   * A connector to get the details of a group.
   *
   * @param group  The group to retrieve from PageSeeder.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If the group is not identifiable
   */
  @Requires(minVersion = 56000)
  public static PSHTTPConnector getGroup(PSGroup group) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    String service = Services.toGetGroup(group.getIdentifier());
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

  /**
   * A connector to get the details of a group.
   *
   * @param identifier The name or id of the group
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If the group is not identifiable
   */
  @Requires(minVersion = 56000)
  public static PSHTTPConnector getGroup(String identifier) {
    String service = Services.toGetGroup(identifier);
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

  /**
   * Rename an existing group in PageSeeder.
   *
   * @param group   the group
   * @param editor  the author of the rename
   * @param newname the new group name
   *
   * @return the connector
   *
   * @throws FailedPrecondition
   */
  public static PSHTTPConnector renameGroup(PSGroup group, PSMember editor, String newname) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(editor, "editor");
    String service = Services.toRenameGroup(editor.getIdentifier(), group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("name", newname);
    return connector;
  }

  /**
   * Archive an existing group in PageSeeder.
   *
   * @param group   the group
   * @param editor  the author of the archive
   *
   * @return the connector
   *
   * @throws FailedPrecondition
   */
  public static PSHTTPConnector archiveGroup(PSGroup group, PSMember editor) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(editor, "editor");
    String service = Services.toArchiveGroup(editor.getIdentifier(), group.getIdentifier());
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

  /**
   * Edit an existing group in PageSeeder.
   *
   * @param group   the group to edit
   * @param editor  the author of the edit
   *
   * @return the connector
   *
   * @throws FailedPrecondition
   */
  public static PSHTTPConnector editGroup(PSGroup group, PSMember editor, GroupOptions options) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(editor, "editor");
    String service = Services.toEditGroup(editor.getIdentifier(), group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    if (group.getDescription() != null) {
      connector.addParameter("description", group.getDescription());
    }
    if (group.getOwner() != null) {
      connector.addParameter("owner", group.getOwner());
    }
    if (group.getDetailsType() != null) {
      connector.addParameter("detailstype", group.getDetailsType());
    }
    if (group.getDefaultRole() != null) {
      connector.addParameter("defaultrole", group.getDefaultRole().toString());
    }
    if (group.getDefaultNotification() != null) {
      connector.addParameter("defaultnotification", group.getDefaultNotification().toString());
    }
    if (options != null) {
      if (options.getVisibility() != null) {
        connector.addParameter("visibility", options.getVisibility());
      }
      if (options.getCommenting() != null) {
        connector.addParameter("commenting", options.getCommenting());
      }
      if (options.getMessage() != null) {
        connector.addParameter("message", options.getMessage());
      }
      if (options.getRegistration() != null) {
        connector.addParameter("registration", options.getRegistration());
      }
      if (options.getAccess() != null) {
        connector.addParameter("access", options.getAccess());
      }
      if (options.getModeration() != null) {
        connector.addParameter("moderation", options.getModeration());
      }
      Map<String, String> properties = options.getProperties();
      if (properties != null) {
        for (String pname : properties.keySet()) {
          connector.addParameter("property." + pname, properties.get(pname));
        }
      }
    }
    return connector;
  }

  /**
   * Adds a group as a subgroup of another using the default options defined by the target group.
   *
   * @param group    The target group
   * @param subgroup The group to add as a subgroup of the target group.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If either the group or subgroup is not identifiable.
   */
  public static PSHTTPConnector addSubGroup(PSGroup group, PSGroup subgroup) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(subgroup, "subgroup");
    String service = Services.toAddSubGroup(group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("subgroup", subgroup.getIdentifier());
    return connector;
  }

  /**
   * Deletes a subgroup from another group.
   *
   * @param group    The "parent" group
   * @param subgroup The subgroup to remove.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If either the group or subgroup is not identifiable.
   */
  public static PSHTTPConnector deleteSubGroup(PSGroup group, PSGroup subgroup) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(subgroup, "subgroup");
    String service = Services.toRemoveSubGroup(group.getIdentifier(), subgroup.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    return connector;
  }

  /**
   * List the projects for the specified member.
   *
   * @param member  the member
   * @param includeAll if <code>true</code>, all the projects are listed (only for admins)
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If either the member is not identifiable.
   */
  public static PSHTTPConnector findProjects(PSMember member, String prefix, boolean includeAll) throws FailedPrecondition {
    Preconditions.isIdentifiable(member, "member");
    String service = Services.toProjectsFind(member.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    if (prefix != null) {
      connector.addParameter("nameprefix", prefix);
    }
    if (includeAll) {
      connector.addParameter("for", "server");
    }
    return connector;
  }

  /**
   * List the subgroups of the specified group.
   *
   * @param group    The group
   *
   * @return The list of subgroups
   *
   * @throws FailedPrecondition If either the group or subgroup is not identifiable.
   */
  public static PSHTTPConnector listSubGroups(PSGroup group) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    String service = Services.toListSubGroups(group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    return connector;
  }

  /**
   * List the groups.
   *
   * @param member    The Member
   * @param nameprefix The prefix of the group/project
   * @param maximum  The maximum groups return
   * @param showGroup Whether to return groups 
   * @param showAll Whether to return all projects/groups for server (Administrator only)
   *
   * @return Returns the list of projects and groups for the given member.
   *
   * @throws FailedPrecondition If member is not identifiable.
   */
  public static PSHTTPConnector listProjectsTree(PSMember member, String nameprefix, int maximum, boolean showGroup, boolean showAll) throws FailedPrecondition {
    if (maximum < 1) { throw new IllegalArgumentException("maximum less than 1."); }
    Preconditions.isIdentifiable(member, "member");
    String service = Services.toProjectsTree(member.getUsername());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("resultsize", String.valueOf(maximum));
    if (showAll) {
      connector.addParameter("for", "server");
    }
    if (nameprefix != null) {
      connector.addParameter("nameprefix", nameprefix);
    }
    connector.addParameter("groups", showGroup ? "true" : "false");
    return connector;
  }

  /**
   * List the groups.
   *
   * @param member    The Member
   * @param nameprefix The prefix of the group/project
   * @param maximum  The maximum groups return
   * @param showGroup Whether to return groups 
   * @param showAll Whether to return all projects/groups for server (Administrator only)
   * 
   * @return Returns the list of projects and groups for the given member.
   *
   * @throws FailedPrecondition If member is not identifiable.
   */
  public static PSHTTPConnector listProjectsTree(PSMember member, String nameprefix, int maximum, boolean showAll) throws FailedPrecondition {
    return listProjectsTree(member, nameprefix, maximum, true, showAll);
  }

  /**
   * Adds a group as a subgroup of another with additional options.
   *
   * @param group        The target group
   * @param subgroup     The group to add as a subgroup of the target group.
   * @param notification The notification setting for the members of the subgroup
   * @param role         The role for members of the subgroup
   * @param listed       Whether the email address should be listed
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If either the group or subgroup is not identifiable.
   */
  public static PSHTTPConnector addSubGroup(PSGroup group, PSGroup subgroup, PSNotification notification, PSRole role, boolean listed)
      throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(subgroup, "subgroup");
    String service = Services.toAddSubGroup(group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("subgroup", subgroup.getIdentifier());
    if (notification != null) {
      connector.addParameter("notification", notification.parameter());
    }
    if (role != null) {
      connector.addParameter("role", role.parameterMixed());
    }
    connector.addParameter("listed", Boolean.toString(listed));
    return connector;
  }

  /**
   * Puts a resource on the project.
   *
   * @param project   The project receiving the resource
   * @param resource  The resource to put
   * @param overwrite Whether to overwrite the resource
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector putResource(PSProject project, PSResource resource, boolean overwrite)
      throws FailedPrecondition {
    Preconditions.isNotEmpty(resource.getLocation(), "location");
    Preconditions.isIdentifiable(project, "project");
    if (resource.isBinary()) { throw new FailedPrecondition("Only text content resource can be put on the project"); }
    if (resource.getContent() == null) { throw new FailedPrecondition("Resource has no content"); }
    String service = Services.toPutResource(project.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("location", resource.getLocation());
    connector.addParameter("content", resource.getContent());
    connector.addParameter("overwrite", Boolean.toString(overwrite));
    return connector;
  }

  // Membership
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the connector to create a member.
   *
   * @param membership The membership to create.
   * @param password   The user's password.
   * @param options    Member options to create the membership
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector createMembership(PSMembership membership, String password, MemberOptions options)
      throws FailedPrecondition, InvalidEntityException {
    PSGroup group = membership.getGroup();
    PSMember member = membership.getMember();
    Preconditions.isNotNull(group, "group");
    Preconditions.isNotNull(member, "member");
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(member, "member");
    Preconditions.isValid(member, "member");
    Preconditions.isNotEmpty(member.getFirstname(), "firstname");
    Preconditions.isNotEmpty(member.getSurname(), "surname");
    String service = Services.toCreateMembership(group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    // Member details
    connector.addParameter("firstname", member.getFirstname());
    connector.addParameter("surname", member.getSurname());
    if (member.getEmail() != null) {
      connector.addParameter("email", member.getEmail());
    }
    if (member.getUsername() != null) {
      connector.addParameter("member-username", member.getUsername());
    }
    if (password != null) {
      connector.addParameter("member-password", password);
    }
    connector.addParameter("listed", Boolean.toString(membership.isListed()));

    if (membership.getNotification() != null) {
      connector.addParameter("notification", membership.getNotification().parameter());
    }
    if (membership.getRole() != null) {
      connector.addParameter("role", membership.getRole().parameterMixed());
    }

    connector.addParameter("auto-activate", Boolean.toString(options.isAutoActivate()));
    connector.addParameter("welcome-email", Boolean.toString(options.hasWelcomeEmail()));
    connector.addParameter("personal-group", Boolean.toString(options.hasPersonalGroup()));
    if (Invitation.DEFAULT != options.getInvitation()) {
      connector.addParameter("invitation", Boolean.toString(options.getInvitation() == Invitation.YES));
    }

    // Membership details
    PSDetails details = membership.getDetails();
    if (details != null) {
      for (int i = 1; i <= PSDetails.MAX_SIZE; i++) {
        // Fields are 1-based
        String field = details.getField(i);
        if (field != null) {
          connector.addParameter("field" + i, field);
        }
      }
    }

    return connector;
  }

  /**
   * Returns the connector to create a member.
   *
   * @param membership The membership to create.
   * @param password   The user's password.
   * @param delegated  Whether the account is created by the user himself or an admin.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector createMembership(PSMembership membership, String password, boolean delegated)
      throws FailedPrecondition, InvalidEntityException {
    MemberOptions options = new MemberOptions();
    options.setAutoActivate(delegated);
    options.setWelcomeEmail(!delegated);
    options.setInvitation(Invitation.NO);
    return createMembership(membership, password, options);
  }

  /**
   * Returns the connector to save a membership.
   *
   * <p>Implementation: this connector cannot be used to modify the username.
   *
   * @param membership The membership to save.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector editMembership(PSMembership membership, boolean forceEmail) throws FailedPrecondition {
    PSGroup group = membership.getGroup();
    PSMember member = membership.getMember();
    Preconditions.isNotNull(membership.getGroup(), "group");
    Preconditions.isNotNull(membership.getMember(), "member");
    Preconditions.isNotEmpty(member.getFirstname(), "firstname");
    Preconditions.isNotEmpty(member.getSurname(), "surname");
    Preconditions.isNotEmpty(member.getUsername(), "email");

    String url = Services.toEditMembership(group.getIdentifier(), member.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, url);
    // Member details
    connector.addParameter("firstname", member.getFirstname());
    connector.addParameter("surname", member.getSurname());
    if (member.getEmail() != null && member.getEmail().length() > 0) {
      connector.addParameter("email", member.getEmail());
    }
    connector.addParameter("listed", Boolean.toString(membership.isListed()));

    if (membership.getNotification() != null) {
      connector.addParameter("notification", membership.getNotification().parameter());
    }
    if (membership.getRole() != null) {
      connector.addParameter("role", membership.getRole().parameterMixed());
    }

    if (forceEmail) {
      connector.addParameter("force-email-change", "true");
    }

    // Membership details
    PSDetails details = membership.getDetails();
    if (details != null) {
      for (int i = 1; i <= PSDetails.MAX_SIZE; i++) {
        // Fields are 1-based
        String field = details.getField(i);
        if (field != null) {
          connector.addParameter("field" + i, field);
        }
      }
    }

    return connector;
  }

  /**
   * Returns the connector to update the password.
   *
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector updatePassword(PSMembership membership, String password) throws FailedPrecondition {
    PSGroup group = membership.getGroup();
    PSMember member = membership.getMember();
    Preconditions.isNotNull(membership.getGroup(), "group");
    Preconditions.isNotNull(membership.getMember(), "member");
    String service = Services.toEditMembership(group.getIdentifier(), member.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("member-password", password);
    return connector;
  }

  /**
   * Returns the connector to create a member.
   *
   * @param membership The user to create.
   * @param options    The invitation options.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector inviteMembership(PSMembership membership, MemberOptions options) throws FailedPrecondition {
    PSGroup group = membership.getGroup();
    PSMember member = membership.getMember();
    Preconditions.isNotNull(membership.getGroup(), "group");
    Preconditions.isNotNull(membership.getMember(), "member");
    Preconditions.isNotEmpty(member.getEmail(), "email");
    String url = Services.toInviteMember(group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, url);
    // Member details
    connector.addParameter("email", member.getEmail());
    connector.addParameter("listed", Boolean.toString(membership.isListed()));

    if (membership.getNotification() != null) {
      connector.addParameter("notification", membership.getNotification().parameter());
    }
    if (membership.getRole() != null) {
      connector.addParameter("role", membership.getRole().parameterMixed());
    }

    connector.addParameter("welcome-email", Boolean.toString(options.hasWelcomeEmail()));
    if (options.getInvitation() != Invitation.DEFAULT) {
      connector.addParameter("invitation", options.getInvitation() == Invitation.YES ? "true" : "false");
    }

    // Membership details
    PSDetails details = membership.getDetails();
    if (details != null) {
      for (int i = 1; i <= PSDetails.MAX_SIZE; i++) {
        // Fields are 1-based
        String field = details.getField(i);
        if (field != null) {
          connector.addParameter("field" + i, field);
        }
      }
    }

    return connector;
  }

  public static PSHTTPConnector registerMembership(PSMembership membership) throws FailedPrecondition {
    PSGroup group = membership.getGroup();
    PSMember member = membership.getMember();
    Preconditions.isNotNull(group, "group");
    Preconditions.isNotNull(membership.getMember(), "member");
    Preconditions.isNotEmpty(member.getEmail(), "email");
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(member, "member");
    String service = Services.toGroupMemberManage(group.getIdentifier(), member.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    // Member details
    connector.addParameter("email", member.getEmail());
    connector.addParameter("listed", Boolean.toString(membership.isListed()));

    if (membership.getNotification() != null) {
      connector.addParameter("notification", membership.getNotification().parameter());
    }
    if (membership.getRole() != null) {
      connector.addParameter("role", membership.getRole().parameterMixed());
    }
    connector.addParameter("register", "true");

    // Membership details
    PSDetails details = membership.getDetails();
    if (details != null) {
      for (int i = 1; i <= PSDetails.MAX_SIZE; i++) {
        // Fields are 1-based
        String field = details.getField(i);
        if (field != null) {
          connector.addParameter("field" + i, field);
        }
      }
    }

    return connector;
  }

  /**
   * Returns the connector to create a member.
   *
   * @param membership The membership being created from that invitation.
   * @param email      Whether to send an email of not
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector inviteSelf(PSMembership membership, boolean email) throws FailedPrecondition {
    PSGroup group = membership.getGroup();
    PSMember member = membership.getMember();
    Preconditions.isNotNull(membership.getGroup(), "group");
    Preconditions.isNotNull(membership.getMember(), "member");
    Preconditions.isNotEmpty(member.getUsername(), "username");
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(member, "member");
    String service = Services.toInviteSelf(group.getIdentifier(), member.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    // Member details
    connector.addParameter("listed", Boolean.toString(membership.isListed()));
    if (membership.getNotification() != null) {
      connector.addParameter("notification", membership.getNotification().parameter());
    }
    connector.addParameter("welcome-email", Boolean.toString(email));
    // Membership details
    PSDetails details = membership.getDetails();
    if (details != null) {
      for (int i = 1; i <= PSDetails.MAX_SIZE; i++) {
        // Fields are 1-based
        String field = details.getField(i);
        if (field != null) {
          connector.addParameter("field" + i, field);
        }
      }
    }

    return connector;
  }

  /**
   * Returns the connector to get the membership details of a member.
   *
   * @param group  The group name or id.
   * @param member The member name or id.
   *
   * @return The corresponding connector
   */
  public static PSHTTPConnector getMembershipDetails(String group, String member) {
    String service = Services.toMembershipDetails(group, member);
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

  /**
   * Returns the connector to remove a member from a group.
   *
   * @param group  The group name or id.
   * @param member The member name or id.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector deleteMembership(String group, String member) throws FailedPrecondition {
    String service = Services.toDeleteMembership(group, member);
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

  /**
   * A connector to list the memberships for a member.
   *
   * @param member The username or id of the member.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector listMembershipsForMember(String member) throws FailedPrecondition {
    Preconditions.isNotEmpty(member, "member");
    String service = Services.toListMemberships(member);
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

  /**
   * A connector to list the memberships for a group.
   *
   * @param group The name of the group
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector listMembershipsForGroup(String group) throws FailedPrecondition {
    Preconditions.isNotEmpty(group, "group");
    String service = Services.toListAlldetailsMembers(group);
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

  /**
   * A connector to list the memberships for a group.
   *
   * @param group            The name of the group
   * @param includeSubgroups If members from subgroups should be included
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector listMembershipsForGroup(String group, boolean includeSubgroups) throws FailedPrecondition {
    Preconditions.isNotEmpty(group, "group");
    String service = Services.toListAlldetailsMembers(group);
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("subgroups", includeSubgroups ? "true" : "false");
    return connector;
  }

  /**
   * A connector to find the memberships in a group.
   *
   * <p>The name of the group must be specified.
   *
   * <p>The attributes of the membership parameter are used as filters for the search.
   *
   * @param membership The name of the group
   * @param isManager  <code>true</code> if the user making the search has a manager role on the role or is an admin.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector findMembershipsForGroup(PSMembership membership, boolean isManager) throws FailedPrecondition {
    PSGroup group = membership.getGroup();
    Preconditions.isNotNull(group, "group");
    Preconditions.isIdentifiable(group, "group");
    String service = Services.toFindGroupMember(group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    PSMember m = membership.getMember();
    // To get details
    if (isManager) {
      connector.addParameter("role", "manager");
    }
    // Member attributes filter
    if (m != null) {
      if (m.getFirstname() != null) {
        connector.addParameter("firstname", m.getFirstname());
      }
      if (m.getSurname() != null) {
        connector.addParameter("surname", m.getSurname());
      }
      if (m.getEmail() != null) {
        connector.addParameter("email", m.getEmail());
      }
      if (m.getUsername() != null) {
        connector.addParameter("member-username", m.getUsername());
      }
    }
    // Membership attributes filter
    if (membership.getRole() != null) {
      connector.addParameter("member-role", membership.getRole().parameter());
    }
    PSDetails details = membership.getDetails();
    if (details != null) {
      for (int i = 1; i <= PSDetails.MAX_SIZE; i++) {
        if (details.getField(i) != null) {
          connector.addParameter("field" + i, details.getField(i));
        }
      }
    }
    return connector;
  }

  /**
   * Returns the connector to activate a member using an activation key.
   *
   * @param member The username or id of the user to activate.
   * @param key    The user activation key.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector getActivateByKey(String member, String key) throws FailedPrecondition {
    Preconditions.isNotEmpty(member, "member");
    Preconditions.isNotEmpty(key, "key");
    String service = Services.toActivateMember(member);
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("key", key);
    return connector;
  }

  /**
   * Returns the connector to activate a member without an activation key (admin only).
   *
   * @param member The username or id of the user to activate.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector getActivate(String member) throws FailedPrecondition {
    Preconditions.isNotEmpty(member, "member");
    String service = Services.toActivateMember(member);
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    return connector;
  }

  /**
   * Returns the connector to force the password to be reset.
   *
   * @param email The email address of the user
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector getForceResetPassword(String group, String email) throws FailedPrecondition {
    Preconditions.isNotEmpty(group, "group");
    Preconditions.isNotEmpty(email, "email");
    String service = Services.toForceResetPassword(group);
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("email", email);
    return connector;
  }

  // Comments
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the connector to retrieve a comment from PageSeeder.
   *
   * @param comment The ID of the comment to retrieve.
   *
   * @return The corresponding connector
   * @throws FailedPrecondition if conditions fail
   */
  public static PSHTTPConnector getComment(PSMember member, Long comment) throws FailedPrecondition {
    Preconditions.isIdentifiable(member, "member");
    Preconditions.isNotNull(comment, "comment");
    String service = Services.toGetComment(member.getIdentifier(), comment.toString());
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

  /**
   * Returns the connector to find comments from PageSeeder.
   *
   * @param member the author searching for comments
   * @param group  the context group
   * @param title  a comment title (can be null)
   * @param type   a comment type (can be null)
   * @param paths  a list of URI paths (can be null)
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition if conditions fail
   */
  public static PSHTTPConnector getCommentsByFilter(PSMember member, PSGroup group, String title, String type, List<String> paths) throws FailedPrecondition {
    Preconditions.isIdentifiable(member, "member");
    Preconditions.isNotNull(group, "group");
    String service = Services.toGetCommentsByFilter(member.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("groups", group.getName());
    // title
    if (title != null) {
      connector.addParameter("title", title);
    }
    // type
    if (type != null) {
      connector.addParameter("type", type);
    }
    // paths
    if (paths != null) {
      StringBuilder pathsAsString = new StringBuilder();
      for (String p : paths) {
        if (pathsAsString.length() != 0) {
          pathsAsString.append(',');
        }
        pathsAsString.append(p);
      }
      connector.addParameter("paths", pathsAsString.toString());
    }
    return connector;
  }

  /**
   * Create a new comment in PageSeeder.
   *
   * @param comment The comment
   * @param creator The comment's creator (may be different from author)
   * @param notify  Notifications
   * @param groups  The groups the comment is posted on
   *
   * @return
   *
   * @throws FailedPrecondition
   */
  public static PSHTTPConnector createComment(PSComment comment, PSMember creator, PSNotify notify, List<PSGroup> groups) throws FailedPrecondition {
    // The author and context determine the service
    Author author = comment.getAuthor();
    Context context = comment.getContext();

    // Basic preconditions to create a comment
    Preconditions.isNotEmpty(comment.getTitle(), "title");
    Preconditions.isNotEmpty(comment.getContent(), "content");
    Preconditions.isNotNull(author, "author");
    Preconditions.isNotNull(context, "context");
    if (context.group() != null) {
      Preconditions.isNotEmpty(context.group().getName(), "group");
    }
    if (context.uri() != null) {
      Preconditions.isIdentifiable(context.uri(), "uri");
      Preconditions.isNotNull(groups, "group");
      if (groups.isEmpty()) { throw new FailedPrecondition("At least one group must be specified when attaching a comment to a URI"); }
    }

    String service = Services.toCreateCommentService(creator, context);
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);

    // Core parameters
    connector.addParameter("title", comment.getTitle());
    connector.addParameter("content", comment.getContent());
    connector.addParameter("contenttype", comment.getMediaType());

    // Optional parameters
    if (comment.hasLabels()) {
      connector.addParameter("labels", comment.getLabelsAsString());
    }
    if (comment.hasProperties()) {
      connector.addParameter("properties", comment.getPropertiesAsString());
    }
    if (comment.getStatus() != null) {
      connector.addParameter("status", comment.getStatus());
    }
    if (comment.getPriority() != null) {
      connector.addParameter("priority", comment.getPriority());
    }
    if (comment.getAssignedTo() != null) {
      connector.addParameter("assignedto", comment.getAssignedTo().getId().toString());
    }
    if (comment.getDue() != null) {
      connector.addParameter("due", ISO8601.CALENDAR_DATE.format(comment.getDue().getTime()));
    }
    if (comment.getType() != null) {
      connector.addParameter("type", comment.getType());
    }

    // URL must be specified if the context is a URI but we don't know the ID
    if (context.uri() != null && context.uri().getId() == null) {
      connector.addParameter("url", context.uri().getURL());
    }

    // If context is not group
    if (groups != null) {
      StringBuilder p = new StringBuilder();
      for (PSGroup group : groups) {
        if (p.length() > 0) {
          p.append(',');
        }
        Preconditions.isNotEmpty(group.getName(), "group name");
        p.append(group.getName());
      }
      connector.addParameter("groups", p.toString());
    }

    // Author is not a PageSeeder member
    if (author.member() == null) {
      connector.addParameter("authorname", author.name());
      if (author.email() != null) {
        connector.addParameter("authoremail", author.email());
      }
    }

    // Attachments
    if (comment.hasAttachments()) {
      StringBuilder uris = new StringBuilder();
      StringBuilder urls = new StringBuilder();
      for (Attachment attachment : comment.getAttachments()) {
        PSURI uri = attachment.uri();
        Preconditions.isIdentifiable(uri, "uri");
        if (uri.getId() != null) {
          if (uris.length() > 0) {
            uris.append(',');
          }
          uris.append(uri.getId());
          if (attachment.fragment() != null) {
            uris.append('!').append(attachment.fragment());
          }
        } else {
          if (urls.length() > 0) {
            uris.append(',');
          }
          urls.append(uri.getURL());
          if (attachment.fragment() != null) {
            uris.append('#').append(attachment.fragment());
          }
        }
      }
      // Add the parameters
      if (uris.length() > 0) {
        connector.addParameter("uris", uris.toString());
      }
      if (urls.length() > 0) {
        connector.addParameter("urls", urls.toString());
      }
    }

    // Notification
    if (notify != null) {
      connector.addParameter("notify", notify.parameter());
    }

    return connector;
  }

  /**
   * Create a new comment in PageSeeder.
   *
   * @param comment The comment
   * @param notify  Notifications
   * @param groups  The groups the comment is posted on
   *
   * @return
   *
   * @throws FailedPrecondition
   */
  public static PSHTTPConnector replyToComment(PSComment comment, PSNotify notify, List<PSGroup> groups, long xlink) throws FailedPrecondition {
    // The author and context determine the service
    Author author = comment.getAuthor();
    Context context = comment.getContext(); // CONTEXT MAY BE NULL IN A REPLY!

    // Basic preconditions to create a comment
    Preconditions.isNotEmpty(comment.getTitle(), "title");
    Preconditions.isNotEmpty(comment.getContent(), "content");
    Preconditions.isNotNull(author, "author");

    String service = Services.toReplyCommentService(author, xlink, context != null ? context.group() : null);
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);

    // Core parameters
    connector.addParameter("title", comment.getTitle());
    connector.addParameter("content", comment.getContent());
    connector.addParameter("contenttype", comment.getMediaType());

    // Optional parameters
    if (comment.hasLabels()) {
      connector.addParameter("labels", comment.getLabelsAsString());
    }
    if (comment.hasProperties()) {
      connector.addParameter("properties", comment.getPropertiesAsString());
    }
    if (comment.getStatus() != null) {
      connector.addParameter("status", comment.getStatus());
    }
    if (comment.getPriority() != null) {
      connector.addParameter("priority", comment.getPriority());
    }
    if (comment.getAssignedTo() != null) {
      connector.addParameter("assignedto", comment.getAssignedTo().getId().toString());
    }
    if (comment.getDue() != null) {
      connector.addParameter("due", ISO8601.CALENDAR_DATE.format(comment.getDue().getTime()));
    }
    if (comment.getType() != null) {
      connector.addParameter("type", comment.getType());
    }

    // If context is not group
    if (groups != null) {
      StringBuilder p = new StringBuilder();
      for (PSGroup group : groups) {
        if (p.length() > 0) {
          p.append(',');
        }
        Preconditions.isNotEmpty(group.getName(), "group name");
        p.append(group.getName());
      }
      connector.addParameter("groups", p.toString());
    }

    // Author is not a PageSeeder member
    if (author.member() == null) {
      connector.addParameter("authorname", author.name());
      if (author.email() != null) {
        connector.addParameter("authoremail", author.email());
      }
    }

    // Attachments
    if (comment.hasAttachments()) {
      StringBuilder uris = new StringBuilder();
      StringBuilder urls = new StringBuilder();
      for (Attachment attachment : comment.getAttachments()) {
        PSURI uri = attachment.uri();
        Preconditions.isIdentifiable(uri, "uri");
        if (uri.getId() != null) {
          if (uris.length() > 0) {
            uris.append(',');
          }
          uris.append(uri.getId());
          if (attachment.fragment() != null) {
            uris.append('!').append(attachment.fragment());
          }
        } else {
          if (urls.length() > 0) {
            urls.append(',');
          }
          urls.append(uri.getURL());
          if (attachment.fragment() != null) {
            urls.append('#').append(attachment.fragment());
          }
        }
      }
      // Add the parameters
      if (uris.length() > 0) {
        connector.addParameter("uris", uris.toString());
      }
      if (urls.length() > 0) {
        connector.addParameter("urls", urls.toString());
      }
    }

    // Notification
    if (notify != null) {
      connector.addParameter("notify", notify.parameter());
    }

    return connector;
  }

  /**
   * Edit an existing comment in PageSeeder.
   *
   * @param comment The comment
   * @param notify  Notifications
   * @param groups  The groups the comment is posted on
   *
   * @return the connector
   *
   * @throws FailedPrecondition
   */
  public static PSHTTPConnector editComment(PSComment comment, PSMember editor, PSNotify notify, List<PSGroup> groups) throws FailedPrecondition {
    // The author and context determine the service
    Author author = comment.getAuthor();
    Context context = comment.getContext();

    // Basic preconditions to editing a comment
    Preconditions.isIdentifiable(comment, "comment");
    Preconditions.isNotEmpty(comment.getTitle(), "title");
    Preconditions.isNotEmpty(comment.getContent(), "content");
    Preconditions.isNotNull(author, "author");
    Preconditions.isNotNull(context, "context");
    if (context.group() != null) {
      Preconditions.isNotEmpty(context.group().getName(), "group");
    }
    if (context.uri() != null) {
      Preconditions.isIdentifiable(context.uri(), "uri");
      Preconditions.isNotNull(groups, "group");
      if (groups.isEmpty()) { throw new FailedPrecondition("At least one group must be specified when attaching a comment to a URI"); }
    }

    String service = Services.toEditComment(editor.getIdentifier(), comment.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);

    // Core parameters
    connector.addParameter("title", comment.getTitle());
    connector.addParameter("content", comment.getContent());
    connector.addParameter("contenttype", comment.getMediaType());

    // context
    if (context.group() != null) {
      connector.addParameter("group", context.group().getName());
    } else if (context.uri() != null) {
      connector.addParameter("uri", context.uri().getIdentifier());
      if (context.fragment() != null) {
        connector.addParameter("fragment", context.fragment());
      }
    }

    // Optional parameters
    if (comment.hasLabels()) {
      connector.addParameter("labels", comment.getLabelsAsString());
    }
    if (comment.hasProperties()) {
      connector.addParameter("properties", comment.getPropertiesAsString());
    }
    if (comment.getStatus() != null) {
      connector.addParameter("status", comment.getStatus());
    }
    if (comment.getPriority() != null) {
      connector.addParameter("priority", comment.getPriority());
    }
    if (comment.getAssignedTo() != null) {
      connector.addParameter("assignedto", comment.getAssignedTo().getId().toString());
    }
    if (comment.getDue() != null) {
      connector.addParameter("due", ISO8601.CALENDAR_DATE.format(comment.getDue().getTime()));
    }
    if (comment.getType() != null) {
      connector.addParameter("type", comment.getType());
    }

    // URL must be specified if the context is a URI but we don't know the ID
    if (context.uri() != null && context.uri().getId() == null) {
      connector.addParameter("url", context.uri().getURL());
    }

    // If context is not group
    if (groups != null) {
      StringBuilder p = new StringBuilder();
      for (PSGroup group : groups) {
        if (p.length() > 0) {
          p.append(',');
        }
        Preconditions.isNotEmpty(group.getName(), "group name");
        p.append(group.getName());
      }
      connector.addParameter("groups", p.toString());
    }

    // Author is not a PageSeeder member
    if (author.member() == null) {
      connector.addParameter("authorname", author.name());
      if (author.email() != null) {
        connector.addParameter("authoremail", author.email());
      }
    }

    // Attachments
    if (comment.hasAttachments()) {
      StringBuilder urls = new StringBuilder();
      for (Attachment attachment : comment.getAttachments()) {
        PSURI uri = attachment.uri();
        Preconditions.isIdentifiable(uri, "uri");
        if (urls.length() > 0) {
          urls.append(',');
        }
        urls.append(uri.getURL());
        if (attachment.fragment() != null) {
          urls.append('#').append(attachment.fragment());
        }
      }
      // Add the parameters
      if (urls.length() > 0) {
        connector.addParameter("urls", urls.toString());
      }
    }

    // Notification
    if (notify != null) {
      connector.addParameter("notify", notify.parameter());
    }

    return connector;
  }

  /**
   * Archive the specified comment.
   *
   * @param comment The comment to archive.
   * @param member  The member archiving the comment.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition if the member or comment is not identifiable.
   */
  public static PSHTTPConnector archiveComment(PSComment comment, PSMember member) throws FailedPrecondition {
    Preconditions.isIdentifiable(member, "member");
    Preconditions.isIdentifiable(comment, "comment");
    String service = Services.toArchiveComment(member.getIdentifier(), comment.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    return connector;
  }

  /**
   * Archive the specified comment.
   *
   * @param comment The comment to archive.
   * @param member  The member archiving the comment.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition if the member or comment is not identifiable.
   */
  public static PSHTTPConnector unarchiveComment(PSComment comment, PSMember member) throws FailedPrecondition {
    Preconditions.isIdentifiable(member, "member");
    Preconditions.isIdentifiable(comment, "comment");
    String service = Services.toUnarchiveComment(member.getIdentifier(), comment.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    return connector;
  }

  // External URIs
  // ----------------------------------------------------------------------------------------------

  /**
   * Create an external URI.
   *
   * @param externaluri the external URI to create
   * @param group       the group where it should be created
   * @param creator     the member who creates it.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector createExternalURI(PSExternalURI externaluri, PSGroup group, PSMember creator)
      throws FailedPrecondition {
    Preconditions.isNotNull(externaluri, "externaluri");
    Preconditions.isNotEmpty(externaluri.getURL(), "url");
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(creator, "member");
    String service = Services.toCreateExternalURIService(creator.getIdentifier(), group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);

    // document properties
    String url = externaluri.getURL();
    connector.addParameter("url", url);
    connector.addParameter("mediatype", externaluri.getMediaType());
    connector.addParameter("labels", externaluri.getLabelsAsString());
    if (externaluri.getTitle() != null) {
      connector.addParameter("title", externaluri.getTitle());
    }
    if (externaluri.getDocid() != null) {
      connector.addParameter("docid", externaluri.getDocid());
    }
    if (externaluri.getDescription() != null) {
      connector.addParameter("description", externaluri.getDescription());
    }
    if (externaluri.isFolder()) {
      connector.addParameter("folder", "true");
    }
    return connector;
  }

  // Documents
  // ----------------------------------------------------------------------------------------------

  /**
   * Create a document without specifying the folder or group folder.
   *
   * <p>PageSeeder will return an error if the document config does not create the folder.
   *
   * @param document   the document to create
   * @param group      the group where the document should be created
   * @param creator    the member who creates the document.
   * @param parameters the template parameters to supply
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector createDocument(PSDocument document, PSGroup group, PSMember creator, Map<String, String> parameters)
      throws FailedPrecondition {
    Preconditions.isNotNull(document, "document");
    Preconditions.isNotEmpty(document.getFilename(), "filename");
    Preconditions.isNotEmpty(document.getTitle(), "title");
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(creator, "member");
    String service = Services.toGetDocumentForURL(creator.getIdentifier(), group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);

    // document properties
    String url = document.getURL();
    connector.addParameter("url", url);
    connector.addParameter("title", document.getTitle());
    connector.addParameter("type", document.getType());
    connector.addParameter("labels", document.getLabelsAsString());
    if (document.getDocid() != null) {
      connector.addParameter("docid", document.getDocid());
    }
    if (document.getDescription() != null) {
      connector.addParameter("description", document.getDescription());
    }

    // Add the template parameters if specified
    if (parameters != null) {
      for (Entry<String, String> p : parameters.entrySet()) {
        String name = p.getKey();
        String value = p.getValue();
        if (value != null) {
          connector.addParameter("template." + name, value);
        }
      }
    }
    return connector;
  }

  /**
   * Edit a document properties.
   *
   * @param document   the document to edit
   * @param group      the group where the document store.
   * @param creator    the member who edit the document.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector editDocumentProperties(PSDocument document, PSGroup group, PSMember creator) throws FailedPrecondition {
    Preconditions.isNotNull(document, "document");
    Preconditions.isNotEmpty(document.getFilename(), "filename");
    Preconditions.isNotEmpty(document.getTitle(), "title");
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(creator, "member");

    String service = Services.toSaveURIProperties(creator.getIdentifier(), group.getIdentifier(), document.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    // document properties
    if (document.getTitle() != null) {
      connector.addParameter("title", document.getTitle());
    }
    if (document.getLabelsAsString() != null) {
      connector.addParameter("labels", document.getLabelsAsString());
    }
    if (document.getDescription() != null) {
      connector.addParameter("description", document.getDescription());
    }
    if (document.getFilename() != null) {
      connector.addParameter("name", document.getFilename());
    }
    return connector;
  }

  /**
   *
   * @param url
   * @param group
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector getURI(String url, PSGroup group) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isNotEmpty(url, "url");
    String service = Services.toGetURIForURL(group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("url", url);
    return connector;
  }

  /**
   *
   * @param url
   * @param group
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector getURI(long uriid, PSGroup group) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    String service = Services.toGetURIForID(group.getIdentifier(), Long.toString(uriid));
    return new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
  }

  /**
   *
   * @param group
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector listDocumentsInGroup(PSGroup group) throws FailedPrecondition {
    Preconditions.isNotNull(group, "group");
    Preconditions.isNotNull(group.getId(), "group id");
    String servlet = Servlets.DOCUMENT_BROWSER;
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVLET, servlet);
    connector.addParameter("grp", group.getId().toString());
    connector.addParameter("astree", "false");
    connector.addParameter("xformat", "xml");
    return connector;
  }

  /**
   * Returns the connector to create a group folder.
   *
   * @param group    The group where the group folder should be created.
   * @param url      The URL of the group folder.
   * @param isPublic <code>true</code> for a public group folder.
   *
   * @return the corresponding connector
   *
   * @throws FailedPrecondition If the group is not identifiable or if the URL is empty.
   */
  public static PSHTTPConnector createGroupFolder(PSGroup group, String url, boolean isPublic) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isNotEmpty(url, "url");
    String service = Services.toCreateGroupFolder(group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("url", url);
    if (isPublic) {
      connector.addParameter("public", "true");
    }
    return connector;
  }

  /**
   * Returns the connector to get a group folder from its URL.
   *
   * @param group    The group the group folder belongs to.
   * @param url      The URL of the group folder.
   *
   * @return the corresponding connector
   *
   * @throws FailedPrecondition If the group is not identifiable or if the URL is empty.
   */
  public static PSHTTPConnector getGroupFolder(PSGroup group, String url) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isNotEmpty(url, "url");
    String service = Services.toGetGroupFolderForURL(group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("url", url);
    return connector;
  }

  /**
   * Returns the connector to get a particular fragment.
   *
   * @param document The document the fragment belong to
   * @param group    The group the document belongs to
   * @param editor   The member wanting to view/edit the fragment
   * @param fragment The ID of the fragment to retrieve.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If the document, group or member is not identifiable or if the fragment ID is empty.
   */
  public static PSHTTPConnector getFragment(PSDocument document, PSGroup group, PSMember editor, String fragment)
      throws FailedPrecondition {
    Preconditions.isIdentifiable(document, "document");
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(editor, "member");
    Preconditions.isNotEmpty(fragment, "fragment");
    String service = Services.toGetFragment(editor.getIdentifier(), group.getIdentifier(), Long.toString(document.getId()), fragment);
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    return connector;
  }

  /**
   * Returns the connector to update a particular fragment.
   *
   * @param document The document the fragment belong to
   * @param group    The group the document belongs to
   * @param editor   The member wanting to view/edit the fragment
   * @param fragment The fragment to update.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If the document, group or member is not identifiable;
   *                            or if the fragment is <code>null</code>;
   *                            or if the fragment ID is <code>null</code> or empty.
   */
  public static PSHTTPConnector putFragment(PSDocument document, PSGroup group, PSMember editor, PSMLFragment fragment)
      throws FailedPrecondition {
    Preconditions.isIdentifiable(document, "document");
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isIdentifiable(editor, "member");
    Preconditions.isNotNull(fragment, "fragment");
    Preconditions.isNotEmpty(fragment.id(), "fragment id");
    String service = Services.toGetFragment(editor.getIdentifier(), group.getIdentifier(), Long.toString(document.getId()), fragment.id());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("content", fragment.toPSML());
    return connector;
  }

  /**
   * Returns the connector to make a search on the specified group.
   *
   * @param predicate The predicate for the search
   * @param group     The group to search.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If the group name if not specified.
   */
  public static PSHTTPConnector find(PSPredicate predicate, PSGroup group) throws FailedPrecondition {
    Preconditions.isNotEmpty(group.getName(), "group name");
    String servlet = Servlets.GENERIC_SEARCH;
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVLET, servlet);
    if (predicate != null) {
      Map<String, String> parameters = predicate.toParameters();
      for (Entry<String, String> p : parameters.entrySet()) {
        connector.addParameter(p.getKey(), p.getValue());
      }
    }
    if (group instanceof PSProject) {
      connector.addParameter("project", group.getName());
    } else {
      connector.addParameter("groups", group.getName());
    }
    connector.addParameter("xformat", "xml");
    return connector;
  }

  /**
   * Returns the connector to make a search on the specified group.
   *
   * @param predicate The predicate for the search
   * @param groups    The groups to search.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If the group name if not specified.
   */
  public static PSHTTPConnector find(PSPredicate predicate, List<PSGroup> groups) throws FailedPrecondition {
    if (groups.isEmpty()) { throw new FailedPrecondition("At one group must be specified"); }
    String servlet = Servlets.GENERIC_SEARCH;
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVLET, servlet);
    if (predicate != null) {
      Map<String, String> parameters = predicate.toParameters();
      for (Entry<String, String> p : parameters.entrySet()) {
        connector.addParameter(p.getKey(), p.getValue());
      }
    }
    // Construct the list of groups
    StringBuilder parameter = new StringBuilder();
    for (PSGroup group : groups) {
      if (parameter.length() > 0) {
        parameter.append(',');
      }
      parameter.append(group.getName());
    }
    connector.addParameter("groups", parameter.toString());
    connector.addParameter("xformat", "xml");
    return connector;
  }

  /**
   * Returns the connector to send an email.
   *
   * @param member  The member on behalf of which the email is sent
   * @param group   The groups to search.
   * @param options The email options.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition If the group or member is not identifiable;
   *                            or if the email options do not include any content.
   */
  public static PSHTTPConnector sendMail(PSMember member, PSGroup group, MailOptions options) throws FailedPrecondition {
    Preconditions.isIdentifiable(member, "member");
    Preconditions.isIdentifiable(group, "group");
    Preconditions.isNotEmpty(options.getContent(), "mail content");
    String service = Services.toSendMail(member.getIdentifier(), group.getIdentifier());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("content", options.getContent());
    connector.addParameter("notify", options.getNotify().toString());
    String template = options.getTemplate().template();
    // The service is mapped to 'name' but the generator uses 'template'
    connector.addParameter("template", template);
    connector.addParameter("name", template);
    if (options.hasRecipients()) {
      connector.addParameter("recipients", options.getRecipientsAsString());
    }
    if (options.hasAttachments()) {
      connector.addParameter("attachments", options.getAttachmentsAsString());
    }
    return connector;
  }
}
