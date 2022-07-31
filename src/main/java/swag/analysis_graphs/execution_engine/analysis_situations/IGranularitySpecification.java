package swag.analysis_graphs.execution_engine.analysis_situations;

public interface IGranularitySpecification extends ISpecification, IClonableTo<IDimensionQualification> {

    @Override
    public LevelInAnalysisSituation getPosition();

    public void setGranularityLevel(LevelInAnalysisSituation granLevel);

    @Override
    public IGranularitySpecification shallowCopy();

    /**
     * 
     * @return true if both dice position and value are not nulls and not
     *         variables.
     * 
     */
    @Override
    public default boolean isDue() {
	return null != this.getPosition() && !this.getPosition().getSignature().isVariable();
    }

}
