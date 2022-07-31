package swag.sparql_builder.ASElements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.jena.sparql.core.Var;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.md_elements.MappingFunctions;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLUtilities;
import swag.sparql_builder.ASElements.configuration.DimensionConfigurationObject;
import swag.sparql_builder.reporting.AbstractDimensionReporter;
import swag.sparql_builder.reporting.IDimensionReoprter;

public class DimensionToSPARQLQueryGenerator implements IDimensionToSPARQLQueryGenerator {

  private static final Logger logger = Logger.getLogger(DimensionToSPARQLQueryGenerator.class);

  private List<IDimensionReoprter> reporters = new ArrayList<>();
  private AnalysisSituation as;
  private AnalysisGraph ag;
  private Map<MDElement, String> varMappings;
  private CustomSPARQLQuery query;
  private IDimensionQualification dimToAS;

  /**
   * 
   * Create a new {@code DimensionToSPARQLQueryGenerator} object.
   * 
   * @param configuration configuration object
   * @param ag analysis graph
   * @param varMappings variable to MD element mappings
   * 
   */
  public DimensionToSPARQLQueryGenerator(AnalysisSituation as, AnalysisGraph ag,
      CustomSPARQLQuery query, Map<MDElement, String> varMappings,
      IDimensionQualification dimToAS) {
    super();
    this.as = as;
    this.ag = ag;
    this.query = query;
    this.varMappings = varMappings;
    this.dimToAS = dimToAS;
    if (dimToAS.getGanularity() != null) {
      this.reporters = AbstractDimensionReporter.createReporter(ag.getSchema(),
          dimToAS.getGanularity().getPosition(), as);
    } else {
      logger.warn(" No granularity defined; no reporting will be done on dimension "
          + dimToAS.getD().getName() + dimToAS.getHierarchy().getName());
    }
  }

  private DimensionMDPathSpecification parseDimension(MDSchema mdSchema,
      IDimensionQualification dimToAS, AnalysisGraph ag) throws Exception {

    List<MDPathFragment> fragments = new ArrayList<>();
    List<String> filters = new ArrayList<>();
    List<MDElement> granularities = new ArrayList<>();

    IDimensionParser parser =
        DimensionParserCreator.createDimensionParser(as, ag, query, varMappings, mdSchema, dimToAS);

    parser.parseDimensionToAS(mdSchema, dimToAS, granularities, fragments, filters);

    DimensionMDPathSpecification d =
        new DimensionMDPathSpecification(fragments, filters, granularities);
    return d;
  }

  @Override
  public CustomSPARQLQuery generateSPARQLFromDimToAS(MDSchema mdSchema,
      IDimensionQualification dimToAS, Map<Var, Var> varsMappings) throws Exception {

    logger.info("Generating basic SPARQL on dimension " + dimToAS.getD().getName() + "/"
        + dimToAS.getHierarchy().getName());

    CustomSPARQLQuery query = CustomSPARQLQuery.createCustomSPARQLQueryWithEmptyGroup();
    boolean queryContainsArtificialHeadVar = false;
    DimensionMDPathSpecification d = parseDimension(mdSchema, dimToAS, ag);

    query = handleQueryBody(query, d, mdSchema, dimToAS);

    // Treating query header. Adding a (artificial) granularity variable
    if (d.getGranularities().size() > 0) {
      SPARQLUtilities.insertDistinctToSingleHeadedQuery(query);
    } else {
      query.getSparqlQuery().addResultVar("xXxArtificialxXx");
      queryContainsArtificialHeadVar = true;
    }

    query = handleIncompleteRelationships(query, d, varsMappings, mdSchema, dimToAS);
    handleFilters(query, d);

    // Removing artificial granularity variables
    if (queryContainsArtificialHeadVar) {
      query.getSparqlQuery().getProject().clear();
    }

    query.removeQueryPatternDuplications1();

    return query;
  }

  protected void handleFilters(CustomSPARQLQuery query, DimensionMDPathSpecification d) {
    // Adding filters
    for (String filter : d.getFilters()) {
      SPARQLUtilities.appendFiltersToQuery(query, filter);
    }
  }

  protected CustomSPARQLQuery handleIncompleteRelationships(CustomSPARQLQuery queryPar,
      DimensionMDPathSpecification d, Map<Var, Var> varsMappings, MDSchema mdSchema,
      IDimensionQualification dimToAS) {

    CustomSPARQLQuery query1 = queryPar;

    // treating incomplete relationships
    for (MDElement elem : d.getGranularities()) {
      query1.getSparqlQuery().getProjectVars().add(elem.getHeadVar());

      varsMappings.put(elem.getMapping().getQuery().getUnaryProjectionVar(), elem.getHeadVar());

      if (DimensionConfigurationObject.isIncompleteOther(mdSchema, as.getDimConfigs(),
          dimToAS.getHierarchy())) {
        SPARQLUtilities.appendIfToQueryHeader(query1, elem.getNameOfHeadVar());

        varsMappings.put(query1.getUnaryProjectionVar(), elem.getHeadVar());

      } else {
        if (DimensionConfigurationObject.isIncompleteSubElm(mdSchema, as.getDimConfigs(),
            dimToAS.getHierarchy())) {
          SPARQLUtilities.appendIfToQueryHeaderRecursively(query1, elem.getNameOfHeadVar(),
              mdSchema, elem);

          varsMappings.put(query1.getUnaryProjectionVar(), elem.getHeadVar());
        }
      }
    }
    return query1;
  }


  protected CustomSPARQLQuery handleQueryBody(CustomSPARQLQuery queryPar,
      DimensionMDPathSpecification d, MDSchema mdSchema, IDimensionQualification dimToAS)
      throws NoMappingExistsForElementException {
    // treating query body
    CustomSPARQLQuery query1 = queryPar;
    for (MDPathFragment fragment : d.getFragments()) {
      if (fragment.getTyp().equals(MDPathFragmentType.Granularity) && (DimensionConfigurationObject
          .isIncompleteOther(mdSchema, as.getDimConfigs(), dimToAS.getHierarchy())
          || DimensionConfigurationObject.isIncompleteSubElm(mdSchema, as.getDimConfigs(),
              dimToAS.getHierarchy()))) {
        query1 = SPARQLUtilities.joinWithAsOptionalOnlyPattern(query1,
            MappingFunctions.getPathQueryAndKeepIndividualMappingBlocksExclusive(mdSchema,
                fragment.getSource().getIdentifyingName(),
                fragment.getTarget().getIdentifyingName()).getQuery());
      } else {
        query1 = query1.joinWithOnlyPatterns(
            MappingFunctions.getPathQueryAndKeepIndividualMappingBlocksExclusive(mdSchema,
                fragment.getSource().getIdentifyingName(),
                fragment.getTarget().getIdentifyingName()).getQuery());
      }
    }
    return query1;
  }

  public Map<MDElement, String> getVarMappings() {
    return varMappings;
  }

  public void setVarMappings(Map<MDElement, String> varMappings) {
    this.varMappings = varMappings;
  }

  public CustomSPARQLQuery getQuery() {
    return query;
  }

  public void setQuery(CustomSPARQLQuery query) {
    this.query = query;
  }

  public IDimensionQualification getDimToAS() {
    return dimToAS;
  }

  public void setDimToAS(IDimensionQualification dimToAS) {
    this.dimToAS = dimToAS;
  }

  public List<IDimensionReoprter> getReporters() {
    return reporters;
  }

  public void setReporters(List<IDimensionReoprter> reporters) {
    this.reporters = reporters;
  }



}
