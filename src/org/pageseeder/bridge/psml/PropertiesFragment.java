/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.psml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.topologi.diffx.xml.XMLWriter;

/**
 * A PSML properties fragment.
 *
 * <p>Warning: this version only supports single value properties.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public class PropertiesFragment extends FragmentBase implements PSMLFragment {

  /**
   * Properties inside this fragment.
   */
  private final List<Property> properties = new ArrayList<Property>();

  /**
   * Creates a new properties fragment with the specified ID.
   *
   * @param id The fragment ID.
   */
  public PropertiesFragment(String id) {
    super(id);
  }

  /**
   * Returns the property for the specified name.
   *
   * @param name the property name.
   * @return the first property matching the specified name or <code>null</code>.
   *
   * @throws NullPointerException if the name if <code>null</code>
   */
  public Property getProperty(String name) {
    if (name == null) throw new NullPointerException("name");
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
  public String getPropertyValue(String name) {
    Property p = getProperty(name);
    return p != null? p.getValue() : null;
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
  public void setProperty(String name, String value) {
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
    psml.openElement("properties-fragment", true);
    psml.attribute("id", id());
    if (type() != null) {
      psml.attribute("type", type());
    }
    for (Property p: this.properties) {
      p.toXML(psml);
    }
    psml.closeElement();
  }
}
