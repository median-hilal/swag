package swag.sparql_builder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;


public class Test {

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

  public static void main(String[] atgs) {


    /*
     * System.getProperties().put("proxySet", "true"); System.getProperties().put("http.proxyHost",
     * "140.78.58.10"); System.getProperties().put("http.proxyPort", "3128");
     */

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
      
      String queryString = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
          + " select ?country1 (group_concat(?film) as ?grp_film) where { { select ?film  (IF ( group_concat(distinct ?country; separator=\"--\") != \"\","
          + "   group_concat(distinct ?country ;"
          + " separator=\"--\"),  \"OtherCountry\"  ) as ?country1 ) " + "where"
          + " { {select ?film ?country where{ ?film   <http://www.wikidata.org/prop/direct/P2047>   ?duration . "
          + "?film  <http://www.wikidata.org/prop/direct/P31>  <http://www.wikidata.org/entity/Q11424> . "
          + "?film     <http://www.wikidata.org/prop/direct/P495>  ?country . ?film  <http://www.wikidata.org/prop/direct/P2047>  ?duration."
          + "} order by asc(xsd:integer(strafter (str(?country), \"entity/Q\")))  } }group by ?film } union {select ?film ?country where {?film ?y ?country.}} }  group by ?country1 order by ?country1";

      Model m = ModelFactory.createOntologyModel();


      m.read(convertPathToURI("C:\\Data", "file.ttl"), "Turtle");
      
      String queryStrNew = "  " + " construct {\r\n" + 
          "    ?x <http://www.example.com/hasmeasure2> ?r.\r\n" + 
          "    ?x <http://www.example.com/hasdirector> ?z.\r\n" + 
          "} where {" + "{select * where{?x <http://www.example.com/hasdirector> ?z.}}" + ""
          + "{select * where {?x <http://www.example.com/hasmeasure2> ?r.}} " + "}";
      
      
      String queryStrNew1 = "  " + "select distinct ?x where {" + "{select * where{?x <http://www.example.com/hasdirector> ?z.}}" + ""
          + "{select * where {?x <http://www.example.com/hasmeasure2> ?r.} } " + " }";
      
      String queryStrNew2 = "  " + " construct {\r\n" + 
          "    ?x <http://www.example.com/hasdirector> ?z .\r\n" + 
          "    ?x <http://www.example.com/hasmeasure2> ?r .\r\n" + 
          "?x <http://www.example.com/hasadditional1> ?a. "       +
          
          "} where {" 
              + "{"
                + "{select * where{?x <http://www.example.com/hasdirector> ?z.}}" + ""
                + "{select * where {?x <http://www.example.com/hasmeasure2> ?r.}}"
              + "} " 
            + " UNION "
            + " {"
             +      "?x <http://www.example.com/hasadditional1> ?a."     
             + "}"
          + "}";

      String queryStr1 = "  " + " select * where {" + "{?x <http://www.example.com/pp1> ?z.}" + ""
          + "{optional {?w <http://www.example.com/pSS> ?r. FILTER (BOUND (?z))}} " + "}";
      
      String qureyString2 = "SELECT   ?film (avg(?duration) as ?theAverage) (avg(?duration) as ?theAverage2) \r\n" + 
          "WHERE\r\n" + 
          "{         \r\n" + 
          "    {\r\n" + 
          "      select ?film #?duration\r\n" + 
          "      where\r\n" + 
          "         {?film   ?y1  ?z.}\r\n" + 
          "    }\r\n" + 
          "\r\n" + 
          "    {\r\n" + 
          "      select ?film ?duration     \r\n" + 
          "      where\r\n" + 
          "         {?film  ?y2  ?duration .}\r\n" + 
          "    }      \r\n" + 
          "}\r\n" + 
          " group by ?film" +
          "#limit 1000";
      Query query2 = QueryFactory.create(queryStrNew2);
      
      String qureyString3 = "SELECT   ?film ?duration\r\n" + 
          "WHERE\r\n" + 
          "{         \r\n" + 
          "    {\r\n" + 
          "      select ?film ?duration\r\n" + 
          "      where\r\n" + 
          "         {?film   ?y1  ?z.}\r\n" + 
          "    }\r\n" + 
          "\r\n" + 
          "    {\r\n" + 
          "      select ?film ?duration     \r\n" + 
          "      where\r\n" + 
          "         {?film  ?y2  ?duration .}\r\n" + 
          "    }      \r\n" + 
          "}\r\n" + 
          "#limit 1000";
      Query query3 = QueryFactory.create(qureyString3);
      
      QueryExecution qe2 = QueryExecutionFactory.create(query2, m);

      Model mConstruct = qe2.execConstruct();
      
      FileWriter fw = null;
      try {
        fw = new FileWriter("C:\\Data\\file1.ttl");
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      mConstruct.write(fw, "Turtle");
      
      System.out.println(mConstruct.getGraph());

      System.out.println("=================================2222222222");

      /*
      for (; res2.hasNext();) {
        QuerySolution soln = res2.next();
        System.out.println("22222222222  ");
        System.out.println(soln.toString());
      }
      
      QueryExecution qe3 = QueryExecutionFactory.create(query2, m);

      ResultSet res3 = qe3.execSelect();

      System.out.println("=================================33333333333333333");

      for (; res3.hasNext();) {
        QuerySolution soln = res3.next();
        System.out.println("33333333333  ");
        System.out.println(soln.toString());
      }

      if (2>1) return;
      */

      // "select *\r\n" + " where{\r\n" + " ?a <http://www.example.com/pp> ?c.\r\n"
      // + " optional {?c <http://www.example.com/SSS> ?z}}";


      Query a = QueryFactory.create(queryStrNew1);

      
      String str1 =
          "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + " select ?x  ?y where {\r\n"
              + "          {?x rdf:type <http://www.example.com/Recommendable>}        \r\n"
              + "          {?x <http://www.example.com/doesntexist> ?y}\r\n" + "        }";

      String str2 =
          "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + " select ?x  ?y where {\r\n"
              + "      ?x rdf:type  <http://www.example.com/Recommendable>.\r\n"
              + "      ?x <http://www.example.com/doesntexist> ?y.\r\n" + "    }";
      
      String str3 =
          "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + " select ?x  ?y where {\r\n"
              + "      ?x rdf:type  <http://www.example.com/Recommendable>.\r\n"
              + "  {select distinct ?y where{    ?x <http://www.example.com/doesntexist> ?y.}} \r\n" + "    }";
      
      String str4 =
          "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + " select ?x  ?y where {\r\n"
              + "      {?x1 rdf:type  <http://www.example.com/Recommendable>.}\r\n"
              + "      {?x2 rdf:type  <http://www.example.com/Recommendable>.}\r\n"
              + "  optional {  ?x <http://www.example.com/doesntexist> ?y3.} \r\n" + "    "
              + "      {?x4 rdf:type  <http://www.example.com/Recommendable>.}\r\n"
              + "  optional {  ?x <http://www.example.com/doesntexist> ?y5.} \r\n" + "    "
                  + "}";

      String str5 = "PREFIX books: <http://example.org/book/> "
          + "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
          + "SELECT ?book ?title WHERE "
          + "{ ?book dc:title ?title optional {{?book dc:title ?title filter (1=1)}}"
          + " { SELECT ?book ?title WHERE { ?book dc:title ?title } } "
          + "}";
      
      Op op1 = Algebra.compile(QueryFactory.create(str1));
      System.out.println("First:" + op1.toString());

      Op op2 = Algebra.compile(QueryFactory.create(str2));
      System.out.println("second:" + op2.toString());
      
      Op op3 = Algebra.compile(QueryFactory.create(str3));
      System.out.println("third:" + op3.toString());

      
      Op op4 = Algebra.compile(QueryFactory.create(str4));
      System.out.println("fourth:" + op4.toString());
      
      
  

      System.out.println("syntax: " + a.getSyntax());
      
      Op op5 = Algebra.compile(QueryFactory.create(qureyString2));
      System.out.println("fourth:" + op5.toString());
      
      System.out.println(qureyString2);
      
      Op op6 = Algebra.compile(QueryFactory.create(str5));
      System.out.println("fourth:" + op6.toString());
      
      System.out.println("str5:" + str5);

      //if (2>1)
       // return;
      
      QueryExecution qe = QueryExecutionFactory.create(a, m);

      ResultSet res = qe.execSelect();

      System.out.println("before");

      for (; res.hasNext();) {
        QuerySolution soln = res.next();
        System.out.println("innnnnnnnnnnn");
        System.out.println(soln.toString());

      }

      //Op op = Algebra.compile(a);
      //System.out.println(op.toString());
      /*
       * Query query = QueryFactory.create(queryString);
       * 
       * CustomSPARQLQuery cust = new CustomSPARQLQuery(query);
       * 
       * cust.removeQueryPatternDuplications();
       * 
       * System.out.println(queryString);
       * 
       * QueryExecution qexec = QueryExecutionFactory
       * .sparqlService("http://140.78.58.71:7202/repositories/Wikidata_films", query); ResultSet
       * results = qexec.execSelect(); // ResultSetFormatter.out(System.out, results, query);
       * 
       * 
       * List<String> res = new ArrayList<>();
       * 
       * List<List<Long>> longs = new ArrayList<>();
       * 
       * long counter = 0; long nosCounter = 0; for (; results.hasNext();) {
       * 
       * if (counter < 2000) { counter++; } else { break; } System.out.println("counter: " + counter);
       * 
       * System.out.println("in"); QuerySolution soln = results.next(); Literal x =
       * soln.getLiteral("country1"); String str = x.toString();
       * System.out.println("Whole string is: " + str); String[] strs = str.split("--"); List<String>
       * row = new ArrayList<>(); List<Long> rowLongs = new ArrayList<>(); for (String str1 : strs) {
       * row.add(str1);
       * 
       * System.out.println("part string is: " + str1); String tmpStr = str1.split("entity/Q")[1];
       * rowLongs.add(Long.parseLong(tmpStr)); System.out.println("Added long: " +
       * Long.parseLong(tmpStr)); } row.sort(Comparator.comparing(String::toString)); String
       * sortedConcatenatedRow = "";
       * 
       * 
       * 
       * 
       * rowLongs.sort(null);
       * 
       * for (Long lon : rowLongs) { System.out.println("here is a long: " + lon); }
       * 
       * if (!checkIfListContainsLongArray(longs, rowLongs)) { System.out.println("AAAAAAA");
       * longs.add(rowLongs); } else { nosCounter++; System.out.println("nosCounter" + nosCounter);
       * System.out.println("NNNNNN"); } }
       */
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
