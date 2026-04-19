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
package org.pageseeder.bridge.psml;

import org.pageseeder.bridge.model.PSXRef;
import org.pageseeder.bridge.model.PSXRef.Display;
import org.pageseeder.bridge.model.PSXRef.Type;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A PSML XRef fragment.
 *
 * @author Philip Rutherford
 *
 * @version 0.12.0
 * @since 0.8.1
 */
public class XRefFragment extends FragmentBase implements PSMLFragment {

  /**
   * XRefs inside this fragment.
   */
  private List<PSXRef> xrefs = new ArrayList<>();

  /**
   * Creates a new XRef fragment with the specified ID.
   *
   * @param id The fragment ID.
   */
  public XRefFragment(String id) {
    super(id);
  }

  /**
   * Creates a new XRef fragment with the specified ID and type.
   *
   * @param id The fragment ID.
   * @param type the fragment type.
   */
  public XRefFragment(String id, String type) {
    super(id, type);
  }

  /**
   * @return the actual list of XRefs in this fragment.
   */
  public List<PSXRef> getXRefs() {
    return this.xrefs;
  }

  /**
   * Set the list of XRefs in this fragment
   *
   * @param xrefs  list of XRefs
   */
  public void setXRefs(List<PSXRef> xrefs) {
    this.xrefs = xrefs;
  }

  /**
   * Adds an XRef to the fragment.
   *
   * @param x The XRef to add.
   */
  public void add(PSXRef x) {
    this.xrefs.add(x);
  }

  @Override
  public void toXML(XMLWriter psml) throws IOException {
    String t = type();
    psml.openElement("xref-fragment", true);
    psml.attribute("id", id());
    if (t != null) {
      psml.attribute("type", t);
    }
    for (PSXRef x : this.xrefs) {
      xrefToXML(x, psml);
    }
    psml.closeElement();
  }

  /**
   * Outputs an XRef as an XML blockxref element
   *
   * @param psml  for writing output
   *
   * @exception IOException  if problem writing XML
   */
  private static void xrefToXML(PSXRef x, XMLWriter psml) throws IOException {
    psml.openElement("blockxref");
    // Target attributes
    Long targetURI = x.getTargetURIId();
    String targetHref = x.getTargetHref();
    String targetDocid = x.getTargetDocid();
    String targetFragment = x.getTargetFragment();
    if (targetURI != null) {
      psml.attribute("uriid", targetURI.toString());
    }
    if (targetHref != null) {
      psml.attribute("href", targetHref);
    }
    if (targetDocid != null) {
      psml.attribute("docid", targetDocid);
    }
    if (targetFragment != null) {
      psml.attribute("frag", targetFragment);
    }

    // Reverse link
    if (x.getReverseLink()) {
      String reverseTitle = x.getReverseTitle();
      Type reverseType = x.getReverseType();
      psml.attribute("reverselink", "true");
      if (reverseTitle != null) {
        psml.attribute("reversetitle", reverseTitle);
      }
      if (reverseType != null) {
        psml.attribute("reversetype", reverseType.toString());
      }
    } else {
      psml.attribute("reverselink", "false");
    }

    // Other attributes
    String title = x.getTitle();
    Display display = x.getDisplay();
    Type type = x.getType();
    Integer level = x.getLevel();
    if (title != null) {
      psml.attribute("title", title);
    }
    if (display != null) {
      psml.attribute("display", display.toString());
    }
    if (type != null) {
      psml.attribute("type", type.toString());
    }
    if (level != null) {
      psml.attribute("level", level);
    }
    if (x.getLabels() != null && !x.getLabels().isEmpty()) {
      psml.attribute("labels", x.getLabelsAsString());
    }
    psml.closeElement();
  }
}
