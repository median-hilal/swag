package swag.analysis_graphs.execution_engine.analysis_situations;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.md_elements.Mapping;
import swag.md_elements.Measure;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

public class MeasureInAnalysisSituation extends Measure
	implements IASItem<IMeasureToAnalysisSituation>, Variable<IMeasureToAnalysisSituation> {

    /**
     * 
     */
    private static final long serialVersionUID = -4128955157537703220L;

    private Signature<IMeasureToAnalysisSituation> signature;

    @Override
    public Signature<IMeasureToAnalysisSituation> getSignature() {
	return signature;
    }

    @Override
    public void setSignature(Signature<IMeasureToAnalysisSituation> signature) {
	this.signature = signature;
    }

    public MeasureInAnalysisSituation(String uri, String name, Mapping mapping,
	    Signature<IMeasureToAnalysisSituation> signature, String label) {
	super(uri, name, mapping, label);
	this.signature = signature;
    }

    /**
     * Creates a {@code MeasureInAnalysisSituation} without a signature
     * (signature is null). Do not use unless it's only for temporary usage
     * where the null signature is not to be used.
     * 
     * @param meas
     * @param signature
     */
    public MeasureInAnalysisSituation(String uri, String name, Mapping mapping, String label) {
	super(uri, name, mapping, label);
    }

    /**
     * Generates a MeasureInAnalysisSituation form a Measure and a Signature
     * instances
     * 
     * @param meas
     * @param signature
     */
    public MeasureInAnalysisSituation(Measure meas, Signature<IMeasureToAnalysisSituation> signature) {
	super(meas.getURI(), meas.getName(), meas.getMapping(), meas.getIdentifyingName(), meas.getLabel());
	this.signature = signature;
    }

    /**
     * shallow copy constructor
     * 
     * @param meas
     */
    private MeasureInAnalysisSituation(MeasureInAnalysisSituation meas) {
	super(meas);
	this.signature = meas.getSignature().shallowCopy();
    }

    /**
     * shallow copy
     * 
     * @return
     */
    @Override
    public MeasureInAnalysisSituation shallowCopy() {
	return new MeasureInAnalysisSituation(this);
    }

    public Measure copyMeasureFromMeasureInAnalysisSituation() {
	return new Measure(this.getURI(), this.getName(), this.getMapping(), this.getIdentifyingName(),
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
	if (o instanceof MeasureInAnalysisSituation) {
	    MeasureInAnalysisSituation ms = (MeasureInAnalysisSituation) o;
	    if (this.getSignature().equals(ms.getSignature()) && this.getIdentifyingName().equals(
		    ms.getIdentifyingName()) /*
					      * && this.getMapping().equals(ms.
					      * getMapping())
					      */)
		return true;
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	if (o instanceof MeasureInAnalysisSituation) {
	    MeasureInAnalysisSituation ms = (MeasureInAnalysisSituation) o;
	    if (this.getSignature().equals(ms.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		appendSuper(super.hashCode()).append(this.getSignature().hashCode()).append(this.getIdentifyingName())
		.toHashCode();
    }

    /**
     * Constructor used to build MeasureInAnalysisSituation for variables, where
     * the measure is non specified yet
     * 
     * @param signature
     */
    public MeasureInAnalysisSituation(Signature<IMeasureToAnalysisSituation> signature) {
	super("", null, new Mapping(), "");
	this.signature = signature;
    }

    @Override
    public void assignFromSourceVar(Variable sourceVar) {
	MeasureInAnalysisSituation measInAS = (MeasureInAnalysisSituation) sourceVar;
	this.setIdentifyingName(measInAS.getIdentifyingName());
	this.setURI(measInAS.getURI());
	this.setName(measInAS.getName());
	this.setMapping(measInAS.getMapping());
	this.getSignature()
		.assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(measInAS.getSignature());
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
	MeasureInAnalysisSituation tempMeasure = (MeasureInAnalysisSituation) tempVariable;
	this.setIdentifyingName(tempMeasure.getIdentifyingName());
	this.setURI(tempMeasure.getURI());
	this.setName(tempMeasure.getName());
	this.setMapping(tempMeasure.getMapping());
	this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setURI("");
	this.setMapping(new Mapping());
	this.getSignature().setVariableState(VariableState.VARIABLE);
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
	if (o instanceof MeasureInAnalysisSituation) {
	    MeasureInAnalysisSituation ms = (MeasureInAnalysisSituation) o;
	    if (this.getSignature().equalsPositional(ms.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public IClonableTo<IMeasureToAnalysisSituation> cloneMeTo(IMeasureToAnalysisSituation to) {
	return new MeasureInAnalysisSituation((Measure) this,
		(Signature<IMeasureToAnalysisSituation>) getSignature().cloneMeTo(to));
    }

}
