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

import org.pageseeder.berlioz.aeson.JSONWriter;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.http.Method;
import org.pageseeder.bridge.http.Request;
import org.pageseeder.bridge.http.Service;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.net.UsernamePassword;
import org.pageseeder.bridge.xml.HandlerFactory;

public class ProjectCheck implements AppAction {

  @Override
  public String getName() {
    return "check-pageseeder";
  }

  @Override
  public int process(HttpServletRequest req, JSONWriter json) {
    String url = req.getParameter("setup-url");
    String username = req.getParameter("setup-username");
    String password = req.getParameter("setup-password");
    String project = req.getParameter("setup-project");
    PSConfig config = PSConfig.newInstance(url);
    PSCredentials credentials = new UsernamePassword(username, password);
    PSGroup group = new Request(Method.GET, Service.get_project, project)
        .config(config)
        .using(credentials)
        .response().consumeItem(HandlerFactory.newPSGroupHandler());
    if (group != null) {
      json.startObject()
      .property("action", "check-project")
      .startObject("result")
      .property("name", group.getName())
      .property("description", group.getDescription())
      .end()
      .end();
      return HttpServletResponse.SC_OK;
    } else {
      json.startObject().property("action", "check-project").end();
      return HttpServletResponse.SC_BAD_REQUEST;
    }
  }

}
