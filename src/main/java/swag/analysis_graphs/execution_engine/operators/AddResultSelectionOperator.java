package swag.analysis_graphs.execution_engine.operators;

import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToResultFilters;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;

/**
 * A navigation operation that adds a result selection condition to the set of
 * result selection conditions of the source analysis situation.
 * 
 * @author swag
 */
public class AddResultSelectionOperator extends MeasureOperation {

    /**
     * 
     */
    private static final long serialVersionUID = -8193881589251304064L;

    ISliceSinglePosition<AnalysisSituationToResultFilters> condition;
    private final static String OPERATOR_NAME = "Add base Measure Condition";

    public AddResultSelectionOperator(String uri, String name,
	    ISliceSinglePosition<AnalysisSituationToResultFilters> conditoin) {
	super(uri, name);
	this.condition = conditoin;
    }

    public AddResultSelectionOperator(String uri, String name, String label, String comment,
	    ISliceSinglePosition<AnalysisSituationToResultFilters> conditoin) {
	super(uri, name, label, comment);
	this.condition = conditoin;
    }

    @Override
    public String getOperatorName() {
	return OPERATOR_NAME;
    }

    public ISliceSinglePosition<AnalysisSituationToResultFilters> getCondition() {
	return condition;
    }

    public void setCondition(ISliceSinglePosition<AnalysisSituationToResultFilters> condition) {
	this.condition = condition;
    }

    @Override
    public void accept(IOperatorVisitor visitor) throws Exception {
	visitor.visit(this);
    }

    @Override
    public String getTypeLabel() {
	return "Add Filter Condition";
    }

}
