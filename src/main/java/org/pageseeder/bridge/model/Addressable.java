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
package org.pageseeder.bridge.model;

import org.eclipse.jdt.annotation.Nullable;

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
  @Nullable String getPath();

  /**
   * @return the path
   */
  String getURL();

}
