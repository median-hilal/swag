package swag.analysis_graphs.execution_engine.analysis_situations;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.md_elements.MDElement;
import swag.md_elements.Mapping;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

public class SlicePositionInAnalysisSituation extends MDElement
	implements IASItem<IDimensionQualification>, Variable<IDimensionQualification> {

    /**
     * 
     */
    private static final long serialVersionUID = -274991174597581502L;
    private Signature<IDimensionQualification> signature;

    public MDElement getSlicePosition() {
	return this;
    }

    public void setSlicePosition(MDElement slicePosition) {
	this.setIdentifyingName(slicePosition.getIdentifyingName());
	this.setURI(slicePosition.getURI());
	this.setName(slicePosition.getName());
	this.setLabel(slicePosition.getLabel());
	this.setMapping(slicePosition.getMapping());
    }

    @Override
    public Signature<IDimensionQualification> getSignature() {
	return signature;
    }

    @Override
    public void setSignature(Signature<IDimensionQualification> signature) {
	this.signature = signature;
    }

    public SlicePositionInAnalysisSituation(MDElement slicePosition, Signature<IDimensionQualification> signature) {
	super(slicePosition.getURI(), slicePosition.getName(), slicePosition.getMapping(),
		slicePosition.getIdentifyingName(), slicePosition.getLabel());
	this.signature = signature;
    }

    private SlicePositionInAnalysisSituation(SlicePositionInAnalysisSituation slicePos) {

	super(slicePos);
	this.signature = slicePos.getSignature().shallowCopy();
    }

    /**
     * Constructor used to build SlicePositionInAnalysisSituation for variables,
     * where the level is non specified yet
     * 
     * @param signature
     */
    public SlicePositionInAnalysisSituation(Signature<IDimensionQualification> signature) {
	super("", "", new Mapping(), "");
	this.signature = signature;
    }

    public MDElement copyMDElementFromSlicePosition() {
	return new MDElement(this.getURI(), this.getName(), this.getMapping(), this.getIdentifyingName(),
		this.getLabel());
    }

    @Override
    public String getVariableName() {
	return this.getSignature().isVariable() ? this.getSignature().getVriableName() : "";
    }

    @Override
    public String getVariableValue() {
	return this.getName();
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof SlicePositionInAnalysisSituation) {
	    SlicePositionInAnalysisSituation sp = (SlicePositionInAnalysisSituation) o;
	    if (this.getSignature().equals(sp.getSignature())
		    && this.getSlicePosition().getIdentifyingName().equals(sp.getSlicePosition().getIdentifyingName()))
		return true;
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	if (o instanceof SlicePositionInAnalysisSituation) {
	    SlicePositionInAnalysisSituation sp = (SlicePositionInAnalysisSituation) o;
	    if (this.getSignature().equals(sp.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public void assignFromSourceVar(Variable sourceVar) {
	SlicePositionInAnalysisSituation sp = (SlicePositionInAnalysisSituation) sourceVar;
	this.setSlicePosition(new MDElement(sp.getSlicePosition()));
	this.getSignature()
		.assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(sp.getSignature());
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		appendSuper(super.hashCode()).append(this.getSignature().hashCode()).toHashCode();
    }

    @Override
    public SlicePositionInAnalysisSituation shallowCopy() {
	return new SlicePositionInAnalysisSituation(this);
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
    public void bind(Variable tempVariable) throws OperationNotSupportedException {
	// TODO Auto-generated method stub
	throw new OperationNotSupportedException();
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	// TODO Auto-generated method stub
	throw new OperationNotSupportedException();
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
	if (o instanceof SlicePositionInAnalysisSituation) {
	    SlicePositionInAnalysisSituation sp = (SlicePositionInAnalysisSituation) o;
	    if (this.getSignature().equalsPositional(sp.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public IClonableTo<IDimensionQualification> cloneMeTo(IDimensionQualification to) {
	return new SlicePositionInAnalysisSituation(new MDElement(this),
		(Signature<IDimensionQualification>) getSignature().cloneMeTo(to));
    }
}
