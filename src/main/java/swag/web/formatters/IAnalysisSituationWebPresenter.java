package swag.web.formatters;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;

public interface IAnalysisSituationWebPresenter {

  public String presentHeader(AnalysisSituation as, AnalysisGraph ag);

  public String presentNavigationSteps(AnalysisSituation as, boolean forResults, AnalysisGraph ag);

  public String presentMeasures(AnalysisSituation as, AnalysisGraph ag);

  public String presentBaseMeasureConditions(AnalysisSituation as, AnalysisGraph ag);

  public String presentResultFilters(AnalysisSituation as, AnalysisGraph ag);

  public String presentMDConditions(AnalysisSituation as, AnalysisGraph ag);

  public String presentDimension(AnalysisSituation as, AnalysisGraph ag);

  public String presentDice(AnalysisSituation as, AnalysisGraph ag,
      IDimensionQualification dimToAS);

  public String presentConditions(AnalysisSituation as, AnalysisGraph ag,
      IDimensionQualification dimToAS);

  public String presentGranularity(AnalysisSituation as, AnalysisGraph ag,
      IDimensionQualification dimToAS);
}
