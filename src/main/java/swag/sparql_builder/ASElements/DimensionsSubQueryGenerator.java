package swag.sparql_builder.ASElements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.jena.sparql.core.Var;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.md_elements.MDElement;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLUtilities;

public class DimensionsSubQueryGenerator {

  private static final Logger logger = Logger.getLogger(DimensionsSubQueryGenerator.class);

  private AnalysisGraph ag;
  private AnalysisSituation as;
  private CustomSPARQLQuery asQuery;
  private ASElementSPARQLGenerator visitor;
  private Map<MDElement, String> varMappings;

  public DimensionsSubQueryGenerator(AnalysisGraph ag, AnalysisSituation as,
      CustomSPARQLQuery asQuery, Map<MDElement, String> varMappings) {
    super();
    this.ag = ag;
    this.as = as;
    this.asQuery = asQuery;
    this.varMappings = varMappings;
  }

  /**
   * 
   * Main function that generates the dimensions subquery.
   * 
   * @param clonedList
   * @param factVariable
   * @param varsMappings used to map granularity variable name with it corresponding new name in the
   *        query of the split.
   * 
   * @return list of variables corresponding to the granularities
   */
  public final List<Var> mainGenerateSubQuery(List<CustomSPARQLQuery> clonedList, Var factVariable,
      Map<Var, Var> varsMappings) {

    CustomSPARQLQuery subQuery = new CustomSPARQLQuery();
    List<Var> vars = generateSubQuery(clonedList, factVariable, varsMappings, subQuery);
    addMultipleSlicesToDimensionsQuery();
    return vars;
  }

  /**
   * 
   * Sub function that can be overriden to define new behaviors.
   * 
   * @param mdSchema
   * @param rq
   * @param clonedList
   * @param factVariable
   * @param as
   * @param varsMappings
   * @param subQuery out variable
   * @return list of variables corresponding to the granularities
   * 
   */
  protected List<Var> generateSubQuery(List<CustomSPARQLQuery> clonedList, Var factVariable,
      Map<Var, Var> varsMappings, CustomSPARQLQuery subQuery) {
    return addDimensionsSubQuery(clonedList, factVariable, varsMappings, subQuery);
  }

  protected CustomSPARQLQuery createDimensionsSubQuery(List<CustomSPARQLQuery> clonedList,
      Var factVariable, List<Var> headGranularitiesVariables, Map<Var, Var> varsMappings) {

    CustomSPARQLQuery subQuery = new CustomSPARQLQuery();

    subQuery =
        subQuery.joinWithFullyAsGroups(ag.getSchema().getFactOfSchema().getMapping().getQuery());

    for (CustomSPARQLQuery rootedQuery : clonedList) {
      if (rootedQuery.getSparqlQuery().getQueryPattern() != null) {
        // Saving the new names of granularity variables in case they are renamed.
        if (rootedQuery.hasHeadVars()) {
          headGranularitiesVariables.add(rootedQuery.getUnaryProjectionVar());
        }
        subQuery = subQuery.joinWithFullyAsGroups(rootedQuery);
      }
    }

    subQuery.removeQueryPatternDuplications();
    subQuery.getSparqlQuery().getProjectVars().add(factVariable);
    SPARQLUtilities.insertDistinctToHeadedQuery(subQuery);
    return subQuery;
  }

  /**
   * 
   * Creates a the dimensional query of the final query.
   * 
   * @param rq
   * @param clonedList
   * @param factVariable
   * @return A list of granularity variables, possibly renamed
   * 
   */
  protected final List<Var> addDimensionsSubQuery(List<CustomSPARQLQuery> clonedList,
      Var factVariable, Map<Var, Var> varsMappings, CustomSPARQLQuery subQuery) {

    // If variables are renamed
    List<Var> headGranularitiesVariables = new ArrayList<>();

    subQuery.setSparqlQuery(
        createDimensionsSubQuery(clonedList, factVariable, headGranularitiesVariables, varsMappings)
            .getSparqlQuery());

    asQuery.addSubQuery(subQuery);



    return headGranularitiesVariables;
  }


  protected final void addMultipleSlicesToDimensionsQuery() {
    IASElementGenerateSPARQLVisitor visitor = new ASElementSPARQLGenerator(ag, varMappings);

    /*
     * for (IMultipleSliceSpecification s : as.getMultipleSlices()) { try { visitor.visit(s);
     * subQuery.addSubQuery(new CustomSPARQLQuery(visitor.getReturn().get(0))); } catch (Exception
     * e) { logger.error("Cannot generate SPARQL from the multiple slice ", e); } }
     */

  }

  public AnalysisGraph getAg() {
    return ag;
  }

  public void setAg(AnalysisGraph ag) {
    this.ag = ag;
  }

  public CustomSPARQLQuery getAsQuery() {
    return asQuery;
  }

  public void setAsQuery(CustomSPARQLQuery asQuery) {
    this.asQuery = asQuery;
  }

  public ASElementSPARQLGenerator getVisitor() {
    return visitor;
  }

  public void setVisitor(ASElementSPARQLGenerator visitor) {
    this.visitor = visitor;
  }

  public Map<MDElement, String> getVarMappings() {
    return varMappings;
  }

  public void setVarMappings(Map<MDElement, String> varMappings) {
    this.varMappings = varMappings;
  }

  public AnalysisSituation getAs() {
    return as;
  }

  public void setAs(AnalysisSituation as) {
    this.as = as;
  }
}
