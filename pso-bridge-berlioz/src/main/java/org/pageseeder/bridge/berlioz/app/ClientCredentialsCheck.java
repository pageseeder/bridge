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
package org.pageseeder.bridge.berlioz.app;

import org.pageseeder.berlioz.aeson.JSONWriter;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.http.Method;
import org.pageseeder.bridge.http.Request;
import org.pageseeder.bridge.http.Response;
import org.pageseeder.bridge.http.Service;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.net.UsernamePassword;
import org.pageseeder.bridge.oauth.ClientCredentials;
import org.pageseeder.bridge.oauth.TokenRequest;
import org.pageseeder.bridge.oauth.TokenResponse;
import org.pageseeder.bridge.xml.HandlerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;

/**
 * Verifies the OAuth client credentials.
 *
 * <ul>
 *   <li><code>setup-url</code>: the PageSeeder base URL</li>
 *   <li><code>setup-client</code>: the client to check</li>
 *   <li><code>setup-secret</code>: the secret of the client to check</li>
 * </ul>
 *
 * <p>If the "setup-url" is valid the config is stored in the "psconfig" attribute.
 *
 *
 * @author Philip Rutherford
 *
 * @since 0.11.38
 * @version 0.11.38
 */
public final class ClientCredentialsCheck implements AppAction {

  @Override
  public String getName() {
    return "check-client-credentials";
  }

  @Override
  public int process(HttpServletRequest req, JSONWriter json) {
    HttpSession session = req.getSession();
    String url = req.getParameter("setup-url");
    String client = req.getParameter("setup-client");
    String secret = req.getParameter("setup-secret");

    // Checks
    if (url == null || "".equals(url)) return JSONResponses.requiresParameter(this, json, "setup-url");
    if (client == null || "".equals(client)) return JSONResponses.requiresParameter(this, json, "setup-client");
    if (secret == null || "".equals(secret)) return JSONResponses.requiresParameter(this, json, "setup-secret");

    // Try the config
    PSConfig config = PSConfig.newInstance(url);
    ClientCredentials clientCredentials = new ClientCredentials(client, secret);
    TokenResponse response = TokenRequest.newClientCredentials(clientCredentials, config).post();

    // We check successful
    if (response.isSuccessful()) {
      PSMember member = response.getMember();
      session.setAttribute("psconfig", config);
      return JSONResponses.ok(this, json, Collections.singletonMap("name", member.getFirstname()));
    } else return JSONResponses.serviceError(this, json, response);
  }

}
