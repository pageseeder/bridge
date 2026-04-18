/*
 * Copyright 2017 Allette Systems (Australia)
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
package org.pageseeder.bridge.xml.sax;

import org.pageseeder.bridge.core.BasicGroup;
import org.pageseeder.bridge.core.Group;
import org.pageseeder.bridge.core.Project;
import org.pageseeder.bridge.xml.BasicHandler;
import org.xml.sax.Attributes;

/**
 * Handler for PageSeeder groups and projects.
 *
 * @author Christophe Lauret
 * @version 0.2.2
 * @since 0.1.0
 */
public final class BasicGroupHandler extends BasicHandler<BasicGroup> {

  @Override
  public void startElement(String element, Attributes attributes) {
    if ("group".equals(element)) {
      Group.Builder builder = new Group.Builder();
      fillUp(builder, attributes);
      add(builder.build());
    } else if ("project".equals(element)) {
      Project.Builder builder = new Project.Builder();
      fillUp(builder, attributes);
      add(builder.build());
    }
  }

  private void fillUp(BasicGroup.Builder builder, Attributes attributes) {
    Long id = getLong(attributes, "id", -1L);
    String name = getString(attributes, "name");
    String title = getString(attributes, "title", "");
    String description = getString(attributes, "description");
    String owner = getString(attributes, "owner");
    String access = getString(attributes, "access");
    String relatedURL = getString(attributes, "relatedurl", "");
    boolean common = "true".equals(getOptionalString(attributes, "common"));

    builder.id(id)
        .description(description)
        .title(title)
        .name(name)
        .owner(owner)
        .access(access)
        .common(common)
        .relatedURL(relatedURL);

  }


}
