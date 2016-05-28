package org.pageseeder.bridge;

/**
 * PageSeeder tokens introduced in PageSeeder 5.9 can be used as valid
 * credentials when making calls to services.
 *
 * @author Christophe Lauret
 */
@Requires(minVersion = 59000)
public class PSToken implements PSCredentials {

  private final String _token;

  public PSToken(String token) {
    // TODO Check valid token format
    this._token = token;
  }

  public String token() {
    return this._token;
  }

}
