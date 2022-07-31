package swag.web.formatters;

import swag.analysis_graphs.execution_engine.operators.AddBaseMeasureSelectionOperator;
import swag.analysis_graphs.execution_engine.operators.AddDimTypedSliceConditoinOperator;
import swag.analysis_graphs.execution_engine.operators.AddDimensionSelectionOperator;
import swag.analysis_graphs.execution_engine.operators.AddMeasureOperator;
import swag.analysis_graphs.execution_engine.operators.AddResultSelectionOperator;
import swag.analysis_graphs.execution_engine.operators.ChangeGranularityOperator;
import swag.analysis_graphs.execution_engine.operators.DrillDownOperator;
import swag.analysis_graphs.execution_engine.operators.DrillDownToOperator;
import swag.analysis_graphs.execution_engine.operators.IOperatorVisitor;
import swag.analysis_graphs.execution_engine.operators.MoveDownToDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveToDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveToNextDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveToPreviousDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveUpToDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.NullOperator;
import swag.analysis_graphs.execution_engine.operators.RollUpOperator;
import swag.analysis_graphs.execution_engine.operators.RollUpToOperator;
import swag.web.WebConstants;

public class NavigationStepImagePresentationVisitor implements IOperatorVisitor {

    private String generatedString;

    public NavigationStepImagePresentationVisitor() {
	super();
	this.generatedString = "";
    }

    @Override
    public void visit(MoveToDiceNodeOperator op) {
	generatedString = "  <!--img src='img/moveToDiceNode.png'  width='40' height='40' /--> "
		+ "<span class='glyphicon glyphicon-minus'></span> " + op.getPrefferredDisplayName()
		+ WebConstants.I_OPEN_RIGHT + WebConstants.OPERATOR_MOVE_TO_DICE_NODE + WebConstants.I_CLOSE;
    }

    @Override
    public void visit(RollUpOperator op) {
	generatedString = "  <!--img src='img/rollUpToLevel.png'  width='40' height='40' /-->"
		+ "<span class='glyphicon glyphicon-minus'></span> " + op.getPrefferredDisplayName()
		+ WebConstants.I_OPEN_RIGHT + WebConstants.OPERATOR_ROLL_UP_TO + WebConstants.I_CLOSE;
    }

    @Override
    public void visit(RollUpToOperator op) {
	generatedString = "  <!--img src='img/rollUpToLevel.png'  width='40' height='40' /-->"
		+ "<span class='glyphicon glyphicon-minus'></span> " + op.getPrefferredDisplayName()
		+ WebConstants.I_OPEN_RIGHT + WebConstants.OPERATOR_ROLL_UP_TO + WebConstants.I_CLOSE;
    }

    @Override
    public void visit(DrillDownOperator op) {
	generatedString = "  <!--img src='img/drillDownToLevel.png' width='40' height='40' /-->  "
		+ "<span class='glyphicon glyphicon-minus'></span> " + op.getPrefferredDisplayName()
		+ WebConstants.I_OPEN_RIGHT + WebConstants.OPERATOR_DRILL_DOWN_TO + WebConstants.I_CLOSE;
    }

    @Override
    public void visit(DrillDownToOperator op) {
	generatedString = "  <!--img src='img/drillDownToLevel.png' width='40' height='40' /-->  "
		+ "<span class='glyphicon glyphicon-minus'></span> " + op.getPrefferredDisplayName()
		+ WebConstants.I_OPEN_RIGHT + WebConstants.OPERATOR_DRILL_DOWN_TO + WebConstants.I_CLOSE;
    }

    @Override
    public void visit(AddDimensionSelectionOperator op) {
	generatedString = "  <!--img src='img/modifySliceCondition.png'  width='40' height='40' /--> "
		+ "<span class='glyphicon glyphicon-minus'></span> " + op.getPrefferredDisplayName()
		+ WebConstants.I_OPEN_RIGHT + WebConstants.OPERATOR_MODIFY_SLICE_CONDITION + WebConstants.I_CLOSE;
    }

    @Override
    public void visit(AddResultSelectionOperator op) {
	generatedString = "  <!--img src='img/modifySliceCondition.png'  width='40' height='40' /--> "
		+ "<span class='glyphicon glyphicon-minus'></span> " + op.getPrefferredDisplayName()
		+ WebConstants.I_OPEN_RIGHT + WebConstants.OPERATOR_MODIFY_RESULT_FILTER_CONDITION
		+ WebConstants.I_CLOSE;
    }

    @Override
    public void visit(AddBaseMeasureSelectionOperator op) {
	generatedString = "  <!--img src='img/modifySliceCondition.png'  width='40' height='40' /--> "
		+ "<span class='glyphicon glyphicon-minus'></span> " + op.getPrefferredDisplayName()
		+ WebConstants.I_OPEN_RIGHT + WebConstants.OPERATOR_MODIFY_BASE_MSR_CONDITION + WebConstants.I_CLOSE;
    }

    @Override
    public void visit(AddDimTypedSliceConditoinOperator op) {
	generatedString = "  <!--img src='img/modifySliceCondition.png'  width='40' height='40' /--> "
		+ "<span class='glyphicon glyphicon-minus'></span> " + op.getPrefferredDisplayName()
		+ WebConstants.I_OPEN_RIGHT + WebConstants.OPERATOR_MODIFY_SLICE_CONDITION + WebConstants.I_CLOSE;

    }

    @Override
    public void visit(NullOperator nullOperator) {
	// TODO Auto-generated method stub

    }

    @Override
    public void visit(AddMeasureOperator op) {
	// TODO Auto-generated method stub

    }

    @Override
    public void visit(MoveToPreviousDiceNodeOperator prevDice) throws Exception {
	// TODO Auto-generated method stub

    }

    @Override
    public void visit(MoveToNextDiceNodeOperator nextDiceSpec) throws Exception {
	// TODO Auto-generated method stub

    }

    @Override
    public void visit(MoveUpToDiceNodeOperator mvDice) throws Exception {
	// TODO Auto-generated method stub

    }

    @Override
    public void visit(MoveDownToDiceNodeOperator mvDice) throws Exception {
	// TODO Auto-generated method stub

    }

    @Override
    public void visit(ChangeGranularityOperator op) throws Exception {
	// TODO Auto-generated method stub

    }

    public String getGeneratedString() {
	return generatedString;
    }

    public void setGeneratedString(String generatedString) {
	this.generatedString = generatedString;
    }

}
