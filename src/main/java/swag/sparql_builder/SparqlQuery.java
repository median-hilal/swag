package swag.sparql_builder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;



public class SparqlQuery {

  /**
   * @param path
   * @param fileName
   * @return
   */
  public static String convertPathToURI(String path, String fileName) {
    String res = "";
    try {
      Path input = Paths.get(path, fileName);
      res = input.toUri().toString();
    } catch (Exception ex) {
      System.out.println(ex);
    }
    return res;
  }

  private static boolean netIsAvailable() {
    try {
      final URL url = new URL("http://www.googlew.com");
      final URLConnection conn = url.openConnection();
      conn.connect();
      conn.getInputStream().close();
      return true;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      return false;
    }
  }

  public static void main(String[] atgs) {



    System.getProperties().put("proxySet", "true");
    System.getProperties().put("http.proxyHost", "140.78.58.10");
    System.getProperties().put("http.proxyPort", "3128");


    System.out.println(netIsAvailable());



    /*
     * String queryString = "" + "select ?country1" + " where {" + " {" + " select ?film" + " (IF "
     * + " (" + "   group_concat(distinct ?country; separator=\"--\") != \"\", " +
     * "  group_concat(distinct ?country ; separator=\"--\"),  " + " \"OtherCountry\"          " +
     * " ) " + " as ?country1 " + " ) " + " where { {select ?film ?country where { " +
     * "  ?film   <http://www.wikidata.org/prop/direct/P2047>   ?duration . " +
     * " ?film  <http://www.wikidata.org/prop/direct/P31>  <http://www.wikidata.org/entity/Q11424> . "
     * + " ?film     <http://www.wikidata.org/prop/direct/P495>  ?country . " +
     * " } order by ?film ?country } }group by ?film" +
     * " } } group by ?country1 order by ?country1";
     */
    //@formatter:off
    
    String queryString = "  SELECT   \r\n" + 
        "distinct\r\n" + 
        "?film ?genre ?country\r\n" + 
        "      WHERE\r\n" + 
        "        {  \r\n" + 
        "           ?film   <http://www.wikidata.org/prop/direct/P31>  <http://www.wikidata.org/entity/Q11424>.\r\n" + 
        "          ?film <http://www.wikidata.org/prop/direct/P136>  ?genre.\r\n" + 
        "           ?film  <http://www.wikidata.org/prop/direct/P495>  ?country . \r\n" + 
        "        }";

    
    String queryString2 = "  SELECT   \r\n" + 
        "distinct\r\n" + 
        "?film ?genre ?country\r\n" + 
        "      WHERE\r\n" + 
        "        {  \r\n" + 
        "          {select distinct ?film where\r\n" + 
        "         {\r\n" + 
        "           ?film   <http://www.wikidata.org/prop/direct/P31>  <http://www.wikidata.org/entity/Q11424>.\r\n" + 
        "         \r\n" + 
        "         }\r\n" + 
        "          }\r\n" + 
        "          \r\n" + 
        "          {select distinct ?film ?genre where{\r\n" + 
        "          ?film <http://www.wikidata.org/prop/direct/P136>  ?genre.\r\n" + 
        "          }\r\n" + 
        "           }\r\n" + 
        "         {\r\n" + 
        "           select distinct ?film ?country where{\r\n" + 
        "           ?film  <http://www.wikidata.org/prop/direct/P495>  ?country .\r\n" + 
        "           }\r\n" + 
        "          }\r\n" + 
        "       \r\n" + 
        "        }";
    


      Query query = QueryFactory.create(queryString);
      
      
      System.out.println(queryString);
      
      QueryExecution qexec = QueryExecutionFactory
      .sparqlService("https://query.wikidata.org/sparql", query);
      
      ResultSet  results = qexec.execSelect(); // ResultSetFormatter.out(System.out, results, query);
      
Query query2 = QueryFactory.create(queryString2);
      
      
      System.out.println(queryString2);
      
      QueryExecution qexec2 = QueryExecutionFactory
      .sparqlService("https://query.wikidata.org/sparql", query2);
      
      ResultSet  results2 = qexec2.execSelect(); // ResultSetFormatter.out(System.out, results, query);
     
      
      List<String> res = new ArrayList<>();
      List<String> res2 = new ArrayList<>();
      
      List<List<Long>> longs = new ArrayList<>();
      
      long counter = 0; 
      long nosCounter = 0; 
      
      for (; results.hasNext();) {
      
        if (counter < 2000000) { counter++; } else { break; } System.out.println("counter: " + counter);
        
        System.out.println("in"); QuerySolution soln = results.next(); 
        
        String film = soln.get("film").toString();
        String genre = soln.get("genre").toString();
        String country = soln.get("country").toString();
        
  
        //System.out.println("Whole string is: " + film + "--" + genre + "--" + country); 
        res.add(film + "--" + genre + "--" + country);
      }
      
      counter = 0; 
      for (; results2.hasNext();) {
        
        if (counter < 2000000) { counter++; } else { break; } 
        
        System.out.println("counter: " + counter);
        
        System.out.println("in"); QuerySolution soln = results2.next(); 
        
        String film = soln.get("film").toString();
        String genre = soln.get("genre").toString();
        String country = soln.get("country").toString();
        
  
        //System.out.println("Whole string is: " + film + "--" + genre + "--" + country); 
        res2.add(film + "--" + genre + "--" + country);
      }
      
      System.out.println("checking the two result sets");
      
      Set <String> set = new HashSet<>(res);
      System.out.println("size of first list/set is:" + res.size() + "/" + set.size());
      
      Set <String> set2 = new HashSet<>(res2);
      System.out.println("size of second list/set is:" + res2.size() + "/" + set2.size());
      
      if (set.size() > set2.size()) {
        set.removeAll(set2);
        for (String str : set) {
          System.out.println("remaining: " + str);
        }
      }else {
        set2.removeAll(set);
        for (String str : set2) {
          System.out.println("remaining2: " + str);
        }
      }
      
      
     
      
     
  }

  public static boolean checkIfListContainsLongArray(List<List<Long>> allLongArr,
      List<Long> toAdd) {

    if (allLongArr.size() == 0) {
      return false;
    }

    for (List<Long> temp : allLongArr) {
      if (compareLongArrays(temp, toAdd)) {
        return true;
      }
    }
    return false;
  }

  public static boolean compareLongArrays(List<Long> first, List<Long> second) {
    first.sort(null);
    second.sort(null);
    if (first.size() != second.size()) {
      return false;
    } else {
      for (int i = 0; i < first.size(); i++) {
        if (!first.get(i).equals(second.get(i))) {
          return false;
        }
      }
    }

    System.out.println(" ---------- printing duplication -------");
    for (Long lon : first) {
      System.out.println("here is in first: " + lon);
    }
    for (Long lon : second) {
      System.out.println("here is a second: " + lon);
    }
    System.out.println(" ---------------------------------------");
    return true;
  }

}


