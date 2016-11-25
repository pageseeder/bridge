/*
 * Copyright 2016 Allette Systems (Australia)
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
package org.pageseeder.bridge.berlioz.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

import org.pageseeder.berlioz.GlobalSettings;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.berlioz.auth.AuthException;
import org.pageseeder.bridge.berlioz.auth.Authenticator;
import org.pageseeder.bridge.berlioz.auth.PSAuthenticator;
import org.pageseeder.bridge.berlioz.auth.PermissionManager;
import org.pageseeder.bridge.berlioz.auth.User;
import org.pageseeder.bridge.berlioz.auth.spi.AuthProvider;
import org.pageseeder.bridge.spi.ConfigProvider;

/**
 * General configuration for the
 *
 * @author Christophe Lauret
 *
 * @version 0.1.3
 * @since 0.1.0
 */
public final class Configuration {

  /** List of available authentication providers. */
  private static final List<AuthProvider> AUTH_PROVIDERS = loadAuthProviders();

  /** Utility class */
  private Configuration() {
    throw new AssertionError();
  }

  /**
   * Returns the default authenticator based on the configuration.
   *
   * @return the default authenticator based on the configuration.
   */
  public static Authenticator<? extends User> getAuthenticator() throws AuthException {
    // Load from config
    Properties config = GlobalSettings.getNode("bridge.authenticator");
    String name = config.getProperty("name", "pageseeder");
    String filter = config.getProperty("member-of");
    for (AuthProvider provider : AUTH_PROVIDERS) {
      Authenticator<? extends User> auth = provider.authenticatorForName(name);
      if (auth instanceof PSAuthenticator) {
        ((PSAuthenticator)auth).setGroupFilter(filter);
      }
      if (auth != null) return auth;
    }
    throw new AuthException("Unsupported authenticator: "+name);
  }

  /**
   * Returns the default permission manager based on the configuration.
   *
   * @return the default permission manager based on the configuration.
   */
  public static PermissionManager getPermissionManager() {
    return new PermissionManager() {
      @Override
      public boolean hasPermission(User user, String permission) {
        return false;
      }
    };
  }

  /**
   * Returns the list of available authenticators
   *
   * @return the list of available authenticators
   */
  public static List<String> listAvailableAuthenticators() {
    List<String> names = new ArrayList<>();
    for (AuthProvider p : AUTH_PROVIDERS) {
      for (Iterator<String> i = p.authenticators(); i.hasNext();) {
        names.add(i.next());
      }
    }
    return names;
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * Loads all authenticators using the service loader.
   *
   * @return The list of authenticators.
   */
  private static List<AuthProvider> loadAuthProviders() {
    List<AuthProvider> providers = new ArrayList<>();
    ServiceLoader<AuthProvider> loader = ServiceLoader.load(AuthProvider.class);
    for (AuthProvider provider : loader) {
      providers.add(provider);
    }
    return providers;
  }

  /**
   * Built-in configuration provider for the Bridge.
   */
  public static class Provider implements ConfigProvider {

    @Override
    public PSConfig getConfig() {
      Properties p = GlobalSettings.getNode("bridge");
      return PSConfig.newInstance(p);
    }

  }
}
