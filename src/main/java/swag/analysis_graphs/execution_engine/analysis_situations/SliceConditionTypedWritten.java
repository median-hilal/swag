package swag.analysis_graphs.execution_engine.analysis_situations;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.md_elements.MDElement;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

public class SliceConditionTypedWritten<T extends ISignatureType> implements ISliceSinglePositionTyped<T> {

    /**
     * 
     */
    private static final long serialVersionUID = 8697955623759125163L;

    private Signature<T> signature;
    private MDElement positionOfCondition;
    private final String uri = "";
    private String condition;
    private String type;

    private void initiateIfNull(String cond, String type) {
	if (cond == null) {
	    this.condition = "";
	}
	if (type == null) {
	    this.type = "";
	}
    }

    public SliceConditionTypedWritten() {
	super();
	initiateIfNull(null, null);
    }

    public SliceConditionTypedWritten(MDElement pos, String cond) {
	initiateIfNull(cond, null);
	this.condition = cond;
	this.positionOfCondition = pos;
    }

    public SliceConditionTypedWritten(MDElement pos, String cond, String type) {
	initiateIfNull(cond, type);
	this.condition = cond;
	this.positionOfCondition = pos;
	this.type = type;
    }

    public SliceConditionTypedWritten(Signature<T> signature) {
	this.signature = signature;
    }

    public SliceConditionTypedWritten(MDElement pos, String cond, Signature<T> signature) {
	this(pos, cond);
	this.signature = signature;
    }

    public SliceConditionTypedWritten(MDElement pos, String cond, String type, Signature<T> signature) {
	this(pos, cond, type);
	this.signature = signature;
    }

    /**
     * 
     * Shallow copy constructor
     * 
     * 
     */
    private SliceConditionTypedWritten(SliceConditionTypedWritten<T> pred) {
	this(pred.getPositionOfCondition(), pred.getCondition(), pred.getType(), pred.getSignature().shallowCopy());
    }

    @Override
    public String getVariableName() {
	return this.getSignature().isVariable() ? this.getSignature().getVriableName() : "";
    }

    @Override
    public String getVariableValue() {
	return condition;
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof SliceConditionTypedWritten) {
	    SliceConditionTypedWritten<T> sc = (SliceConditionTypedWritten<T>) o;
	    if (this.getSignature().equals(sc.getSignature())
		    && asUtilities.equalsWithNull(this.getCondition(), sc.getCondition())
		    && asUtilities.equalsWithNull(this.getPositionOfCondition(), sc.getPositionOfCondition())
		    && asUtilities.equalsWithNull(this.getType(), sc.getType())) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	if (o instanceof SliceConditionTypedWritten) {
	    SliceConditionTypedWritten<T> sc = (SliceConditionTypedWritten<T>) o;
	    if (this.getSignature().equals(sc.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		append(this.getSignature().hashCode()).append(this.getPositionOfCondition()).append(this.getCondition())
		.append(this.getSignature()).toHashCode();
    }

    @Override
    public void assignFromSourceVar(Variable sourceVar) {
	SliceConditionTypedWritten<T> sc = (SliceConditionTypedWritten<T>) sourceVar;
	this.setPositionOfCondition(sc.getPositionOfCondition());
	this.setCondition(sc.getCondition());
	this.setType(type);
	this.getSignature()
		.assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(sc.getSignature());
    }

    @Override
    public String acceptStringVisitor(VariableStringVisitor visitor, int variableIndex) {
	return visitor.visit(this, variableIndex);
    }

    @Override
    public void bind(Variable tempVariable) {

	SliceConditionTypedWritten<T> sc = (SliceConditionTypedWritten<T>) tempVariable;
	this.setCondition(sc.getCondition());
	this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
	this.setPositionOfCondition(sc.getPositionOfCondition());
	this.setType(sc.getType());

	BindParent();
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setCondition(null);
	this.getSignature().setVariableState(VariableState.VARIABLE);
	this.setPositionOfCondition(null);
	this.setType(null);
	unBindParent();
    }

    @Override
    public Variable shallowCopy() {
	return new SliceConditionTypedWritten<T>(this);
    }

    @Override
    public void acceptVisitor(IVariableVisitor v) throws Exception {
	v.visit(this);
    }

    @Override
    public Variable getParent() {
	return this.getSignature().getParentSpecification();
    }

    @Override
    public T getContainingObject() {
	return this.getSignature().getContainingObject();
    }

    @Override
    public Signature<T> getSignature() {
	return this.signature;
    }

    @Override
    public void setSignature(Signature<T> signature) {
	this.signature = signature;

    }

    public String getCondition() {
	return condition;
    }

    @Override
    public void addToAnalysisSituationOrNavigationStepVariables(IVariablesList asORnv) {
	if (this.getSignature().isVariable())
	    asORnv.addToVariables(this);
    }

    @Override
    public String getURI() {
	return uri;
    }

    @Override
    public void setURI(String uri) {
	throw new UnsupportedOperationException();
    }

    @Override
    public String getConditoin() {
	return this.condition;
    }

    @Override
    public void setCondition(String condition) {
	this.condition = condition;
    }

    @Override
    public MDElement getPositionOfCondition() {
	return positionOfCondition;
    }

    @Override
    public void setPositionOfCondition(MDElement positionOfCondition) {
	this.positionOfCondition = positionOfCondition;
    }

    @Override
    public Class<T> getTypeOfContainingObject() {
	return (Class<T>) this.getSignature().getContainingObject().getClass();
    }

    @Override
    public boolean equalsByNameAndPositionAndState(Variable o) {
	if (o instanceof SliceConditionTypedWritten) {
	    SliceConditionTypedWritten<T> sc = (SliceConditionTypedWritten<T>) o;
	    if (this.getSignature().equalsPositional(sc.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public String getType() {
	return this.type;
    }

    @Override
    public void setType(String type) {
	this.type = type;
    }

    @Override
    public IClonableTo<T> cloneMeTo(T to) {
	return new SliceConditionTypedWritten<T>(getPositionOfCondition(), getCondition(), getType(),
		(Signature<T>) getSignature().cloneMeTo(to));
    }

}
