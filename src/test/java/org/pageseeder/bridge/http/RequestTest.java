package org.pageseeder.bridge.http;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.PSToken;
import org.pageseeder.bridge.net.UsernamePassword;

public final class RequestTest {

  @Before
  public void setUp() {
    PSConfig config = PSConfig.newInstance("https://ps.pageseeder.com");
    PSConfig.setDefault(config);
  }

  // Setting and getting class attributes
  // --------------------------------------------------------------------------

  @Test
  public void testHeaders() {
    Request request = new Request(Method.GET, Service.get_version);
    request.header("a", "1");
    request.header("b", "2");
    // Check values
    Assert.assertEquals("1", request.header("a"));
    Assert.assertEquals("1", request.header("A"));
    Assert.assertEquals("2", request.header("b"));
    Assert.assertNull(request.header("c"));
    // Updates
    request.header("A", "3");
    Assert.assertEquals("3", request.header("a"));
    Assert.assertEquals("3", request.header("A"));
  }

  /**
   * Test that setting basic credentials automatically updates the "Authorization" header
   */
  @Test
  public void testCredentials_Basic() {
    PSCredentials credentials = new UsernamePassword("ali", "baba");
    Request request = new Request(Method.GET, Service.get_version).using(credentials);
    Assert.assertEquals("Basic YWxpOmJhYmE=", request.header("Authorization"));
    Assert.assertEquals(credentials, request.credentials());
  }

  /**
   * Test that setting an OAuth token automatically updates the "Authorization" header
   */
  @Test
  public void testCredentials_Token() {
    PSToken token = new PSToken("SXDCVgyfudsoai784dfh34qdryqyfdfdyjs");
    Request request = new Request(Method.GET, Service.get_version).using(token);
    Assert.assertEquals("Bearer "+token.token(), request.header("Authorization"));
    Assert.assertEquals(token, request.credentials());
  }

  /**
   * Test that setting a session automatically updates the "Authorization" header.
   */
  @Test
  public void testCredentials_Session() {
    PSSession session = new PSSession("SXDCVgyfudsoai784dfh34qdryqyfdfdyjs");
    Request request = new Request(Method.GET, Service.get_version).using(session);
    Assert.assertNull(request.header("Authorization"));
    Assert.assertEquals(session, request.credentials());
  }

  /**
   * Test that using to <code>null</code> removes the credentials and "Authorization" header.
   */
  @Test
  public void testCredentials_Null() {
    Request request = new Request(Method.GET, Service.get_version).using(null);
    Assert.assertNull(request.header("Authorization"));
    Assert.assertNull(request.credentials());
  }

  /**
   * Test that using the PATCH method set the "X-HTTP-Method-Override" to "PATCH"
   */
  @Test
  public void testPatchHeader() {
    Request r1 = new Request(Method.PATCH, "/index.html");
    Assert.assertEquals("PATCH", r1.header("X-HTTP-Method-Override"));
    Request r2 = new Request(Method.PATCH, Service.get_version);
    Assert.assertEquals("PATCH", r2.header("X-HTTP-Method-Override"));
    Request r3 = new Request(Method.PATCH, Servlet.GENERIC_SEARCH);
    Assert.assertEquals("PATCH", r3.header("X-HTTP-Method-Override"));
  }

  /**
   * Test that the "User-Agent" header is set by default
   */
  @Test
  public void testUserAgent() {
    String userAgent = BasicRequest.getUserAgentString();
    Request r1 = new Request(Method.GET, "/index.html");
    Assert.assertEquals(userAgent, r1.header("User-Agent"));
    Request r2 = new Request(Method.GET, Service.get_version);
    Assert.assertEquals(userAgent, r2.header("User-Agent"));
    Request r3 = new Request(Method.GET, Servlet.GENERIC_SEARCH);
    Assert.assertEquals(userAgent, r3.header("User-Agent"));
  }

  /**
   * Test that servlets automatically include the "xformat" attribute.
   */
  @Test
  public void testServletXFormat() {
    Request request = new Request(Method.GET, Servlet.GENERIC_SEARCH);
    Assert.assertEquals("xml", request.parameter("xformat"));
  }

  /**
   * Test that setting and getting the connection timeout.
   */
  @Test
  public void testTimeout() {
    Request request = new Request(Method.GET, Servlet.GENERIC_SEARCH);
    Assert.assertEquals(-1, request.timeout());
    request.timeout(30_000);
    Assert.assertEquals(30_000, request.timeout());
  }

  @Test
  public void testParameters() {
    Request request = new Request(Method.GET, Service.get_version);
    request.parameter("a", "1");
    request.parameter("b", "2");
    request.parameter("c", " ");
    request.parameter("d", "");
    // Check values
    Assert.assertEquals("1", request.parameter("a"));
    Assert.assertEquals("2", request.parameter("b"));
    Assert.assertEquals(" ", request.parameter("c"));
    Assert.assertEquals("", request.parameter("d"));
    Assert.assertNull(request.parameter("z"));

  }

  @Test
  public void testEncodeParameters() {
    Request request = new Request(Method.GET, Service.get_version);
    request.parameter("a", "1");
    request.parameter("b", "2");
    request.parameter("c", " ");
    request.parameter("d", "");
    request.parameter("e", "=");
    request.parameter("f", "&");
    request.parameter("g", "#");
    Assert.assertEquals("a=1&b=2&c=+&d=&e=%3D&f=%26&g=%23", request.encodeParameters());
  }

  @Test
  public void testToURL() {
    // Raw path
    Assert.assertEquals("https://ps.pageseeder.com/ps/", new Request(Method.GET, "/").toURLString());
  }

  @Test
  public void testPath_Raw() {
    // Raw path
    Assert.assertEquals("/",           new Request(Method.GET, "/").path());
    Assert.assertEquals("/index.html", new Request(Method.GET, "/index.html").path());
    Assert.assertEquals("/a/b/c",      new Request(Method.GET, "/a/b/c").path());
    Assert.assertEquals("/xyz",        new Request(Method.GET, "/xyz?").path());
    Assert.assertEquals("/xyz",        new Request(Method.GET, "/xyz?#").path());
    Assert.assertEquals("/xyz",        new Request(Method.GET, "/xyz?a=1").path());
    Assert.assertEquals("/xyz",        new Request(Method.GET, "/xyz?a=1#m").path());
    Assert.assertEquals("/xyz",        new Request(Method.GET, "/xyz?a=1&b=2#m?x=1").path());
  }

  @Test
  public void testPath_Service() {
    Assert.assertTrue(new Request(Method.GET, Service.get_version).path().startsWith("/service/"));
  }

  @Test
  public void testPath_Servlet() {
    Assert.assertTrue(new Request(Method.GET, Servlet.UPLOAD).path().startsWith("/servlet/"));
  }

  @Test
  public void testQuery() {
    // Raw path
    Assert.assertEquals("",            new Request(Method.GET, "/").encodeParameters());
    Assert.assertEquals("",            new Request(Method.GET, "/index.html").encodeParameters());
    Assert.assertEquals("",            new Request(Method.GET, "/a/b/c").encodeParameters());
    Assert.assertEquals("",            new Request(Method.GET, "/xyz?").encodeParameters());
    Assert.assertEquals("",            new Request(Method.GET, "/xyz?#").encodeParameters());
    Assert.assertEquals("a=1",         new Request(Method.GET, "/xyz?a=1").encodeParameters());
    Assert.assertEquals("a=1",         new Request(Method.GET, "/xyz?a=1#m").encodeParameters());
    Assert.assertEquals("a=1&b=2",     new Request(Method.GET, "/xyz?a=1&b=2#m?x=1").encodeParameters());
    Assert.assertEquals("",            new Request(Method.GET, "/xyz#x=1?a=1&b=2#m?y=1").encodeParameters());
  }

  // Request execution
  // --------------------------------------------------------------------------

  @Test
  public void testOK() {
    Response response = Request.response(Method.GET, Service.get_version);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.code());
    Assert.assertTrue(response.length() > 0);
  }

  @Test
  public void testMalformedURL() {
    Response response = Request.response(Method.GET, ":%*<>\n:/ %1%");
    Assert.assertFalse(response.isSuccessful());
    Assert.assertTrue(response.code() < 0);
    Assert.assertNull(response.getContentType());
    Assert.assertNull(response.etag());
    Assert.assertEquals(-1, response.length());
  }

  @Test
  public void testUnresolvedHost() {
    PSConfig config = PSConfig.newInstance("https://ps.example.org");
    PSConfig.setDefault(config);
    Response response = Request.response(Method.GET, Service.get_version);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertTrue(response.code() < 0);
    Assert.assertNull(response.getContentType());
    Assert.assertNull(response.etag());
    Assert.assertEquals(-1, response.length());
  }




  // TODO
//  @Test
//  public void testBadCertificate() {
//    PSConfig config = PSConfig.newInstance("https://localhost");
//    PSConfig.setDefault(config);
//    Response response = Request.execute(Method.GET, Service.get_version);
//    Assert.assertFalse(response.isSuccessful());
//    Assert.assertTrue(response.code() < 0);
//    Assert.assertNull(response.getContentType());
//    Assert.assertNull(response.etag());
//    Assert.assertEquals(-1, response.length());
//  }

  @Test
  public void testCannotConnect() {
    PSConfig config = PSConfig.newInstance("http://ps.pageseeder.com:7777");
    PSConfig.setDefault(config);
    Response response = new Request(Method.GET, Service.get_version).timeout(2000).response();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertTrue(response.code() < 0);
    Assert.assertNull(response.getContentType());
    Assert.assertNull(response.etag());
    Assert.assertEquals(-1, response.length());
  }

  @Test
  public void testNotFound() {
    Response response = Request.response(Method.GET, "/does/not.exist");
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(404, response.code() );
    Assert.assertTrue(response.length() > 0);
  }

  @Test
  public void testEtag() {
    // Get the etag
    String etag = Request.response(Method.GET, Service.get_version).etag();
    // Try request with etag
    Request request = new Request(Method.GET, Service.get_version);
    Assert.assertNull(request.header("If-None-Match"));
    // Add the etag
    request.etag(etag);
    String ifNoneMatch = request.header("If-None-Match");
    Assert.assertEquals('"'+etag+'"', ifNoneMatch);
    //
    Response response = request.response();
    Assert.assertEquals(HttpURLConnection.HTTP_NOT_MODIFIED, response.code());
  }

}
