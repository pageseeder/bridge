/*
 * Copyright 2015 Allette Systems (Australia)
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
package org.pageseeder.bridge.nio;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;

import org.eclipse.jdt.annotation.Nullable;

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
  public @Nullable String probeContentType(Path path) throws IOException {
    boolean isPSML = path.toString().toLowerCase().endsWith(".psml");
    if (isPSML) return "application/vnd.pagaseeder.psml+xml";
    return null;
  }

}
