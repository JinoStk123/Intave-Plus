package de.jpx3.intave.security;

import de.jpx3.intave.tools.EncryptedResource;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public final class HWIDVerification {
  private static EncryptedResource encryptedResource;
  private static String identifier;

  public static String publicHardwareIdentifier() {
    if(encryptedResource == null) {
      encryptedResource = new EncryptedResource("hardware-id", false);
    }
    if (!encryptedResource.exists()) {
      identifier = randomString();
      ByteArrayInputStream inputStream = new ByteArrayInputStream(identifier.getBytes(StandardCharsets.UTF_8));
      encryptedResource.write(inputStream);
    }
    if(identifier == null) {
      identifier = new Scanner(new InputStreamReader(encryptedResource.read())).next();
    }
    return identifier;
  }

  private static String randomString() {
    byte[] randomBytes = new byte[Character.BYTES * 256];
    ThreadLocalRandom.current().nextBytes(randomBytes);
    return new String(randomBytes);
  }
}
