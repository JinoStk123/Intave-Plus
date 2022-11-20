package de.jpx3.intave.security.blacklist;

import com.google.common.collect.ImmutableList;
import de.jpx3.intave.resource.Resource;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

public final class Blacklist {
  private final List<String> blacklistedHashes;
  private final MessageDigest sha256Digest;

  private Blacklist(List<String> blacklistedHashes) {
    this.blacklistedHashes = blacklistedHashes;
    try {
      this.sha256Digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("Unable to find SHA-256 hashing digest", exception);
    }
  }

  public boolean nameBlacklisted(String name) {
    return hashBlacklisted(hashOf(name));
  }

  public boolean idBlacklisted(UUID id) {
    return hashBlacklisted(hashOf(id.toString()));
  }

  private boolean hashBlacklisted(String input) {
    for (String blacklistedHash : blacklistedHashes) {
      if (blacklistedHash.equals(input)) {
        return true;
      }
    }
    return false;
  }

  private String hashOf(String input) {
    return bytesToHex(sha256Digest.digest(input.getBytes(StandardCharsets.UTF_8)));
  }

  private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (byte daBite : hash) {
      String hex = Integer.toHexString(0xff & daBite);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

  public static Blacklist empty() {
    return new Blacklist(ImmutableList.of());
  }

  public static Blacklist from(Resource resource) {
    return new Blacklist(resource.readLines());
  }
}
