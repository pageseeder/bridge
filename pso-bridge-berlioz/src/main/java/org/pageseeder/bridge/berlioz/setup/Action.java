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
