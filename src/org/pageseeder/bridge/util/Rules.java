/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
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
   * Indicates whether it is a valid email.
   *
   * @param email
   */
  public static boolean isEmail(String email) {
    if (email == null || email.isEmpty()) return false;
    if (email.indexOf('@') < 0) return false;
    return EMAIL.matcher(email).matches();
  }
}
