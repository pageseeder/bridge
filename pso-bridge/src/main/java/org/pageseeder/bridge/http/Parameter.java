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

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A HTTP parameter
 *
 * @author Christophe Lauret
 * @version 0.9.1
 * @since 0.9.1
 */
public final class Parameter {

  /**
   * The parameter name (not URL encoded)
   */
  private final String name;

  /**
   * The parameter value (not URL encoded)
   */
  private final String value;

  /**
   * Create a new parameter
   *
   * @param name  The parameter name (not URL encoded)
   * @param value The parameter value (not URL encoded)
   */
  public Parameter(String name, String value) {
    this.name = Objects.requireNonNull(name, "Parameter name must not be null");
    this.value = Objects.requireNonNull(value, "Parameter value must not be null");
  }

  /**
   * @return The parameter name (not URL encoded)
   */
  public String name() {
    return this.name;
  }

  /**
   * @return The parameter value (not URL encoded)
   */
  public String value() {
    return this.value;
  }

  public void append(StringBuilder query) {
    query.append(URLEncoder.encode(this.name, StandardCharsets.UTF_8));
    query.append("=").append(URLEncoder.encode(this.value, StandardCharsets.UTF_8));
  }

  @Override
  public String toString() {
    StringBuilder q = new StringBuilder();
    append(q);
    return q.toString();
  }


  public static Parameter newParameter(String parameter) {
    int e = parameter.indexOf('=');
    if (e < 0) {
      String name = URLDecoder.decode(parameter, StandardCharsets.UTF_8);
      return new Parameter(name, "");
    } else {
      String name = URLDecoder.decode(parameter.substring(0, e), StandardCharsets.UTF_8);
      String value = URLDecoder.decode(parameter.substring(e+1), StandardCharsets.UTF_8);
      return new Parameter(name, value);
    }
  }

}
