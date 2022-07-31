package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

public class SliceSetMultiple implements ISliceSetMultiple {

    public SliceSetMultiple(Signature<AnalysisSituation> signature) {
	super();
	this.signature = signature;
    }

    public SliceSetMultiple(String uri, String name, Signature<AnalysisSituation> signature) {
	super();
	this.signature = signature;
	this.uri = uri;
	this.name = name;
    }

    public SliceSetMultiple() {

    }

    /**
     * 
     */
    private static final long serialVersionUID = 5921767662530098472L;
    private String uri;
    private String name;
    private List<ISliceMultiplePosition<AnalysisSituation>> conditions;

    private Signature<AnalysisSituation> signature;

    private SliceSetMultiple(SliceSetMultiple sliceSet) {
	uri = sliceSet.getUri();
	name = sliceSet.getName();
	setSignature(sliceSet.getSignature().shallowCopy());
	setConditions(sliceSet.getConditions().stream()
		.map(x -> (ISliceMultiplePosition<AnalysisSituation>) x.shallowCopy()).collect(Collectors.toList()));
    }

    @Override
    public String getVariableName() {
	return this.getSignature().isVariable() ? this.getSignature().getVriableName() : "";
    }

    @Override
    public String getVariableValue() {
	return this.getUri();
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof SliceSetMultiple) {
	    SliceSetMultiple sc = (SliceSetMultiple) o;
	    if (this.getSignature().equals(sc.getSignature()) && this.getConditions().containsAll(sc.getConditions())
		    && sc.getConditions().containsAll(this.getConditions())
		    && asUtilities.equalsWithNull(this.getUri(), sc.getUri())) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	if (o instanceof SliceSetMultiple) {
	    SliceSetMultiple sc = (SliceSetMultiple) o;
	    if (this.getSignature().equals(sc.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		append(this.getSignature().hashCode()).toHashCode();
    }

    @Override
    public void assignFromSourceVar(Variable sourceVar) {
	SliceSetMultiple sc = (SliceSetMultiple) sourceVar;
	this.setConditions(sc.getConditions().stream()
		.map(x -> (ISliceMultiplePosition<AnalysisSituation>) x.shallowCopy()).collect(Collectors.toList()));
	this.setUri(sc.getUri());
	this.setName(sc.getName());
	;
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

	SliceSetMultiple sc = (SliceSetMultiple) tempVariable;
	this.setConditions(sc.getConditions().stream()
		.map(x -> (ISliceMultiplePosition<AnalysisSituation>) x.shallowCopy()).collect(Collectors.toList()));
	this.setUri(sc.getUri());
	this.setName(sc.getName());
	this.getSignature()
		.assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(sc.getSignature());
	this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setConditions(new ArrayList<ISliceMultiplePosition<AnalysisSituation>>());
	this.setUri("");
	this.setName("");
	this.getSignature().setVariableState(VariableState.VARIABLE);
    }

    @Override
    public SliceSetMultiple shallowCopy() {
	return new SliceSetMultiple(this);
    }

    @Override
    public String getUri() {
	return this.uri;
    }

    @Override
    public String getName() {
	return this.name;
    }

    @Override
    public void setUri(String uri) {
	this.uri = uri;
    }

    @Override
    public void setName(String name) {
	this.name = name;
    }

    @Override
    public List<ISliceMultiplePosition<AnalysisSituation>> getConditions() {
	return this.conditions;
    }

    @Override
    public void setConditions(List<ISliceMultiplePosition<AnalysisSituation>> conditions) {
	this.conditions = conditions;
    }

    @Override
    public Signature<AnalysisSituation> getSignature() {
	return this.signature;
    }

    @Override
    public void setSignature(Signature<AnalysisSituation> signature) {
	this.signature = signature;
    }

    @Override
    public void acceptVisitor(IVariableVisitor v) {
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
	if (o instanceof SliceSetMultiple) {
	    SliceSetMultiple sc = (SliceSetMultiple) o;
	    if (this.getSignature().equalsPositional(sc.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public IClonableTo<AnalysisSituation> cloneMeTo(AnalysisSituation to) {
	throw new RuntimeException("Undefined Operation");
    }

}
