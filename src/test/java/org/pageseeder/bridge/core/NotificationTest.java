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
import org.junit.Test;

public final class NotificationTest {

  @Test
  public void testForName() {
    for (Notification n : Notification.values()) {
      Assert.assertSame(n, Notification.forName(n.name()));
      Assert.assertSame(n, Notification.forName(n.name().toUpperCase()));
    }
    Assert.assertSame(Notification.none, Notification.forName(null));
    Assert.assertSame(Notification.none, Notification.forName(""));
  }
}
