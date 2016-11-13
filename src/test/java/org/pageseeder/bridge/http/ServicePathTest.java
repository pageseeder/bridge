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
    Assert.assertEquals("/service/members", new ServicePath("/members").toPath());
    Assert.assertEquals("/service/about", new ServicePath("/about").toPath());
  }

  @Test
  public void testToPath_String() {
    Assert.assertEquals("/service/members/~abc",   new ServicePath("/members/{member}").toPath("abc"));
    Assert.assertEquals("/service/groups/~abc",    new ServicePath("/groups/{group}").toPath("abc"));
    Assert.assertEquals("/service/caches/abc",     new ServicePath("/caches/{name}").toPath("abc"));
  }

  @Test
  public void testToPath_Numbers() {
    // Integers
    Assert.assertEquals("/service/members/123",   new ServicePath("/members/{member}").toPath(123));
    Assert.assertEquals("/service/groups/123",    new ServicePath("/groups/{group}").toPath(123));
    Assert.assertEquals("/service/uris/123",      new ServicePath("/uris/{uri}").toPath(123));
    Assert.assertEquals("/service/caches/123",    new ServicePath("/caches/{name}").toPath(123));
    // Longs
    Assert.assertEquals("/service/members/123",   new ServicePath("/members/{member}").toPath(123L));
    Assert.assertEquals("/service/groups/123",    new ServicePath("/groups/{group}").toPath(123L));
    Assert.assertEquals("/service/uris/123",      new ServicePath("/uris/{uri}").toPath(123L));
    Assert.assertEquals("/service/caches/123",    new ServicePath("/caches/{name}").toPath(123L));
  }

  @Test
  public void testToPath_Escape() {
    Assert.assertEquals("/service/members/~a%20b", new ServicePath("/members/{member}").toPath("a b"));
    Assert.assertEquals("/service/members/~john%40example.org", new ServicePath("/members/{member}").toPath("john@example.org"));
    Assert.assertEquals("/service/groups/~a%20b",  new ServicePath("/groups/{group}").toPath("a b"));
    Assert.assertEquals("/service/uris/a%20b",    new ServicePath("/uris/{uri}").toPath("a b"));
    Assert.assertEquals("/service/caches/a%20b",   new ServicePath("/caches/{name}").toPath("a b"));
  }

  @Test
  public void testToPath_EntitiesWithID() {
    Assert.assertEquals("/service/members/123",   new ServicePath("/members/{member}").toPath(new PSMember(123L)));
    Assert.assertEquals("/service/groups/123",    new ServicePath("/groups/{group}").toPath(new PSGroup(123L)));
  }

  @Test
  public void testToPath_EntitiesWithName() {
    Assert.assertEquals("/service/members/~me",   new ServicePath("/members/{member}").toPath(new PSMember("me")));
    Assert.assertEquals("/service/groups/~example-test",    new ServicePath("/groups/{group}").toPath(new PSGroup("example-test")));
  }

  @Test(expected = NullPointerException.class)
  public void testToPath_Null() {
    ServicePath.newServicePath("/service/members/{member}").toPath((String)null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToPath_Missing1() {
    ServicePath.newServicePath("/service/members/{member}").toPath();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToPath_Missing2() {
    ServicePath.newServicePath("/service/members/{member}/comments/{comment}").toPath();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToPath_Extra1() {
    ServicePath.newServicePath("/service/members").toPath("me");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToPath_Extra2() {
    ServicePath.newServicePath("/service/members/{member}").toPath("me", "you");
  }
}
