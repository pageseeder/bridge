package org.pageseeder.bridge.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Additional options when creating a group which aren't part of the group entity.
 *
 * Most values are initialised to <code>null</code> so that it will use PageSeeder's default.
 */
public final class GroupOptions {

  /**
   * The subscription message
   */
  private String message = null;

  /**
   * Access to group and comments [public|member]  (default is "member")
   */
  private String access = null;

  /**
   * Who can comment [public|reviewer|contributor] (default is "public")
   */
  private String commenting = null;

  /**
   * Which comments will be moderated [reviewer|email|all] (default is "reviewer")
   */
  private String moderation = null;

  /**
   * Self-registration method [normal|moderated|confirmed] (default is "normal")
   */
  private String registration = null;

  /**
   * To set the control group.
   */
  private String visibility = null;

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
  private Map<String, String> properties = new HashMap<String, String>();

  /**
   * @return the message
   */
  public String getMessage() {
    return this.message;
  }

  /**
   * @return the access
   */
  public String getAccess() {
    return this.access;
  }

  /**
   * @return the commenting
   */
  public String getCommenting() {
    return this.commenting;
  }

  /**
   * @return the moderation
   */
  public String getModeration() {
    return this.moderation;
  }

  /**
   * @return the registration
   */
  public String getRegistration() {
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
   * @return
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

  public String getProperty(String key) {
    return this.properties.get(key);
  }

  public Map<String, String> getProperties() {
    return this.properties;
  }

  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }

  public String getVisibility() {
    return this.visibility;
  }
}
