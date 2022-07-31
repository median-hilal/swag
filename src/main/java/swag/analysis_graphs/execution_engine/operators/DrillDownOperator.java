package swag.analysis_graphs.execution_engine.operators;

import swag.md_elements.Dimension;
import swag.md_elements.QB4OHierarchy;

/**
 * A navigation operation that changes the current granularity level on a
 * dimenion-hierarchy to the level below it in the hierarchy.
 * 
 * @author swag
 */
public class DrillDownOperator extends DimOperation {

    /**
     * 
     */
    private static final long serialVersionUID = -2324921294513908364L;
    private final static String OPERATOR_NAME = "Drill down";

    @Override
    public String getOperatorName() {
	return OPERATOR_NAME;
    }

    public DrillDownOperator(String name, String abbName, Dimension onDimension, QB4OHierarchy hier) {
	super(name, abbName, onDimension, hier);
    }

    /**
     * 
     * Constructs a new {@code DrillDownOperator} with label and comment being
     * set.
     * 
     * @param uri
     *            uri of the operation
     * @param name
     *            local name of the operation
     * @param label
     *            the label
     * @param comment
     *            the comment
     * @param dim
     *            dimension of the operation
     * @param hier
     *            hierarchy of the operation
     */
    public DrillDownOperator(String uri, String name, String label, String comment, Dimension dim, QB4OHierarchy hier) {
	super(uri, name, label, comment, dim, hier);
    }

    @Override
    public void accept(IOperatorVisitor visitor) throws Exception {
	visitor.visit(this);
    }

    @Override
    public String getTypeLabel() {
	return "Drill Down";
    }
}
