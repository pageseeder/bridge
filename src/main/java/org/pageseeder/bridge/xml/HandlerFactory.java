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

import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSProject;
import org.xml.sax.Attributes;

/**
 * Factory for handlers.
 *
 * @author Christophe Lauret
 *
 * @version 0.9.2
 * @since 0.9.2
 */
public final class HandlerFactory {

  /**
   * @return a handler implementation for groups and projects.
   */
  public static Handler<PSGroup> newPSGroupHandler() {
    return new BasicHandler<PSGroup>() {

      @Override
      public void startElement(String element, Attributes atts) {
        if (isElement("group")) {
          PSGroup group = PSEntityFactory.toGroup(atts, new PSGroup());
          add(group);
        } else if (isElement("project")) {
          PSGroup project = PSEntityFactory.toGroup(atts, new PSProject());
          add(project);
        }
      }

      @Override
      public void endElement(String element) {}
    };
  }

  /**
   * @return a handler implementation for groups and projects.
   */
  public static Handler<PSMember> newPSMemberHandler() {
    return new BasicHandler<PSMember>() {

      @Override
      public void startElement(String element, Attributes atts) {
        if (isElement("member")) {
          PSMember m = PSEntityFactory.toMember(atts, null);
          add(m);
        }
      }

      @Override
      public void endElement(String element) {}
    };
  }

}
