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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pageseeder.bridge.PSEntity;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSURI;

/**
 * Computes the path of a PageSeeder service from a URI template.
 *
 * @author Christophe Lauret
 * @version 0.9.2
 * @since 0.9.1
 */
public final class ServicePath {

  /**
   * A valid PageSeeder service URI template.
   */
  private static final Pattern VALID_TEMPLATE =
      Pattern.compile("/(?:(?:(?:[a-z0-9]+)|(?:\\{[a-z]+\\}))/)*(?:(?:[a-z0-9]+)|(?:\\{[a-z]+\\}))");

  /**
   * Matches a URI variable.
   */
  private static final Pattern VARIABLE = Pattern.compile("\\{[a-z]+\\}");

  /**
   * The URI template used for this service.
   */
  private String _template;

  /**
   * The number of variables in the template.
   */
  private int _count;

  // Constructors and factory methods
  // --------------------------------------------------------------------------

  /**
   * Creates a new service path.
   *
   * @param template The URI template used for this service.
   */
  ServicePath(String template) {
    this._template = Objects.requireNonNull(template, "Template must not be null");
    this._count = countVariables(template);
  }

  /**
   * Factory method that validates the template.
   *
   * @param template The URI template path for the service.
   *
   * @throws IllegalArgumentException If the template path is invalid.
   */
  public static ServicePath newServicePath(String template) {
    if (!VALID_TEMPLATE.matcher(template).matches())
      throw new IllegalArgumentException("not a valid template: "+template);
    return new ServicePath(template);
  }

  // Class methods
  // --------------------------------------------------------------------------

  /**
   * Returns the URI template for this service.
   *
   * @return the URI template for this service.
   */
  public String template() {
    return this._template;
  }

  /**
   * Returns the number of URI variables in the URI template.
   *
   * @return the number of URI variables in the URI template.
   */
  public int count() {
    return this._count;
  }

  /**
   * Computes the path from the list of variables.
   *
   * <p>Warning: incompatible change since version 0.9.1, the path is now
   * systematically prefixed by "/service".
   *
   * @param variables The URI variables.
   *
   * @return The computed path prefixed by "/service"
   *
   * @throws IllegalArgumentException If the expected number of variables does not match the argument
   */
  public String toPath(Object... variables) {
    Objects.requireNonNull(variables, "Variables must not be null");
    if (this._count != variables.length)
      throw new IllegalArgumentException("Expected "+this._count+" variables but got "+variables.length);
    StringBuilder url = new StringBuilder("/service");
    int i = 0;
    // URI | member | group
    List<Token> tokens = toTokens(this._template, this._count);
    for (Token t : tokens) {
      if (t instanceof Literal) {
        url.append(t.toString());
      } else {
        url.append(t.toString(variables[i++]));
      }
    }
    return url.toString();
  }

  // Private methods
  // --------------------------------------------------------------------------

  /**
   * Returns the list of tokens in the template.
   *
   * @param template The template
   * @param count    the number of variables in the template.
   *
   * @return The corresponding list of tokens.
   */
  private static List<Token> toTokens(String template, int count) {
    // No variable return single literal token
    if (count == 0)
      return Collections.singletonList((Token)new Literal(template));
    // Parse
    List<Token> tokens = new ArrayList<Token>();
    Matcher m = VARIABLE.matcher(template);
    int start = 0;
    while (m.find()) {
      // any text since the last variable
      if (m.start() > start) {
        String literal = template.substring(start, m.start());
        tokens.add(new Literal(literal));
      }
      // add the variable
      String var = m.group();
      if ("{member}".equals(var)) {
        tokens.add(new MemberVariable());
      } else if ("{group}".equals(var)) {
        tokens.add(new GroupVariable());
      } else if ("{uri}".equals(var)) {
        tokens.add(new URIVariable());
      } else {
        tokens.add(new Variable());
      }
      // update the state variables
      start = m.end();
    }
    // any text left over, including if there were no expansions
    if (start < template.length()) {
      String text = template.substring(start, template.length());
      // support for wild cards only at the end of the string.
      tokens.add(new Literal(text));
    }

    return tokens;
  }

  /**
   * Count the number of variables in the template
   *
   * @param template The URI template
   *
   * @return The number of variables.
   */
  private static int countVariables(String template) {
    int i = 0;
    int count = 0;
    while (true) {
      i = template.indexOf('{', i)+1;
      if (i > 0) {
        count++;
      } else {
        break;
      }
    }
    return count;
  }

  /**
   * A token that makes up the URI template.
   */
  private static interface Token {

    /**
     * Returns the token value for the specified variable.
     *
     * NB Only specified to avoid having to cast the implementation
     */
    public String toString(Object o);

  }

  /**
   * Literal string token to copy verbatim to the output URL.
   */
  private static class Literal implements Token {
    private final String _token;
    public Literal(String t) {
      this._token = t;
    }
    @Override
    public String toString(Object o) {
      return this._token;
    }
    @Override
    public String toString() {
      return this._token;
    }
  }

  /**
   * A member variable
   */
  private static class MemberVariable implements Token {

    @Override
    public String toString(Object o) {
      if (o instanceof Integer || o instanceof Long)
        return o.toString();
      else if (o instanceof PSMember) {
        PSMember member = (PSMember)o;
        if (member.getId() != null)
          return member.getId().toString();
        else if (member.getUsername() != null)
          return '~'+encode(member.getUsername());
        else if (member.getUsername() != null)
          return '~'+encode(member.getEmail());
        else throw new IllegalArgumentException("Member must have an id, username or email to be used as a variable");
      } else {
        String s = Objects.toString(o);
        if (isNumeric(s)) return s;
        else return '~'+encode(s);
      }
    }
  }

  /**
   * A group variable
   */
  private static class GroupVariable implements Token {

    @Override
    public String toString(Object o) {
      if (o instanceof Integer || o instanceof Long) return o.toString();
      else if (o instanceof PSGroup) {
        PSGroup group = (PSGroup)o;
        if (group.getId() != null)
          return group.getId().toString();
        else if (group.getName() != null)
          return '~'+group.getName();
        else throw new IllegalArgumentException("Group must have an id or name to be used as a variable");
      } else {
        String s = o.toString();
        if (isNumeric(s)) return s;
        else return '~'+encode(s);
      }
    }
  }

  /**
   * A URI variable
   */
  private static class URIVariable implements Token {

    @Override
    public String toString(Object o) {
      if (o instanceof Integer || o instanceof Long) return o.toString();
      else if (o instanceof PSURI) {
        PSURI uri = (PSURI)o;
        if (uri.getId() != null)
          return uri.getId().toString();
        else throw new IllegalArgumentException("URI must have an id to be used as a variable");
      } else {
        String s = o.toString();
        if (isNumeric(s)) return s;
        else return encode(s);
      }
    }
  }

  /**
   * A general variable
   */
  private static class Variable implements Token {

    @Override
    public String toString(Object o) {
      if (o instanceof Integer || o instanceof Long) return o.toString();
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

  /**
   * URL encode the string using UTF-8.
   *
   * @param s the string to encode.
   *
   * @return the encoded string.
   */
  private static String encode(String s) {
    try {
      return URLEncoder.encode(s, "UTF-8").replace("+", "%20");
    } catch (UnsupportedEncodingException ex) {
      // shouldn't happen
      throw new IllegalArgumentException();
    }
  }

  /**
   * Indicates whether the specific string is entirely made of digits.
   *
   * @param s the string to test.
   *
   * @return <code>true</code> if the string exclusively includes digits and no longer than 19 characters.
   */
  private static boolean isNumeric(String s) {
    // 19 is the length of Long.MAX_VALUE (9223372036854775807)
    // So even is it's a number it will be parsed as a long
    if (s.length() > 19) return false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c < '0' || c > '9') return false;
    }
    return true;
  }

}
