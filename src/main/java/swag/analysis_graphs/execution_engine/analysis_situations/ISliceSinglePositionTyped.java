package swag.analysis_graphs.execution_engine.analysis_situations;

import javax.naming.OperationNotSupportedException;

import swag.web.IVariableVisitor;

public interface ISliceSinglePositionTyped<T extends ISignatureType>
    extends ISliceSinglePosition<T> {

  @Override
  public default void acceptVisitor(IVariableVisitor v)
      throws OperationNotSupportedException, Exception {
    v.visit(this);
  }
}
