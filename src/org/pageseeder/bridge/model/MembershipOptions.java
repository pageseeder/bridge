package org.pageseeder.bridge.model;


/**
 * Additional options when creating a membership.
 */
public final class MembershipOptions {

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

}
