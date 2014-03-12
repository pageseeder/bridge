/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

/**
 * An addressable PageSeeder entity.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
interface Addressable {

  /**
   * @return the protocol scheme "http" or "https"
   */
  String getScheme();

  /**
   * @return the host
   */
  String getHost();

  /**
   * @return the path
   */
  String getHostURL();

  /**
   * @return the port
   */
  int getPort();

  /**
   * @return the path
   */
  String getPath();

  /**
   * @return the path
   */
  String getURL();

}
