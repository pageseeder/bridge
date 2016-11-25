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
package org.pageseeder.bridge.berlioz.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for I/O operations.
 *
 * <p>Note: this is a low level API, this is mostly designed for internal use and is subject to change.
 *
 * @author Christophe Lauret
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public final class IOUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);

  /** Utility class. */
  private IOUtils() {
  }

  /**
   * Returns a byte array of exactly the specified size containing the contents of the input
   *
   * @param input The input stream to read
   * @param size  the size of the array
   *
   * @return the content of the input as a byte array
   *
   * @throws IOException If an error occurs while reading the input stream
   */
  public static byte[] toByteArray(InputStream input, int size) throws IOException {
    byte[] data = new byte[size];
    DataInputStream dataIs = new DataInputStream(input);
    dataIs.readFully(data);
    return data;
  }

  /**
   * Returns the requested resource or <code>null</code>.
   *
   * @param name the name of the resource to retrieve.
   *
   * @return the corresponding byte array or <code>null</code> if not found or I/O error occurs.
   */
  public static byte[] getResource(String name) {
    byte[] data = null;
    ClassLoader loader = IOUtils.class.getClassLoader();
    try (InputStream in = loader.getResourceAsStream(name)) {
      if (in != null) {
        data = toByteArray(in);
      }
    } catch (IOException ex) {
      LOGGER.warn("An error occurred while retrieving resource", ex);
    }
    return data;
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  private static byte[] toByteArray(InputStream input) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    copyTo(input, output, new byte[1024]);
    return output.toByteArray();
  }

  private static long copyTo(InputStream input, OutputStream output, byte[] buffer) throws IOException {
    long count = 0L;
    int n = 0;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }
}
