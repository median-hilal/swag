package swag.predicates;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import swag.data_handler.connection_to_rdf.SPARQLEndpointConnection;

public class Main {

  public static Model read() {
    Model m = ModelFactory.createOntologyModel();
    m.read(convertPathToURI("C:\\Data", "predicates.ttl"), "Turtle");

    System.out.println(QUERY_STRINGS.PREDICATE_INSTANCES);
    Query a = QueryFactory.create(QUERY_STRINGS.PREDICATE_INSTANCES);

    QueryExecution qe = QueryExecutionFactory.create(a, m);

    ResultSet res = qe.execSelect();

    System.out.println("before");

    for (; res.hasNext();) {
      QuerySolution soln = res.next();
      System.out.println("innnnnnnnnnnn");
      System.out.println(soln.toString());
    }

    return m;
  }

  public static void main(String[] args) {
    Model model = read();
    SPARQLEndpointConnection conn = new SPARQLEndpointConnection();
    IPredicateGraph graph = new PredicateGraph();

    IPredicateFunctions funcs =
        PredicateFunctionsFactory.createHybridPredicateFunctions(graph, model, conn);

    List<Predicate> preds = funcs.getAllPredicates();


    for (Predicate pred : preds) {
      List<PredicateInstance> instances = new ArrayList<>();
      System.out.println("pred instances got 1" + pred.toString());
      instances = funcs.getAllPredicateInstances(pred.getURI());
      for (PredicateInstance ins : instances) {
        System.out.println("Instance: " + ins.toString());
      }
    }

    List<Predicate> preds1 = funcs.getAllPredicates();

    for (Predicate pred : preds1) {
      List<PredicateInstance> instances = new ArrayList<>();
      System.out.println("pred instances got 2" + pred.toString());
      instances = funcs.getAllPredicateInstances(pred.getURI());
      for (PredicateInstance ins : instances) {
        System.out.println("Instance: " + ins.toString());
      }
    }

    for (Predicate pred : preds1) {
      List<PredicateInstance> instances = new ArrayList<>();
      System.out.println("pred instances got 3" + pred.toString());
      instances = funcs.generateAllSubjectPredicateInstances(pred.getURI());
      for (PredicateInstance ins : instances) {
        System.out.println("Instance: " + ins.toString());

        System.out.println(funcs.generatePredicateInstanceQuery(ins.getUri()));

      }
    }

  }

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

}
