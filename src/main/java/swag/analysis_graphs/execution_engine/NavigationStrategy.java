package swag.analysis_graphs.execution_engine;

import swag.analysis_graphs.dao.IAnalysisGraphDAO;
import swag.analysis_graphs.dao.IDataDAO;
import swag.analysis_graphs.dao.IMDSchemaDAO;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.md_elements.MDSchema;

/**
 * 
 * The contract that has to be implemented for executing a navigation step.
 * 
 * @author swag
 *
 */
public interface NavigationStrategy {

  /**
   * Executes a navigation step {@code nv}
   * 
   * @param nv the navigation step to be executed
   * @param schema the MD schema on which to execute the navigation
   * @param mdDao the
   * @param agDao
   * @param dataDao
   */
  public void doNavigate(NavigationStep nv, MDSchema schema, IMDSchemaDAO mdDao,
      IAnalysisGraphDAO agDao, IDataDAO dataDao);
}
