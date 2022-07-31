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
 * Parses fragments on a dimension. Only the involved dimensions's elements becomes mandatory.
 * 
 * @author swag
 *
 */
public class DimensionParserOnlyInvolvedElements extends DimensionAbstractParser {

  public DimensionParserOnlyInvolvedElements(AnalysisGraph ag, CustomSPARQLQuery asQuery,
      Map<MDElement, String> varMappings) {
    super(ag, asQuery, varMappings);
  }

  @Override
  public void parseAdditionals(IDimensionQualification dimToAS, MDSchema mdSchema,
      List<MDPathFragment> frgments) {

    Set<MDElement> elementsOnDim = dimToAS.getAllInvolvedElms();
    for (MDElement elem : elementsOnDim) {
    	if(!elem.isNullElement()){
      frgments
          .add(new MDPathFragment(mdSchema.getFactOfSchema(), elem, MDPathFragmentType.Mandatory));
    	}
    }
  }


}
