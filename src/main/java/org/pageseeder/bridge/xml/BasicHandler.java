/*
 * Copyright 2016 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.bridge.xml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.pageseeder.bridge.http.ContentException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Simplifies SAX parsing by providing basic state machine on top of default handler.
 *
 * <p>This class provides a simple implementation of the {@link Handler} that can be used
 * to retrieve custom objects from the XML.
 *
 * <p>A typical implementation should implement the following methods:
 * <ol>
 *   <li>{@link #startElement(String, Attributes)}</li>
 *   <li>{@link #endElement(String)}</li>
 * </ol>
 *
 * <p>This handle can be used with a namespace aware parser of a non-namespace aware parser,
 * but it will only report the name of elements without their prefix or namespace URI. This
 * is sufficient for retrieving most objects from PageSeeder responses, but is not intended
 * as a general purpose SAX handler.
 *
 * @author Christophe Lauret
 *
 * @param <T> The type of object to retrieve form the SAX events
 */
public abstract class BasicHandler<T> extends Handler<T> {

  /**
   * The list of objects that have been retrieved from the XML.
   */
  private List<T> list = new ArrayList<>();

  /**
   * The ancestry of element in the context.
   *
   * <p>The first element is the ancestor and the last element is the current element.
   */
  private List<String> ancestorOrSelf = new ArrayList<>();

  /**
   * The buffer when capturing text values from elements.
   */
  private @Nullable StringBuilder buffer = null;

  /**
   * The locator if supplied by the SAX implementation.
   */
  private @Nullable Locator locator = null;

  public BasicHandler() {
  }

  // Methods to implement
  // ---------------------------------------------------------------------------

  /**
   * Method called after the SAX {@link #startElement(String, String, String, Attributes)} is
   * invoked and <i>after</i> the state of this handler has been updated.
   *
   * @param element The name of the element
   * @param atts The attributes attached to this element.
   */
  public void startElement(String element, Attributes atts) {
  }

  /**
   * Method called after the SAX {@link #endElement(String, String, String)} is
   * invoked and <i>before</i> the state of this handler has been updated.
   *
   * @param element The name of the element.
   */
  public void endElement(String element) {}

  // SAX implementations
  // ---------------------------------------------------------------------------

  @Override
  public final void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    String element = localName.length() == 0? qName : localName;
    this.ancestorOrSelf.add(element);
    try {
      startElement(element, attributes);
    } catch (AttributeException ex) {
      String xpath = "/"+String.join("/", this.ancestorOrSelf)+"/@"+ex.getAttributeName();
      throw new SAXParseException(ex.getMessage()+" XPath="+xpath, this.locator);
    }
  }

  @Override
  public final void endElement(String uri, String localName, String qName) throws SAXException {
    String element = localName.length() == 0? qName : localName;
    endElement(element);
    if (!this.ancestorOrSelf.isEmpty()) {
      this.ancestorOrSelf.remove(this.ancestorOrSelf.size()-1);
    }
  }

  /**
   * Captures the characters reported by SAX if the buffer is not <code>null</code>.
   */
  @Override
  public final void characters(char[] ch, int start, int length) throws SAXException {
    StringBuilder b = this.buffer;
    if (b != null) {
      b.append(ch, start, length);
    }
  }

  /**
   * Stores the locator so that it can be used to report issues.
   *
   * <p>Subclasses should ensure that this method is called using super to ensure that the
   * locator is provided for parsing errors.
   *
   * @param locator A locator for all SAX document events.
   */
  @Override
  public void setDocumentLocator(Locator locator) {
    this.locator = locator;
  }

  // Working with the character buffer
  // ---------------------------------------------------------------------------

  /**
   * Initialises the buffer to capture characters with {@link #characters(char[], int, int)}.
   *
   * Use this in the {@link #startElement(String, Attributes)} method when the element starts.
   */
  protected final void newBuffer() {
    this.buffer = new StringBuilder();
  }

  /**
   * Returns the content of the buffer.
   *
   * @return the content of the current buffer as a string or <code>null</code>.
   */
  @Nullable
  protected final String buffer() {
    StringBuilder b = this.buffer;
    return b != null? b.toString() : null;
  }

  /**
   * Returns the content of the buffer as a string an optionally reset it.
   *
   * @param clear <code>true</code> to clear the content of the buffer as well.
   *
   * @return the content of the current buffer as a string.
   */
  @Nullable
  protected final String buffer(boolean clear) {
    String text = buffer();
    if (clear) {
      this.buffer = null;
    }
    return text;
  }

  /**
   * Appends content to the buffer.
   *
   * @param s The content to add.
   */
  protected final void append(String s) {
    StringBuilder b = this.buffer;
    if (b != null) {
      b.append(s);
    }
  }

  /**
   * Clears the buffer.
   *
   * Calling this method will cause stop the {@link #characters(char[], int, int)} method
   * from recording in the buffer.
   */
  protected void clearBuffer() {
    this.buffer = null;
  }

  // Manage items
  // --------------------------------------------------------------------------

  /**
   * If an item was being processed, it is added to the list.
   *
   * <p>The current item is set to <code>null</code>.
   *
   * @param item The item to add to the list.
   *
   * @throws NullPointerException if the item is <code>null</code>
   */
  protected final void add(T item) {
    if (item == null) throw new NullPointerException("Cannot add null item to list");
    this.list.add(item);
  }

  // Manage items
  // --------------------------------------------------------------------------

  /**
   * @return the name of the current element or <code>null</code> before/after the document element.
   */
  @Nullable
  protected final String element() {
    return this.ancestorOrSelf.isEmpty()? null : this.ancestorOrSelf.get(this.ancestorOrSelf.size()-1);
  }

  /**
   * @return the name of the parent element or <code>null</code> if the current element does not have any.
   */
  @Nullable
  protected final String parent() {
    return (this.ancestorOrSelf.size() > 1)? this.ancestorOrSelf.get(this.ancestorOrSelf.size()-2) : null;
  }

  /**
   * Returns the name of the ancestor element or <code>null</code> if the
   * current element does not have any ancestor at that level.
   *
   * <ul>
   *   <li><code>ancestor(0)</code> is <code>current()</code></li>
   *   <li><code>ancestor(1)</code> is <code>parent()</code></li>
   * </ul>
   *
   * @param i the level of ancestry (should be a positive integer including zero)
   *
   * @return the name of the ancestor element or <code>null</code> if the
   * current element does not have any ancestor at that level.
   *
   * @throws IndexOutOfBoundsException if the specified index is less than 0;
   */
  @Nullable
  protected final String ancestor(int i) {
    return (this.ancestorOrSelf.size() > i)? this.ancestorOrSelf.get(this.ancestorOrSelf.size()-(i-1)) : null;
  }

  /**
   * Indicates whether the current element name is equals to the specified element.
   *
   * @param element the name of the element.
   *
   * @return <code>true</code> is it is the current element
   */
  protected final boolean isElement(String element) {
    return element.equals(element());
  }

  /**
   * Indicates whether the specified name is the same as parent
   *
   * @param parent The name of the parent
   *
   * @return <code>true</code> is if the the specified name is the same as parent;
   *
   * @throws NullPointerException if parent is <code>null</code>.
   */
  protected final boolean isParent(String parent) {
    return parent.equals(parent());
  }

  /**
   * Indicates whether the current context has the specified ancestor.
   *
   * <p>This method ignores the current element.
   *
   * @param ancestor the ancestor to look for
   *
   * @return <code>true</code> the context as the specified parent or ancestor;
   *         <code>false</code> otherwise.
   */
  protected final boolean hasAncestor(String ancestor) {
    if (this.ancestorOrSelf.size() <= 1) return false;
    for (int i = this.ancestorOrSelf.size() - 2 /* skip current*/ ; i >= 0; i--) {
      if (ancestor.equals(this.ancestorOrSelf.get(i))) return true;
    }
    return false;
  }

  /**
   * Indicates whether the current element name is equals to any of the
   * specified elements.
   *
   * @param elements the names of the elements to match the current.
   *
   * @return <code>true</code> is it is the current element
   */
  protected final boolean isAny(String... elements) {
    String current = element();
    if (current != null) {
      for (String e : elements) {
        if (e.equals(current)) return true;
      }
    }
    return false;
  }


  // Attribute utility classes
  // --------------------------------------------------------------------------

  /**
   * @param atts Attributes being parsed
   * @param name The name of the requested attribute.
   *
   * @return The corresponding value.
   *
   * @throws ContentException If the attribute is missing or could not be parsed as a long.
   */
  public static Long getLong(Attributes atts, String name) {
    String value = atts.getValue(name);
    if (value == null) throw new MissingAttributeException(name);
    return toLong(value, name);
  }

  /**
   * @param atts     Attributes being parsed
   * @param name     The name of the requested attribute.
   * @param fallback The value to return if the attribute is not specified
   *
   * @return The corresponding value or the fallback value.
   *
   * @throws ContentException If the attribute is missing or could not be parsed as a long.
   */
  public static Long getLong(Attributes atts, String name, Long fallback) {
    String value = atts.getValue(name);
    return value != null? toLong(value, name) : fallback;
  }

  /**
   * @param atts Attributes being parsed
   * @param name The name of the requested attribute.
   *
   * @return The corresponding value.
   *
   * @throws MissingAttributeException If the attribute could not be parsed as a long.
   * @throws MissingAttributeException If the attribute could not be parsed as a long.
   */
  public static @Nullable Long getOptionalLong(Attributes atts, String name) {
    String value = atts.getValue(name);
    if (value == null) return null;
    return toLong(value, name);
  }

  /**
   * @param atts Attributes being parsed
   * @param name The name of the requested attribute.
   *
   * @return The corresponding value.
   *
   * @throws MissingAttributeException If the attribute is missing or could not be parsed as a long.
   */
  public static String getString(Attributes atts, String name) {
    String value = atts.getValue(name);
    if (value == null) throw new MissingAttributeException(name);
    return value;
  }

  /**
   * @param atts     Attributes being parsed
   * @param name     The name of the requested attribute.
   * @param fallback The value to return if the attribute is not specified
   *
   * @return The corresponding value or the fallback value.
   */
  public static String getString(Attributes atts, String name, String fallback) {
    String value = atts.getValue(name);
    return value != null? value : fallback;
  }

  /**
   * @param atts     Attributes being parsed
   * @param name     The name of the requested attribute.
   *
   * @return The corresponding value.
   *
   * @throws MissingAttributeException If the attribute is missing or could not be parsed as a long.
   */
  public static int getInt(Attributes atts, String name) {
    String value = atts.getValue(name);
    if (value == null) throw new MissingAttributeException(name);
    return toInt(value, name);
  }

  /**
   * @param atts     Attributes being parsed
   * @param name     The name of the requested attribute.
   * @param fallback The value to return if the attribute is not specified
   *
   * @return The corresponding value or the fallback value.
   */
  public static int getInt(Attributes atts, String name, int fallback) {
    String value = atts.getValue(name);
    return value != null? toInt(value, name) : fallback;
  }

  /**
   * @param atts Attributes being parsed
   * @param name The name of the requested attribute.
   *
   * @return The corresponding value.
   */
  @Nullable
  public static String getOptionalString(Attributes atts, String name) {
    return atts.getValue(name);
  }

  /**
   *
   * @param value The attribute value
   * @param name  The name of the attribute
   *
   * @return The value of a Long instance.
   *
   * @throws InvalidAttributeException If the attribute value could not be parsed as Long object.
   */
  private static Long toLong(String value, String name) {
    try {
      return Long.valueOf(value);
    } catch (NumberFormatException ex) {
      throw new InvalidAttributeException(name, ex);
    }
  }

  /**
   *
   * @param value The attribute value
   * @param name  The name of the attribute
   *
   * @return The values as int
   *
   * @throws InvalidAttributeException If the attribute value could not be parsed as an int.
   */
  private static int toInt(String value, String name) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException ex) {
      throw new InvalidAttributeException(name, ex);
    }
  }

  // Handler implementation
  // --------------------------------------------------------------------------

  /**
   * @return The list of item that have been processed.
   */
  @Override
  public List<T> list() {
    return this.list;
  }

  /**
   * @return the last item that was processed and added to the list.
   */
  @Override
  @Nullable
  public T get() {
    return this.list.isEmpty()? null : this.list.get(this.list.size()-1);
  }

}