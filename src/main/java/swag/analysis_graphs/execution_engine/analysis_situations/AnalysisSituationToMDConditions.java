package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AnalysisSituationToMDConditions implements ISignatureType, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6348371904360834766L;

    AnalysisSituation as;
    private List<ISliceSinglePosition<AnalysisSituationToMDConditions>> MDConditions = new ArrayList<>();

    public AnalysisSituationToMDConditions(AnalysisSituation as,
	    List<ISliceSinglePosition<AnalysisSituationToMDConditions>> MDConditions) {
	super();
	this.as = as;
	this.MDConditions = MDConditions;
    }

    public AnalysisSituationToMDConditions(AnalysisSituation as) {
	super();
	this.as = as;
    }

    public void AddMDCondition(ISliceSinglePosition<AnalysisSituationToMDConditions> cond) {
	MDConditions.add(cond);
    }

    public List<ISliceSinglePosition<AnalysisSituationToMDConditions>> getMDConditions() {
	if (MDConditions == null) {
	    MDConditions = new ArrayList<>();
	}
	return MDConditions;
    }

    public void setMDConditions(List<ISliceSinglePosition<AnalysisSituationToMDConditions>> MDConditions) {
	this.MDConditions = MDConditions;
    }

    @Override
    public boolean comparePositoinal(ISignatureType c) {

	if (this == c) {
	    return true;
	}
	if (c instanceof AnalysisSituationToMDConditions) {
	    AnalysisSituationToMDConditions as = (AnalysisSituationToMDConditions) c;
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
	return new AnalysisSituationToMDConditions((AnalysisSituation) to,
		getMDConditions().stream()
			.map(x -> (ISliceSinglePosition<AnalysisSituationToMDConditions>) x.cloneMeTo(this))
			.collect(Collectors.toList()));
    }
}
