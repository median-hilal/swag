package swag.analysis_graphs.dao;

import java.util.List;
import java.util.Map;
import org.apache.jena.query.ResultSet;

import swag.md_elements.MDSchema;

/**
 * 
 * This interface provides methods to retrieve data-specific information from a data connection. For
 * example, possible values for a level in a multidimensional schema. Methods in this interface work
 * on the actual underlying data.
 * 
 * @author swag
 *
 */
public interface IDataDAO {


  /**
   * 
   * Executes a query against a provided endpointURI and retrieves the results.
   * 
   * @param query a SPARQL query to execute
   * 
   * @return result set of the query
   * 
   * @throws Exception when any error occurs during execution
   * 
   */
  public ResultSet sendQueryToEndpointAndGetResults(String queryString) throws Exception;

  /**
   * 
   * Gets the dice value in the upper level for the current dice value.
   * 
   * @param mdSchema the MD schema at hand
   * @param currDiceVal the current dice value as a String
   * @param currLevelURI the URI of the dice level
   * 
   * @return the corresponding dice value in the upper level.
   * 
   * @throws Exception
   * 
   */
  public String getNextUpDiceValue(MDSchema mdSchema, String currDiceVal, String currLevelURI)
      throws Exception;


  /**
   * 
   * Gets the next dice value in the current level.
   * 
   * @param mdSchema the MD schema at hand
   * @param currDiceVal the current dice value as a String
   * @param currLevelURI the URI of the dice level
   * 
   * @return the next dice value in the current level.
   * 
   * @throws Exception
   * 
   */
  public String getNextDiceValue(MDSchema mdSchema, String currDiceVal, String currLevelURI)
      throws Exception;

  /**
   * 
   * Gets the previous dice value in the current dice level.
   * 
   * @param mdSchema the MD schema at hand
   * @param currDiceVal the current dice value as a String
   * @param currLevelURI the URI of the dice level
   * 
   * @return the previous dice value in the current dice level as a String.
   * 
   * @throws Exception
   *
   */
  public String getPreviousDiceValue(MDSchema mdSchema, String currDiceVal, String currLevelURI)
      throws Exception;

  /**
   * 
   * Gets the possible values for a specific MD element.
   * 
   * @param mdSchema the MD schema at hand
   * @param itemURI the URI of the MD item to get possible values for
   * 
   * @return a list of String representing the possible values
   * 
   * @throws Exception
   * 
   */
  public List<String> getMDItemPossibleValues(MDSchema mdSchema, String itemURI) throws Exception;

  /**
   * 
   * Gets the possible values pairs (label, actual value) for a specific MD element.
   * 
   * @param mdSchema the MD schema at hand
   * @param itemURI the URI of the MD item to get possible value pairs for
   * 
   * @return a map of String pairs representing the possible values
   * 
   * @throws Exception
   */
  public Map<String, String> getMDItemPossibleValuesPairs(MDSchema mdSchema, String itemURI)
      throws Exception;
}
