package org.pageseeder.bridge.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.junit.Assert;
import org.junit.Test;

public class Base64Test {

  private static final byte[] ZERO_BYTES = new byte[0];

  private static final List<String> SAMPLES = Arrays
      .asList(" ", "Test", "Hello", "~~~", "Me?", "~~~Me? ", "Café");

  @Test
  public void testEncodeBase64Empty() {
    Assert.assertEquals(0L, Base64.encode(ZERO_BYTES).length());
    Assert.assertEquals(0L, Base64.encodeURL(ZERO_BYTES).length());
  }

  @Test
  public void testEncodeBase64() {
    for (String sample : SAMPLES) {
      byte[] bytes = sample.getBytes(StandardCharsets.UTF_8);
      String exp = DatatypeConverter.printBase64Binary(bytes);
      String got = Base64.encode(bytes);
      Assert.assertEquals(exp, got);
    }
  }

  @Test
  public void testEncodeBase64url() {
    for (String sample : SAMPLES) {
      byte[] bytes = sample.getBytes(StandardCharsets.UTF_8);
      String exp = DatatypeConverter.printBase64Binary(bytes).replace('+', '-').replace('/', '_');
      if (exp.indexOf('=') >= 0) {
        exp = exp.substring(0, exp.indexOf(61));
      }
      String got = new String(Base64.encodeURL(bytes));
      Assert.assertEquals(exp, got);
    }
  }

  @Test
  public void testDecodeBase64Empty() {
    Assert.assertEquals(0L, Base64.decode("").length);
    Assert.assertEquals(0L, Base64.decodeURL("").length);
  }

  @Test
  public void testDecodeBase64() {
    for (String sample : SAMPLES) {
      byte[] data = sample.getBytes(StandardCharsets.UTF_8);
      String b64 = DatatypeConverter.printBase64Binary(data);
      byte[] got = Base64.decode(b64);
      Assert.assertArrayEquals(data, got);
    }
  }

  @Test
  public void testDecodeBase64url() {
    for (String sample : SAMPLES) {
      byte[] data = sample.getBytes(StandardCharsets.UTF_8);
      String b64url = DatatypeConverter.printBase64Binary(data).replace('+', '-').replace('/', '_');
      if (b64url.indexOf('=') >= 0) {
        b64url = b64url.substring(0, b64url.indexOf('='));
      }
      byte[] got = Base64.decodeURL(b64url);
      Assert.assertArrayEquals(data, got);
    }
  }

  @Test
  public void testRoundtripBase64() {
    for (String sample : SAMPLES) {
      byte[] data = sample.getBytes(StandardCharsets.UTF_8);
      Assert.assertArrayEquals(data, Base64.decode(Base64.encode(data)));
    }
  }

  @Test
  public void testRoundtripBase64url() {
    for (String sample : SAMPLES) {
      byte[] data = sample.getBytes(StandardCharsets.UTF_8);
      Assert.assertArrayEquals(data, Base64.decodeURL(Base64.encodeURL(data)));
    }
  }

  @Test(expected = NullPointerException.class)
  public void testEncodeBase64Null() {
    Base64.encode(null);
  }

  @Test(expected = NullPointerException.class)
  public void testEncodeBase64urlNull() {
    Base64.encodeURL(null);
  }

  @Test(expected = NullPointerException.class)
  public void testDecodeBase64Null() {
    Base64.decode(null);
  }

  @Test(expected = NullPointerException.class)
  public void testDecodeBase64urlNull() {
    Base64.decodeURL(null);
  }

  @Test
  public void testDecodeBase64IllegalLength() {
    // XXX: Datatype converter is lenient with strings of incorrect length
    Base64.decode("abc");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDecodeBase64urlIllegalLength() {
    Base64.decodeURL("a");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDecodeBase64IllegalChar() {
    Assert.assertArrayEquals(ZERO_BYTES, Base64.decode("abc#"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDecodeBase64urlIllegalChar() {
    Assert.assertArrayEquals(ZERO_BYTES, Base64.decodeURL("abc#"));
  }

}
