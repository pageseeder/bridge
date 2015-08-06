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
