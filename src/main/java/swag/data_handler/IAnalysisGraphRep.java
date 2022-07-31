package swag.data_handler;

import java.util.List;

import swag.analysis_graphs.execution_engine.DefinedAGConditions;
import swag.analysis_graphs.execution_engine.DefinedAGConditionsTypes;
import swag.analysis_graphs.execution_engine.DefinedAGPredicates;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.predicates.IPredicateGraph;

/**
 * Contract to retrieve main analysis graph objects. Irrelevant to the underlying implementation.
 * 
 * @author swag
 *
 */
public interface IAnalysisGraphRep {

  /**
   * gets all the names of the available analysis situations
   * 
   * @return list of the names of the available ASs
   */
  public List<String> getAllAvailableAnalysisSituationsURIs(String agName);

  /**
   * given analysis situation name, this function scans the OWL file and constructs the analysis
   * situation
   * 
   * @param uri the name of the analysis situation
   * @return the analysis situation
   * @throws Exception
   */
  public AnalysisSituation getAnalysisSituationByURI(String uri, int getType) throws Exception;

  /**
   * Gets the condition types that are defined for an analysis graph
   * 
   * @param predicateGraph the swag.predicates graph.
   * 
   * @return and object of type {@code DefinedAGConditionsTypes}
   */
  public DefinedAGConditionsTypes getDefinedAGConditionTypes(IPredicateGraph predicateGraph);

  /**
   * Gets the conditions that are defined for an analysis graph
   * 
   * @param predicateGraph the swag.predicates graph.
   * 
   * @return and object of type {@code DefinedAGConditions}
   */
  public DefinedAGConditions getDefinedAGConditions(IPredicateGraph predicateGraph);

  /**
   * Gets the swag.predicates that are defined for an analysis graph
   * 
   * @param predicateGraph the swag.predicates graph.
   * 
   * @return and object of type {@code DefinedAGPredicates}
   */
  public DefinedAGPredicates getDefinedAGPredicates(IPredicateGraph predicateGraph);

  /**
   * gets all the names of the available navigation steps
   * 
   * @return list of the names of the available navigation steps
   */
  public List<String> getAllAvailableNavigationStepsNames();
}
