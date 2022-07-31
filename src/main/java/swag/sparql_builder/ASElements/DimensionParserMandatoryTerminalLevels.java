package swag.sparql_builder.ASElements;

import java.util.List;
import java.util.Map;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.md_elements.Level;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.sparql_builder.CustomSPARQLQuery;

public class DimensionParserMandatoryTerminalLevels extends DimensionAbstractParser {

  public DimensionParserMandatoryTerminalLevels(AnalysisGraph ag, CustomSPARQLQuery asQuery,
      Map<MDElement, String> varMappings) {
    super(ag, asQuery, varMappings);
  }

  @Override
  public void parseAdditionals(IDimensionQualification dimToAS, MDSchema mdSchema,
      List<MDPathFragment> frgments) {
    Level terminalLevel = mdSchema.getFinestLevelOnDimension(dimToAS.getD().getIdentifyingName());
    frgments.add(new MDPathFragment(mdSchema.getFactOfSchema(), terminalLevel,
        MDPathFragmentType.Mandatory));
  }


}
