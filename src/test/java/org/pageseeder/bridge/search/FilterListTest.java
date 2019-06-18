/*
 * Copyright (c) 2019 Allette systems pty. ltd.
 */
package org.pageseeder.bridge.search;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Carlos Cabral
 * @since 10 May 2019
 */
public class FilterListTest {

  
  
  @Test
  public void testToString() {
    String filterValue1 = "text1, text2, text3";
    String filterValue2 = "text4, text5 text6";
    String filterValue3 = "text7 text8";
    Filter filter1 = new Filter("filter-1", filterValue1);
    Filter filter2 = new Filter("filter-1", filterValue2);
    Filter filter3 = new Filter("filter-1", filterValue3);
    FilterList filters = FilterList.newList(filter1, filter2, filter3);
    Assert.assertEquals("filter-1:text1\\, text2\\, text3,filter-1:text4\\, text5 text6,filter-1:text7 text8", filters.toString());
  }
  
}
