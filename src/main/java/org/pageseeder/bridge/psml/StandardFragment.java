/*
 *  Copyright (c) 2015 Allette Systems pty. ltd.
 */
package org.pageseeder.bridge.psml;

import java.io.IOException;

import com.topologi.diffx.xml.XMLWriter;

/**
 * A standard PSML fragment.
 * 
 * <p>
 * 
 * @author Ciber Cai
 * @version 22 January 2015 
 */
public class StandardFragment extends FragmentBase implements PSMLFragment {

  /**
   * the xml content of fragment
   */
  private String content;

  /**
   * @param id the id of fragment
   */
  public StandardFragment(String id) {
    super(id);
  }

  /**
   * @param id the id of fragment
   * @param type the type of fragment
   */
  public StandardFragment(String id, String type) {
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
