package swag.sparql_builder.reporting;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.sparql.core.Var;

import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.analysis_situations.AggregationFunction;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.VarMetaData;
import swag.md_elements.Level;
import swag.md_elements.MDSchema;
import swag.md_elements.MappingFunctions;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLUtilities;

/**
 * @author swag
 *
 */
public class DimensionReporterPureVsNonPureFacts extends AbstractDimensionReporter {

  private static final org.apache.log4j.Logger logger =
      org.apache.log4j.Logger.getLogger(DimensionReporterPureVsNonPureFacts.class);

  public DimensionReporterPureVsNonPureFacts(Level level, MDSchema mdSchema, AnalysisSituation as) {
    super(level, mdSchema, as);
  }

  @Override
  public CustomSPARQLQuery appendLevelStatementVariablesAndExpressions(CustomSPARQLQuery query) {
    CustomSPARQLQuery addedQuery = new CustomSPARQLQuery();
    addedQuery.getSparqlQuery().getProject().add(getMdSchema().getFactOfSchema().getHeadVar());
    try {
      addedQuery.getSparqlQuery()
          .setQueryPattern(
              MappingFunctions
                  .getPathQuery(getMdSchema(), getMdSchema().getFactOfSchema().getIdentifyingName(),
                      getLevel().getIdentifyingName())
                  .getQuery().getSparqlQuery().getQueryPattern());
      addedQuery.getSparqlQuery().getGroupBy().add(getMdSchema().getFactOfSchema().getHeadVar());
      List<String> nameOfCreatedVariable = new ArrayList<>();
      SPARQLUtilities.createAggregationQuery(addedQuery, AggregationFunction.COUNT_DISTINCT,
          getLevel().getHeadVar(), nameOfCreatedVariable);
      getLevelStatementVariablesAndExpressions().put(Var.alloc(nameOfCreatedVariable.get(0)), null);
      query.insertSubQuery(addedQuery);
    } catch (NoMappingExistsForElementException e) {
      logger.warn("Cannot perform reporting with level " + getLevel().getName() + " because of "
          + e.getMessage());
    }
    return query;
  }

  @Override
  public void fillBindStatementVariablesAndExpressions() {
    for (Var var : getLevelStatementVariablesAndExpressions().keySet()) {
      getBindStatementVariablesAndExpressions().put(Var.alloc(var.getName() + "Count0"),
          "BIND (if(?" + var.getName() + "<= 1 , 1, 0) AS ?" + var.getName() + "Count0)");
      getBindStatementVariablesAndExpressions().put(Var.alloc(var.getName() + "Count1"),
          "BIND (if(?" + var.getName() + "> 1 , 1, 0) AS ?" + var.getName() + "Count1)");
    }
  }

  @Override
  public CustomSPARQLQuery appendBindStatementVariablesAndExpressions(CustomSPARQLQuery query) {
    String bindStatement = StringUtils.EMPTY;
    for (Var var : getBindStatementVariablesAndExpressions().keySet()) {
      bindStatement += getBindStatementVariablesAndExpressions().get(var) + " ";
    }
    String queryString =
        SPARQLUtilities.appendToQueryString(query.getSparqlQuery().toString(), bindStatement);

    return new CustomSPARQLQuery(queryString);
  }


  @Override
  public CustomSPARQLQuery appendMostOuterVariablesAndExpressions(CustomSPARQLQuery query) {

    List<String> nameOfCreatedVariable = new ArrayList<>();
    CustomSPARQLQuery newMsrQuery = new CustomSPARQLQuery(query);

    for (Var var : getBindStatementVariablesAndExpressions().keySet()) {
      newMsrQuery = SPARQLUtilities.createAggregationQuery(newMsrQuery, AggregationFunction.SUM,
          var, nameOfCreatedVariable);

      newMsrQuery.getSparqlQuery().getProject().forEachVarExpr((v, expr) -> {
        if (v.getName().equals(nameOfCreatedVariable.get(0))) {
          getMostOuterVariablesAndExpressions().put(v, expr);
          getAs().addToVarsMetaData(v, new VarMetaData(v, v.getName(), ""));
        }
      });
    }

    return newMsrQuery;
  }
}
