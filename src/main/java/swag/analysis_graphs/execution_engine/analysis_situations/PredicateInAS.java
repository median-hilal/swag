package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.HashSet;
import java.util.Set;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Preconditions;

import swag.analysis_graphs.execution_engine.Signature;
import swag.predicates.PredicateInstance;

public abstract class PredicateInAS<E extends ISignatureType> extends PredicateInAG implements IASItem<E>, Variable<E> {

    /**
     * 
     */
    private static final long serialVersionUID = 8987708507136281134L;

    private Signature<E> signature;

    @Override
    public Signature<E> getSignature() {
	return this.signature;
    }

    @Override
    public void setSignature(Signature<E> signature) {
	this.signature = Preconditions.checkNotNull(signature);
    }

    public PredicateInAS(Set<PredicateVariableToMDElementMapping> varMappings, String predicateInstanceURI,
	    PredicateInstance predicateInstance, Signature<E> signature) {

	super(varMappings, predicateInstanceURI, predicateInstance);
	this.signature = signature;
    }

    public PredicateInAS(PredicateInAG superPredicate) {

	super(superPredicate.getVarMappings(), superPredicate.getURI(), superPredicate.getPredicateInstance());
    }

    public PredicateInAS(Signature<E> signature) {
	this.signature = signature;
    }

    /**
     * 
     * Shallow copy constructor
     * 
     * @param pred
     *            a predicate in AS to copy
     * 
     */
    private PredicateInAS(PredicateInAS<E> pred) {

	setVarMappings(pred.getVarMappings());
	setURI(pred.getURI());
	setPredicateInstance(pred.getPredicateInstance());

	this.signature = pred.getSignature();
    }

    @Override
    public String getVariableName() {
	return this.getSignature().isVariable() ? this.getSignature().getVriableName() : "";
    }

    @Override
    public String getVariableValue() {
	return getURI();
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof PredicateInAS) {
	    PredicateInAS<E> sc = (PredicateInAS<E>) o;
	    if (this.getSignature().equals(sc.getSignature())
		    && asUtilities.equalsWithNull(this.getPredicateInstance(), sc.getPredicateInstance())
		    && asUtilities.equalsWithNull(this.getURI(), sc.getURI())
		    && asUtilities.equalsWithNull(this.getVarMappings(), sc.getVarMappings())) {
		return true;
	    }
	    return true;
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	if (o instanceof PredicateInAS) {
	    PredicateInAS<E> sc = (PredicateInAS<E>) o;
	    if (this.getSignature().equals(sc.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		append(this.getSignature().hashCode()).append(this.getURI()).toHashCode();
    }

    @Override
    public Variable getParent() {
	return this.signature.getParentSpecification();
    }

    @Override
    public void bind(Variable tempVariable) {

	PredicateInAS<E> sc = (PredicateInAS<E>) tempVariable;
	this.setVarMappings(PredicateVariableToMDElementMapping
		.deepCopySetOfPredicateVariableToMDElementMapping(sc.getVarMappings()));
	this.setURI(sc.getURI());
	this.setPredicateInstance(sc.getPredicateInstance());
	this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setVarMappings(new HashSet<PredicateVariableToMDElementMapping>());
	this.setURI("");
	this.setPredicateInstance(null);
	this.getSignature().setVariableState(VariableState.VARIABLE);
    }

    @Override
    public boolean equalsByNameAndPositionAndState(Variable o) {
	if (o instanceof PredicateInAS) {
	    PredicateInAS<E> sc = (PredicateInAS<E>) o;
	    if (this.getSignature().equalsPositional(sc.getSignature()))
		return true;
	}
	return false;
    }

}
