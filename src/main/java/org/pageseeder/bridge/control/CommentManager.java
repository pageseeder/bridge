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
package org.pageseeder.bridge.control;

import java.util.Collections;
import java.util.List;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.FailedPrecondition;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.model.PSComment;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSNotify;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.net.PSHTTPResponseInfo;
import org.pageseeder.bridge.net.PSHTTPResponseInfo.Status;
import org.pageseeder.bridge.xml.PSCommentHandler;

/**
 * A manager for comments and tasks.
 *
 * @author Christophe Lauret
 * @version 0.3.0
 * @since 0.3.0
 */
public final class CommentManager extends Sessionful {

  /**
   * Where the comments are cached.
   */
  private static volatile PSEntityCache<PSComment> cache = EHEntityCache.newInstance("pscomments");

  /**
   * Creates a new manager for PageSeeder comments.
   *
   * @param credentials A valid session to connect to PageSeeder.
   */
  public CommentManager(PSCredentials credentials) {
    super(credentials);
  }

  /**
   * Creates the specified comment in PageSeeder.
   *
   * <p>This method only works for comments posted against a group.
   *
   * @param comment The comment to create
   */
  public boolean createComment(PSComment comment) throws FailedPrecondition, APIException {
    if (comment.getAuthor() == null || comment.getAuthor().member() == null)
      throw new FailedPrecondition("Comment must have a member author");
    return createComment(comment, comment.getAuthor().member());
  }

  /**
   * Creates the specified comment in PageSeeder.
   *
   * <p>This method only works for comments posted against a group.
   *
   * @param comment The comment to create
   * @param creator The comment's creator (may be different from author)
   */
  public boolean createComment(PSComment comment, PSMember creator) throws FailedPrecondition, APIException {
    List<PSGroup> empty = Collections.emptyList();
    return createComment(comment, creator, null, empty);
  }

  /**
   * Creates the specified comment in PageSeeder.
   *
   * @param comment The comment to create
   * @param notify  Whether the comments should be silent, normal or an announcement (may be <code>null</code>)
   * @param group   The group the comment should be posted against
   */
  public boolean createComment(PSComment comment, PSNotify notify, PSGroup group) throws FailedPrecondition, APIException {
    if (comment.getAuthor() == null || comment.getAuthor().member() == null)
      throw new FailedPrecondition("Comment must have a member author");
    return createComment(comment, comment.getAuthor().member(), notify, Collections.singletonList(group));
  }

  /**
   * Creates the specified comment in PageSeeder.
   *
   * @param comment The comment to create
   * @param creator The comment's creator (may be different from author)
   * @param notify  Whether the comments should be silent, normal or an announcement (may be <code>null</code>)
   * @param group   The group the comment should be posted against
   */
  public boolean createComment(PSComment comment, PSMember creator, PSNotify notify, PSGroup group) throws FailedPrecondition, APIException {
    return createComment(comment, creator, notify, Collections.singletonList(group));
  }

  /**
   * Creates the specified comment in PageSeeder.
   *
   * @param comment The comment to create
   * @param notify  Whether the comments should be silent, normal or an announcement (may be <code>null</code>)
   * @param groups  The group the comment should be posted against
   */
  public boolean createComment(PSComment comment, PSNotify notify, List<PSGroup> groups) throws FailedPrecondition, APIException {
    if (comment.getAuthor() == null || comment.getAuthor().member() == null)
      throw new FailedPrecondition("Comment must have a member author");
    return createComment(comment, comment.getAuthor().member(), notify, groups);
  }

  /**
   * Creates the specified comment in PageSeeder.
   *
   * @param comment The comment to create
   * @param creator The comment's creator (may be different from author)
   * @param notify  Whether the comments should be silent, normal or an announcement (may be <code>null</code>)
   * @param groups  The group the comment should be posted against
   */
  public boolean createComment(PSComment comment, PSMember creator, PSNotify notify, List<PSGroup> groups) throws FailedPrecondition, APIException {
    PSHTTPConnector connector = PSHTTPConnectors.createComment(comment, creator, notify, groups).using(this._credentials);
    PSCommentHandler handler = new PSCommentHandler(comment);
    PSHTTPResponseInfo info = connector.post(handler);
    if (info.getStatus() != Status.SUCCESSFUL) return false;
    cache.put(comment);
    return true;
  }

  /**
   * Edits the specified comment in PageSeeder.
   *
   * @param comment The comment to save
   * @param notify  Whether the comments should be silent, normal or an announcement (may be <code>null</code>)
   * @param group   The group the comment should be posted against
   */
  public boolean save(PSComment comment, PSNotify notify, PSGroup group) throws FailedPrecondition, APIException {
    if (comment.getAuthor() == null || comment.getAuthor().member() == null)
      throw new FailedPrecondition("Comment must have a member author");
    return save(comment, comment.getAuthor().member(), notify, Collections.singletonList(group));
  }

  /**
   * Edits the specified comment in PageSeeder.
   *
   * @param comment The comment to save
   * @param editor  The comment's editor (may be different from author)
   * @param notify  Whether the comments should be silent, normal or an announcement (may be <code>null</code>)
   * @param group   The group the comment should be posted against
   */
  public boolean save(PSComment comment, PSMember editor, PSNotify notify, PSGroup group) throws FailedPrecondition, APIException {
    return save(comment, editor, notify, Collections.singletonList(group));
  }

  /**
   * Edits the specified comment in PageSeeder.
   *
   * @param comment The comment to save
   * @param notify  Whether the comments should be silent, normal or an announcement (may be <code>null</code>)
   * @param groups  The groups the comment should be posted against
   */
  public boolean save(PSComment comment, PSNotify notify, List<PSGroup> groups) throws FailedPrecondition, APIException {
    if (comment.getAuthor() == null || comment.getAuthor().member() == null)
      throw new FailedPrecondition("Comment must have a member author");
    return save(comment, comment.getAuthor().member(), notify, groups);
  }

  /**
   * Edits the specified comment in PageSeeder.
   *
   * @param comment The comment to save
   * @param editor  The comment's editor (may be different from author)
   * @param notify  Whether the comments should be silent, normal or an announcement (may be <code>null</code>)
   * @param groups  The groups the comment should be posted against
   */
  public boolean save(PSComment comment, PSMember editor, PSNotify notify, List<PSGroup> groups) throws FailedPrecondition, APIException {
    PSHTTPConnector connector = PSHTTPConnectors.patchComment(comment, editor, notify, groups).using(this._credentials);
    PSCommentHandler handler = new PSCommentHandler(comment);
    PSHTTPResponseInfo info = connector.patch(handler);
    if (info.getStatus() != Status.SUCCESSFUL) return false;
    cache.put(comment);
    return true;
  }

  /**
   * Archives the specified comment in PageSeeder.
   *
   * @param comment The comment to archive
   * @param member  The member archiving the comment
   */
  public boolean archiveComment(PSComment comment, PSMember member) throws FailedPrecondition, APIException {
    PSHTTPConnector connector = PSHTTPConnectors.archiveComment(comment, member).using(this._credentials);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Unarchives the specified comment in PageSeeder.
   *
   * @param comment The comment to archive
   * @param member  The member archiving the comment
   */
  public boolean unarchiveComment(PSComment comment, PSMember member) throws FailedPrecondition, APIException {
    PSHTTPConnector connector = PSHTTPConnectors.unarchiveComment(comment, member).using(this._credentials);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Creates the specified comment in PageSeeder.
   *
   * @param comment The comment to archive
   * @param notify  Whether the comments should be silent, normal or an announcement (may be <code>null</code>)
   * @param groups   The group the comment should be posted against
   * @param xlink   The comment to reply to
   */
  public boolean replyToComment(PSComment comment, PSNotify notify, List<PSGroup> groups, long xlink) throws FailedPrecondition, APIException {
    PSHTTPConnector connector = PSHTTPConnectors.replyToComment(comment, notify, groups, xlink).using(this._credentials);
    PSCommentHandler handler = new PSCommentHandler(comment);
    PSHTTPResponseInfo info = connector.post(handler);
    if (info.getStatus() != Status.SUCCESSFUL) return false;
    cache.put(comment);
    return true;
  }

  /**
   * Identify a comment from a specific comment ID.
   *
   * @param id     The ID of the comment.
   * @param member The member who is trying to access the comment.
   *
   * @return the matching comment (<code>null</code> if not found)
   */
  public PSComment getComment(long id, PSMember member) throws APIException {
    PSComment comment = cache.get(Long.valueOf(id));
    if (comment == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getComment(member, id).using(this._credentials);
      PSCommentHandler handler = new PSCommentHandler();
      connector.get(handler);
      comment = handler.getComment();
      if (comment != null) {
        cache.put(comment);
      }
    }
    return comment;
  }

  /**
   * Find comments using criteria.
   *
   * @param member The member who is trying to access the comments.
   * @param group  The context group
   * @param title  The comments title (can be <code>null</code>)
   * @param type   The comments type (can be <code>null</code>)
   * @param paths  A list of paths of URIs the comments must be attached to (can be <code>null</code>)
   *
   * @return the list of comments found (never <code>null</code>)
   */
  public List<PSComment> findComments(PSMember member, PSGroup group,
      String title, String type, List<String> paths) throws APIException {
    return findComments(member, group, title, type, null, paths);
  }

  /**
   * Find comments using criteria.
   *
   * @param member    The member who is trying to access the comments.
   * @param group     The context group
   * @param title     The comments title (can be <code>null</code>)
   * @param type      The comments type (can be <code>null</code>)
   * @param statuses  A list of statuses the comments must have (can be <code>null</code>)
   * @param paths     A list of paths of URIs the comments must be attached to (can be <code>null</code>)
   *
   * @return the list of comments found (never <code>null</code>)
   */
  public List<PSComment> findComments(PSMember member, PSGroup group, String title,
      String type, List<String> statuses, List<String> paths) throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.findComments(member, group, title, type, statuses, paths).using(this._credentials);
    PSCommentHandler handler = new PSCommentHandler();
    connector.get(handler);
    List<PSComment> comments = handler.listComments();
    // store them for later TODO??
    for (PSComment comment : comments) {
      cache.put(comment);
    }
    return comments;
  }

  /**
   * Find comments using criteria.
   *
   * @deprecated Use {@link #findComments(PSMember, PSGroup,String, String, List)} instead.
   *
   * @param member The member who is trying to access the comments.
   * @param group  The context group
   * @param title  The comments title (can be <code>null</code>)
   * @param type   The comments type (can be <code>null</code>)
   * @param paths  A list of paths of URIs the comments must be attached to (can be <code>null</code>)
   *
   * @return the list of comments found (never <code>null</code>)
   */
  @Deprecated
  public List<PSComment> getCommentsByFilter(PSMember member, PSGroup group,
      String title, String type, List<String> paths) throws APIException {
    return findComments(member, group, title, type, paths);
  }

  /**
   * Find comments using criteria.
   *
   * @deprecated Use {@link #findComments(PSMember, PSGroup,String, String, List, List)} instead.
   *
   * @param member    The member who is trying to access the comments.
   * @param group     The context group
   * @param title     The comments title (can be <code>null</code>)
   * @param type      The comments type (can be <code>null</code>)
   * @param statuses  A list of statuses the comments must have (can be <code>null</code>)
   * @param paths     A list of paths of URIs the comments must be attached to (can be <code>null</code>)
   *
   * @return the list of comments found (never <code>null</code>)
   */
  @Deprecated
  public List<PSComment> getCommentsByFilter(PSMember member, PSGroup group,
      String title, String type, List<String> statuses, List<String> paths) throws APIException {
    return findComments(member, group, title, type, statuses, paths);
  }

  /**
   * Returns the internal cache used for the comments.
   *
   * @return the internal cache used for the comments
   */
  public static PSEntityCache<PSComment> getCache() {
    return cache;
  }

}
