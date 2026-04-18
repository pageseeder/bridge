package org.pageseeder.bridge.oauth;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class JSONParameterTest {

  @Test
  public void testParse_OK() {
    String json = "{  \"refresh_token\":\"_0EsX3fRDAGrFuzYBVJVLUxngcxHDK3O\"\n,\n\"id_token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiQ2hyaXN0b3BoZSBMYXVyZXQiLCJnaXZlbl9uYW1lIjoiQ2hyaXN0b3BoZSIsImZhbWlseV9uYW1lIjoiTGF1cmV0IiwicHJlZmVycmVkX3VzZXJuYW1lIjoiY2xhdXJldCIsImVtYWlsIjoiY2xhdXJldEB3ZWJvcmdhbmljLmNvbSIsImlzcyI6Imh0dHBzOi8vc2VsZi1pc3N1ZWQubWUiLCJzdWIiOiIxIiwiYXVkIjoiYzQ5N2E1NTNkMGY1ZjcwOCIsImV4cCI6MTQ2NjA0NTg4OCwiaWF0IjoxNDY2MDQyMjg4fQ.CgjhbpR8dHdgXLPcJdlcX9lQERyPj0gP1k5STWBQ02E\",\"token_type\":\"bearer\",\"access_token\":\"g8gf9OwQLONpj3geZQreEF3iKg95aRov\",\"expires_in\":3600}";
    Map<String, String> map = JSONParameter.parse(new ByteArrayInputStream(json.getBytes()));
    Assert.assertEquals("3600", map.get("expires_in"));
    Assert.assertEquals("bearer", map.get("token_type"));
    Assert.assertEquals("_0EsX3fRDAGrFuzYBVJVLUxngcxHDK3O", map.get("refresh_token"));
    Assert.assertEquals("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiQ2hyaXN0b3BoZSBMYXVyZXQiLCJnaXZlbl9uYW1lIjoiQ2hyaXN0b3BoZSIsImZhbWlseV9uYW1lIjoiTGF1cmV0IiwicHJlZmVycmVkX3VzZXJuYW1lIjoiY2xhdXJldCIsImVtYWlsIjoiY2xhdXJldEB3ZWJvcmdhbmljLmNvbSIsImlzcyI6Imh0dHBzOi8vc2VsZi1pc3N1ZWQubWUiLCJzdWIiOiIxIiwiYXVkIjoiYzQ5N2E1NTNkMGY1ZjcwOCIsImV4cCI6MTQ2NjA0NTg4OCwiaWF0IjoxNDY2MDQyMjg4fQ.CgjhbpR8dHdgXLPcJdlcX9lQERyPj0gP1k5STWBQ02E", map.get("id_token"));
    Assert.assertEquals("g8gf9OwQLONpj3geZQreEF3iKg95aRov", map.get("access_token"));
  }

}
