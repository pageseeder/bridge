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
import org.pageseeder.bridge.core.URI;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.time.OffsetDateTime;

public final class XMLStreamURITest {

  @Test
  public void testDocument() throws IOException, XMLStreamException {
    URI uri = XMLStreamTest.parseItem("uri/document.xml", new XMLStreamURI());
    Assert.assertNotNull(uri);
    Assert.assertEquals(123456L, uri.getId());
    Assert.assertFalse(uri.isExternal());
    Assert.assertFalse(uri.isFolder());
    Assert.assertEquals("https", uri.getScheme());
    Assert.assertEquals("ps.example.com", uri.getHost());
    Assert.assertEquals(443, uri.getPort());
    Assert.assertEquals("/ps/test/acme/documents/hello%20world.psml", uri.getPath());
    Assert.assertEquals("/ps/test/acme/documents/hello world.psml", uri.getDecodedPath());
    Assert.assertEquals("Hello World!", uri.getTitle());
    Assert.assertEquals("application/vnd.pageseeder.psml+xml", uri.getMediaType());
    Assert.assertEquals("Hello World!", uri.getDisplayTitle());
    Assert.assertEquals(OffsetDateTime.parse("2008-03-17T17:11:18+11:00"), uri.getCreatedDate());
    Assert.assertEquals(OffsetDateTime.parse("2017-01-03T14:45:21+11:00"), uri.getModifiedDate());
    Assert.assertEquals("HELLO_WORLD", uri.getDocid());
  }

  @Test
  public void testFolder() throws IOException, XMLStreamException {
    URI uri = XMLStreamTest.parseItem("uri/folder.xml", new XMLStreamURI());
    Assert.assertNotNull(uri);
    Assert.assertEquals(123L, uri.getId());
    Assert.assertFalse(uri.isExternal());
    Assert.assertTrue(uri.isFolder());
    Assert.assertEquals("https", uri.getScheme());
    Assert.assertEquals("ps.example.com", uri.getHost());
    Assert.assertEquals(443, uri.getPort());
    Assert.assertEquals("/ps/test/acme/documents", uri.getPath());
    Assert.assertEquals("/ps/test/acme/documents", uri.getDecodedPath());
    Assert.assertEquals("folder", uri.getMediaType());
    Assert.assertEquals( "", uri.getTitle());
    Assert.assertEquals("documents", uri.getDisplayTitle());
    Assert.assertNull(uri.getCreatedDate());
    Assert.assertEquals(OffsetDateTime.parse("2017-01-03T14:45:21+11:00"), uri.getModifiedDate());
    Assert.assertNull(uri.getDocid());
  }
}
