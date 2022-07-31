package swag.sparql_builder.ASElements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.expr.Expr;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.md_elements.Dimension;
import swag.md_elements.MDElement;
import swag.md_elements.QB4OHierarchy;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLUtilities;
import swag.sparql_builder.ASElements.configuration.DimensionConfigurationObject;

/**
 * 
 * Generates the dimensional subquery. Used when the selected non-strict hierarchies option is
 * splitting equally.
 * 
 * @author swag
 *
 */
public class DimensionSubQuerySplitGenerator extends DimensionsSubQueryGenerator {

  public DimensionSubQuerySplitGenerator(AnalysisGraph ag, AnalysisSituation as,
      CustomSPARQLQuery asQuery, Map<MDElement, String> varMappings) {
    super(ag, as, asQuery, varMappings);
  }

  @Override
  public List<Var> generateSubQuery(List<CustomSPARQLQuery> clonedList, Var factVariable,
      Map<Var, Var> varsMappings, CustomSPARQLQuery subquery) {

    List<Var> headGranularitiesVariables = new ArrayList<>();

    CustomSPARQLQuery query = createDimensionsSubQuery(clonedList, factVariable,
        headGranularitiesVariables, varsMappings);

    CustomSPARQLQuery outQuery = CustomSPARQLQuery.createCustomSPARQLQueryWithEmptyGroup();


    CustomSPARQLQuery tempQuery = new CustomSPARQLQuery(query);


    for (Var v : tempQuery.getSparqlQuery().getProjectVars()) {

      SPARQLUtilities.renameVariableByNumber(v.getName());
    }

    VarExprList exprs = tempQuery.getSparqlQuery().getProject();

    VarExprList exprList = new VarExprList();
    Map<Var, Var> mapping = new HashMap<>();

    // Renaming head variables in the split subquery to avoid any conflict with dimensions subquery.
    BiConsumer<Var, Expr> consumer = (v, e) -> {
      Var v1 = Var.alloc(SPARQLUtilities.renameVariableByNumber(v.getName()));
      exprList.add(v1, e);
      mapping.put(v, v1);
      varsMappings.put(v, v1);
    };

    exprs.forEachExpr(consumer);

    List<Var> variablesNotInExpressions = new ArrayList<>();
    for (Var v : tempQuery.getSparqlQuery().getProjectVars()) {
      if (!mapping.containsKey(v)) {
        variablesNotInExpressions.add(v);
      }
    }



    tempQuery.getSparqlQuery().getProject().clear();
    tempQuery.getSparqlQuery().getProject().addAll(exprList);
    tempQuery.getSparqlQuery().getProject().getVars().addAll(variablesNotInExpressions);

    outQuery.addSubQuery(tempQuery);

    List<String> nameOfCreatedVariable = new ArrayList<>();

    for (Var v : headGranularitiesVariables) {

      String mdElmName = getAs().getMDElementURIByQueryVariableName(v.getName());
      Dimension dim = getAg().getSchema().getDimensionOfLevel(mdElmName);
      QB4OHierarchy hier = getAg().getSchema().getHierarchyOfLevel(mdElmName);

      if (dim != null && DimensionConfigurationObject.isNonStrictNone(getAg().getSchema(),
          getAs().getDimConfigs(), dim)) {
        if (!v.equals(factVariable)) {
          outQuery = SPARQLUtilities.createAggregationQuery(outQuery, "COUNT",
              /* Calling the method for new variable */mapping.get(v), nameOfCreatedVariable);
        }
      }
    }
    outQuery.getSparqlQuery().getProjectVars().add(factVariable);
    outQuery.getSparqlQuery().addGroupBy(factVariable.getName());

    subquery.setSparqlQuery(query.getSparqlQuery());

    getAsQuery().addSubQuery(query);
    if (headGranularitiesVariables.size() > 0) {
      getAsQuery().addSubQuery(outQuery);
    }

    return headGranularitiesVariables;
  }

}
