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

import javax.servlet.http.HttpServletRequest;

import org.pageseeder.berlioz.aeson.JSONWriter;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.net.UsernamePassword;

public final class Registration implements AppAction {

  @Override
  public String getName() {
    return "client-registration";
  }

  @Override
  public int process(HttpServletRequest req, JSONWriter json) {
    String url = req.getParameter("setup-url");
    String username = req.getParameter("setup-username");
    String password = req.getParameter("setup-password");
    String oauthurl = req.getParameter("setup-oauthurl");
    String clienturi = oauthurl+"/";
    String redirecturi = oauthurl+"/auth";
    PSConfig config = PSConfig.newInstance(url);
    PSCredentials credentials = new UsernamePassword(username, password);

    return 0;
  }

}
