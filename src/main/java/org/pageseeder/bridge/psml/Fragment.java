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

import org.pageseeder.xmlwriter.XMLWriter;

/**
 * A PSML fragment.
 *
 * @author Christophe Lauret
 * @author Philip Rutherford
 */
public class Fragment extends StandardFragment implements PSMLFragment {

  /**
   * the xml content of fragment
   */
  private String content;
  
  /**
   * Creates a new fragment.
   *
   * @param id The fragment ID.
   */
  public Fragment(String id) {
    super(id);
  }

  /**
   * Creates a new fragment.
   * 
   * @param id the id of fragment
   * @param type the type of fragment
   */
  public Fragment(String id, String type) {
    super(id, type);
  }
  
  /**
   * @param content the xml content within fragment element.
   */
  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement("fragment", true);
    xml.attribute("id", id());
    if (type() != null) {
      xml.attribute("type", type());
    }
    if (this.content != null) {
      xml.writeXML(this.content);
    }
    xml.closeElement();
  }

}
