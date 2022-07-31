package swag.sparql_builder.ASElements.configuration;

public enum MsrNonStrictConfigurationType {
  TREAT_AS_FACT, INTERN_AGG;


  public static MsrNonStrictConfigurationType getConfName(String str) {

    if (str.equalsIgnoreCase("InternalAggregation")) {
      return INTERN_AGG;
    }

    return TREAT_AS_FACT;

  }
}
