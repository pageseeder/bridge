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

import org.junit.Test;
import org.pageseeder.bridge.core.Details;
import org.pageseeder.bridge.core.Field;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static org.junit.Assert.*;

public final class XMLStreamDetailsTest {

  @Test
  public void testEmpty() throws IOException, XMLStreamException {
    Details details = XMLStreamTest.parseItem("details/details-empty.xml", new XMLStreamDetails());
    assertTrue(details.isEmpty());
    for (int position=1; position <= Field.MAX_SIZE; position++) {
      assertNull(details.getField(position));
    }
  }

  @Test
  public void testSingleField() throws IOException, XMLStreamException {
    Details details = XMLStreamTest.parseItem("details/details-field.xml", new XMLStreamDetails());
    assertFalse(details.isEmpty());
    for (int position=1; position <= Field.MAX_SIZE; position++) {
      if (position != 7) assertNull(details.getField(position));
    }
    Field field = details.getField(7);
    assertNotNull(field);
    assertEquals(7, field.getPosition());
    assertEquals("number", field.getName());
    assertEquals("A number", field.getTitle());
    assertEquals("natural", field.getType());
    assertEquals("seven", field.getValue());
    assertTrue(field.isEditable());
  }

  @Test
  public void testPassNoUsername() throws IOException, XMLStreamException {
    Details details = XMLStreamTest.parseItem("details/details-fields.xml", new XMLStreamDetails());
    assertFalse(details.isEmpty());
    for (int position=4; position <= Field.MAX_SIZE; position++) {
      Field field = details.getField(position);
      if (position == 1) {
        // <field position="1" name="nickname">Bill</field>
        Field expected = new Field(position, "nickname", "Bill", false, "nickname", Field.DEFAULT_TYPE);
        assertFieldEquals(expected, field);
      } else if (position == 2) {
        // <field position="2" name="title" editable="true">Mr</field>
        Field expected = new Field(position, "title", "Mr", true, "title", Field.DEFAULT_TYPE);
        assertFieldEquals(expected, field);
      } else if (position == 3) {
        // <field position="3" name="color" editable="true" title="Color">Red</field>
        Field expected = new Field(position, "color", "Red", true, "Color", Field.DEFAULT_TYPE);
        assertFieldEquals(expected, field);
      } else if (position == 5) {
        // <field position="5" name="shape" editable="true"  title="Shape / form"  type="geometric">circle</field>
        Field expected = new Field(position, "shape", "circle", true, "Shape / form", "geometric");
        assertFieldEquals(expected, field);
      } else {
        assertNull(details.getField(position));
      }
    }
  }



  // TODO Check failures too


  private static void assertFieldEquals(Field expected, Field got) {
    assertNotNull(got);
    assertEquals(expected.getPosition(), got.getPosition());
    assertEquals(expected.getName(),    got.getName());
    assertEquals(expected.getTitle(),   got.getTitle());
    assertEquals(expected.getType(),    got.getType());
    assertEquals(expected.getValue(),   got.getValue());
    assertEquals(expected.isEditable(), got.isEditable());
  }

}
