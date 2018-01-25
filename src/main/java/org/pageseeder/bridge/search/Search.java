/*
 * Copyright 2018 Allette Systems (Australia)
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
package org.pageseeder.bridge.search;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

/**
 * A convenience class to make search requests via PageSeeder's suite of search services.
 *
 * @version 0.12.0
 * @since 0.12.0
 */
public final class Search {

  /**
   * Utility class.
   */
  private Search(){}

  // Private helpers
  // --------------------------------------------------------------------------

  /**
   * Join the values in the collection using the specified separator.
   *
   * @param values    The values to join
   * @param separator The separator to use
   *
   * @return the joined values as a single string.
   */
  public static String join(Collection<?> values, char separator) {
    StringBuilder s = new StringBuilder();
    for (Object o : values) {
      if (s.length() > 0) {
        s.append(separator);
      }
      s.append(o);
    }
    return s.toString();
  }

  /**
   * Format the date time as a string for use in search filters and ranges.
   *
   * <p>The date time string is serialised using ISO9601 in universal time (UTC).</p>
   *
   * @param datetime The datetime instance to format
   *
   * @return The corresponding string format.
   */
  public static String format(LocalDateTime datetime) {
    // We format using second resolutions in UTC
    return datetime.atZone(ZoneOffset.systemDefault())
        .withZoneSameInstant(ZoneOffset.UTC)
        .truncatedTo(ChronoUnit.SECONDS)
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+"Z";
  }
}
