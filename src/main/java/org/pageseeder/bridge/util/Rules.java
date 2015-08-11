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
package org.pageseeder.bridge.util;

import java.util.regex.Pattern;

/**
 * Utility class providing rules to applying to various entities and their fielsds.
 *
 * @author christophe Lauret
 */
public final class Rules {

  /**
   * List of characters allowed in the "dot-atom" part of email addresses:
   *
   * <pre>
   *  ALPHA / DIGIT /    ; Printable US-ASCII
   *  "!" / "#" /        ;  characters not including
   *  "$" / "%" /        ;  specials.  Used for atoms.
   *  "&" / "'" /
   *  "*" / "+" /
   *  "-" / "/" /
   *  "=" / "?" /
   *  "^" / "_" /
   *  "`" / "{" /
   *  "|" / "}" /
   *  "~"
   * </pre>
   *
   * @see <a href="http://tools.ietf.org/html/rfc5322#section-3.2.3">Internet Message Format - Atom</a>
   */
  private static final String DOT_ATOM = "a-z0-9!#$%&'*+/=?^_`{|}~-";

  /**
   * Pattern to produce validate the local-part of an email address.
   *
   * @see <a href="http://tools.ietf.org/html/rfc5322#section-3.2.3">Internet Message Format - Atom</a>
   */
  private static final String LOCAL_PART_PATTERN = "["+DOT_ATOM+"]+(?:\\.["+DOT_ATOM+"]+)*";

  /**
   * Pattern to produce validate the domain of an email address.
   *
   * <p>Note this is more permissive than is allowed, for example this expression does not test for
   * top level domains (.com, .net, etc...)
   *
   * @see <a href="http://tools.ietf.org/html/rfc5322#section-3.2.3">Internet Message Format - Atom</a>
   */
  private static final String DOMAIN_PATTERN = "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

  /**
   * Pattern checking email address.
   */
  private static final Pattern EMAIL = Pattern.compile(LOCAL_PART_PATTERN+"@"+DOMAIN_PATTERN, Pattern.CASE_INSENSITIVE);

  /** Utility class */
  private Rules() {
  }

  /**
   * Indicates whether it is a valid email address by looking at its syntax.
   *
   * @param email The email to validate
   *
   * @return <code>true</code> if the email is considered valid from its syntax;
   *         <code>false</code> otherwise.
   */
  public static boolean isEmail(String email) {
    if (email == null || email.isEmpty()) return false;
    if (email.indexOf('@') < 0) return false;
    return EMAIL.matcher(email).matches();
  }

  /**
   * @param mtype the media type to check
   *
   * @return <code>true</code> if it's an XML media type
   */
  public static boolean isXMLMediaType(String mtype) {
    return "text/xml".equals(mtype) ||
        "application/xml".equals(mtype) ||
        "text/xml-external-parsed-entity".equals(mtype) ||
        "application/xml-external-parsed-entity".equals(mtype) ||
        "application/xml-dtd".equals(mtype) ||
        (mtype != null && mtype.endsWith("+xml"));
  }

}
