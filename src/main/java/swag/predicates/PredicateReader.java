package swag.predicates;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;

import swag.data_handler.connection_to_rdf.SPARQLEndpointConnection;
import swag.data_handler.connection_to_rdf.exceptions.RemoteSPARQLQueryExecutionException;

public class PredicateReader {


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
    Model m = read();
    ResultSet res = queryPredicates(m);
    List<Predicate> predicates = buildPredicate(res);

    Predicate pr = null;
    for (Predicate p : predicates) {
      pr = p;
      break;
    }

    List<PredicateInputVar> inputVars = new ArrayList<PredicateInputVar>();

    SPARQLEndpointConnection conn = new SPARQLEndpointConnection();
    // generatePredicateInstanceQuerz("http://www.wikidata.org/entity/Q178810", inputVars,
    // "http://www.amcis2021.com/swag/pr#predInstance1", m, conn);
  }

  public static ResultSet queryPredicates(Model m) {

    String queryString = QUERY_STRINGS.PREDICATES;

    Query query = QueryFactory.create(queryString);
    QueryExecution exec = QueryExecutionFactory.create(query, m);

    ResultSet res = exec.execSelect();
    return res;
  }

  public static List<Predicate> buildPredicate(ResultSet res) {

    List<Predicate> predicates = new ArrayList<>();
    Predicate pred = new Predicate();
    boolean firstTime = true;

    String predicateURI = "";
    while (res.hasNext()) {
      QuerySolution sol = res.next();

      if (!predicateURI.equals(sol.get("predicate").toString())) {

        if (!firstTime) {
          predicates.add(pred);
        }
        firstTime = false;

        pred = new Predicate();
        predicateURI = sol.get("predicate").toString();

        pred.setURI(predicateURI);

        /* varName variable added after refactoring - not tetsted */
        String outputvar = sol.get("outputVar") != null ? sol.get("outputVar").toString() : null;
        String outputvarType =
            sol.get("outputVarType") != null ? sol.get("outputVarType").toString() : null;
        String outputvarName =
            sol.get("outputVarName") != null ? sol.get("outputVarName").toString() : null;


        if (outputvar != null) {
          PredicateOutputVar var =
              new PredicateOutputVar(outputvarType != null ? outputvarType.toString() : "",
                  outputvar.toString(), outputvarName);
          pred.addTooUTputVars(var);
        }

        /* varName variable added after refactoring - not tetsted */
        String inputvar = sol.get("inputVar") != null ? sol.get("inputVar").toString() : null;
        String inputvarType =
            sol.get("inputVarType") != null ? sol.get("inputVarType").toString() : null;
        String inputvarName =
            sol.get("inputVarName") != null ? sol.get("inputVarName").toString() : null;


        if (inputvar != null) {
          PredicateInputVar var = new PredicateInputVar(inputvarType, inputvar, inputvarName);
          pred.addToInputVars(var);
        }

        /* varName variable added after refactoring - not tetsted */
        String subjectvar = sol.get("subjectVar") != null ? sol.get("subjectVar").toString() : null;
        String subjectVarType =
            sol.get("subjectVarType") != null ? sol.get("subjectVarType").toString() : null;
        String subjectvarName =
            sol.get("subjectVarName") != null ? sol.get("subjectVarName").toString() : null;

        if (subjectvar != null && pred.getSubjectVar() == null) {
          PredicateOutputVar var =
              new PredicateOutputVar(subjectVarType, subjectvar, subjectvarName);
          pred.setSubjectVar(var);
        }

        /* varName variable added after refactoring - not tetsted */
        String descriptionvar =
            sol.get("descriptionVar") != null ? sol.get("descriptionVar").toString() : null;
        String descriptionvarType =
            sol.get("descriptionVarType") != null ? sol.get("descriptionVarType").toString() : null;

        String descriptionVarName =
            sol.get("descriptionVarName") != null ? sol.get("descriptionVarName").toString() : null;

        if (descriptionvar != null && pred.getDescriptionVar() == null) {
          PredicateOutputVar var =
              new PredicateOutputVar(descriptionvarType, descriptionvar, descriptionVarName);
          pred.setDescriptionVar(var);
        }

        String query = sol.get("query") != null ? sol.get("query").toString() : null;
        if (query != null && pred.getQuery() == null) {
          pred.setQuery(new swag.predicates.Query(query));
        }

        String topic = sol.get("topic") != null ? sol.get("topic").toString() : null;
        if (topic != null) {
          pred.addToTopics(topic);
        }
      }
    }

    return predicates;
  }

  /**
   * Untested.
   * 
   * @param predicateURI
   * @param model
   * @param con
   * @return
   */
  public static List<PredicateInstance> getAllPotentialPredicateInstances(String predicateURI,
      Model model, SPARQLEndpointConnection con) {

    List<PredicateInstance> predicateInstances = new ArrayList<>();

    String queryString = QUERY_STRINGS.PREDICATE_BY_NAME;
    queryString = queryString.replaceAll("\\?xXxPredicatexXx", predicateURI);

    Query query = QueryFactory.create(queryString);
    QueryExecution exec = QueryExecutionFactory.create(query, model);
    ResultSet res = exec.execSelect();

    Predicate pred = buildPredicate(res).get(0);

    ResultSet res1 = null;
    Query query1 = QueryFactory.create(pred.getQuery().getQuery());
    try {
      res1 = con.sendQueryToEndpointAndGetResults(query1);
    } catch (RemoteSPARQLQueryExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    List<String> subjects = getSubjectsFromResultSet(res,
        pred.getSubjectVar().getVariable()/* Changed after refactoring without testing */);


    return predicateInstances;

  }

  public static List<PredicateInstance> createPredicateInstancesOnSubjectVar(Predicate pred,
      List<String> subjects) {

    List<PredicateInstance> instances = new ArrayList<>();

    for (String sub : subjects) {
      VariableBinding binding = new VariableBinding(pred.getSubjectVar(), sub);
      List<VariableBinding> bindings = new ArrayList<>();
      bindings.add(binding);
      PredicateInstance instance = new PredicateInstance(pred, bindings);
      instances.add(instance);
    }

    return instances;
  }

  public static List<String> getSubjectsFromResultSet(ResultSet res, String subjectVarName) {

    List<String> subjects = new ArrayList<>();

    while (res.hasNext()) {
      QuerySolution sol = res.next();
      subjects.add(Utils.getStringValueIfNotNull(sol.get(subjectVarName)));
    }

    subjects.stream().filter(x -> x != null).collect(Collectors.toList());
    return subjects;

  }

  public List<String> getAllPredicateTopics(Predicate pred) {

    return new ArrayList<String>();
  }


  public static Query instantiatePredicateInstanceByURI(String predicateInstanceURI,
      List<VariableSelection> inputVars, Model m) {

    String queryString = QUERY_STRINGS.PREDICATE_INSTANCE_BY_NAME;
    queryString = queryString.replaceAll("\\?xXxPredicateInstancexXx", predicateInstanceURI);

    Query query = QueryFactory.create(queryString);
    QueryExecution exec = QueryExecutionFactory.create(query, m);

    ResultSet res = exec.execSelect();
    String predicatInstanceQuery = "";

    RDFNode subjectVar = null;

    while (res.hasNext()) {
      QuerySolution sol = res.next();
      predicatInstanceQuery = Utils.getStringValueIfNotNull(sol.get("query"));
      break;
    }

    return instantiatePredicateInstanceQuery(inputVars, QueryFactory.create(predicatInstanceQuery));
  }

  public static Query instantiatePredicateInstanceQuery(List<VariableSelection> inputVars,
      Query query) {

    String predicatInstanceQuery = "";

    for (VariableSelection sel : inputVars) {
      predicatInstanceQuery = predicatInstanceQuery.replaceFirst("\\{",
          "{ FILTER (" + sel.getValue() + " = " + "?" + sel.getVar().getVariable() + ")");
    }


    return QueryFactory.create(predicatInstanceQuery);
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
