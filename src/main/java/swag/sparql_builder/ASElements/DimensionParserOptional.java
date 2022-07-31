package swag.sparql_builder.ASElements;

import java.util.List;
import java.util.Map;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.sparql_builder.CustomSPARQLQuery;

public class DimensionParserOptional extends DimensionAbstractParser {

  public DimensionParserOptional(AnalysisGraph ag, CustomSPARQLQuery asQuery,
      Map<MDElement, String> varMappings) {
    super(ag, asQuery, varMappings);

  }

  @Override
  public void parseAdditionals(IDimensionQualification dimToAS, MDSchema mdSchema,
      List<MDPathFragment> frgments) {
    // TODO Auto-generated method stub
  }


}
