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

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.pageseeder.bridge.model.PSMember;
import org.pageseeder.bridge.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for Open ID.
 */
final class OpenID {

  /** Logger for this class */
  private static final Logger LOGGER = LoggerFactory.getLogger(OpenID.class);

  /** Utility class. */
  private OpenID(){}

  protected static @Nullable PSMember parseIDToken(String idToken, byte[] key) {
    LOGGER.debug("Parsing JWT ID token");
    // [header].[payload].[signature]
    @NonNull String[] segments = idToken.split("\\.");
    if (segments.length != 3) {
      LOGGER.error("Invalid ID Token: {} segments found", segments.length);
      return null;
    }
    // Check header
    Map<String, String> header = JSONParameter.parse(Base64.decodeURL(segments[0], StandardCharsets.UTF_8));
    boolean isValidHeader = "JWT".equals(header.get("typ")) && "HS256".equals(header.get("alg"));
    if (!isValidHeader) {
      LOGGER.error("Invalid ID token: unsupported JWT header");
      return null;
    }

    // Check signature
    byte[] signature = Base64.decodeURL(segments[2]);
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(key, "HmacSHA256"));
      byte[] verify = mac.doFinal((segments[0]+"."+segments[1]).getBytes());
      if (!Arrays.equals(signature, verify)) {
        LOGGER.error("JWT token signature does not match");
        return null;
      }
    } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
      return null;
    }

    // Check Payload
    try {
      Map<@NonNull String, @NonNull String> payload = JSONParameter.parse(Base64.decodeURL(segments[1], StandardCharsets.UTF_8));
      String sub = payload.get("sub");
      if (sub != null) {
        Long id = Long.valueOf(sub);
        String username = payload.get("preferred_username");
        String firstname = payload.get("given_name");
        String surname = payload.get("family_name");
        String email = payload.get("email");
        PSMember member = new PSMember(id);
        member.setEmail(email);
        member.setFirstname(firstname);
        member.setSurname(surname);
        member.setUsername(username);
        LOGGER.debug("Found member {} ({}) via Open ID", username, id);
        return member;
      } else {
        LOGGER.error("Invalid ID token: missing 'sub' in payload");
      }
    } catch (Exception ex) {
      LOGGER.error("Invalid ID token: unable to parse payload", ex);
    }

    return null;
  }

}
