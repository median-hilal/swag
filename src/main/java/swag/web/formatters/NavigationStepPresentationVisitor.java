package swag.web.formatters;

import org.apache.commons.lang3.StringUtils;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceNodeInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
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
import swag.sparql_builder.Configuration;
import swag.web.ServletsHelper;
import swag.web.WebConstants;

public class NavigationStepPresentationVisitor implements IOperatorVisitor {

    private String generatedString;
    private NavigationStep nv;
    private AnalysisGraph ag;

    public NavigationStepPresentationVisitor(NavigationStep nv, AnalysisGraph ag) {
	super();
	this.generatedString = StringUtils.EMPTY;
	this.nv = nv;
	this.ag = ag;
    }

    @Override
    public void visit(AddBaseMeasureSelectionOperator op) {

	AddBaseMeasureSelectionOperator c = op;
	generatedString = StringUtils.EMPTY;
	if (c.getCondition().getSignature().isVariableOrBoundVariable()) {
	    generatedString += "<li class='variableListItem'>";
	} else {
	    generatedString += "<li class='nonVariableListItem'>";
	}

	generatedString += "<h6 class='specification'>" + " <img src='img/ico-filter.png'  width='20' height='20' />"
		+ WebConstants.I_OPEN_RIGHT + WebConstants.SLICE_COND_STRING + WebConstants.I_CLOSE
		+ "<p class='specification'> "
		+ (c.getCondition().getSignature().isVariableOrBoundVariable()
			? /* "" + lvl.getSignature().getVriableName() + */ "<br/>"
				+ ServletsHelper.getVariableString(c.getCondition(), nv, ag)
			: c.getCondition().getConditoin())
		+ "</p> " + "</h6> </li>";

    }

    private boolean isHideHierarchies() {
	return Configuration.getInstance().is("showHierarchies");
    }

    @Override
    public void visit(MoveToDiceNodeOperator op) {
	MoveToDiceNodeOperator mv = op;

	LevelInAnalysisSituation lvl = mv.getDiceSpecification().getPosition();
	DiceNodeInAnalysisSituation node = mv.getDiceSpecification().getDiceNodeInAnalysisSituation();

	if (lvl != null) {
	    generatedString = StringUtils.EMPTY;

	    if (lvl.getSignature().isVariableOrBoundVariable() || node.getSignature().isVariableOrBoundVariable()) {
		generatedString += "<li class='variableListItem'>";
	    } else {
		generatedString += "<li class='nonVariableListItem'>";
	    }

	    String hierString = isHideHierarchies()
		    ? "/" + (lvl.getSignature().getContainingObject().getHierarchy().getLabel()) : "";

	    generatedString += "<h6 class='specification'> "
		    + " <img src='img/ico-hierarchy.png'  width='20' height='20' />" + " <p class='specification'> "
		    + lvl.getSignature().getContainingObject().getD().getLabel() + hierString + "</p> </h6>"

		    + "</li> </ui>" + "<ui> <li>" + " <h6 class='specification'>"
		    + " <img src='img/ico-dice-black.png'  width='20' height='20' />" + " <p class='specification'> "
		    + (lvl.getSignature().isVariableOrBoundVariable()
			    ? " " + "<br/>" + ServletsHelper.getVariableString(lvl, nv, ag) : lvl.getLabel())
		    + " </p> " + WebConstants.I_OPEN_RIGHT + WebConstants.DICE_NODE_STRING + WebConstants.I_CLOSE
		    + "<p class='specification'> "
		    + (node.getSignature().isVariableOrBoundVariable()
			    ? " " + "<br/>" + ServletsHelper.getVariableString(node, nv, ag) : node.getNodeValue())
		    + " </p> " + "</h6> </li>";
	}

    }

    @Override
    public void visit(RollUpOperator op) {
	RollUpOperator gr = op;
	String hierString = isHideHierarchies() ? "/" + (gr.getOnHierarchy().getLabel()) : "";

	generatedString = StringUtils.EMPTY;
	generatedString += "<li class='nonVariableListItem'>";
	generatedString += "<h6 class='specification'> "
		+ " <img src='img/ico-hierarchy.png'  width='20' height='20' />" + " <p class='specification'> "
		+ gr.getOnDimension().getLabel() + hierString + "</p> </h6>" + "</li>";

    }

    @Override
    public void visit(RollUpToOperator op) {
	RollUpToOperator gr = op;

	generatedString = StringUtils.EMPTY;
	LevelInAnalysisSituation lvl = gr.getGranSpec().getPosition();

	if (lvl != null) {

	    if (lvl.getSignature().isVariableOrBoundVariable()) {
		generatedString += "<li class='variableListItem'>";
	    } else {
		generatedString += "<li class='nonVariableListItem'>";
	    }

	    String hierString = isHideHierarchies()
		    ? "/" + lvl.getSignature().getContainingObject().getHierarchy().getLabel() : "";

	    generatedString += "<h6 class='specification'> "
		    + " <img src='img/ico-hierarchy.png'  width='20' height='20' />" + " <p class='specification'> "
		    + lvl.getSignature().getContainingObject().getD().getLabel() + hierString + "</p> </h6>"
		    + "</li> </ui>" + "<ui> <li>" + "<h6 class='specification'> "
		    + " <img src='img/ico-granularity.png'  width='20' height='20' />" + " <p class='specification'> "
		    + (lvl.getSignature().isVariableOrBoundVariable()
			    ? /* "" + lvl.getSignature().getVriableName() + */ "<br/>"
				    + ServletsHelper.getVariableString(lvl, nv, ag)
			    : lvl.getLabel())
		    + "</p> " + WebConstants.I_OPEN_RIGHT + WebConstants.GRAN_LEVEL_STRING + WebConstants.I_CLOSE
		    + "</h6> </li>";
	}

    }

    @Override
    public void visit(DrillDownOperator op) {

	generatedString = StringUtils.EMPTY;
	DrillDownOperator gr = op;

	String hierString = isHideHierarchies() ? "/" + (gr.getOnHierarchy().getLabel()) : "";

	generatedString += "<li class='nonVariableListItem'>";
	generatedString += "<h6 class='specification'> "
		+ " <img src='img/ico-hierarchy.png'  width='20' height='20' />" + " <p class='specification'> "
		+ gr.getOnDimension().getLabel() + hierString + "</p> </h6>" + "</li>";
    }

    @Override
    public void visit(DrillDownToOperator op) {

	DrillDownToOperator gr = op;
	generatedString = StringUtils.EMPTY;
	LevelInAnalysisSituation lvl = gr.getGranSpec().getPosition();
	if (lvl != null) {
	    if (lvl.getSignature().isVariableOrBoundVariable()) {
		generatedString += "<li class='variableListItem'>";
	    } else {
		generatedString += "<li class='nonVariableListItem'>";
	    }

	    String hierString = isHideHierarchies()
		    ? "/" + lvl.getSignature().getContainingObject().getHierarchy().getLabel() : "";

	    generatedString += "<h6 class='specification'> "
		    + " <img src='img/ico-hierarchy.png'  width='20' height='20' />" + " <p class='specification'> "
		    + lvl.getSignature().getContainingObject().getD().getLabel() + hierString + "</p> </h6>"
		    + "</li> </ui>" + "<ui> <li>" + "<h6 class='specification'> "
		    + " <img src='img/ico-granularity.png'  width='20' height='20' />" + " <p class='specification'> "
		    + (lvl.getSignature().isVariableOrBoundVariable()
			    ? /* "" + lvl.getSignature().getVriableName() + */ "<br/>"
				    + ServletsHelper.getVariableString(lvl, nv, ag)
			    : lvl.getLabel())
		    + "</p> " + WebConstants.I_OPEN_RIGHT + WebConstants.GRAN_LEVEL_STRING + WebConstants.I_CLOSE
		    + "</h6> </li>";
	}
    }

    @Override
    public void visit(AddDimensionSelectionOperator op) {
	AddDimensionSelectionOperator c = op;

	generatedString = StringUtils.EMPTY;
	if (c.getCondition().getSignature().isVariableOrBoundVariable()) {
	    generatedString += "<li class='variableListItem'>";
	} else {
	    generatedString += "<li class='nonVariableListItem'>";
	}

	String hierString = isHideHierarchies()
		? "/" + c.getCondition().getSignature().getContainingObject().getHierarchy().getLabel() : "";

	generatedString += "<h6 class='specification'> "
		+ " <img src='img/ico-hierarchy.png'  width='20' height='20' />" + " <p class='specification'> "
		+ c.getCondition().getSignature().getContainingObject().getD().getLabel() + hierString + "</p> </h6>"
		+ "</li> </ui>" + "<ui> <li>" + "<h6 class='specification'>"
		+ " <img src='img/ico-filter.png'  width='20' height='20' />" + WebConstants.I_OPEN_RIGHT
		+ WebConstants.SLICE_COND_STRING + WebConstants.I_CLOSE + "<p class='specification'> "
		+ (c.getCondition().getSignature().isVariableOrBoundVariable()
			? /* "" + lvl.getSignature().getVriableName() + */ "<br/>"
				+ ServletsHelper.getVariableString(c.getCondition(), nv, ag)
			: c.getCondition().getConditoin())
		+ "</p> " + "</h6> </li>";

    }

    @Override
    public void visit(AddResultSelectionOperator op) {

	AddResultSelectionOperator c = op;

	generatedString = StringUtils.EMPTY;
	if (c.getCondition().getSignature().isVariableOrBoundVariable()) {
	    generatedString += "<li class='variableListItem'>";
	} else {
	    generatedString += "<li class='nonVariableListItem'>";
	}

	generatedString += "<h6 class='specification'>" + " <img src='img/ico-filter.png'  width='20' height='20' />"
		+ WebConstants.I_OPEN_RIGHT + WebConstants.SLICE_COND_STRING + WebConstants.I_CLOSE
		+ "<p class='specification'> "
		+ (c.getCondition().getSignature().isVariableOrBoundVariable()
			? /* "" + lvl.getSignature().getVriableName() + */ "<br/>"
				+ ServletsHelper.getVariableString(c.getCondition(), nv, ag)
			: c.getCondition().getConditoin())
		+ "</p> " + "</h6> </li>";
    }

    @Override
    public void visit(AddDimTypedSliceConditoinOperator op) {
	AddDimTypedSliceConditoinOperator c = op;

	generatedString = StringUtils.EMPTY;

	if (c.getCondition().getSignature().isVariableOrBoundVariable()) {
	    generatedString += "<li class='variableListItem'>";
	} else {
	    generatedString += "<li class='nonVariableListItem'>";
	}

	String hierString = isHideHierarchies()
		? "/" + c.getCondition().getSignature().getContainingObject().getHierarchy().getLabel() : "";

	generatedString += "<h6 class='specification'> "
		+ " <img src='img/ico-hierarchy.png'  width='20' height='20' />" + " <p class='specification'> "
		+ c.getCondition().getSignature().getContainingObject().getD().getLabel() + hierString + "</p> </h6>"
		+ "</li> </ui>" + "<ui> <li>" + "<h6 class='specification'>"
		+ " <img src='img/ico-filter.png'  width='20' height='20' />" + WebConstants.I_OPEN_RIGHT
		+ WebConstants.SLICE_COND_STRING + WebConstants.I_CLOSE + "<p class='specification'> "
		+ (c.getCondition().getSignature().isVariableOrBoundVariable()
			? /* "" + lvl.getSignature().getVriableName() + */ "<br/>"
				+ ServletsHelper.getVariableString(c.getCondition(), nv, ag)
			: c.getCondition().getConditoin())
		+ "</p> " + "</h6> </li>";

    }

    @Override
    public void visit(NullOperator nullOperator) {
	// TODO Auto-generated method stub

    }

    @Override
    public void visit(AddMeasureOperator op) {
	// TODO Auto-generated method stub

    }

    public String getGeneratedString() {
	return generatedString;
    }

    public void setGeneratedString(String generatedString) {
	this.generatedString = generatedString;
    }

    public NavigationStep getNv() {
	return nv;
    }

    public void setNv(NavigationStep nv) {
	this.nv = nv;
    }

    public AnalysisGraph getAg() {
	return ag;
    }

    public void setAg(AnalysisGraph ag) {
	this.ag = ag;
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

    /**
     * 
     * Get condition label, or the actual expression if the label is not present
     * 
     * @param cond
     *            the condition
     * @return condition label or expression
     */
    private String getConditoinDisplayString(ISliceSinglePosition cond) {
	return PresentationUtils.getConditoinDisplayString(cond, ag);
    }
}
