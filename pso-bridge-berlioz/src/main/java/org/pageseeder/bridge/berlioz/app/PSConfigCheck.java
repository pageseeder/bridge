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
import org.pageseeder.bridge.Version;

public class PSConfigCheck implements AppAction {

  @Override
  public String getName() {
    return "check-pageseeder";
  }

  @Override
  public int process(HttpServletRequest req, JSONWriter json) {
    String url = req.getParameter("setup-url");
    Version version = PSConfig.newInstance(url).getVersion();
    if (version != null) {
      json.startObject()
      .property("action", "check-pageseeder")
      .startObject("result")
      .property("major", version.major())
      .property("build", version.build())
      .property("version", version.version())
      .end()
      .end();
      return HttpServletResponse.SC_OK;
    } else {
      json.startObject().property("action", "check-pageseeder").end();
      return HttpServletResponse.SC_BAD_REQUEST;
    }
  }

}
