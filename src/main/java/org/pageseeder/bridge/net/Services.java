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
package org.pageseeder.bridge.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.pageseeder.bridge.Requires;
import org.pageseeder.bridge.model.PSComment.Author;
import org.pageseeder.bridge.model.PSComment.Context;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSURI;

/**
 * A low-level utility class to generate the URL for PageSeeder services.
 *
 * <p>Provides useful constants for PageSeeder services.
 *
 * <p>As a convention, most parameters in this class are strings that correspond to the
 * identifier for the entity been searched, for example:
 * <ul>
 *   <li><b>group</b> - group name of ID
 *   <li><b>member</b> - member username or ID
 * </ul>
 *
 * <p>The order of the parameters is generally the order in which they appear in the URL
 * of the underlying service.
 *
 * <p>There is generally no reason to access this class directly, higher-level class should be accessed instead.
 *
 * @author Christophe Lauret
 * @version 0.2.23
 * @since 0.2.0
 */
public final class Services {

  // Note: methods are declared in the order they are declared in the "services.xml" file on PageSeeder

  /**
   * Utility class.
   */
  private Services() {}

  /**
   * If member is not a number then add "~" prefix and URL encode it.
   *
   * @param member  the member id or username
   *
   * @return the member id or prefixed username
   */
  public static String prefixMember(String member) {
    try {
      Long.parseLong(member);
    } catch (NumberFormatException ex1) {
      try {
        member = "~" + URLEncoder.encode(member, "UTF-8").replace("+", "%20");
      } catch (UnsupportedEncodingException ex) {
        // shouldn't happen
        ex.printStackTrace();
      }
    }
    return member;
  }

  /**
   * If group is not a number then add "~" prefix.
   *
   * @param group  the group id or name
   *
   * @return the group id or prefixed name
   */
  public static String prefixGroup(String group) {
    try {
      Long.parseLong(group);
    } catch (NumberFormatException ex1) {
      group = "~" + group;
    }
    return group;
  }

  // General Services
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the URL to get the PageSeeder version.
   *
   * @return <code>/version</code>.
   */
  public static String toVersion() {
    return "/version";
  }

  /**
   * Returns the URL toe get information about PageSeeder.
   *
   * @return <code>/about</code>.
   */
  public static String toAbout() {
    return "/about";
  }

  /**
   * Returns the URL to report an error.
   *
   * @return <code>/error/report</code>.
   */
  public static String toReportError() {
    return "/error/report";
  }

  // Member Services
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the URL to create a member.
   *
   * <p>To create a group member, use {@link #toCreateMembership(String)} instead.
   *
   * @return <code>/members</code>.
   */
  public static String toCreateMember() {
    return "/members";
  }

  /**
   * Returns the URL to self-register a member.
   *
   * @return <code>/members/register</code>.
   */
  public static String toRegisterMember() {
    return "/members/register";
  }

  /**
   * Returns the URL to create a group member.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/members</code>.
   */
  public static String toCreateMembership(String group) {
    return "/groups/" + prefixGroup(group) + "/members";
  }

  /**
   * Returns the URL to remove a member from a group.
   *
   * @deprecated Use {@link #toMembership(String, String)} with DELETE
   *
   * @param group  the group name or id
   * @param member the member username or id
   *
   * @return <code>/groups/[group]/members/[member]/delete</code>.
   */
  @Deprecated
  public static String toDeleteMembership(String group, String member) {
    return "/groups/" + prefixGroup(group) + "/members/" + prefixMember(member) + "/delete";
  }

  /**
   * Returns the URL to invite a member to a group.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/members/invite</code>.
   */
  public static String toInviteMember(String group) {
    return "/groups/" + prefixGroup(group) + "/members/invite";
  }

  /**
   * Returns the URL for a member to ivite himself to a group.
   *
   * @param group  the group name or id
   * @param member the member username or id
   *
   * @return <code>/groups/[group]/members/[member]/inviteself</code>.
   */
  public static String toInviteSelf(String group) {
    return "/groups/" + prefixGroup(group) + "/members/inviteself";
  }

  /**
   * Returns the URL for a member to ivite himself to a group.
   *
   * @deprecated Use {@link #toInviteSelf(String)} instead
   *
   * @param group  the group name or id
   * @param member the member username or id
   *
   * @return <code>/groups/[group]/members/[member]/inviteself</code>.
   */
  @Deprecated
  public static String toInviteSelf(String group, String member) {
    return "/groups/" + prefixGroup(group) + "/members/inviteself";
  }

  /**
   * Returns the URL to create a member's personal group.
   *
   * <p>The personal group is the home group on the personal project.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[group]/creategroup</code>.
   */
  public static String toCreate(String member) {
    return "/members/" + prefixMember(member) + "/creategroup";
  }

  /**
   * Returns the URL to reset the current session.
   *
   * @return <code>/resetsession</code>.
   */
  public static String toResetSession() {
    return "/resetsession";
  }

  /**
   * Returns the URL to activate a member.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/activate</code>.
   */
  protected static String toActivateMember(String member) {
    return "/members/" + prefixMember(member) + "/activate";
  }

  /**
   * Returns the URL to unlock a member (Administrator only).
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/unlock</code>.
   */
  protected static String toUnlockMember(String member) {
    return "/members/" + prefixMember(member) + "/unlock";
  }

  /**
   * Returns the URL to edit the details of a member.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/edit</code>.
   */
  protected static String toMember(String member) {
    return "/members/" + prefixMember(member);
  }

  /**
   * Returns the URL to edit the details of a member.
   *
   * @deprecated Use {@link #toMember(String)} with PATCH
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/edit</code>.
   */
  @Deprecated
  protected static String toMemberEdit(String member) {
    return "/members/" + prefixMember(member) + "/edit";
  }

  /**
   * Returns the URL for a member to decline the invitation to a group.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/declineinvitation</code>.
   */
  protected static String toDeclineInvitation(String member) {
    return "/members/" + prefixMember(member) + "/declineinvitation";
  }

  /**
   * Returns the URL to return the groups visible to a member.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/visiblegroups</code>.
   */
  protected static String toVisibleGroups(String member) {
    return "/members/" + prefixMember(member) + "/visiblegroups";
  }

  /**
   * Returns the URL to list the memberships of a user.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/memberships</code>.
   */
  protected static String toListMemberships(String member) {
    return "/members/" + prefixMember(member) + "/memberships";
  }

  /**
   * Returns the URL to return the details of a member.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]</code>.
   */
  public static String toMemberDetails(String member) {
    return "/members/" + prefixMember(member);
  }

  /**
   * Returns the URL to reset the password of the current user.
   *
   * <p>To use the email templates for a specific group, use {@link #toResetPassword(String)} instead.
   *
   * @return <code>/members/resetpassword</code>.
   */
  public static String toResetPassword() {
    return "/members/resetpassword";
  }

  /**
   * Returns the URL to reset the password of the current user.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/members/resetpassword</code>.
   */
  public static String toResetPassword(String group) {
    return "/groups/" + prefixGroup(group) + "/members/resetpassword";
  }

  /**
   * Returns the URL to reset the password of the current user.
   *
   * <p>To use the email templates for a specific group, use {@link #toForceResetPassword(String)} instead.
   *
   * @return <code>/members/resetpassword</code>.
   */
  public static String toForceResetPassword() {
    return "/members/forceresetpassword";
  }

  /**
   * Returns the URL to force the password to be reset.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/members/forceresetpassword</code>.
   */
  public static String toForceResetPassword(String group) {
    return "/groups/" + prefixGroup(group) + "/members/forceresetpassword";
  }

  /**
   * Returns the URL to check the password strength.
   *
   * @return <code>/password/meter</code>.
   */
  public static String toPasswordMeter() {
    return "/password/meter";
  }

  // Subgroup Services
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the URL to return the list of labels in a group.
   *
   * @param group the group name or id
   *
   * @return <code>/groups/[group]/labels</code>.
   */
  public static String toListLabels(String group) {
    return "/groups/" + prefixGroup(group) + "/labels";
  }

  /**
   * Returns the URL to list the subgroups of a group.
   *
   * @param group the group name or id.
   *
   * @return <code>/groups/[group]/subgroups</code>.
   */
  public static String toListSubGroups(String group) {
    return "/groups/" + prefixGroup(group) + "/subgroups";
  }

  /**
   * Returns the URL to add a subgroup.
   *
   * @param group the group name or id the subgroup is added to.
   *
   * @return <code>/groups/[group]/subgroups/add</code>.
   */
  public static String toAddSubGroup(String group) {
    return "/groups/" + prefixGroup(group) + "/subgroups/add";
  }

  /**
   * Returns the URL to a subgroup.
   *
   * @param group the group name or id.
   * @param sub   the name or id of the subgroup
   *
   * @return <code>/groups/[group]/subgroups/[sub]</code>.
   */
  public static String toSubGroup(String group, String sub) {
    return "/groups/" + prefixGroup(group) + "/subgroups/" + sub;
  }

  /**
   * Returns the URL to edit a subgroup.
   *
   * @deprecated Use {@link #toSubGroup(String, String)} with PATCH
   *
   * @param group the group name or id.
   * @param sub   the name or id of the subgroup
   *
   * @return <code>/groups/[group]/subgroups/[sub]/edit</code>.
   */
  @Deprecated
  public static String toEditSubGroup(String group, String sub) {
    return "/groups/" + prefixGroup(group) + "/subgroups/" + sub + "/edit";
  }

  /**
   * Returns the URL to remove a subgroup from a group.
   *
   * @param group the group name or id.
   * @param sub   the name or id of the subgroup
   *
   * @return <code>/groups/[group]/subgroups/[sub]/remove</code>.
   */
  public static String toRemoveSubGroup(String group, String sub) {
    return "/groups/" + prefixGroup(group) + "/subgroups/" + sub + "/remove";
  }

  // Email Services
  // ----------------------------------------------------------------------------------------------

  // /members/{member:member}/groups/{group:group}/mail/preview

  /**
   * Returns the URL to send an email.
   *
   * @param member the username or id of the member on behalf of whom the mail is sent
   * @param group  the group name or id, the member belongs to.
   *
   * @return <code>/members/[member]/groups/[group]/mail/send</code>
   */
  public static String toSendMail(String member, String group) {
    return "/members/" + prefixMember(member) + "/groups/" + prefixGroup(group) + "/mail/send";
  }

  // Projects Services
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the URL to create a project.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/projects</code>.
   */
  public static String toCreateProject(String member) {
    return "/members/" + prefixMember(member) + "/projects";
  }

  /**
   * Returns the URL to a project.
   *
   * @param member  the member username or id
   * @param project the project name or id
   *
   * @return <code>/members/[member]/projects/[project]</code>.
   */
  public static String toProject(String member, String project) {
    return "/members/" + prefixMember(member) + "/projects/" + project;
  }

  /**
   * Returns the URL to edit a project.
   *
   * @deprecated Use {@link #toProject(String, String)} with PATCH
   *
   * @param member  the member username or id
   * @param project the project name or id
   *
   * @return <code>/members/[member]/projects/[project]/edit</code>.
   */
  @Deprecated
  public static String toEditProject(String member, String project) {
    return "/members/" + prefixMember(member) + "/projects/" + project + "/edit";
  }

  /**
   * Returns the URL to get a project.
   *
   * @param project the project name or id
   *
   * @return <code>/members/[member]/projects/[project]</code>.
   */
  public static String toGetProject(String project) {
    return "/projects/" + project;
  }

  /**
   * Returns the URL to get a project.
   *
   * @deprecated Use {@link #toGetProject(String)}
   *
   * @param member  the member username or id
   * @param project the project name or id
   *
   * @return <code>/members/[member]/projects/[project]</code>.
   */
  @Deprecated
  public static String toGetProject(String member, String project) {
    return "/projects/" + project;
  }

  /**
   * Returns the URL to list the subprojects of a project.
   *
   * @param member  the member username or id
   * @param project the project name or id
   *
   * @return <code>/members/[member]/projects/[project]/subprojectlist</code>.
   */
  public static String toListSubProjects(String member, String project) {
    return "/members/" + prefixMember(member) + "/projects/" + project + "/subprojectlist";
  }

  /**
   * Returns the URL to list the top-level projects a member belongs to.
   *
   * @param member  the member username or id
   *
   * @return <code>/members/[member]/projectlist</code>.
   */
  public static String toListProjects(String member) {
    return "/members/" + prefixMember(member) + "/projectlist";
  }

  /**
   * Returns the URL to return the results for a projects/groups search.
   *
   * @param member  the member username or id
   *
   * @return <code>/members/[member]/projects/find</code>.
   */
  public static String toProjectsFind(String member) {
    return "/members/" + prefixMember(member) + "/projects/find";
  }

  /**
   * Returns the URL to return the sub-tree of projects a member belongs to.
   *
   * @deprecated Use {@link #toListSubProjects(String, String)} or {@link #toSubProjectsTree(String, String)} instead
   *
   * @param member  the member username or id
   * @param project the project name or id
   *
   * @return <code>/members/[member]/projects/[project]/subprojects</code>.
   */
  @Deprecated
  public static String toSubProjects(String member, String project) {
    return "/members/" + prefixMember(member) + "/projects/" + project + "/subprojects";
  }

  /**
   * Returns the URL to return the tree of projects a member belongs to.
   *
   * @param member  the member username or id
   *
   * @return <code>/members/[member]/projects</code>.
   */
  public static String toProjectsTree(String member) {
    return "/members/" + prefixMember(member) + "/projecttree";
  }

  /**
   * Returns the URL to return the sub-tree of projects a member belongs to.
   *
   * @param member  the member username or id
   * @param project the project name or id
   *
   * @return <code>/members/[member]/projects/[project]/subprojecttree</code>.
   */
  public static String toSubProjectsTree(String member, String project) {
    return "/members/" + prefixMember(member) + "/projects/" + project + "/subprojecttree";
  }

  /**
   * Returns the URL to archive a project.
   *
   * @param member  the member username or id
   * @param project the project name or id
   *
   * @return <code>/members/[member]/projects/[project]/archive</code>.
   */
  public static String toArchiveProject(String member, String project) {
    return "/members/" + prefixMember(member) + "/projects/" + project + "/archive";
  }

  /**
   * Returns the URL to rename a project.
   *
   * @param member  the member username or id
   * @param project the project name or id
   *
   * @return <code>/members/[member]/projects/[project]/rename</code>.
   */
  public static String toRenameProject(String member, String project) {
    return "/members/" + prefixMember(member) + "/projects/" + project + "/rename";
  }

  /**
   * Returns the URL to unarchive a project.
   *
   * @param member  the member username or id
   * @param project the project name or id
   *
   * @return <code>/members/[member]/projects/[project]/unarchive</code>.
   */
  public static String toUnarchiveProject(String member, String project) {
    return "/members/" + prefixMember(member) + "/projects/" + project + "/unarchive";
  }

  // Group Folder (Group URI) Services
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the URL to list the group folders for a group.
   *
   * @param group the group name or id
   *
   * @return <code>/groups/[group]/groupfolders</code>.
   */
  @Requires(minVersion = 56012)
  public static String toListGroupFolders(String group) {
    return "/groups/" + prefixGroup(group) + "/groupfolders";
  }

  /**
   * Returns the URL to create a new group folder in a group.
   *
   * @param group the group name or id
   *
   * @return <code>/groups/[group]/groupfolders/create</code>.
   */
  @Requires(minVersion = 56012)
  public static String toCreateGroupFolder2(String group) {
    return "/groups/" + prefixGroup(group) + "/groupfolders/create";
  }

  /**
   * Returns the URL to get a group folder for a given url.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/folders/forurl</code>.
   */
  @Requires(minVersion = 56012)
  public static String toGetGroupFolderForURL2(String group) {
    return "/groups/" + prefixGroup(group) + "/groupfolders/forurl";
  }

  /**
   * Returns the URL to create a group folder.
   *
   * @deprecated Use {@link #toCreateGroupFolder2(String)} instead
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/folders/create</code>.
   */
  @Deprecated
  public static String toCreateGroupFolder(String group) {
    return "/groups/" + prefixGroup(group) + "/folders/create";
  }

  /**
   * Returns the URL to get a group folder for a given url.
   *
   * @deprecated Use {@link #toGetGroupFolderForURL2(String)} instead
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/folders/forurl</code>.
   */
  @Deprecated
  public static String toGetGroupFolderForURL(String group) {
    return "/groups/" + prefixGroup(group) + "/folders/forurl";
  }

  // Group Services
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the URL to create a group.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/groups</code>.
   */
  public static String toCreateGroup(String member) {
    return "/members/" + prefixMember(member) + "/groups";
  }

  /**
   * Returns the URL to a group.
   *
   * @param member the member name or id
   * @param group  the group name or id
   *
   * @return <code>/members/[member]/groups/[group]</code>.
   */
  public static String toGroup(String member, String group) {
    return "/members/" + prefixMember(member) + "/groups/" + prefixGroup(group);
  }

  /**
   * Returns the URL to edit a group.
   *
   * @deprecated Use {@link #toGroup(String, String)} with PATCH
   *
   * @param member the member name or id
   * @param group  the group name or id
   *
   * @return <code>/members/[member]/groups/[group]</code>.
   */
  @Deprecated
  public static String toEditGroup(String member, String group) {
    return "/members/" + prefixMember(member) + "/groups/" + prefixGroup(group) + "/edit";
  }

  /**
   * Returns the URL to get a group.
   *
   * @param group the group name or id
   *
   * @return <code>/groups/[group]</code>.
   */
  public static String toGetGroup(String group) {
    return "/groups/" + prefixGroup(group);
  }

  /**
   * Returns the URL to get a group.
   *
   * @deprecated Use {@link #toGetGroup(String)}
   *
   * @param member the member name or id
   * @param group  the group name or id
   *
   * @return <code>/members/[member]/groups/[group]</code>.
   */
  @Deprecated
  public static String toGetGroup(String member, String group) {
    return "/groups/" + prefixGroup(group);
  }

  // /groups/{group:group}/size

  // /members/{member:member}/groups/{group:group}/publish/start

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/publish/start

  // /groups/{group:group}/publish/check

  // /groups/{group:group}/publish/cancel

  /**
   * Returns the URL to list the members of a group.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/members</code>.
   */
  public static String toListMembers(String group) {
    return "/groups/" + prefixGroup(group) + "/members";
  }

  /**
   * Returns the URL to invoke the group member search service.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/members/find</code>.
   */
  public static String toFindGroupMember(String group) {
    return "/groups/" + prefixGroup(group) + "/members/find";
  }

  /**
   * Returns the URL to list the members of a group including all their details.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/members</code>.
   */
  public static String toListAlldetailsMembers(String group) {
    return "/groups/" + prefixGroup(group) + "/members/alldetails";
  }

  /**
   * Returns the URL to invoke the group member details service.
   *
   * @param group  the group name or id
   * @param member the member username or id
   *
   * @return <code>/groups/[groupname]/members/[username]</code>.
   */
  public static String toMembershipDetails(String group, String member) {
    return "/groups/" + prefixGroup(group) + "/members/" + prefixMember(member);
  }

  /**
   * Returns the URL to invoke the group member edit service.
   *
   * @param group  the group name or id
   * @param member the member username or id
   *
   * @return <code>/groups/[group]/members/[member]</code>.
   */
  public static String toMembership(String group, String member) {
    return "/groups/" + prefixGroup(group) + "/members/" + prefixMember(member);
  }

  /**
   * Returns the URL to invoke the group member edit service.
   *
   * @deprecated Use {@link # toMembership(String, String)} with PATCH
   *
   * @param group  the group name or id
   * @param member the member username or id
   *
   * @return <code>/groups/[group]/members/[member]/edit</code>.
   */
  @Deprecated
  public static String toEditMembership(String group, String member) {
    return "/groups/" + prefixGroup(group) + "/members/" + prefixMember(member) + "/edit";
  }

  /**
   * Returns the URL to deregister a member from a group.
   *
   * @param group  the group name or id
   * @param member the member username or id
   *
   * @return <code>/groups/[group]/members/[member]/deregister</code>.
   */
  public static String toDeregisterMember(String group, String member) {
    return "/groups/" + prefixGroup(group) + "/members/" + prefixMember(member) + "/deregister";
  }

  /**
   * Returns the URL to invoke the group member registration service.
   *
   * @param group  the group name or id
   * @param member the member username or id
   *
   * @return <code>/groups/[group]/members/[member]/manage</code>.
   */
  public static String toGroupMemberManage(String group, String member) {
    return "/groups/" + prefixGroup(group) + "/members/" + prefixMember(member) + "/manage";
  }

  // /groups/{group:group}/autocomplete/{field}

  // /groups/{group:group}/autocomplete

  // /groups/{group:group}/autosuggest

  // /groups/{group:group}/omnibox

  // /groups/{group:group}/autosuggest/fields

  // /groups/{group:group}/facet

  // /groups/{group:group}/publishconfig

  // /groups/{group:group}/error/report

  // /members/{member:member}/groups/{group:group}/applyshare

  // /members/{member:member}/groups/{group:group}/resolvexrefs

  // /members/{member:member}/groups/{group:group}/converttopsml

  // /groups/{group:group}/images/find

  // /members/{member:member}/groups/{group:group}/index/start

  // /groups/{group:group}/index/status

  // /groups/{group:group}/index/clear

  /**
   * Returns the URL to archive a group.
   *
   * @param member  the member username or id
   * @param group   the group to archive
   *
   * @return <code>/members/[member]/groups/[groups]/archive</code>.
   */
  public static String toArchiveGroup(String member, String group) {
    return "/members/" + prefixMember(member) + "/groups/" + prefixGroup(group) + "/archive";
  }

  /**
   * Returns the URL to rename a group.
   *
   * @param member  the member username or id
   * @param group   the project's old name or id
   *
   * @return <code>/members/[member]/groups/[groups]/rename</code>.
   */
  public static String toRenameGroup(String member, String group) {
    return "/members/" + prefixMember(member) + "/groups/" + prefixGroup(group) + "/rename";
  }

  // /members/{member:member}/groups/{group:group}/unarchive

  // URI Services
  // ----------------------------------------------------------------------------------------------

  // /groups/{group:group}/uris/forurl/uris

  // /groups/{group:group}/uris/{uri:uri}/uris

  // /groups/{group:group}/externaluris/hosts

  // /groups/{group:group}/hosts/{host}/externaluris

  // /groups/{group:group}/externaluris/forurl/externaluris

  // /groups/{group:group}/externaluris/{uri:uri}/externaluris

  /**
   * Returns the URL to list URIs for a given URL.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/uris/forurl/uris</code>.
   */
  public static String tolistURIsForURL(String group) {
    return "/groups/" + prefixGroup(group) + "/uris/forurl/uris";
  }

  /**
   * Returns the URL to get a URI for a given URL.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/uris/forurl</code>.
   */
  public static String toGetURIForURL(String group) {
    return "/groups/" + prefixGroup(group) + "/uris/forurl";
  }

  /**
   * Returns the URL to get a URI for a given ID.
   *
   * @param group  the group name or id
   * @param uri    the URI id.
   *
   * @return <code>/groups/[group]/uris/[uri]</code>.
   */
  public static String toGetURIForID(String group, String uri) {
    return "/groups/" + prefixGroup(group) + "/uris/" + uri;
  }

  /**
   * Returns the URL to create an external URI.
   *
   * @param member the member id
   * @param group  the group name or id
   *
   * @return <code>/members/[member]/groups/[group]/uris/[uri]</code>.
   */
  public static String toCreateExternalURIService(String member, String group) {
    return "/members/" + prefixMember(member) + "/groups/" + prefixGroup(group) + "/externaluris";
  }

  /**
   * Returns the URL to list XRefs.
   *
   * @param group        the group name or id
   * @param uriid        the URI id
   *
   * @return <code>/groups/[group]/uris/[uri]/xrefs</code>.
   */
  public static String toListXRefs(String group, Long uriid) {
    return "/groups/" + prefixGroup(group) + "/uris/" + uriid + "/xrefs";
  }

  // /members/{member:member}/groups/{group:group}/externaluris/{uri:uri}

  // /members/{member:member}/groups/{group:group}/externaluris/{uri:uri}/archive

  // /members/{member:member}/groups/{group:group}/externaluris/{uri:uri}/unarchive

  // /groups/{group:group}/uris/{uri:uri}/versions

  // /groups/{group:group}/uris/{uri:uri}/xrefs

  // /groups/{group:group}/uris/{uri:uri}/xreftree

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/versions

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/versions/{versionid}/archive

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/xrefs

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/xrefs/{xrefid}/archive

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/resolvexrefs

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/index/start

  // /groups/{group:group}/uris/{uri:uri}/history

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/properties

  // /members/{member:member}/groups/{group:group}/documents

  /**
   * Returns the URL to create a document for a given URL.
   *
   * @param member the member username or id
   * @param group  the group name or id
   *
   * @return <code>/members/[member]/groups/[group]/documents/forurl</code>.
   */
  public static String toCreateDocumentForURL(String member, String group) {
    return "/members/" + prefixMember(member) + "/groups/" + prefixGroup(group) + "/documents/forurl";
  }

  /**
   * Returns the URL to edit the URI.
   *
   * @param member the member username or id
   * @param group  the group name or id
   * @param uri    the id of uri
   *
   * @return <code>/members/[member]/groups/[group]/uris/{uri} </code>.
   */
  public static String toEditURI(String member, String group, String uri) {
    return "/members/" + prefixMember(member) + "/groups/" + prefixGroup(group) + "/uris/" + uri;
  }

  /**
   * Returns the URL to saves the core properties of a URI.
   *
   * @deprecated Use {@link # toEditURI(String, String, String)} with PATCH
   *
   * @param member the member username or id
   * @param group  the group name or id
   * @param uri    the id of uri
   *
   * @return <code>/members/[member]/groups/[group]/uris/{uri}/properties </code>.
   */
  @Deprecated
  public static String toSaveURIProperties(String member, String group, String uri) {
    return "/members/" + prefixMember(member) + "/groups/" + prefixGroup(group) + "/uris/" + uri + "/properties";
  }

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/documents

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/duplicate

  // /members/{member:member}/groups/{group:group}/folders

  // /members/{member:member}/groups/{group:group}/folders/forurl

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/folders

  // /groups/{group:group}/uris/{uri:uri}/schemas

  // /groups/{group:group}/uris/{uri:uri}/index

  // /groups/{group:group}/uris/{uri:uri}/publishconfig

  // /groups/{group:group}/uris/publishconfig

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/editinlinelabel

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/editpsmlproperty

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/fragments

  /**
   * Returns the URL to get/put a fragment from a document.
   *
   * @param member   the username or id of the editor
   * @param group    the group name or id, the document belongs to.
   * @param uri      the uri ID of the document
   * @param fragment the fragment ID to retrieve
   *
   * @return <code>/members/[member]/groups/[group]/uris/[uri]/fragments/[fragment]</code>
   */
  public static String toGetFragment(String member, String group, String uri, String fragment) {
    return "/members/" + prefixMember(member) + "/groups/" + prefixGroup(group) + "/uris/" + uri + "/fragments/" + fragment;
  }

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/fragments/{fragment}/revert

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/edits/{editid}/share

  // /groups/{group:group}/uris/{uri:uri}/config

  // /groups/{group:group}/uris/{uri:uri}/drafts

  // /groups/{group:group}/uris/{uri:uri}/fragments/{fragment}/drafts

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/move

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/archive

  // /members/{member:member}/groups/{group:group}/uris/{uri:uri}/foldervalidate

  // /members/{member:member}/uris/{uri:uri}/export

  // /members/{member:member}/export

  // Host Services
  // ----------------------------------------------------------------------------------------------

  // /members/{member:member}/hosts/{host}/update

  // Task Services
  // ----------------------------------------------------------------------------------------------

  // /members/{member:member}/tasks/find

  // /tasks/find

  // /members/{member:member}/tasks/{task}

  // /tasks/{task}

  // Comment and Discussions Services
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the URL to get the list of discussions on the group.
   *
   * @param group  the group name or ID
   *
   * @return <code>/members/[userid]/comments/[xlinkid]/reply</code>.
   */
  public static String toDiscussionsForGroup(String group) {
    return "/groups/" + prefixGroup(group) + "/discussions";
  }

  // /groups/{group:group}/discussions/{discussion}

  // /members/{member:member}/discussions/forurl

  // /members/{member:member}/discussions/{discussion}

  /**
   * Returns the URL to reply to a comment.
   *
   * @param member the username or ID of the member
   * @param uri    the uri ID
   *
   * @return <code>/members/[userid]/comments/[xlinkid]/reply</code>.
   */
  public static String toDiscussionsForURI(String member, String uri) {
    return "/members/" + prefixMember(member) + "/uris/" + uri + "/discussions";
  }

  /**
   * Returns the URL to reply to a comment.
   *
   * @param member the username or ID of the member
   * @param uri    the uri ID
   * @param fragment    the fragment ID
   *
   * @return <code>/members/[userid]/comments/[xlinkid]/reply</code>.
   */
  public static String toDiscussionsForFragment(String member, String uri, String fragment) {
    return "/members/" + prefixMember(member) + "/uris/" + uri + "/fragments/" + fragment + "/discussions";
  }

  /**
   * Returns the URL to load a comment.
   *
   * @param member  the username or ID of the member
   * @param comment the comment ID
   *
   * @return <code>/members/[userid]/comments/[xlinkid]</code>.
   */
  public static String toGetComment(String member, String comment) {
    return "/members/" + prefixMember(member) + "/comments/" + comment;
  }

  // /discussions/forurl

  // /discussions/{discussion}

  /**
   * Returns the URL to find comments.
   *
   * @return <code>/member/[userid]/comments/find</code>.
   */
  public static String toFindComments(String member) {
    return "/members/" + prefixMember(member) + "/comments/find";
  }

  // /uris/{uri:uri}/discussions

  // /uris/{uri:uri}/fragments/{fragment}/discussions

  // /members/{member:member}/groups/{group:group}/comments

  // /groups/{group:group}/comments

  // /members/{member:member}/groups/{group:group}/comments/{xlinkid}/reply

  // /groups/{group:group}/comments/{xlinkid}/reply

  // /members/{member:member}/comments/{xlinkid}/reply

  /**
   * Returns the URL to reply to a comment.
   *
   * @param member the username or ID of the member
   * @param xlink  the xlink id of the comment to reply to
   *
   * @return <code>/members/[userid]/comments/[xlinkid]/reply</code>.
   */
  protected static String toReplyComment(String member, String xlink) {
    return "/members/" + prefixMember(member) + "/comments/" + xlink + "/reply";
  }

  // /members/{member:member}/comments/forurl

  // /members/{member:member}/uris/{uri:uri}/comments

  // /members/{member:member}/uris/{uri:uri}/fragments/{fragment}/comments

  // /comments/{xlinkid}/reply

  // /comments/forurl

  // /uris/{uri:uri}/comments

  // /uris/{uri:uri}/fragments/{fragment}/comments

  /**
   * Returns the URL to a comment.
   *
   * @param member  the username or ID of the member
   * @param comment the xlink ID of the comment to archive
   *
   * @return <code>/members/[member]/comments/[comment]</code>.
   */
  public static String toComment(String member, String comment) {
    return "/members/" + prefixMember(member) + "/comments/" + comment;
  }

  /**
   * Returns the URL to edit a comment.
   *
   * @deprecated Use {@link # toComment(String, String)} with PATCH
   *
   * @param member  the username or ID of the member
   * @param comment the xlink ID of the comment to archive
   *
   * @return <code>/members/[member]/comments/[comment]/edit</code>.
   */
  @Deprecated
  public static String toEditComment(String member, String comment) {
    return "/members/" + prefixMember(member) + "/comments/" + comment + "/edit";
  }

  /**
   * Returns the URL to archive to a comment.
   *
   * @param member  the username or ID of the member
   * @param comment the xlink ID of the comment to archive
   *
   * @return <code>/members/[member]/comments/[comment]/archive</code>.
   */
  public static String toArchiveComment(String member, String comment) {
    return "/members/" + prefixMember(member) + "/comments/" + comment + "/archive";
  }

  /**
   * Returns the URL to unarchive to a comment.
   *
   * @param member  the username or ID of the member
   * @param comment the xlink ID of the comment to archive
   *
   * @return <code>/members/[member]/comments/[comment]/unarchive</code>.
   */
  public static String toUnarchiveComment(String member, String comment) {
    return "/members/" + prefixMember(member) + "/comments/" + comment + "/unarchive";
  }

  /**
   * Shorthand method to return the service to use to create a comment.
   *
   * @param creator The creator of the comment.
   * @param context The context of the comment.
   *
   * @return The corresponding service path.
   */
  public static String toCreateCommentService(PSMember creator, Context context) {
    // If the author is a member it is prefixed by '/members/[id]'
    StringBuilder service = new StringBuilder();
    if (creator != null) {
      service.append("/members/").append(creator.getIdentifier());
    }
    // The context determines the rest of the URL
    if (context.group() != null) {
      service.append("/groups/").append(context.group().getIdentifier()).append("/comments");
    } else {
      PSURI uri = context.uri();
      if (uri != null && uri.getId() != null) {
        service.append("/uris/").append(uri.getId());
        if (context.fragment() != null) {
          service.append("/fragments/").append(context.fragment());
        }
        service.append("/comments");
      } else {
        service.append("/comments/forurl");
      }
    }
    return service.toString();
  }

  /**
   * Shorthand method to return the service to use to reply to a comment.
   *
   * @param author  The author of the comment.
   * @param xlinkid The comment to reply to.
   * @param group   The group the user is
   *
   * @return The corresponding service path.
   */
  public static String toReplyCommentService(Author author, long xlinkid, PSGroup group) {
    // If the author is a member it is prefixed by '/members/[id]'
    StringBuilder service = new StringBuilder();
    if (author.member() != null) {
      service.append("/members/").append(author.member().getIdentifier());
    }
    if (group != null) {
      service.append("/groups/").append(group.getIdentifier());
    }
    service.append("/comments/").append(xlinkid).append("/reply");
    return service.toString();
  }

  // Loading Zone related Services
  // ----------------------------------------------------------------------------------------------

  // /members/{member:member}/groups/{group:group}/loadingzone/delete

  // /members/{member:member}/groups/{group:group}/loadingzone/unzip

  // /members/{member:member}/groups/{group:group}/loadingzone/clear

  // /members/{member:member}/groups/{group:group}/loadingzone/uris

  // Thread related Services
  // ----------------------------------------------------------------------------------------------

  // /threads

  // /groups/{group:group}/threads

  // /groups/{group:group}/threads/{threadid}/cancel

  // /threads/{threadid}/cancel

  /**
   * Returns the URL to check the progress of a thread.
   *
   * @param threadid  the ID of the thread
   *
   * @return <code>/threads/[threadid]/progress</code>.
   */
  public static String toThreadProgress(String threadid) {
    return "/threads/" + threadid + "/progress";
  }

  // /threads/{threadid}/progress

  // /groups/{group:group}/threads/{threadid}/logs

  // Developer Related Services
  // ----------------------------------------------------------------------------------------------

  /**
   * Returns the URL to a resource.
   *
   * @param project the project name or id where the resource should be put.
   *
   * @return <code>/groups/[project]/resources</code>.
   */
  public static String toResource(String project) {
    return "/groups/" + project + "/resources";
  }

  /**
   * Returns the URL to create a resource.
   *
   * @param project the project name or id where the resource should be put.
   *
   * @return <code>/groups/[project]/resources</code>.
   */
  public static String toCreateResource(String project) {
    return "/groups/" + project + "/resources";
  }

  /**
   * Returns the URL to put a resource.
   *
   * @param project the project name or id where the resource should be put.
   *
   * @return <code>/groups/[project]/resources/get</code>.
   */
  public static String toGetResource(String project) {
    return "/groups/" + project + "/resources";
  }

  /**
   * Returns the URL to put a resource.
   *
   * @deprecated Use {@link #toResource(String)} with PUT
   *
   * @param project the project name or id where the resource should be put.
   *
   * @return <code>/groups/[project]/resources</code>.
   */
  @Deprecated
  public static String toPutResource(String project) {
    return "/groups/" + project + "/resources/put";
  }

  /**
   * Returns the URL to delete a resource.
   *
   * @deprecated Use {@link #toResource(String)} with DELETE
   *
   * @param project the project name or id where the resource should be put.
   *
   * @return <code>/groups/[project]/resources/delete</code>.
   */
  @Deprecated
  public static String toDeleteResource(String project) {
    return "/groups/" + project + "/resources/delete";
  }

  // /groups/{group:group}/resources/move

  // /groups/{group:group}/resources/export

  // /groups/{group:group}/resources/import

  // /groups/{group:group}/resources/history

  // /groups/{group:group}/documenttypes/converttopsml

}
