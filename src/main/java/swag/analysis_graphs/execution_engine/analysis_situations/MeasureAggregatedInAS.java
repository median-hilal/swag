package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.HashMap;

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
public class MeasureAggregatedInAS implements IMeasureInAS {

    /**
     * 
     */
    private static final long serialVersionUID = -1523954940729759355L;

    private Signature<AnalysisSituation> signature;
    private MeasureAggregated measure;
    private Configuration configuration = new Configuration(new HashMap<String, String>());

    /**
     * 
     * Creates a new MeasureAggregated object. Configuration is set to a new
     * empty configuration;
     * 
     * @param measure
     * @param signature
     */
    public MeasureAggregatedInAS(MeasureAggregated measure, Signature<AnalysisSituation> signature) {
	this.measure = measure;
	this.signature = signature;
	this.configuration = new Configuration();
    }

    /**
     * Creates a new MeasureAggregated object.
     * 
     * @param measure
     * @param signature
     * @param configuration
     */
    public MeasureAggregatedInAS(MeasureAggregated measure, Signature<AnalysisSituation> signature,
	    Configuration configuration) {
	this.measure = measure;
	this.signature = signature;
	this.configuration = configuration;
    }

    /**
     * shallow copy constructor
     * 
     * @param meas
     */
    private MeasureAggregatedInAS(MeasureAggregatedInAS meas) {
	this.measure = meas.measure;
	this.signature = meas.getSignature().shallowCopy();
    }

    /**
     * shallow copy
     * 
     * @return
     */
    @Override
    public MeasureAggregatedInAS shallowCopy() {
	return new MeasureAggregatedInAS(this);
    }

    @Override
    public String getVariableName() {
	return this.getSignature().isVariable() ? this.getSignature().getVriableName() : "";
    }

    @Override
    public String getVariableValue() {
	return this.getName();
    }

    /**
     * Checks if the current aggregated measure in analysis situation is based
     * on the passed aggregated measure.
     * 
     * @param msr
     *            the aggregated measure to check against
     * @return if the current aggregated measure in analysis situation is based
     *         on the passed aggregated measure.
     */
    public boolean isbasedOnAggregatedMeasure(MeasureAggregated msr) {
	return this.getMeasure().equals(msr);
    }

    /**
     * Checks if the currect aggregated measure is based on the passed
     * derived/base measure.
     * 
     * @param msr
     *            the base measure or derived measure to check against
     * 
     * @return true if the derived measure of this agg measure uses the passed
     *         msr in derivation, or the derived measure of this agg measure
     *         equals the passed msr in derivation, or the derived measure of
     *         this agg measure equals msr in derivation
     */
    public boolean isBasedOnMeasure(Measure msr) {
	return
	/*
	 * the derived measure of this agg measure uses the passed msr in
	 * derivation
	 */
	this.getMeasure().getMeasure().getMeasures().contains(msr)
		/*
		 * the derived measure of this agg measure equals the passed msr
		 * in derivation
		 */
		|| this.getMeasure().getMeasure().equals(msr)
		/*
		 * the derived measure of this agg measure equals msr in
		 * derivation
		 */
		|| this.getMeasure().getMeasure().equalsIgnoreType(msr);
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof MeasureAggregatedInAS) {
	    MeasureAggregatedInAS ms = (MeasureAggregatedInAS) o;
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
	if (o instanceof MeasureAggregatedInAS) {
	    MeasureAggregatedInAS ms = (MeasureAggregatedInAS) o;
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
	MeasureAggregatedInAS measInAS = (MeasureAggregatedInAS) sourceVar;

	this.setURI(measInAS.getURI());
	this.setName(measInAS.getName());
	this.setComment(measInAS.getComment());
	this.setSourceDerivedMeasure(measInAS.getSourceDerivedMeasure());
	this.setAgg(measInAS.getAgg());

	this.getSignature()
		.assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(measInAS.getSignature());
    }

    @Override
    public Variable getParent() {
	return this.signature.getParentSpecification();
    }

    @Override
    public String acceptStringVisitor(VariableStringVisitor visitor, int variableIndex)
	    throws OperationNotSupportedException {
	return visitor.visit(this, variableIndex);
    }

    @Override
    public void bind(Variable tempVariable) {
	MeasureAggregatedInAS tempMeasure = (MeasureAggregatedInAS) tempVariable;
	this.setURI(tempMeasure.getURI());
	this.setName(tempMeasure.getName());
	this.setComment(tempMeasure.getComment());
	this.setSourceDerivedMeasure(tempMeasure.getSourceDerivedMeasure());
	this.setAgg(tempMeasure.getAgg());

	this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
    }

    @Override
    public void unBind() throws OperationNotSupportedException {
	this.setURI("");
	this.setName("");
	this.setComment("");
	this.setSourceDerivedMeasure(null);
	this.setAgg(null);
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
	if (o instanceof MeasureAggregatedInAS) {
	    MeasureAggregatedInAS ms = (MeasureAggregatedInAS) o;
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
    public void setMeasure(IMeasure measure) {
	if (measure instanceof MeasureAggregated) {
	    this.measure = (MeasureAggregated) measure;
	} else {
	    throw new RuntimeException("Cannot cast to MeasureDerived " + measure.getURI());
	}
    }

    @Override
    public MeasureAggregated getMeasure() {
	return this.measure;
    }

    public MeasureDerived getSourceDerivedMeasure() {
	return measure.getMeasure();
    }

    public void setSourceDerivedMeasure(MeasureDerived measure) {
	this.measure.setMeasure(measure);
    }

    public AggregationFunction getAgg() {
	return measure.getAgg();
    }

    public void setAgg(AggregationFunction agg) {
	this.measure.setAgg(agg);
    }

    @Override
    public Configuration getConfiguration() {
	return configuration;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
	this.configuration = configuration;
    }

    @Override
    public IClonableTo<AnalysisSituation> cloneMeTo(AnalysisSituation to) {
	return new MeasureAggregatedInAS(getMeasure(), (Signature<AnalysisSituation>) getSignature().cloneMeTo(to),
		getConfiguration());
    }

    @Override
    public String getLabel() {
	return this.getMeasure().getLabel();
    }

}
