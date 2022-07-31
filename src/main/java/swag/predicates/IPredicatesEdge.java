package swag.predicates;

import swag.graph.Edge;

public interface IPredicatesEdge extends Edge {

  @Override
  public IPredicateNode getSource();

  @Override
  public IPredicateNode getTarget();

  @Override
  public default String getIdentifyingName() {

    return this.getSource().getIdentifyingName() + "//" + this.getType() + "//"
        + this.getTarget().getIdentifyingName();
  }

  public EPredicateEdgeType getType();
}
