/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.psml;

import java.io.IOException;

import com.topologi.diffx.xml.XMLWritable;
import com.topologi.diffx.xml.XMLWriter;

/**
 * A single PSML property
 *
 * @author Christophe Lauret
 */
public class Property implements XMLWritable {

  private String name;

  private String type;

  private String title;

  private String value;

  public Property(String name) {
    this.name = name;
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @return the type
   */
  public String getType() {
    return this.type;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return this.value;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @param value the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public void toXML(XMLWriter psml) throws IOException {
    psml.openElement("property");
    psml.attribute("name",  this.name);
    if (this.value != null)
      psml.attribute("value", this.value);
    if (this.title != null)
      psml.attribute("title",  this.title);
    if (this.type != null)
      psml.attribute("datatype",  this.type);
    psml.closeElement();
  }

}
