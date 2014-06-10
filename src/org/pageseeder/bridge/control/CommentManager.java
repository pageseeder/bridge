/*
 * Copyright (c) 2014 Allette Systems
 */
package org.pageseeder.bridge.control;

import java.util.Collections;
import java.util.List;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.FailedPrecondition;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.model.PSComment;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSNotify;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.net.PSHTTPResponseInfo;
import org.pageseeder.bridge.net.PSHTTPResponseInfo.Status;

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
   * @param session A valid session to connect to PageSeeder.
   */
  public CommentManager(PSSession session) {
    super(session);
  }

  /**
   * Creates the specified comment in PageSeeder.
   *
   * <p>This method only works for comments posted against a group.
   *
   * @param comment The comment to create
   */
  public boolean createComment(PSComment comment) throws FailedPrecondition, APIException {
    List<PSGroup> nogroup = Collections.emptyList();
    PSHTTPConnector connector = PSHTTPConnectors.createComment(comment, null, nogroup).using(this._session);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Creates the specified comment in PageSeeder.
   *
   * @param comment The comment to create
   * @param notify  Whether the comments should be silent, normal or an announcement (may be <code>null</code>)
   * @param group   The group the comment should be posted against
   */
  public boolean createComment(PSComment comment, PSNotify notify, PSGroup group) throws FailedPrecondition, APIException {
    PSHTTPConnector connector = PSHTTPConnectors.createComment(comment, notify, Collections.singletonList(group)).using(this._session);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Creates the specified comment in PageSeeder.
   *
   * @param comment The comment to create
   * @param notify  Whether the comments should be silent, normal or an announcement (may be <code>null</code>)
   * @param groups   The group the comment should be posted against
   */
  public boolean createComment(PSComment comment, PSNotify notify, List<PSGroup> groups) throws FailedPrecondition, APIException {
    PSHTTPConnector connector = PSHTTPConnectors.createComment(comment, notify, groups).using(this._session);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Archives the specified comment in PageSeeder.
   *
   * @param comment The comment to archive
   * @param member  The member archiving the comment
   */
  public boolean archiveComment(PSComment comment, PSMember member) throws FailedPrecondition, APIException {
    PSHTTPConnector connector = PSHTTPConnectors.archiveComment(comment, member).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.unarchiveComment(comment, member).using(this._session);
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
    PSHTTPConnector connector = PSHTTPConnectors.replyToComment(comment, notify, groups, xlink).using(this._session);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
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
