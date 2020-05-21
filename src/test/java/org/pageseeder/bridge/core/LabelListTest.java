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

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class LabelListTest {

  @Test
  public void testNoLabels() {
    LabelList empty = LabelList.NO_LABELS;
    assertTrue(empty.isEmpty());
    assertTrue(empty.toList().isEmpty());
    assertEquals(0, empty.size());
    for (String label : empty) {
      throw new AssertionError("We should only iterate over non-null fields!");
    }
    assertEquals("", empty.toString());
  }

  @Test
  public void testListConstructorEmpty() {
    assertEquals(LabelList.NO_LABELS, new LabelList(Collections.emptyList()));
    assertEquals(LabelList.NO_LABELS, new LabelList(Collections.singletonList("")));
    assertEquals(LabelList.NO_LABELS, new LabelList(Collections.singletonList(" ")));
    assertEquals(LabelList.NO_LABELS, new LabelList(Collections.singletonList(",")));
    assertEquals(LabelList.NO_LABELS, new LabelList(Arrays.asList(""," ", null, ",","@")));
  }

  @Test
  public void testListConstructorFilter() {
    assertEquals(Collections.singletonList("a"), new LabelList(Collections.singletonList("a")).toList());
    assertEquals(Arrays.asList("a","b"), new LabelList(Arrays.asList("a", "b")).toList());
    assertEquals(Arrays.asList("a","b"), new LabelList(Arrays.asList("a", "", "b")).toList());
    assertEquals(Arrays.asList("a","b"), new LabelList(Arrays.asList("a", "", null, "b", ",", " ")).toList());
  }

  @Test
  public void testListConstructorTrim() {
    assertEquals(Arrays.asList("a","b","c"), new LabelList(Arrays.asList("  a", " \tb\n","c ")).toList());
  }

  @Test
  public void testParseEmpty() {
    assertEquals(LabelList.NO_LABELS, LabelList.parse(""));
    assertEquals(LabelList.NO_LABELS, LabelList.parse(null));
    assertEquals(LabelList.NO_LABELS, LabelList.parse(" "));
    assertEquals(LabelList.NO_LABELS, LabelList.parse(","));
    assertEquals(LabelList.NO_LABELS, LabelList.parse(",,"));
    assertEquals(LabelList.NO_LABELS, LabelList.parse(" , , "));
    assertEquals(LabelList.NO_LABELS, LabelList.parse(" ,\n,@\t,,,"));
    assertEquals(LabelList.NO_LABELS, LabelList.parse(" ,\n,@\t,a&b,,"));
  }

  @Test
  public void testParseFilter() {
    assertEquals(Collections.singletonList("a"), LabelList.parse("a").toList());
    assertEquals(Arrays.asList("a","b"), LabelList.parse("a,b").toList());
    assertEquals(Arrays.asList("a","b"), LabelList.parse("a,,b").toList());
    assertEquals(Arrays.asList("a","b"), LabelList.parse(",,a,,b,,").toList());
    assertEquals(Arrays.asList("a","b"), LabelList.parse(",,a,a$b,b,,").toList());
    assertEquals(Arrays.asList("a","b"), LabelList.parse(",,a,a b,b,,").toList());
  }

  @Test
  public void testParseTrim() {
    assertEquals(Collections.singletonList("a"), LabelList.parse("  a").toList());
    assertEquals(Collections.singletonList("b"), LabelList.parse(" \tb\n ").toList());
    assertEquals(Collections.singletonList("c"), LabelList.parse("c\t ").toList());
    assertEquals(Arrays.asList("a","b"), LabelList.parse("a,b").toList());
    assertEquals(Arrays.asList("a","b"), LabelList.parse(" a,b").toList());
    assertEquals(Arrays.asList("a","b"), LabelList.parse(" a,b ").toList());
    assertEquals(Arrays.asList("a","b","c"), LabelList.parse("  a, b ,c ").toList());
    assertEquals(Arrays.asList("a","b","c"), LabelList.parse("  a, b ,c ").toList());
    assertEquals(Arrays.asList("a","b","c"), LabelList.parse("  a, \tb\n,c ").toList());
  }
}
