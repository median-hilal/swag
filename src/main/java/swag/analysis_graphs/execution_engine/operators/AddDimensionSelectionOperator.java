package swag.analysis_graphs.execution_engine.operators;

import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.md_elements.Dimension;
import swag.md_elements.QB4OHierarchy;

/**
 * A navigation operation that adds a dimension selection condition to a
 * specific dimension/hierarchy.
 * 
 * @author swag
 */
public class AddDimensionSelectionOperator extends DimOperation {

    /**
     * 
     */
    private static final long serialVersionUID = -8193881589251304064L;
    ISliceSinglePosition<IDimensionQualification> condition;
    private final static String OPERATOR_NAME = "Move up to dice node";

    /**
     * 
     * Creates a new {@code AddDimensionSelectionOperator} instance.
     * 
     * @param uri
     * @param name
     * @param dim
     * @param hier
     * @param conditoin
     */
    public AddDimensionSelectionOperator(String uri, String name, Dimension dim, QB4OHierarchy hier,
	    ISliceSinglePosition<IDimensionQualification> conditoin) {
	super(uri, name, dim, hier);
	this.condition = conditoin;
    }

    public AddDimensionSelectionOperator(String uri, String name, String label, String comment, Dimension dim,
	    QB4OHierarchy hier, ISliceSinglePosition<IDimensionQualification> conditoin) {
	super(uri, name, label, comment, dim, hier);
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

    public ISliceSinglePosition<IDimensionQualification> getCondition() {
	return condition;
    }

    public void setCondition(ISliceSinglePosition<IDimensionQualification> condition) {
	this.condition = condition;
    }

    @Override
    public String getTypeLabel() {
	return "Add Selection Condition";
    }

}
