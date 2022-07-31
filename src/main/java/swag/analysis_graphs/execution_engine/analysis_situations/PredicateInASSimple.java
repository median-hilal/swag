package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.HashSet;
import java.util.Set;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.md_elements.MDElement;
import swag.predicates.PredicateInstance;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

public class PredicateInASSimple extends PredicateInAS<IDimensionQualification>
	implements ISliceSinglePosition<IDimensionQualification> {

    public PredicateInASSimple(Set<PredicateVariableToMDElementMapping> varMappings, String predicateInstanceURI,
	    PredicateInstance predicateInstance, Signature<IDimensionQualification> signature) {
	super(varMappings, predicateInstanceURI, predicateInstance, signature);
    }

    private String type = "";
    private MDElement positionofPredicate;
    /**
    * 
    */
    private static final long serialVersionUID = 8987708507136281134L;

    public PredicateInASSimple(Set<PredicateVariableToMDElementMapping> varMappings, String predicateInstanceURI,
	    PredicateInstance predicateInstance, Signature<IDimensionQualification> signature, MDElement elem,
	    String type) {

	this(varMappings, predicateInstanceURI, predicateInstance, signature, elem);
	this.type = type;
    }

    public PredicateInASSimple(Set<PredicateVariableToMDElementMapping> varMappings, String predicateInstanceURI,
	    PredicateInstance predicateInstance, Signature<IDimensionQualification> signature, MDElement elem) {

	super(varMappings, predicateInstanceURI, predicateInstance, signature);
	this.positionofPredicate = elem;
    }

    public PredicateInASSimple(PredicateInAG superPredicate, MDElement positionofPredicate) {
	super(superPredicate);
	this.positionofPredicate = positionofPredicate;
    }

    public PredicateInASSimple(Signature<IDimensionQualification> signature) {
	super(signature);
    }

    /**
     * 
     * Shallow copy constructor
     * 
     * @param pred
     *            a predicate in AS to copy
     * 
     */
    private PredicateInASSimple(PredicateInASSimple pred) {

	this(pred.getVarMappings(), pred.getURI(), pred.getPredicateInstance(), pred.getSignature(),
		pred.getPositionOfCondition(), pred.getType());

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
	if (o instanceof PredicateInASSimple) {
	    PredicateInASSimple sc = (PredicateInASSimple) o;
	    if (this.getSignature().equals(sc.getSignature())
		    && asUtilities.equalsWithNull(this.getPositionOfCondition(), sc.getPositionOfCondition())
		    && asUtilities.equalsWithNull(this.getPredicateInstance(), sc.getPredicateInstance())
		    && asUtilities.equalsWithNull(this.getURI(), sc.getURI())
		    && asUtilities.equalsWithNull(this.getVarMappings(), sc.getVarMappings())
		    && asUtilities.equalsWithNull(this.getType(), sc.getType())) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	if (o instanceof PredicateInASSimple) {
	    PredicateInASSimple sc = (PredicateInASSimple) o;
	    if (this.getSignature().equals(sc.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		append(this.getSignature().hashCode()).append(this.getURI()).append(this.getPositionOfCondition())
		.toHashCode();
    }

    @Override
    public void assignFromSourceVar(Variable sourceVar) {
	PredicateInASSimple sc = (PredicateInASSimple) sourceVar;
	this.setVarMappings(PredicateVariableToMDElementMapping
		.deepCopySetOfPredicateVariableToMDElementMapping(sc.getVarMappings()));
	this.setURI(sc.getURI());
	this.setPredicateInstance(sc.getPredicateInstance());
	this.setPositionOfCondition(sc.getPositionOfCondition());
	this.setType(sc.getType());
	this.getSignature()
		.assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(sc.getSignature());
    }

    @Override
    public Variable getParent() {
	return this.getSignature().getParentSpecification();
    }

    @Override
    public String acceptStringVisitor(VariableStringVisitor visitor, int variableIndex) {
	return visitor.visit(this, variableIndex);
    }

    @Override
    public void bind(Variable tempVariable) {

	PredicateInASSimple sc = (PredicateInASSimple) tempVariable;
	this.setVarMappings(PredicateVariableToMDElementMapping
		.deepCopySetOfPredicateVariableToMDElementMapping(sc.getVarMappings()));
	this.setURI(sc.getURI());
	this.setPredicateInstance(sc.getPredicateInstance());
	this.setPositionOfCondition(sc.getPositionOfCondition());
	this.setType(sc.getType());
	BindParent();
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setVarMappings(new HashSet<PredicateVariableToMDElementMapping>());
	this.setURI("");
	this.setPredicateInstance(null);
	this.setPositionOfCondition(null);
	this.setType("");
	this.getSignature().setVariableState(VariableState.VARIABLE);
	unBindParent();
    }

    @Override
    public PredicateInASSimple shallowCopy() {
	return new PredicateInASSimple(this);
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
    public MDElement getPositionOfCondition() {
	return positionofPredicate;
    }


    @Override
    public void setPositionOfCondition(MDElement e) {
	this.positionofPredicate = e;
    }


    @Override
    public String getConditoin() {
	return "";
    }

    @Override
    public void setCondition(String condition) {

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
    public IClonableTo<IDimensionQualification> cloneMeTo(IDimensionQualification to) {
	throw new RuntimeException("Undefined Operation");
    }

}
