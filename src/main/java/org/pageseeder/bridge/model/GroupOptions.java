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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Additional options when creating a group which aren't part of the group entity.
 *
 * Most values are initialised to <code>null</code> so that it will use PageSeeder's default.
 *
 * @version 0.10.2
 */
public final class GroupOptions {

  /**
   * The subscription message
   */
  private @Nullable String message = null;

  /**
   * Access to group and comments [public|member]  (default is "member")
   */
  private @Nullable String access = null;

  /**
   * Who can comment [public|reviewer|contributor] (default is "public")
   */
  private @Nullable String commenting = null;

  /**
   * Which comments will be moderated [reviewer|email|all] (default is "reviewer")
   */
  private @Nullable String moderation = null;

  /**
   * Self-registration method [normal|moderated|confirmed] (default is "normal")
   */
  private @Nullable String registration = null;

  /**
   * To set the control group.
   */
  private @Nullable String visibility = null;

  /**
   * Whether to add the current member as a manager (default is "true")
   */
  private boolean addmember = true;

  /**
   * Whether to create default documents (ignored for projects) (default is "true")
   */
  private boolean createdocuments = true;

  /**
   * Whether this is a common group (default is "false")
   */
  private boolean common = false;

  /**
   * Group properties.
   */
  private Map<String, String> properties = new HashMap<>();

  /**
   * @return the message
   */
  public @Nullable String getMessage() {
    return this.message;
  }

  /**
   * @return the access
   */
  public @Nullable String getAccess() {
    return this.access;
  }

  /**
   * @return the commenting
   */
  public @Nullable String getCommenting() {
    return this.commenting;
  }

  /**
   * @return the moderation
   */
  public @Nullable String getModeration() {
    return this.moderation;
  }

  /**
   * @return the registration
   */
  public @Nullable String getRegistration() {
    return this.registration;
  }

  /**
   * @return the common
   */
  public boolean isCommon() {
    return this.common;
  }

  /**
   *
   * @return <code>true</code> if creator will be added to group
   */
  public boolean doAddCreatorAsMember() {
    return this.addmember;
  }

  public boolean doCreateDocuments() {
    return this.createdocuments;
  }

  /**
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * @param access the access to set
   */
  public void setAccess(String access) {
    this.access = access;
  }

  /**
   * @param commenting the commenting to set
   */
  public void setCommenting(String commenting) {
    this.commenting = commenting;
  }

  /**
   * @param moderation the moderation to set
   */
  public void setModeration(String moderation) {
    this.moderation = moderation;
  }

  /**
   * @param registration the registration to set
   */
  public void setRegistration(String registration) {
    this.registration = registration;
  }

  /**
   * @param common the common to set
   */
  public void setCommon(boolean common) {
    this.common = common;
  }

  /**
   * Whether to add the creator of the group.
   *
   * @param addmember <code>true</code> to add the member creating the group as a member
   */
  public void setAddCreatorAsMember(boolean addmember) {
    this.addmember = addmember;
  }

  public void setCreateDocuments(boolean createdocuments) {
    this.createdocuments = createdocuments;
  }

  public void setProperty(String key, String value) {
    this.properties.put(key, value);
  }

  public @Nullable String getProperty(String key) {
    return this.properties.get(key);
  }

  public Map<String, String> getProperties() {
    return this.properties;
  }

  public void setVisibility(@Nullable String visibility) {
    this.visibility = visibility;
  }

  public @Nullable String getVisibility() {
    return this.visibility;
  }
}
