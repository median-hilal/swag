package swag.sparql_builder.ASElements.configuration;

public enum DimensionIncompleteConfigurationType {
  DISCARD_FACT, OTHER, SUBELEMENT;

  public static DimensionIncompleteConfigurationType getConfName(String str) {

    if (str.equalsIgnoreCase("OTHER")) {
      return OTHER;
    }

    if (str.equalsIgnoreCase("SUBELEMENT")) {
      return SUBELEMENT;
    }

    return DISCARD_FACT;
  }
}
