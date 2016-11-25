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
package org.pageseeder.bridge.berlioz.setup;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.control.GroupManager;
import org.pageseeder.bridge.model.PSProject;
import org.pageseeder.bridge.model.PSResource;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 *
 * @author Christophe Lauret
 *
 * @version 0.1.4
 * @since 0.1.0
 */
public final class UploadResources implements Action {

  private enum Status {failed, uploaded};

  /**
   * Projects the resources should be uploaded to.
   */
  PSProject to;

  public PSProject getTo() {
    return this.to;
  }

  public void setTo(PSProject to) {
    this.to = to;
  }

  @Override
  public void simulate(SetupEnvironment env, final XMLWriter xml) throws SetupException, IOException {
    Path setupDir = env.getRoot().toPath();
    // Templates
    Path template = setupDir.resolve("template/"+this.to.getName());
    if (Files.exists(template)) {
      Files.walkFileTree(template, new UploadSimulator(template, this.to, "/WEB-INF/template", xml));
    }
    // Config
    Path woconfig = setupDir.resolve("woconfig/"+this.to.getName());
    if (Files.exists(woconfig)) {
      Files.walkFileTree(woconfig, new UploadSimulator(woconfig, this.to, "/woconfig", xml));
    }
  }

  @Override
  public void execute(SetupEnvironment env, final XMLWriter xml) throws SetupException, IOException {
    Path setupDir = env.getRoot().toPath();
    GroupManager manager = env.getGroupManager();
    // Templates
    Path template = setupDir.resolve("template/"+this.to.getName());
    if (Files.exists(template)) {
      Files.walkFileTree(template, new ResourceUploader(template, this.to, manager, "/WEB-INF/template", xml));
    }
    // Config
    Path woconfig = setupDir.resolve("woconfig/"+this.to.getName());
    if (Files.exists(woconfig)) {
      Files.walkFileTree(woconfig, new ResourceUploader(woconfig, this.to, manager, "/woconfig", xml));
    }
  }

  @Override
  public String toString() {
    return String.format("upload-resources to %s", this.to);
  }

  private static String toString(Path p) {
    StringBuilder s = new StringBuilder();
    for (Path path : p) {
      if (s.length() > 0) {
        s.append('/');
      }
      s.append(path.toString());
    }
    return s.toString();
  }

  private static class ResourceUploader extends SimpleFileVisitor<Path> {

    private final Path _from;

    private final PSProject _project;

    private final GroupManager _groups;

    private final String _target;

    private final XMLWriter _xml;

    public ResourceUploader(Path from, PSProject project, GroupManager groups, String target, XMLWriter xml) {
      this._from = from;
      this._project = project;
      this._groups = groups;
      this._target = target;
      this._xml = xml;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      String mediatype = Files.probeContentType(file);
      Path f = this._from.relativize(file);
      String pspath = this._target+"/"+this._project.getName()+"/"+UploadResources.toString(f);
      Status status = Status.uploaded;
      try {
        String content = new String(Files.readAllBytes(file));
        PSResource resource = new PSResource(pspath, content);
        this._groups.putResource(this._project, resource, true);
      } catch (APIException ex) {
        status = Status.failed;
        // Nothing else we can do here
      } finally {
        this._xml.openElement("upload-resource");
        this._xml.attribute("path", pspath);
        this._xml.attribute("mediatype", mediatype);
        this._xml.attribute("to", this._project.getName());
        this._xml.attribute("status", status.name());
        this._xml.closeElement();
      }
      return FileVisitResult.CONTINUE;
    };

  }

  private static class UploadSimulator extends SimpleFileVisitor<Path> {

    private final Path _from;

    private final PSProject _project;

    private final String _target;

    private final XMLWriter _xml;

    public UploadSimulator(Path from, PSProject project, String target, XMLWriter xml) {
      this._from = from;
      this._project = project;
      this._target = target;
      this._xml = xml;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      String mediatype = Files.probeContentType(file);
      Path f = this._from.relativize(file);
      this._xml.openElement("upload-resource");
      this._xml.attribute("path", this._target+"/"+this._project.getName()+"/"+UploadResources.toString(f));
      this._xml.attribute("mediatype", mediatype);
      this._xml.attribute("to", this._project.getName());
      this._xml.attribute("status", Status.uploaded.name());
      this._xml.closeElement();
      return FileVisitResult.CONTINUE;
    }

  }

}
