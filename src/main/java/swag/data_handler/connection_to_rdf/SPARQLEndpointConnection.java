package swag.data_handler.connection_to_rdf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;

import swag.data_handler.connection_to_rdf.exceptions.RemoteSPARQLQueryExecutionException;

public class SPARQLEndpointConnection {

  private static final org.apache.log4j.Logger logger =
      org.apache.log4j.Logger.getLogger(SPARQLEndpointConnection.class);

  
    private static final Map<String, String> defaultPropMap; static {
    
    Map<String, String> aMap = new HashMap<>();
    aMap.put("proxySet", "true");
    aMap.put("http.proxyHost", "140.78.58.10"); aMap.put("http.proxyPort", "3128"); defaultPropMap
    = Collections.unmodifiableMap(aMap); } private static final String defaultsparqlEndpointURI =
    "https://query.wikidata.org/sparql";
   
  private String sparqlEndpointURI = "https://query.wikidata.org/sparql";

  
    private Map<String, String> props;
    
    public Map<String, String> getProps() { return props; }
    
    public void setProps(Map<String, String> props) { this.props = props; }
   

  public String getSparqlEndpointURI() {
    return sparqlEndpointURI;
  }

  public void setSparqlEndpointURI(String sparqlEndpointURI) {
    this.sparqlEndpointURI = sparqlEndpointURI;
  }

  /**
   * Default constructor with default settings.
   */
  public SPARQLEndpointConnection() {
    super();
     this.sparqlEndpointURI = defaultsparqlEndpointURI;
     this.props = defaultPropMap;
     setSystemProperties();
  }

  
    public SPARQLEndpointConnection(String sparqlEndpointURI) { super(); this.sparqlEndpointURI =
    sparqlEndpointURI; this.props = defaultPropMap; setSystemProperties(); }
    
    public SPARQLEndpointConnection(String sparqlEndpointURI, Map<String, String> props) { super();
    this.sparqlEndpointURI = sparqlEndpointURI; this.props = props; setSystemProperties(); }
    
    public void setSystemProperties() { for (Map.Entry<String, String> entry : props.entrySet()) {
    System.getProperties().remove(entry.getKey()); System.getProperties().put(entry.getKey(),
    entry.getValue()); } }
   

  /**
   * 
   * Executes a SPARQL query against a provided sparqlEndpointURI in the creation of the class and
   * retrieves the results.
   * 
   * @param query a SPARQL query to execute
   * 
   * @return result set of the query
   * 
   * @throws RemoteSPARQLQueryExecutionException when any error occurs during execution
   * 
   */
  public ResultSet sendQueryToEndpointAndGetResults(Query query)
      throws RemoteSPARQLQueryExecutionException {
    try {
      QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpointURI, query);
      ResultSet results = qexec.execSelect();
      return results;
    } catch (Exception ex) {
      System.err.println(ex);
      logger.error("Could not execute remote query: " + query.toString(), ex);
      throw new RemoteSPARQLQueryExecutionException(
          "Could not execute remote query: " + query.toString(), ex);
    }
  }


}
