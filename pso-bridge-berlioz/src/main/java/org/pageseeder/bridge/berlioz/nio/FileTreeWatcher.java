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
package org.pageseeder.bridge.berlioz.nio;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.pageseeder.berlioz.GlobalSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Monitors a folder and its sub-folders for changes.
 *
 * <p>This class will automatically start a thread, and create the appropriate watchers on the
 * directories found. Events are then reported to the watch listener.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.4
 * @since 0.1.0
 */
public final class FileTreeWatcher {

  /** To know what's going on */
  private static final Logger LOGGER = LoggerFactory.getLogger(FileTreeWatcher.class);

  /** The root of the file tree to watch. */
  private final Path _root;

  /** A list of path to ignore within the root. */
  private final List<Path> _ignore;

  /** The listener to report events to. */
  private final WatchListener _listener;

  /** Maintains the status of this watcher. */
  private AtomicBoolean running;

  private WatchService watchService;
  private Thread watchThread;

  /** Maps Watch keys to the watched directory path. */
  private final Map<WatchKey,Path> _keys;

  /**
   * Creates a new watcher.
   *
   * @param root     The root of the file tree to watch.
   * @param ignore   A list of paths to ignore
   * @param listener The listener which receives the events
   */
  public FileTreeWatcher(Path root, List<Path> ignore, WatchListener listener) {
    this._root = root;
    this._ignore = ignore;
    this._listener = listener;
    this._keys = new HashMap<>();

    this.running = new AtomicBoolean(false);

    this.watchService = null;
    this.watchThread = null;
  }

  /**
   * Create a new watcher for PSML.
   *
   * @param listener The watch listener.
   *
   * @return a new recursive watcher with the default settings for a PSML directory.
   */
  public static FileTreeWatcher newPSMLWatcher(WatchListener listener) {
    File r = GlobalSettings.getAppData();
    File psml = new File(r, "psml");
    Path root = Paths.get(psml.toURI());
    Path ignore = root.resolve("META-INF");
    List<Path> ignorePaths = Collections.singletonList(ignore);
    return new FileTreeWatcher(root, ignorePaths, listener);
  }

  /**
   * Starts the watcher service and registers watches in all of the sub-folders of
   * the given root folder.
   *
   * <p><b>Important:</b> This method returns immediately, even though the watches
   * might not be in place yet. For large file trees, it might take several seconds
   * until all directories are being monitored. For normal cases (1-100 folders), this
   * should not take longer than a few milliseconds.
   */
  public void start() throws IOException {
    this.watchService = FileSystems.getDefault().newWatchService();
    this.watchThread = new Thread(new Runnable() {
      @Override
      public void run() {
        WatchListener listener = FileTreeWatcher.this._listener;
        FileTreeWatcher.this.running.set(true);
        registerAll(FileTreeWatcher.this._root);
        while (FileTreeWatcher.this.running.get()) {
          try {
            WatchKey key = FileTreeWatcher.this.watchService.take();

            Path dir = FileTreeWatcher.this._keys.get(key);
            if (dir == null) {
              LOGGER.warn("WatchKey not recognized!!");
              continue;
            }

            // Iterate through events
            for (WatchEvent<?> event: key.pollEvents()) {
              WatchEvent.Kind<?> kind = event.kind();
              if (kind == OVERFLOW) { continue; }

              // Context for directory entry event is the file name of entry
              WatchEvent<Path> ev = cast(event);
              Path name = ev.context();
              Path child = dir.resolve(name);

              // Register new directories
              if (kind == ENTRY_CREATE && Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                registerAll(child);
              }

              // Report all other events to the listener
              else if (listener != null) {
                listener.received(child, ev.kind());
              }
            }

            // Remove deleted directories
            boolean valid = key.reset();
            if (!valid) {
              FileTreeWatcher.this._keys.remove(key);
              // all directories are inaccessible
              if (FileTreeWatcher.this._keys.isEmpty()) {
                break;
              }
            }

          } catch (InterruptedException | ClosedWatchServiceException ex) {
            FileTreeWatcher.this.running.set(false);
          }
        }
      }
    }, "Watcher");

    this.watchThread.start();
  }

  /**
   * Stops the file tree watcher and any associated thread,
   */
  public synchronized void stop() {
    if (this.watchThread != null) {
      try {
        this.watchService.close();
        this.running.set(false);
        this.watchThread.interrupt();
      } catch (IOException ex) {
        // Don't care
      }
    }
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * Walk the file tree and registers the specified directory and all of its sub-directories
   * except those who match the list of ignored paths.
   *
   * @param start The directory to start from.
   */
  private synchronized void registerAll(final Path start) {
    LOGGER.info("Registering new folders at watch service ...");
    try {
      Files.walkFileTree(start, new FileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
          if (FileTreeWatcher.this._ignore.contains(dir)) return FileVisitResult.SKIP_SUBTREE;
          else {
            register(dir);
            return FileVisitResult.CONTINUE;
          }
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException ex)  {
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException ex)  {
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException ex) {
      // Don't care
    }
  }

  /**
   * Registers the specified directory with the {@link WatchService}.
   *
   * @param dir The directory to watch.
   */
  private void register(Path dir) {
    try {
      WatchKey key = dir.register(this.watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
      LOGGER.info("Registering {}", dir);
      this._keys.put(key, dir);
    } catch (IOException ex) {
      // TODO handle
      ex.printStackTrace();
    }
  }

  /**
   * Cast watch event to avoid type safety warnings on the code.
   */
  @SuppressWarnings("unchecked")
  private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
    return (WatchEvent<T>)event;
  }

}
