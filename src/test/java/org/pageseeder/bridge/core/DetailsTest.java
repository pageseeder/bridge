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

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class DetailsTest {

  @Test
  public void testEmpty() {
    Details empty = Details.NO_DETAILS;
    assertEmptyDetails(empty);
  }

  @Test
  public void testEmpty2() {
    Details empty = new Details(Collections.emptyList());
    assertEmptyDetails(empty);
  }

  @Test
  public void testSingle() {
    Field field = new Field(3,"color","red", true, "Colour", "rgb");
    Details details = new Details(Collections.singletonList(field));
    assertFalse(details.isEmpty());
    for (int p=1; p <= Field.MAX_SIZE; p++) {
      Field f = details.getField(p);
      if (p == 3) {
        assertSame(field, f);
      } else {
        assertNull(f);
      }
    }
    for (Field f : details) {
      assertSame(field, f);
    }
  }

  @Test
  public void testMultiple() {
    Field field1 = new Field(1,"color","red", true, "Colour", "rgb");
    Field field2 = new Field(2,"shape","square", false, "Shape", "geometric");
    Field field3 = new Field(3,"size","big");
    Details details = new Details(field1, field2, field3);
    assertFalse(details.isEmpty());
    for (int p=1; p <= Field.MAX_SIZE; p++) {
      Field f = details.getField(p);
      if (p == 1) {
        assertSame(field1, f);
      } else if (p == 2) {
        assertSame(field2, f);
      } else if (p == 3) {
        assertSame(field3, f);
      } else {
        assertNull(f);
      }
    }
    int count = 0;
    for (Field f : details) count++;
    assertEquals(3, count);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testPositionZero() {
    Details.NO_DETAILS.getField(0);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testPositionMax() {
    Details.NO_DETAILS.getField(Field.MAX_SIZE+1);
  }

  private static void assertEmptyDetails(Details empty) {
    assertTrue(empty.isEmpty());
    for (int p=1; p <= Field.MAX_SIZE; p++) {
      Field field = empty.getField(p);
      assertNull(field);
    }
    for (Field field : empty) {
      throw new AssertionError("We should only iterate over non-null fields!");
    }
  }

}
