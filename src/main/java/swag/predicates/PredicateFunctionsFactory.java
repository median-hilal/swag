package swag.predicates;

import org.apache.jena.rdf.model.Model;

import swag.data_handler.connection_to_rdf.SPARQLEndpointConnection;

public class PredicateFunctionsFactory {

  public static HybridPredicateFunctions createHybridPredicateFunctions(IPredicateGraph graph,
      Model model, SPARQLEndpointConnection conn) {

    FileBasedPredicateFunction fileBased = new FileBasedPredicateFunction(graph, model, conn);
    ObjectBasedPredicateFunctinos objectBased = new ObjectBasedPredicateFunctinos(graph);
    return new HybridPredicateFunctions(graph, fileBased, objectBased);
  }

  public static ObjectBasedPredicateFunctinos createObjectBasedPredicateFunctions(
      IPredicateGraph graph) {
    return new ObjectBasedPredicateFunctinos(graph);
  }

}
