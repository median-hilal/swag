package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.HashMap;

import swag.md_elements.Measure;
import swag.sparql_builder.ASElements.configuration.Configuration;

public class MeasureAndAggFuncSpecificationImpl implements MeasureAndAggFuncSpecificationInterface {

  private MeasureInAnalysisSituation measure;
  private AggregationOperationInAnalysisSituation aggregationOperation;
  private ISetOfComparison set = NoneSet.getNoneSet();

  @Override
  public ISetOfComparison getSet() {
    return set;
  }

  @Override
  public void setSet(ISetOfComparison set) {
    this.set = set;
  }

  @Override
  public MeasureInAnalysisSituation getPosition() {
    return measure;
  }

  @Override
  public void setPosition(MeasureInAnalysisSituation measure) {
    this.measure = measure;
  }

  @Override
  public AggregationOperationInAnalysisSituation getAggregationOperationInAnalysisSituation() {
    return aggregationOperation;
  }

  @Override
  public void setAggregationOperationInAnalysisSituation(
      AggregationOperationInAnalysisSituation aggregationOperation) {
    this.aggregationOperation = aggregationOperation;
  }

  private Configuration configuration;

  @Override
  public Configuration getConfiguration() {
    return this.configuration;
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  public MeasureAndAggFuncSpecificationImpl() {}

  public MeasureAndAggFuncSpecificationImpl(MeasureInAnalysisSituation measure,
      AggregationOperationInAnalysisSituation aggregationOperation) {
    this.measure = measure;
    this.aggregationOperation = aggregationOperation;
  }

  public MeasureAndAggFuncSpecificationImpl(MeasureInAnalysisSituation measure,
      AggregationOperationInAnalysisSituation aggregationOperation, Configuration configuration) {

    this(measure, aggregationOperation);
    this.configuration = configuration;
  }

  public MeasureAndAggFuncSpecificationImpl(MeasureInAnalysisSituation measure,
      AggregationOperationInAnalysisSituation aggregationOperation, Configuration configuration,
      ISetOfComparison set) {

    this(measure, aggregationOperation, configuration);
    this.set = set;
  }

  @Override
  public MeasureAndAggFuncSpecificationInterface shallowCopy() {
    MeasureAndAggFuncSpecificationImpl spec = new MeasureAndAggFuncSpecificationImpl();
    spec.measure = this.measure.shallowCopy();
    spec.aggregationOperation = this.aggregationOperation.shallowCopy();
    spec.set = this.set.shallowCopy();
    spec.configuration =
        new Configuration(new HashMap<String, String>(this.configuration.getConfigurationsMap()));
    return spec;
  }

  @Override
  public boolean positionAsMDElementBasedEquals(ISpecification si) {
    if (si instanceof MeasureAndAggFuncSpecificationImpl) {
      MeasureAndAggFuncSpecificationImpl imp = (MeasureAndAggFuncSpecificationImpl) si;
      Measure meas1 = imp.getPosition().copyMeasureFromMeasureInAnalysisSituation();
      Measure meas2 = this.getPosition().copyMeasureFromMeasureInAnalysisSituation();;
      if (meas1.equals(meas2)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isDue() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void addToAnalysisSituationVariables(AnalysisSituation as) {
    // TODO Auto-generated method stub

  }

}
