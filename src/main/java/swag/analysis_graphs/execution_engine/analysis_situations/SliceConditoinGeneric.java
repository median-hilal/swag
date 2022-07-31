package swag.analysis_graphs.execution_engine.analysis_situations;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.md_elements.MDElement;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

public class SliceConditoinGeneric<T extends ISignatureType> implements ISliceSinglePosition<T> {

    /**
     * 
     */
    private static final long serialVersionUID = 8697955623759125163L;

    private Signature<T> signature;
    private MDElement positionOfCondition;
    private String uri;
    private String condition;
    private String type;

    private void initiateIfNull(String cond, String uri, String type) {
	if (uri == null) {
	    uri = "";
	}
	if (cond == null) {
	    cond = "";
	}
	if (type == null) {
	    type = "";
	}
    }

    public SliceConditoinGeneric() {
	super();
	initiateIfNull(null, null, null);
    }

    public SliceConditoinGeneric(MDElement pos, String cond, String uri) {
	initiateIfNull(cond, uri, null);
	this.condition = cond;
	this.positionOfCondition = pos;
	this.uri = uri;
    }

    public SliceConditoinGeneric(MDElement pos, String cond, String uri, String type) {
	initiateIfNull(cond, uri, type);
	this.condition = cond;
	this.positionOfCondition = pos;
	this.uri = uri;
	this.type = type;
    }

    public SliceConditoinGeneric(Signature<T> signature) {
	this.signature = signature;
    }

    public SliceConditoinGeneric(MDElement pos, String cond, String uri, Signature<T> signature) {
	this(pos, cond, uri);
	this.signature = signature;
    }

    public SliceConditoinGeneric(MDElement pos, String cond, String uri, String type, Signature<T> signature) {
	this(pos, cond, uri, type);
	this.signature = signature;
    }

    /**
     * 
     * Shallow copy constructor
     * 
     * 
     */
    private SliceConditoinGeneric(SliceConditoinGeneric<T> pred) {
	this(pred.getPositionOfCondition(), pred.getCondition(), pred.getURI(), pred.getSignature().shallowCopy());
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
	if (o instanceof SliceConditoinGeneric) {
	    SliceConditoinGeneric<T> sc = (SliceConditoinGeneric<T>) o;
	    if (this.getSignature().equals(sc.getSignature())
		    && asUtilities.equalsWithNull(this.getCondition(), sc.getCondition())
		    && asUtilities.equalsWithNull(this.getPositionOfCondition(), sc.getPositionOfCondition())
		    && asUtilities.equalsWithNull(this.getURI(), sc.getURI())
		    && asUtilities.equalsWithNull(this.getType(), sc.getType())) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	if (o instanceof SliceConditoinGeneric) {
	    SliceConditoinGeneric<T> sc = (SliceConditoinGeneric<T>) o;
	    if (this.getSignature().equals(sc.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		append(this.getSignature().hashCode()).append(this.getPositionOfCondition()).append(this.getURI())
		.append(this.getCondition()).append(this.getSignature()).toHashCode();
    }

    @Override
    public void assignFromSourceVar(Variable sourceVar) {
	SliceConditoinGeneric<T> sc = (SliceConditoinGeneric<T>) sourceVar;
	this.setPositionOfCondition(sc.getPositionOfCondition());
	this.setCondition(sc.getCondition());
	this.setURI(sc.getURI());
	this.setType(sc.getType());
	this.getSignature()
		.assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(sc.getSignature());
    }

    @Override
    public String acceptStringVisitor(VariableStringVisitor visitor, int variableIndex) {
	return visitor.visit(this, variableIndex);
    }

    @Override
    public void bind(Variable tempVariable) {

	SliceConditoinGeneric<T> sc = (SliceConditoinGeneric<T>) tempVariable;
	this.setCondition(sc.getCondition());
	this.setURI(sc.getURI());
	this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
	this.setPositionOfCondition(sc.getPositionOfCondition());
	this.setType(sc.getType());
	BindParent();
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setURI("");
	this.setCondition("");
	this.getSignature().setVariableState(VariableState.VARIABLE);
	this.setPositionOfCondition(null);
	this.setType("");
	unBindParent();
    }

    @Override
    public Variable shallowCopy() {
	return new SliceConditoinGeneric<T>(this);
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
    public void setSignature(Signature<T> signature) {
	this.signature = signature;
    }

    @Override
    public boolean equalsByNameAndPositionAndState(Variable o) {
	if (o instanceof SliceConditoinGeneric) {
	    SliceConditoinGeneric<T> sc = (SliceConditoinGeneric<T>) o;
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
	return new SliceConditoinGeneric<T>(getPositionOfCondition(), getCondition(), getURI(),
		(Signature<T>) getSignature().cloneMeTo(to));
    }

}
