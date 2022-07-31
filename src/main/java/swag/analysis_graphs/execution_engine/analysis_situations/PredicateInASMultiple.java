package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.md_elements.MDElement;
import swag.md_elements.MultipleMDElement;
import swag.predicates.PredicateInstance;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

public class PredicateInASMultiple extends PredicateInAS<AnalysisSituation>
	implements ISliceMultiplePosition<AnalysisSituation> {

    /**
     * 
     */
    private static final long serialVersionUID = 8987708507136281134L;

    private String type = "";

    public PredicateInASMultiple(Set<PredicateVariableToMDElementMapping> varMappings, String predicateInstanceURI,
	    PredicateInstance predicateInstance, Signature<AnalysisSituation> signature, String type) {

	super(varMappings, predicateInstanceURI, predicateInstance, signature);
	this.type = type;
    }

    public PredicateInASMultiple(Set<PredicateVariableToMDElementMapping> varMappings, String predicateInstanceURI,
	    PredicateInstance predicateInstance, Signature<AnalysisSituation> signature) {

	super(varMappings, predicateInstanceURI, predicateInstance, signature);
    }

    public PredicateInASMultiple(PredicateInAG superPredicate) {
	super(superPredicate);
    }

    public PredicateInASMultiple(Signature<AnalysisSituation> signature) {
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
    private PredicateInASMultiple(PredicateInASMultiple pred) {

	this(pred.getVarMappings(), pred.getURI(), pred.getPredicateInstance(), pred.getSignature(), pred.getType());

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
	if (o instanceof PredicateInASMultiple) {
	    PredicateInASMultiple sc = (PredicateInASMultiple) o;
	    if (this.getSignature().equals(sc.getSignature())
		    && asUtilities.equalsWithNull(this.getPredicateInstance(), sc.getPredicateInstance())
		    && asUtilities.equalsWithNull(this.getURI(), sc.getURI())
		    && asUtilities.equalsWithNull(this.getVarMappings(), sc.getVarMappings())
		    && asUtilities.equalsWithNull(this.getType(), sc.getType())) {
		return true;
	    }
	    return true;
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	if (o instanceof PredicateInASMultiple) {
	    PredicateInASMultiple sc = (PredicateInASMultiple) o;
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
    public void assignFromSourceVar(Variable sourceVar) {
	PredicateInASMultiple sc = (PredicateInASMultiple) sourceVar;
	this.setVarMappings(PredicateVariableToMDElementMapping
		.deepCopySetOfPredicateVariableToMDElementMapping(sc.getVarMappings()));
	this.setURI(sc.getURI());
	this.setPredicateInstance(sc.getPredicateInstance());
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

	PredicateInASMultiple sc = (PredicateInASMultiple) tempVariable;
	this.setVarMappings(PredicateVariableToMDElementMapping
		.deepCopySetOfPredicateVariableToMDElementMapping(sc.getVarMappings()));
	this.setURI(sc.getURI());
	this.setPredicateInstance(sc.getPredicateInstance());
	this.setType(sc.getType());
	this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setVarMappings(new HashSet<PredicateVariableToMDElementMapping>());
	this.setURI("");
	this.setPredicateInstance(null);
	this.setType("");
	this.getSignature().setVariableState(VariableState.VARIABLE);
    }

    @Override
    public PredicateInASMultiple shallowCopy() {
	return new PredicateInASMultiple(this);
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
	return MultipleMDElement.getInstance();
    }

    @Override
    public void setPositionOfCondition(MDElement e) {
	;
    }

    @Override
    public String getConditoin() {
	return "";
    }

    @Override
    public void setCondition(String condition) {

    }

    @Override
    public List<MDElement> getPositions() {
	return getElems();
    }

    @Override
    public void setPositions(List<MDElement> positions) throws OperationNotSupportedException {
	throw new OperationNotSupportedException();
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
    public IClonableTo<AnalysisSituation> cloneMeTo(AnalysisSituation to) {
	throw new RuntimeException("Undefined Operation");
    }

}
