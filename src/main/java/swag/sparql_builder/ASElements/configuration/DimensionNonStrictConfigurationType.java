package swag.sparql_builder.ASElements.configuration;

public enum DimensionNonStrictConfigurationType {
  TREAT_AS_FACT, SPLIT_EQULALY;


  public static DimensionNonStrictConfigurationType getConfName(String str) {

    if (str.equalsIgnoreCase("SPLITEQULALY")) {
      return SPLIT_EQULALY;
    }

    return TREAT_AS_FACT;

  }
}
