package org.pageseeder.bridge.net;

/**
 * A low-level utility class providing constants for the PageSeeder Servlets.
 *
 * <p>There is generally no reason to access this class directly, higher-level class should be
 * accessed instead.
 *
 * <p>Also, <i>services</i> should be used in preference to servlets which usage is
 * deprecated for the most part.
 *
 * @author Christophe Lauret
 * @version 0.2.4
 * @since 0.2.4
 */
public final class Servlets {

  /**
   * The document browser Servlet <code>com.pageseeder.review.DocumentBrowser</code>.
   */
  public final static String DOCUMENT_BROWSER = "com.pageseeder.review.DocumentBrowser";

  /**
   * The generic search Servlet <code>com.pageseeder.search.GenericSearch</code>.
   */
  public final static String GENERIC_SEARCH = "com.pageseeder.search.GenericSearch";

}
