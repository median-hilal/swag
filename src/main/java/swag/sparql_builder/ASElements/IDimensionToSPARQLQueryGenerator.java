package swag.sparql_builder.ASElements;

import java.util.List;
import java.util.Map;

import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.md_elements.MDSchema;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.reporting.IDimensionReoprter;

/**
 * 
 * Responsible of generating SPARQL query from a dimension qualification
 * 
 * @author swag
 *
 */
public interface IDimensionToSPARQLQueryGenerator {

  /**
   * 
   * Generates a SPARQL query from the dimension qualifiation at hand and the MD schema
   * 
   * @param mdSchema the MD schema at hand
   * @param dimToAS the dimensional qualification at hand
   * 
   * @return a SPARQL query for the dimension
   * 
   * @throws Exception
   * 
   */
  public CustomSPARQLQuery generateSPARQLFromDimToAS(MDSchema mdSchema,
      IDimensionQualification dimToAS,
      Map<org.apache.jena.sparql.core.Var, org.apache.jena.sparql.core.Var> varsMappings)
      throws Exception;

  public List<IDimensionReoprter> getReporters();
}
