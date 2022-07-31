package swag.sparql_builder.reporting;

import java.util.Map;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;

import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregatedInAS;
import swag.md_elements.MDSchema;
import swag.sparql_builder.CustomSPARQLQuery;

/**
 * Contract for generating reporting to user on summarizability for measure summarizability issues.
 * 
 * @author swag
 *
 */
public interface IMeasureReoprter {

  /**
   * Gets the underlying MD schema
   * 
   * @return the MD schema
   */
  public MDSchema getMdSchema();

  /**
   * Gets the aggregated measure on which the reporting takes place
   * 
   * @return the aggregated measure on which the reporting takes place
   */
  public MeasureAggregatedInAS getAggregatedMeasure();

  /**
   * Gets the measures added by the reporter and their expressions if exist (assigned by
   * {@code appendMeasureStatementVariablesAndExpressions})
   * 
   * @return a map of added reporting measures variable and each corresponding expression
   */
  public Map<Var, Expr> getMeasureStatementVariablesAndExpressions();

  /**
   * Gets the BIND renamed variables and the whole BIND expression as a String, added by the
   * reporter (assigned by {@code appendBindStatementVariablesAndExpressions})
   * 
   * @return a map of added reporting BIND variable and each corresponding expression
   */
  public Map<Var, String> getBindStatementVariablesAndExpressions();

  /**
   * Gets the most outer aggregated measures used for the final reporting and their expressions when
   * needed, added by the reporter (assigned by {@code appendMostOuterVariablesAndExpressions})
   * 
   * @return a map of added final reported aggregated measure and each corresponding expression
   */
  public Map<Var, Expr> getMostOuterVariablesAndExpressions();

  /**
   * Step 1. in generating reporting query... generates the measure that has to be reported and
   * assigns it to the list of measures that are to be aggregated
   * ({@code getMeasureStatementVariablesAndExpressions}).
   * 
   * @param msrQuery the measure query to modify
   * 
   * @return the modified query that accounts for reporting
   */
  public CustomSPARQLQuery appendMeasureStatementVariablesAndExpressions(
      CustomSPARQLQuery msrQuery);

  /**
   * Step 2. in generating reporting query... generates BIND statements that are to be used further
   * up in the final query ({@code getBindStatementVariablesAndExpressions}).
   * 
   */
  public void fillBindStatementVariablesAndExpressions();

  /**
   * Step 3. in generating reporting query... appends the BIND expressions to the query. Gets the
   * BIND expressions using ({@code getBindStatementVariablesAndExpressions}).
   * 
   * @param query the main query to modify
   * 
   * @return the modified query that accounts for reporting with BIND expressions added
   */
  public CustomSPARQLQuery appendBindStatementVariablesAndExpressions(CustomSPARQLQuery query);

  /**
   * Step 4. in generating reporting query... generates the final outer aggregated measure that has
   * to be reported to the user and assigns it to the list of measures that are to be aggregated
   * ({@code getMostOuterVariablesAndExpressions}).
   * 
   * @param query the main query to append most outer reporting aggregation to
   * 
   * @return the modified query that accounts for reporting
   */
  public CustomSPARQLQuery appendMostOuterVariablesAndExpressions(CustomSPARQLQuery query);
}
