/*
 * Copyright 2017 Allette Systems (Australia)
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

package org.pageseeder.bridge.core;

import org.junit.Assert;

public class BasicGroupTest {

  public static void assertEquals(BasicGroup exp, BasicGroup got) {
    Assert.assertEquals(exp.getId(), got.getId());
    Assert.assertEquals(exp.getName(), got.getName());
    Assert.assertEquals(exp.getParentName(), got.getParentName());
    Assert.assertEquals(exp.getShortName(), got.getShortName());
    Assert.assertEquals(exp.getTitle(), got.getTitle());
    Assert.assertEquals(exp.getDescription(), got.getDescription());
    Assert.assertEquals(exp.getOwner(), got.getOwner());
    Assert.assertEquals(exp.getRelatedURL(), got.getRelatedURL());
    Assert.assertEquals(exp.getAccess(), got.getAccess());
    Assert.assertEquals(exp.isCommon(), got.isCommon());
    Assert.assertEquals(exp.isProject(), got.isProject());
  }

}
