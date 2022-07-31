package swag.analysis_graphs.execution_engine.operators;

/**
 * 
 * Operator visitor in order to double dispatch processing based on visitor instance and navigation
 * operation instance.
 * 
 * @author swag
 *
 */
public interface IOperatorVisitor {

  /**
   * Visit an {@code AddBaseMeasureSelectionOperator} operator
   * 
   * @param op the {@code AddBaseMeasureSelectionOperator} operator to visit
   */
  public void visit(AddBaseMeasureSelectionOperator op) throws Exception;

  /**
   * Visit an {@code MoveToDiceNodeOperator} operator
   * 
   * @param op the {@code MoveToDiceNodeOperator} operator to visit
   */
  public void visit(MoveToDiceNodeOperator op) throws Exception;

  /**
   * Visit an {@code RollUpOperator} operator
   * 
   * @param op the {@code RollUpOperator} operator to visit
   */
  public void visit(RollUpOperator op) throws Exception;

  /**
   * Visit an {@code RollUpToOperator} operator
   * 
   * @param op the {@code RollUpToOperator} operator to visit
   */
  public void visit(RollUpToOperator op) throws Exception;

  /**
   * Visit an {@code DrillDownOperator} operator
   * 
   * @param op the {@code DrillDownOperator} operator to visit
   */
  public void visit(DrillDownOperator op) throws Exception;

  /**
   * Visit an {@code DrillDownToOperator} operator
   * 
   * @param op the {@code DrillDownToOperator} operator to visit
   */
  public void visit(DrillDownToOperator op) throws Exception;

  /**
   * Visit an {@code AddDimensionSelectionOperator} operator
   * 
   * @param op the {@code AddDimensionSelectionOperator} operator to visit
   */
  public void visit(AddDimensionSelectionOperator op) throws Exception;

  /**
   * Visit an {@code AddDimTypedSliceConditoinOperator} operator
   * 
   * @param op the {@code AddDimTypedSliceConditoinOperator} operator to visit
   */
  public void visit(AddDimTypedSliceConditoinOperator op) throws Exception;

  /**
   * Visit an {@code AddResultSelectionOperator} operator
   * 
   * @param op the {@code AddResultSelectionOperator} operator to visit
   */
  public void visit(AddResultSelectionOperator op) throws Exception;

  /**
   * Visit an {@code AddMeasureOperator} operator
   * 
   * @param op the {@code AddMeasureOperator} operator to visit
   */
  public void visit(AddMeasureOperator op) throws Exception;

  /**
   * Visit an {@code NullOperator} operator
   * 
   * @param op the {@code NullOperator} operator to visit
   */
  public void visit(NullOperator nullOperator) throws Exception;

  /**
   * Visit an {@code MoveToPreviousDiceNodeOperator} operator
   * 
   * @param op the {@code MoveToPreviousDiceNodeOperator} operator to visit
   */
  public void visit(MoveToPreviousDiceNodeOperator prevDice) throws Exception;

  /**
   * Visit an {@code MoveToNextDiceNodeOperator} operator
   * 
   * @param op the {@code MoveToNextDiceNodeOperator} operator to visit
   */
  public void visit(MoveToNextDiceNodeOperator nextDiceSpec) throws Exception;

  /**
   * Visit an {@code MoveUpToDiceNodeOperator} operator
   * 
   * @param op the {@code MoveUpToDiceNodeOperator} operator to visit
   */
  public void visit(MoveUpToDiceNodeOperator mvDice) throws Exception;

  /**
   * Visit an {@code MoveDownToDiceNodeOperator} operator
   * 
   * @param op the {@code MoveDownToDiceNodeOperator} operator to visit
   */
  public void visit(MoveDownToDiceNodeOperator mvDice) throws Exception;

  /**
   * Visit an {@code ChangeGranularityOperator} operator
   * 
   * @param op the {@code ChangeGranularityOperator} operator to visit
   */
  public void visit(ChangeGranularityOperator op) throws Exception;
}
