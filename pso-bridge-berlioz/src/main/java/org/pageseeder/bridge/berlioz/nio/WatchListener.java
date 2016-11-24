package org.pageseeder.bridge.berlioz.nio;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Receive notification of a path event.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.4
 * @since 0.1.4
 */
public interface WatchListener {

  /**
   * This method is invoked whenever a file event occurs.
   *
   * @param path The path affected
   * @param kind The kind of event for the file
   */
  public void received(Path path, WatchEvent.Kind<Path> kind);

}
