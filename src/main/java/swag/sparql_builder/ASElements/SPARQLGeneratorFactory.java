package swag.sparql_builder.ASElements;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.md_elements.MDSchema;
import swag.md_elements.MDSchemaType;
import swag.sparql_builder.AsSPARQLGeneratorExtended;
import swag.sparql_builder.AsSPARQLGeneratorSimple;

/**
 * 
 * Creates a new IAnalysisSituationToSPARQL instance.
 * 
 * @author swag
 *
 */
public class SPARQLGeneratorFactory {

  public static IAnalysisSituationToSPARQL createSPARQLGenerator(MDSchema schema,
      AnalysisGraph graph) {
    if (schema.getSchemaType().equals(MDSchemaType.QB4OLAP)) {
      return new AsSPARQLGeneratorSimple(schema, graph);
    } else {
      if (schema.getSchemaType().equals(MDSchemaType.SMD)) {
        return new AsSPARQLGeneratorExtended(schema, graph);
      } else {
        throw new RuntimeException("Cannot find a suitable generator for this MD schema type");
      }
    }
  }
}
