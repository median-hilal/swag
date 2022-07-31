package swag.predicates;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

import swag.graph.Path;

public class PredicateGraph implements IPredicateGraph {

  private static final Logger logger = Logger.getLogger(PredicateGraph.class);


  private Map<IPredicateNode, Set<Expansion>> expansions;
  private Map<IPredicateNode, Set<IPredicatesEdge>> mdGraphMap;

  public PredicateGraph() {
    this.expansions = new HashMap<>();
    this.mdGraphMap = new HashMap<>();
  }

  @Override
  public void addExpansion(IPredicateNode node, Expansion expansion) {
    if (this.expansions.get(node) != null && this.expansions.get(node).size() != 0) {
      this.expansions.get(node).add(expansion);
    } else {
      Set<Expansion> exps = new HashSet<>();
      exps.add(expansion);
      this.expansions.put(node, exps);
    }
  }

  @Override
  public IPredicateNode getNode(String name) {
    for (IPredicateNode node : mdGraphMap.keySet()) {
      if (node.getIdentifyingName().equals(name)) {
        return node;
      }
    }
    return null;
  }

  @Override
  public IPredicatesEdge getEdge(String name) {
    for (IPredicateNode elem : mdGraphMap.keySet()) {
      for (IPredicatesEdge rel : mdGraphMap.get(elem)) {
        if (rel.getIdentifyingName().equals(name))
          return rel;
      }
    }
    return null;
  }

  @Override
  public Set<IPredicatesEdge> getEdgesOfNode(IPredicateNode node) {
    return mdGraphMap.get(node) != null ? mdGraphMap.get(node) : new HashSet<IPredicatesEdge>();
  }

  @Override
  public boolean addNode(IPredicateNode node) {
    if (mdGraphMap.get(node) == null) {
      mdGraphMap.put(node, new HashSet<>());
      return true;
    }
    return false;
  }

  @Override
  public boolean addEdge(IPredicatesEdge edge) {

    if (edge != null) {

      if (getEdge(edge.getIdentifyingName()) != null) {
        return false;
      }

      IPredicateNode node = edge.getSource();

      if (node != null) {
        if (mdGraphMap.get(node) == null) {
          Set<IPredicatesEdge> list = new HashSet<>();
          list.add(edge);
          mdGraphMap.put(node, list);
        } else {
          mdGraphMap.get(node).add(edge);
        }
        return true;
      }
    }
    return false;
  }


  @Override
  public List<Path<IPredicateNode, IPredicatesEdge>> getAllPathsBetweenTwoVertices(
      IPredicateNode startNode, IPredicateNode endNode) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<IPredicatesEdge> getAllEdges() {
    Set<IPredicatesEdge> relations = new HashSet<>();
    for (IPredicateNode elem : this.mdGraphMap.keySet()) {
      relations.addAll(getEdgesOfNode(elem));
    }
    return relations;
  }

  @Override
  public Set<IPredicateNode> getAllNodes() {
    return this.mdGraphMap.keySet();
  }

  @Override
  public boolean areConnectedVia(String leftURI, String rightURI,
      EPredicateEdgeType typeOfConnection) {

    IPredicateNode left = this.getNode(leftURI);
    IPredicateNode right = this.getNode(rightURI);

    for (IPredicatesEdge edge : getEdgesOfNode(left)) {
      if (edge.getType().equals(typeOfConnection) && edge.getTarget().equals(right)) {
        return true;
      }
    }
    return false;
  }

  public IPredicateNode getTargetOfEdgeOfNode(IPredicateNode node, EPredicateEdgeType edgeType) {
    for (IPredicatesEdge edge : getEdgesOfNode(node)) {
      if (edge.getType().equals(edgeType)) {
        return edge.getTarget();
      }
    }
    return null;
  }

  @Override
  public List<Predicate> getAllPredicates() {
    return this.getAllNodes().stream()
        .filter(predicate -> predicate.getTyp().equals(Predicate.class)).map(p -> (Predicate) p)
        .collect(Collectors.toList());
  }

  @Override
  public List<PredicateInstance> getAllPredicateInstances(String predicateURI) {
    return this.getAllNodes().stream()
        .filter(
            predicate -> predicate.getTyp().equals(PredicateInstance.class) && this.areConnectedVia(
                predicate.getIdentifyingName(), predicateURI, EPredicateEdgeType.INSTANCE_OF))
        .map(p -> (PredicateInstance) p).collect(Collectors.toList());
  }

  @Override
  public List<LiteralCondition> getAllConditionTypeInstances(String conditionTypeURI) {
    return this.getAllNodes().stream()
        .filter(predicate -> predicate.getTyp().equals(LiteralConditionType.class)
            && this.areConnectedVia(predicate.getIdentifyingName(), conditionTypeURI,
                EPredicateEdgeType.INSTANCE_OF))
        .map(p -> (LiteralCondition) p).collect(Collectors.toList());
  }

  @Override
  public List<PredicateInstance> getAllSubjectPredicateInstances(String predicateURI) {
    return this.getAllNodes().stream()
        .filter(predicate -> predicate.getTyp().equals(PredicateInstance.class)
            && this.areConnectedVia(predicate.getIdentifyingName(), predicateURI,
                EPredicateEdgeType.INSTANCE_OF)
            && ((PredicateInstance) predicate).bindingsContainVariable(
                ((Predicate) getTargetOfEdgeOfNode(predicate, EPredicateEdgeType.INSTANCE_OF))
                    .getSubjectVar()))
        .map(p -> (PredicateInstance) p).collect(Collectors.toList());
  }

  @Override
  public boolean isExpanded(IPredicateNode node, Expansion expansion) {
    if (expansions.get(node) != null && expansions.get(node).contains(expansion)) {
      return true;
    }
    return false;
  }

  @Override
  public <T extends IPredicateNode> List<T> getNodesOfType(Class<T> clazz) {
    return mdGraphMap.keySet().stream().filter(predicate -> predicate.getTyp().equals(clazz))
        .map(predicate -> (T) predicate).collect(Collectors.toList());
  }

  @Override
  public <T extends IPredicateNode> T getNodeG(String nodeURI) {
    return (T) getNode(nodeURI);
  }

  @Override
  public void addSourceEdgeTargetTriple(IPredicateNode source, IPredicatesEdge edge,
      IPredicateNode target) {

    addNode(source);
    addNode(target);
    addEdge(edge);
  }

  @Override
  public void createEdgeAndAddSourceEdgeTargetTriple(IPredicateNode source,
      EPredicateEdgeType edgeType, IPredicateNode target) {

    PredicateEdge edge = this.createPredicateEdge(source, edgeType, target);
    addNode(source);
    addNode(target);
    addEdge(edge);
  }

  @Override
  public PredicateEdge createPredicateEdge(IPredicateNode source, EPredicateEdgeType edgeType,
      IPredicateNode target) {
    return new PredicateEdge(source, target, edgeType);
  }

  @Override
  public Set<IPredicateNode> getNodesByIdentifyingNameSimilarity(String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<IPredicateNode> getNodesByName(String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T extends IPredicateNode> List<T> getNodesInstancesOf(Class<T> clazz) {
    // TODO Auto-generated method stub
    return null;
  }
}
