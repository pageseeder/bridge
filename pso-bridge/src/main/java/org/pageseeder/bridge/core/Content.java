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

import org.pageseeder.xmlwriter.XMLWritable;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.io.Serializable;

/**
 *
 *
 * @author Christophe Lauret
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public class Content implements Serializable, XMLWritable {

  public static final String DEFAULT_MEDIATYPE = "text/plain";

  public static final Content EMPTY = new Content("");

  /** The content of the comment (required) */
  private final String content;

  /** The content type of the comment, defaults to 'text/plain' */
  private final String type;

  /**
   * Create a new plain text comment
   *
   * @param content The content
   */
  public Content(String content) {
    this(content, DEFAULT_MEDIATYPE);
  }

  /**
   * Create a new plain text comment
   *
   * @param content the actual content
   * @param type    the media type of the content
   */
  public Content(String content, String type) {
    this.content = content;
    this.type = type;
  }

  public String getContent() {
    return this.content;
  }

  public String getType() {
    return this.type;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("content");
    xml.attribute("type", this.type);
    xml.writeText(this.content);
    xml.closeElement();
  }
}
