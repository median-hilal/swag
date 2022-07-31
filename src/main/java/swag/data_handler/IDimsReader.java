package swag.data_handler;

import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;

/**
 * Contract for reading dimension qualification of an analysis situation
 * 
 * @author swag
 */
public interface IDimsReader {

  /**
   * Reads all the specifications that are defined on dimension/hierarchy: Granularity, Dice, and
   * Selection.
   * 
   * @param uri the URI of the analysis situation individual
   * @param factURI the URI of the fact class of the analysis situation
   * @param as the analysis situation object
   * @throws Exception
   */
  public void readDims(String uri, String factURI, AnalysisSituation as) throws Exception;
}
