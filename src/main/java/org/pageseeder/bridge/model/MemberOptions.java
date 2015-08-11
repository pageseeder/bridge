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


/**
 * Additional options when creating a membership.
 */
public final class MemberOptions {

  public enum Invitation {YES, NO, DEFAULT};

  /**
   * Whether to send a welcome email to the new member.
   */
  private boolean welcomeEmail = true;

  /**
   * Whether to create the personal group
   */
  private boolean personalGroup = false;

  /**
   * Whether to activate the new member automatically
   */
  private boolean autoActivate = false;

  /**
   * Whether to create an invitation, otherwise force membership
   */
  private Invitation invitation = Invitation.DEFAULT;

  /**
   * @return the welcomeEmail
   */
  public boolean hasWelcomeEmail() {
    return this.welcomeEmail;
  }

  /**
   * @return the personalGroup
   */
  public boolean hasPersonalGroup() {
    return this.personalGroup;
  }

  /**
   * @return the autoActivate
   */
  public boolean isAutoActivate() {
    return this.autoActivate;
  }

  /**
   * @return the invitation
   */
  public Invitation getInvitation() {
    return this.invitation;
  }

  /**
   * @param welcomeEmail the welcomeEmail to set
   */
  public void setWelcomeEmail(boolean welcomeEmail) {
    this.welcomeEmail = welcomeEmail;
  }

  /**
   * @param personalGroup the personalGroup to set
   */
  public void setPersonalGroup(boolean personalGroup) {
    this.personalGroup = personalGroup;
  }

  /**
   * @param autoActivate the autoActivate to set
   */
  public void setAutoActivate(boolean autoActivate) {
    this.autoActivate = autoActivate;
  }

  /**
   * @param invitation the invitation to set
   */
  public void setInvitation(Invitation invitation) {
    this.invitation = invitation;
  }

}
