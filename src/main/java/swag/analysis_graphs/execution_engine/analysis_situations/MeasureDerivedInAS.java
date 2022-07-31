package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.md_elements.Measure;
import swag.sparql_builder.ASElements.configuration.Configuration;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

/**
 * 
 * This class represents a measure, either a simple basic measure or an
 * expression derived measure.
 * 
 * @author swag
 *
 */
public class MeasureDerivedInAS implements IMeasureInAS {

    /**
     * 
     */
    private static final long serialVersionUID = 532329699704808951L;

    private Signature<AnalysisSituation> signature;
    private MeasureDerived measure;
    private Configuration configuration = new Configuration(new HashMap<String, String>());

    public MeasureDerivedInAS(MeasureDerived measure, Signature<AnalysisSituation> signature) {
	this.measure = measure;
	this.signature = signature;
    }

    /**
     * shallow copy constructor
     * 
     * @param meas
     */
    private MeasureDerivedInAS(MeasureDerivedInAS meas) {
	this.measure = meas.measure;
	this.signature = meas.getSignature().shallowCopy();
    }

    public MeasureDerivedInAS(MeasureDerived measure, Signature<AnalysisSituation> signature,
	    Configuration configuration) {
	super();
	this.signature = signature;
	this.measure = measure;
	this.configuration = configuration;
    }

    /**
     * shallow copy
     * 
     * @return
     */
    @Override
    public MeasureDerivedInAS shallowCopy() {
	return new MeasureDerivedInAS(this);
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
	if (o instanceof MeasureDerivedInAS) {
	    MeasureDerivedInAS ms = (MeasureDerivedInAS) o;
	    if (this.getSignature().equals(ms.getSignature()) && this.getURI().equals(
		    ms.getURI()) /*
				  * && this.getMapping().equals(ms.getMapping())
				  */)
		return true;
	}
	return false;
    }

    @Override
    public boolean equalsPositional(Object o) {
	if (o instanceof MeasureDerivedInAS) {
	    MeasureDerivedInAS ms = (MeasureDerivedInAS) o;
	    if (this.getSignature().equals(ms.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		appendSuper(super.hashCode()).append(this.getSignature().hashCode()).append(this.getURI()).toHashCode();
    }

    @Override
    public void assignFromSourceVar(Variable sourceVar) {
	MeasureDerivedInAS measInAS = (MeasureDerivedInAS) sourceVar;

	this.setURI(measInAS.getURI());
	this.setName(measInAS.getName());
	this.setComment(measInAS.getComment());
	this.setExpression(measInAS.getExpression());
	this.setMeasures(new ArrayList<Measure>(measInAS.getMeasures()));
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
	MeasureDerivedInAS tempMeasure = (MeasureDerivedInAS) tempVariable;
	this.setURI(tempMeasure.getURI());
	this.setName(tempMeasure.getName());
	this.setComment(tempMeasure.getComment());
	this.setExpression(tempMeasure.getExpression());
	this.setMeasures(new ArrayList<Measure>(tempMeasure.getMeasures()));
	this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setURI("");
	this.setName("");
	this.setComment("");
	this.setExpression("");
	this.setMeasures(new ArrayList<Measure>());
	this.getSignature().setVariableState(VariableState.VARIABLE);
    }

    @Override
    public void acceptVisitor(IVariableVisitor v) throws Exception {
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
	if (o instanceof MeasureDerivedInAS) {
	    MeasureDerivedInAS ms = (MeasureDerivedInAS) o;
	    if (this.getSignature().equalsPositional(ms.getSignature()))
		return true;
	}
	return false;
    }

    @Override
    public Signature<AnalysisSituation> getSignature() {
	return signature;
    }

    @Override
    public void setSignature(Signature<AnalysisSituation> signature) {
	this.signature = signature;
    }

    public List<Measure> getMeasures() {
	return this.measure.getMeasures();
    }

    public void setMeasures(List<Measure> measures) {
	this.measure.setMeasures(measures);
    }

    public String getExpression() {
	return this.measure.getExpression();
    }

    public void setExpression(String expression) {
	this.measure.setExpression(expression);
    }

    @Override
    public String getComment() {
	return measure.getComment();
    }

    @Override
    public void setComment(String comment) {
	this.measure.setComment(comment);
    }

    @Override
    public String getURI() {
	return measure.getURI();
    }

    @Override
    public void setURI(String uri) {
	this.measure.setURI(uri);
    }

    @Override
    public String getName() {
	return measure.getName();
    }

    @Override
    public void setName(String name) {
	this.measure.setName(name);
    }

    @Override
    public MeasureDerived getMeasure() {
	return measure;
    }

    @Override
    public void setMeasure(IMeasure measure) {
	if (measure instanceof MeasureDerived) {
	    this.measure = (MeasureDerived) measure;
	} else {
	    throw new RuntimeException("Cannot cast to MeasureDerived " + measure.getURI());
	}
    }

    @Override
    public Configuration getConfiguration() {
	return this.configuration;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
	this.configuration = configuration;
    }

    @Override
    public IClonableTo<AnalysisSituation> cloneMeTo(AnalysisSituation to) {
	return new MeasureDerivedInAS(getMeasure(), (Signature<AnalysisSituation>) getSignature().cloneMeTo(to),
		getConfiguration());
    }

    @Override
    public String getLabel() {
	return this.getMeasure().getLabel();
    }

}
