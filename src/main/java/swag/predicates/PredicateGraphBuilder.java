package swag.predicates;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import swag.data_handler.connection_to_rdf.SPARQLEndpointConnection;
import swag.md_elements.MDSchema;

public class PredicateGraphBuilder implements IPredicateGraphBuilder {

  private MDSchema schema;

  public MDSchema getSchema() {
    return schema;
  }

  public void setSchema(MDSchema schema) {
    this.schema = schema;
  }

  public PredicateGraphBuilder(MDSchema schema) {
    super();
    this.schema = schema;
  }

  public static Model read(String pathToPredicatesFile) {
    Model m = ModelFactory.createOntologyModel();
    m.read(pathToPredicatesFile, "Turtle");
    System.out.println(QUERY_STRINGS.PREDICATE_INSTANCES);
    Query a = QueryFactory.create(QUERY_STRINGS.PREDICATE_INSTANCES);
    QueryExecution qe = QueryExecutionFactory.create(a, m);
    ResultSet res = qe.execSelect();
    return m;
  }

  @Override
  public IPredicateGraph buildPredicatesGraphWitoutRemoteAccess(String pathToPredicatesFile) {

    Model model = read(pathToPredicatesFile);
    SPARQLEndpointConnection conn = new SPARQLEndpointConnection();
    IPredicateGraph graph = new PredicateGraph();

    IPredicateFunctions funcs =
        PredicateFunctionsFactory.createHybridPredicateFunctions(graph, model, conn);

    for (Predicate pred : funcs.getAllPredicates()) {
      funcs.getAllPredicateInstances(pred.getURI());
    }

    for (LiteralCondition cond : funcs.getAllConditions(schema)) {
      graph.addNode(cond);
    }

    return graph;
  }
}
