package swag.data_handler.connection_to_rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.jena.query.Query;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import swag.analysis_graphs.execution_engine.NoValueFoundException;
import swag.data_handler.connection_to_rdf.exceptions.RemoteSPARQLQueryExecutionException;
import swag.web.ServletPresentationHelper;

public class DataRetriever {

  private static final org.apache.log4j.Logger logger =
      org.apache.log4j.Logger.getLogger(DataRetriever.class);

  /**
   * returns as a String the first value for the first head variable of the query
   * 
   * @param con a connection to SPARQL Endpoint
   * @param q the query to be sent
   * @return the first result as a string
   * @throws NoValueFoundException when the query gets no result
   * @throws RemoteSPARQLQueryExecutionException
   */
  public static String getFirstQueryResultAsString(SPARQLEndpointConnection con, Query q)
      throws NoValueFoundException, RemoteSPARQLQueryExecutionException {
    String str = "";
    try {
      ResultSet res = con.sendQueryToEndpointAndGetResults(q);
      if (res.hasNext()) {
        QuerySolution s = res.next();
        try {
          str = s.get(q.getProjectVars().get(0).toString()).toString();
        } catch (Exception ex) {
          logger.error("Failed to get first result string.", ex);
        }
      } else {
        NoValueFoundException opEx =
            new NoValueFoundException("Found no results in query: " + q.toString());
        throw opEx;
      }
    } catch (NoValueFoundException ex) {
      logger.error("No value found!", ex);
      ex.printStackTrace();
    } catch (RemoteSPARQLQueryExecutionException ex) {
      logger.error("No value found!", ex);
      throw (ex);
    }
    return str;
  }

  /**
   * returns as a List of String the values for the first head variable of the query
   * 
   * @param con a connection to SPARQL Endpoint
   * @param q the query to be sent
   * @return a list of String of the the results for the first head variable of the query
   * @throws NoValueFoundException when the query gets no results
   * @throws RemoteSPARQLQueryExecutionException
   */
  public static List<String> getQueryResultsAsStringList(SPARQLEndpointConnection con, Query q)
      throws NoValueFoundException, RemoteSPARQLQueryExecutionException {

    List<String> results = new ArrayList<String>();
    try {
      ResultSet res = con.sendQueryToEndpointAndGetResults(q);
      if (!res.hasNext()) {
        NoValueFoundException opEx =
            new NoValueFoundException("getQueryResultsAsStringList: " + q.toString());
        throw opEx;
      }
      while (res.hasNext()) {
        QuerySolution s = res.next();
        try {
          results.add(s.get(q.getProjectVars().get(0).toString()).toString());
        } catch (Exception ex) {
          logger.error("Failed to get first result string!", ex);
        }
      }
    } catch (NoValueFoundException ex) {
      logger.error("No value found!", ex);
      throw ex;
    } catch (RemoteSPARQLQueryExecutionException ex) {
      logger.error("Cannot connect to the SPARQL endpoint!", ex);
      throw (ex);
    }
    return results;
  }

  /**
   * returns as a map of the values and the labels for the first and second head variables of the
   * query (item and its label)
   * 
   * @param con a connection to SPARQL Endpoint
   * @param q the query to be sent
   * @return a list of String of the the results for the first head variable of the query
   * @throws NoValueFoundException when the query gets no results
   * @throws RemoteSPARQLQueryExecutionException
   */
  public static Map<String, String> getLabeledQueryResults(SPARQLEndpointConnection con, Query q)
      throws NoValueFoundException, RemoteSPARQLQueryExecutionException {

    Map<String, String> results = new HashMap<String, String>();
    try {
      ResultSet res = con.sendQueryToEndpointAndGetResults(q);
      if (!res.hasNext()) {
        NoValueFoundException opEx =
            new NoValueFoundException("getQueryResultsAsStringList: " + q.toString());
        throw opEx;
      }
      while (res.hasNext()) {
        QuerySolution s = res.next();
        try {
          results.put(s.get(q.getProjectVars().get(0).toString()).toString(),
              ServletPresentationHelper
                  .removeLabelSuffix(s.get(q.getProjectVars().get(1).toString()).toString()));
        } catch (Exception ex) {
          System.err.println(ex);
          logger.error("Exception occurred while trying to retrieve the values!", ex);
          ex.printStackTrace();
        }
      }
    } catch (NoValueFoundException ex) {
      logger.error("no value found!", ex);
      throw ex;
    } catch (RemoteSPARQLQueryExecutionException ex) {
      throw (ex);
    }
    return results;
  }
}
