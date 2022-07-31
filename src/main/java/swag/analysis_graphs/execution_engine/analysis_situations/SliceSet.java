package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.md_elements.MDElement;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

public class SliceSet implements ISliceSetDim {

    public SliceSet(Signature<IDimensionQualification> signature) {
	super();
	this.signature = signature;
	conditions = new ArrayList<>();
    }

    public SliceSet(String uri, String name, Signature<IDimensionQualification> signature) {
	super();
	this.signature = signature;
	this.uri = uri;
	this.name = name;
	conditions = new ArrayList<>();
    }

    public SliceSet() {
	conditions = new ArrayList<>();
    }

    public SliceSet(List<ISliceSinglePosition<IDimensionQualification>> conditions,
	    Signature<IDimensionQualification> signature) {
	super();
	this.conditions = conditions;
	this.signature = signature;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 5921767662530098472L;

    public SliceSet(String uri, String name, List<ISliceSinglePosition<IDimensionQualification>> conditions,
	    MDElement position, Signature<IDimensionQualification> signature) {
	super();
	this.uri = uri;
	this.name = name;
	this.conditions = conditions;
	this.position = position;
	this.signature = signature;
    }

    private String uri;
    private String name;
    private List<ISliceSinglePosition<IDimensionQualification>> conditions;
    private MDElement position;

    private Signature<IDimensionQualification> signature;

    private SliceSet(SliceSet sliceSet) {
	uri = sliceSet.getUri();
	name = sliceSet.getName();
	position = sliceSet.getPositoinOfSliceSet();
	setSignature(sliceSet.getSignature().shallowCopy());
	setConditions(sliceSet.getConditions().stream()
		.map(x -> (ISliceSinglePosition<IDimensionQualification>) x.shallowCopy())
		.collect(Collectors.toList()));
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
	if (o instanceof SliceSet) {
	    SliceSet sc = (SliceSet) o;
	    if (this.getSignature().equals(sc.getSignature()) && this.getConditions().containsAll(sc.getConditions())
		    && sc.getConditions().containsAll(this.getConditions())
		    && asUtilities.equalsWithNull(this.getUri(), sc.getUri())
		    && asUtilities.equalsWithNull(this.getPositoinOfSliceSet(), sc.getPositoinOfSliceSet())) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	if (o instanceof SliceSet) {
	    SliceSet sc = (SliceSet) o;
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
	SliceSet sc = (SliceSet) sourceVar;
	this.setConditions(
		sc.getConditions().stream().map(x -> (ISliceSinglePosition<IDimensionQualification>) x.shallowCopy())
			.collect(Collectors.toList()));
	this.setUri(sc.getUri());
	this.setName(sc.getName());
	this.setPositionOfMDElement(sc.getPositoinOfSliceSet());
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

	SliceSet sc = (SliceSet) tempVariable;
	this.setConditions(
		sc.getConditions().stream().map(x -> (ISliceSinglePosition<IDimensionQualification>) x.shallowCopy())
			.collect(Collectors.toList()));
	this.setUri(sc.getUri());
	this.setName(sc.getName());
	this.setPositionOfMDElement(sc.getPositoinOfSliceSet());
	this.getSignature()
		.assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(sc.getSignature());
	this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setConditions(new ArrayList<ISliceSinglePosition<IDimensionQualification>>());
	this.setUri("");
	this.setName("");
	this.setPositionOfMDElement(null);
	this.getSignature().setVariableState(VariableState.VARIABLE);
    }

    @Override
    public SliceSet shallowCopy() {
	return new SliceSet(this);
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
    public List<ISliceSinglePosition<IDimensionQualification>> getConditions() {
	return this.conditions;
    }

    @Override
    public void setConditions(List<ISliceSinglePosition<IDimensionQualification>> conditions) {
	this.conditions = conditions;
    }

    @Override
    public Signature<IDimensionQualification> getSignature() {
	return this.signature;
    }

    @Override
    public void setSignature(Signature<IDimensionQualification> signature) {
	this.signature = signature;
    }

    @Override
    public void acceptVisitor(IVariableVisitor v) {
	v.visit(this);
    }

    @Override
    public MDElement getPositoinOfSliceSet() {
	return this.position;
    }

    @Override
    public void setPositionOfMDElement(MDElement elem) {
	this.position = elem;
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
	if (o instanceof SliceSet) {
	    SliceSet sc = (SliceSet) o;
	    if (this.getSignature().equalsPositional(sc.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public IClonableTo<IDimensionQualification> cloneMeTo(IDimensionQualification to) {
	return new SliceSet(
		this.getConditions().stream().map(x -> (ISliceSinglePosition<IDimensionQualification>) x.cloneMeTo(to))
			.collect(Collectors.toList()),
		getSignature() != null ? (Signature<IDimensionQualification>) getSignature().cloneMeTo(to) : null);
    }

}
