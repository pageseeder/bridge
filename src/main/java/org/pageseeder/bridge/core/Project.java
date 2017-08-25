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
 * A PageSeeder project.
 *
 * <p>The public ID of a project is its name.
 *
 * @author Christophe Lauret
 */
public final class Project extends BasicGroup implements XMLWritable {

  /** As per recommendation */
  private static final long serialVersionUID = 1L;

  public Project(long id, GroupName name, String title, String description, String owner) {
    super(id, name, title, description, owner, GroupAccess.MEMBER, false, "");
  }

  public Project(long id, GroupName name, String title, String description, String owner, GroupAccess access, boolean common, String relatedURL) {
    super(id, name, title, description, owner, access, common, relatedURL);
  }

  @Override
  public boolean isProject() {
    return true;
  }

  @Override
  public String toString() {
    return "Project(" + this.getId() + ":" + this.getName() + ")";
  }

  public static class Builder extends BasicGroup.Builder {

    public Project build() {
      return new Project(this.id, this.name, this.title, this.description, this.owner, this.access, this.common, this.relatedURL);
    }
  }
}
