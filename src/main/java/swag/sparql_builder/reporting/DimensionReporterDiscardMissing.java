package swag.sparql_builder.reporting;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.sparql.core.Var;

import swag.analysis_graphs.execution_engine.analysis_situations.AggregationFunction;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.VarMetaData;
import swag.md_elements.Level;
import swag.md_elements.MDSchema;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLUtilities;

/**
 * @author swag
 *
 */
public class DimensionReporterDiscardMissing extends AbstractDimensionReporter {

  public DimensionReporterDiscardMissing(Level level, MDSchema mdSchema, AnalysisSituation as) {
    super(level, mdSchema, as);
  }

  @Override
  public CustomSPARQLQuery appendLevelStatementVariablesAndExpressions(CustomSPARQLQuery query) {
    getLevelStatementVariablesAndExpressions().put(getLevel().getHeadVar(), null);
    return query;
  }

  @Override
  public void fillBindStatementVariablesAndExpressions() {
    for (Var var : getLevelStatementVariablesAndExpressions().keySet()) {
      getBindStatementVariablesAndExpressions().put(Var.alloc(var.getName() + "NotBound"),
          "BIND (if(BOUND(?" + var.getName() + ") , 0, 1) AS ?is" + var.getName() + "NotBound)");
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
