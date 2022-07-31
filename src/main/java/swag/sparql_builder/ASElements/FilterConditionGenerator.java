package swag.sparql_builder.ASElements;

import java.util.Map;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToResultFilters;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.md_elements.MDElement;

public class FilterConditionGenerator {

  Map<MDElement, String> mdElemToVarMap;
  AnalysisGraph ag;


  public String generateFilterConditoinString(
      ISliceSinglePosition<AnalysisSituationToResultFilters> cond) {


    return "";
  }
}
