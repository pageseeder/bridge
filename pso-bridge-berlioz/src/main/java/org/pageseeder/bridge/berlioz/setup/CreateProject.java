/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.setup;

import java.io.IOException;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.control.GroupManager;
import org.pageseeder.bridge.model.GroupOptions;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSProject;

import org.pageseeder.xmlwriter.XMLWriter;

/**
 * Setup action to create a specific project.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.5
 * @since 0.1.0
 */
public final class CreateProject implements Action {

  /** Used for the status of an execution task */
  private enum Status {created, failed, skipped};

  /** The project to create */
  PSProject project = null;

  /** The group options for this project. */
  GroupOptions options = null;

  public CreateProject() {
  }

  public PSProject getProject() {
    return this.project;
  }

  public void setProject(PSProject project) {
    this.project = project;
  }

  public GroupOptions getOptions() {
    return this.options;
  }

  public void setOptions(GroupOptions options) {
    this.options = options;
  }

  @Override
  public void simulate(SetupEnvironment env, XMLWriter xml) throws SetupException, IOException {
    Status status = Status.skipped;
    try {
      status = execute(env, true);
    } finally {
      toXML(xml, this.project, status);
    }
  }

  @Override
  public void execute(SetupEnvironment env, XMLWriter xml) throws SetupException, IOException {
    Status status = Status.skipped;
    try {
      status = execute(env, false);
    } finally {
      toXML(xml, this.project, status);
    }
  }

  @Override
  public String toString() {
    return String.format("create-project: %s with %s", this.project, this.options);
  }

  // private helpers
  // ----------------------------------------------------------------------------------------------

  public Status execute(SetupEnvironment env, boolean simulate) throws SetupException {
    Status status = Status.skipped;
    GroupManager manager = env.getGroupManager();
    PSMember m = env.getMember();
    try {
      PSProject current = manager.getProject(this.project.getName());
      if (current != null) {
        env.putProject(current);
      } else {
        if (!simulate) {
          if (this.options != null) {
            manager.createProject(this.project, m, this.options);
          } else {
            manager.createProject(this.project, m);
          }
        }
        status = Status.created;
      }
    } catch (APIException ex) {
      status = Status.failed;
      throw new SetupException("Unable to create project '"+this.project.getName()+"'", ex);
    }
    return status;
  }

  private void toXML(XMLWriter xml, PSProject project, Status status) throws IOException {
    xml.openElement("create-project");
    xml.attribute("name", this.project.getName());
    xml.attribute("status", status.name());
    xml.closeElement();
  }

}
