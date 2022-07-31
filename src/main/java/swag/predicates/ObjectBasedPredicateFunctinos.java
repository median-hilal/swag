package swag.predicates;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.jena.query.Query;

import swag.md_elements.MDSchema;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLUtilities;

public class ObjectBasedPredicateFunctinos implements IPredicateFunctions {

  private FileBasedPredicateFunction filePredicatesFunctions;
  private IPredicateGraph graph;

  public ObjectBasedPredicateFunctinos(IPredicateGraph graph) {
    super();
    this.graph = graph;
  }

  @Override
  public List<Predicate> getAllPredicates() {
    if (graph.getNode(PredicateClass.createInstance().getIdentifyingName()) != null) {
      Expansion ex = new Expansion(EExpansionType.PREDICATES, null);
      if (graph.isExpanded(graph.getNode(PredicateClass.createInstance().getIdentifyingName()),
          ex)) {
        return graph.getNodesOfType(Predicate.class);
      }
    }
    return new ArrayList<Predicate>();
  }

  @Override
  public List<PredicateInstance> getAllPredicateInstances(String predicateURI) {
    if (graph.getNode(predicateURI) != null) {
      Expansion ex = new Expansion(EExpansionType.STORED_PREDICATE_INSTANCES, null);
      if (graph.isExpanded(graph.getNode(predicateURI), ex)) {
        return graph.getNodesOfType(PredicateInstance.class);
      }
    }
    return new ArrayList<PredicateInstance>();
  }

  @Override
  public List<PredicateInstance> generateAllSubjectPredicateInstances(String predicateURI) {

    Predicate pred = graph.getNodeG(predicateURI);

    if (pred != null) {
      Expansion ex =
          new Expansion(EExpansionType.VARIABLE_BASED_PREDICATE_INSTANCES, pred.getSubjectVar());
      if (graph.isExpanded(pred, ex)) {
        return graph.getNodesOfType(PredicateInstance.class).stream()
            .filter(predicateInstance -> predicateInstance.isSubjectInstance())
            .collect(Collectors.toList());
      }
    }
    return new ArrayList<PredicateInstance>();
  }

  @Override
  public PredicateInstance createPredicateInstance(String predicateURI,
      List<VariableBinding> bindings) {

    Predicate pred = graph.getNodeG(predicateURI);
    PredicateInstance instance = null;

    if (pred != null) {
      instance = new PredicateInstance(pred, bindings);
    }
    return instance;
  }

  @Override
  public Query generatePredicateInstanceQuery(String predicateURIInstance) {

    PredicateInstance predIns = graph.getNodeG(predicateURIInstance);

    if (predIns != null) {
      CustomSPARQLQuery query = new CustomSPARQLQuery(
          Utils.removeEscapeCharacter(predIns.getInstanceOf().getQuery().getQuery()));
      for (VariableBinding binding : predIns.getBindings()) {
        SPARQLUtilities.appendFilterClauseToQuery(query, binding.generateFilteringString());
      }
      return query.getSparqlQuery();
    }
    return null;
  }

  @Override
  public List<LiteralCondition> getAllConditions(MDSchema schema) {

    if (graph.getNode(LiteralConditionRootClass.createInstance().getIdentifyingName()) != null) {
      Expansion ex = new Expansion(EExpansionType.LITERAL_CONDITOINS, null);
      if (graph.isExpanded(
          graph.getNode(LiteralConditionRootClass.createInstance().getIdentifyingName()), ex)) {
        return graph.getNodesOfType(LiteralCondition.class);
      }
    }
    return new ArrayList<LiteralCondition>();
  }

  @Override
  public List<LiteralConditionType> getAllLiteralConditionTypes(MDSchema schema) {

    if (graph.getNode(LiteralConditionRootClass.createInstance().getIdentifyingName()) != null) {
      Expansion ex = new Expansion(EExpansionType.LITERAL_CONDITION_TYPES, null);
      if (graph.isExpanded(
          graph.getNode(LiteralConditionRootClass.createInstance().getIdentifyingName()), ex)) {
        return graph.getNodesOfType(LiteralConditionType.class);
      }
    }
    return new ArrayList<LiteralConditionType>();
  }

  @Override
  public List<LiteralCondition> getAllConditionTypeInstances(MDSchema schema,
      String conditionType) {

    if (graph.getNode(conditionType) != null) {

      Expansion ex = new Expansion(EExpansionType.LITERAL_CONDITOINS, null);
      if (graph.isExpanded(graph.getNode(conditionType), ex)) {
        return graph.getAllConditionTypeInstances(conditionType);
      }
    }
    return new ArrayList<LiteralCondition>();
  }

}
