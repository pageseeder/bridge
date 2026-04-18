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


import org.jspecify.annotations.Nullable;
import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

/**
 * An attachment to the comment.
 *
 * @version 0.12.0
 */
public final class Attachment implements Serializable, XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  /**
   * The URI of the attachment.
   */
  private final URI uri;

  /**
   * The fragment the comment is attached to.
   */
  private final @Nullable String fragment;

  /**
   * @param uri The URI to attach
   */
  public Attachment(URI uri) {
    this.uri = Objects.requireNonNull(uri, "URI is required");
    this.fragment = null;
  }

  /**
   * @param uri      The URI to attach
   * @param fragment The fragment ID to attach it to.
   */
  public Attachment(URI uri, @Nullable String fragment) {
    this.uri = uri;
    this.fragment = fragment;
  }

  /**
   * @return the attached URI
   */
  public URI getURI() {
    return this.uri;
  }

  /**
   * @return the fragment of the URI where the comment is attached
   */
  public @Nullable String getFragment() {
    return this.fragment;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("attachment");
    if (this.fragment != null) xml.attribute("fragment", this.fragment);
    this.uri.toXML(xml);
    xml.closeElement();
  }
}
