package org.pageseeder.bridge.http;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

public final class HeaderTest {

  @Test
  public void testToCharset() {
    Assert.assertNull(Header.toCharset(null));
    Assert.assertNull(Header.toCharset("text/html"));
    Assert.assertEquals(StandardCharsets.UTF_8, Header.toCharset("text/html; charset=utf-8"));
    Assert.assertEquals(StandardCharsets.UTF_8, Header.toCharset("text/html; charset=UTF-8"));
    Assert.assertEquals(StandardCharsets.UTF_8, Header.toCharset("text/html; CHARSET=UTF-8"));
    Assert.assertEquals(StandardCharsets.UTF_8, Header.toCharset("text/html;CHARSET=UTF-8"));
    Assert.assertEquals(StandardCharsets.UTF_8, Header.toCharset("application/xml"));
    Assert.assertEquals(StandardCharsets.UTF_8, Header.toCharset("application/vnd.pageseeder.psml+xml"));
    Assert.assertEquals(StandardCharsets.UTF_8, Header.toCharset("application/xml;charset=utf-8"));
    Assert.assertEquals(StandardCharsets.ISO_8859_1, Header.toCharset("application/xml; charset=iso-8859-1"));
    Assert.assertEquals(StandardCharsets.ISO_8859_1, Header.toCharset("text/html; charset=iso-8859-1"));
  }

  @Test
  public void testToMediatype() {
    Assert.assertNull(Header.toMediaType(null));
    Assert.assertEquals("text/html", Header.toMediaType("text/html; charset=utf-8"));
    Assert.assertEquals("text/html", Header.toMediaType("text/html;CHARSET=UTF-8"));
    Assert.assertEquals("application/xml", Header.toMediaType("application/xml"));
    Assert.assertEquals("application/vnd.pageseeder.psml+xml", Header.toMediaType("application/vnd.pageseeder.psml+xml"));
    Assert.assertEquals("application/vnd.pageseeder.psml+xml", Header.toMediaType("application/vnd.pageseeder.psml+xml; charset=utf-8"));
    Assert.assertEquals("application/xml", Header.toMediaType("application/xml;charset=utf-8"));
    Assert.assertEquals("image/jpeg", Header.toMediaType("image/jpeg"));
    Assert.assertEquals("video/mpeg", Header.toMediaType("video/mpeg"));
  }
}
