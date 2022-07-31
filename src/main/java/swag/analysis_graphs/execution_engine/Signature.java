package swag.analysis_graphs.execution_engine;

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.analysis_situations.IClonableTo;
import swag.analysis_graphs.execution_engine.analysis_situations.ISignatureType;
import swag.analysis_graphs.execution_engine.analysis_situations.ItemInAnalysisSituationType;
import swag.analysis_graphs.execution_engine.analysis_situations.Variable;
import swag.analysis_graphs.execution_engine.analysis_situations.VariableState;
import swag.analysis_graphs.execution_engine.analysis_situations.asUtilities;

/**
 * 
 * Represents the signature of an analysis situation element
 * 
 * @author swag
 *
 * @param <T>
 */
public class Signature<T extends ISignatureType> implements Serializable, IClonableTo<T> {

    /**
     * 
     */
    private static final long serialVersionUID = -5976916094896444833L;
    private VariableState variableState;
    private T containingObject;
    private ItemInAnalysisSituationType itemType;
    private String variableName;
    private Variable parentSpecification;

    public Signature(T containingObject, ItemInAnalysisSituationType itemType, VariableState variableState, String name,
	    Variable parentSpecification) {
	this.containingObject = containingObject;
	this.itemType = itemType;
	this.variableState = variableState;
	this.variableName = name;
	this.parentSpecification = parentSpecification;
    }

    /**
     * shallow copy constructor
     * 
     * @param sig
     */
    public Signature(Signature<T> sig) {
	this.containingObject = sig.getContainingObject();
	this.itemType = sig.getItemType();
	this.variableState = sig.getVariableState();
	this.variableName = sig.getVriableName();
	this.parentSpecification = sig.getParentSpecification();
    }

    public void assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(Signature<T> sig) {
	this.itemType = sig.getItemType();
	this.variableState = sig.getVariableState();
	this.variableName = sig.getVriableName();
    }

    /**
     * Returns true if and only if the variable state of the element is variable
     * (non bound)
     * 
     * @return Returns true if and only if the variable state of the element is
     *         variable (non bound)
     */
    public boolean isVariable() {
	return (this.variableState == VariableState.VARIABLE ? true : false);
    }

    /**
     * Returns true if and only if the variable state of the element is bound
     * variable
     * 
     * @return Returns true if and only if the variable state of the element is
     *         bound variable
     */
    public boolean isBoundVariable() {
	return (this.variableState == VariableState.BOUND_VARIABLE ? true : false);
    }

    public boolean isVariableOrBoundVariable() {
	return (this.variableState == VariableState.BOUND_VARIABLE || this.variableState == VariableState.VARIABLE
		? true : false);
    }

    public Signature<T> shallowCopy() {
	return new Signature<T>(this);
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof Signature) {
	    Signature<T> sig = (Signature<T>) o;
	    if (this.getItemType().equals(sig.getItemType()) && this.getVriableName().equals(sig.getVriableName())
		    && this.getContainingObject().comparePositoinal(sig.getContainingObject())
		    && asUtilities.equalsWithNull(this.getParentSpecification(), sig.getParentSpecification()))
		return true;
	}
	return false;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
		.append(this.getVriableName()).append(this.getItemType())
		.append(this.getContainingObject().generatePositionalHashCode()).toHashCode();
    }

    /**
     * 
     * Performs a positional equality check
     * 
     * @param o
     *            the object to check positional equality against
     * @return true if objects are equal. Otherwise false
     */
    public boolean equalsPositional(Object o) {
	if (o instanceof Signature) {
	    Signature<T> sig = (Signature<T>) o;
	    if (this.getItemType().equals(sig.getItemType()) && this.getVriableName().equals(sig.getVriableName())
		    && this.getContainingObject().comparePositoinal(sig.getContainingObject())
		    && asUtilities.equalsWithNull(this.getParentSpecification(), sig.getParentSpecification()))
		return true;
	}
	return false;
    }

    public T getContainingObject() {
	return containingObject;
    }

    public void setContainingObject(T containingObject) {
	this.containingObject = containingObject;
    }

    public VariableState getVariableState() {
	return variableState;
    }

    public void setVariableState(VariableState variableState) {
	this.variableState = variableState;
    }

    public ItemInAnalysisSituationType getItemType() {
	return itemType;
    }

    public void setItemType(ItemInAnalysisSituationType itemType) {
	this.itemType = itemType;
    }

    public String getVriableName() {
	return variableName;
    }

    public void setVariableName(String name) {
	this.variableName = name;
    }

    public Variable getParentSpecification() {
	return parentSpecification;
    }

    public void setParentSpecification(Variable parentSpecification) {
	this.parentSpecification = parentSpecification;
    }

    @Override
    public IClonableTo<T> cloneMeTo(T to) {
	return new Signature<T>(to, this.itemType, this.variableState, this.variableName, null);
    }

}
