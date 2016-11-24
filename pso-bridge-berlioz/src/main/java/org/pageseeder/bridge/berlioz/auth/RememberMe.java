package org.pageseeder.bridge.berlioz.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the remember me functionality.
 *
 * <p>This is a low level API, do not use directly.
 *
 * @author Christophe Lauret
 */
public final class RememberMe {

  /**
   * Issues should be reported here.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(RememberMe.class);

  /** Required for IV generation */
  private final static SecureRandom R = new SecureRandom();

  /** AES-128 GCM Mode authentication tag size (96 bits) */
  private final static int TAG_LENGTH_BYTES = 12;

  /** AES-128 GCM Mode initialization vector size (96 bits) */
  private final static int IV_LENGTH_BYTES = 12;

  /** AES-128 GCM Mode key length (128 bits = 16 bytes) */
  private final static int KEY_LENGTH_BYTES = 16;

  /**
   * Default name of the remember me cookie.
   */
  private final static String DEFAULT_COOKIE_NAME = "pid";

  /**
   * Default max age for the remember me cookie (in seconds) - 30 days.
   */
  private final static int DEFAULT_COOKIE_MAX_AGE_SECONDS = 3600*24*30;

  private SecretKeySpec masterKey = null;

  private SecretKeySpec commonKey = null;

  private Path userkeysStore = null;

  private Map<String, SecretKeySpec> userkeys = new HashMap<String, SecretKeySpec>();

  /**
   * The name to use for the cookie.
   */
  private final String _cookieName;

  /**
   * The max age for the cookie.
   */
  private final int _cookieMaxAge;

  /**
   * Create the new remember me function with the default configuration.
   */
  public RememberMe() {
    this._cookieName = DEFAULT_COOKIE_NAME;
    this._cookieMaxAge = DEFAULT_COOKIE_MAX_AGE_SECONDS;
  }

  /**
   * Initialise the remeber functionality using the configuration at the specified path.
   *
   * @param auth
   *
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public void init(Path auth) throws GeneralSecurityException, IOException {
//    Security.addProvider(new BouncyCastleProvider());
// org.bouncycastle.jce.provider.BouncyCastleProvider
    // FIXME: detect if GCM mode is available

    if (!Files.exists(auth)) {
      Files.createDirectory(auth);
    }
    // Initialises the master key
    this.masterKey = generateMaster("Allette TimeSheet");

    // Initializes the key
    this.commonKey = getKey(auth, this.masterKey);

    // Initializes the username
    this.userkeysStore = auth.resolve("users.properties");
    load(this.userkeysStore, this.userkeys, this.commonKey);
  }

  /**
   * Returns the credentials encapsulated in the specified cookie.
   *
   * @param cookie The remember me cookie containing the credentials.
   *
   * @return The corresponding credentials.
   */
  public Credentials getCredentials(Cookie cookie) {
    if (cookie != null) {
     String value = cookie.getValue();
      int colon = value.indexOf(':');
      if (colon > 0) {
        try {
          byte [] userEncrypted = parseBase64URL(value.substring(0, colon));
          byte [] passEncrypted = parseBase64URL(value.substring(colon+1));

          // Decrypt the user using the common key
          String username = new String(decrypt(userEncrypted, this.commonKey));

          // Decrypt the password using the unique key set for the user
          SecretKeySpec userKey = this.userkeys.get(username);
          if (userKey != null) {
            String password = new String(decrypt(passEncrypted, userKey));
            return new Credentials(username, password);
          }
        } catch (GeneralSecurityException ex) {
          // TODO Auto-generated catch block
          ex.printStackTrace();
        }
      }
    }
    return null;
  }

  /**
   * Return the rememeber me cookie from the specified array of cookies.
   *
   * @param cookies The array of cookie held by the request (may be <code>null</code>).
   *
   * @return The remember me cookie or <code>null</code> if not found.
   */
  public Cookie getCookie(Cookie[] cookies) {
    if (cookies == null) return null;
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (this._cookieName.equals(cookie.getName())) return cookie;
      }
    }
    return null;
  }

  /**
   * Generate a new rememberme cookie from the specified credentials.
   *
   * @param credentials The credentials to encrypt in the cookie.
   *
   * @return The corresponding cookie.
   */
  public Cookie newCookie(Credentials credentials) {
    Cookie cookie = null;
    try {

      // Retrieve the unique key for the user
      SecretKeySpec userKey = this.userkeys.get(credentials.username());
      if (userKey == null) {
        // Generate a new key for the user if necessary
        userKey = new SecretKeySpec(newRandomBytes(KEY_LENGTH_BYTES), "AES");
        this.userkeys.put(credentials.username(), this.commonKey);
        save(this.userkeysStore, this.userkeys, this.commonKey);
      }

      // Encrypt the user using the common key
      byte[] user = encrypt(credentials.username().getBytes(), this.commonKey);
      byte[] pass = encrypt(credentials.password().getBytes(), userKey);

      String value = toBase64URL(user)+":"+toBase64URL(pass);
      cookie = new Cookie(this._cookieName, value);
      cookie.setHttpOnly(true);
      cookie.setSecure(true);
      cookie.setMaxAge(this._cookieMaxAge);

    } catch (IOException | GeneralSecurityException ex) {
      // TODO Auto-generated catch block
      ex.printStackTrace();
    }

    return cookie;
  }

  // ----------------------------------------------------------------------------------------------
  // Low-level encryption classes

  /**
   * Encrypt the specified message using the key.
   *
   * @param message
   * @param key
   * @return
   * @throws GeneralSecurityException
   */
  private static byte[] encrypt(byte[] message, Key key) throws GeneralSecurityException {
    byte[] iv = newRandomBytes(IV_LENGTH_BYTES);
    GCMParameterSpec s = new GCMParameterSpec(TAG_LENGTH_BYTES*8, iv);
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, key, s);
    byte[] enc = cipher.doFinal(message);
    return concatenate(iv, enc);
  }

  /**
   * Decrypt the specified message using the key.
   *
   * @param message The encrypted message.
   * @param key     The key
   *
   * @return the decrypted message.
   *
   * @throws GeneralSecurityException
   */
  private static byte[] decrypt(byte[] encrypted, Key key) throws GeneralSecurityException {
    byte[] iv = Arrays.copyOf(encrypted, IV_LENGTH_BYTES);
    GCMParameterSpec s = new GCMParameterSpec(TAG_LENGTH_BYTES*8, iv);
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.DECRYPT_MODE, key, s);

    byte[] raw = Arrays.copyOfRange(encrypted, IV_LENGTH_BYTES, encrypted.length);
    byte [] message = cipher.doFinal(raw);

    return message;
  }

  /**
   * Generate an array of random bytes using a secure PRG
   *
   * @param length the number of bytes to generate.
   *
   * @return the corresponding byte array.
   */
  private static byte[] newRandomBytes(int length) {
    byte[] bytes = new byte[length];
    R.nextBytes(bytes);
    return bytes;
  }

  /**
   * Concatenate the two arrays
   *
   * @param a The first array
   * @param b The second array.
   *
   * @return The two arrays merged
   */
  private static byte[] concatenate(byte[] a, byte[] b) {
    byte[] c = new byte[a.length + b.length];
    System.arraycopy(a, 0, c, 0, a.length);
    System.arraycopy(b, 0, c, a.length, b.length);
    return c;
  }

  /**
   * Retrieve the key to use to encode user data.
   *
   * @param auth
   * @param master
   * @return
   * @throws IOException
   * @throws GeneralSecurityException
   */
  private static SecretKeySpec getKey(Path auth, SecretKeySpec master) throws IOException, GeneralSecurityException {
    // Initializes the key
    Path keyStore = auth.resolve("common.key");
    byte[] keyBytes = null;
    if (Files.exists(keyStore)) {
      byte[] encrypted = Files.readAllBytes(keyStore);
      keyBytes = decrypt(encrypted, master);
    } else {
      keyBytes = newRandomBytes(KEY_LENGTH_BYTES);
      byte[] encrypted = encrypt(keyBytes, master);
      Files.write(keyStore, encrypted);
    }
    return new SecretKeySpec(keyBytes, "AES");
  }

  /**
   * Generate the master key from the specified password.
   *
   * @param password The password to use to generate the master key.
   *
   * @return A new secret key to encode the user specific password.
   *
   * @throws GeneralSecurityException
   */
  private static SecretKeySpec generateMaster(String password) throws GeneralSecurityException {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    byte[] salt = new byte[]{1,2,3,4,5,6,7,8,9,0,1,2,3,4,5};
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1024, KEY_LENGTH_BYTES*8);
    SecretKey tmp = factory.generateSecret(spec);
    return new SecretKeySpec(tmp.getEncoded(), "AES");
  }

  private static void load(Path store, Map<String, SecretKeySpec> data, SecretKeySpec key) throws IOException, GeneralSecurityException {
    if (!Files.exists(store)) return;
    List<String> lines = Files.readAllLines(store, StandardCharsets.UTF_8);
    for (String line : lines) {
      int separator = line.indexOf('=');
      if (separator > 0) {
        String username = line.substring(0, separator);
        String base64 = line.substring(separator+1);
        byte[] encrypted = DatatypeConverter.parseBase64Binary(base64);
        byte[] keyBytes = decrypt(encrypted, key);
        SecretKeySpec secret = new SecretKeySpec(keyBytes, "AES");
        data.put(username, secret);
      }
    }
  }

  private static void save(Path store, Map<String, SecretKeySpec> data, SecretKeySpec key) throws IOException, GeneralSecurityException {
    List<String> lines = new ArrayList<String>(data.size());
    for (Entry<String, SecretKeySpec> entry : data.entrySet()) {
      byte[] keyBytes = entry.getValue().getEncoded();
      byte[] encrypted = encrypt(keyBytes, key);
      String line = entry.getKey()+"="+DatatypeConverter.printBase64Binary(encrypted);
      lines.add(line);
    }
    Files.write(store, lines, StandardCharsets.UTF_8);

  }

  private String toBase64URL(byte[] data) {
    return DatatypeConverter.printBase64Binary(data).replace('+', '-').replace('/', '_');
  }

  private byte[] parseBase64URL(String data) {
    return DatatypeConverter.parseBase64Binary(data.replace('-', '+').replace('_', '/'));
  }


  public static final class Credentials {
    private final String _username;
    private final String _password;
    public Credentials(String username, String password) {
      this._username = username;
      this._password = password;
    }

    public String username() {
      return this._username;
    }

    public String password() {
      return this._password;
    }

  }

}
