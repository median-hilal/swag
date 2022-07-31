package swag.sparql_builder.ASElements;

import java.util.List;
import java.util.Map;
import java.util.Set;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.sparql_builder.CustomSPARQLQuery;

/**
 * 
 * Parses fragments on a dimension. All the elements of the involved dimensions becomes mandatory.
 * 
 * @author swag
 *
 */
public class DimensionParserMandatoryAllDimElements extends DimensionAbstractParser {

  /**
   * 
   * Generates a new {@code DimensionParserMandatoryAllDimElements} instance
   * 
   * @param ag
   * @param asQuery
   * @param varMappings
   */
  public DimensionParserMandatoryAllDimElements(AnalysisGraph ag, CustomSPARQLQuery asQuery,
      Map<MDElement, String> varMappings) {
    super(ag, asQuery, varMappings);
  }

  @Override
  public void parseAdditionals(IDimensionQualification dimToAS, MDSchema mdSchema,
      List<MDPathFragment> frgments) {

    Set<MDElement> elementsOnDim =
        mdSchema.getAllElementsOnDimension(dimToAS.getD().getIdentifyingName());

    for (MDElement elem : elementsOnDim) {
      frgments
          .add(new MDPathFragment(mdSchema.getFactOfSchema(), elem, MDPathFragmentType.Mandatory));
    }
  }

}
