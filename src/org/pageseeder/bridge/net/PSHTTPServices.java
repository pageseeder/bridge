/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.net;

/**
 * A utility class for PageSeeder Services.
 *
 * <p>Provides useful constants for Services used in this project.
 *
 * @author Christophe Lauret
 * @version 0.2.0
 * @since 0.2.0
 */
final class PSHTTPServices {

  /**
   * Utility class.
   */
  private PSHTTPServices() {}

  // Public service URLs (referenced directly somewhere)
  // ---------------------------------------------------------------------------------------------

  /**
   * Returns the URL to invoke the group member edit service.
   *
   * @param username the user name
   *
   * @return <code>/groups/[groupname]/members/[username]/edit</code>.
   */
  public static String toEditMembership(String group, String username) {
    return "/groups/" + group + "/members/" + username + "/edit";
  }

  /**
   * Returns the URL to invoke the group member registration service.
   *
   * @param username the user name
   *
   * @return <code>/groups/[groupname]/members/[username]/manage</code>.
   */
  public static String toGroupMemberManage(String group, String username) {
    return "/groups/" + group + "/members/" + username + "/manage";
  }

  /**
   * Returns the URL to invoke the group member search service.
   *
   * @return <code>/groups/[groupname]/members/find</code>.
   */
  public static String toFindGroupMember(String group) {
    return "/groups/" + group + "/members/find";
  }

  /**
   * Returns the URL to force the password to be reset.
   *
   * @return <code>/groups/[groupname]/members/forceresetpassword</code>.
   */
  public static String toForceResetPassword(String group) {
    return "/groups/" + group + "/members/forceresetpassword";
  }

  // Protected service URLs (referenced by the PSConnectors helper class)
  // ---------------------------------------------------------------------------------------------

  /**
   * Returns the URL to invoke the group member creation service.
   *
   * @return <code>/groups/[groupname]/members/create</code>.
   */
  protected static String toCreateMembership(String group) {
    return "/groups/" + group + "/members/create";
  }

  /**
   * Returns the URL to invoke the group member invitation service.
   *
   * @return <code>/groups/[groupname]/members/invite</code>.
   */
  protected static String toInviteMember(String group) {
    return "/groups/" + group + "/members/invite";
  }

  /**
   * Returns the URL to invoke the group member invitation service.
   *
   * @return <code>/groups/[groupname]/members/invite</code>.
   */
  protected static String toInviteSelf(String group, String member) {
    return "/groups/" + group + "/members/"+member+"/inviteself";
  }

  /**
   * Returns the URL to invoke the group member activation service.
   *
   * @param username the user name
   *
   * @return <code>/members/[username]/activate</code>.
   */
  protected static String toActivateMember(String username) {
    return "/members/" + username + "/activate";
  }

  /**
   * Returns the URL to list the memberships of a user.
   *
   * @param username the user name
   *
   * @return <code>/members/[username]/memberships</code>.
   */
  protected static String toListMemberships(String username) {
    return "/members/" + username + "/memberships";
  }

  /**
   * Returns the URL to invoke the group member details service.
   *
   * @param username the user name
   *
   * @return <code>/groups/[groupname]/members/[username]/details</code>.
   */
  protected static String toMembershipDetails(String group, String username) {
    return "/groups/" + group + "/members/" + username + "/details";
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
