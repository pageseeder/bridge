package org.pageseeder.bridge;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class VersionTest {

  @Test
  public void testToVersion() {
    List<String> strings = Arrays.asList("2", "3.01", "5.9", "5.91", "5.99999");
    List<Version> versions = Arrays.asList(
      new Version(2,    0, "2.0000"),
      new Version(3,  100, "3.0100"),
      new Version(5, 9000, "5.9000"),
      new Version(5, 9100, "5.9100"),
      new Version(5, 9999, "5.9999")
    );
    for (int i=0; i < strings.size(); i++) {
      Version got = Version.parse(strings.get(i));
      Version exp = versions.get(i);
      Assert.assertEquals(exp.major(), got.major());
      Assert.assertEquals(exp.build(), got.build());
      Assert.assertEquals(exp.version(), got.version());
    }
  }

  @Test(expected = NullPointerException.class)
  public void testToVersion_Null() {
    Version.parse(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToVersion_Illegal() {
    Version.parse("");
  }

}
