package swag.analysis_graphs.execution_engine.analysis_situations;

import swag.md_elements.Level;

public class DiceSpecificaitonImpl implements IDiceSpecification {

    /**
     * 
     */
    private static final long serialVersionUID = -5771848308673426005L;

    private LevelInAnalysisSituation diceLevel;
    private DiceNodeInAnalysisSituation diceNode;

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
	return this.diceLevel;
    }

    @Override
    public void setDiceLevelInAnalysisSituation(LevelInAnalysisSituation diceLevel) {
	this.diceLevel = diceLevel;
    }

    @Override
    public DiceNodeInAnalysisSituation getDiceNodeInAnalysisSituation() {
	return this.diceNode;
    }

    @Override
    public void setDiceNodeInAnalysisSituation(DiceNodeInAnalysisSituation diceNode) {
	this.diceNode = diceNode;
    }

    public DiceSpecificaitonImpl() {
    }

    public DiceSpecificaitonImpl(ISetOfComparison set) {
	this.set = set;
    }

    public DiceSpecificaitonImpl(LevelInAnalysisSituation diceLevel, DiceNodeInAnalysisSituation diceNode) {
	this.diceLevel = diceLevel;
	this.diceNode = diceNode;
    }

    public DiceSpecificaitonImpl(LevelInAnalysisSituation diceLevel, DiceNodeInAnalysisSituation diceNode,
	    ISetOfComparison set) {
	this(diceLevel, diceNode);
	this.set = set;
    }

    @Override
    public IDiceSpecification shallowCopy() {
	DiceSpecificaitonImpl diceSpecificaiotn = new DiceSpecificaitonImpl();
	diceSpecificaiotn.diceLevel = this.diceLevel.shallowCopy();
	diceSpecificaiotn.diceNode = this.diceNode.shallowCopy();
	diceSpecificaiotn.set = this.set.shallowCopy();
	return diceSpecificaiotn;
    }

    @Override
    public boolean positionAsMDElementBasedEquals(ISpecification si) {
	if (si instanceof DiceSpecificaitonImpl) {
	    DiceSpecificaitonImpl imp = (DiceSpecificaitonImpl) si;
	    Level lvl1 = imp.getPosition().copyLevelFromLevelInAnalysisSituation();
	    Level lvl2 = this.getPosition().copyLevelFromLevelInAnalysisSituation();
	    ;
	    if (lvl1.equals(lvl2)) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public void addToAnalysisSituationVariables(AnalysisSituation as) {
	if (this.getPosition() != null && this.getPosition().getSignature().isVariable())
	    as.addToVariables(this.getPosition());

	if (this.getDiceNodeInAnalysisSituation() != null
		&& this.getDiceNodeInAnalysisSituation().getSignature().isVariable())
	    as.addToVariables(this.getDiceNodeInAnalysisSituation());
    }

    @Override
    public IClonableTo<IDimensionQualification> cloneMeTo(IDimensionQualification to) {
	return new DiceSpecificaitonImpl((LevelInAnalysisSituation) getPosition().cloneMeTo(to),
		(DiceNodeInAnalysisSituation) getDiceNodeInAnalysisSituation().cloneMeTo(to));
    }
}
