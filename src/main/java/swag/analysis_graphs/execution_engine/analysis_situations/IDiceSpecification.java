package swag.analysis_graphs.execution_engine.analysis_situations;

import swag.sparql_builder.ASElements.IASElementGenerateSPARQLVisitor;

public interface IDiceSpecification extends ISpecification, IClonableTo<IDimensionQualification> {

    public default void acceptVisitor(IASElementGenerateSPARQLVisitor visitor) {
	visitor.visit(this);
    }

    @Override
    public LevelInAnalysisSituation getPosition();

    public void setDiceLevelInAnalysisSituation(LevelInAnalysisSituation diceLevel);

    public DiceNodeInAnalysisSituation getDiceNodeInAnalysisSituation();

    public void setDiceNodeInAnalysisSituation(DiceNodeInAnalysisSituation diceNode);

    @Override
    public IDiceSpecification shallowCopy();

    /**
     * 
     * @return true if both dice position and value are not nulls and not
     *         variables.
     * 
     */
    @Override
    public default boolean isDue() {
	return null != this.getPosition() && !this.getPosition().getSignature().isVariable()
		&& null != this.getDiceNodeInAnalysisSituation()
		&& !this.getDiceNodeInAnalysisSituation().getSignature().isVariable();
    }

}
