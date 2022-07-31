package swag.analysis_graphs.execution_engine.analysis_situations;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.md_elements.MDElement;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

public class SliceCondition<T extends ISignatureType> implements ISliceSinglePositionNoType<T> {

    /**
     * 
     */
    private static final long serialVersionUID = 8697955623759125163L;

    private Signature<T> signature;
    private MDElement positionOfCondition;
    private String uri;
    private String condition;
    private String type = "";
    private SliceConditionStatus status = SliceConditionStatus.UNKNOWN;

    public boolean isWritten() {
	return status.equals(SliceConditionStatus.WRITTEN);
    }

    public boolean isNonWritten() {
	return status.equals(SliceConditionStatus.NON_WRITTEN);
    }

    public boolean isUNknown() {
	return status.equals(SliceConditionStatus.UNKNOWN);
    }

    public SliceConditionStatus getStatus() {
	return status;
    }

    public void setStatus(SliceConditionStatus status) {
	this.status = status;
    }

    private void initiateIfNull(String cond, String uri) {
	if (uri == null) {
	    uri = "";
	}
	if (cond == null) {
	    cond = "";
	}
    }

    private void initiateIfNull(String cond, String uri, String type) {
	initiateIfNull(cond, uri);
	if (type == null) {
	    type = "";
	}
    }

    public SliceCondition() {
	super();
	initiateIfNull(null, null, null);
    }

    public SliceCondition(MDElement pos, String cond, String uri, SliceConditionStatus status) {
	this.condition = cond;
	this.positionOfCondition = pos;
	this.uri = uri;
	this.status = status;
	initiateIfNull(cond, uri, null);
    }

    public SliceCondition(MDElement pos, String cond, String uri, String type, SliceConditionStatus status) {
	this.condition = cond;
	this.positionOfCondition = pos;
	this.uri = uri;
	this.type = type;
	this.status = status;
	initiateIfNull(cond, uri, type);
    }

    public SliceCondition(Signature<T> signature) {
	this.signature = signature;
    }

    public SliceCondition(Signature<T> signature, String type) {
	this(signature);
	this.setType(type);
    }

    public SliceCondition(MDElement pos, String cond, String uri, Signature<T> signature, SliceConditionStatus status) {
	this(pos, cond, uri, status);
	this.signature = signature;
    }

    public SliceCondition(MDElement pos, String cond, String uri, String type, Signature<T> signature,
	    SliceConditionStatus status) {
	this(pos, cond, uri, type, status);
	this.signature = signature;
    }

    /**
     * 
     * Shallow copy constructor
     * 
     * 
     */
    private SliceCondition(SliceCondition<T> pred) {
	this(pred.getPositionOfCondition(), pred.getCondition(), pred.getURI(), pred.getType(),
		pred.getSignature().shallowCopy(), pred.getStatus());
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
	if (o instanceof SliceCondition) {
	    SliceCondition<T> sc = (SliceCondition<T>) o;
	    if (this.getSignature().equals(sc.getSignature())
		    && asUtilities.equalsWithNull(this.getCondition(), sc.getCondition())
		    && asUtilities.equalsWithNull(this.getPositionOfCondition(), sc.getPositionOfCondition())
		    && asUtilities.equalsWithNull(this.getURI(), sc.getURI())
		    && asUtilities.equalsWithNull(this.getType(), sc.getType())
	    // Status is not considered; othewrise navigation may fail
	    // && asUtilities.equalsWithNull(this.getStatus(), sc.getStatus())
	    ) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	if (o instanceof SliceCondition) {
	    SliceCondition<T> sc = (SliceCondition<T>) o;
	    if (this.getSignature().equals(sc.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		append(this.getSignature()).append(this.getPositionOfCondition()).append(this.getURI())
		.append(this.getCondition()).toHashCode();
    }

    @Override
    public void assignFromSourceVar(Variable sourceVar) {
	SliceCondition<T> sc = (SliceCondition<T>) sourceVar;
	this.setPositionOfCondition(sc.getPositionOfCondition());
	this.setCondition(sc.getCondition());
	this.setURI(sc.getURI());
	this.setType(sc.getType());
	this.setStatus(sc.getStatus());
	this.getSignature()
		.assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(sc.getSignature());
    }

    @Override
    public String acceptStringVisitor(VariableStringVisitor visitor, int variableIndex) {
	return visitor.visit(this, variableIndex);
    }

    @Override
    public void bind(Variable tempVariable) {

	SliceCondition<T> sc = (SliceCondition<T>) tempVariable;
	this.setCondition(sc.getCondition());
	this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
	this.setPositionOfCondition(sc.getPositionOfCondition());
	this.setType(sc.getType());
	this.setURI(sc.getURI());
	this.setStatus(sc.getStatus());
	BindParent();
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setCondition(null);
	this.getSignature().setVariableState(VariableState.VARIABLE);
	this.setPositionOfCondition(null);
	this.setType(null);
	this.setURI(null);
	this.setStatus(SliceConditionStatus.UNKNOWN);
	unBindParent();
    }

    @Override
    public Variable shallowCopy() {
	return new SliceCondition<T>(this);
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
	this.uri = uri;
	if (uri == null) {
	    this.uri = "";
	}
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
	if (o instanceof SliceCondition) {
	    SliceCondition<T> sc = (SliceCondition<T>) o;
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
	return new SliceCondition<T>(getPositionOfCondition(), getCondition(), getURI(), getType(),
		(Signature<T>) getSignature().cloneMeTo(to), getStatus());
    }

}
