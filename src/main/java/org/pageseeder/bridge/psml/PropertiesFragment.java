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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * A PSML properties fragment.
 *
 * <p>Warning: this version only supports single value properties.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.1.0
 */
public class PropertiesFragment extends FragmentBase implements PSMLFragment {

  /**
   * Properties inside this fragment.
   */
  private final List<Property> properties = new ArrayList<>();

  /**
   * Creates a new properties fragment with the specified ID.
   *
   * @param id The fragment ID.
   */
  public PropertiesFragment(String id) {
    super(id);
  }

  /**
   * Creates a new properties fragment with the specified ID and type.
   *
   * @param id The fragment ID.
   * @param type the fragment type..
   */
  public PropertiesFragment(String id, String type) {
    super(id, type);
  }

  /**
   * Returns the property for the specified name.
   *
   * @param name the property name.
   * @return the first property matching the specified name or <code>null</code>.
   *
   * @throws NullPointerException if the name if <code>null</code>
   */
  public @Nullable Property getProperty(String name) {
    // We do the check for consistency (otherwise would return null when property list is empty)
    Objects.requireNonNull(name, "Name must be supplied");
    for (Property p : this.properties) {
      if (name.equals(p.getName())) return p;
    }
    return null;
  }

  /**
   * @return the actual list of properties in this fragment.
   */
  public List<Property> getProperties() {
    return this.properties;
  }

  /**
   * Adds a property to the fragment.
   *
   * @param p The property to add.
   */
  public void add(Property p) {
    this.properties.add(p);
  }

  /**
   * Sets a property.
   *
   * <p>Note: If multiple properties share the same name ALL of them are changed.
   *
   * @param property The property to set.
   */
  public void set(Property property) {
    for (Property p : this.properties) {
      if (p.getName().equals(property.getName())) {
        p.setValue(property.getValue());
        p.setType(property.getType());
      }
    }
  }

  /**
   * Returns the property for the specified name.
   *
   * @param name the property name.
   * @return the value of the first property matching the specified name or <code>null</code>.
   *
   * @throws NullPointerException if the name if <code>null</code>
   */
  public @Nullable String getPropertyValue(String name) {
    Property p = getProperty(name);
    return p != null ? p.getValue() : null;
  }

  /**
   * Sets a property.
   *
   * <p>Note: If multiple properties share the same name ALL of them are changed.
   *
   * <p>If the property does not exist in the fragment nothing happens.
   *
   * @param name  The name of the property to set.
   * @param value The value of the property to set.
   */
  public void setProperty(String name, @Nullable String value) {
    if (value == null) return;
    for (Property p : this.properties) {
      if (p.getName().equals(name)) {
        p.setValue(value);
      }
    }
  }

  /**
   * Sets a property.
   *
   * <p>Note: If multiple properties share the same name ALL of them are changed.
   *
   * <p>If the property does not exist in the fragment nothing happens.
   *
   * @param name  The name of the property to set.
   * @param value The value of the property to set.
   */
  public void setProperty(String name, int value) {
    for (Property p : this.properties) {
      if (p.getName().equals(name)) {
        p.setValue(Integer.toString(value));
      }
    }
  }

  @Override
  public void toXML(XMLWriter psml) throws IOException {
    String t = type();
    psml.openElement("properties-fragment", true);
    psml.attribute("id", id());
    if (t != null) {
      psml.attribute("type", t);
    }
    for (Property p : this.properties) {
      p.toXML(psml);
    }
    psml.closeElement();
  }
}
