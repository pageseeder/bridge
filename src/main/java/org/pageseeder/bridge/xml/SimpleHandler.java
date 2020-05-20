package org.pageseeder.bridge.xml;

import org.eclipse.jdt.annotation.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

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
 * @deprecated Use the BasicHandler instead
 *
 * @param <T> The type of object to retrieve
 *
 * @author Christophe Lauret
 *
 * @version 0.9.2
 * @since 0.9.2
 */
@Deprecated
public abstract class SimpleHandler<T> extends BasicHandler<T> {

  /**
   * The current item being processed (may be <code>null</code>)
   */
  private @Nullable T item = null;

  public SimpleHandler() {
  }

  /**
   * Ensures that the last object that was process is added to list.
   */
  @Override
  public void endDocument() {
    addItem();
  }

  /**
   * @return the item currently processed.
   */
  protected @Nullable T item() {
    return this.item;
  }

  /**
   * If there is a current item being processed, it is added to the list.
   *
   * <p>The current item is always set to <code>null</code> after this method
   * is invoked.
   */
  protected void addItem() {
    @Nullable T i = this.item;
    if (i != null) {
      add(i);
    }
    this.item = null;
  }

  /**
   * Set the current item to a new item and return it for immediate use.
   *
   * <p>It return the object created, so that we can write directly or use
   * chaining methods.
   * <pre>
   *   Object item = newItem(new Object());
   * </pre>
   *
   * <p>This method replaces the current item. To ensure that
   * the current item is added before the new item use {@link #nextItem(Object)}
   *
   * @param item the item to set as the current item.
   *
   * @return the same item
   */
  protected T newItem(T item) {
    this.item = item;
    return item;
  }

  /**
   * If an item is being processed, it is added to the list and replaced by a new item.
   *
   * <p>This method also sets the current item. It is the equivalent of:
   * <pre>
   *  addItem();
   *  Object item = newItem(new Object());
   * </pre>
   *
   * @param item the item to set as the current item.
   *
   * @return the same item
   */
  protected T nextItem(T item) {
    if (this.item != null) {
      add(item);
    }
    this.item = item;
    return item;
  }

}