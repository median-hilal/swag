package swag.sparql_builder.ASElements;

import java.util.Map;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.md_elements.MDSchemaType;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.ASElements.configuration.DimensionConfigurationObject;

/**
 * 
 * Creates suitable implementation instances depending on the passed configurations.
 * 
 * @author swag
 *
 */
public class DimensionParserCreator {

  public static DimensionsSubQueryGenerator createDimensionsSubQueryGenerator(AnalysisSituation as,
      AnalysisGraph ag, CustomSPARQLQuery query, Map<MDElement, String> varMappings) {

    for (IDimensionQualification dim : as.getDimensionsToAnalysisSituation()) {
      if (DimensionConfigurationObject.isNonStrictSplit(ag.getSchema(), as.getDimConfigs(),
          dim.getD())) {
        return new DimensionSubQuerySplitGenerator(ag, as, query, varMappings);
      }
    }
    return new DimensionsSubQueryGenerator(ag, as, query, varMappings);
  }

  /**
   * 
   * Creates suitable dimension parser implementation depending on the passed parameters.
   * 
   * @param conf
   * @return
   * 
   */
  public static IDimensionParser createDimensionParser(AnalysisSituation as, AnalysisGraph ag,
      CustomSPARQLQuery query, Map<MDElement, String> varMappings, MDSchema schmea,
      IDimensionQualification dimQ) {

    if (ag.getSchema().getSchemaType().equals(MDSchemaType.QB4OLAP)) {
      return new DimensionParserOnlyInvolvedElements(ag, query, varMappings);
    }

    if (DimensionConfigurationObject.isIncompleteOther(schmea, as.getDimConfigs(), dimQ.getD())
        || DimensionConfigurationObject.isIncompleteSubElm(schmea, as.getDimConfigs(),
            dimQ.getD())) {
      if (false /* conf.is("mandatory", "baselevel") */) {
        return new DimensionParserMandatoryTerminalLevels(ag, query, varMappings);
      } else {
        if (false /* conf.is("mandatory", "all") */) {
          return new DimensionParserMandatoryAllDimElements(ag, query, varMappings);
        } else {
          return new DimensionParserOptional(ag, query, varMappings);
        }
      }
    } else {
      return new DimensionParserNormalSettings(ag, query, varMappings);
    }
  }
}
