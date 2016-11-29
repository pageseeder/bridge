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
package org.pageseeder.bridge.psml;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * A single PSML property.
 *
 * <p>Implementation note: this version only support simple single value properties.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.1.0
 */
public class Property implements XMLWritable {

  /**
   * The name of the property.
   */
  private String name;

  /**
   * The type of property.
   */
  private @Nullable String type;

  /**
   * The title of the property
   */
  private @Nullable String title;

  /**
   * The value of the property
   */
  private @Nullable String value;

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
  public @Nullable String getType() {
    return this.type;
  }

  /**
   * @return the value
   */
  public @Nullable String getValue() {
    return this.value;
  }

  /**
   * @return the title
   */
  public @Nullable String getTitle() {
    return this.title;
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
  public void setType(@Nullable String type) {
    this.type = type;
  }

  /**
   * @param value the value to set
   */
  public void setValue(@Nullable String value) {
    this.value = value;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(@Nullable String title) {
    this.title = title;
  }

  @Override
  public void toXML(XMLWriter psml) throws IOException {
    String v = this.value;
    String t = this.title;
    String d = this.type;
    psml.openElement("property");
    psml.attribute("name",  this.name);
    if (v != null) {
      psml.attribute("value", v);
    }
    if (t != null) {
      psml.attribute("title",  t);
    }
    if (d != null) {
      psml.attribute("datatype",  d);
    }
    psml.closeElement();
  }

}
