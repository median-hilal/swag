package swag.data_handler;

import swag.sparql_builder.ASElements.configuration.Configuration;

public interface IConfigurationReader {

  public Configuration readConfiguration() throws Exception;
}
