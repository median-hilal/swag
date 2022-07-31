package swag.sparql_builder.reporting;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.sparql.core.Var;

import swag.analysis_graphs.execution_engine.analysis_situations.AggregationFunction;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregatedInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.VarMetaData;
import swag.md_elements.MDSchema;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLUtilities;

public class MeasureReporterSeparateFact extends AbstractMeasureReporter {

  public MeasureReporterSeparateFact(MeasureAggregatedInAS measToAS, MDSchema mdSchema,
      AnalysisSituation as) {
    super(measToAS, mdSchema, as);
  }

  @Override
  public CustomSPARQLQuery appendMeasureStatementVariablesAndExpressions(
      CustomSPARQLQuery msrQuery) {

    msrQuery.getSparqlQuery().getProject().forEachVarExpr((var, expr) -> {
      if (var.getName()
          .equals(getAggregatedMeasure().getMeasure().getMeasure().getHeadVar().getName())) {
        getMeasureStatementVariablesAndExpressions().put(var, null);
      }
    });
    getMeasureStatementVariablesAndExpressions().put(getMdSchema().getFactOfSchema().getHeadVar(),
        null);

    return msrQuery;
  }

  @Override
  public void fillBindStatementVariablesAndExpressions() {
    for (Var var : getMeasureStatementVariablesAndExpressions().keySet()) {
      getBindStatementVariablesAndExpressions().put(var, StringUtils.EMPTY);
    }
  }

  @Override
  public CustomSPARQLQuery appendBindStatementVariablesAndExpressions(CustomSPARQLQuery query) {
    return query;
  }


  @Override
  public CustomSPARQLQuery appendMostOuterVariablesAndExpressions(CustomSPARQLQuery query) {

    List<String> nameOfCreatedVariable = new ArrayList<>();
    CustomSPARQLQuery newMsrQuery = new CustomSPARQLQuery(query);

    for (Var var : getBindStatementVariablesAndExpressions().keySet()) {
      newMsrQuery = SPARQLUtilities.createAggregationQuery(newMsrQuery,
          AggregationFunction.COUNT_DISTINCT, var, nameOfCreatedVariable);

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
