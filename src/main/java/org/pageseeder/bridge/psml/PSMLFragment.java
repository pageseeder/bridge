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

import org.pageseeder.xmlwriter.XMLWritable;

/**
 * General interface for all PSML fragments.
 *
 * <p>This interface is used for methods and classes dealing with generic fragments.
 *
 * @author Christophe Lauret
 * @version 0.1.0
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
