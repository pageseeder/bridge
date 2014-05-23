/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.net;

import org.pageseeder.bridge.model.PSComment.Author;
import org.pageseeder.bridge.model.PSComment.Context;
import org.pageseeder.bridge.model.PSGroup;
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
   * Returns the URL to get a group.
   *
   * @param group the group name or id
   *
   * @return <code>/groups/[group]</code>.
   */
  public static String toGetGroup(String group) {
    return "/groups/"+group;
  }

  /**
   * Returns the URL to add a subgroup.
   *
   * @param group the group name or id the subgroup is added to.
   *
   * @return <code>/groups/[group]/subgroups/add</code>.
   */
  public static String toAddSubGroup(String group) {
    return "/groups/"+group+"/subgroups/add";
  }

  /**
   * Returns the URL to put a resource
   *
   * @param project the project name or id where the resource should be put.
   *
   * @return <code>/groups/[project]/resources/put</code>.
   */
  public static String toPutResource(String project) {
    return "/groups/"+project+"/resources/put";
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
   * Returns the URL to list the members of a group
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/members</code>.
   */
  public static String toListMembers(String group) {
    return "/groups/" + group + "/members";
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
   * Returns the URL to remove a member from a group.
   *
   * @param group  the group name or id
   * @param member the member username or id
   *
   * @return <code>/groups/[group]/members/[member]/delete</code>.
   */
  public static String toDeleteMembership(String group, String member) {
    return "/groups/" + group + "/members/" + member + "/delete";
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
   * Returns the URL to create a member.
   *
   * @param member the member username or id
   *
   * @return <code>/members/[member]/edit</code>.
   */
  protected static String toMemberCreate(String member) {
    return "/members/" + member + "/create";
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

  /**
   * Returns the URL to get a document for a given URL.
   *
   * @param member the member username or id
   * @param group  the group name or id
   *
   * @return <code>/members/[member]/groups/[group]/documents/forurl</code>.
   */
  public static String toGetDocumentForURL(String member, String group) {
    return "/members/"+member+"/groups/"+group+"/documents/forurl";
  }

  /**
   * Returns the URL to get a URI for a given URL.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/uris/forurl</code>.
   */
  public static String toGetURIForURL(String group) {
    return "/groups/"+group+"/uris/forurl";
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
    return "/groups/"+group+"/uris/"+uri;
  }

  /**
   * Returns the URL to create a group folder.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/folders/create</code>.
   */
  public static String toCreateGroupFolder(String group) {
    return "/groups/"+group+"/folders/create";
  }

  /**
   * Returns the URL to get a group folder for a given url.
   *
   * @param group  the group name or id
   *
   * @return <code>/groups/[group]/folders/forurl</code>.
   */
  public static String toGetGroupFolderForURL(String group) {
    return "/groups/"+group+"/folders/forurl";
  }

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
    return "/members/"+member+"/groups/"+group+"/uris/"+uri+"/fragments/"+fragment;
  }

  /**
   * Returns the URL to send an email
   *
   * @param member the username or id of the member on behalf of whom the mail is sent
   * @param group  the group name or id, the member belongs to.
   *
   * @return <code>/members/[member]/groups/[group]/mail/send</code>
   */
  public static String toSendMail(String member, String group) {
    return "/members/"+member+"/groups/"+group+"/mail/send";
  }

  /**
   * Return the service to use to create a comment.
   *
   * @param author  The author of the comment.
   * @param context The context of the comment.
   *
   * @return The corresponding service path.
   */
  public static String toCreateCommentService(Author author, Context context) {
    // If the author is a member it is prefixed by '/members/[id]'
    StringBuilder service = new StringBuilder();
    if (author.member() != null) {
      service.append("/members/").append(author.member().getIdentifier());
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
   * Return the service to use to create a comment.
   *
   * @param author  The author of the comment.
   * @param xlinkid The comment to reply to.
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

}
