package org.pageseeder.bridge.model;

import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.PSConfig;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.control.MemberManager;

public final class MemberManagerTest {

  @Test
  public void testLogin_Success() throws APIException {
    PSConfig config = PSConfig.newInstance("https://ps.allette.com.au", "http://ps.allette.com.au:8282");
    PSConfig.setDefault(config);
    Scanner scanner = new Scanner( System.in );
    System.out.print("Enter a valid username/email: ");
    String username = scanner.nextLine();
    System.out.print("Enter your password: ");
    String password = scanner.nextLine();
    scanner.close();
    PSSession session = MemberManager.login(username, password);
    Assert.assertNotNull(session);
    System.out.print(session);
  }

  @Test
  public void testLogin_Failure() throws APIException {
    PSConfig config = PSConfig.newInstance("https://ps.allette.com.au", "http://ps.allette.com.au:8282");
    PSConfig.setDefault(config);
    PSSession session = MemberManager.login("67yuiIGDI&FitTFCDYU", "vhyauighd477yaxon7");
    Assert.assertNull(session);
  }
}
