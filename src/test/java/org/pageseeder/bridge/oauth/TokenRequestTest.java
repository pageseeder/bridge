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
package org.pageseeder.bridge.oauth;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.net.UsernamePassword;

/**
 *
 */
public class TokenRequestTest {
  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testToString_ProtectPassword() {
    PSConfig config = PSConfig.newInstance("https://localhost/", "http://localhost:8282/");
    PSConfig.setDefault(config);
    String password = "opensesame";
    UsernamePassword user= new UsernamePassword("ali_baba", password);
    ClientCredentials client = new ClientCredentials("123456789123456789", "hfcnosty78cfgertfgai4");
    TokenRequest request = TokenRequest.newPassword(user, client);
    Assert.assertFalse(request.toString().contains(password));
  }

  @Test
  public void testNewClientCredentials_NullClient() {
    exceptionRule.expect(NullPointerException.class);
    exceptionRule.expectMessage("The client credentials are required");
    PSConfig config = PSConfig.newInstance("https://localhost/", "http://localhost:8282/");
    ClientCredentials client = null;
    TokenRequest request = TokenRequest.newClientCredentials(client, config);
  }

  @Test
  public void testNewClientCredentials_NullConfig() {
    exceptionRule.expect(NullPointerException.class);
    exceptionRule.expectMessage("The ps config is required");
    PSConfig config = null;
    ClientCredentials client= new ClientCredentials("123456789123456789", "hfcnosty78cfgertfgai4");
    TokenRequest request = TokenRequest.newClientCredentials(client, config);
  }
}
