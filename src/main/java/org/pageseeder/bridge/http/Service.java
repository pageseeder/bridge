/*
 * Copyright 2016 Allette Systems (Australia)
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
package org.pageseeder.bridge.http;

import org.eclipse.jdt.annotation.NonNull;

/**
 * A enumeration of all services in PageSeeder.
 *
 * @author Christophe Lauret
 * @version 0.9.2
 * @since 0.9.1
 */
public enum Service {

  /**
   * Version of PageSeeder <code>/version</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-version-GET.html">get-version (GET)</a>
   */
  get_version("/version"),

  /**
   * Information about the system <code>/about</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/about-GET.html">about (GET)</a>
   */
  about("/about"),

  /**
   * Information about the system <code>/publish/about</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/about-publish-GET.html">about-publish (GET)</a>
   */
  about_publish("/publish/about"),

  /**
   * Report an error <code>/error/report</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/report-error-GET.html">report-error (GET)</a>
   */
  report_error("/error/report"),

  /**
   * Return a summary all internally caches in PageSeeder <code>/caches</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/cache-summary-GET.html">cache-summary (GET)</a>
   */
  cache_summary("/caches"),

  /**
   * Clear all caches <code>/caches/clear</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/clear-all-cache-GET.html">clear-all-cache (GET)</a>
   */
  clear_all_cache("/caches/clear"),

  /**
   * Return information about a specific cache  <code>/caches/{name}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-cache-GET.html">get-cache (GET)</a>
   */
  get_cache("/caches/{name}"),

  /**
   * Return the list of elements in a specific cache <code>/caches/{name}/elements</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-cache-elements-GET.html">list-cache-elements (GET)</a>
   */
  list_cache_elements("/caches/{name}/elements"),

  /**
   * Return the list of elements in a specific cache <code>/caches/{name}/elements/{key}/remove</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/remove-cache-element-GET.html">remove-cache-element (GET)</a>
   */
  remove_cache_element("/caches/{name}/elements/{key}/remove"),

  /**
   * Clear a specific cache <code>/caches/{name}/clear</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/clear-cache-GET.html">clear-cache (GET)</a>
   */
  clear_cache("/caches/{name}/clear"),

  /**
   * Create or list members <code>/members</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/members-GET.html">members (GET)</a>
   */
  members("/members"),

  /**
   * Reset password as admin <code>/members/forceresetpassword</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/force-reset-password-GET.html">force-reset-password (GET)</a>
   */
  force_reset_password("/members/forceresetpassword"),

  /**
   * Self-register a member <code>/members/register</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/register-member-GET.html">register-member (GET)</a>
   */
  register_member("/members/register"),

  /**
   * Reset password as normal user <code>/members/resetpassword</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/reset-password-GET.html">reset-password (GET)</a>
   */
  reset_password("/members/resetpassword"),

  /**
   * Send activation email <code>/members/sendactivation</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/send-activation-GET.html">send-activation (GET)</a>
   */
  send_activation("/members/sendactivation"),

  /**
   * Get or edit a member <code>/members/{member}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/member-GET.html">member (GET)</a>
   */
  member("/members/{member}"),

  /**
   * Activate a member <code>/members/{member}/activate</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/activate-member-GET.html">activate-member (GET)</a>
   */
  activate_member("/members/{member}/activate"),

  /**
   * Create a member's personal group <code>/members/{member}/creategroup</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-personal-group-GET.html">create-personal-group (GET)</a>
   */
  create_personal_group("/members/{member}/creategroup"),

  /**
   * Decline invitation <code>/members/{member}/declineinvitation</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/decline-invitation-GET.html">decline-invitation (GET)</a>
   */
  decline_invitation("/members/{member}/declineinvitation"),

  /**
   * Edit member email with token <code>/members/{member}/email</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-member-email-GET.html">edit-member-email (GET)</a>
   */
  edit_member_email("/members/{member}/email"),

  /**
   * Get the invitations for the member <code>/members/{member}/invitations</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-member-invitations-GET.html">list-member-invitations (GET)</a>
   */
  list_member_invitations("/members/{member}/invitations"),

  /**
   * Unlock a member's account <code>/members/{member}/unlock</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/unlock-member-GET.html">unlock-member (GET)</a>
   */
  unlock_member("/members/{member}/unlock"),

  /**
   * Get the memberships for the member <code>/members/{member}/memberships</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-member-memberships-GET.html">list-member-memberships (GET)</a>
   */
  list_member_memberships("/members/{member}/memberships"),

  /**
   * Create a member's personal group
   * <code>/resetsession</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/reset-session-GET.html">reset-session (GET)</a>
   */
  reset_session("/resetsession"),

  /**
   * Collection of members in the group.
   *
   * This service can be used to create a member and add the member to a group
   *
   * <code>/groups/{group}/members</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/memberships-GET.html">memberships (GET)</a>
   */
  memberships("/groups/{group}/members"),

  /**
   * Reset password as normal user within a group <code>/groups/{group}/members/resetpassword</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/reset-password-group-GET.html">reset-password-group (GET)</a>
   */
  reset_password_group("/groups/{group}/members/resetpassword"),

  /**
   * Get, edit or delete the membership of a member to a group <code>/groups/{group}/members/{member}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-membership-GET.html">get-membership (GET)</a>
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-membership-PATCH.html">edit-membership (PATCH)</a>
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/delete-membership-DELETE.html">membership (DELETE)</a>
   */
  membership("/groups/{group}/members/{member}"),

  /**
   * Reset password as admin within a group
   * <code>/groups/{group}/members/forceresetpassword</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/force-reset-password-group-GET.html">force-reset-password-group (GET)</a>
   */
  force_reset_password_group("/groups/{group}/members/forceresetpassword"),

  /**
   * Invite a member to a group <code>/groups/{group}/members/invite</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/invite-member-GET.html">invite-member (GET)</a>
   */
  invite_member("/groups/{group}/members/invite"),

  /**
   * Invite self to a group <code>/groups/{group}/members/inviteself</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/invite-self-GET.html">invite-self (GET)</a>
   */
  invite_self("/groups/{group}/members/inviteself"),

  /**
   * Check the strength of the password <code>/password/meter</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/password-meter-GET.html">password-meter (GET)</a>
   */
  password_meter("/password/meter"),

  /**
   * Get the logged in member <code>/self</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-self-GET.html">get-self (GET)</a>
   */
  get_self("/self"),

  /**
   * Get the memberships for the logged in member <code>/self/memberships</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-self-memberships-GET.html">list-self-memberships (GET)</a>
   */
  list_self_memberships("/self/memberships"),

  /**
   * Returns the list of labels in a group <code>/groups/{group}/labels</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-labels-GET.html">list-labels (GET)</a>
   */
  list_labels("/groups/{group}/labels"),

  /**
   * Returns the list of subgroups in a group <code>/groups/{group}/subgroups</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-subgroups-GET.html">list-subgroups (GET)</a>
   */
  list_subgroups("/groups/{group}/subgroups"),

  /**
   * Create a new subgroup in a group <code>/groups/{group}/subgroups/add</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/add-subgroup-GET.html">add-subgroup (GET)</a>
   */
  add_subgroup("/groups/{group}/subgroups/add"),

  /**
   * Edit a subgroup in a group <code>/groups/{group}/subgroups/{subgroup}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-subgroup-GET.html">edit-subgroup (GET)</a>
   */
  edit_subgroup("/groups/{group}/subgroups/{subgroup}"),

  /**
   * Remove a subgroup from a group <code>/groups/{group}/subgroups/{subgroup}/remove</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/remove-subgroup-GET.html">remove-subgroup (GET)</a>
   */
  remove_subgroup("/groups/{group}/subgroups/{subgroup}/remove"),

  /**
   * Preview an Email output <code>/members/{member}/groups/{group}/mail/preview</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/preview-mail-GET.html">preview-mail (GET)</a>
   */
  preview_mail("/members/{member}/groups/{group}/mail/preview"),

  /**
   * Send an Email <code>/members/{member}/groups/{group}/mail/send</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/send-mail-GET.html">send-mail (GET)</a>
   */
  send_mail("/members/{member}/groups/{group}/mail/send"),

  /**
   * Finds projects and groups for a member <code>/members/{member}/projects/find</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/find-projects-GET.html">find-projects (GET)</a>
   */
  find_projects("/members/{member}/projects/find"),

  /**
   * Creates a project <code>/members/{member}/projects</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-project-GET.html">create-project (GET)</a>
   */
  create_project("/members/{member}/projects"),

  /**
   * Edits a project <code>/members/{member}/projects/{group}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-project-GET.html">edit-project (GET)</a>
   */
  edit_project("/members/{member}/projects/{group}"),

  /**
   * List project's member details <code>/projects/{group}/memberdetails</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-project-memberdetails-GET.html">list-project-memberdetails (GET)</a>
   */
  list_project_memberdetails("/projects/{group}/memberdetails"),

  /**
   * Get a project <code>/projects/{group}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-project-GET.html">get-project (GET)</a>
   */
  get_project("/projects/{group}"),

  /**
   * List the children projects/groups the member is registered to - should use URL of list-subprojects-deprecated
   * <code>/members/{member}/projects/{group}/subprojectlist</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-subprojects-GET.html">list-subprojects (GET)</a>
   */
  list_subprojects("/members/{member}/projects/{group}/subprojectlist"),

  /**
   * List the top level projects the member is registered to - should use URL of list-projects-deprecated
   * <code>/members/{member}/projectlist</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-projects-GET.html">list-projects (GET)</a>
   */
  list_projects("/members/{member}/projectlist"),

  /**
   * List the projects the member is registered to (as a tree)
   * <code>/members/{member}/projecttree</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-projecttree-GET.html">list-projecttree (GET)</a>
   */
  list_projecttree("/members/{member}/projecttree"),

  /**
   * List the sub-projects the member is registered to (as a tree)
   * <code>/members/{member}/projects/{group}/subprojecttree</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-subprojecttree-GET.html">list-subprojecttree (GET)</a>
   */
  list_subprojecttree("/members/{member}/projects/{group}/subprojecttree"),

  /**
   * Launch an archive thread for the project
   * <code>/members/{member}/projects/{group}/archive</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/archive-project-GET.html">archive-project (GET)</a>
   */
  archive_project("/members/{member}/projects/{group}/archive"),

  /**
   * Launch a rename thread for the project <code>/members/{member}/projects/{group}/rename</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/rename-project-GET.html">rename-project (GET)</a>
   */
  rename_project("/members/{member}/projects/{group}/rename"),

  /**
   * Launch an unarchive thread for the project <code>/members/{member}/projects/{group}/unarchive</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/unarchive-project-GET.html">unarchive-project (GET)</a>
   */
  unarchive_project("/members/{member}/projects/{group}/unarchive"),

  /**
   * Get a group's group folders <code>/groups/{group}/groupfolders</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-groupfolders-GET.html">list-groupfolders (GET)</a>
   */
  list_groupfolders("/groups/{group}/groupfolders"),

  /**
   * Create a group folder <code>/groups/{group}/groupfolders/create</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-groupfolder-GET.html">create-groupfolder (GET)</a>
   */
  create_groupfolder("/groups/{group}/groupfolders/create"),

  /**
   * Get a group folder with URL <code>/groups/{group}/groupfolders/forurl</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-groupfolder-forurl-GET.html">get-groupfolder-forurl (GET)</a>
   */
  get_groupfolder_forurl("/groups/{group}/groupfolders/forurl"),

  /**
   * Get a group folder with ID and sharing details (that's why the member on the URL) <code>/members/{member}/groups/{group}/groupfolders/{groupfolderid}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-groupfolder-sharing-GET.html">get-groupfolder-sharing (GET)</a>
   */
  get_groupfolder_sharing("/members/{member}/groups/{group}/groupfolders/{groupfolderid}"),

  /**
   * Edit a group folder <code>/groups/{group}/groupfolders/{id}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-groupfolder-GET.html">edit-groupfolder (GET)</a>
   */
  edit_groupfolder("/groups/{group}/groupfolders/{id}"),

  /**
   * Creates a group <code>/members/{member}/groups</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-group-GET.html">create-group (GET)</a>
   */
  create_group("/members/{member}/groups"),

  /**
   * Edits a group <code>/members/{member}/groups/{group}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-group-GET.html">edit-group (GET)</a>
   */
  edit_group("/members/{member}/groups/{group}"),

  /**
   * Get a group <code>/groups/{group}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-group-GET.html">get-group (GET)</a>
   */
  group("/groups/{group}"),

  /**
   * Get the groups visible to this member <code>/members/{member}/visiblegroups</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-visible-groups-GET.html">list-visible-groups (GET)</a>
   */
  list_visible_groups("/members/{member}/visiblegroups"),

  /**
   * Get group member details <code>/members/{member}/groups/{group}/memberdetails</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-group-memberdetails-GET.html">get-group-memberdetails (GET)</a>
   */
  get_group_memberdetails("/members/{member}/groups/{group}/memberdetails"),

  /**
   * Get group properties <code>/groups/{group}/properties</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-group-properties-GET.html">get-group-properties (GET)</a>
   */
  get_group_properties("/groups/{group}/properties"),

  /**
   * Start a publish job <code>/members/{member}/groups/{group}/publish/start</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/start-group-publish-GET.html">start-group-publish (GET)</a>
   */
  start_group_publish("/members/{member}/groups/{group}/publish/start"),

  /**
   * Start a publish job
   * <code>/members/{member}/groups/{group}/uris/{uri}/publish/start</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/start-uri-publish-GET.html">start-uri-publish (GET)</a>
   */
  start_uri_publish("/members/{member}/groups/{group}/uris/{uri}/publish/start"),

  /**
   * Status of a publish job
   * <code>/members/{member}/publish/check</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/check-group-publish-GET.html">check-group-publish (GET)</a>
   */
  check_group_publish("/members/{member}/publish/check"),

  /**
   * Cancelling a publish job
   * <code>/members/{member}/publish/cancel</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/cancel-group-publish-GET.html">cancel-group-publish (GET)</a>
   */
  cancel_group_publish("/members/{member}/publish/cancel"),

  /**
   * Status of a publish job
   * <code>/publish/checkall</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/checkall-publish-GET.html">checkall-publish (GET)</a>
   */
  checkall_publish("/publish/checkall"),

  /**
   * "Pause/continue" a scheduled publish job
   * <code>/publish/schedule</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/schedule-publish-action-GET.html">schedule-publish-action (GET)</a>
   */
  schedule_publish_action("/publish/schedule"),

  /**
   * Search for members in the group
   * <code>/groups/{group}/members/find</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/find-group-members-GET.html">find-group-members (GET)</a>
   */
  find_group_members("/groups/{group}/members/find"),

  /**
   * Returns the list of members in the group
   * <code>/groups/{group}/members/alldetails</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-group-members-details-GET.html">list-group-members-details (GET)</a>
   */
  list_group_members_details("/groups/{group}/members/alldetails"),

  /**
   * Deregister a member
   * <code>/groups/{group}/members/{member}/deregister</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/deregister-group-member-GET.html">deregister-group-member (GET)</a>
   */
  deregister_group_member("/groups/{group}/members/{member}/deregister"),

  /**
   * Manage a membership
   * <code>/groups/{group}/members/{member}/manage</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/manage-group-member-GET.html">manage-group-member (GET)</a>
   */
  manage_group_member("/groups/{group}/members/{member}/manage"),

  /**
   * Autocomplete for a specific field
   * <code>/groups/{group}/autocomplete/{field}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/autocomplete-group-field-GET.html">autocomplete-group-field (GET)</a>
   */
  autocomplete_group_field("/groups/{group}/autocomplete/{field}"),

  /**
   * Autocomplete <code>/groups/{group}/autocomplete</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/autocomplete-group-GET.html">autocomplete-group (GET)</a>
   */
  autocomplete_group("/groups/{group}/autocomplete"),

  /**
   * Autosuggest a query <code>/groups/{group}/autosuggest</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/autosuggest-group-GET.html">autosuggest-group (GET)</a>
   */
  autosuggest_group("/groups/{group}/autosuggest"),

  /**
   * Service supporting the omnibox <code>/groups/{group}/omnibox</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/omnibox-group-GET.html">omnibox-group (GET)</a>
   */
  omnibox_group("/groups/{group}/omnibox"),

  /**
   * Autosuggest a query for a specific field <code>/groups/{group}/autosuggest/fields</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/autosuggest-group-fields-GET.html">autosuggest-group-fields (GET)</a>
   */
  autosuggest_group_fields("/groups/{group}/autosuggest/fields"),

  /**
   * Returns the facets for a field <code>/groups/{group}/facet</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-group-facet-GET.html">get-group-facet (GET)</a>
   */
  get_group_facet("/groups/{group}/facet"),

  /**
   * Gets the publish config for a group <code>/members/{member}/groups/{group}/publishconfig</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-group-publish-config-GET.html">get-group-publish-config (GET)</a>
   */
  get_group_publish_config("/members/{member}/groups/{group}/publishconfig"),

  /**
   * Error reporting at group level <code>/groups/{group}/error/report</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/report-group-error-GET.html">report-group-error (GET)</a>
   */
  report_group_error("/groups/{group}/error/report"),

  /**
   * Apply sharing for a group <code>/members/{member}/groups/{group}/applyshare</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/apply-group-share-GET.html">apply-group-share (GET)</a>
   */
  apply_group_share("/members/{member}/groups/{group}/applyshare"),

  /**
   * Convert documents to PSML for a group <code>/members/{member}/groups/{group}/converttopsml</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/convert-to-psml-GET.html">convert-to-psml (GET)</a>
   */
  convert_to_psml("/members/{member}/groups/{group}/converttopsml"),

  /**
   * Resolve PS standard documents <code>/members/{member}/resolvestandard</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/resolve-standard-GET.html">resolve-standard (GET)</a>
   */
  resolve_standard("/members/{member}/resolvestandard"),

  /**
   * Find images <code>/groups/{group}/images/find</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/find-group-image-GET.html">find-group-image (GET)</a>
   */
  find_group_image("/groups/{group}/images/find"),

  /**
   * Start group indexing <code>/members/{member}/groups/{group}/index/start</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/start-group-index-GET.html">start-group-index (GET)</a>
   */
  start_group_index("/members/{member}/groups/{group}/index/start"),

  /**
   * Group's index status <code>/groups/{group}/index/status</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-group-index-status-GET.html">get-group-index-status (GET)</a>
   */
  get_group_index_status("/groups/{group}/index/status"),

  /**
   * Clear group's index <code>/groups/{group}/index/clear</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/clear-group-index-GET.html">clear-group-index (GET)</a>
   */
  clear_group_index("/groups/{group}/index/clear"),

  /**
   * Launch an archive thread for the group <code>/members/{member}/groups/{group}/archive</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/archive-group-GET.html">archive-group (GET)</a>
   */
  archive_group("/members/{member}/groups/{group}/archive"),

  /**
   * Launch a rename thread for the group <code>/members/{member}/groups/{group}/rename</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/rename-group-GET.html">rename-group (GET)</a>
   */
  rename_group("/members/{member}/groups/{group}/rename"),

  /**
   * Launch an unarchive thread for the group <code>/members/{member}/groups/{group}/unarchive</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/unarchive-group-GET.html">unarchive-group (GET)</a>
   */
  unarchive_group("/members/{member}/groups/{group}/unarchive"),

  /**
   * Start server indexing
   * <code>/members/{member}/index/start</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/member-server-index-start-GET.html">member-server-index-start (GET)</a>
   */
  member_server_index_start("/members/{member}/index/start"),

  /**
   * Status of server indexing <code>/members/{member}/index/status</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/member-server-index-status-GET.html">member-server-index-status (GET)</a>
   */
  member_server_index_status("/members/{member}/index/status"),

  /**
   * Gets the publish config for all URIs in a group <code>/members/{member}/groups/{group}/uris/publishconfig</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uris-publish-config-GET.html">get-uris-publish-config (GET)</a>
   */
  get_uris_publish_config("/members/{member}/groups/{group}/uris/publishconfig"),

  /**
   * Gets the publish config for a URI <code>/members/{member}/groups/{group}/uris/{uri}/publishconfig</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uri-publish-config-GET.html">get-uri-publish-config (GET)</a>
   */
  get_uri_publish_config("/members/{member}/groups/{group}/uris/{uri}/publishconfig"),

  /**
   * List URIs for URL <code>/groups/{group}/uris/forurl/uris</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-uri-uris-forurl-GET.html">list-uri-uris-forurl (GET)</a>
   */
  list_uri_uris_forurl("/groups/{group}/uris/forurl/uris"),

  /**
   * List URIs for URI <code>/groups/{group}/uris/{uri}/uris</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-uri-uris-GET.html">list-uri-uris (GET)</a>
   */
  list_uri_uris("/groups/{group}/uris/{uri}/uris"),

  /**
   * Load a host's external URIs <code>/groups/{group}/hosts/{host}/externaluris</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-host-externaluris-GET.html">list-host-externaluris (GET)</a>
   */
  list_host_externaluris("/groups/{group}/hosts/{host}/externaluris"),

  /**
   * Load a URI's children external URIs <code>/groups/{group}/externaluris/forurl/externaluris</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-externaluri-externaluris-forurl-GET.html">list-externaluri-externaluris-forurl (GET)</a>
   */
  list_externaluri_externaluris_forurl("/groups/{group}/externaluris/forurl/externaluris"),

  /**
   * Load a URI's children external URIs <code>/groups/{group}/externaluris/{uri}/externaluris</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-externaluri-externaluris-GET.html">list-externaluri-externaluris (GET)</a>
   */
  list_externaluri_externaluris("/groups/{group}/externaluris/{uri}/externaluris"),

  /**
   * Load a single URI object <code>/groups/{group}/uris/forurl</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uri-forurl-GET.html">get-uri-forurl (GET)</a>
   */
  get_uri_forurl("/groups/{group}/uris/forurl"),

  /**
   * Load a single URI object <code>/groups/{group}/uris/{uri}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uri-GET.html">get-uri (GET)</a>
   */
  get_uri("/groups/{group}/uris/{uri}"),

  /**
   * Load a URI object with sharing information <code>/members/{member}/groups/{group}/uris/forurl</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uri-sharing-forurl-GET.html">get-uri-sharing-forurl (GET)</a>
   */
  get_uri_sharing_forurl("/members/{member}/groups/{group}/uris/forurl"),

  /**
   * Get or edit a URI <code>/members/{member}/groups/{group}/uris/{uri}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uri-sharing-GET.html">get-uri-sharing (GET)</a>
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-uri-PATCH.html">edit-uri (PATCH)</a>
   */
  member_uri("/members/{member}/groups/{group}/uris/{uri}"),

  /**
   * Create an external URI <code>/members/{member}/groups/{group}/externaluris</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-externaluri-GET.html">create-externaluri (GET)</a>
   */
  create_externaluri("/members/{member}/groups/{group}/externaluris"),

  /**
   * Edit an external URI <code>/members/{member}/groups/{group}/externaluris/{uri}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-externaluri-GET.html">edit-externaluri (GET)</a>
   */
  edit_externaluri("/members/{member}/groups/{group}/externaluris/{uri}"),

  /**
   * Archive an external URI <code>/members/{member}/groups/{group}/externaluris/{uri}/archive</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/archive-externaluri-GET.html">archive-externaluri (GET)</a>
   */
  archive_externaluri("/members/{member}/groups/{group}/externaluris/{uri}/archive"),

  /**
   * Unarchive an external URI <code>/members/{member}/groups/{group}/externaluris/{uri}/unarchive</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/unarchive-externaluri-GET.html">unarchive-externaluri (GET)</a>
   */
  unarchive_externaluri("/members/{member}/groups/{group}/externaluris/{uri}/unarchive"),

  /**
   * Load a URI's versions <code>/groups/{group}/uris/{uri}/versions</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-uri-versions-GET.html">list-uri-versions (GET)</a>
   */
  list_uri_versions("/groups/{group}/uris/{uri}/versions"),

  /**
   * Load a URI's workflow <code>/groups/{group}/uris/{uri}/workflow</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uri-workflow-GET.html">get-uri-workflow (GET)</a>
   */
  get_uri_workflow("/groups/{group}/uris/{uri}/workflow"),

  /**
   * Resolve references for a group <code>/members/{member}/groups/{group}/resolverefs</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/resolve-group-refs-GET.html">resolve-group-refs (GET)</a>
   */
  resolve_group_refs("/members/{member}/groups/{group}/resolverefs"),

  /**
   * Resolve references for a group folder <code>/members/{member}/groups/{group}/groupfolders/{guri}/resolverefs</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/resolve-groupfolder-refs-GET.html">resolve-groupfolder-refs (GET)</a>
   */
  resolve_groupfolder_refs("/members/{member}/groups/{group}/groupfolders/{guri}/resolverefs"),

  /**
   * Resolve References for a folder <code>/members/{member}/groups/{group}/uris/{uri}/resolverefs</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/resolve-folder-refs-GET.html">resolve-folder-refs (GET)</a>
   */
  resolve_folder_refs("/members/{member}/groups/{group}/uris/{uri}/resolverefs"),

  /**
   * Load a URI's xrefs <code>/groups/{group}/uris/{uri}/xrefs</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-uri-xrefs-GET.html">list-uri-xrefs (GET)</a>
   */
  list_uri_xrefs("/groups/{group}/uris/{uri}/xrefs"),

  /**
   * Load a URI's xref tree <code>/groups/{group}/uris/{uri}/xreftree</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-uri-xreftree-GET.html">list-uri-xreftree (GET)</a>
   */
  list_uri_xreftree("/groups/{group}/uris/{uri}/xreftree"),

  /**
   * Create a new workflow <code>/members/{member}/groups/{group}/uris/{uri}/workflow</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-uri-workflow-GET.html">create-uri-workflow (GET)</a>
   */
  create_uri_workflow("/members/{member}/groups/{group}/uris/{uri}/workflow"),

  /**
   * Create a new version <code>/members/{member}/groups/{group}/uris/{uri}/versions</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-uri-version-GET.html">create-uri-version (GET)</a>
   */
  create_uri_version("/members/{member}/groups/{group}/uris/{uri}/versions"),

  /**
   * Archive a URI's version <code>/members/{member}/groups/{group}/uris/{uri}/versions/{versionid}/archive</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/archive-uri-version-GET.html">archive-uri-version (GET)</a>
   */
  archive_uri_version("/members/{member}/groups/{group}/uris/{uri}/versions/{versionid}/archive"),

  /**
   * Create a new xref <code>/members/{member}/groups/{group}/uris/{uri}/xrefs</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-uri-xref-GET.html">create-uri-xref (GET)</a>
   */
  create_uri_xref("/members/{member}/groups/{group}/uris/{uri}/xrefs"),

  /**
   * Archive a URI's xref <code>/members/{member}/groups/{group}/uris/{uri}/xrefs/{xrefid}/archive</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/archive-uri-xref-GET.html">archive-uri-xref (GET)</a>
   */
  archive_uri_xref("/members/{member}/groups/{group}/uris/{uri}/xrefs/{xrefid}/archive"),

  /**
   * Start folder indexing <code>/members/{member}/groups/{group}/uris/{uri}/index/start</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/start-uri-index-GET.html">start-uri-index (GET)</a>
   */
  start_uri_index("/members/{member}/groups/{group}/uris/{uri}/index/start"),

  /**
   * Load Timeline for a URI <code>/groups/{group}/uris/{uri}/history</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uri-history-GET.html">get-uri-history (GET)</a>
   */
  get_uri_history("/groups/{group}/uris/{uri}/history"),

  /**
   * Create a PSML document <code>/members/{member}/groups/{group}/documents</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-document-GET.html">create-document (GET)</a>
   */
  create_document("/members/{member}/groups/{group}/documents"),
  create_document_forurl("/members/{member}/groups/{group}/documents/forurl"),
  create_document_foruri("/members/{member}/groups/{group}/uris/{uri}/documents"),

  /**
   * Duplicate a PSML document <code>/members/{member}/groups/{group}/uris/{uri}/duplicate</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/duplicate-psml-GET.html">duplicate-psml (GET)</a>
   */
  duplicate_psml("/members/{member}/groups/{group}/uris/{uri}/duplicate"),

  /**
   * Create a folder <code>/members/{member}/groups/{group}/folders</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-folder-POST.html">create-folder (POST)</a>
   */
  create_folder("/members/{member}/groups/{group}/folders"),

  /**
   * Create a folder <code>/members/{member}/groups/{group}/folders</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-folder-forurl-POST.html">create-folder-forurl (POST)</a>
   */
  create_folder_forurl("/members/{member}/groups/{group}/folders/forurl"),

  /**
   * Create a folder <code>/members/{member}/groups/{group}/folders</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-folder-foruri-POST.html">create-folder-foruri (POST)</a>
   */
  create_folder_foruri("/members/{member}/groups/{group}/uris/{uri}/folders"),

  /**
   * Returns the schemas available for a URI <code>/groups/{group}/uris/{uri}/schemas</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uri-schemas-GET.html">get-uri-schemas (GET)</a>
   */
  get_uri_schemas("/groups/{group}/uris/{uri}/schemas"),

  /**
   * Returns the index entry for a URI <code>/groups/{group}/uris/{uri}/index</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uri-index-GET.html">get-uri-index (GET)</a>
   */
  get_uri_index("/groups/{group}/uris/{uri}/index"),

  /**
   * Edit inline label <code>/members/{member}/groups/{group}/uris/{uri}/editinlinelabel</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-psml-inlinelabel-GET.html">edit-psml-inlinelabel (GET)</a>
   */
  edit_psml_inlinelabel("/members/{member}/groups/{group}/uris/{uri}/editinlinelabel"),

  /**
   * Edit property <code>/members/{member}/groups/{group}/uris/{uri}/editpsmlproperty</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-psml-property-GET.html">edit-psml-property (GET)</a>
   */
  edit_psml_property("/members/{member}/groups/{group}/uris/{uri}/editpsmlproperty"),

  /**
   * Create New Fragment <code>/members/{member}/groups/{group}/uris/{uri}/fragments</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/add-uri-fragment-GET.html">add-uri-fragment (GET)</a>
   */
  add_uri_fragment("/members/{member}/groups/{group}/uris/{uri}/fragments"),

  /**
   * Move a Fragment <code>/members/{member}/groups/{group}/uris/{uri}/fragments/{fragment}/move</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/move-uri-fragment-GET.html">move-uri-fragment (GET)</a>
   */
  move_uri_fragment("/members/{member}/groups/{group}/uris/{uri}/fragments/{fragment}/move"),

  /**
   * Fragment's Content <code>/members/{member}/groups/{group}/uris/{uri}/fragments/{fragment}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/uri-fragment-GET.html">uri-fragment (GET)</a>
   */
  uri_fragment("/members/{member}/groups/{group}/uris/{uri}/fragments/{fragment}"),

  /**
   * Revert Fragment's Content <code>/members/{member}/groups/{group}/uris/{uri}/fragments/{fragment}/revert</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/revert-uri-fragment-GET.html">revert-uri-fragment (GET)</a>
   */
  revert_uri_fragment("/members/{member}/groups/{group}/uris/{uri}/fragments/{fragment}/revert"),

  /**
   * List Fragment's Edits <code>/groups/{group}/uris/{uri}/fragments/{fragment}/edits</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-fragment-edits-GET.html">list-fragment-edits (GET)</a>
   */
  list_fragment_edits("/groups/{group}/uris/{uri}/fragments/{fragment}/edits"),

  /**
   * List Structure Edits <code>/groups/{group}/uris/{uri}/structure/edits</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-structure-edits-GET.html">list-structure-edits (GET)</a>
   */
  list_structure_edits("/groups/{group}/uris/{uri}/structure/edits"),

  /**
   * Get or edit URI metadata <code>/members/{member}/groups/{group}/uris/{uri}/metadata</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uri-metadata-GET.html">get-uri-metadata (GET)</a>
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-uri-metadata-PATCH.html">edit-uri-metadata (PATCH)</a>
   */
  uri_metadata("/members/{member}/groups/{group}/uris/{uri}/metadata"),

  /**
   * Revert URI Metadata <code>/members/{member}/groups/{group}/uris/{uri}/metadata/revert</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/revert-uri-metadata-GET.html">revert-uri-metadata (GET)</a>
   */
  revert_uri_metadata("/members/{member}/groups/{group}/uris/{uri}/metadata/revert"),

  /**
   * List Metadata Edits <code>/groups/{group}/uris/{uri}/metadata/edits</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-metadata-edits-GET.html">list-metadata-edits (GET)</a>
   */
  list_metadata_edits("/groups/{group}/uris/{uri}/metadata/edits"),

  /**
   * Share latest edits <code>/members/{member}/groups/{group}/uris/{uri}/edits/sharelatest</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/share-uri-latest-edits-GET.html">share-uri-latest-edits (GET)</a>
   */
  share_uri_latest_edits("/members/{member}/groups/{group}/uris/{uri}/edits/sharelatest"),

  /**
   * Share an edit <code>/members/{member}/groups/{group}/uris/{uri}/edits/{editid}/share</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/share-uri-edit-GET.html">share-uri-edit (GET)</a>
   */
  share_uri_edit("/members/{member}/groups/{group}/uris/{uri}/edits/{editid}/share"),

  /**
   * Create an edit note <code>/members/{member}/groups/{group}/uris/{uri}/edits/{editid}/notes</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-edit-note-GET.html">create-edit-note (GET)</a>
   */
  create_edit_note("/members/{member}/groups/{group}/uris/{uri}/edits/{editid}/notes"),

  /**
   * Get config files for a URI <code>/members/{member}/groups/{group}/uris/{uri}/config</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uri-config-GET.html">get-uri-config (GET)</a>
   */
  get_uri_config("/members/{member}/groups/{group}/uris/{uri}/config"),

  /**
   * Get draft edits for a URI <code>/groups/{group}/uris/{uri}/drafts</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uri-drafts-GET.html">get-uri-drafts (GET)</a>
   */
  get_uri_drafts("/groups/{group}/uris/{uri}/drafts"),

  /**
   * Delete a specific draft <code>/members/{member}/groups/{group}/uris/{uri}/drafts/{editid}/delete</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/delete-uri-draft-GET.html">delete-uri-draft (GET)</a>
   */
  delete_uri_draft("/members/{member}/groups/{group}/uris/{uri}/drafts/{editid}/delete"),

  /**
   * Get draft edits for a URI fragment <code>/groups/{group}/uris/{uri}/fragments/{fragment}/drafts</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-uri-fragment-drafts-GET.html">get-uri-fragment-drafts (GET)</a>
   */
  get_uri_fragment_drafts("/groups/{group}/uris/{uri}/fragments/{fragment}/drafts"),

  /**
   * Move a uri <code>/members/{member}/groups/{group}/uris/{uri}/move</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/move-uri-GET.html">move-uri (GET)</a>
   */
  move_uri("/members/{member}/groups/{group}/uris/{uri}/move"),

  /**
   * Archive a uri <code>/members/{member}/groups/{group}/uris/{uri}/archive</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/archive-uri-GET.html">archive-uri (GET)</a>
   */
  archive_uri("/members/{member}/groups/{group}/uris/{uri}/archive"),

  /**
   * Delete a uri <code>/members/{member}/groups/{group}/uris/{uri}/delete</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/delete-uri-GET.html">delete-uri (GET)</a>
   */
  delete_uri("/members/{member}/groups/{group}/uris/{uri}/delete"),

  /**
   * Validation uri <code>/members/{member}/groups/{group}/uris/{uri}/validate</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/validate-uri-GET.html">validate-uri (GET)</a>
   */
  validate_uri("/members/{member}/groups/{group}/uris/{uri}/validate"),

  /**
   * Start folder validation <code>/members/{member}/groups/{group}/uris/{uri}/foldervalidate</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/validate-folder-GET.html">validate-folder (GET)</a>
   */
  validate_folder("/members/{member}/groups/{group}/uris/{uri}/foldervalidate"),

  /**
   * Start export <code>/members/{member}/uris/{uri}/export</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/uri-export-GET.html">uri-export (GET)</a>
   */
  uri_export("/members/{member}/uris/{uri}/export"),

  /**
   * Start export for multiple uris or uri using path <code>/members/{member}/export</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/uris-export-GET.html">uris-export (GET)</a>
   */
  uris_export("/members/{member}/export"),

  /**
   * Start export <code>/members/{member}/groups/{group}/uris/{uri}/export</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/uri-group-export-GET.html">uri-group-export (GET)</a>
   */
  uri_group_export("/members/{member}/groups/{group}/uris/{uri}/export"),

  /**
   * Start export for multiple uris or uri using path <code>/members/{member}/groups/{group}/export</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/uris-group-export-GET.html">uris-group-export (GET)</a>
   */
  uris_group_export("/members/{member}/groups/{group}/export"),

  /**
   * Update host's group URIs and URIs with new scheme/port <code>/members/{member}/hosts/{host}/update</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/update-host-GET.html">update-host (GET)</a>
   */
  update_host("/members/{member}/hosts/{host}/update"),

  /**
   * List external hosts for group <code>/groups/{group}/externaluris/hosts</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-externaluris-hosts-GET.html">list-externaluris-hosts (GET)</a>
   */
  list_externaluris_hosts("/groups/{group}/externaluris/hosts"),

  /**
   * Create or list hosts for server <code>/hosts</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-hosts-GET.html">list-hosts (GET)</a>
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-host-POST.html">create-host (POST)</a>
   */
  hosts("/hosts"),

  /**
   * Edit or delete a host <code>/hosts/{hostname}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-host-PATCH.html">edit-host (PATCH)</a>
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/delete-host-DELETE.html">delete-host (DELETE)</a>
   */
  host("/hosts/{hostname}"),

  /**
   * Create a host alias <code>/hosts/{hostname}/hostaliases</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-hostalias-GET.html">create-hostalias (POST)</a>
   */
  hostaliases("/hosts/{hostname}/hostaliases"),

  /**
   * Edit or delete a host alias <code>/hostaliases/{aliasname}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-hostalias-GET.html">edit-hostalias (PATCH)</a>
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/delete-hostalias-GET.html">delete-hostalias (DELETE)</a>
   */
  hostalias("/hostaliases/{aliasname}"),

  /**
   * Find tasks <code>/members/{member}/tasks/find</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/find-tasks-GET.html">find-tasks (GET)</a>
   */
  find_tasks("/members/{member}/tasks/find"),

  /**
   * Find public tasks <code>/tasks/find</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/find-tasks-public-GET.html">find-tasks-public (GET)</a>
   */
  find_tasks_public("/tasks/find"),

  /**
   * Load a task <code>/members/{member}/tasks/{taskid}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-task-GET.html">get-task (GET)</a>
   */
  get_task("/members/{member}/tasks/{taskid}"),

  /**
   * Load a public task <code>/tasks/{taskid}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-task-public-GET.html">get-task-public (GET)</a>
   */
  get_task_public("/tasks/{taskid}"),

  /**
   * Load a group's discussions <code>/groups/{group}/discussions</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-group-discussions-GET.html">list-group-discussions (GET)</a>
   */
  list_group_discussions("/groups/{group}/discussions"),

  /**
   * Load a group discussion <code>/groups/{group}/discussions/{discussion}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-group-discussion-GET.html">get-group-discussion (GET)</a>
   */
  get_group_discussion("/groups/{group}/discussions/{discussion}"),

  /**
   * Load discussions for a URL <code>/members/{member}/discussions/forurl</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-url-discussions-GET.html">list-url-discussions (GET)</a>
   */
  list_url_discussions("/members/{member}/discussions/forurl"),

  /**
   * Load a discussion <code>/members/{member}/discussions/{discussion}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-discussion-GET.html">get-discussion (GET)</a>
   */
  get_discussion("/members/{member}/discussions/{discussion}"),

  /**
   * Load discussions for a URI <code>/members/{member}/uris/{uri}/discussions</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-uri-discussions-GET.html">list-uri-discussions (GET)</a>
   */
  list_uri_discussions("/members/{member}/uris/{uri}/discussions"),

  /**
   * Load discussions for a URI fragment <code>/members/{member}/uris/{uri}/fragments/{fragment}/discussions</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-uri-fragment-discussions-GET.html">list-uri-fragment-discussions (GET)</a>
   */
  list_uri_fragment_discussions("/members/{member}/uris/{uri}/fragments/{fragment}/discussions"),

  /**
   * Load public discussions for a URL <code>/discussions/forurl</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-url-discussions-public-GET.html">list-url-discussions-public (GET)</a>
   */
  list_url_discussions_public("/discussions/forurl"),

  /**
   * Load a public discussion <code>/discussions/{discussion}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-discussion-public-GET.html">get-discussion-public (GET)</a>
   */
  get_discussion_public("/discussions/{discussion}"),

  /**
   * Find comments <code>/members/{member}/comments/find</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/find-comments-GET.html">find-comments (GET)</a>
   */
  find_comments("/members/{member}/comments/find"),

  /**
   * Find public comments <code>/comments/find</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/find-comments-public-GET.html">find-comments-public (GET)</a>
   */
  find_comments_public("/comments/find"),

  /**
   * List draft comments <code>/members/{member}/comments/draft</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-draft-comments-GET.html">list-draft-comments (GET)</a>
   */
  list_draft_comments("/members/{member}/comments/draft"),

  /**
   * Load public discussions for a URI <code>/uris/{uri}/discussions</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-uri-discussions-public-GET.html">list-uri-discussions-public (GET)</a>
   */
  list_uri_discussions_public("/uris/{uri}/discussions"),

  /**
   * Load discussions for a URI fragment <code>/uris/{uri}/fragments/{fragment}/discussions</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-uri-fragment-discussions-public-GET.html">list-uri-fragment-discussions-public (GET)</a>
   */
  list_uri_fragment_discussions_public("/uris/{uri}/fragments/{fragment}/discussions"),

  /**
   * Create a new group comment from member <code>/members/{member}/groups/{group}/comments</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-comment-GET.html">create-comment (GET)</a>
   */
  create_comment("/members/{member}/groups/{group}/comments"),

  /**
   * Create a new group comment <code>/groups/{group}/comments</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-comment-public-GET.html">create-comment-public (GET)</a>
   */
  create_comment_public("/groups/{group}/comments"),

  /**
   * Create a new group reply from member <code>/members/{member}/groups/{group}/comments/{xlinkid}/reply</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/reply-group-comment-GET.html">reply-group-comment (GET)</a>
   */
  reply_group_comment("/members/{member}/groups/{group}/comments/{xlinkid}/reply"),

  /**
   * Create a new group reply <code>/groups/{group}/comments/{xlinkid}/reply</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/reply-group-comment-public-GET.html">reply-group-comment-public (GET)</a>
   */
  reply_group_comment_public("/groups/{group}/comments/{xlinkid}/reply"),

  /**
   * Create a new reply from member <code>/members/{member}/comments/{xlinkid}/reply</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/reply-comment-GET.html">reply-comment (GET)</a>
   */
  reply_comment("/members/{member}/comments/{xlinkid}/reply"),

  /**
   * Create a new comment for URL from member <code>/members/{member}/comments/forurl</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-comment-url-GET.html">create-comment-url (GET)</a>
   */
  create_comment_url("/members/{member}/comments/forurl"),

  /**
   * Create a new comment for URI from member <code>/members/{member}/uris/{uri}/comments</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-comment-uri-GET.html">create-comment-uri (GET)</a>
   */
  create_comment_uri("/members/{member}/uris/{uri}/comments"),

  /**
   * Create a new comment for URI fragment from member <code>/members/{member}/uris/{uri}/fragments/{fragment}/comments</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-comment-uri-fragment-GET.html">create-comment-uri-fragment (GET)</a>
   */
  create_comment_uri_fragment("/members/{member}/uris/{uri}/fragments/{fragment}/comments"),

  /**
   * Create a new reply <code>/comments/{xlinkid}/reply</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/reply-comment-public-GET.html">reply-comment-public (GET)</a>
   */
  reply_comment_public("/comments/{xlinkid}/reply"),

  /**
   * Create a new comment for URL <code>/comments/forurl</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-comment-url-public-GET.html">create-comment-url-public (GET)</a>
   */
  create_comment_url_public("/comments/forurl"),

  /**
   * Create a new comment for URI <code>/uris/{uri}/comments</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-comment-uri-public-GET.html">create-comment-uri-public (GET)</a>
   */
  create_comment_uri_public("/uris/{uri}/comments"),

  /**
   * Create a new comment for URI fragment <code>/uris/{uri}/fragments/{fragment}/comments</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-comment-uri-fragment-public-GET.html">create-comment-uri-fragment-public (GET)</a>
   */
  create_comment_uri_fragment_public("/uris/{uri}/fragments/{fragment}/comments"),

  /**
   * Get, edit or delete a comment <code>/members/{member}/comments/{commentid}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-comment-GET.html">get-comment (GET)</a>
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/edit-comment-PATCH.html">edit-comment (PATCH)</a>
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/delete-comment-DELETE.html">delete-comment (DELETE)</a>
   */
  comment("/members/{member}/comments/{commentid}"),

  /**
   * Archive comment <code>/members/{member}/comments/{commentid}/archive</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/archive-comment-GET.html">archive-comment (GET)</a>
   */
  archive_comment("/members/{member}/comments/{commentid}/archive"),

  /**
   * Unarchive comment <code>/members/{member}/comments/{commentid}/unarchive</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/unarchive-comment-GET.html">unarchive-comment (GET)</a>
   */
  unarchive_comment("/members/{member}/comments/{commentid}/unarchive"),

  /**
   * Load a public comment <code>/comments/{commentid}</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-comment-public-GET.html">get-comment-public (GET)</a>
   */
  get_comment_public("/comments/{commentid}"),

  /**
   * Deleting the content of a member loading zone <code>/members/{member}/groups/{group}/loadingzone/delete</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/delete-member-loaded-content-GET.html">delete-member-loaded-content (GET)</a>
   */
  delete_member_loaded_content("/members/{member}/groups/{group}/loadingzone/delete"),

  /**
   * Unzipping content in a member loading zone <code>/members/{member}/groups/{group}/loadingzone/unzip</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/unzip-member-loaded-content-GET.html">unzip-member-loaded-content (GET)</a>
   */
  unzip_member_loaded_content("/members/{member}/groups/{group}/loadingzone/unzip"),

  /**
   * Clearing the content in a member loading zone <code>/members/{member}/groups/{group}/loadingzone/clear</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/clear-member-loading-zone-GET.html">clear-member-loading-zone (GET)</a>
   */
  clear_member_loading_zone("/members/{member}/groups/{group}/loadingzone/clear"),

  /**
   * Uploaded URIs <code>/members/{member}/groups/{group}/loadingzone/uris</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-member-loaded-uris-GET.html">get-member-loaded-uris (GET)</a>
   */
  get_member_loaded_uris("/members/{member}/groups/{group}/loadingzone/uris"),

  /**
   * List All Threads <code>/threads</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/list-server-threads-GET.html">list-server-threads (GET)</a>
   */
  list_server_threads("/threads"),

  list_group_threads("/groups/{group}/threads"),

  /**
   * Cancel Thread <code>/groups/{group}/threads/{threadid}/cancel</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/cancel-group-thread-GET.html">cancel-group-thread (GET)</a>
   */
  cancel_group_thread("/groups/{group}/threads/{threadid}/cancel"),

  /**
   * Cancel Thread <code>/threads/{threadid}/cancel</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/cancel-thread-GET.html">cancel-thread (GET)</a>
   */
  cancel_thread("/threads/{threadid}/cancel"),

  /**
   * Thread Progress <code>/groups/{group}/threads/{threadid}/progress</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-group-thread-progress-GET.html">get-group-thread-progress (GET)</a>
   */
  get_group_thread_progress("/groups/{group}/threads/{threadid}/progress"),

  /**
   * Thread Progress <code>/threads/{threadid}/progress</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-thread-progress-GET.html">get-thread-progress (GET)</a>
   */
  get_thread_progress("/threads/{threadid}/progress"),

  /**
   * Thread Logs <code>/groups/{group}/threads/{threadid}/logs</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-group-thread-logs-GET.html">get-group-thread-logs (GET)</a>
   */
  get_group_thread_logs("/groups/{group}/threads/{threadid}/logs"),

  /**
   * List or create resources <code>/groups/{group}/resources</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/group-resources-GET.html">group-resources (GET)</a>
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/create-group-resource-POST.html">create-group-resource (POST)</a>
   */
  group_resources("/groups/{group}/resources"),

  /**
   * Move a resource <code>/groups/{group}/resources/move</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/move-group-resources-GET.html">move-group-resources (GET)</a>
   */
  move_group_resources("/groups/{group}/resources/move"),

  /**
   * Export all the files in the project <code>/groups/{group}/resources/export</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/export-group-resources-GET.html">export-group-resources (GET)</a>
   */
  export_group_resources("/groups/{group}/resources/export"),

  /**
   * Import all the files in the project <code>/groups/{group}/resources/import</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/import-group-resources-GET.html">import-group-resources (GET)</a>
   */
  import_group_resources("/groups/{group}/resources/import"),

  /**
   * Update a resource <code>/groups/{group}/resources/history</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/get-resource-history-GET.html">get-resource-history (GET)</a>
   */
  get_resource_history("/groups/{group}/resources/history"),

  /**
   * Convert document types to PSML <code>/groups/{group}/documenttypes/converttopsml</code>
   *
   * @see <a href="https://dev.pageseeder.com/api/web_services/services/convert-documenttypes-to-psml-GET.html">convert-documenttypes-to-psml (GET)</a>
   */
  convert_documenttypes_to_psml("/groups/{group}/documenttypes/converttopsml");

  /**
   * The service path template.
   */
  private final ServicePath _path;

  private Service(String template) {
    this._path = new ServicePath(template);
  }

  /**
   * @return the underlying template
   */
  public String template() {
    return this._path.template();
  }

  /**
   * @return the number of expected variables for this service.
   */
  public int countVariables() {
    return this._path.count();
  }

  /**
   * Returns the actual path for the service using the specified URI variables
   * and prefixed by "service"
   *
   * @param variables The URI variable to pass.
   *
   * @return The corresponding variable.
   *
   * @see ServicePath#toPath(Object...)
   */
  @SafeVarargs
  public final String toPath(@NonNull Object... variables) {
    return this._path.toPath(variables);
  }

}
