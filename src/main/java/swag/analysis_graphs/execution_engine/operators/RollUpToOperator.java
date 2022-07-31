package swag.analysis_graphs.execution_engine.operators;

import swag.analysis_graphs.execution_engine.analysis_situations.IGranularitySpecification;
import swag.md_elements.Dimension;
import swag.md_elements.QB4OHierarchy;

/**
 * A roll up to a specific level navigation operator that changes the
 * granularity level on a dimensions/hierarchy to a specific level that is
 * higher it in the hierarchy.
 * 
 * @author swag
 */
public class RollUpToOperator extends ChangeGranularityOperator {

    /**
     * 
     */
    private static final long serialVersionUID = 7769218028380260361L;
    private final static String OPERATOR_NAME = "Change granularity to";

    @Override
    public String getOperatorName() {
	return OPERATOR_NAME;
    }

    public RollUpToOperator(String name, String abbName, IGranularitySpecification granSpec, Dimension dimension,
	    QB4OHierarchy hierarchy) {
	super(name, abbName, granSpec, dimension, hierarchy);
    }

    /**
     * 
     * Constructs a new {@code RollUpToOperator} with label and comment being
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
    public RollUpToOperator(String uri, String name, String label, String comment, IGranularitySpecification granSpec,
	    Dimension dim, QB4OHierarchy hier) {
	super(uri, name, label, comment, granSpec, dim, hier);
    }

    @Override
    public void accept(IOperatorVisitor visitor) throws Exception {
	visitor.visit(this);
    }

    @Override
    public String getTypeLabel() {
	return "Roll Up to Level";
    }
}
