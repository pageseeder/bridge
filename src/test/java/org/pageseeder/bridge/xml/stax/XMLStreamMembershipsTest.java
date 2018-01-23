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

package org.pageseeder.bridge.xml.stax;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.bridge.core.Membership;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

public final class XMLStreamMembershipsTest {

  @Test
  public void testPassBasic() throws IOException, XMLStreamException {
    List<Membership> memberships = XMLStreamTest.parseList("membership/memberships-formember.xml", new XMLStreamMembership());
//    Assert.assertEquals(1L, membership.getId());
    Assert.assertEquals(3, memberships.size());
  }

}
