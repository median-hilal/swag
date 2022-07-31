package swag.predicates;

public class PredicateEdge implements IPredicatesEdge {

  private IPredicateNode source;
  private IPredicateNode target;
  private EPredicateEdgeType type;

  @Override
  public IPredicateNode getSource() {
    return this.source;
  }

  @Override
  public IPredicateNode getTarget() {
    return this.target;
  }

  @Override
  public EPredicateEdgeType getType() {
    return this.type;
  }

  public PredicateEdge(IPredicateNode source, IPredicateNode target, EPredicateEdgeType type) {
    super();
    this.source = source;
    this.target = target;
    this.type = type;
  }


}
