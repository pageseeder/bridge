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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.pageseeder.berlioz.aeson.JSONWriter;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.http.Method;
import org.pageseeder.bridge.http.Request;
import org.pageseeder.bridge.http.Response;
import org.pageseeder.bridge.http.Service;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.net.UsernamePassword;
import org.pageseeder.bridge.xml.HandlerFactory;

public class CredentialsCheck implements AppAction {

  @Override
  public String getName() {
    return "check-credentials";
  }

  @Override
  public int process(HttpServletRequest req, JSONWriter json) {
    HttpSession session = req.getSession();
    String url = req.getParameter("setup-url");
    String username = req.getParameter("setup-username");
    String password = req.getParameter("setup-password");
    PSConfig config = PSConfig.newInstance(url);
    PSCredentials credentials = new UsernamePassword(username, password);
    Response response = new Request(Method.GET, Service.get_self).config(config).using(credentials).response();
    if (response.code() == 200) { // We check 200 as we don't accept redirects
      PSMember member = response.consumeItem(HandlerFactory.newPSMemberHandler());
      session.setAttribute("psconfig", config);
      json.startObject()
      .property("action", "check-credentials")
      .startObject("result").property("name", member.getFirstname()).end()
      .end();
      return HttpServletResponse.SC_OK;
    } else return HttpServletResponse.SC_BAD_REQUEST;
  }
}
