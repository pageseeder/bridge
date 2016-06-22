package org.pageseeder.bridge.http;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.bridge.model.PSGroup;
import org.pageseeder.bridge.model.PSMember;

public class ServicePathTest {


  private static final String[] invalid_templates = new String[]{
    "",
    "/",
    "/members/{member:member}",
    "/members{member}"
  };

  private static final String[] valid_templates = new String[]{
    "/members",
    "/members/{member}",
    "/members/{member}/comments"
  };

  @Test
  public void testConstructor() {
    for (String t : invalid_templates) {
      Assert.assertEquals(t, new ServicePath(t).template());
    }
    for (String t : valid_templates) {
      Assert.assertEquals(t, new ServicePath(t).template());
    }
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_Null() {
    new ServicePath(null);
  }

  @Test
  public void testFactory() {
    for (String t : valid_templates) {
      Assert.assertEquals(t, ServicePath.newServicePath(t).template());
    }
  }

  @Test(expected = NullPointerException.class)
  public void testFactory_Null() {
    ServicePath.newServicePath(null);
  }

  @Test
  public void testFactory_Illegal() {
    for (String t : invalid_templates) {
      try {
        ServicePath.newServicePath(t).template();
        Assert.fail("Template '"+t+"' should throw an IllegalArgumentException");
      } catch (IllegalArgumentException ex) {
        // Ignore
      }
    }
  }

  @Test
  public void testCount() {
    Assert.assertEquals(0, new ServicePath("/members").count());
    Assert.assertEquals(1, new ServicePath("/members/{member}").count());
    Assert.assertEquals(1, new ServicePath("/members/{member}/comments").count());
    Assert.assertEquals(2, new ServicePath("/members/{member}/groups/{group}").count());
    Assert.assertEquals(3, new ServicePath("/members/{member}/groups/{group}/uris/{uri}").count());
  }

  @Test
  public void testToPath_Static() {
    Assert.assertEquals("/members", new ServicePath("/members").toPath());
    Assert.assertEquals("/about", new ServicePath("/about").toPath());
  }

  @Test
  public void testToPath_String() {
    Assert.assertEquals("/members/~abc",   new ServicePath("/members/{member}").toPath("abc"));
    Assert.assertEquals("/groups/~abc",    new ServicePath("/groups/{group}").toPath("abc"));
    Assert.assertEquals("/caches/abc",     new ServicePath("/caches/{name}").toPath("abc"));
  }

  @Test
  public void testToPath_Numbers() {
    // Integers
    Assert.assertEquals("/members/123",   new ServicePath("/members/{member}").toPath(123));
    Assert.assertEquals("/groups/123",    new ServicePath("/groups/{group}").toPath(123));
    Assert.assertEquals("/uris/123",      new ServicePath("/uris/{uri}").toPath(123));
    Assert.assertEquals("/caches/123",    new ServicePath("/caches/{name}").toPath(123));
    // Longs
    Assert.assertEquals("/members/123",   new ServicePath("/members/{member}").toPath(123L));
    Assert.assertEquals("/groups/123",    new ServicePath("/groups/{group}").toPath(123L));
    Assert.assertEquals("/uris/123",      new ServicePath("/uris/{uri}").toPath(123L));
    Assert.assertEquals("/caches/123",    new ServicePath("/caches/{name}").toPath(123L));
  }

  @Test
  public void testToPath_Escape() {
    Assert.assertEquals("/members/~a%20b", new ServicePath("/members/{member}").toPath("a b"));
    Assert.assertEquals("/members/~john%40example.org", new ServicePath("/members/{member}").toPath("john@example.org"));
    Assert.assertEquals("/groups/~a%20b",  new ServicePath("/groups/{group}").toPath("a b"));
    Assert.assertEquals("/uris/a%20b",    new ServicePath("/uris/{uri}").toPath("a b"));
    Assert.assertEquals("/caches/a%20b",   new ServicePath("/caches/{name}").toPath("a b"));
  }

  @Test
  public void testToPath_EntitiesWithID() {
    Assert.assertEquals("/members/123",   new ServicePath("/members/{member}").toPath(new PSMember(123L)));
    Assert.assertEquals("/groups/123",    new ServicePath("/groups/{group}").toPath(new PSGroup(123L)));
  }

  @Test
  public void testToPath_EntitiesWithName() {
    Assert.assertEquals("/members/~me",   new ServicePath("/members/{member}").toPath(new PSMember("me")));
    Assert.assertEquals("/groups/~example-test",    new ServicePath("/groups/{group}").toPath(new PSGroup("example-test")));
  }

  @Test(expected = NullPointerException.class)
  public void testToPath_Null() {
    ServicePath.newServicePath("/members/{member}").toPath(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToPath_Missing1() {
    ServicePath.newServicePath("/members/{member}").toPath();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToPath_Missing2() {
    ServicePath.newServicePath("/members/{member}/comments/{comment}").toPath();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToPath_Extra1() {
    ServicePath.newServicePath("/members").toPath("me");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToPath_Extra2() {
    ServicePath.newServicePath("/members/{member}").toPath("me", "you");
  }
}
