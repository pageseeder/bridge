/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.xml;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSMembership;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for PageSeeder subscription form servlet.
 *
 * @author Christophe Lauret
 * @version 0.3.7
 * @since 0.3.7
 */
public final class SubscriptionFormHandler extends DefaultHandler {

  /** Member element */
  private static final String MEMBER = "mem";

  /** Member's ID element */
  private static final String ID = "id";

  /** Member's surname element */
  private static final String SURNAME = "surname";

  /** Member's username element */
  private static final String USERNAME = "username";

  /** Member's first name element */
  private static final String FIRSTNAME = "firstname";

  /** Member's email element */
  private static final String EMAIL = "memberemail";

  /** Member's groupname element */
  private static final String GROUPNAME = "name";

  /** Member's current group element */
  private static final String CURRENTGROUPS = "currentgroups";

  /** Member's group element */
  private static final String GROUP = "group";

  /** Member's group description element */
  private static final String DESCRIPTION = "description";

  /** State variable to indicate whether to record character data in the buffer (state variable)  */
  private boolean record = false;

  /** A buffer for character data (state variable)  */
  private StringBuilder buffer = new StringBuilder();

  /** List of group the user is a member of (state variable)  */
  private Deque<String> parents = new ArrayDeque<>();

  /** The current group (state variable) */
  private PSGroup group = null;

  /**
   * The current member.
   */
  private PSMember member = null;

  /**
   * The list of memberships for the member.
   */
  private List<PSMembership> memberships = new ArrayList<>();

  /**
   * Create a new handler without a pre-existing member or group.
   *
   * This constructor should be used when only the member or group id is known.
   */
  public SubscriptionFormHandler() {
  }

  @Override
  public void startElement(String uri, String local, String name, Attributes attributes) throws SAXException {
    String parent = this.parents.peek();
    if (MEMBER.equals(name)) {
      this.member = new PSMember();
    } else if (MEMBER.equals(parent)) {
      this.record = ID.equals(name) || SURNAME.equals(name) || USERNAME.equals(name) || FIRSTNAME.equals(name);
    } else if (this.parents.contains(CURRENTGROUPS)) {
      if (GROUP.equals(name)) {
        this.group = new PSGroup();
      } else {
        this.record = GROUPNAME.equals(name) || ID.equals(name) || DESCRIPTION.equals(name);
      }
    } else {
      this.record = EMAIL.equals(name);
    }
    this.parents.push(name);
  }

  @Override
  public void endElement(String uri, String local, String name) throws SAXException {
    String parent = this.parents.pop();
    parent = this.parents.peek();

    // Inside parent
    if (MEMBER.equals(parent)) {
      if (ID.equals(name)) {
        long id = Long.parseLong(this.buffer.toString());
        this.member.setId(id);
      } else if (SURNAME.equals(name)) {
        String surname = this.buffer.toString();
        this.member.setSurname(surname);
      } else if (USERNAME.equals(name)) {
        String username = this.buffer.toString();
        this.member.setUsername(username);
      } else if (FIRSTNAME.equals(name)) {
        String firstname = this.buffer.toString();
        this.member.setFirstname(firstname);
      }
    } else if (EMAIL.equals(name)) {
      String email = this.buffer.toString();
      this.member.setEmail(email);
    }

    // check current group contains the specified group name
    if (this.parents.contains(CURRENTGROUPS)) {
      if (this.group != null) {
        if (ID.equals(name)) {
          long id = Long.parseLong(this.buffer.toString());
          this.group.setId(id);
        } else if (DESCRIPTION.equals(name)) {
          String description = this.buffer.toString();
          this.group.setDescription(description);
        } else if (GROUPNAME.equals(name)) {
          String gname = this.buffer.toString();
          this.group.setName(gname);
        } else if (GROUP.equals(name)) {
          PSMembership membership = new PSMembership(this.group, this.member);
          this.memberships.add(membership);
          this.group = null;
        }
      }
    }
    this.record = false;
    this.buffer.setLength(0);
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (this.record) {
      this.buffer.append(ch, start, length);
    }
  }

  /**
   * Returns the list of memberships for the member.
   *
   * @return the list of memberships.
   */
  public List<PSMembership> getMemberships() {
    return this.memberships;
  }

  /**
   * Returns the member that was before set (before parsing) or that was parsed (after parsing).
   *
   * @return the member
   */
  public PSMember getMember() {
    return this.member;
  }
}
