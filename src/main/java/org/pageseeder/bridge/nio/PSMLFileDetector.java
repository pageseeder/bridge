/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.nio;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;

/**
 * A file type detector provider for probing PSML files.
 *
 * <p>If the filename of the path ends with ".psml", the {@link #probeContentType(Path)}
 * method will return <code>"application/vnd.pagaseeder.psml+xml"</code>.
 *
 * @author Christophe Lauret
 * @version 0.2.33
 * @since 0.2.33
 */
public final class PSMLFileDetector extends FileTypeDetector {

  /**
   * Zero-argument public constructor as required by <code>ServiceLoader</code>.
   */
  public PSMLFileDetector() {
    // Provider classes must have a zero-argument constructor so that they can be instantiated during loading.
  }

  @Override
  public String probeContentType(Path path) throws IOException {
    boolean isPSML = path.toString().toLowerCase().endsWith(".psml");
    if (isPSML) return "application/vnd.pagaseeder.psml+xml";
    return null;
  }

}
