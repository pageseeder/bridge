package org.pageseeder.bridge;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * PageSeeder tokens introduced in PageSeeder 5.9 can be used as valid
 * credentials when making calls to services.
 *
 * @author Christophe Lauret
 */
@Requires(minVersion = 59000)
public class PSToken implements PSCredentials, Serializable {

  /** As required for Serializable */
  private static final long serialVersionUID = 20161016L;

  /**
   * PageSeeder tokens normally use a base64 encoding with no padding.
   *
   * <p>We allow all possible Base64 characters as well as '.' and padding '=' for extensibility.
   *
   * <p>No assumption is made on the length, but we discard any token that is too short or too long.
   */
  protected static final Pattern VALID_PAGESEEDER_TOKEN = Pattern.compile("[a-zA-Z0-9=.+/_-]{16,1024}");

  /**
   * The actual token.
   */
  private final String _token;

  /**
   * When this token expires.
   */
  private final long _expires;

  /**
   * Creates a new PageSeeder access token.
   *
   * @param token A new PageSeeder access token.
   *
   * @throws NullPointerException if the token is <code>null</code>
   * @throws IllegalArgumentException if the token is not valid.
   */
  public PSToken(String token) {
    this(token, 0L);
  }

  /**
   * Creates a new PageSeeder access token.
   *
   * @param token   A new PageSeeder access token.
   * @param expires When the token expires.
   *
   * @throws NullPointerException if the token is <code>null</code>
   * @throws IllegalArgumentException if the token is not valid.
   */
  public PSToken(String token, long expires) {
    if (token == null)
      throw new NullPointerException("Access token is null");
    if (!VALID_PAGESEEDER_TOKEN.matcher(token).matches())
      throw new IllegalArgumentException("Access token is invalid");
    this._token = token;
    this._expires = expires;
  }

  /**
   * @return the actual access token.
   */
  public String token() {
    return this._token;
  }

  /**
   * @return when the token expires in milliseconds since Epoch.
   */
  public long expiresMillis() {
    return this._expires;
  }

  /**
   * @return <code>true</code> if the token has expired;
   *         <code>false</code> otherwise or if it is not known.
   */
  public boolean hasExpired() {
    return System.currentTimeMillis() - this._expires > 0;
  }

}
