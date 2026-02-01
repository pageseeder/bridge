/*
 * Copyright 2015 Allette Systems (Australia)
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
package org.pageseeder.bridge.model;

import org.jspecify.annotations.Nullable;

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
  private @Nullable String location;

  /**
   * The content of the resource.
   */
  private @Nullable String content;

  /**
   * The content of the resource.
   */
  private byte @Nullable[] bytes;

  /**
   * Default constructor.
   */
  public PSResource() {
  }

  /**
   * Creates a new resource at the specified location and content.
   *
   * @param location the location of the resource
   * @param content  its textual content.
   */
  public PSResource(String location, String content) {
    this.location = location;
    this.content = content;
  }

  /**
   * @return the location
   */
  public @Nullable String getLocation() {
    return this.location;
  }

  /**
   * @return the content
   */
  public @Nullable String getContent() {
    return this.content;
  }

  /**
   * @return the content
   */
  public byte @Nullable[] getBytes() {
    return this.bytes;
  }

  /**
   * @return <code>true</code> if the resource is represented as a bytes rather then text.
   */
  public boolean isBinary() {
    return this.bytes != null;
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
    if (this.bytes != null) throw new IllegalStateException("Binary content already set");
    this.content = content;
  }

  /**
   * @param bytes the bytes to set
   */
  public void setBytes(byte[] bytes) {
    if (this.content != null) throw new IllegalStateException("Text content already set");
    this.bytes = bytes;
  }
}
