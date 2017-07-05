package org.pageseeder.bridge.xml;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.psml.PropertiesFragment;
import org.pageseeder.bridge.psml.Property;
import org.xml.sax.Attributes;

/**
 * This handler returns a properties fragment.
 *
 * <p>PLEASE NOTE: this class is still experimental and only supports of subset of PSML properties!
 *
 * @author Christophe Lauret
 *
 * @version 0.10.6
 * @since 0.10.6
 */
public class PropertiesFragmentHandler extends BasicHandler<PropertiesFragment> {

  /**
   * The current fragment being processed.
   */
  private @Nullable PropertiesFragment fragment = null;

  /**
   * The current property being processed.
   */
  private @Nullable Property property = null;

  /**
   * Create a new fragment handler for document.
   */
  public PropertiesFragmentHandler() {
  }

  @Override
  public void startElement(String element, Attributes atts) {
    if ("property".equals(element)) {
      String name = getString(atts, "name");
      String title = atts.getValue("title");
      String value = atts.getValue("value");
      String datatype = atts.getValue("datatype");
      Property p = new Property(name);
      p.setType(datatype);
      p.setValue(value);
      p.setTitle(title);
      this.property = p;
    } else if ("properties-fragment".equals(element)) {
      String id = getString(atts, "id");
      String type = atts.getValue("type");
      if (type != null) {
        this.fragment = new PropertiesFragment(id, type);
      } else {
        this.fragment = new PropertiesFragment(id);
      }
    }
  }

  @Override
  public void endElement(String element) {
    PropertiesFragment f = this.fragment;
    Property p = this.property;
    if (f != null) {
      if ("properties-fragment".equals(element)) {
        add(f);
      } else if ("property".equals(element) && p != null) {
        f.add(p);
      }
    }
  }
}
