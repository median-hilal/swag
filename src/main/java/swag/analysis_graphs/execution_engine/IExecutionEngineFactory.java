package swag.analysis_graphs.execution_engine;

/**
 * Factory contract to create an analysis graph engine instance
 * 
 * @author swag
 *
 */
public interface IExecutionEngineFactory {

  /**
   * Creates the analysis graph engine instance
   * 
   * @param pathToSourceOntologies the path to ontologies that contain the required information to
   *        populate the analysis graph engine
   * @param mdSchemaFileName the name of the file that contains the MD schema
   * @param mdSchemaName the name of the MD schema
   * @param analysisGraphFileName the name of the analysis graph file
   * @param analysisGraphName the name of the analysis graph
   * 
   * @return an analysis graph engine instance
   * @throws Exception
   */
  public AnalysisGraphsManager initiateExecutionEngin(String pathToSourceOntologies,
      String mdSchemaFileName, String mdSchemaName, String analysisGraphFileName,
      String analysisGraphName) throws Exception;
}
