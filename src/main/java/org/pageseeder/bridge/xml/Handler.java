/*
 * Copyright 2016 Allette Systems (Australia)
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
package org.pageseeder.bridge.xml;

import java.util.List;

import org.xml.sax.helpers.DefaultHandler;

/**
 * A handler is a SAX handler which can be supplied to a response in order to
 * retrieve an object or a list of objects directly from the PageSeeder XML
 * response.
 *
 * @param <T> the type of object this handler will return.
 */
public abstract class Handler<T> extends DefaultHandler {

  /**
   * @return The list of item that have been processed.
   */
  public abstract List<T> list();

  /**
   * @return the last item that was processed and added to the list.
   */
  public abstract T get();

}
