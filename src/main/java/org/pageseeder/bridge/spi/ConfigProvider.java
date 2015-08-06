/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.spi;

import org.pageseeder.bridge.PSConfig;

/**
 * An interface for service providers that provide a PageSeeder configuration.
 *
 * @author Christophe Lauret
 *
 * @version 0.3.1
 * @since 0.3.1
 */
public interface ConfigProvider {

  /**
   * Returns the PageSeeder configuration to use.
   *
   * @return the PageSeeder configuration to use.
   */
  PSConfig getConfig();

}
