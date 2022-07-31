package swag.analysis_graphs.execution_engine.operators;

import swag.analysis_graphs.execution_engine.analysis_situations.IGranularitySpecification;
import swag.md_elements.Dimension;
import swag.md_elements.QB4OHierarchy;

/**
 * A drill down to a specific level navigation operation that changes the
 * granularity level on a dimension/hierarchy to a specific level that is lower
 * it in the hierarchy.
 * 
 * @author swag
 */
public class DrillDownToOperator extends ChangeGranularityOperator {

    /**
     * 
     */
    private static final long serialVersionUID = -2614862336287071644L;
    private final static String OPERATOR_NAME = "Change granularity to";

    @Override
    public String getOperatorName() {
	return OPERATOR_NAME;
    }

    public DrillDownToOperator(String name, String abbName, IGranularitySpecification granSpec, Dimension dim,
	    QB4OHierarchy hier) {
	super(name, abbName, granSpec, dim, hier);
    }

    /**
     * 
     * Constructs a new {@code DrillDownToOperator} with label and comment being
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
     * @param granSpec
     */
    public DrillDownToOperator(String uri, String name, String label, String comment,
	    IGranularitySpecification granSpec, Dimension dim, QB4OHierarchy hier) {
	super(uri, name, label, comment, granSpec, dim, hier);
    }

    @Override
    public void accept(IOperatorVisitor visitor) throws Exception {
	visitor.visit(this);
    }

    @Override
    public String getTypeLabel() {
	return "Drill Down to Level";
    }
}
