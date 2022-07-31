package swag.data_handler;

import java.util.List;

import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.NoSuchElementExistsException;
import swag.graph.Graph;
import swag.md_elements.Dimension;
import swag.md_elements.Fact;
import swag.md_elements.Level;
import swag.md_elements.MDElement;
import swag.md_elements.MDRelation;
import swag.md_elements.Measure;

public interface MDSchemaRepInterface {

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
   * Gets the first (arbitraty) next level in a hierarchy on a dimension
   * 
   * @param currLevelURI
   * @param factURI
   * @return
   * @throws NoMappingExistsForElementException
   */
  public Level getFirstNextRollUpLevel(String currLevelURI, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException;

  /**
   * Gets the next level specified as a parameter in a hierarchy on a dimension
   * 
   * @param nextLevelURI the URI of the required next Level
   * @param currLevelURI
   * @param factURI
   * @return
   * @throws NoMappingExistsForElementException
   */
  public Level getNextRollUpLevel(String nextLevelURI, String currLevelURI, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException;

  /**
   * Gets the first (arbitraty) previous level in a hierarchy on a dimension
   * 
   * @param currLevelURI
   * @param factURI
   * @return
   * @throws NoMappingExistsForElementException
   */
  public Level getFirstPreviousRollUpLevel(String currLevelURI, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException;

  /**
   * Gets the previous level specified as a parameter in a hierarchy on a dimension
   * 
   * @param currLevelURI
   * @param factURI
   * @return
   * @throws NoMappingExistsForElementException
   */
  public Level getPreviousRollUpLevel(String previousLevelURI, String currLevelURI, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException;

  /**
   * Gets all the levels on a dimension
   * 
   * @param dimURI the dimension URI to get the levels on
   * @param factURI
   * @param con
   * @return a list of level URI if they exist, otherwise n empty list
   */
  public List<String> getLevelsOnDimension(String dimURI, String factURI);

  /**
   * gets the URI for SPARQL querying
   * 
   * @return the string of the SPARQL endpoint, and an empty String in case it cannot be found
   */
  public String getEndpointURI();
}
