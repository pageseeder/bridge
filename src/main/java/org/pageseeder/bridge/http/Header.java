package org.pageseeder.bridge.http;

import java.util.Objects;

/**
 * A HTTP header
 *
 * @author Christophe Lauret
 * @version 0.9.1
 * @since 0.9.1
 */
public final class Header {

  /**
   * The parameter name (not URL encoded)
   */
  private final String _name;

  /**
   * The parameter value (not URL encoded)
   */
  private final String _value;

  /**
   * Create a new parameter
   *
   * @param name  The parameter name (not URL encoded)
   * @param value The parameter value (not URL encoded)
   */
  public Header(String name, String value) {
    this._name = Objects.requireNonNull(name, "Header name must not be null");
    this._value = Objects.requireNonNull(value, "Header value must not be null");
  }

  /**
   * @return The parameter name (not URL encoded)
   */
  public String name() {
    return this._name;
  }

  /**
   * @return The parameter value (not URL encoded)
   */
  public String value() {
    return this._value;
  }

  @Override
  public String toString() {
    return this._name+":"+this._value;
  }

}
