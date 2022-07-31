package swag.data_handler;

import java.util.List;
import java.util.Map;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Statement;

import swag.analysis_graphs.dao.IAnalysisGraphDAO;
import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.DefinedAGPredicates;
import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.NoSuchElementExistsException;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.analysis_graphs.execution_engine.operators.Operation;
import swag.md_elements.Dimension;
import swag.md_elements.Fact;
import swag.md_elements.Level;
import swag.md_elements.MDSchema;
import swag.md_elements.Mapping;
import swag.md_elements.Measure;

public interface OWLRepsAccessInterface extends IAnalysisGraphDAO {

  public void readOWL(String owlPath, String owlName);


  public void appendOWL(String owlPath, String owlName);


  public AnalysisGraph buildAnalysisGraph(String name, String uri, String namespace,
      MDSchema mdSchema);


  public DefinedAGPredicates getDefinedAGPredicates();

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
   * @param uri
   * @return
   * @throws NoSuchElementExistsException
   * @throws NoMappingExistsForElementException
   */
  public Mapping getMappingByElementURI(String uri)
      throws NoSuchElementExistsException, NoMappingExistsForElementException;

  /**
   * This function should be only called for paths that have mapping; otherwise its behavior is not
   * expectable
   * 
   * @param p the path to generate its mapping
   * @return
   */
  public List<Query> getPathQueries(swag.data_handler.Path p);

  /**
   * gets the query connecting mdElementURI1 and mdElementURI1 by joining the queries on the path.
   * Throws a NoMappingExistsForElementException when an error occurs
   * 
   * @param mdElementURI2
   * @param mdElementURI1
   * @return
   * @throws NoMappingExistsForElementException
   */
  public Mapping getMappingQueryBetweenTwoElementsByURI(String mdElementURI2, String mdElementURI1)
      throws NoMappingExistsForElementException;

  /**
   * @param ind
   * @return
   */
  public Map<OntProperty, List<Individual>> getOutMDInPMappedPropsAndValues(Individual ind);

  /**
   * @param ind
   * @return
   */
  public Map<OntProperty, List<Individual>> getOutMDInPMappedPropsAndValuesThatHaveMapping(
      Individual ind);

  /**
   * Each edge is considered to be uniquely defined by its start and end nodes.
   * 
   * @param stmt a triple
   * @return true if the statement has a mapping query
   */
  public boolean checkIfMDPropertyHasMapping(Statement stmt);

  /**
   * @return
   */
  public MDSchema getMDSchemaByByURI();

  /**
   * @return
   */
  public List<Fact> getMDSchemaFact();

  /**
   * If the uri declares a dimension, the method builds returns the dimension, otherwise throws and
   * exception
   * 
   * @param uri the URI of the dimension
   * @return new Dimension
   * @throws NoSuchElementExistsException when uri doesn't lead to an element or leads to an element
   *         that is not a dimension
   */
  public Dimension getDimensionByURI(String uri) throws NoSuchElementExistsException;

  /**
   * @param uri
   * @param factURI
   * @return
   * @throws NoSuchElementExistsException
   * @throws NoMappingExistsForElementException
   */
  public Level getLevelByURI(String uri, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException;

  /**
   * creates unmapped level depending on the uri passed
   * 
   * @param uri
   * @return Level
   * @throws NoSuchElementExistsException
   * @throws NoMappingExistsForElementException
   */
  public Level getLevelByURI(String uri) throws NoSuchElementExistsException;

  /**
   * @param uri
   * @return
   * @throws NoSuchElementExistsException
   * @throws NoMappingExistsForElementException
   */
  public Fact getFactByURI(String uri)
      throws NoSuchElementExistsException, NoMappingExistsForElementException;

  /**
   * creates unmapped measure
   * 
   * @param uri
   * @param factURI
   * @return new Measure, which has no Mapping
   * @throws NoSuchElementExistsException
   * @throws NoMappingExistsForElementException
   */
  public Measure getMeasureByURI(String uri) throws NoSuchElementExistsException;

  /**
   * creates a mapped measure
   * 
   * @param uri
   * @param factURI
   * @return new Measure
   * @throws NoSuchElementExistsException
   * @throws NoMappingExistsForElementException
   */
  public Measure getMeasureByURI(String uri, String factURI)
      throws NullPointerException, NoSuchElementExistsException, NoMappingExistsForElementException;

  /**
   * Gets the next level in a hierarchy on a dimension
   * 
   * @param currLevelURI
   * @param factURI
   * @return
   * @throws NoMappingExistsForElementException
   */
  public Level getNextRollUpLevel(String currLevelURI, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException;

  /**
   * Gets the previous level in a hierarchy on a dimension
   * 
   * @param currLevelURI
   * @param factURI
   * @return
   * @throws NoMappingExistsForElementException
   */
  public Level getPreviousRollUpLevel(String currLevelURI, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException;

  /**
   * Gets all the levels on a dimension
   * 
   * @param dimURI the dimension URI to get the levels on
   * @param factURI
   * @param con
   * @return a list of level URI if they exist, otherwise n empty list
   */
  public List<String> getLevelPossibleValues(String dimURI, String factURI);

  /**
   * gets all the names of the available navigation steps
   * 
   * @return list of the names of the available navigation steps
   */
  public List<String> getAllAvailableNavigationStepsNames();

  /**
   * gets the operators of the navigation step passed by URI
   * 
   * @param nvURI the URI of the intended navigation step
   * @param factURI
   * @return a list of navigation operators
   */
  public List<Operation> getNavigationStepOperators(NavigationStep nv, String factURI);


  public String getEndpointURI();
}
