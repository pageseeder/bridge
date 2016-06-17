package org.pageseeder.bridge.oauth;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.net.UsernamePassword;

public class TokenRequestTest {

  @Test
  public void testToString_ProtectPassword() {
    PSConfig config = PSConfig.newInstance("https://localhost/", "http://localhost:8282/");
    PSConfig.setDefault(config);
    String password = "opensesame";
    UsernamePassword user= new UsernamePassword("ali_baba", password);
    ClientCredentials client = new ClientCredentials("123456789123456789", "hfcnosty78cfgertfgai4");
    TokenRequest request = TokenRequest.newPassword(user, client);
    Assert.assertTrue(!request.toString().contains(password));
  }
}
