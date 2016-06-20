package org.pageseeder.bridge.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pageseeder.bridge.PSEntity;

/**
 * A enumeration of all services in PageSeeder.
 *
 * @author Christophe Lauret
 * @version 0.9.1
 * @since 0.9.1
 */
public enum Service {

  /** Version of PageSeeder */
  get_version("/version"),

  /** Information about the system */
  about("/about"),

  /** Information about the system */
  about_publish("/publish/about"),

  /** Report an error */
  report_error("/error/report"),

  /** Clear all caches */
  clear_all_cache("/caches/clear"),

  /** Return a summary all internally caches in PageSeeder */
  cache_summary("/caches"),

  /** Return information about a specific cache */
  get_cache("/caches/{name}"),

  /** Return the list of elements in a specific cache */
  list_cache_elements("/caches/{name}/elements"),

  /** Return the list of elements in a specific cache */
  remove_cache_element("/caches/{name}/elements/{key}/remove"),

  /** Clear a specific cache */
  clear_cache("/caches/{name}/clear"),

  /** Create a member */
  create_member("/members"),

  /** Self-register a member */
  register_member("/members/register"),

  /** Create a member and add the member to a group */
  create_membership("/groups/{group:group}/members"),

  /** Permanently delete the membership of a member to a group */
  delete_membership("/groups/{group:group}/members/{member:member}"),

  /** Invite a member to a group */
  invite_member("/groups/{group:group}/members/invite"),

  /** Invite self to a group */
  invite_self("/groups/{group:group}/members/inviteself"),

  /** Create a member's personal group */
  create_personal_group("/members/{member:member}/creategroup"),

  /** Create a member's personal group */
  reset_session("/resetsession"),

  /** Activate a member */
  activate_member("/members/{member:member}/activate"),

  /** Unlock a member's account */
  unlock_member("/members/{member:member}/unlock"),

  /** Edit a member */
  edit_member("/members/{member:member}"),

  /** Edit member email with token */
  edit_member_email("/members/{member:member}/email"),

  /** Decline invitation */
  decline_invitation("/members/{member:member}/declineinvitation"),

  /** Get the memberships for the member */
  list_member_memberships("/members/{member:member}/memberships"),

  /** Get the invitations for the member */
  list_member_invitations("/members/{member:member}/invitations"),

  /** Get a member */
  get_member("/members/{member:member}"),

  /** Reset password as normal user */
  reset_password("/members/resetpassword"),

  /** Send activation email */
  send_activation("/members/sendactivation"),

  /** Reset password as normal user within a group */
  reset_password_group("/groups/{group:group}/members/resetpassword"),

  /** Reset password as admin */
  force_reset_password("/members/forceresetpassword"),

  /** Reset password as admin within a group */
  force_reset_password_group("/groups/{group:group}/members/forceresetpassword"),

  /** Check the strength of the password */
  password_meter("/password/meter"),

  /** Get the memberships for the logged in member */
  get_self("/self"),

  /** Get the memberships for the logged in member */
  list_self_memberships("/self/memberships"),

  /** Returns the list of labels in a group */
  list_labels("/groups/{group:group}/labels"),

  /** Returns the list of subgroups in a group */
  list_subgroups("/groups/{group:group}/subgroups"),

  /** Create a new subgroup in a group */
  add_subgroup("/groups/{group:group}/subgroups/add"),

  /** Edit a subgroup in a group */
  edit_subgroup("/groups/{group:group}/subgroups/{subgroup}"),

  /** Remove a subgroup from a group */
  remove_subgroup("/groups/{group:group}/subgroups/{subgroup}/remove"),

  /** Preview an Email output */
  preview_mail("/members/{member:member}/groups/{group:group}/mail/preview"),

  /** Send an Email */
  send_mail("/members/{member:member}/groups/{group:group}/mail/send"),

  /** Finds projects and groups for a member. */
  find_projects("/members/{member:member}/projects/find"),

  /** Creates a project */
  create_project("/members/{member:member}/projects"),

  /** Edits a project */
  edit_project("/members/{member:member}/projects/{group:group}"),

  /** List project's member details */
  list_project_memberdetails("/projects/{group:group}/memberdetails"),

  /** Get a project */
  get_project("/projects/{group:group}"),

  /** List the children projects/groups the member is registered to - should use URL of list-subprojects-deprecated */
  list_subprojects("/members/{member:member}/projects/{group:group}/subprojectlist"),

  /** List the top level projects the member is registered to - should use URL of list-projects-deprecated */
  list_projects("/members/{member:member}/projectlist"),

  /** List the projects the member is registered to (as a tree) */
  list_projecttree("/members/{member:member}/projecttree"),

  /** List the sub-projects the member is registered to (as a tree) */
  list_subprojecttree("/members/{member:member}/projects/{group:group}/subprojecttree"),

  /** Launch an archive thread for the project */
  archive_project("/members/{member:member}/projects/{group:group}/archive"),

  /** Launch a rename thread for the project */
  rename_project("/members/{member:member}/projects/{group:group}/rename"),

  /** Launch an unarchive thread for the project */
  unarchive_project("/members/{member:member}/projects/{group:group}/unarchive"),

  /** Get a group's group folders */
  list_groupfolders("/groups/{group:group}/groupfolders"),

  /** Create a group folder */
  create_groupfolder("/groups/{group:group}/groupfolders/create"),

  /** Get a group folder with URL */
  get_groupfolder_forurl("/groups/{group:group}/groupfolders/forurl"),

  /** Get a group folder with ID and sharing details (that's why the member on the URL) */
  get_groupfolder_sharing("/members/{member:member}/groups/{group:group}/groupfolders/{groupfolderid}"),

  /** Edit a group folder */
  edit_groupfolder("/groups/{group:group}/groupfolders/{id}"),

  /** Creates a group */
  create_group("/members/{member:member}/groups"),

  /** Edits a group */
  edit_group("/members/{member:member}/groups/{group:group}"),

  /** Get a group */
  get_group("/groups/{group:group}"),

  /** Get the groups visible to this member */
  list_visible_groups("/members/{member:member}/visiblegroups"),

  /** Get group member details */
  get_group_memberdetails("/members/{member:member}/groups/{group:group}/memberdetails"),

  /** Get group properties */
  get_group_properties("/groups/{group:group}/properties"),

  /** Start a publish job */
  start_group_publish("/members/{member:member}/groups/{group:group}/publish/start"),

  /** Start a publish job */
  start_uri_publish("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/publish/start"),

  /** Status of a publish job */
  check_group_publish("/members/{member:member}/publish/check"),

  /** Cancelling a publish job */
  cancel_group_publish("/members/{member:member}/publish/cancel"),

  /** Status of a publish job */
  checkall_publish("/publish/checkall"),

  /** "Pause/continue" a scheduled publish job */
  schedule_publish_action("/publish/schedule"),

  /** Collection of members in the group */
  list_group_members("/groups/{group:group}/members"),

  /** Search for members in the group */
  find_group_members("/groups/{group:group}/members/find"),

  /** Returns the list of members in the group */
  list_group_members_details("/groups/{group:group}/members/alldetails"),

  /** Get a membership */
  get_membership("/groups/{group:group}/members/{member:member}"),

  /** Edit a membership */
  edit_membership("/groups/{group:group}/members/{member:member}"),

  /** Deregister a member */
  deregister_group_member("/groups/{group:group}/members/{member:member}/deregister"),

  /** Manage a membership */
  manage_group_member("/groups/{group:group}/members/{member:member}/manage"),

  /** Autocomplete for a specific field */
  autocomplete_group_field("/groups/{group:group}/autocomplete/{field}"),

  /** Autocomplete */
  autocomplete_group("/groups/{group:group}/autocomplete"),

  /** Autosuggest a query */
  autosuggest_group("/groups/{group:group}/autosuggest"),

  /** Service supporting the omnibox */
  omnibox_group("/groups/{group:group}/omnibox"),

  /** Autosuggest a query for a specific field */
  autosuggest_group_fields("/groups/{group:group}/autosuggest/fields"),

  /** Returns the facets for a field */
  get_group_facet("/groups/{group:group}/facet"),

  /** Gets the publish config for a group */
  get_group_publish_config("/members/{member:member}/groups/{group:group}/publishconfig"),

  /** Error reporting at group level */
  report_group_error("/groups/{group:group}/error/report"),

  /** Apply sharing for a group */
  apply_group_share("/members/{member:member}/groups/{group:group}/applyshare"),

  /** Convert documents to PSML for a group */
  convert_to_psml("/members/{member:member}/groups/{group:group}/converttopsml"),

  /** Resolve PS standard documents */
  resolve_standard("/members/{member:member}/resolvestandard"),

  /** Find images */
  find_group_image("/groups/{group:group}/images/find"),

  /** Start group indexing */
  start_group_index("/members/{member:member}/groups/{group:group}/index/start"),

  /** Group's index status */
  get_group_index_status("/groups/{group:group}/index/status"),

  /** Clear group's index */
  clear_group_index("/groups/{group:group}/index/clear"),

  /** Launch an archive thread for the group */
  archive_group("/members/{member:member}/groups/{group:group}/archive"),

  /** Launch a rename thread for the group */
  rename_group("/members/{member:member}/groups/{group:group}/rename"),

  /** Launch an unarchive thread for the group */
  unarchive_group("/members/{member:member}/groups/{group:group}/unarchive"),

  /** Start server indexing */
  member_server_index_start("/members/{member:member}/index/start"),

  /** Status of server indexing */
  member_server_index_status("/members/{member:member}/index/status"),

  /** Gets the publish config for all URIs in a group */
  get_uris_publish_config("/members/{member:member}/groups/{group:group}/uris/publishconfig"),

  /** Gets the publish config for a URI */
  get_uri_publish_config("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/publishconfig"),

  /** Lise URIs for URL */
  list_uri_uris_forurl("/groups/{group:group}/uris/forurl/uris"),

  /** List URIs for URI */
  list_uri_uris("/groups/{group:group}/uris/{uri:uri}/uris"),

  /** Load a host's external URIs */
  list_host_externaluris("/groups/{group:group}/hosts/{host}/externaluris"),

  /** Load a URI's children external URIs */
  list_externaluri_externaluris_forurl("/groups/{group:group}/externaluris/forurl/externaluris"),

  /** Load a URI's children external URIs */
  list_externaluri_externaluris("/groups/{group:group}/externaluris/{uri:uri}/externaluris"),

  /** Load a single URI object */
  get_uri_forurl("/groups/{group:group}/uris/forurl"),

  /** Load a single URI object */
  get_uri("/groups/{group:group}/uris/{uri:uri}"),

  /** Load a URI object with sharing information */
  get_uri_sharing_forurl("/members/{member:member}/groups/{group:group}/uris/forurl"),

  /** Load a URI object with sharing information */
  get_uri_sharing("/members/{member:member}/groups/{group:group}/uris/{uri:uri}"),

  /** Create an external URI */
  create_externaluri("/members/{member:member}/groups/{group:group}/externaluris"),

  /** Edit an external URI */
  edit_externaluri("/members/{member:member}/groups/{group:group}/externaluris/{uri:uri}"),

  /** Archive an external URI */
  archive_externaluri("/members/{member:member}/groups/{group:group}/externaluris/{uri:uri}/archive"),

  /** Unarchive an external URI */
  unarchive_externaluri("/members/{member:member}/groups/{group:group}/externaluris/{uri:uri}/unarchive"),

  /** Load a URI's versions */
  list_uri_versions("/groups/{group:group}/uris/{uri:uri}/versions"),

  /** Load a URI's workflow */
  get_uri_workflow("/groups/{group:group}/uris/{uri:uri}/workflow"),

  /** Resolve references for a group */
  resolve_group_refs("/members/{member:member}/groups/{group:group}/resolverefs"),

  /** Resolve references for a group folder */
  resolve_groupfolder_refs("/members/{member:member}/groups/{group:group}/groupfolders/{guri}/resolverefs"),

  /** Resolve References for a folder */
  resolve_folder_refs("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/resolverefs"),

  /** Load a URI's xrefs */
  list_uri_xrefs("/groups/{group:group}/uris/{uri:uri}/xrefs"),

  /** Load a URI's xref tree */
  list_uri_xreftree("/groups/{group:group}/uris/{uri:uri}/xreftree"),

  /** Create a new workflow */
  create_uri_workflow("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/workflow"),

  /** Create a new version */
  create_uri_version("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/versions"),

  /** Archive a URI's version */
  archive_uri_version("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/versions/{versionid}/archive"),

  /** Create a new xref */
  create_uri_xref("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/xrefs"),

  /** Archive a URI's xref */
  archive_uri_xref("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/xrefs/{xrefid}/archive"),


  /** ========================================================================================= */
  /** Document Services                                                                         */
  /** ========================================================================================= */

  /** Start folder indexing */
  start_uri_index("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/index/start"),

  /** Load Timeline for a URI */
  get_uri_history("/groups/{group:group}/uris/{uri:uri}/history"),

  /** Edits a URI */
  edit_uri("/members/{member:member}/groups/{group:group}/uris/{uri:uri}"),

  /** Create a PSML document */
  create_document("/members/{member:member}/groups/{group:group}/documents"),
  create_document_forurl("/members/{member:member}/groups/{group:group}/documents/forurl"),
  create_document_foruri("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/documents"),

  /** Duplicate a PSML document */
  duplicate_psml("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/duplicate"),

  /** Create a folder */
  create_folder("/members/{member:member}/groups/{group:group}/folders"),
  create_folder_forurl("/members/{member:member}/groups/{group:group}/folders/forurl"),
  create_folder_foruri("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/folders"),

  /** Returns the schemas available for a URI */
  get_uri_schemas("/groups/{group:group}/uris/{uri:uri}/schemas"),

  /** Returns the index entry for a URI */
  get_uri_index("/groups/{group:group}/uris/{uri:uri}/index"),

  /** Edit inline label */
  edit_psml_inlinelabel("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/editinlinelabel"),

  /** Edit property */
  edit_psml_property("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/editpsmlproperty"),

  /** Create New Fragment */
  add_uri_fragment("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/fragments"),

  /** Move a Fragment */
  move_uri_fragment("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/fragments/{fragment}/move"),

  /** Fragment's Content */
  uri_fragment("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/fragments/{fragment}"),

  /** Revert Fragment's Content */
  revert_uri_fragment("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/fragments/{fragment}/revert"),

  /** List Fragment's Edits */
  list_fragment_edits("/groups/{group:group}/uris/{uri:uri}/fragments/{fragment}/edits"),

  /** List Structure Edits */
  list_structure_edits("/groups/{group:group}/uris/{uri:uri}/structure/edits"),

  /** Get URI metadata */
  get_uri_metadata("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/metadata"),

  /** Edit URI metadata */
  edit_uri_metadata("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/metadata"),

  /** Revert URI Metadata */
  revert_uri_metadata("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/metadata/revert"),

  /** List Metadata Edits */
  list_metadata_edits("/groups/{group:group}/uris/{uri:uri}/metadata/edits"),

  /** Share latest edits */
  share_uri_latest_edits("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/edits/sharelatest"),

  /** Share an edit */
  share_uri_edit("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/edits/{editid}/share"),

  /** Create an edit note */
  create_edit_note("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/edits/{editid}/notes"),

  /** Get config files for a URI */
  get_uri_config("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/config"),

  /** Get draft edits for a URI */
  get_uri_drafts("/groups/{group:group}/uris/{uri:uri}/drafts"),

  /** Delete a specific draft */
  delete_uri_draft("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/drafts/{editid}/delete"),

  /** Get draft edits for a URI fragment */
  get_uri_fragment_drafts("/groups/{group:group}/uris/{uri:uri}/fragments/{fragment}/drafts"),

  /** Move a uri */
  move_uri("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/move"),

  /** Archive a uri */
  archive_uri("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/archive"),

  /** Delete a uri */
  delete_uri("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/delete"),

  /** Validation uri */
  validate_uri("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/validate"),

  /** Start folder validation */
  validate_folder("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/foldervalidate"),

  /** Start export */
  uri_export("/members/{member:member}/uris/{uri:uri}/export"),

  /** Start export for multiple uris or uri using path */
  uris_export("/members/{member:member}/export"),

  /** Start export */
  uri_group_export("/members/{member:member}/groups/{group:group}/uris/{uri:uri}/export"),

  /** Start export for multiple uris or uri using path */
  uris_group_export("/members/{member:member}/groups/{group:group}/export"),

  /** Update host's group URIs and URIs with new scheme/port */
  update_host("/members/{member:member}/hosts/{host}/update"),

  /** List external hosts for group */
  list_externaluris_hosts("/groups/{group:group}/externaluris/hosts"),

  /** List hosts for server */
  list_hosts("/hosts"),

  /** Create a host */
  create_host("/hosts"),

  /** Edit a host */
  edit_host("/hosts/{hostname}"),

  /** Delete a host */
  delete_host("/hosts/{hostname}"),

  /** Create a host alias */
  create_hostalias("/hosts/{hostname}/hostaliases"),

  /** Edit a host alias */
  edit_hostalias("/hostaliases/{aliasname}"),

  /** Delete a host alias */
  delete_hostalias("/hostaliases/{aliasname}"),

  /** Find tasks */
  find_tasks("/members/{member:member}/tasks/find"),

  /** Find public tasks */
  find_tasks_public("/tasks/find"),

  /** Load a task */
  get_task("/members/{member:member}/tasks/{taskid}"),

  /** Load a public task */
  get_task_public("/tasks/{taskid}"),

  /** Load a group's discussions */
  list_group_discussions("/groups/{group:group}/discussions"),

  /** Load a group discussion */
  get_group_discussion("/groups/{group:group}/discussions/{discussion}"),

  /** Load discussions for a URL */
  list_url_discussions("/members/{member:member}/discussions/forurl"),

  /** Load a discussion */
  get_discussion("/members/{member:member}/discussions/{discussion}"),

  /** Load discussions for a URI */
  list_uri_discussions("/members/{member:member}/uris/{uri:uri}/discussions"),

  /** Load discussions for a URI fragment */
  list_uri_fragment_discussions("/members/{member:member}/uris/{uri:uri}/fragments/{fragment}/discussions"),

  /** Load public discussions for a URL */
  list_url_discussions_public("/discussions/forurl"),

  /** Load a public discussion */
  get_discussion_public("/discussions/{discussion}"),

  /** Find comments */
  find_comments("/members/{member:member}/comments/find"),

  /** Find public comments */
  find_comments_public("/comments/find"),

  /** List draft comments */
  list_draft_comments("/members/{member:member}/comments/draft"),

  /** Load public discussions for a URI */
  list_uri_discussions_public("/uris/{uri:uri}/discussions"),

  /** Load discussions for a URI fragment */
  list_uri_fragment_discussions_public("/uris/{uri:uri}/fragments/{fragment}/discussions"),

  /** Create a new group comment from member */
  create_comment("/members/{member:member}/groups/{group:group}/comments"),

  /** Create a new group comment */
  create_comment_public("/groups/{group:group}/comments"),

  /** Create a new group reply from member */
  reply_group_comment("/members/{member:member}/groups/{group:group}/comments/{xlinkid}/reply"),

  /** Create a new group reply */
  reply_group_comment_public("/groups/{group:group}/comments/{xlinkid}/reply"),

  /** Create a new reply from member */
  reply_comment("/members/{member:member}/comments/{xlinkid}/reply"),

  /** Create a new comment for URL from member */
  create_comment_url("/members/{member:member}/comments/forurl"),

  /** Create a new comment for URI from member */
  create_comment_uri("/members/{member:member}/uris/{uri:uri}/comments"),

  /** Create a new comment for URI fragment from member */
  create_comment_uri_fragment("/members/{member:member}/uris/{uri:uri}/fragments/{fragment}/comments"),

  /** Create a new reply */
  reply_comment_public("/comments/{xlinkid}/reply"),

  /** Create a new comment for URL */
  create_comment_url_public("/comments/forurl"),

  /** Create a new comment for URI */
  create_comment_uri_public("/uris/{uri:uri}/comments"),

  /** Create a new comment for URI fragment */
  create_comment_uri_fragment_public("/uris/{uri:uri}/fragments/{fragment}/comments"),

  /** Edit comment */
  edit_comment("/members/{member:member}/comments/{commentid}"),

  /** Archive comment */
  archive_comment("/members/{member:member}/comments/{commentid}/archive"),

  /** Unarchive comment */
  unarchive_comment("/members/{member:member}/comments/{commentid}/unarchive"),

  /** Delete comment */
  delete_comment("/members/{member:member}/comments/{commentid}"),

  /** Load a comment */
  get_comment("/members/{member:member}/comments/{commentid}"),

  /** Load a public comment */
  get_comment_public("/comments/{commentid}"),

  /** Deleting the content of a member loading zone */
  delete_member_loaded_content("/members/{member:member}/groups/{group:group}/loadingzone/delete"),

  /** Unzipping content in a member loading zone */
  unzip_member_loaded_content("/members/{member:member}/groups/{group:group}/loadingzone/unzip"),

  /** Clearing the content in a member loading zone */
  clear_member_loading_zone("/members/{member:member}/groups/{group:group}/loadingzone/clear"),

  /** Uploaded URIs */
  get_member_loaded_uris("/members/{member:member}/groups/{group:group}/loadingzone/uris"),

  /** List All Threads */
  list_server_threads("/threads"),

  list_group_threads("/groups/{group:group}/threads"),

  /** Cancel Thread */
  cancel_group_thread("/groups/{group:group}/threads/{threadid}/cancel"),

  /** Cancel Thread */
  cancel_thread("/threads/{threadid}/cancel"),

  /** Thread Progress */
  get_group_thread_progress("/groups/{group:group}/threads/{threadid}/progress"),

  /** Thread Progress */
  get_thread_progress("/threads/{threadid}/progress"),

  /** Thread Logs */
  get_group_thread_logs("/groups/{group:group}/threads/{threadid}/logs"),

  /** Create a resource */
  group_resources("/groups/{group:group}/resources"),

  /** Create a resource */
  create_group_resource("/groups/{group:group}/resources"),

  /** Move a resource */
  move_group_resources("/groups/{group:group}/resources/move"),

  /** Export all the files in the project */
  export_group_resources("/groups/{group:group}/resources/export"),

  /** Import all the files in the project */
  import_group_resources("/groups/{group:group}/resources/import"),

  /** Update a resource */
  get_resource_history("/groups/{group:group}/resources/history"),

  /** Convert document types to PSML */
  convert_documenttypes_to_psml("/groups/{group:group}/documenttypes/converttopsml");

  private final String _template;

  private List<Token> _tokens;

  private Service(String template) {
    this._template = template;
    this._tokens = toTokens(template);
  }

  public String toPath(Object... variables) {
    StringBuilder url = new StringBuilder();
    int i = 0;
    // URI | member | group
    for (Token t : this._tokens) {
      if (t instanceof StringToken) {
        url.append(t.toString());
      } else {
        url.append(t.toString(variables[i++]));
      }
    }
    return url.toString();
  }

  private static List<Token> toTokens(String template) {
    // No need to parse
    if (template.indexOf('{') <0 )
      return Collections.singletonList((Token)new StringToken(template));
    //
    Pattern P = Pattern.compile("\\{[a-z:]+\\}");
    Matcher m = P.matcher(template);
    // FIXME
    return null;
  }

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

  private static interface Token {
    public String toString(Object o);
  }

  private static class StringToken implements Token{
    final String _token;
    public StringToken(String t) {
      this._token = t;
    }
    @Override
    public String toString(Object o) {
      return this._token;
    }
  }

  private static class EntityToken implements Token {

    @Override
    public String toString(Object o) {
      if (o instanceof Long) return o.toString();
      else if (o instanceof PSEntity) {
        PSEntity entity = (PSEntity)o;
        if (entity.getId() != null)
          return entity.getId().toString();
        else
          return encode(entity.getIdentifier());
      } else {
        String s = o.toString();
        if (isNumeric(s)) return s;
        else return encode(s);
      }
    }
  }

  private static String encode(String s) {
    try {
      return "~" + URLEncoder.encode(s, "UTF-8").replace("+", "%20");
    } catch (UnsupportedEncodingException ex) {
      // shouldn't happen
      throw new IllegalArgumentException();
    }
  }

  private static boolean isNumeric(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c < '0' && c > '9') return false;
    }
    return true;
  }

}
