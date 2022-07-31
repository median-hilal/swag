package swag.sparql_builder.ASElements;

import java.util.List;

import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;

public interface IDimensionParser {

  /**
   * 
   * Compares granularity, dice, and slice specifications. generates a set of disjoint MD paths that
   * constitute the dimension structure. Granularity on the dimension is also an output of this
   * function. Also filters of slice and dice are results. The resulting MD fragments can be used
   * later to generate a dimension query
   * 
   * @param mdSchema
   * @param dimToAS
   * @param granularities
   * @param frgments
   * @param filters
   * @throws Exception
   */
  public void parseDimensionToAS(MDSchema mdSchema, IDimensionQualification dimToAS,
      List<MDElement> granularities, List<MDPathFragment> frgments, List<String> filters)
      throws Exception;
}
