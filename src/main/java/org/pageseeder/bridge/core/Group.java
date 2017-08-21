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
package org.pageseeder.bridge.core;

import org.pageseeder.xmlwriter.XMLWritable;

/**
 * A PageSeeder group.
 *
 * <p>The public ID of a group is its name.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.2.0
 */
public final class Group extends BasicGroup implements XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  public Group(long id, GroupID name, String title, String description, String owner) {
    super(id, name, title, description, owner, GroupAccess.MEMBER, false, "");
  }

  public Group(long id, GroupID name, String title, String description, String owner, GroupAccess access, boolean common, String relatedURL) {
    super(id, name, title, description, owner, access, common, relatedURL);
  }

  @Override
  public boolean isProject() {
    return false;
  }

  @Override
  public String toString() {
    return "Group(" + getId() + ":" + getName() + ")";
  }

  public static class Builder extends BasicGroup.Builder {

    public Group build() {
      return new Group(this.id, this.name, this.title, this.description, this.owner, this.access, this.common, this.relatedURL);
    }
  }
}
