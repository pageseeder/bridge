package org.pageseeder.bridge.http;

import org.junit.Assert;
import org.junit.Test;

public class DocumentPathTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_Null() {
    new DocumentPath((String)null);
  }

  @Test(expected = NullPointerException.class)
  public void testNewLocalPath1_Null() {
    DocumentPath.newLocalPath(null, "");
  }

  @Test(expected = NullPointerException.class)
  public void testNewLocalPath2_Null() {
    DocumentPath.newLocalPath("", null);
  }

  @Test
  public void testPath() {
    Assert.assertEquals("/", new DocumentPath("").path());
    Assert.assertEquals("/", new DocumentPath("/").path());
    Assert.assertEquals("/folder", new DocumentPath("folder").path());
    Assert.assertEquals("/folder", new DocumentPath("/folder").path());
    Assert.assertEquals("/folder", new DocumentPath("/folder/").path());
    Assert.assertEquals("/folder", new DocumentPath("folder/").path());
    Assert.assertEquals("/folder/file.xml", new DocumentPath("folder/file.xml").path());
    Assert.assertEquals("/folder/sub/file.xml", new DocumentPath("folder/sub/file.xml").path());
    Assert.assertEquals("/folder/sub/file.xml", new DocumentPath("/folder/sub/file.xml").path());
  }

  @Test
  public void testNewLocalPath() {
    Assert.assertEquals("/test/file.psml", DocumentPath.newLocalPath("test", "file.psml").path());
    Assert.assertEquals("/test/file.psml", DocumentPath.newLocalPath("test", "file.psml").path());
    Assert.assertEquals("/test/file.psml", DocumentPath.newLocalPath("test", "/file.psml").path());
    Assert.assertEquals("/test/example/file.psml", DocumentPath.newLocalPath("test-example", "file.psml").path());
    Assert.assertEquals("/test/example/file.psml", DocumentPath.newLocalPath("test-example", "/file.psml").path());
    Assert.assertEquals("/test/sub/example/file.psml", DocumentPath.newLocalPath("test-sub-example", "/file.psml").path());
    Assert.assertEquals("/test/sub/example/folder/file.psml", DocumentPath.newLocalPath("test-sub-example", "/folder/file.psml").path());
  }

  @Test
  public void testSize() {
    Assert.assertEquals(0, new DocumentPath("").size());
    Assert.assertEquals(0, new DocumentPath("/").size());
    Assert.assertEquals(1, new DocumentPath("folder").size());
    Assert.assertEquals(1, new DocumentPath("/folder").size());
    Assert.assertEquals(1, new DocumentPath("/folder/").size());
    Assert.assertEquals(1, new DocumentPath("folder/").size());
    Assert.assertEquals(2, new DocumentPath("folder/file.xml").size());
    Assert.assertEquals(3, new DocumentPath("folder/sub/file.xml").size());
    Assert.assertEquals(3, new DocumentPath("/folder/sub/file.xml").size());
    Assert.assertEquals(4, new DocumentPath("a/ab//c///d/").size());
  }

  @Test
  public void testParent() {
    Assert.assertNull(new DocumentPath("").parent());
    Assert.assertNull(new DocumentPath("/").parent());
    Assert.assertEquals(new DocumentPath("/"), new DocumentPath("folder").parent());
    Assert.assertEquals(new DocumentPath("/"), new DocumentPath("/folder").parent());
    Assert.assertEquals(new DocumentPath("/"), new DocumentPath("/folder/").parent());
    Assert.assertEquals(new DocumentPath("/"), new DocumentPath("folder/").parent());
    Assert.assertEquals(new DocumentPath("/folder"), new DocumentPath("folder/file.xml").parent());
    Assert.assertEquals(new DocumentPath("/folder/sub"), new DocumentPath("folder/sub/file.xml").parent());
    Assert.assertEquals(new DocumentPath("/folder/sub"), new DocumentPath("/folder/sub/file.xml").parent());
  }

  @Test
  public void testChild() {
    Assert.assertEquals("/test",                new DocumentPath("/").child("test").path());
    Assert.assertEquals("/folder/test.html",    new DocumentPath("/folder").child("test.html").path());
    Assert.assertEquals("/folder/sub/file.xml", new DocumentPath("folder/sub").child("file.xml").path());
    Assert.assertEquals("/folder/sub/file.xml", new DocumentPath("/folder/sub/").child("file.xml").path());
    Assert.assertEquals("/folder/sub/file.xml", new DocumentPath("/folder/sub/").child("/file.xml").path());
    Assert.assertEquals("/folder/sub/file.xml", new DocumentPath("/folder/").child("/sub/file.xml").path());
    Assert.assertEquals("/folder/sub/file.xml", new DocumentPath("/folder").child("sub/file.xml").path());
    Assert.assertEquals("/folder/sub/file.xml", new DocumentPath("folder/").child("/sub/file.xml").path());
    Assert.assertEquals("/folder/sub/file.xml", new DocumentPath("folder").child("sub/file.xml/").path());
    // dot notation
    Assert.assertEquals("/folder", new DocumentPath("folder").child(".").path());
    Assert.assertEquals("/folder", new DocumentPath("folder").child("").path());
    Assert.assertEquals("/folder/sub/file.xml", new DocumentPath("folder").child("/sub/./file.xml").path());
    Assert.assertEquals("/folder/file.xml", new DocumentPath("folder").child("/sub/../file.xml").path());
  }

  @Test(expected = NullPointerException.class)
  public void testChild_null() {
    new DocumentPath("/").child(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testChild_Illegal() {
    new DocumentPath("/folder").child("..");
  }

}
