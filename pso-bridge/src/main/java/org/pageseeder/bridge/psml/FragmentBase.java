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

import org.jspecify.annotations.Nullable;
import org.pageseeder.xmlwriter.XML.NamespaceAware;
import org.pageseeder.xmlwriter.XMLStringWriter;

import java.io.IOException;
import java.util.Objects;

/**
 * Base class for PSML fragments providing common logic for the id and type.
 *
 * <p>This class is designed to be extend to create custom fragment types.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.1.0
 */
public abstract class FragmentBase implements PSMLFragment {

  /**
   * The fragment ID.
   */
  private String id;

  /**
   * The fragment type.
   */
  private @Nullable String type;

  /**
   * Creates a new untyped fragment with the specified ID.
   *
   * @param id The fragment ID.
   */
  protected FragmentBase(String id) {
    this.id = Objects.requireNonNull(id, "Fragment ID cannot be null");
    this.type = null;
  }

  /**
   * Creates a new fragment with the specified ID.
   *
   * @param id   The fragment ID.
   * @param type The fragment type.
   */
  protected FragmentBase(String id, String type) {
    this.id = Objects.requireNonNull(id, "Fragment ID cannot be null");
    this.type = type;
  }

  @Override
  public String id() {
    return this.id;
  }

  @Override
  public @Nullable String type() {
    return this.type;
  }

  @Override
  public String toPSML() {
    try {
      XMLStringWriter xml = new XMLStringWriter(NamespaceAware.No);
      toXML(xml);
      return xml.toString();
    } catch (IOException ex) {
      // Should NEVER occur since we write to a string
      throw new IllegalStateException("Unable to write PSML for fragment", ex);
    }
  }
}
