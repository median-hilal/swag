package swag.analysis_graphs.execution_engine.operators;

import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToBaseMeasureCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;

/**
 * A navigation operation that adds a base measure selection condition to the
 * set of base measure conditions of the source analysis situation.
 * 
 * @author swag
 */
public class AddBaseMeasureSelectionOperator extends MeasureOperation {

    /**
     * 
     */
    private static final long serialVersionUID = -8193881589251304064L;
    ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition> condition;
    private final static String OPERATOR_NAME = "Add base Measure Condition";

    /**
     * Constructs a new {@code AddBaseMeasureSelectionOperator} instance
     * 
     * @param uri
     * @param name
     * @param conditoin
     */
    public AddBaseMeasureSelectionOperator(String uri, String name,
	    ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition> conditoin) {
	super(uri, name);
	this.condition = conditoin;
    }

    public AddBaseMeasureSelectionOperator(String uri, String name, String label, String comment,
	    ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition> conditoin) {
	super(uri, name, label, comment);
	this.condition = conditoin;
    }

    @Override
    public void accept(IOperatorVisitor visitor) throws Exception {
	visitor.visit(this);
    }

    @Override
    public String getOperatorName() {
	return OPERATOR_NAME;
    }

    public ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition> getCondition() {
	return condition;
    }

    public void setCondition(ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition> condition) {
	this.condition = condition;
    }

    @Override
    public String getTypeLabel() {
	return "Add Base Measure Selection Condition";
    }
}
