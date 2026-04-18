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

public class MemberTest {

  public static void assertEquals(Member exp, Member got) {
    Assert.assertEquals(exp.getId(), got.getId());
    Assert.assertEquals(exp.getUsername(), got.getUsername());
    Assert.assertEquals(exp.getEmail(), got.getEmail());
    Assert.assertEquals(exp.getFirstname(), got.getFirstname());
    Assert.assertEquals(exp.getSurname(), got.getSurname());
    Assert.assertEquals(exp.getStatus(), got.getStatus());
    Assert.assertEquals(exp.isActivated(), got.isActivated());
    Assert.assertEquals(exp.isLocked(), got.isLocked());
    Assert.assertEquals(exp.isOnVacation(), got.isOnVacation());
    Assert.assertEquals(exp.hasAttachments(), got.hasAttachments());
    Assert.assertEquals(exp.getLastLogin(), got.getLastLogin());
  }
}
