package swag.analysis_graphs.execution_engine.operators;

import swag.analysis_graphs.execution_engine.analysis_situations.IDiceSpecification;
import swag.md_elements.Dimension;
import swag.md_elements.QB4OHierarchy;

/**
 * A navigation operation that changes the dice node on a dimension/hierarchy to the node that is a
 * member of the level up the current dice level in the hierarchy.
 * 
 * @author swag
 */
public class MoveUpToDiceNodeOperator extends MoveToDiceNodeOperator {

  /**
   * 
   */
  private static final long serialVersionUID = -207157341862684714L;
  private final static String OPERATOR_NAME = "Move up to dice node";

  public String getOperatorName() {
    return OPERATOR_NAME;
  }

  public MoveUpToDiceNodeOperator(String uri, String name, Dimension opOnDimension,
      IDiceSpecification diceSpecification, QB4OHierarchy hier) {
    super(uri, name, opOnDimension, diceSpecification, hier);
  }

  /**
   * 
   * Constructs a new {@code MoveUpToDiceNodeOperator} with label and comment being set.
   * 
   * @param uri uri of the operation
   * @param name local name of the operation
   * @param label the label
   * @param comment the comment
   * @param dim dimension of the operation
   * @param hier hierarchy of the operation
   * @param diceSpecification
   */
  public MoveUpToDiceNodeOperator(String uri, String name, String label, String comment,
      Dimension dim, IDiceSpecification diceSpecification, QB4OHierarchy hier) {
    super(uri, name, label, comment, dim, diceSpecification, hier);
  }

  @Override
  public void accept(IOperatorVisitor visitor) throws Exception {
    visitor.visit(this);
  }
}
