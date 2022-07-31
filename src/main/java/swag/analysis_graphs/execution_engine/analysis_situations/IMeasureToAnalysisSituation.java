package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;

public interface IMeasureToAnalysisSituation extends Serializable, ISignatureType {

  public AnalysisSituation getAs();

  public void setAs(AnalysisSituation as);

  public MeasureSpecificationInterface getMeasureSpecificationInterface();

  public void setMeasureSpecificationInterface(
      MeasureSpecificationInterface measAndAggSpecifications);

  public IMeasureToAnalysisSituation copy();

}
