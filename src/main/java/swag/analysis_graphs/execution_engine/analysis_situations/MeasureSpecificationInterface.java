package swag.analysis_graphs.execution_engine.analysis_situations;

import swag.sparql_builder.ASElements.configuration.Configuration;

public interface MeasureSpecificationInterface extends ISpecification {

  public MeasureInAnalysisSituation getPosition();

  public void setPosition(MeasureInAnalysisSituation measurePosition);

  public AggregationOperationInAnalysisSituation getAggregationOperationInAnalysisSituation();

  public void setAggregationOperationInAnalysisSituation(
      AggregationOperationInAnalysisSituation aggregationOperation);

  public MeasureAndAggFuncSpecificationInterface shallowCopy();

  /**
   * 
   * Gets the configuration (summarizability wise) of the dimension qualification)
   * 
   * @return
   * 
   */
  public Configuration getConfiguration();


  public void setConfiguration(Configuration configuration);
}
