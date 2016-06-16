package org.pageseeder.bridge.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for HTTP
 */
public final class HTTP {

  /** Logger for this class */
  private static final Logger LOGGER = LoggerFactory.getLogger(HTTP.class);

  private HTTP() {
  }

  /**
   * Returns the string to write the parameters sent via POST as <code>application/x-www-form-urlencoded</code>.
   *
   * @return the string to write the parameters sent via POST.
   */
  public static String encodeParameters(Map<String, String> parameters) {
    StringBuilder q = new StringBuilder();
    try {
      for (Entry<String, String> p : parameters.entrySet()) {
        if (q.length() > 0) {
          q.append("&");
        }
        q.append(URLEncoder.encode(p.getKey(), "utf-8"));
        q.append("=").append(URLEncoder.encode(p.getValue(), "utf-8"));
      }
    } catch (UnsupportedEncodingException ex) {
      // Should never happen as UTF-8 is supported
      ex.printStackTrace();
    }
    return q.toString();
  }

  /**
   * Returns the correct input stream from the connection depending on the response code.
   *
   * <p>This method will open the stream and it must be closed externally.
   *
   * @param connection The HTP URL connection.
   *
   * @return the input stream
   *
   * @throws IOException if thrown by {@link HttpURLConnection#getResponseCode()} or {@link HttpURLConnection#getInputStream().
   */
  public static InputStream stream(HttpURLConnection connection) throws IOException {
    int responseCode = connection.getResponseCode();
    if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_BAD_REQUEST) return connection.getInputStream();
    else return connection.getErrorStream();
  }

}
