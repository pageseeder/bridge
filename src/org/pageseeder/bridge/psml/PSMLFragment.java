/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.psml;

import com.topologi.diffx.xml.XMLWritable;

/**
 * General interface for all PSML fragments.
 *
 * @author Christophe Lauret
 */
public interface PSMLFragment extends XMLWritable {

  /**
   * @return the fragment ID (unique within the document)
   */
  String id();

  /**
   * @return the fragment type.
   */
  String type();

  /**
   * @return the fragment as PSML including the fragment element and its contents.
   */
  String toPSML();
}
