package swag.predicates;

import swag.graph.Node;

public interface IPredicateNode extends Node {

  public default Class getTyp() {
    return this.getClass();
  }

}
