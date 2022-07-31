package swag.analysis_graphs.dao;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.md_elements.MDSchema;


public interface IAnalysisGraphDAO {

  /**
   * 
   * Gets the analysis situations of the analysis graph, one by one, and then connects them with the
   * navigations. If an error occurs then a null is returned.
   * 
   * @param agName Name of the analysis graph
   * @param uri URI of the analysis graph
   * @param namespace the namespace of the analysis graph
   * @param mdSchema the multidimensional schema on top of which the analysis graph is built
   * 
   * @return an analysis graph, or null in case of an error.
   * 
   */
  public AnalysisGraph buildAnalysisGraph(String agName, String uri, String namespace,
      MDSchema mdSchema);
}

