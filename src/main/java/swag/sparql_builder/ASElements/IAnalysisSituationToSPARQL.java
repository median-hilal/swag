package swag.sparql_builder.ASElements;

import org.apache.jena.query.Query;

import swag.analysis_graphs.execution_engine.NoOrderFunctionApplicableException;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLQueryGenerationException;

/**
 * 
 * General methods to generate SPARQL queries form analysis situations.
 * 
 * @author swag
 *
 */
public interface IAnalysisSituationToSPARQL {

  /**
   * 
   * Generates the SPARQL query from the analysis situation, and enforces each implementing
   * generator to ensure filling of the variables map, that maps each specification in the analysis
   * situation to its variable.
   * 
   * @param as the analysis situation to generate query for.
   * 
   * @return a CustomSPARQLQuery
   * 
   * @throws SPARQLQueryGenerationException
   * 
   */
  public default CustomSPARQLQuery generateSPARQLFromAS(AnalysisSituation as)
      throws SPARQLQueryGenerationException {
    CustomSPARQLQuery query = doQueryGenerationMainProcessing(as);
    fillInRenamedVariablesMap();
    return query;
  }

  /**
   * 
   * The main logic used for generating the SPARQL query from the analysis situation.
   * 
   * @param as the analysis situation to generate query for.
   * 
   * @return a CustomSPARQLQuery
   * 
   * @throws SPARQLQueryGenerationException
   * 
   */
  public CustomSPARQLQuery doQueryGenerationMainProcessing(AnalysisSituation as)
      throws SPARQLQueryGenerationException;

  /**
   * 
   * Enforces each implementing generator to ensure filling of the variables map, that maps each
   * specification in the analysis situation to its variable.
   * 
   */
  public void fillInRenamedVariablesMap();

  /**
   * Generates a SPARQL query that gets the dice value corresponding to the current one in the
   * higher level
   * 
   * @param q the query from the lower level to the higherLevel
   * @param val1 the current dice value
   * @param varName the name of the variable on which to apply a FILTER expression and suppress it
   *        to {@param val1}
   * @return a query capable of retrieving the required new dice value
   */
  public Query generateQueryForNextUpDiceValue(Query q, String val1, String varName);

  /**
   * Generates a SPARQL query that gets the dice value next or previous to the current one.
   * 
   * @param q query
   * @param val1 the current dice value
   * @param varName the name of the variable on which to apply a FILTER expression and suppress it
   *        to {@param val1}
   * 
   * @return a query capable of retrieving the required new dice value
   */
  public Query generateQueryForNextOrPreviousDiceValue(Query q, String val1, String varName,
      boolean isNext) throws NoOrderFunctionApplicableException;

}
