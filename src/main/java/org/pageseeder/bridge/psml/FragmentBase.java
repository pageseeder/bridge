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
import org.pageseeder.xmlwriter.XML.NamespaceAware;
import org.pageseeder.xmlwriter.XMLStringWriter;

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
