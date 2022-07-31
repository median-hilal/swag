package swag.sparql_builder;

import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.sparql_builder.ASElements.IAnalysisSituationToSPARQL;

/**
 * 
 * Generates SPARQL query from comparative analysis situation.
 * 
 * @author swag
 *
 */
public interface ICompAnalysisSituationQueryGenerator {

  /**
   * 
   * Generates the corresponding SPARQL query of a comparative analysis situation.
   * 
   * @param mainAS the comparative analysis situation to generate SPARQL for
   * @param generator the normal SPARQL generator for analysis situation
   * 
   * @return a SPARQL query corresponding to the comparative analysis situation
   * 
   * @throws Exception
   * 
   */
  public CustomSPARQLQuery generateComparativeQuery(AnalysisSituation mainAS,
      IAnalysisSituationToSPARQL generator) throws Exception;
}
