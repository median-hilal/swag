package swag.analysis_graphs.execution_engine.analysis_situations;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.md_elements.Level;
import swag.md_elements.Mapping;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

public class LevelInAnalysisSituation extends Level
	implements IASItem<IDimensionQualification>, Variable<IDimensionQualification> {

    private Signature<IDimensionQualification> signature;

    @Override
    public Signature<IDimensionQualification> getSignature() {
	return signature;
    }

    @Override
    public void setSignature(Signature<IDimensionQualification> signature) {
	this.signature = signature;
    }

    public LevelInAnalysisSituation(Level l) {
	super(l);
    }

    public LevelInAnalysisSituation(String uri, String name, Mapping mapping,
	    Signature<IDimensionQualification> signature, String label) {
	super(uri, name, mapping, label);
	this.signature = signature;
    }

    public LevelInAnalysisSituation(Level l, Signature<IDimensionQualification> signature) {
	// System.out.println(l.getURI() + "--" + l.getName() + "--" +
	// l.getMapping());
	super(l.getURI(), l.getName(), l.getMapping(), l.getIdentifyingName(), l.getLabel());
	this.signature = signature;
    }

    /**
     * 
     * Creates a {@code LevelInAnalysisSituation} with a null signature. Do not
     * use unless it's only for temporary usage where the null signature is not
     * to be used.
     * 
     * 
     * @param uri
     * @param name
     * @param mapping
     */
    public LevelInAnalysisSituation(String uri, String name, Mapping mapping, String label) {
	super(uri, name, mapping, label);
    }

    /**
     * Shallow copy constructor constructor
     * 
     * @param lvl
     */
    private LevelInAnalysisSituation(LevelInAnalysisSituation lvl) {
	super(lvl);
	this.signature = lvl.getSignature().shallowCopy();
    }

    public Level copyLevelFromLevelInAnalysisSituation() {
	return new Level(this.getURI(), this.getName(), this.getMapping(), this.getIdentifyingName(), this.getLabel());
    }

    /**
     * Shallow copy
     * 
     * @return
     */
    @Override
    public LevelInAnalysisSituation shallowCopy() {
	return new LevelInAnalysisSituation(this);
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
	if (o instanceof LevelInAnalysisSituation) {
	    LevelInAnalysisSituation lvl = (LevelInAnalysisSituation) o;
	    if (this.getSignature().equals(lvl.getSignature()) && this.getIdentifyingName().equals(lvl
		    .getIdentifyingName()) /*
					    * && this.getMapping().equals(lvl.
					    * getMapping())
					    */)
		return true;
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	if (o instanceof LevelInAnalysisSituation) {
	    LevelInAnalysisSituation lvl = (LevelInAnalysisSituation) o;
	    if (this.getSignature().equals(lvl.getSignature()))
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

    @Override
    public void assignFromSourceVar(Variable sourceVar) {
	LevelInAnalysisSituation lvlInAS = (LevelInAnalysisSituation) sourceVar;
	this.setIdentifyingName(lvlInAS.getIdentifyingName());
	this.setURI(lvlInAS.getURI());
	this.setName(lvlInAS.getName());
	this.setMapping(lvlInAS.getMapping());
	this.setLabel(lvlInAS.getLabel());
	this.getSignature()
		.assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(lvlInAS.getSignature());
    }

    /**
     * Constructor used to build LevelInAnalysisSituation for variables, where
     * the level is non specified yet
     * 
     * @param signature
     */
    public LevelInAnalysisSituation(Signature<IDimensionQualification> signature) {
	super("", "", new Mapping(), "");
	this.signature = signature;
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
	LevelInAnalysisSituation tempLevel = (LevelInAnalysisSituation) tempVariable;
	this.setIdentifyingName(tempLevel.getIdentifyingName());
	this.setURI(tempLevel.getURI());
	this.setName(tempLevel.getName());
	this.setMapping(tempLevel.getMapping());
	this.setLabel(tempLevel.getLabel());
	this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setURI("");
	this.setName("");
	this.setMapping(new Mapping());
	this.setLabel("");
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
	if (o instanceof LevelInAnalysisSituation) {
	    LevelInAnalysisSituation lvl = (LevelInAnalysisSituation) o;
	    if (this.getSignature().equalsPositional(lvl.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public IClonableTo<IDimensionQualification> cloneMeTo(IDimensionQualification to) {
	return new LevelInAnalysisSituation(getIdentifyingName(), getName(), getMapping(),
		(Signature<IDimensionQualification>) getSignature().cloneMeTo(to), getLabel());
    }
}
