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
package org.pageseeder.bridge;

import org.pageseeder.bridge.http.Method;
import org.pageseeder.bridge.http.Request;
import org.pageseeder.bridge.http.Response;
import org.pageseeder.bridge.http.Service;
import org.pageseeder.bridge.xml.BasicHandler;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

/**
 * PageSeeder version
 *
 * @author Christophe Lauret
 *
 * @version 0.9.5
 * @since 0.9.5
 */
public final class Version {

  /**
   * The major version (e.g. '5')
   */
  private final int _major;

  /**
   * The build number for that version (e.g. '5900')
   */
  private final int _build;

  /**
   * The full string version of the build (e.g. '5.5900')
   */
  private final String _version;

  /**
   * Create a new version
   *
   * @param major   The major version (e.g. '5')
   * @param build   The build number for that version (e.g. '5900')
   * @param version The full string version of the build (e.g. '5.5900')
   */
  public Version(int major, int build, String version) {
    this._major = major;
    this._build = build;
    this._version = version;
  }

  /**
   * @return The major version (e.g. '5')
   */
  public int major() {
    return this._major;
  }

  /**
   * @return The build number for that version (e.g. '5900')
   */
  public int build() {
    return this._build;
  }

  /**
   * @return The full string version of the build (e.g. '5.5900')
   */
  public String version() {
    return this._version;
  }

  /**
   * Return the PageSeeder version for the specified URL.
   */
  public static Version getVersion(PSConfig config) {
    Response response = new Request(Method.GET, Service.get_version).config(config).response();
    if (response.isSuccessful()) return response.consumeItem(new VersionHandler());
    return null;
  }

  /**
   * Extracts the version details from
   */
  private static class VersionHandler extends BasicHandler<Version> {

    @Override
    public void startElement(String element, Attributes atts) {
      if (isElement("version")) {
        try {
          int major = Integer.parseInt(atts.getValue("major"));
          int build = Integer.parseInt(atts.getValue("build"));
          String version = atts.getValue("string");
          add(new Version(major, build, version));
        } catch (NumberFormatException ex) {
          LoggerFactory.getLogger(Version.class).error("version is not formatted correctly", ex);
        }
      }
    }
  }

}
