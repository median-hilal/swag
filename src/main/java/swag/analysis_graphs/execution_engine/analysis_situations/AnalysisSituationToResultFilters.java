package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Class to act as a containing object of Result filters
 * 
 * @author swag
 */
public class AnalysisSituationToResultFilters implements IMeasureSignatureType, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1904284745675377281L;

    AnalysisSituation as;
    private List<ISliceSinglePosition<AnalysisSituationToResultFilters>> resultFilters = new ArrayList<>();

    /**
     * 
     * Creates a new {@code AnalysisSituationToResultFilters} instance
     * 
     * @param as
     *            analysis situation
     * @param resultFilters
     *            the list of result filters
     */
    public AnalysisSituationToResultFilters(AnalysisSituation as,
	    List<ISliceSinglePosition<AnalysisSituationToResultFilters>> resultFilters) {
	super();
	this.as = as;
	this.resultFilters = resultFilters;
    }

    public AnalysisSituationToResultFilters(AnalysisSituation as) {
	super();
	this.as = as;
    }

    /**
     * Adds a conditions to the result filters list
     * 
     * @param cond
     *            the condition to add
     */
    public void AddResultBaseFilter(ISliceSinglePosition<AnalysisSituationToResultFilters> cond) {
	resultFilters.add(cond);
    }

    /**
     * Gets the result filters list
     * 
     * @return a list of result filters
     */
    public List<ISliceSinglePosition<AnalysisSituationToResultFilters>> getResultFilters() {
	if (resultFilters == null) {
	    resultFilters = new ArrayList<>();
	}
	return resultFilters;
    }

    /**
     * Sets the list of result filters
     * 
     * @param resultFilters
     *            a list of result filters
     */
    public void setResultFilters(List<ISliceSinglePosition<AnalysisSituationToResultFilters>> resultFilters) {
	this.resultFilters = resultFilters;
    }

    @Override
    public boolean comparePositoinal(ISignatureType c) {
	if (this == c) {
	    return true;
	}
	if (c instanceof AnalysisSituationToResultFilters) {
	    AnalysisSituationToResultFilters as = (AnalysisSituationToResultFilters) c;
	    return this.getAs() != null && as.getAs() != null
		    /* null when it is a navigation step */ ? this.getAs().comparePositoinal(as.getAs()) : true;
	}
	return false;
    }

    @Override
    public int generatePositionalHashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		append(this.getClass()).append(as != null ? as.getFact() : "").toHashCode();
    }

    public AnalysisSituation getAs() {
	return as;
    }

    public void setAs(AnalysisSituation as) {
	this.as = as;
    }

    @Override
    public IClonableTo<ISignatureType> cloneMeTo(ISignatureType to) {
	return new AnalysisSituationToResultFilters((AnalysisSituation) to,
		getResultFilters().stream()
			.map(x -> (ISliceSinglePosition<AnalysisSituationToResultFilters>) x.cloneMeTo(this))
			.collect(Collectors.toList()));
    }
}
