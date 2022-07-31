package swag.analysis_graphs.execution_engine.analysis_situations;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Preconditions;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

public class AggregationOperationInAnalysisSituation
	implements IASItem<IMeasureToAnalysisSituation>, Variable<IMeasureToAnalysisSituation> {

    private String aggregationFunction;
    private Object op; // TODO to be implemented later; it specifies an
		       // operation applied to the
		       // aggregation function and the measure
    private Signature<IMeasureToAnalysisSituation> signature;
    private String name;

    @Override
    public Signature<IMeasureToAnalysisSituation> getSignature() {
	return signature;
    }

    @Override
    public void setSignature(Signature<IMeasureToAnalysisSituation> signature) {
	this.signature = Preconditions.checkNotNull(signature);
    }

    public String getAggregationFunction() {
	return aggregationFunction;
    }

    public void setAggregationFunction(String aggregationFunction) {
	this.aggregationFunction = Preconditions.checkNotNull(aggregationFunction);
    }

    public Object getOp() {
	return op;
    }

    public void setOp(Object op) {
	this.op = Preconditions.checkNotNull(op);
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = Preconditions.checkNotNull(name);
    }

    /**
     * 
     * Creates a {@code AggregationOperationInAnalysisSituation} with a null
     * signature. Do not use unless it's only for temporary usage where the null
     * signature is not to be used.
     * 
     * @param name
     * @param aggregationFunction
     * @param op
     */
    public AggregationOperationInAnalysisSituation(String name, String aggregationFunction, Object op) {
	setName(name);
	setAggregationFunction(aggregationFunction);
	setOp(op);
    }

    public AggregationOperationInAnalysisSituation(String name, String aggregationFunction, Object op,
	    Signature<IMeasureToAnalysisSituation> signature) {
	setName(name);
	setAggregationFunction(aggregationFunction);
	setOp(op);
	setSignature(signature);
    }

    /**
     * shallow copy construct
     * 
     * @param agg
     */
    private AggregationOperationInAnalysisSituation(AggregationOperationInAnalysisSituation agg) {
	setName(agg.getName());
	setAggregationFunction(agg.getAggregationFunction());
	setOp(agg.getOp());
	setSignature(agg.getSignature().shallowCopy());
    }

    /**
     * shallow copy
     * 
     * @return
     */
    @Override
    public AggregationOperationInAnalysisSituation shallowCopy() {
	return new AggregationOperationInAnalysisSituation(this);
    }

    /**
     * Constructor called when the aggregation is a variable
     * 
     * @param signature
     */
    public AggregationOperationInAnalysisSituation(Signature<IMeasureToAnalysisSituation> signature) {
	setName("");
	setAggregationFunction("");
	setOp("");
	setSignature(signature);
    }

    @Override
    public String getVariableName() {
	return this.getSignature().isVariable() ? this.getSignature().getVriableName() : "";
    }

    @Override
    public String getVariableValue() {
	return this.aggregationFunction;
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof AggregationOperationInAnalysisSituation) {
	    AggregationOperationInAnalysisSituation ag = (AggregationOperationInAnalysisSituation) o;
	    if (this.getSignature().equals(ag.getSignature())
		    && this.getAggregationFunction().equals(ag.getAggregationFunction())
		    && this.getName().equals(ag.getName()) && this.getOp().equals(ag.getOp()))
		return true;
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	return this.equals(o);
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
	// if deriving: appendSuper(super.hashCode()).
		append(this.getSignature().hashCode()).append(this.getAggregationFunction()).append(this.getName())
		.append(this.getOp()).toHashCode();
    }

    @Override
    public void assignFromSourceVar(Variable sourceVar) {
	AggregationOperationInAnalysisSituation agg = (AggregationOperationInAnalysisSituation) sourceVar;
	this.setName(agg.getName());
	this.setAggregationFunction(agg.getAggregationFunction());
	this.setOp(agg.getOp());
	this.getSignature()
		.assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(agg.getSignature());
    }

    @Override
    public Variable getParent() {
	return this.signature.getParentSpecification();
    }

    @Override
    public String acceptStringVisitor(VariableStringVisitor visitor, int variableIndex) {
	return visitor.visit(this, variableIndex);
    }

    @Override
    public void bind(Variable tempVariable) {
	AggregationOperationInAnalysisSituation tempAgg = (AggregationOperationInAnalysisSituation) tempVariable;
	this.setName(tempAgg.getName());
	this.setAggregationFunction(tempAgg.getAggregationFunction());
	this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setName("");
	this.setAggregationFunction("");
	this.getSignature().setVariableState(VariableState.VARIABLE);
    }

    @Override
    public void acceptVisitor(IVariableVisitor v) {
	v.visit(this);
    }

    @Override
    public Object getContainingObject() {
	return this.getContainingObject();
    }

    @Override
    public void addToAnalysisSituationOrNavigationStepVariables(IVariablesList asORnv) {
	if (this.getSignature().isVariable())
	    asORnv.addToVariables(this);
    }

    @Override
    public boolean equalsByNameAndPositionAndState(Variable v) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public IClonableTo<IMeasureToAnalysisSituation> cloneMeTo(IMeasureToAnalysisSituation to) {
	return new AggregationOperationInAnalysisSituation(this.getName(), this.getAggregationFunction(), this.getOp(),
		(Signature<IMeasureToAnalysisSituation>) this.getSignature().cloneMeTo(to));
    }

}
