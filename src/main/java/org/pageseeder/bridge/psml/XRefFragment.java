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
import java.util.ArrayList;
import java.util.List;

import org.pageseeder.bridge.model.PSXRef;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * A PSML XRef fragment.
 *
 * @author Philip Rutherford
 * @since 0.8.1
 */
public class XRefFragment extends FragmentBase implements PSMLFragment {

  /**
   * XRefs inside this fragment.
   */
  private List<PSXRef> xrefs = new ArrayList<PSXRef>();

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
    psml.openElement("xref-fragment", true);
    psml.attribute("id", id());
    if (type() != null) {
      psml.attribute("type", type());
    }
    for (PSXRef x : this.xrefs) {
      XRefToXML(x, psml);
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
  private void XRefToXML(PSXRef x, XMLWriter psml) throws IOException {
    psml.openElement("blockxref");
    if (x.getTargetURIId() != null)
      psml.attribute("uriid", Long.toString(x.getTargetURIId()));
    if (x.getTargetHref() != null)
      psml.attribute("href", x.getTargetHref());
    if (x.getTargetDocid() != null)
      psml.attribute("docid", x.getTargetDocid());
    if (x.getTargetFragment() != null)
      psml.attribute("frag", x.getTargetFragment());
    if (x.getReverseLink()) {
      psml.attribute("reverselink", "true");
      if (x.getReverseTitle() != null)
        psml.attribute("reversetitle", x.getReverseTitle());
      if (x.getReverseType() != null)
        psml.attribute("reversetype", x.getReverseType().toString());
    } else {
      psml.attribute("reverselink", "false");
    }
    if (x.getTitle() != null)
      psml.attribute("title", x.getTitle());
    if (x.getDisplay() != null)
      psml.attribute("display", x.getDisplay().toString());
    if (x.getType() != null)
      psml.attribute("type", x.getType().toString());
    if (x.getLevel() != null)
      psml.attribute("level", x.getLevel());
    if (x.getLabels() != null && x.getLabels().size() > 0)
      psml.attribute("labels", x.getLabelsAsString());
    psml.closeElement();    
  }
}
