package swag.data_handler;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationReader implements IConfigurationReader {

  public swag.sparql_builder.ASElements.configuration.Configuration readConfiguration() {


    Map<String, String> configurations = new HashMap<String, String>();

    return new swag.sparql_builder.ASElements.configuration.Configuration(
        configurations);

  }
}
