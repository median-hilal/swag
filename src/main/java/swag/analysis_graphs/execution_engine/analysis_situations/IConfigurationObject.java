package swag.analysis_graphs.execution_engine.analysis_situations;

import swag.sparql_builder.ASElements.configuration.Configuration;

public interface IConfigurationObject {

  /**
   * Gets the summarizability configuration of the measure
   * 
   * @return the configuration
   */
  public Configuration getConfiguration();

  /**
   * Sets the summarizability configuration of the measure
   * 
   * @param configuration
   */
  public void setConfiguration(Configuration configuration);
}
