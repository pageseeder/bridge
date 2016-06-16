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
package org.pageseeder.bridge.oauth;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple utility class to parse the JSON responses from the OAuth server.
 *
 * <p>It is designed to operate within the narrow constraints of OAuth
 * PageSeeder responses and MUST NOT be used as a general utility class as it
 * is likely to fail.
 *
 * @version 0.9.0
 * @since 0.9.0
 */
final class JSON {

  /** Logger for this class */
  private static final Logger LOGGER = LoggerFactory.getLogger(JSON.class);

  private static final Pattern JSON_STRING = Pattern.compile("\"([a-z_]+)\":\"([^\"]+)\"");

  private static final Pattern JSON_NUMBER = Pattern.compile("\"([a-z_]+)\":([0-9]+)");

  /** Utility class. */
  private JSON(){}

  static Map<String,String> parse(String s) {
    return  parse(new Scanner(s));
  }

  static Map<String,String> parse(InputStream in) {
    return parse(new Scanner(in, "utf-8"));
  }

  private static Map<String,String> parse(Scanner scanner) {
    Map<String, String> map = new HashMap<>(4);
    scanner.useDelimiter("\\s*[{,}]\\s*");
    while (scanner.hasNext()) {
      if (scanner.hasNext(JSON_STRING)) {
        scanner.next(JSON_STRING);
        MatchResult r = scanner.match();
        String name = r.group(1);
        String value = r.group(2);
        map.put(name, value);
      } else if (scanner.hasNext(JSON_NUMBER)) {
        scanner.next(JSON_NUMBER);
        MatchResult r = scanner.match();
        String name = r.group(1);
        String value = r.group(2);
        map.put(name, value);
      } else {
        String token = scanner.next();
        LOGGER.warn("Unable to parse JSON! Found unparsable token {}", token);
        break;
      }
    }
    return map;
  }

  public static void main(String[] args) {
    String json = "{\"refresh_token\":\"_0EsX3fRDAGrFuzYBVJVLUxngcxHDK3O\"\n,\n\"id_token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiQ2hyaXN0b3BoZSBMYXVyZXQiLCJnaXZlbl9uYW1lIjoiQ2hyaXN0b3BoZSIsImZhbWlseV9uYW1lIjoiTGF1cmV0IiwicHJlZmVycmVkX3VzZXJuYW1lIjoiY2xhdXJldCIsImVtYWlsIjoiY2xhdXJldEB3ZWJvcmdhbmljLmNvbSIsImlzcyI6Imh0dHBzOi8vc2VsZi1pc3N1ZWQubWUiLCJzdWIiOiIxIiwiYXVkIjoiYzQ5N2E1NTNkMGY1ZjcwOCIsImV4cCI6MTQ2NjA0NTg4OCwiaWF0IjoxNDY2MDQyMjg4fQ.CgjhbpR8dHdgXLPcJdlcX9lQERyPj0gP1k5STWBQ02E\",\"token_type\":\"bearer\",\"access_token\":\"g8gf9OwQLONpj3geZQreEF3iKg95aRov\",\"expires_in\":3600}";
    Map<String, String> x = parse(new ByteArrayInputStream(json.getBytes()));
    System.out.println(x);
  }
}
