package org.pageseeder.bridge.http;

import org.junit.Assert;
import org.junit.Test;

public class ServiceErrorTest {

  @Test(expected = NumberFormatException.class)
  public void testConstructor_NullCode() {
    new ServiceError(null, "");
  }

  @Test(expected = NumberFormatException.class)
  public void testConstructor_InvalidCode() {
    new ServiceError("X", "");
  }

  @Test
  public void testConstructor() {
    ServiceError se = new ServiceError("0100", "Not looking good");
    Assert.assertEquals(256, se.id());
    Assert.assertEquals("0100", se.code());
    Assert.assertEquals("Not looking good", se.message());
  }

  @Test
  public void testCode() {
    Assert.assertEquals("0000", ServiceError.code(0));
    Assert.assertEquals("0001", ServiceError.code(1));
    Assert.assertEquals("000A", ServiceError.code(10));
    Assert.assertEquals("0064", ServiceError.code(100));
    Assert.assertEquals("0400", ServiceError.code(1024));
    Assert.assertEquals("FFFF", ServiceError.code(65535));
    Assert.assertEquals("10000", ServiceError.code(65536));
    Assert.assertEquals("0010", ServiceError.code(0x10));
    Assert.assertEquals("0100", ServiceError.code(0x100));
    Assert.assertEquals("1000", ServiceError.code(0x1000));
    Assert.assertEquals("FFFF", ServiceError.code(0xffff));
  }

}
