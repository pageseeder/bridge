/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.psml;

import java.io.IOException;

import org.pageseeder.xmlwriter.XMLStringWriter;

/**
 * Base class for PSML fragments providing common logic for the id and type.
 *
 * <p>This class is designed to be extend to create custom fragment types.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public abstract class FragmentBase implements PSMLFragment {

  /**
   * The fragment ID.
   */
  private String id;

  /**
   * The fragment type.
   */
  private String type;

  /**
   * Creates a new untyped fragment with the specified ID.
   *
   * @param id The fragment ID.
   */
  public FragmentBase(String id) {
    this.id = id;
    this.type = null;
  }

  /**
   * Creates a new fragment with the specified ID.
   *
   * @param id   The fragment ID.
   * @param type The fragment type.
   */
  public FragmentBase(String id, String type) {
    this.id = id;
    this.type = type;
  }

  @Override
  public String id() {
    return this.id;
  }

  @Override
  public String type() {
    return this.type;
  }

  @Override
  public String toPSML() {
    String psml = null;
    try {
      XMLStringWriter xml = new XMLStringWriter(false);
      toXML(xml);
      psml = xml.toString();
    } catch (IOException ex) {
      // Should NEVER occur since we write to a string
    }
    return psml;
  }
}
