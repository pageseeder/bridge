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
package org.pageseeder.bridge;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public final class PSConfigTest {

  @Test
  public void testNewInstance_Properties() throws Exception{
    PSConfig config = PSConfig.newInstance(new Properties());
    Assert.assertEquals(PSConfig.DEFAULT_WEBSITE.getScheme(), config.getScheme());
    Assert.assertEquals(PSConfig.DEFAULT_WEBSITE.getHost(), config.getHost());
    Assert.assertEquals(PSConfig.DEFAULT_WEBSITE.getPort(), config.getPort());
    Assert.assertEquals(PSConfig.DEFAULT_API.getScheme(), config.getAPIScheme());
    Assert.assertEquals(PSConfig.DEFAULT_API.getHost(), config.getAPIHost());
    Assert.assertEquals(PSConfig.DEFAULT_API.getPort(), config.getAPIPort());
    Assert.assertEquals(PSConfig.DEFAULT_WEBSITE.toString(), config.buildHostURL().toString());
    Assert.assertEquals(PSConfig.DEFAULT_API.toString(), config.buildAPIURL().toString());
    Assert.assertEquals(PSConfig.DEFAULT_PREFIX, config.getSitePrefix());
  }

  @Test
  public void testNewInstance_URIs() throws Exception{
    // Test with default ports
    PSConfig config = PSConfig.newInstance("https://localhost/", "http://localhost/");
    Assert.assertEquals("https",     config.getScheme());
    Assert.assertEquals("localhost", config.getHost());
    Assert.assertEquals(443,         config.getPort());
    Assert.assertEquals("http",      config.getAPIScheme());
    Assert.assertEquals("localhost", config.getAPIHost());
    Assert.assertEquals(80,          config.getAPIPort());
    Assert.assertEquals(PSConfig.DEFAULT_PREFIX, config.getSitePrefix());
    // Test with custom ports
    config = PSConfig.newInstance("https://pageseeder.local:8443/", "http://localhost:8282/");
    Assert.assertEquals("https",     config.getScheme());
    Assert.assertEquals("pageseeder.local", config.getHost());
    Assert.assertEquals(8443,        config.getPort());
    Assert.assertEquals("http",      config.getAPIScheme());
    Assert.assertEquals("localhost", config.getAPIHost());
    Assert.assertEquals(8282,          config.getAPIPort());
    Assert.assertEquals(PSConfig.DEFAULT_PREFIX, config.getSitePrefix());
  }

}