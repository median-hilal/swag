package swag.analysis_graphs.execution_engine.operators;

import com.google.common.base.Preconditions;

import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAndAggFuncSpecificationInterface;

/**
 * An operator that adds a measure to the set of measures of the source analysis
 * situation.
 * 
 * @author swag
 */
public class AddMeasureOperator extends Operation {

    /**
     * 
     */
    private static final long serialVersionUID = -913204066382938357L;
    private final static String OPERATOR_NAME = "Add measure";

    @Override
    public String getOperatorName() {
	return OPERATOR_NAME;
    }

    private MeasureAndAggFuncSpecificationInterface measToAS;

    /**
     * Constructs a new {@code AddMeasureOperator} instance
     * 
     * @param uri
     *            the uri of the operation
     * @param name
     *            the local name of the operation
     * @param measToAS
     *            the measure to analysis situation
     */
    public AddMeasureOperator(String uri, String name, MeasureAndAggFuncSpecificationInterface measToAS) {
	super(uri, name);
	setMeasToAS(measToAS);
    }

    public AddMeasureOperator(String uri, String name, String label, String comment,
	    MeasureAndAggFuncSpecificationInterface measToAS) {
	super(uri, name, label, comment);
	this.measToAS = measToAS;
    }

    @Override
    public void accept(IOperatorVisitor visitor) throws Exception {
	visitor.visit(this);
    }

    public MeasureAndAggFuncSpecificationInterface getMeasToAS() {
	return measToAS;
    }

    public void setMeasToAS(MeasureAndAggFuncSpecificationInterface measToAS) {
	this.measToAS = Preconditions.checkNotNull(measToAS);
    }

    @Override
    public String getTypeLabel() {
	return "Add Measure";
    }
}
