package swag.analysis_graphs.execution_engine.analysis_situations;

import swag.sparql_builder.ASElements.configuration.Configuration;

public class MeasureToAnalysisSituaitonImpl implements IMeasureToAnalysisSituation {

    private AnalysisSituation as;

    @Override
    public AnalysisSituation getAs() {
	return as;
    }

    @Override
    public void setAs(AnalysisSituation as) {
	this.as = as;
    }

    private MeasureSpecificationInterface measureSpecification;

    @Override
    public MeasureSpecificationInterface getMeasureSpecificationInterface() {
	return measureSpecification;
    }

    @Override
    public void setMeasureSpecificationInterface(MeasureSpecificationInterface measureSpecification) {
	this.measureSpecification = measureSpecification;
    }

    public MeasureToAnalysisSituaitonImpl() {
    };

    public MeasureToAnalysisSituaitonImpl(AnalysisSituation as, MeasureSpecificationInterface measureSpecification) {
	this.as = as;
	this.measureSpecification = measureSpecification;
    }

    public MeasureToAnalysisSituaitonImpl(AnalysisSituation as, Configuration configuration,
	    MeasureSpecificationInterface measureSpecification) {
	super();
	this.as = as;
	this.measureSpecification = measureSpecification;
    }

    @Override
    public IMeasureToAnalysisSituation copy() {

	MeasureToAnalysisSituaitonImpl measToAS = new MeasureToAnalysisSituaitonImpl();

	measToAS.measureSpecification = measureSpecification.shallowCopy();
	measToAS.as = this.getAs();

	return measToAS;
    }

    @Override
    public boolean comparePositoinal(ISignatureType c) {

	if (this == c) {
	    return true;
	}

	if (c instanceof MeasureToAnalysisSituaitonImpl) {
	    MeasureToAnalysisSituaitonImpl as = (MeasureToAnalysisSituaitonImpl) c;
	    if (this.getMeasureSpecificationInterface().getPosition()
		    .equals(as.getMeasureSpecificationInterface().getPosition())) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public int generatePositionalHashCode() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public IClonableTo<ISignatureType> cloneMeTo(ISignatureType to) {
	throw new RuntimeException("Unsupported Operation");
    }

}
