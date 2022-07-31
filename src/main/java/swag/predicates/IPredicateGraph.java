package swag.predicates;

import java.util.List;

import swag.graph.Graph;

public interface IPredicateGraph extends Graph<IPredicateNode, IPredicatesEdge> {

  public void addExpansion(IPredicateNode node, Expansion expansions);

  public boolean areConnectedVia(String leftURI, String rightURI,
      EPredicateEdgeType typeOfConnection);

  public List<Predicate> getAllPredicates();

  public List<PredicateInstance> getAllPredicateInstances(String predicateURI);

  public List<LiteralCondition> getAllConditionTypeInstances(String conditionTypeURI);

  public List<PredicateInstance> getAllSubjectPredicateInstances(String predicateURI);

  public IPredicateNode getTargetOfEdgeOfNode(IPredicateNode node, EPredicateEdgeType edgeType);

  public boolean isExpanded(IPredicateNode node, Expansion expansion);

  public <T extends IPredicateNode> List<T> getNodesOfType(Class<T> clazz);

  public <T extends IPredicateNode> List<T> getNodesInstancesOf(Class<T> clazz);

  public <T extends IPredicateNode> T getNodeG(String nodeURI);

  public void addSourceEdgeTargetTriple(IPredicateNode source, IPredicatesEdge edge,
      IPredicateNode target);

  public void createEdgeAndAddSourceEdgeTargetTriple(IPredicateNode source,
      EPredicateEdgeType edgeType, IPredicateNode target);

  public IPredicatesEdge createPredicateEdge(IPredicateNode source, EPredicateEdgeType edgeType,
      IPredicateNode target);
}
