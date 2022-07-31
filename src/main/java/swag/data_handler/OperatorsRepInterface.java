package swag.data_handler;

import java.util.List;

import swag.analysis_graphs.execution_engine.analysis_situations.Variable;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.analysis_graphs.execution_engine.operators.AddBaseMeasureSelectionOperator;
import swag.analysis_graphs.execution_engine.operators.AddDimTypedSliceConditoinOperator;
import swag.analysis_graphs.execution_engine.operators.AddDimensionSelectionOperator;
import swag.analysis_graphs.execution_engine.operators.AddMeasureOperator;
import swag.analysis_graphs.execution_engine.operators.AddResultSelectionOperator;
import swag.analysis_graphs.execution_engine.operators.DrillDownOperator;
import swag.analysis_graphs.execution_engine.operators.DrillDownToOperator;
import swag.analysis_graphs.execution_engine.operators.MoveDownToDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveToDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveToNextDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveUpToDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.Operation;
import swag.analysis_graphs.execution_engine.operators.OperatorInsufficientDefinitionException;
import swag.analysis_graphs.execution_engine.operators.RollUpOperator;
import swag.analysis_graphs.execution_engine.operators.RollUpToOperator;

public interface OperatorsRepInterface {

  /**
   * gets the operators of the navigation step passed by URI
   * 
   * @param nvURI the URI of the intended navigation step
   * @return a list of navigation operators
   */
  public List<Operation> getNavigationStepOperators(NavigationStep nv, String factURI);

  /**
   * Creates a new RollUpOperator, the passed URI should be for an existing RollUp operator
   * 
   * @param uri the URI of the operator
   * @return a new RollUpOpreator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public RollUpOperator createRollUpOperatorFromURI(String uri)
      throws OperatorInsufficientDefinitionException;

  /**
   * Creates a new RollUpToOperator, the passed URI should be for an existing RollUp operator
   * 
   * @param uri the URI of the operator
   * @return a new RollUpOpreator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public RollUpToOperator createRollUpToOperatorFromURI(String uri, String factURI,
      List<Variable> nvVariables) throws OperatorInsufficientDefinitionException;

  /**
   * Creates a new DrillDownOperator, the passed URI should be for an existing DrillDown operator
   * 
   * @param uri the URI of the operator
   * @return a new DrillDownOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public DrillDownOperator createDrillDownOperatorFromURI(String uri)
      throws OperatorInsufficientDefinitionException;

  /**
   * Creates a new DrillDownToOperator, the passed URI should be for an existing DrillDownTo
   * operator
   * 
   * @param uri the URI of the operator
   * @return a new DrillDownToOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public DrillDownToOperator createDrillDownToOperatorFromURI(String uri, String factURI,
      List<Variable> nvVariables) throws OperatorInsufficientDefinitionException;

  /**
   * Creates a new MoveToDiceNodeOperator, the passed URI should be for an existing
   * MoveToDiceNodeOperator operator
   * 
   * @param uri the URI of the operator
   * @return a new MoveToDiceNodeOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public MoveToDiceNodeOperator createMoveToDiceNodeOperatorFromURI(String uri, String factURI,
      List<Variable> nvVariables) throws OperatorInsufficientDefinitionException;

  /**
   * Creates a new MoveToNextDiceNodeOperator, the passed URI should be for an existing
   * MoveToDiceNodeOperator operator
   * 
   * @param uri the URI of the operator
   * @return a new MoveToDiceNodeOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public MoveToNextDiceNodeOperator createMoveToNextDiceNodeOperatorFromURI(String uri,
      String factURI, List<Variable> nvVariables) throws OperatorInsufficientDefinitionException;

  /**
   * Creates a new MoveDownToDiceNodeOperator, the passed URI should be for an existing
   * MoveDownToDiceNodeOperator operator
   * 
   * @param uri the URI of the operator
   * @return a new MoveDownToDiceNodeOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public MoveDownToDiceNodeOperator createMoveDownToDiceNodeOperatorFromURI(String uri,
      String factURI, List<Variable> nvVariables) throws OperatorInsufficientDefinitionException;

  /**
   * Creates a new MoveUpToDiceNodeOperator, the passed URI should be for an existing
   * MoveUpToDiceNodeOperator operator
   * 
   * @param uri the URI of the operator
   * @return a new MoveUpToDiceNodeOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public MoveUpToDiceNodeOperator createMoveUpToDiceNodeOperatorFromURI(String uri, String factURI,
      List<Variable> nvVariables) throws OperatorInsufficientDefinitionException;

  /**
   * Creates a new AddMeasureOperator, the passed URI should be for an existing AddMeasureOperator
   * operator
   * 
   * @param uri the URI of the operator
   * @return a new AddMeasureOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public AddMeasureOperator createAddMeasureOperatorFromURI(String uri)
      throws OperatorInsufficientDefinitionException;

  /**
   * Creates a new AddDimensionSelectionOperator, the passed URI should be for an existing
   * RefocusSliceConditionOperator operator
   * 
   * @param uri the URI of the operator
   * @return a new RefocusSliceConditionOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public AddDimensionSelectionOperator createAddDimSliceConditionOperatorFromURI(String uri,
      String factURI, List<Variable> nvVariables) throws OperatorInsufficientDefinitionException;

  AddBaseMeasureSelectionOperator createAddBaseMsrConditionOperatorFromURI(String uri,
      String factURI, List<Variable> nvVariables) throws OperatorInsufficientDefinitionException;

  AddResultSelectionOperator createAddResultFilterOperatorFromURI(String uri, String factURI,
      List<Variable> nvVariables) throws OperatorInsufficientDefinitionException;

  AddDimTypedSliceConditoinOperator createAddDimSliceConditionTypedOperatorFromURI(String uri,
      String factURI, List<Variable> nvVariables) throws OperatorInsufficientDefinitionException;

}
