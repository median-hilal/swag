package swag.sparql_builder.ASElements.configuration;

public enum MsrIncompleteConfigurationType {
  DISCARD_FACT, DEFAULT_VALUE, OPTIONAL;

  public static MsrIncompleteConfigurationType getConfName(String str) {

    if (str.equalsIgnoreCase("OPTIONAL")) {
      return OPTIONAL;
    }

    if (str.equalsIgnoreCase("DEFAULT_VALUE")) {
      return DEFAULT_VALUE;
    }

    return DISCARD_FACT;
  }
}
