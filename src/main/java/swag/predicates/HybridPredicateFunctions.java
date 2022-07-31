package swag.predicates;

import java.util.ArrayList;
import java.util.List;
import org.apache.jena.query.Query;

import swag.md_elements.MDSchema;

public class HybridPredicateFunctions implements IPredicateFunctions {

  private IPredicateGraph graph;
  private FileBasedPredicateFunction filePredicatesFunctions;
  private ObjectBasedPredicateFunctinos objectPredicatesFunctions;

  public HybridPredicateFunctions(IPredicateGraph graph,
      FileBasedPredicateFunction filePredicatesFunctions,
      ObjectBasedPredicateFunctinos objectPredicatesFunctions) {
    super();
    this.graph = graph;
    this.filePredicatesFunctions = filePredicatesFunctions;
    this.objectPredicatesFunctions = objectPredicatesFunctions;
  }

  @Override
  public List<Predicate> getAllPredicates() {

    if (graph.getNode(PredicateClass.createInstance().getIdentifyingName()) == null) {
      graph.addNode(PredicateClass.createInstance());
    }
    if (graph.getNode(PredicateClass.createInstance().getIdentifyingName()) != null) {
      Expansion ex = new Expansion(EExpansionType.PREDICATES, null);
      if (graph.isExpanded(graph.getNode(PredicateClass.createInstance().getIdentifyingName()),
          ex)) {
        return objectPredicatesFunctions.getAllPredicates();
      } else {
        return filePredicatesFunctions.getAllPredicates();
      }
    }
    return new ArrayList<Predicate>();
  }

  @Override
  public List<PredicateInstance> getAllPredicateInstances(String predicateURI) {
    if (graph.getNode(predicateURI) != null) {
      Expansion ex = new Expansion(EExpansionType.STORED_PREDICATE_INSTANCES, null);
      if (graph.isExpanded(graph.getNode(predicateURI), ex)) {
        return objectPredicatesFunctions.getAllPredicateInstances(predicateURI);
      } else {
        return filePredicatesFunctions.getAllPredicateInstances(predicateURI);
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
        return objectPredicatesFunctions.generateAllSubjectPredicateInstances(predicateURI);
      } else {
        return filePredicatesFunctions.generateAllSubjectPredicateInstances(predicateURI);
      }
    }
    return new ArrayList<PredicateInstance>();
  }

  @Override
  public PredicateInstance createPredicateInstance(String predicateURI,
      List<VariableBinding> bindings) {

    Predicate pred = graph.getNodeG(predicateURI);
    if (pred != null) {
      return objectPredicatesFunctions.createPredicateInstance(predicateURI, bindings);
    } else {
      return filePredicatesFunctions.createPredicateInstance(predicateURI, bindings);
    }
  }

  @Override
  public Query generatePredicateInstanceQuery(String predicateURIInstance) {
    PredicateInstance predIns = graph.getNodeG(predicateURIInstance);
    if (predIns != null) {
      return objectPredicatesFunctions.generatePredicateInstanceQuery(predicateURIInstance);
    } else {
      return filePredicatesFunctions.generatePredicateInstanceQuery(predicateURIInstance);
    }
  }

  @Override
  public List<LiteralCondition> getAllConditions(MDSchema schema) {

    if (graph.getNode(LiteralConditionRootClass.createInstance().getIdentifyingName()) == null) {
      graph.addNode(LiteralConditionRootClass.createInstance());
    }

    Expansion ex = new Expansion(EExpansionType.LITERAL_CONDITOINS, null);
    if (graph.isExpanded(
        graph.getNode(LiteralConditionRootClass.createInstance().getIdentifyingName()), ex)) {
      return objectPredicatesFunctions.getAllConditions(schema);
    } else {
      return filePredicatesFunctions.getAllConditions(schema);
    }

  }

  @Override
  public List<LiteralConditionType> getAllLiteralConditionTypes(MDSchema schema) {

    if (graph.getNode(LiteralConditionRootClass.createInstance().getIdentifyingName()) == null) {
      graph.addNode(PredicateClass.createInstance());
    }

    if (graph.getNode(LiteralConditionRootClass.createInstance().getIdentifyingName()) != null) {

      Expansion ex = new Expansion(EExpansionType.LITERAL_CONDITION_TYPES, null);
      if (graph.isExpanded(
          graph.getNode(LiteralConditionRootClass.createInstance().getIdentifyingName()), ex)) {
        return objectPredicatesFunctions.getAllLiteralConditionTypes(schema);
      } else {
        return filePredicatesFunctions.getAllLiteralConditionTypes(schema);
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
        return objectPredicatesFunctions.getAllConditionTypeInstances(schema, conditionType);
      } else {
        return filePredicatesFunctions.getAllConditionTypeInstances(schema, conditionType);
      }
    }

    return new ArrayList<LiteralCondition>();
  }
}
