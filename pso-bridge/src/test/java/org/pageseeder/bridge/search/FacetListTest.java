/*
 * Copyright (c) 2019 Allette systems pty. ltd.
 */
package org.pageseeder.bridge.search;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Carlos Cabral
 * @since 05 July 2019
 */
public class FacetListTest {

  
  
  @Test
  public void testDefaultValues() {
    FacetList facets = FacetList.newFacetList();
    assertEquals(-1, facets.facetSize());
    assertEquals(0, facets.size());
    Map<String, String> parameters = new LinkedHashMap<>();
    facets.toParameters(parameters);
    assertEquals(0, parameters.size());
  }
  
  @Test
  public void testFacetSize() {
    Map<String, String> parameters = new LinkedHashMap<>();
    FacetList facets = FacetList.newFacetList();
    facets = facets.facetSize(250);
    facets.toParameters(parameters);
    assertEquals(1, parameters.size());
    assertTrue(parameters.containsValue("250"));
    assertTrue(parameters.containsKey("facetsize"));

    facets = facets.facetSize(100);
    parameters.clear();
    facets.toParameters(parameters);
    assertEquals(1, parameters.size());
    assertTrue(parameters.containsValue("100"));
    assertTrue(parameters.containsKey("facetsize"));
    
    facets = facets.facetSize(-1);
    parameters.clear();
    facets.toParameters(parameters);
    assertEquals(0, parameters.size());    
 }
  
  
  @Test
  public void testFacet() {
    Map<String, String> parameters = new LinkedHashMap<>();
    FacetList facets = FacetList.newFacetList("field-1");
    facets.toParameters(parameters);
    assertEquals(1, parameters.size());
    assertTrue(parameters.containsValue("field-1"));
    assertTrue(parameters.containsKey("facets"));
    
    parameters.clear();
    facets = facets.facet(new Facet("field-2", false));    
    facets.toParameters(parameters);
    assertEquals(1, parameters.size());
    assertTrue(parameters.containsValue("field-1,field-2"));
    assertTrue(parameters.containsKey("facets"));
    
    parameters.clear();
    facets = facets.facet(new Facet("field-3", true));    
    facets.toParameters(parameters);
    assertEquals(2, parameters.size());
    assertTrue(parameters.containsValue("field-1,field-2"));
    assertTrue(parameters.containsKey("facets"));
    assertTrue(parameters.containsValue("field-3"));
    assertTrue(parameters.containsKey("flexiblefacets"));
 }
}
