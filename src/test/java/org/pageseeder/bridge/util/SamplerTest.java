package org.pageseeder.bridge.util;

import org.junit.Assert;
import org.junit.Test;

public final class SamplerTest {

  @Test
  public void testNextFirstName() {
    Sampler sampler = new Sampler();
    String a = sampler.nextFirstName();
    Assert.assertNotNull(a);
    String b = sampler.nextFirstName();
    Assert.assertNotNull(b);
    Assert.assertNotEquals(a, b);
  }

  @Test
  public void testNextLastName() {
    Sampler sampler = new Sampler();
    String a = sampler.nextLastName();
    Assert.assertNotNull(a);
    String b = sampler.nextLastName();
    Assert.assertNotNull(b);
    Assert.assertNotEquals(a, b);
  }

}
