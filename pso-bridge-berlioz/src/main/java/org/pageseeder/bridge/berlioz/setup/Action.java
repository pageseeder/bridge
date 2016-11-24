/*
 * Copyright (c) 1999-2014 allette systems pty. ltd.
 */
package org.pageseeder.bridge.berlioz.setup;

import java.io.IOException;

import org.pageseeder.xmlwriter.XMLWriter;

/**
 * Classes implementing this interface can perform a setup action.
 *
 * <p>Actions must provide a method to execute on PageSeeder.
 *
 * <p>Each action should also provide a simulation method to simulate the execution of
 * the setup action.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.5
 * @since 0.1.0
 */
public interface Action {

  /**
   * Simulate this action and return the result on the XML writer.
   *
   * @param env The setup environment including PageSeeder managers
   * @param xml The XML to write to.
   *
   * @throws SetupException Any error related to the setup
   * @throws IOException    If thrown while writing on the XML string.
   */
  void simulate(SetupEnvironment env, XMLWriter xml) throws SetupException, IOException;

  /**
   * Execute this action and return the result on the XML writer.
   *
   * @param env The setup environment including PageSeeder managers
   * @param xml The XML to write to.
   *
   * @throws SetupException Any error related to the setup
   * @throws IOException    If thrown while writing on the XML string.
   */
  void execute(SetupEnvironment env, XMLWriter xml) throws SetupException, IOException;

}
