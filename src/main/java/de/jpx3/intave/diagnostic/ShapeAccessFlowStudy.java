package de.jpx3.intave.diagnostic;

public final class ShapeAccessFlowStudy {

  // total number of bb access
  public static int requests;

  // total number of required lookups
  public static int lookups;

  // total number of lookups, that didn't need to "ask the server"
  public static int dynamic;

  public static int green, yellow, red;

  public static void incremRequests() {
    requests++;
  }

  public static void incremLookups() {
    lookups++;
  }

  public static void incremDynamic() {
    dynamic++;
  }

  public static String output() {
    return requests + " requests required " + lookups + " lookups, " + green + " green, " + yellow + " yellow, " + red + " red, " + dynamic + " dynamic";
  }
}
