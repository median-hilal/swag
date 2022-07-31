package swag.sparql_builder.ASElements;

import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.md_elements.MDSchema;
import swag.sparql_builder.CustomSPARQLQuery;

public interface IDimensionQueryGroupVarBodyBuilder {

  public CustomSPARQLQuery addDimensionalQueryGroupByVarBody(
      IDimensionQualification dimToAS, CustomSPARQLQuery rq, MDSchema mdSchema)
      throws Exception;
}
