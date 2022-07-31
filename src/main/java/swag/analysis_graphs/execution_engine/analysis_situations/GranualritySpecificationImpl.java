package swag.analysis_graphs.execution_engine.analysis_situations;

public class GranualritySpecificationImpl implements IGranularitySpecification {

    /**
    * 
    */
    private static final long serialVersionUID = -5434685196675441057L;

    private LevelInAnalysisSituation granLevel;

    private ISetOfComparison set = NoneSet.getNoneSet();

    @Override
    public ISetOfComparison getSet() {
	return set;
    }

    @Override
    public void setSet(ISetOfComparison set) {
	this.set = set;
    }

    @Override
    public LevelInAnalysisSituation getPosition() {
	return this.granLevel;
    }

    @Override
    public void setGranularityLevel(LevelInAnalysisSituation granLevel) {
	this.granLevel = granLevel;
    }

    public GranualritySpecificationImpl() {
    }

    public GranualritySpecificationImpl(LevelInAnalysisSituation granLevel) {
	super();
	this.granLevel = granLevel;
    }

    public GranualritySpecificationImpl(LevelInAnalysisSituation granLevel, ISetOfComparison set) {
	this(granLevel);
	this.set = set;
    }

    @Override
    public IGranularitySpecification shallowCopy() {

	GranualritySpecificationImpl granSpecificaiotn = new GranualritySpecificationImpl();
	granSpecificaiotn.granLevel = this.granLevel.shallowCopy();
	granSpecificaiotn.set = this.set.shallowCopy();
	return granSpecificaiotn;
    }

    @Override
    public boolean positionAsMDElementBasedEquals(ISpecification si) {
	if (si instanceof GranualritySpecificationImpl) {
	    GranualritySpecificationImpl imp = (GranualritySpecificationImpl) si;
	    if ((imp.getPosition().copyLevelFromLevelInAnalysisSituation())
		    .equals(this.getPosition().copyLevelFromLevelInAnalysisSituation())) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public void addToAnalysisSituationVariables(AnalysisSituation as) {
	if (this.getPosition() != null && this.getPosition().getSignature().isVariable())
	    as.addToVariables(this.getPosition());
    }

    @Override
    public IClonableTo<IDimensionQualification> cloneMeTo(IDimensionQualification to) {
	return new GranualritySpecificationImpl((LevelInAnalysisSituation) getPosition().cloneMeTo(to));
    }

}
