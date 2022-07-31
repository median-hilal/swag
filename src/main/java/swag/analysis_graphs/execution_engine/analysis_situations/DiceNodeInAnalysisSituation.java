package swag.analysis_graphs.execution_engine.analysis_situations;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Preconditions;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

/**
 * 
 * Represents a dice node in an analysis situation.
 * 
 * @author swag
 *
 */
public class DiceNodeInAnalysisSituation
	implements IASItem<IDimensionQualification>, Variable<IDimensionQualification> {

    /**
     * 
     */
    private static final long serialVersionUID = 2911713175025308932L;

    private String nodeValue;
    private Signature<IDimensionQualification> signature;

    /**
     * Creates a new {@code DiceNodeInAnalysisSituation} instance
     * 
     * @param value
     *            value of the dice node
     * @param signature
     *            the signature of the dice node
     */
    public DiceNodeInAnalysisSituation(String value, Signature<IDimensionQualification> signature) {
	super();
	setNodeValue(value);
	setSignature(signature);
    }

    /**
     * 
     * Creates a {@code DiceNodeInAnalysisSituation} instance without a
     * signature (signature is null). Do not use unless it's only for temporary
     * usage where the null signature is not to be used.
     * 
     * @param value
     */
    public DiceNodeInAnalysisSituation(String value) {
	super();
	setNodeValue(value);
    }

    /**
     * shallow copy constructor. Creates a shallow copy of the calling
     * {@code DiceNodeInAnalysisSituation} instance.
     * 
     * @param diceNode
     *            the {@code DiceNodeInAnalysisSituation} instance to shallow
     *            copy
     */
    private DiceNodeInAnalysisSituation(DiceNodeInAnalysisSituation diceNode) {
	setNodeValue(diceNode.getNodeValue());
	setSignature(diceNode.getSignature().shallowCopy());
    }

    /**
     * Shallow copy. Creates a shallow copy of the calling
     * {@code DiceNodeInAnalysisSituation} instance.
     * 
     * @return a shallow copy of the calling {@code DiceNodeInAnalysisSituation}
     *         instance
     */
    @Override
    public DiceNodeInAnalysisSituation shallowCopy() {
	return new DiceNodeInAnalysisSituation(this);
    }

    /**
     * Constructor in case the dice node is a variable
     * 
     * @param signature
     */
    public DiceNodeInAnalysisSituation(Signature<IDimensionQualification> signature) {
	super();
	setNodeValue("");
	setSignature(signature);
    }

    @Override
    public String getVariableName() {
	return this.getSignature().isVariable() ? this.getSignature().getVriableName() : "";
    }

    @Override
    public String getVariableValue() {
	return this.nodeValue;
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof DiceNodeInAnalysisSituation) {
	    DiceNodeInAnalysisSituation dc = (DiceNodeInAnalysisSituation) o;
	    if (this.getSignature().equals(dc.getSignature()) && this.getNodeValue().equals(dc.getNodeValue()))
		return true;
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	if (o instanceof DiceNodeInAnalysisSituation) {
	    DiceNodeInAnalysisSituation dc = (DiceNodeInAnalysisSituation) o;
	    if (this.getSignature().equals(dc.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public void assignFromSourceVar(Variable sourceVar) {
	DiceNodeInAnalysisSituation dn = (DiceNodeInAnalysisSituation) sourceVar;
	this.setNodeValue(dn.getNodeValue());
	this.getSignature()
		.assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(dn.getSignature());
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
	// if deriving: appendSuper(super.hashCode()).
		append(this.getSignature().hashCode()).append(this.getNodeValue()).toHashCode();
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
	DiceNodeInAnalysisSituation tempNode = (DiceNodeInAnalysisSituation) tempVariable;
	this.setNodeValue(tempNode.getNodeValue());
	this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setNodeValue("");
	this.getSignature().setVariableState(VariableState.VARIABLE);
    }

    @Override
    public void acceptVisitor(IVariableVisitor v) throws OperationNotSupportedException {
	v.visit(this);
    }

    @Override
    public Object getContainingObject() {
	return this.getSignature().getContainingObject();
    }

    @Override
    public void addToAnalysisSituationOrNavigationStepVariables(IVariablesList asORnv) {
	if (this.getSignature().isVariable())
	    asORnv.addToVariables(this);
    }

    @Override
    public boolean equalsByNameAndPositionAndState(Variable o) {
	if (o instanceof DiceNodeInAnalysisSituation) {
	    DiceNodeInAnalysisSituation dc = (DiceNodeInAnalysisSituation) o;
	    if (this.getSignature().equalsPositional(dc.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public Signature<IDimensionQualification> getSignature() {
	return this.signature;
    }

    @Override
    public void setSignature(Signature<IDimensionQualification> signature) {
	this.signature = Preconditions.checkNotNull(signature);
    }

    public String getNodeValue() {
	return nodeValue;
    }

    public void setNodeValue(String nodeValue) {
	this.nodeValue = Preconditions.checkNotNull(nodeValue);
    }

    @Override
    public IClonableTo<IDimensionQualification> cloneMeTo(IDimensionQualification to) {
	return new DiceNodeInAnalysisSituation(getNodeValue(),
		(Signature<IDimensionQualification>) getSignature().cloneMeTo(to));
    }
}
