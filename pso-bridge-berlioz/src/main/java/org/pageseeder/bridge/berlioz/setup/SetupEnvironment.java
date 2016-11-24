/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.setup;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.berlioz.auth.PSUser;
import org.pageseeder.bridge.berlioz.auth.Sessions;
import org.pageseeder.bridge.control.GroupManager;
import org.pageseeder.bridge.model.GroupOptions;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.model.PSProject;

/**
 * Defines the environment
 *
 * @author Christophe Lauret
 *
 * @version 0.1.5
 * @since 0.1.5
 */
public final class SetupEnvironment {

  /**
   * Group options defined in this enviroment.
   */
  final Map<String, GroupOptions> _groupOptions = new HashMap<>();

  /**
   * Projects defined in this environment.
   */
  private final Map<String, PSProject> _projects = new HashMap<>();

  /**
   * Groups defined in this environment.
   */
  private final Map<String, PSGroup> _groups = new HashMap<>();

  /**
   * The root of where the setup files are located.
   */
  private File root = null;

  /**
   * The setup user created by the {@link #init()} method.
   */
  private PSUser user = null;

  /**
   * Lazily loaded member corresponding to the setup user.
   */
  private PSMember member = null;

  /**
   * Lazily loaded group manager.
   */
  private GroupManager groupManager = null;

  public void setRoot(File f) {
    this.root = f;
  }

  public File getRoot() {
    return this.root;
  }

  /**
   * Initialises the setup environment for execution or simulation.
   *
   * <p>This method essentially loads the setup user so that managers can be used with the setup
   * session.
   *
   * @throws SetupException If the setup user could not be found
   */
  public void init() throws SetupException {
    if (this.user == null) {
      try {
        this.user = Sessions.getConfiguredUser("bridge.setup");
      } catch (APIException ex) {
        throw new SetupException("Unable to load setup user");
      }
    }
  }

  /**
   * Returns session of the setup user.
   *
   * @return the session of the setup user.
   *
   * @throws IllegalStateException If the {@link #init()} method was not called previously.
   */
  public PSSession getSession() {
    if (this.user == null) throw new IllegalStateException("Environment must be initialiazed first with init()");
    return this.user.getSession();
  }

  /**
   * Returns the member corresponding to the setup user.
   *
   * @return the member corresponding to the setup user.
   *
   * @throws IllegalStateException If the {@link #init()} method was not called previously.
   */
  public PSMember getMember() {
    if (this.user == null) throw new IllegalStateException("Environment must be initialiazed first with init()");
    if (this.member == null) {
      this.member = this.user.toMember();
    }
    return this.user.toMember();
  }

  /**
   * Returns the group manager with the setup session to manage groups and projects.
   *
   * @return the corresponding group manager
   *
   * @throws IllegalStateException If the {@link #init()} method was not called previously.
   */
  public GroupManager getGroupManager() {
    PSSession session = getSession();
    if (this.groupManager == null) {
      this.groupManager = new GroupManager(session);
    }
    return this.groupManager;
  }

  /**
   * Add or update the project to the environment
   *
   * @param project The project to add or update.
   */
  public void putProject(PSProject project) {
    this._projects.put(project.getName(), project);
  }

  /**
   * Add or update the group to the environment
   *
   * @param project The group to add or update.
   */
  public void putGroup(PSGroup group) {
    this._groups.put(group.getName(), group);
  }

  /**
   * Returns the specified project if defined in the environment
   *
   * @param name The project name
   * @return The corresponding project instance or <code>null</code>
   */
  public PSProject getProject(String name) {
    return this._projects.get(name);
  }

  /**
   * Returns the specified group if defined in the environment
   *
   * @param name The group name
   * @return The corresponding group instance or <code>null</code>
   */
  public PSGroup getGroup(String name) {
    return this._groups.get(name);
  }
}
