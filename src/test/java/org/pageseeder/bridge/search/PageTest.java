/*
 * Copyright (c) 2019 Allette systems pty. ltd.
 */
package org.pageseeder.bridge.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author Carlos Cabral
 * @since 05 July 2019
 */
public class PageTest {
  
  @Test
  public void testDefault() {
    Page page = new Page();
    Map<String, String> parameters = new LinkedHashMap<>();
    page.toParameters(parameters);
    assertEquals(0, parameters.size());
  }  

  @Test
  public void testBiggerThanDefault() {
    Page page = new Page(2, 200);
    Map<String, String> parameters = new LinkedHashMap<>();
    page.toParameters(parameters);
    assertEquals(2, parameters.size());
    assertTrue(parameters.containsKey("page"));
    assertTrue(parameters.containsValue("2"));
    assertTrue(parameters.containsKey("pagesize"));
    assertTrue(parameters.containsValue("200"));
  }
  
  @Test
  public void testSmallerThanDefault() {
    Page page = new Page(1, 10);
    Map<String, String> parameters = new LinkedHashMap<>();
    page.toParameters(parameters);
    assertEquals(1, parameters.size());
    assertTrue(parameters.containsKey("pagesize"));
    assertTrue(parameters.containsValue("10"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testInvalidZero() {
    new Page(0, 0);
  }
  
  @Test (expected = IllegalArgumentException.class)
  public void testInvalidNegative() {
    new Page(-1, -1);
  }
 
}
