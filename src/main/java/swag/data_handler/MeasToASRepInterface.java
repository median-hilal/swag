package swag.data_handler;

import swag.analysis_graphs.execution_engine.ElementInsufficientSpecificationException;
import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureToAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAndAggFuncSpecificationInterface;

public interface MeasToASRepInterface {

  /**
   * generates a MeasureToAnalysisSiuation starting from the passed URI; the passed URI should be
   * valid
   * 
   * @param uri the name of the measure at hand
   * @param measures useless right now
   * @param as the analysis situation at hand
   * @return instance of MeasureToAnalysisSituation corresponding to the parameters passed
   * @throws NoMappingExistsForElementException
   */
  public MeasureAndAggFuncSpecificationInterface getMeasureToASByURI(
      IMeasureToAnalysisSituation measToASInt, String uri, String factURI, AnalysisSituation as)
      throws ElementInsufficientSpecificationException, NoMappingExistsForElementException;
}
