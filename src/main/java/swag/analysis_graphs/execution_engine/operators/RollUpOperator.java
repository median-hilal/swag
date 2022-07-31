package swag.analysis_graphs.execution_engine.operators;

import swag.md_elements.Dimension;
import swag.md_elements.QB4OHierarchy;

/**
 * A navigation operation that changes the current granularity level on a
 * dimenion/hierarchy to the level up in the hierarchy.
 * 
 * @author swag
 */
public class RollUpOperator extends DimOperation {
    /**
     * 
     */
    private static final long serialVersionUID = 7408753346317184815L;
    private final static String OPERATOR_NAME = "Roll up";

    @Override
    public String getOperatorName() {
	return OPERATOR_NAME;
    }

    private Dimension onDimension;
    private QB4OHierarchy onHierarchy;

    public RollUpOperator(String uri, String name, Dimension onDimension, QB4OHierarchy hier) {
	super(uri, name, onDimension, hier);
    }

    /**
     * 
     * Constructs a new {@code RollUpOperator} with label and comment being set.
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
    public RollUpOperator(String uri, String name, String label, String comment, Dimension dim, QB4OHierarchy hier) {
	super(uri, name, label, comment, dim, hier);
    }

    @Override
    public void accept(IOperatorVisitor visitor) throws Exception {
	visitor.visit(this);
    }

    @Override
    public QB4OHierarchy getOnHierarchy() {
	return onHierarchy;
    }

    @Override
    public void setOnHierarchy(QB4OHierarchy onHierarchy) {
	this.onHierarchy = onHierarchy;
    }

    @Override
    public Dimension getOnDimension() {
	return onDimension;
    }

    @Override
    public void setOnDimension(Dimension onDimension) {
	this.onDimension = onDimension;
    }

    @Override
    public String getTypeLabel() {
	return "Roll Up";
    }
}
