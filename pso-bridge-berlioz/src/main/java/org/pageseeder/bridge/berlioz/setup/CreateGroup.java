/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.setup;

import java.io.IOException;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.control.GroupManager;
import org.pageseeder.bridge.model.GroupOptions;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;

import org.pageseeder.xmlwriter.XMLWriter;

/**
 * Setup action to create a specific group.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.5
 * @since 0.1.0
 */
public final class CreateGroup implements Action {

  /** Used for the status of an execution task */
  private enum Status {created, failed, skipped};

  /** The group to create */
  PSGroup group = null;

  /** The options to use to create the group */
  GroupOptions options = null;

  public CreateGroup() {
  }

  public PSGroup getGroup() {
    return this.group;
  }

  public void setGroup(PSGroup group) {
    this.group = group;
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
      toXML(xml, this.group, status);
    }
  }

  @Override
  public void execute(SetupEnvironment env, XMLWriter xml) throws SetupException, IOException {
    Status status = Status.skipped;
    try {
      status = execute(env, false);
    } finally {
      toXML(xml, this.group, status);
    }
  }

  @Override
  public String toString() {
    return String.format("create-group: %s with %s", this.group, this.options);
  }



  // private helpers
  // ----------------------------------------------------------------------------------------------

  public Status execute(SetupEnvironment env, boolean simulate) throws SetupException {
    Status status = Status.skipped;
    GroupManager manager = env.getGroupManager();
    PSMember m = env.getMember();
    try {
      PSGroup current = manager.get(this.group.getName());
      if (current != null) {
        env.putGroup(current);
      } else {
        if (!simulate) {
          if (this.options != null) {
            manager.createGroup(this.group, m, this.options);
          } else {
            manager.createGroup(this.group, m);
          }
        }
        status = Status.created;
      }
    } catch (APIException ex) {
      status = Status.failed;
      throw new SetupException("Unable to create group '"+this.group.getName()+"'", ex);
    }
    return status;
  }

  private void toXML(XMLWriter xml, PSGroup group, Status status) throws IOException {
    xml.openElement("create-group");
    xml.attribute("name", this.group.getName());
    xml.attribute("status", status.name());
    xml.closeElement();
  }
}
