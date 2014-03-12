/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.model;

/**
 * A project resource used for
 *
 * <p>Note: this object does not support binary resources (such as images).
 *
 * @author Christophe Lauret
 */
public class PSResource {

  /**
   * The location of the resource from the website root.
   */
  private String location;

  /**
   * The content of the resource.
   */
  private String content;

  public PSResource() {
  }

  /**
   *
   * @param location
   * @param content
   */
  public PSResource(String location, String content) {
    this.location = location;
    this.content = content;
  }

  /**
   * @return the location
   */
  public String getLocation() {
    return this.location;
  }

  /**
   * @return the content
   */
  public String getContent() {
    return this.content;
  }

  /**
   * @param location the location to set
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * @param content the content to set
   */
  public void setContent(String content) {
    this.content = content;
  }


}
