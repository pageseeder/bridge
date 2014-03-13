/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.control;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.PSEntityCache;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.net.PSHTTPConnector;
import org.pageseeder.bridge.net.PSHTTPConnectors;
import org.pageseeder.bridge.xml.PSMemberHandler;

/**
 * A manager for groups and projects (based on PageSeeder Groups)
 *
 * @author Christophe Lauret
 * @version 0.2.0
 * @since 0.2.0
 */
public final class MemberManager extends Sessionful {

  /**
   * Where the users are cached.
   */
  private static volatile PSEntityCache<PSMember> cache = EHEntityCache.newInstance("psmembers", "email");

  /**
   *
   */
  public MemberManager(PSSession user) {
    super(user);
  }

  /**
   * Returns the member for the specified username
   *
   * @param username The username of that member
   *
   * @return
   *
   * @throws APIException
   */
  public PSMember getByUsername(String username) throws APIException {
    PSMember member = cache.get(username);
    if (member == null) {
      PSHTTPConnector connector = PSHTTPConnectors.getMember(username).using(this.session);
      PSMemberHandler handler = new PSMemberHandler();
      connector.get(handler);
      member = handler.getMember();
      if (member != null)
        cache.put(member);
    }
    return member;
  }

  /**
   * Returns the member for the specified username
   *
   * @param username The username of that member
   *
   * @return
   *
   * @throws APIException
   */
  public void save(PSMember member) throws APIException {
    // TODO Verify
    // XXX Force email change set to true (requires Admin)
    PSHTTPConnector connector = PSHTTPConnectors.editMember(member, true).using(this.session);
    PSMemberHandler handler = new PSMemberHandler(member);
    connector.post(handler);
    member = handler.getMember();
    if (member != null)
      cache.put(member);
  }

  /**
   * Resets the session for this user in PageSeeder ensuring that their session includes all
   * updates made to group membership.
   *
   * <p>This is required after a member has been joined to a group.
   *
   * @throws APIException
   */
  public void resetSession() throws APIException {
    PSHTTPConnector connector = PSHTTPConnectors.resetSession().using(this.session);
    connector.post();
  }

  public static PSEntityCache<PSMember> getCache() {
    return cache;
  }
}
