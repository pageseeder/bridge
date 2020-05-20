package org.pageseeder.bridge.http;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSSession;

public class ResponseTest {

  @Before
  public void setUp() {
    PSConfig config = PSConfig.newInstance("https://ps.pageseeder.com");
    PSConfig.setDefault(config);
  }

  /**
   * Simple request unauthenticated request
   */
  @Test
  public void testOK() {
    Response response = Request.response(Method.GET, Service.get_version);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertTrue(response.isXML());
  }

  /**
   * Check that the response is NotXML
   */
  @Test
  public void testNotXML() {
    Response response = Request.response(Method.GET, "/weborganic/favicon.ico");
    Assert.assertTrue(response.isSuccessful());
    Assert.assertFalse(response.isXML());
  }

  /**
   * Check that the response is XML
   */
  @Test
  public void testIsXML() {
    Response response = Request.response(Method.GET, Service.get_version);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertTrue(response.isXML());
  }

  @Test
  public void testETag() {
    Response response = Request.response(Method.GET, Service.get_version);
    Assert.assertTrue(response.isSuccessful());
    String etag = response.etag();
    Assert.assertNotNull(etag);
    Assert.assertTrue(etag.length() > 0);
    Assert.assertTrue(etag.indexOf('"') < 0);
  }

  @Test
  public void testSession() {
    Response response = Request.response(Method.GET, Service.get_version);
    Assert.assertTrue(response.isSuccessful());
    if (response.header("Set-Cookie") != null) {
      PSSession session = response.session();
      Assert.assertNotNull(session);
    } else {
      System.out.println("Unable to test if session works: no 'Set-Cookie' header");
    }
  }

  @Test
  public void testConsume() {
    Response response = Request.response(Method.HEAD, Service.get_version);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertTrue(response.isXML());
    response.consumeBytes(System.out);
  }

  @Test
  public void testConsumeAndWrite() {
    Response response = Request.response(Method.HEAD, Service.get_version);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertTrue(response.isXML());
    response.consumeBytes(System.out);
  }


}
