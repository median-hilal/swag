package swag.data_handler;

import swag.analysis_graphs.execution_engine.ElementInsufficientSpecificationException;
import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;

public interface IQualificationDimToASRepInterface {

  /**
   * when diceNode and diceLevel, one is null and the other is not null, the method assigns null to
   * both The method doesn't check the semantics of the existence of the variables For each
   * component, it either assigns a value, a variable value, or a null
   * 
   * @param uri of dimensionToAS
   * @param factURI the fact of the analysis situation at hand
   * @param as
   * @return a DimensionsToAnalysisSituation generated from parsing
   * @throws NoMappingExistsForElementException
   */
  public IDimensionQualification getDimensionToASByURI(String uri, String factURI,
      AnalysisSituation as) throws Exception, ElementInsufficientSpecificationException,
      NoMappingExistsForElementException;
}
