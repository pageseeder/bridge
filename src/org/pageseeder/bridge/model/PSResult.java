/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A single search result.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public class PSResult implements Serializable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * Fields inside the single result.
   */
  private List<Field> fields = new ArrayList<Field>();

  /**
   * Sole constructor
   */
  public PSResult() {
  }

  /**
   * Add a field to this result.
   *
   * @param field The field to add.
   */
  public void add(Field field){
    this.fields.add(field);
  }

  /**
   * Returns the PageSeeder ID ("psid" field) as a long.
   *
   * @return The corresponding PageSeeder ID
   */
  public Long getPSID() {
    String psid = getValue("psid");
    Long id = null;
    try {
      id = Long.valueOf(psid);
    } catch (NumberFormatException ex) {
      // Unable to parse ID, shuld never happen by specification
    }
    return id;
  }

  /**
   * Returns the value for the given index field name.
   *
   * <p>If there are multiple field values, this method only returns the first one.
   *
   * @param name the name of the index field.
   *
   * @return The corresponding value.
   */
  public String getValue(String name){
    for (Field f : this.fields) {
      if (f._name.equals(name)) return f._value;
    }
    return null;
  }

  /**
   * Returns the value for the given index field name.
   *
   * <p>If there are multiple field values, this method only returns the first one.
   *
   * @param name the name of the property.
   *
   * @return The corresponding value.
   */
  public String getValueOfProperty(String name){
    String fieldname = "psproperty-"+name;
    for (Field f : this.fields) {
      if (f._name.equals(fieldname)) return f._value;
    }
    return null;
  }

  /**
   * Returns the value for the given index field name.
   *
   * <p>If there are multiple field values, this method only returns the first one.
   *
   * @param name the name of the index field.
   *
   * @return The corresponding value.
   */
  public List<String> getValues(String name){
    List<String> values = new ArrayList<String>();
    for (Field f : this.fields) {
      if (f._name.equals(name)) {
        values.add(f._value);
      }
    }
    return values;
  }

  /**
   * @return All fields in this result object.
   */
  public List<Field> getFields() {
    return this.fields;
  }

  /**
   * Individual index fields.
   */
  public static class Field implements Serializable {

    /** As per requirement.*/
    private static final long serialVersionUID = 20140219L;

    /**
     * The name of the index field.
     */
    private final String _name;

    /**
     * The value of the index field.
     */
    private final String _value;

    /**
     * Sole constructor.
     *
     * @param name  the name of the index field.
     * @param value the value of the index field.
     */
    public Field(String name, String value) {
      this._name = name;
      this._value= value;
    }

    /**
     * The name of the index field.
     *
     * @return the name of the index field.
     */
    public String name() {
      return this._name;
    }

    /**
     * The value of the index field.
     *
     * @return the value of the index field.
     */
    public String value() {
      return this._value;
    }

    @Override
    public String toString() {
      return "f:"+this._name+"="+this._value;
    }

  }
}
