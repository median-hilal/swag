package swag.sparql_builder.ASElements;

import java.util.List;
import org.apache.jena.sparql.core.Var;

import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregatedInAS;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.reporting.IMeasureReoprter;

/**
 * 
 * Contract for generating a SPARQL query from a measure of an analysis situation
 * 
 * @author swag
 *
 */
public interface IMeasureToSPARQLQueryGenerator {

  /**
   * 
   * Generates a SPARQL query from the measure specification at hand and the MD schema
   * 
   * @return a resulting SPARQL query
   * 
   * @throws NoMappingExistsForElementException
   * @throws Exception
   */
  public CustomSPARQLQuery generateSPARQLFromMeasToAS()
      throws NoMappingExistsForElementException, Exception;

  /**
   * 
   * Gets the visitor which
   * 
   * @return
   */
  public IASElementGenerateSPARQLVisitor getVisitor();

  /**
   * Generates the most outer aggregation variables of the main analytical query
   * 
   * @param subQuery the subquery of the measure of analysis situation at hand
   * @param measToAS the measure of analysis situation at hand
   * @param granCountVars
   * @param dims the dimensions qualification of the analysis situation
   * 
   * @return a new main SPARQL query
   */
  public CustomSPARQLQuery generateOuterMostAggVars(CustomSPARQLQuery subQuery,
      MeasureAggregatedInAS measToAS, List<Var> granCountVars, List<IDimensionQualification> dims,
      AnalysisSituation as);

  public List<IMeasureReoprter> getReporter();


}
