package swag.sparql_builder.ASElements;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.Query;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASMultiple;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASSimple;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateVariableToMDElementMapping;
import swag.md_elements.MDElement;
import swag.predicates.IPredicateFunctions;
import swag.predicates.IPredicateGraph;
import swag.predicates.LiteralCondition;
import swag.predicates.PredicateFunctionsFactory;
import swag.predicates.PredicateSyntacticTypes;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLUtilities;

public class SliceConditionSPARQLGenerator {

  public static String generatePredicateInASQuery(PredicateInASSimple predicate,
      IPredicateGraph predGraph) throws Exception {

    IPredicateFunctions predFuncs =
        PredicateFunctionsFactory.createObjectBasedPredicateFunctions(predGraph);

    Query query = predFuncs.generatePredicateInstanceQuery(predicate.getURI());
    Query queryClone = query.cloneQuery();
    PredicateVariableToMDElementMapping varofElemNam = predicate.getVarMappings().stream()
        .filter(x -> predicate.getPositionOfCondition().equalsIgnoreType(x.getElem())).findFirst()
        .orElse(null);

    if (varofElemNam == null) {
      throw new Exception("Cannot find a variable in the predicate that mathces the MD element "
          + predicate.getPositionOfCondition());
    }

    queryClone.getProjectVars()
        .removeIf(x -> !x.getName().equals(varofElemNam.getVar().getVariable()));

    if (queryClone.getProjectVars().size() == 0) {
      throw new Exception(
          "Cannot find a variable in predicate query matching the predicate variable "
              + varofElemNam.getVar().getVariable());
    }

    if (varofElemNam.getConnectOver() != null) {

      if (varofElemNam.getConnectOver().getProjectVars().size() != 2) {
        throw new Exception("Wrongly defined connect over ");
      }

      CustomSPARQLQuery newQuery = new CustomSPARQLQuery(varofElemNam.getConnectOver());
      Query newCloneInnerQuery = SPARQLUtilities.renameAllOccurrencesOfVariable(queryClone,
          varofElemNam.getVar().getVariable(),
          varofElemNam.getConnectOver().getProjectVars().get(1).getName());
      newQuery.addSubQuery(new CustomSPARQLQuery(newCloneInnerQuery));
      return newQuery.getSparqlQuery().toString();

    } else {
      return queryClone.toString();
    }
  }

  public static String generatePredicateInASQuery(PredicateInASMultiple predicate,
      IPredicateGraph predGraph) throws Exception {

    IPredicateFunctions predFuncs =
        PredicateFunctionsFactory.createObjectBasedPredicateFunctions(predGraph);

    Query query = predFuncs.generatePredicateInstanceQuery(predicate.getURI());
    Query queryClone = query.cloneQuery();
    Set<PredicateVariableToMDElementMapping> varofElemNams = predicate.getVarMappings();

    Set<String> varNames =
        varofElemNams.stream().map(x -> x.getVar().getVariable()).collect(Collectors.toSet());

    CustomSPARQLQuery outNewQuery = new CustomSPARQLQuery(queryClone);

    for (PredicateVariableToMDElementMapping varofElemNam : varofElemNams) {

      queryClone.getProjectVars().removeIf(x -> !varNames.contains(x.getName()));


      if (varofElemNam.getConnectOver() != null) {

        if (varofElemNam.getConnectOver().getProjectVars().size() != 2) {
          throw new Exception("Wrongly defined connect over ");
        }

        CustomSPARQLQuery newQuery = new CustomSPARQLQuery(varofElemNam.getConnectOver());
        Query newCloneInnerQuery = SPARQLUtilities.renameAllOccurrencesOfVariable(
            outNewQuery.getSparqlQuery(), varofElemNam.getVar().getVariable(),
            varofElemNam.getConnectOver().getProjectVars().get(1).getName());
        newQuery.addSubQuery(new CustomSPARQLQuery(newCloneInnerQuery));
        outNewQuery = newQuery;

      }
    }

    return outNewQuery.getSparqlQuery().toString();

  }

  public static String generateConditionnASQuery(ISliceSinglePosition predicate, AnalysisGraph ag,
      Map<MDElement, String> mdElemToVarMap) throws Exception {

    String condStr = StringUtils.EMPTY;
    LiteralCondition cond =
        ag.getDefinedAGConditions().getConditoinByIdentifyingName(predicate.getURI());

    if (cond != null) {
      Set<PredicateVariableToMDElementMapping> varofElemNams = cond.getMappings();
      Set<String> varNames =
          varofElemNams.stream().map(x -> x.getVar().getVariable()).collect(Collectors.toSet());
      condStr = cond.getExpression();

      for (PredicateVariableToMDElementMapping v : varofElemNams) {

        // The variable has been renamed somewhere
        if (mdElemToVarMap.containsKey(v.getElem())) {
          condStr =
              condStr.replace(v.getVar().getVariable(), "?" + mdElemToVarMap.get(v.getElem()));
        } else {
          // Otherwise fallback to default name from the MD schema
          condStr =
              condStr.replace(v.getVar().getVariable(), "?" + (v.getElem().getHeadVar() != null
                  ? v.getElem().getHeadVar().getVarName() : v.getElem().getName()));
        }
      }

      for (Map.Entry<String, String> binding : cond.getBindings().entrySet()) {
        condStr = condStr.replaceFirst(cond.getDirectInputVarByUri(binding.getKey()),
            SPARQLUtilities.literalzeConditoinOperand(binding.getValue()));
      }

      PredicateSyntacticTypes t = cond.getSyntacticType();

      switch (t) {
        case FILTER:
          condStr = "FILTER( " + condStr + " )";
          break;

        case DEFAULT:
          break;

        case HAVING:
          /*
           * Having clause is not added as no multiple HAVING clauses are allowed. The calling
           * routine should add HAVING clause.
           */
          break;

        default:
          break;
      }
    }
    return condStr;
  }

  public static Object generatePredicateInASQuery(PredicateInASMultiple predicate,
      AnalysisGraph ag) {
    throw new UnsupportedOperationException();
  }
}
