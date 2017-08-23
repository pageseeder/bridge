/*
 * Copyright 2017 Allette Systems (Australia)
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

package org.pageseeder.bridge.core;


import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.io.Serializable;

/**
 * The context of the comment which may be either a group or a URI.
 */
public final class Context implements Serializable, XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /** The group the comment is attached to. */
  private final @Nullable Group _group;

  /** The URI the comment is attached to. */
  private final @Nullable URI _uri;

  /** The fragment (for a URI only) */
  private final String _fragment;

  /**
   * Create a group context.
   *
   * @param group The group to use as the context.
   */
  public Context(Group group) {
    this._group = group;
    this._uri = null;
    this._fragment = "";
  }

  /**
   * Create a URI context.
   *
   * @param uri The uri to use as the context.
   */
  public Context(URI uri) {
    this._group = null;
    this._uri = uri;
    this._fragment = "default";
  }

  /**
   * Create a URI context at a particular fragment.
   *
   * @param uri      The uri to use as the context.
   * @param fragment The fragment of the URI to use as context
   */
  public Context(URI uri, @Nullable String fragment) {
    this._group = null;
    this._uri = uri;
    this._fragment = fragment != null? fragment : "default";
  }

  /**
   * @return the group
   */
  public @Nullable Group getGroup() {
    return this._group;
  }

  /**
   * @return the _uri
   */
  public @Nullable URI getUri() {
    return this._uri;
  }

  /**
   * @return the fragment
   */
  public @Nullable String getFragment() {
    return this._fragment;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    Group group = this._group;
    URI uri = this._uri;
    xml.openElement("context");
    if (this._fragment.length() > 0 && !"default".equals(this._fragment)) {
      xml.attribute("fragment", this._fragment);
    }
    if (uri != null) {
      uri.toXML(xml);
    }
    if (group != null) {
      group.toXML(xml);
    }
    xml.closeElement();
  }
}