package swag.sparql_builder.ASElements.configuration;

import java.util.Map;

public class DimensionConfiguration extends Configuration {

  public DimensionConfiguration(Map<String, String> configurations) {
    super(configurations);
    // TODO Auto-generated constructor stub
  }

  public boolean isOptional() {
    return getConfiguration("optional").equals("true");
  }

}
