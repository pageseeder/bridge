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
import javax.servlet.http.HttpServletResponse;

import org.pageseeder.berlioz.aeson.JSONWriter;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.Version;

/**
 * Checks that a PageSeeder config is valid.
 *
 * <ul>
 *   <li><code>setup-url</code>: the PageSeeder base URL</li>
 * </ul>
 *
 * @author Christophe Lauret
 *
 * @since 0.9.8
 * @version 0.9.8
 */
public final class PSConfigCheck implements AppAction {

  @Override
  public String getName() {
    return "check-pageseeder";
  }

  @Override
  public int process(HttpServletRequest req, JSONWriter json) {
    String url = req.getParameter("setup-url");

    // Checks
    if (url == null || "".equals(url)) return JSONResponses.requiresParameter(this, json, "setup-url");

    Version version = PSConfig.newInstance(url).getVersion();
    if (version != null) {
      Map<String, String> result = new HashMap<>();
      result.put("major", Integer.toString(version.major()));
      result.put("build", Integer.toString(version.build()));
      result.put("version", version.version());
      JSONResponses.ok(this, json, result);
      return HttpServletResponse.SC_OK;
    } else {
      String description = "Unable of retrieve PageSeeder version or invalid URL";
      return JSONResponses.error(this, json, description);
    }
  }

}
