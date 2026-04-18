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
package org.pageseeder.bridge.util;

import java.nio.charset.Charset;

/**
 * A base utility class for Base64 until we switch to Java 8.
 */
public final class Base64 {

  public static String encode(byte[] bytes) {
    return java.util.Base64.getEncoder().encodeToString(bytes);
  }

  public static String encode(String data, Charset charset) {
    return encode(data.getBytes(charset));
  }

  public static byte[] decode(String base64) throws IllegalArgumentException {
    return java.util.Base64.getDecoder().decode(base64);
  }

  public static String decode(String data, Charset charset) {
    byte[] bytes = decode(data);
    return new String(bytes, charset);
  }

  public static String encodeURL(byte[] bytes) {
    return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  public static String encodeURL(String data, Charset charset) {
    return encodeURL(data.getBytes(charset));
  }

  public static byte[] decodeURL(String base64url) throws IllegalArgumentException {
    return java.util.Base64.getUrlDecoder().decode(base64url);
  }

  public static String decodeURL(String data, Charset charset) {
    byte[] bytes = decodeURL(data);
    return new String(bytes, charset);
  }

}
