package org.pageseeder.bridge.xml;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xml.sax.Attributes;

/**
 * A base class to construct objects from search results.
 *
 * @author Christophe Lauret
 *
 * @param <T> The type of object to build from the search result
 */
public abstract class BasicResultHandler<T> extends BasicHandler<T> {

  /**
   * The list of fields to extract, if empty, all fields are extracted.
   */
  private final List<String> _fields;

  /**
   * State variable to indicate the group the current result belong to
   */
  private String group = null;

  /**
   * State variable to indicate the name of the current field.
   */
  private String fieldname = null;

  /**
   * This method is called whenever a new result document starts.
   *
   * @param group  The group this document is part of.
   */
  public abstract void startResult(String group);

  /**
   * This method is called whenever a result document ends.
   *
   * @param group  The group this document is part of.
   */
  public abstract void endResult();

  /**
   * This method is called for each field.
   *
   * @param name  The name of the search results field.
   * @param value The value of the search results field.
   */
  public abstract void field(String name, String value);

  /**
   * This method is called for each score element on a document.
   *
   * @score the score of the current document.
   */
  public void score(double score) {
  }

  /**
   * Creates a basic handler for result capturing every field.
   */
  public BasicResultHandler() {
    this._fields = Collections.emptyList();
  }

  /**
   * Creates a basic handler for result only capturing fields listed by the
   * parameter.
   *
   * <p>Only the fields which name match one of the names specified will be
   * reported by the {@link #field(String, String)} method.
   *
   * <p>Implementation note: this method avoid creating unnecessary strings
   * for fields the extending class is not interested in.
   *
   * @param fields The names of the fields to capture.
   *
   * @throws NullPointerException if fields is <code>null</code>.
   */
  public BasicResultHandler(String... fields) {
    this._fields = Arrays.asList(fields);
  }

  @Override
  public final void startElement(String element, Attributes attributes) {
    if ("document".equals(element)) {
      startResult(this.group);

    } else if (isParent("document")) {
      if ("field".equals(element)) {
        String name = attributes.getValue("name");
        List<String> fields = this._fields;
        if (fields.contains(name) || fields.isEmpty()) {
          this.fieldname = attributes.getValue("name");
          newBuffer();
        }
      } else if ("score".equals(element)) {
        newBuffer();
      }

    } else if ("search".equals(element)) {
      String groupname = attributes.getValue("groupname");
      if (groupname != null) {
        this.group = groupname;
      }
    }
  }

  @Override
  public final void endElement(String element) {
    if (isElement("document")) {
      endResult();
    } else if (isElement("field") && isParent("document")) {
      String name = this.fieldname;
      if (name != null) {
        String value = buffer(true);
        if (value != null) {
          field(name, value);
        }
        this.fieldname = null;
      }
    } else if ("score".equals(element) && isParent("document")) {
      String value = buffer(true);
      if (value != null) {
        try {
          double score = Double.valueOf(value);
          score(score);
        } catch (NumberFormatException ex) {
          // Do nothing.
        }
      }
    }
  }

}
