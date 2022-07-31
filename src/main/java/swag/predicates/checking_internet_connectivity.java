package swag.predicates;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;

import swag.data_handler.connection_to_rdf.exceptions.RemoteSPARQLQueryExecutionException;

public class checking_internet_connectivity {
  public static void main(String args[]) throws Exception {

    // System.setProperty("http.proxyHost", "140.78.58.10");
    // System.setProperty("http.proxyPort", "3128");
    // System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");

    // HTTPS

    org.apache.jena.query.Query query = QueryFactory.create("select * where {?x ?y ?z} limit 100");

    try {
      QueryExecution qexec =
          QueryExecutionFactory.sparqlService("https://query.wikidata.org/sparql", query);
      ResultSet results = qexec.execSelect();

    } catch (Exception ex) {
      System.err.println(ex);
      throw new RemoteSPARQLQueryExecutionException(
          "Could not execute remote query: " + query.toString(), ex);
    }

    Process process = java.lang.Runtime.getRuntime().exec("ping www.geeksforgeeks.org");
    int x = process.waitFor();
    if (x == 0) {
      System.out.println("Connection Successful, " + "Output was " + x);
    } else {
      System.out.println("Internet Not Connected, " + "Output was " + x);
    }
  }
}
