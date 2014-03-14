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
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.Requires;
import org.pageseeder.bridge.control.GroupManager.GroupOptions;
import org.pageseeder.bridge.model.PSDetails;
import org.pageseeder.bridge.model.PSDocument;
import org.pageseeder.bridge.model.PSFolder;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSGroupFolder;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMembership;
import org.pageseeder.bridge.model.PSNotification;
import org.pageseeder.bridge.model.PSPredicate;
import org.pageseeder.bridge.model.PSProject;
import org.pageseeder.bridge.model.PSResource;
import org.pageseeder.bridge.model.PSRole;
import org.pageseeder.bridge.psml.PSMLFragment;

/**
 * A utility class to provide predefined PageSeeder connectors.
 *
 * @author Christophe Lauret
 * @version 0.2.1
 * @since 0.2.0
 */
public final class PSHTTPConnectors {

  /**
   * Utility class.
   */
  private PSHTTPConnectors() {
  }

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
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/resetsession");
    return connector;
  }

  // Group
  // ----------------------------------------------------------------------------------------------

  /**
   * The group name must include '-'.
   *
   * @param group      The group to create.
   * @param creator    The user creating the group.
   * @param options    The additional group options.
   *
   * @return the corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector createGroup(PSGroup group, PSMember creator, GroupOptions options) throws FailedPrecondition {
    Preconditions.isNotEmpty(group.getName(), "group name");
    Preconditions.includesDash(group.getName(), "group name");
    Preconditions.isNotNull(group.getDescription(), "group description");
    Preconditions.isIdentifiable(creator, "creator");
    String name = group.getName();
    int dash = name.lastIndexOf('-');
    String project = name.substring(0, dash);
    String shortname = name.substring(dash+1);
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/members/"+creator.getIdentifier()+"/groups/create");
    connector.addParameter("projectname", project);
    connector.addParameter("shortname",   shortname);
    connector.addParameter("description", group.getDescription());
    if (group.getOwner() != null)
      connector.addParameter("owner",  group.getOwner());
    if (group.getDetailsType() != null)
      connector.addParameter("detailstype",  group.getDetailsType());
    if (group.getDefaultRole() != null)
      connector.addParameter("defaultrole",  group.getDefaultRole().toString());
    if (group.getDefaultNotification() != null)
      connector.addParameter("defaultnotification", group.getDefaultNotification().toString());
    // Group options
    if (options != null) {
      connector.addParameter("addmember",  Boolean.toString(options.isAddmember()));
      connector.addParameter("common",  Boolean.toString(options.isCommon()));
      connector.addParameter("createdocuments",  Boolean.toString(options.isCreatedocuments()));
      if (options.getAccess() != null)
        connector.addParameter("access",  options.getAccess());
      if (options.getCommenting() != null)
        connector.addParameter("commenting", options.getCommenting());
      if (options.getMessage() != null)
        connector.addParameter("message", options.getMessage());
      if (options.getModeration() != null)
        connector.addParameter("moderation", options.getModeration());
      if (options.getRegistration() != null)
        connector.addParameter("registration", options.getRegistration());
      if (options.getVisibility() != null)
        connector.addParameter("visibility", options.getVisibility());
      // Group properties
      for (Entry<String, String> property : options.getProperties().entrySet()) {
        connector.addParameter("property."+property.getKey(), property.getValue().toString());
      }
    }

    return connector;
  }

  /**
   *
   * @param project
   * @param creator
   *
   * @return the corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector createProject(PSProject project, PSMember creator, GroupOptions options) throws FailedPrecondition {
    Preconditions.isNotEmpty(project.getName(), "project name");
    Preconditions.isNotNull(project.getDescription(), "project description");
    Preconditions.isNotEmpty(project.getDescription(), "project description");
    Preconditions.isIdentifiable(creator, "creator");
    String url = "/members/"+creator.getIdentifier()+"/projects/create";
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, url);
    String name = project.getName();
    int dash = name.lastIndexOf('-');
    String parent = dash >= 0? name.substring(0, dash) : null;
    String shortname = dash >= 0? name.substring(dash+1) : name;
    if (parent != null) {
      connector.addParameter("projectname", parent);
    } else {
      // we must compute the host URL
      String hosturl = PSConfig.singleton().getHostURL();
      connector.addParameter("hosturl",   hosturl.toString());
    }
    connector.addParameter("shortname",   shortname);
    connector.addParameter("description", project.getDescription());
    if (project.getOwner() != null)
      connector.addParameter("owner",  project.getOwner());
    if (project.getDetailsType() != null)
      connector.addParameter("detailstype",  project.getDetailsType());
    // Group options
    if (options != null) {
      connector.addParameter("addmember",  Boolean.toString(options.isAddmember()));
      connector.addParameter("common",  Boolean.toString(options.isCommon()));
      if (options.getAccess() != null)
        connector.addParameter("access",  options.getAccess());
      if (options.getCommenting() != null)
        connector.addParameter("commenting", options.getCommenting());
      if (options.getMessage() != null)
        connector.addParameter("message", options.getMessage());
      if (options.getModeration() != null)
        connector.addParameter("moderation", options.getModeration());
      if (options.getRegistration() != null)
        connector.addParameter("registration", options.getRegistration());
      if (options.getVisibility() != null)
        connector.addParameter("visibility", options.getVisibility());
      // Group properties
      for (Entry<String, String> property : options.getProperties().entrySet()) {
        connector.addParameter("property."+property.getKey(), property.getValue().toString());
      }
    }
    return connector;
  }

  /**
   * A connector to get the details of a group.
   *
   * @param identifier The name or id of the group
   * @param member     The member that will access the group
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector getGroup(String identifier, PSMember member) throws FailedPrecondition {
    Preconditions.isIdentifiable(member, "member");
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/members/"+member.getIdentifier()+"/groups/"+identifier);
    return connector;
  }

  /**
   * A connector to get the details of a group.
   *
   * @param group  The group to retrieve from PageSeeder.
   *
   * @return The corresponding connector
   *
   * @throws If the group is not identifiable
   */
  @Requires(minVersion=56000)
  public static PSHTTPConnector getGroup(PSGroup group) throws FailedPrecondition {
    Preconditions.isIdentifiable(group, "group");
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/groups/"+group.getIdentifier());
    return connector;
  }

  /**
   * A connector to get the details of a group.
   *
   * @param identifier The name or id of the group
   *
   * @return The corresponding connector
   *
   * @throws If the group is not identifiable
   */
  @Requires(minVersion=56000)
  public static PSHTTPConnector getGroup(String identifier) throws FailedPrecondition {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/groups/"+identifier);
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
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/groups/"+group.getIdentifier()+"/subgroups/add");
    connector.addParameter("subgroup", subgroup.getIdentifier());
    return connector;
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
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/groups/"+group.getIdentifier()+"/subgroups/add");
    connector.addParameter("subgroup", subgroup.getIdentifier());
    if (notification != null)
      connector.addParameter("notification", notification.parameter());
    if (role != null)
      connector.addParameter("role", role.parameter());
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
  public static PSHTTPConnector putResource(PSProject project, PSResource resource, boolean overwrite) throws FailedPrecondition {
    Preconditions.isNotEmpty(resource.getLocation(), "location");
    Preconditions.isIdentifiable(project, "project");
    if (resource.isBinary()) throw new FailedPrecondition("Only text content resource can be put on the project");
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/groups/"+project+"/resources/put");
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
   * @param user      The user to create.
   * @param password  The user's password.
   * @param delegated Whether the account is created by the user himself or an admin.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector createMembership(PSMembership membership, String password, boolean delegated) throws FailedPrecondition {
    PSGroup group = membership.getGroup();
    PSMember member = membership.getMember();
    Preconditions.isNotNull(membership.getGroup(), "group");
    Preconditions.isNotNull(membership.getMember(), "member");
    Preconditions.isNotEmpty(member.getFirstname(), "firstname");
    Preconditions.isNotEmpty(member.getSurname(), "surname");
    Preconditions.isNotEmpty(member.getEmail(), "email");
    String url = PSHTTPServices.toCreateMembership(group.getName());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, url);
    // Member details
    connector.addParameter("firstname", member.getFirstname());
    connector.addParameter("surname", member.getSurname());
    connector.addParameter("email", member.getEmail());
    if (member.getUsername() != null)
      connector.addParameter("member-username", member.getUsername());
    if (password != null)
      connector.addParameter("member-password", password);
    connector.addParameter("listed", Boolean.toString(membership.isListed()));

    if (membership.getNotification() != null)
      connector.addParameter("notification", membership.getNotification().parameter());
    if (membership.getRole() != null)
      connector.addParameter("role", membership.getRole().parameter());

    connector.addParameter("auto-activate", delegated ? "true" : "false");
    connector.addParameter("welcome-email", delegated ? "false": "true");
    connector.addParameter("invitation", "false");

    // Membership details
    PSDetails details = membership.getDetails();
    if (details != null) {
      for (int i=1; i <= PSDetails.MAX_SIZE; i++) {
        // Fields are 1-based
        String field = details.getField(i);
        if (field != null)
          connector.addParameter("field"+i, field);
      }
    }

    return connector;
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

    String url = PSHTTPServices.toEditMembership(group.getName(), member.getUsername());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, url);
    // Member details
    connector.addParameter("firstname", member.getFirstname());
    connector.addParameter("surname", member.getSurname());
    if (member.getEmail() != null && member.getEmail().length() > 0)
      connector.addParameter("email", member.getEmail());
    connector.addParameter("listed", Boolean.toString(membership.isListed()));

    if (membership.getNotification() != null)
      connector.addParameter("notification", membership.getNotification().parameter());
    if (membership.getRole() != null)
      connector.addParameter("role", membership.getRole().parameter());

    if (forceEmail)
      connector.addParameter("force-email-change", "true");

    // Membership details
    PSDetails details = membership.getDetails();
    if (details != null) {
      for (int i=1; i <= PSDetails.MAX_SIZE; i++) {
        // Fields are 1-based
        String field = details.getField(i);
        if (field != null)
          connector.addParameter("field"+i, field);
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
    String url = PSHTTPServices.toEditMembership(group.getName(), member.getUsername());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, url);
    connector.addParameter("member-password", password);
    return connector;
  }

  /**
   * Returns the connector to create a member.
   *
   * @param user      The user to create.
   * @param password  The user's password.
   * @param delegated Whether the account is created by the user himself or an admin.
   * @param admin     A PageSeeder admin to invoke the service.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector inviteMembership(PSMembership membership, boolean delegated) throws FailedPrecondition {
    PSGroup group = membership.getGroup();
    PSMember member = membership.getMember();
    Preconditions.isNotNull(membership.getGroup(), "group");
    Preconditions.isNotNull(membership.getMember(), "member");
    Preconditions.isNotEmpty(member.getEmail(), "email");
    String url = PSHTTPServices.toInviteMember(group.getName());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, url);
    // Member details
    connector.addParameter("email", member.getEmail());
    connector.addParameter("listed", Boolean.toString(membership.isListed()));

    if (membership.getNotification() != null)
      connector.addParameter("notification", membership.getNotification().parameter());
    if (membership.getRole() != null)
      connector.addParameter("role", membership.getRole().parameter());

    connector.addParameter("auto-activate", delegated ? "true" : "false");
    connector.addParameter("welcome-email", delegated ? "false": "true");
    connector.addParameter("invitation", "false");

    // Membership details
    PSDetails details = membership.getDetails();
    if (details != null) {
      for (int i=1; i <= PSDetails.MAX_SIZE; i++) {
        // Fields are 1-based
        String field = details.getField(i);
        if (field != null)
          connector.addParameter("field"+i, field);
      }
    }

    return connector;
  }


  public static PSHTTPConnector registerMembership(PSMembership membership) throws FailedPrecondition {
    PSGroup group = membership.getGroup();
    PSMember member = membership.getMember();
    Preconditions.isNotNull(membership.getGroup(), "group");
    Preconditions.isNotNull(membership.getMember(), "member");
    Preconditions.isNotEmpty(member.getEmail(), "email");
    String url = "/groups/"+group.getName()+"/members/"+member.getUsername()+"/manage";
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, url);
    // Member details
    connector.addParameter("email", member.getEmail());
    connector.addParameter("listed", Boolean.toString(membership.isListed()));

    if (membership.getNotification() != null)
      connector.addParameter("notification", membership.getNotification().parameter());
    if (membership.getRole() != null)
      connector.addParameter("role", membership.getRole().parameter());
    connector.addParameter("register", "true");

    // Membership details
    PSDetails details = membership.getDetails();
    if (details != null) {
      for (int i=1; i <= PSDetails.MAX_SIZE; i++) {
        // Fields are 1-based
        String field = details.getField(i);
        if (field != null)
          connector.addParameter("field"+i, field);
      }
    }

    return connector;
  }
  /**
   * Returns the connector to create a member.
   *
   * @param user      The user to create.
   * @param password  The user's password.
   * @param delegated Whether the account is created by the user himself or an admin.
   * @param admin     A PageSeeder admin to invoke the service.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector inviteSelf(PSMembership membership, boolean delegated) throws FailedPrecondition {
    PSGroup group = membership.getGroup();
    PSMember member = membership.getMember();
    Preconditions.isNotNull(membership.getGroup(), "group");
    Preconditions.isNotNull(membership.getMember(), "member");
    Preconditions.isNotEmpty(member.getUsername(), "username");
    String url = PSHTTPServices.toInviteSelf(group.getName(), member.getUsername());
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, url);
    // Member details
    connector.addParameter("listed", Boolean.toString(membership.isListed()));
    if (membership.getNotification() != null)
      connector.addParameter("notification", membership.getNotification().parameter());
    connector.addParameter("welcome-email", delegated ? "false": "true");
    // Membership details
    PSDetails details = membership.getDetails();
    if (details != null) {
      for (int i=1; i <= PSDetails.MAX_SIZE; i++) {
        // Fields are 1-based
        String field = details.getField(i);
        if (field != null)
          connector.addParameter("field"+i, field);
      }
    }

    return connector;
  }

  /**
   * Returns the connector to activate a member using an activation key.
   *
   * @param username The username of the user details to retrieve.
   * @param user     The PS user making the request
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector getMember(String username) throws FailedPrecondition {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/members/" + username + "/details");
    return connector;
  }


  /**
   * Returns the connector to activate a member using an activation key.
   *
   * @param username The username of the user details to retrieve.
   * @param user     The PS user making the request
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector editMember(PSMember member, boolean forceEmail) throws FailedPrecondition {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/members/" + member.getUsername() + "/edit");
    connector.addParameter("member-username", member.getUsername());
    connector.addParameter("firstname", member.getFirstname());
    connector.addParameter("surname", member.getSurname());
    connector.addParameter("email", member.getEmail());
    connector.addParameter("force-email-change", Boolean.toString(forceEmail));
    return connector;
  }

  /**
   * Returns the connector to activate a member using an activation key.
   *
   * @param username The username of the user details to retrieve.
   * @param user     The PS user making the request
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector getMembershipDetails(String group, String username) throws FailedPrecondition {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, PSHTTPServices.toMembershipDetails(group, username));
    return connector;
  }

  /**
   * Returns the connector to activate a member using an activation key.
   *
   * @param username The username of the user details to retrieve.
   * @param user     The PS user making the request
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector deleteMembership(String group, String username) throws FailedPrecondition {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/groups/"+group+"/members/"+username+"/delete");
    return connector;
  }

  /**
   * A connector to list the memberships for a member.
   *
   * @param username The username to add.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector listMembershipsForMember(String username) throws FailedPrecondition {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, PSHTTPServices.toListMemberships(username));
    return connector;
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
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/groups/"+group+"/members");
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
    Preconditions.isNotEmpty(group.getName(), "group");
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/groups/"+group.getName()+"/members/find");
    PSMember m = membership.getMember();
    // To get details
    if (isManager) connector.addParameter("role", "manager");
    // Member attributes filter
    if (m != null) {
      if (m.getFirstname() != null) connector.addParameter("firstname",       m.getFirstname());
      if (m.getSurname()   != null) connector.addParameter("surname",         m.getSurname());
      if (m.getEmail()     != null) connector.addParameter("email",           m.getEmail());
      if (m.getUsername()  != null) connector.addParameter("member-username", m.getUsername());
    }
    // Membership attributes filter
    if (membership.getRole() != null) connector.addParameter("member-role", membership.getRole().parameter());
    PSDetails details = membership.getDetails();
    if (details != null) {
      for (int i=1; i <= PSDetails.MAX_SIZE; i++) {
        if (details.getField(i) != null) connector.addParameter("field"+i, details.getField(i));
      }
    }
    return connector;
  }

  /**
   * Returns the connector to activate a member using an activation key.
   *
   * @param username The username of the user to activate.
   * @param key      The user activation key.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector getActivateByKey(String username, String key) throws FailedPrecondition {
    Preconditions.isNotEmpty(username, "username");
    Preconditions.isNotEmpty(key, "key");
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, PSHTTPServices.toActivateMember(username));
    connector.addParameter("key", key);
    return connector;
  }

  /**
   * Returns the connector to force the password to be reset.
   *
   * @param email The email address of the user
   * @param admin A user with administrator privileges
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector getForceResetPassword(String group, String email) throws FailedPrecondition {
    Preconditions.isNotEmpty(group, "group");
    Preconditions.isNotEmpty(email, "email");
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, PSHTTPServices.toForceResetPassword(group));
    connector.addParameter("email", email);
    return connector;
  }

  // Documents
  // ----------------------------------------------------------------------------------------------

  /**
   * Create a document without specifying the folder or group folder.
   *
   * <p>PageSeeder will return an error if the document config does not create the folder.
   *
   * @param document the document to create
   * @param group    the group where the document should be created
   * @param creator  the member who creates the document.
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
    Preconditions.isNotNull(group, "group");
    Preconditions.isNotEmpty(group.getName(), "group name");
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/members/"+creator.getId()+"/groups/"+group.getId()+"/uris");

    // document properties
    connector.addParameter("filename", document.getFilename());
    connector.addParameter("title", document.getTitle());
    connector.addParameter("groups", group.getName());
    connector.addParameter("type", document.getType());
    connector.addParameter("labels", document.getLabelsAsString());
    if (document.getDocid() != null)
      connector.addParameter("documentid", document.getDocid());
    if (document.getDescription() != null)
      connector.addParameter("description", document.getDescription());

    // Add the template parameters if specified
    if (parameters != null) {
      for (Entry<String, String> p : parameters.entrySet()) {
        String name = p.getKey();
        String value = p.getValue();
        if (value != null) {
          connector.addParameter("template."+name, value);
        }
      }
    }
    return connector;
  }

  /**
   *
   * @param document the document to create
   * @param group    the group where the document should be created
   * @param creator  the member who creates the document.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector createDocument(PSDocument document, PSGroup group, PSGroupFolder folder, PSMember creator, Map<String, String> parameters)
      throws FailedPrecondition {
    Preconditions.isNotNull(group, "folder");
    Preconditions.isNotNull(folder.getId(), "folder");
    PSHTTPConnector connector = createDocument(document, group, creator, parameters);
    connector.addParameter("guri", folder.getId().toString());
    return connector;
  }

  /**
   *
   * @param document the document to create
   * @param group    the group where the document should be created
   * @param creator  the member who creates the document.
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector createDocument(PSDocument document, PSGroup group, PSFolder folder, PSMember creator, Map<String, String> parameters)
      throws FailedPrecondition {
    Preconditions.isNotNull(folder, "folder");
    Preconditions.isNotNull(folder.getId(), "folder");
    PSHTTPConnector connector = createDocument(document, group, creator, parameters);
    connector.addParameter("uri", folder.getId().toString());
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
  public static PSHTTPConnector getURI(String url, PSGroup group) throws FailedPrecondition  {
    Preconditions.isNotNull(group, "group");
    Preconditions.isNotNull(group.getId(), "group id");
    Preconditions.isNotEmpty(url, "url");
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/groups/"+group.getId()+"/uris/forurl");
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
  public static PSHTTPConnector getURI(long uriid, PSGroup group) throws FailedPrecondition  {
    Preconditions.isNotNull(group, "group");
    Preconditions.isNotNull(group.getId(), "group id");
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/groups/"+group.getId()+"/uris/"+uriid);
    return connector;
  }

  /**
   *
   * @param groupId
   *
   * @return The corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector listDocumentsInGroup(PSGroup group) throws FailedPrecondition {
    Preconditions.isNotNull(group, "group");
    Preconditions.isNotNull(group.getId(), "group id");
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVLET, "com.pageseeder.review.DocumentBrowser");
    connector.addParameter("grp",       group.getId().toString());
    connector.addParameter("astree",    "false");
    connector.addParameter("xformat",   "xml");
    return connector;
  }

  /**
   *
   *
   * @return the corresponding connector
   *
   * @throws FailedPrecondition Should any precondition fail.
   */
  public static PSHTTPConnector createGroupFolder(PSGroup group, String url, boolean isPublic) throws FailedPrecondition {
    Preconditions.isNotEmpty(group.getName(), "group name");
    Preconditions.isNotEmpty(url, "url");
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/groups/"+group.getName()+"/folders/create");
    connector.addParameter("url", url);
    if (isPublic)
      connector.addParameter("public", "true");
    return connector;
  }

  /**
  *
  *
  * @return the corresponding connector
  *
  * @throws FailedPrecondition Should any precondition fail.
  */
 public static PSHTTPConnector getGroupFolder(PSGroup group, String url) throws FailedPrecondition {
   Preconditions.isNotEmpty(group.getName(), "group name");
   Preconditions.isNotEmpty(url, "url");
   PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, "/groups/"+group.getName()+"/folders/forurl");
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
  public static PSHTTPConnector getFragment(PSDocument document, PSGroup group, PSMember editor, String fragment)
      throws FailedPrecondition  {
    Preconditions.isNotNull(document, "document");
    Preconditions.isNotNull(group, "group");
    Preconditions.isNotNull(group.getId(), "group id");
    String service = "/members/"+editor.getId()+"/groups/"+group.getId()+"/uris/"+document.getId()+"/fragments/"+fragment;
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
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
  public static PSHTTPConnector putFragment(PSDocument document, PSGroup group, PSMember editor, PSMLFragment fragment)
      throws FailedPrecondition  {
    Preconditions.isNotNull(document, "document");
    Preconditions.isNotNull(group, "group");
    Preconditions.isNotNull(group.getId(), "group id");
    String service = "/members/"+editor.getId()+"/groups/"+group.getId()+"/uris/"+document.getId()+"/fragments/"+fragment.id();
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVICE, service);
    connector.addParameter("content", fragment.toPSML());
    connector.addParameter("http-request-method", "PUT");
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
  public static PSHTTPConnector find(PSPredicate predicate, PSGroup group) throws FailedPrecondition {
    PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVLET, "com.pageseeder.search.GenericSearch");
    if (predicate != null) {
      Map<String, String> parameters = predicate.toParameters();
      for (Entry<String, String> p : parameters.entrySet()) {
        connector.addParameter(p.getKey(), p.getValue());
      }
    }
    connector.addParameter("groups", group.getName());
    connector.addParameter("xformat", "xml");
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
 public static PSHTTPConnector find(PSPredicate predicate, List<PSGroup> groups) throws FailedPrecondition {
   PSHTTPConnector connector = new PSHTTPConnector(PSHTTPResourceType.SERVLET, "com.pageseeder.search.GenericSearch");
   if (predicate != null) {
     Map<String, String> parameters = predicate.toParameters();
     for (Entry<String, String> p : parameters.entrySet()) {
       connector.addParameter(p.getKey(), p.getValue());
     }
   }
   // Construct the list of groups
   StringBuilder parameter = new StringBuilder();
   for (PSGroup group : groups) {
     if (parameter.length() > 0) parameter.append(',');
     parameter.append(group.getName());
   }
   connector.addParameter("groups", parameter.toString());
   connector.addParameter("xformat", "xml");
   return connector;
 }
}
