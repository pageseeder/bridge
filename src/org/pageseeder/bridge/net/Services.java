/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.net;

/**
 * A low-level utility class to generate the URL for PageSeeder services.
 *
 * <p>Provides useful constants for PageSeeder services.
 *
 * <p>As a convention, most parameters in this class are string that corresponding to the
 * identifier for the entity been searched, for example:
 * <ul>
 *   <li><b>group</b> - group name of ID
 *   <li><b>member</b> - member username or ID
 * </ul>
 *
 * @author Christophe Lauret
 * @version 0.2.4
 * @since 0.2.0
 */
public final class Services {

  /**
   * Utility class.
   */
  private Services() {
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
   * Returns the URL to create a group.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/groups/create</code>.
   */
  public static String toCreateGroup(String member) {
    return "/members/"+member+"/groups/create";
  }

  /**
   * Returns the URL to create a project.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/projects/create</code>.
   */
  public static String toCreateProject(String member) {
    return "/members/"+member+"/projects/create";
  }

  /**
   * Returns the URL to invoke the group member edit service.
   *
   * @param group  the group name or id
   * @param member the member username or id
   *
   * @return <code>/groups/[group]/members/[member]/edit</code>.
   */
  public static String toEditMembership(String group, String member) {
    return "/groups/" + group + "/members/" + member + "/edit";
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
    return "/groups/" + group + "/members/" + member + "/manage";
  }

  /**
   * Returns the URL to invoke the group member search service.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/members/find</code>.
   */
  public static String toFindGroupMember(String group) {
    return "/groups/" + group + "/members/find";
  }

  /**
   * Returns the URL to force the password to be reset.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/members/forceresetpassword</code>.
   */
  public static String toForceResetPassword(String group) {
    return "/groups/" + group + "/members/forceresetpassword";
  }

  /**
   * Returns the URL to invoke the group member creation service.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/members/create</code>.
   */
  public static String toCreateMembership(String group) {
    return "/groups/" + group + "/members/create";
  }

  /**
   * Returns the URL to invoke the group member invitation service.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/members/invite</code>.
   */
  public static String toInviteMember(String group) {
    return "/groups/" + group + "/members/invite";
  }

  /**
   * Returns the URL to invoke the group member invitation service.
   *
   * @param group  the group name or id
   * @param member the member username or id
   *
   * @return <code>/groups/[group]/members/invite</code>.
   */
  public static String toInviteSelf(String group, String member) {
    return "/groups/" + group + "/members/"+member+"/inviteself";
  }

  /**
   * Returns the URL to return the details of a member.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/details</code>.
   */
  public static String toMemberDetails(String member) {
    return "/members/" + member + "/details";
  }

  /**
   * Returns the URL to edit the details of a member.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/edit</code>.
   */
  protected static String toMemberEdit(String member) {
    return "/members/" + member + "/edit";
  }

  /**
   * Returns the URL to invoke the group member activation service.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/activate</code>.
   */
  protected static String toActivateMember(String member) {
    return "/members/" + member + "/activate";
  }

  /**
   * Returns the URL to list the memberships of a user.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/memberships</code>.
   */
  protected static String toListMemberships(String member) {
    return "/members/" + member + "/memberships";
  }

  /**
   * Returns the URL to invoke the group member details service.
   *
   * @param group  the group name or id
   * @param member the member username or id
   *
   * @return <code>/groups/[groupname]/members/[username]/details</code>.
   */
  protected static String toMembershipDetails(String group, String member) {
    return "/groups/" + group + "/members/" + member + "/details";
  }

  /**
   * Returns the URL to reply to a comment.
   *
   * @param userId  the user id
   * @param xlinkId the xlink id of the comment to reply to
   *
   * @return <code>/members/[userid]/comments/[xlinkid]/reply</code>.
   */
  protected static String toReplyComment(String xlinkId, long userId) {
    return "/members/" + userId + "/comments/" + xlinkId + "/reply";
  }
}
