package swag.sparql_builder.reporting;

import java.util.Map;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;

import swag.md_elements.Level;
import swag.md_elements.MDSchema;
import swag.sparql_builder.CustomSPARQLQuery;

/**
 * Contract for generating reporting to user on summarizability for dimension summarizability
 * issues.
 * 
 * @author swag
 *
 */
public interface IDimensionReoprter {

  /**
   * Default process flow for reporting that spans the four steps explained in the documentation of
   * the methods in this interface.
   * 
   * @param querythe query to append reporting to.
   * 
   * @return the new query that corresponds to the performed reporting.
   */
  public default CustomSPARQLQuery doReport(CustomSPARQLQuery query) {
    CustomSPARQLQuery newQuery = new CustomSPARQLQuery(query);
    newQuery = this.appendLevelStatementVariablesAndExpressions(newQuery);
    this.fillBindStatementVariablesAndExpressions();
    newQuery = this.appendBindStatementVariablesAndExpressions(newQuery);
    newQuery = this.appendMostOuterVariablesAndExpressions(newQuery);
    return newQuery;
  }

  /**
   * Gets the MD schema
   * 
   * @return the MD schema
   */
  public MDSchema getMdSchema();

  /**
   * Gets the level on which the reporting takes place
   * 
   * @return the level on which the reporting takes place
   */
  public Level getLevel();

  /**
   * Gets the levels added by the reporter and their expressions if exist (assigned by
   * {@code appendLevelStatementVariablesAndExpressions})
   * 
   * @return a map of added reporting measures variable and each corresponding expression
   */
  public Map<Var, Expr> getLevelStatementVariablesAndExpressions();

  /**
   * Step 1. in generating reporting query... generates the query and the variables related to level
   * that has to be reported and assigns it to the list of variables that are to be aggregated
   * ({@code getLevelStatementVariablesAndExpressions}).
   * 
   * @param msrQuery the measure query to modify
   * 
   * @return the modified query that accounts for reporting
   */
  public CustomSPARQLQuery appendLevelStatementVariablesAndExpressions(CustomSPARQLQuery msrQuery);

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
