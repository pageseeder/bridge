/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.net;

/**
 * Type for resource requested on PageSeeder used to simplify the creation of URLs.
 *
 * @author Christophe Lauret
 * @version 0.2.0
 * @since 0.2.0
 */
public enum PSHTTPResourceType {

  /**
   * A PageSeeder Servlet.
   */
  SERVLET,

  /**
   * A PageSeeder Service.
   */
  SERVICE,

  /**
   * Any resource on PageSeeder.
   */
  RESOURCE

}
