/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.setup;

import java.io.IOException;
import java.util.List;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.control.GroupManager;
import org.pageseeder.bridge.model.PSGroup;

import org.pageseeder.xmlwriter.XMLWriter;

/**
 * Setup action to add a group to another group as a subgroup.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.5
 * @since 0.1.0
 */
public final class AddSubGroup implements Action {

  /** Used for the status of an execution task */
  private enum Status {added, failed, skipped};

  /** The group to add as a subgroup */
  PSGroup group = null;

  /** The group the subgroup is added to */
  PSGroup to = null;

  public AddSubGroup() {
  }

  public PSGroup getGroup() {
    return this.group;
  }

  public void setGroup(PSGroup group) {
    this.group = group;
  }

  public void setTo(PSGroup to) {
    this.to = to;
  }

  public PSGroup getTo() {
    return this.to;
  }

  @Override
  public void simulate(SetupEnvironment env, XMLWriter xml) throws SetupException, IOException {
    toXML(xml, this.group, this.to, Status.added);
  }

  @Override
  public void execute(SetupEnvironment env, XMLWriter xml) throws SetupException, IOException {
    Status status = Status.skipped;
    GroupManager manager = env.getGroupManager();
    try {
      // check whether the subgroup already exists
      List<PSGroup> subgroups = manager.listSubGroups(this.to);
      boolean add = true;
      for (PSGroup g : subgroups) {
        if (g.getName().equals(this.group.getName())) {
          add = false;
        }
      }
      // If not add the subgroup
      if (add) {
        manager.addSubGroup(this.to, this.group);
        status = Status.added;
      }
    } catch (APIException ex) {
      status = Status.failed;
      throw new SetupException("Unable to add subgroup", ex);
    } finally {
      toXML(xml, this.group, this.to, status);
    }
  }

  @Override
  public String toString() {
    return String.format("add-subgroup: %s to %s", this.group, this.to);
  }


  private void toXML(XMLWriter xml, PSGroup group, PSGroup to, Status status) throws IOException {
    xml.openElement("add-subgroup");
    xml.attribute("name", this.group.getName());
    xml.attribute("to", this.to.getName());
    xml.attribute("status", status.name());
    xml.closeElement();
  }
}
