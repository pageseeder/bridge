package org.pageseeder.bridge.berlioz.auth;

/**
 * Provides an interface
 *
 * @author Christophe Lauret
 */
public interface PermissionManager {

  /**
   * Indicates whether the specified user has a given permission.
   *
   * @param user       The user to test.
   * @param permission The permission to check
   *
   * @return <code>true</code> if the user has the specified permission
   *         <code>false</code> otherwise.
   */
  boolean hasPermission(User user, String permission);

}
