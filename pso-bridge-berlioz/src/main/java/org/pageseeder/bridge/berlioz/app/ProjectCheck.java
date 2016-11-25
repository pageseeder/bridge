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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.pageseeder.berlioz.aeson.JSONWriter;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.http.Method;
import org.pageseeder.bridge.http.Request;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.net.UsernamePassword;
import org.pageseeder.bridge.xml.HandlerFactory;

/**
 * Checks that a project exists.
 *
 * <ul>
 *   <li><code>setup-url</code>: the PageSeeder base URL</li>
 *   <li><code>setup-username</code>: the username of the user to check</li>
 *   <li><code>setup-password</code>: the password of the user to check</li>
 *   <li><code>setup-project</code>: the name of the project to check</li>
 * </ul>
 *
 * <p>If the "setup-url" is valid the config is stored in the "psconfig" attribute.
 *
 *
 * @author Christophe Lauret
 *
 * @since 0.9.9
 * @version 0.9.9
 */
public final class ProjectCheck implements AppAction {

  @Override
  public String getName() {
    return "check-pageseeder";
  }

  @Override
  public int process(HttpServletRequest req, JSONWriter json) {
    String url = req.getParameter("setup-url");
    String username = req.getParameter("setup-username");
    String password = req.getParameter("setup-password");
    String name = req.getParameter("setup-project");

    // Checks
    if (url == null || "".equals(url)) return JSONResponses.requiresParameter(this, json, "setup-url");
    if (username == null || "".equals(username)) return JSONResponses.requiresParameter(this, json, "setup-username");
    if (password == null || "".equals(password)) return JSONResponses.requiresParameter(this, json, "setup-password");
    if (name == null || "".equals(name)) return JSONResponses.requiresParameter(this, json, "setup-project");

    // Let's try to create a project
    PSConfig config = PSConfig.newInstance(url);
    PSCredentials credentials = new UsernamePassword(username, password);
    PSGroup project = Request.newService(Method.GET, "/projects/{group}", name)
        .config(config)
        .using(credentials)
        .response().consumeItem(HandlerFactory.newPSGroupHandler());
    if (project != null) {
      Map<String, String> result = new HashMap<>();
      result.put("name", project.getName());
      result.put("description", project.getDescription());
      return JSONResponses.ok(this, json, result);
    } else {
      String description = "Unable to retrieve the project";
      return JSONResponses.error(this, json, description);
    }
  }

}
