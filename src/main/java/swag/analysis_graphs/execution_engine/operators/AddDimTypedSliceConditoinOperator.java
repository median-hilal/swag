package swag.analysis_graphs.execution_engine.operators;

import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.md_elements.Dimension;
import swag.md_elements.QB4OHierarchy;

public class AddDimTypedSliceConditoinOperator extends DimOperation {

    /**
     * 
     */
    private static final long serialVersionUID = -162882894626465997L;
    ISliceSinglePosition<IDimensionQualification> condition;
    private final static String OPERATOR_NAME = "Move up to dice node";

    public AddDimTypedSliceConditoinOperator(String uri, String name, Dimension dim, QB4OHierarchy hier,
	    ISliceSinglePosition<IDimensionQualification> conditoin) {
	super(uri, name, dim, hier);
	this.condition = conditoin;
    }

    public AddDimTypedSliceConditoinOperator(String uri, String name, String label, String comment, Dimension dim,
	    QB4OHierarchy hier, ISliceSinglePosition<IDimensionQualification> conditoin) {
	super(uri, name, label, comment, dim, hier);
	this.condition = conditoin;
    }

    @Override
    public String getOperatorName() {
	return OPERATOR_NAME;
    }

    public ISliceSinglePosition<IDimensionQualification> getCondition() {
	return condition;
    }

    public void setCondition(ISliceSinglePosition<IDimensionQualification> condition) {
	this.condition = condition;
    }

    @Override
    public void accept(IOperatorVisitor visitor) throws Exception {
	visitor.visit(this);
    }

    @Override
    public String getTypeLabel() {
	return "Add Selection Condition";
    }
}
