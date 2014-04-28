/*
 * Copyright (c) 2014 Allette Systems
 */
package org.pageseeder.bridge.control;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.FailedPrecondition;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.model.PSComment;
import org.pageseeder.bridge.model.PSGroup;
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
  private static volatile PSEntityCache<PSGroup> cache = EHEntityCache.newInstance("pscomments");

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
   */
  public boolean createComment(PSComment comment) throws FailedPrecondition, APIException {
    return createComment(comment, null, null);
  }

  /**
   * Creates the specified comment in PageSeeder.
   */
  public boolean createComment(PSComment comment, PSNotify notify, PSGroup group) throws FailedPrecondition, APIException {
    // The author and context determine the service
    PSHTTPConnector connector = PSHTTPConnectors.createComment(comment, notify, group).using(this._session);
    PSHTTPResponseInfo info = connector.post();
    return info.getStatus() == Status.SUCCESSFUL;
  }

  /**
   * Returns the internal cache used for the comments.
   *
   * @return the internal cache used for the comments
   */
  public static PSEntityCache<PSGroup> getCache() {
    return cache;
  }

}
