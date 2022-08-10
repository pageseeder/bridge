/*
 * Copyright 2021 Allette Systems (Australia)
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

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.bridge.PSConfig;

/**
 * @author ccabral
 * @since 09 August 2022
 */
public class PSHTTPResourceTest {

  @Test
  public void testConstructor_withNameAndType(){
    PSConfig defaultConfig = PSConfig.newInstance("https://psdomain.com/ps");
    PSConfig.setDefault(defaultConfig);
    PSHTTPResourceType type = PSHTTPResourceType.SERVICE;
    String service = "/groups/groupname/test";
    PSHTTPResource resource = new PSHTTPResource(type, service);
    Assert.assertEquals(type, resource.type());
    Assert.assertEquals(service, resource.name());
    Assert.assertNotNull(resource.config());
    Assert.assertEquals(defaultConfig.getWebsiteBaseURL(), resource.config().getWebsiteBaseURL());
    Assert.assertEquals("https://psdomain.com/ps/service/groups/groupname/test", resource.toString());
  }

  @Test
  public void testConstructor_builderWithDefaultPSConfig(){
    PSConfig defaultConfig = PSConfig.newInstance("https://psdomain.com/ps");
    PSConfig.setDefault(defaultConfig);
    PSHTTPResourceType type = PSHTTPResourceType.SERVICE;
    String service = "/groups/groupname/test";
    String body = "body";
    PSHTTPResource.Builder builder = new PSHTTPResource.Builder(type, service);
    builder.body(body);
    PSHTTPResource resource = builder.build();
    Assert.assertEquals(type, resource.type());
    Assert.assertEquals(service, resource.name());
    Assert.assertEquals(body, resource.body());
    Assert.assertEquals(true, resource.includeErrorContent());
    Assert.assertNotNull(resource.config());
    Assert.assertEquals(defaultConfig.getWebsiteBaseURL(), resource.config().getWebsiteBaseURL());
    Assert.assertEquals("https://psdomain.com/ps/service/groups/groupname/test", resource.toString());
  }

  @Test
  public void testConstructor_builderWithCustomPSConfig(){
    PSConfig defaultConfig = PSConfig.newInstance("https://psdomain.com/ps");
    PSConfig.setDefault(defaultConfig);
    PSConfig anotherConfig = PSConfig.newInstance("https://anotherpsdomain.com/ps");
    PSHTTPResourceType type = PSHTTPResourceType.SERVICE;
    String service = "/groups/groupname/test";
    String body = "body";
    PSHTTPResource.Builder builder = new PSHTTPResource.Builder(type, service);
    builder.body(body);
    builder.config(anotherConfig);
    PSHTTPResource resource = builder.build();
    Assert.assertEquals(type, resource.type());
    Assert.assertEquals(service, resource.name());
    Assert.assertEquals(body, resource.body());
    Assert.assertEquals(true, resource.includeErrorContent());
    Assert.assertNotNull(resource.config());
    Assert.assertEquals(anotherConfig.getWebsiteBaseURL(), resource.config().getWebsiteBaseURL());
    Assert.assertEquals("https://anotherpsdomain.com/ps/service/groups/groupname/test", resource.toString());
  }
}
