package org.pageseeder.bridge.xml;

import org.eclipse.jdt.annotation.Nullable;
import org.xml.sax.SAXException;

/**
 * Extends the basic handler by providing methods to create immutable objects.
 *
 *
 * @param <T> The type of object to retrieve
 * @param <B> The builder for the type of objects to retrieve
 *
 * @author Christophe Lauret
 *
 * @version 0.9.2
 * @since 0.9.2
 */
public abstract class BuildableHandler<T, B> extends BasicHandler<T> {

  /**
   * The current item being processed (may be <code>null</code>)
   */
  private @Nullable B builder;

  public BuildableHandler() {
    this.builder = null;
  }

  public BuildableHandler(B builder) {
    this.builder = builder;
  }

  /**
   * Build a new item using the builder.
   *
   * <p>If the builder can be reused for other item, the builder should be reset
   * after the item has been built.
   *
   * @param builder The name of the element.
   */
  public abstract T build(B builder);

  /**
   * Ensures that the last object that was process is added to list.
   */
  @Override
  public void endDocument() throws SAXException {
    addItem();
  }

  /**
   * @return the item currently processed.
   */
  protected @Nullable B builder() {
    return this.builder;
  }

  /**
   * If there is a builder, the builder attempts to build the item
   * and add it to the list before resetting the builder.
   *
   * <p>The current builder is not modified so that it can be reused
   * to build other objects.
   *
   * <p>If the builder cannot be reused, use the {@link #newBuilder(Object)}
   */
  protected void addItem() {
    @Nullable B b = this.builder;
    if (b != null) {
      T item = build(b);
      add(item);
    }
  }

  /**
   * Set the current builder to a new builder and return it for immediate use.
   *
   * <p>It returns the object created, so that we can write directly or
   * use chaining methods.
   * <pre>
   *   Builder builder = newBuilder(new Builder());
   * </pre>
   *
   * @param builder the builder to set as the current builder.
   *
   * @return the same builder
   */
  protected B newBuilder(B builder) {
    this.builder = builder;
    return builder;
  }

}