package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Class to act as a containing object of base measure conditions
 * 
 * @author swag
 */
public class AnalysisSituationToBaseMeasureCondition implements IMeasureSignatureType, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5093834830356867261L;

    private AnalysisSituation as;
    private List<ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition>> baseMeasureConditions = new ArrayList<>();

    /**
     * Creates a new {@code AnalysisSituationToBaseMeasureCondition} instance
     * 
     * @param as
     *            the analysis situation
     * @param baseMeasureConditions
     *            the conditions
     */
    public AnalysisSituationToBaseMeasureCondition(AnalysisSituation as,
	    List<ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition>> baseMeasureConditions) {
	super();
	this.as = as;
	this.baseMeasureConditions = baseMeasureConditions;
    }

    public AnalysisSituationToBaseMeasureCondition(AnalysisSituation as) {
	super();
	this.as = as;
    }

    /**
     * Adds a new base measure condition to the list
     * 
     * @param cond
     *            the condition to add
     */
    public void AddBaseMsrCondition(ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition> cond) {
	baseMeasureConditions.add(cond);
    }

    /**
     * Gets the list of base measure conditions
     * 
     * @return a list of base measure conditoins
     */
    public List<ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition>> getBaseMsrConditions() {
	if (baseMeasureConditions == null) {
	    baseMeasureConditions = new ArrayList<>();
	}
	return baseMeasureConditions;
    }

    /**
     * Sets the list of base measure conditions
     * 
     * @param resultBaseFilters
     */
    public void setBaseMsrConditions(
	    List<ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition>> baseMeasureConditions) {
	this.baseMeasureConditions = baseMeasureConditions;
    }

    @Override
    public boolean comparePositoinal(ISignatureType c) {
	if (this == c) {
	    return true;
	}
	if (c instanceof AnalysisSituationToBaseMeasureCondition) {
	    AnalysisSituationToBaseMeasureCondition as = (AnalysisSituationToBaseMeasureCondition) c;
	    return this.getAs().comparePositoinal(as.getAs());
	}
	return false;
    }

    @Override
    public int generatePositionalHashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		append(this.getClass()).append(this.getAs().getFact()).toHashCode();
    }

    public AnalysisSituation getAs() {
	return as;
    }

    public void setAs(AnalysisSituation as) {
	this.as = as;
    }

    @Override
    public IClonableTo<ISignatureType> cloneMeTo(ISignatureType to) {
	return new AnalysisSituationToBaseMeasureCondition((AnalysisSituation) to,
		getBaseMsrConditions().stream()
			.map(x -> (ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition>) x.cloneMeTo(this))
			.collect(Collectors.toList()));
    }
}
