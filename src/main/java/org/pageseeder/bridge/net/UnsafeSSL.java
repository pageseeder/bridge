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
package org.pageseeder.bridge.net;

import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.LoggerFactory;

/**
 * A utility class to trust any certificate so that the HTTPS connections can
 * be established to self-signed certificates.
 *
 * <h2>Security implications</h2>
 * <p>This class creates a trust manager which trusts any certificate and
 * a hostname verifier which verifies any hostname, then use these as defaults
 * for <code>HttpsURLConnection</code>s.
 *
 * <p><b>Never use this in production!</b> This class is only intended to
 * allow tests to self-signed certificate during local development or testing.
 *
 * <p>This class must be called before HTTP connections are established,
 * usually in a static initializer:
 * <pre>
 *  static {
 *    UnsafeSSL.enableIfSystemProperty();
 *  }
 * </pre>
 *
 * <p>Unless you're in a security context that permits it, it is strongly
 * recommended that you use the {@link #enableIfSystemProperty()} method rather
 * the {@link #enable()} as it gives the additional protection of requiring
 * a system property to be specified.
 *
 * @author Christophe Lauret
 *
 * @version 0.10.2
 * @since 0.9.3
 */
public final class UnsafeSSL {

  /**
   * Ensures this is enabled only once.
   */
  private static volatile boolean enabled = false;

  /**
   * Enable unsafe SSL, only if the system property "bridge.ssl.enableUnsafe" is set to "true"
   *
   * <p>If this method is called multiple times, it will simply return.
   */
  public static void enableIfSystemProperty() {
    if ("true".equals(System.getProperty("bridge.ssl.enableUnsafe", "false"))) {
      enable();
    }
  }

  /**
   * Enable unsafe SSL in all cases.
   *
   * <p>If this method is called multiple times, it will simply return.
   */
  public static void enable() {
    if (enabled) return;
    enabled = true;
    try {
      // Create a trust manager that does not validate certificate chains
      final TrustManager[] trustAllCerts = new TrustManager[] {
        new X509TrustManager() {
          @Override
          public void checkClientTrusted(java.security.cert.X509Certificate @Nullable[] chain, @Nullable String authType) throws CertificateException {
          }

          @Override
          public void checkServerTrusted(java.security.cert.X509Certificate @Nullable[] chain, @Nullable String authType) throws CertificateException {
          }

          @Override
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
          }
        }
      };

      // Install the all-trusting trust manager
      final SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

      // Create an SSL socket factory with our all-trusting manager
      final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
      HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
      HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
      LoggerFactory.getLogger(UnsafeSSL.class).error("Unsafe SSL has been enabled! ALL HTTPS CONNECTIONS ARE POTENTIALLY UNSAFE - USE THIS FEATURES FOR TESTING ONLY");
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}
