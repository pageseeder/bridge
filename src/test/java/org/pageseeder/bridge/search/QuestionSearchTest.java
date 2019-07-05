/*
 * Copyright (c) 2019 Allette systems pty. ltd.
 */
package org.pageseeder.bridge.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

/**
 * @author Carlos Cabral
 * @since 05 July 2019
 */
public class QuestionSearchTest {
  
  @Test
  public void testFacets() {
    QuestionSearch search = new QuestionSearch();
    FacetList facets = FacetList.newFacetList("field-1","field-2");
    search = search.facets(facets);
    Map<String, String> parameters = search.toParameters();
    assertEquals(1, parameters.size());
    assertTrue(parameters.containsKey("facets"));
    assertTrue(parameters.containsValue("field-1,field-2"));
    
    facets = FacetList.newFacetList("field-3","field-4");
    facets = facets.facetSize(250);
    search = search.facets(facets);
    parameters = search.toParameters();
    assertEquals(2, parameters.size());
    assertTrue(parameters.containsKey("facets"));
    assertTrue(parameters.containsValue("field-3,field-4"));
    assertTrue(parameters.containsValue("250"));
    assertTrue(parameters.containsKey("facetsize"));
  }
  
  @Test
  public void testFacet() {
    QuestionSearch search = new QuestionSearch();
    FacetList facets = FacetList.newFacetList("field-1","field-2");
    search = search.facets(facets);
    Map<String, String> parameters = search.toParameters();
    assertEquals(1, parameters.size());
    assertTrue(parameters.containsKey("facets"));
    assertTrue(parameters.containsValue("field-1,field-2"));

    facets = facets.facetSize(250);
    search = search.facets(facets);
    parameters = search.toParameters();
    assertEquals(2, parameters.size());
    assertTrue(parameters.containsKey("facets"));
    assertTrue(parameters.containsValue("field-1,field-2"));
    assertTrue(parameters.containsValue("250"));
    assertTrue(parameters.containsKey("facetsize"));
    
    search = search.facet(new Facet("field-3", false));
    search = search.facet(new Facet("field-4", true));
    parameters = search.toParameters();
    assertEquals(3, parameters.size());
    assertTrue(parameters.containsKey("facets"));
    assertTrue(parameters.containsValue("field-1,field-2,field-3"));
    assertTrue(parameters.containsKey("flexiblefacets"));
    assertTrue(parameters.containsValue("field-4"));
    assertTrue(parameters.containsValue("250"));
    assertTrue(parameters.containsKey("facetsize"));
  }
  
  @Test
  public void testSortFields() {
    QuestionSearch search = new QuestionSearch();
    search = search.sortFields(FieldList.newList("field-1", "field-2"));
    Map<String, String> parameters = search.toParameters();
    assertEquals(1, parameters.size());
    assertTrue(parameters.containsKey("sortfields"));
    assertTrue(parameters.containsValue("field-1,field-2"));
  }
  
  @Test
  public void testQuestion() {
    QuestionSearch search = new QuestionSearch();    
    search = search.question("Test Question");
    Map<String, String> parameters = search.toParameters();
    assertEquals(1, parameters.size());
    assertTrue(parameters.containsKey("question"));
    assertTrue(parameters.containsValue("Test Question"));
    
    search = search.question(new Question("Another Test Question", Arrays.asList("field-1","field-2"), 50));
    parameters = search.toParameters();
    assertEquals(3, parameters.size());
    assertTrue(parameters.containsKey("question"));
    assertTrue(parameters.containsValue("Another Test Question"));
    assertTrue(parameters.containsKey("questionfields"));
    assertTrue(parameters.containsValue("field-1,field-2"));
    assertTrue(parameters.containsKey("suggestsize"));
    assertTrue(parameters.containsValue("50"));
  }
 
  @Test
  public void testPage() {
    QuestionSearch search = new QuestionSearch();    
    search = search.page(2);
    Map<String, String> parameters = search.toParameters();
    assertEquals(1, parameters.size());
    assertTrue(parameters.containsKey("page"));
    assertTrue(parameters.containsValue("2"));
    
    search = search.pageSize(101);
    parameters = search.toParameters();
    assertEquals(2, parameters.size());
    assertTrue(parameters.containsKey("page"));
    assertTrue(parameters.containsValue("2"));
    assertTrue(parameters.containsKey("pagesize"));
    assertTrue(parameters.containsValue("101"));
    
    search = search.page(new Page(3, 200));
    parameters = search.toParameters();
    assertEquals(2, parameters.size());
    assertTrue(parameters.containsKey("page"));
    assertTrue(parameters.containsValue("3"));
    assertTrue(parameters.containsKey("pagesize"));
    assertTrue(parameters.containsValue("200"));
  }
}
