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
package org.pageseeder.bridge.berlioz.oauth;

import javax.servlet.http.HttpSession;

import org.pageseeder.bridge.PSToken;
import org.pageseeder.bridge.berlioz.auth.Sessions;
import org.pageseeder.bridge.http.Method;
import org.pageseeder.bridge.http.Request;
import org.pageseeder.bridge.http.Response;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.xml.Handler;
import org.pageseeder.bridge.xml.HandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class
 *
 * @author Christophe Lauret
 */
public final class OAuthUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(PasswordFilter.class);

  public static PSMember retrieve(PSToken token) {
    PSMember member = null;
    try {
      Response response = new Request(Method.GET, "/self").using(token).response();
      Handler<PSMember> handler = HandlerFactory.newPSMemberHandler();
      member = response.consumeItem(handler);
      if (member != null) {
        LOGGER.debug("Found member: {}", member.getUsername());
      }
    } catch (Exception ex) {
      LOGGER.info("Unable to load member from service: {}", ex.getMessage(), ex);
    }
    return member;
  }

  /**
   * Returns the OAuth user in session only if his session has not expired.
   *
   * @param session The HTTP session.
   *
   * @return the current OAuth user.
   */
  public static OAuthUser getOAuthUserInSession(HttpSession session) {
    Object o = session.getAttribute(Sessions.USER_ATTRIBUTE);
    if (o instanceof OAuthUser) {
      OAuthUser user = (OAuthUser)o;
      PSToken token = user.getToken();
      if (token != null && !token.hasExpired()) return user;
    }
    return null;
  }

}
