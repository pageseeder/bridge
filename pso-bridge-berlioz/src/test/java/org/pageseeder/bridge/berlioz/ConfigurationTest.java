package org.pageseeder.bridge.berlioz;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.pageseeder.berlioz.GlobalSettings;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.berlioz.auth.AuthException;
import org.pageseeder.bridge.berlioz.auth.Authenticator;
import org.pageseeder.bridge.berlioz.config.Configuration;

public final class ConfigurationTest {

  /**
   * This test is not working and it is not asserting anything.
   */
  @Test
  @Ignore
  public void testConfigProvider() {
    File dir = new File("src/test/data");
    GlobalSettings.setup(dir);
    GlobalSettings.setMode("local");
    PSConfig manual = PSConfig.newInstance(new Properties());
    PSConfig auto = PSConfig.getDefault();
    System.out.println(manual.getAPIBaseURL());
    System.out.println(auto.getAPIBaseURL());
  }

  @Test
  public void testListAvailableAuthenticators() {
    List<String> available = Configuration.listAvailableAuthenticators();
    Assert.assertNotNull(available);
    Assert.assertFalse(available.isEmpty());
    Assert.assertTrue(available.contains("pageseeder"));
    System.out.println("Available authenticators: "+available);
  }


  /**
   * This test needs to have some configuration to be perfomed.
   * @throws AuthException
   */
  @Test
  @Ignore
  public void testGetAuthenticator() throws AuthException {
    Authenticator<?> auth = Configuration.getAuthenticator();
    Assert.assertNotNull(auth);
  }

}
