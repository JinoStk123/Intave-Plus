package de.jpx3.intave.security;

import de.jpx3.intave.tools.annotate.Natify;

public final class LicenseVerification {
  private static String licenseName;

  @Natify
  public static String network() {
    return "Intavede";
  }

  @Natify
  public static String licenseKey() {
    String rawLicense = rawLicense();
    return rawLicense.substring(4, Math.min(9, rawLicense.length()));
  }

  @Natify
  public static String rawLicense() {
    if(licenseName == null) {
/*      InputStream resourceAsStream = LicenseVerification.class.getResourceAsStream("5ee6db6d-6751-4081-9cbf-28eb0f6cc055");
      StringBuilder stringBuilder = new StringBuilder();
      Scanner scanner = new Scanner(resourceAsStream);
      while (scanner.hasNext()) {
        stringBuilder.append(scanner.next());
      }
      licenseName = stringBuilder.toString();*/
      licenseName = "TkxzRWpMdE1NVmdCUUdOMjdmNmdTdz09yB1f45kTpS5yiTeuw6DrRQ==";// Intavede
    }
    return licenseName;
  }
}
